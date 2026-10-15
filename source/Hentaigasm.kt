package eu.kanade.tachiyomi.animeextension.all.hentaigasm

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceScreen
import eu.kanade.tachiyomi.animesource.ConfigurableAnimeSource
import eu.kanade.tachiyomi.animesource.model.AnimeFilter
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.online.ParsedAnimeHttpSource
import eu.kanade.tachiyomi.network.GET
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class Hentaigasm :
    ParsedAnimeHttpSource(),
    ConfigurableAnimeSource {
    override val name = "Hentaigasm"
    override val lang = "all"
    override val supportsLatest = true

    private val preferences: SharedPreferences =
        Injekt.get<Application>().getSharedPreferences("source_$id", 0x0000)

    /** Read on every access so domain changes take effect immediately. */
    override val baseUrl: String
        get() = preferences.getString(PREF_DOMAIN, DEFAULT_DOMAIN).orEmpty()
            .trim()
            .trimEnd('/')
            .ifBlank { DEFAULT_DOMAIN }

    // The hgasmN.com CDNs (which serve the MP4 files) reject Aniyomi/okhttp's default User-Agent
    // with an empty response, breaking video playback. We pin a generic Chrome desktop UA to keep
    // the CDN happy. Users can still override it globally via Anikku Settings → Advanced if needed.
    override fun headersBuilder(): Headers.Builder = super.headersBuilder()
        .set("User-Agent", DESKTOP_UA)
        .add("Referer", "$baseUrl/")

    // ====================== Popular / Latest ======================

    override fun popularAnimeRequest(page: Int): Request = listingRequest(page, orderBy = "views")
    override fun popularAnimeSelector(): String = "div.thumb"
    override fun popularAnimeNextPageSelector(): String = "div.nav-previous a, a.next, a.nextpostslink"
    override fun popularAnimeFromElement(element: Element): SAnime = parseEntry(element)

    override fun latestUpdatesRequest(page: Int): Request = listingRequest(page, orderBy = "date")
    override fun latestUpdatesSelector(): String = popularAnimeSelector()
    override fun latestUpdatesNextPageSelector(): String? = popularAnimeNextPageSelector()
    override fun latestUpdatesFromElement(element: Element): SAnime = parseEntry(element)

    /** Builds `[<basePath>]/page/<N>/?orderby=<sort>` style URLs. */
    private fun listingRequest(page: Int, orderBy: String, genreSlug: String? = null): Request {
        val basePath = if (genreSlug != null) "/genre/$genreSlug" else ""
        val pageSuffix = if (page > 1) "/page/$page" else ""
        val builder = "$baseUrl$basePath$pageSuffix/".toHttpUrl().newBuilder()
        if (orderBy.isNotEmpty()) builder.addQueryParameter("orderby", orderBy)
        return GET(builder.build(), headers)
    }

    private fun parseEntry(element: Element): SAnime {
        val link = element.selectFirst("a.clip-link, a[rel=bookmark]")!!
        return SAnime.create().apply {
            setUrlWithoutDomain(link.attr("href"))
            title = link.attr("title").ifBlank { link.text() }
            thumbnail_url = element.selectFirst("img")?.attr("abs:src")
        }
    }

    // =========================== Search ===========================

    override fun searchAnimeRequest(page: Int, query: String, filters: AnimeFilterList): Request {
        val genreFilter = filters.filterIsInstance<GenreFilter>().firstOrNull()
        val sortFilter = filters.filterIsInstance<SortFilter>().firstOrNull()
        val orderBy = sortFilter?.let { SORT_VALUES[it.state] } ?: ""
        val genreSlug = genreFilter?.takeIf { it.state > 0 }?.let { GENRES[it.state].second }

        // Free-text search ignores the genre/sort filters (the site doesn't support combining them).
        if (query.isNotBlank()) {
            val pageSuffix = if (page > 1) "/page/$page" else ""
            val url = "$baseUrl$pageSuffix/".toHttpUrl().newBuilder()
                .addQueryParameter("s", query)
                .build()
            return GET(url, headers)
        }
        return listingRequest(page, orderBy = orderBy, genreSlug = genreSlug)
    }

    override fun searchAnimeSelector(): String = popularAnimeSelector()
    override fun searchAnimeNextPageSelector(): String? = popularAnimeNextPageSelector()
    override fun searchAnimeFromElement(element: Element): SAnime = parseEntry(element)

    // ============================ Filters ============================

    override fun getFilterList(): AnimeFilterList = AnimeFilterList(
        AnimeFilter.Header("Filters are ignored when you enter a search query."),
        SortFilter(),
        GenreFilter(),
    )

    private class SortFilter : AnimeFilter.Select<String>(
        "Sort by",
        SORT_LABELS,
    )

    private class GenreFilter : AnimeFilter.Select<String>(
        "Genre",
        GENRES.map { it.first }.toTypedArray(),
    )

    // ======================= Anime details =======================

    override fun animeDetailsParse(document: Document): SAnime = SAnime.create().apply {
        title = document.selectFirst("h1.entry-title, h1.title")?.text().orEmpty()
        thumbnail_url = document.selectFirst("meta[property=og:image]")?.attr("abs:content")
        description = document.selectFirst("meta[property=og:description]")?.attr("content")
        // Tags live in `<div class="post … tag-X tag-Y category-Z">` as CSS classes, and the matching
        // human-readable labels are inside the post body's <a href="/genre/X/"> links.
        val postDiv = document.selectFirst("div.post")
        val tagLabels = postDiv?.select("a[href*=/genre/]")?.map { it.text().trim() }
            ?.filter { it.isNotEmpty() }
            ?.distinct()
            .orEmpty()
        genre = tagLabels.joinToString().ifBlank { null }
        status = SAnime.COMPLETED
    }

    // ============================ Episodes ============================
    // Each WordPress post is a self-contained episode.

    override fun episodeListParse(response: Response): List<SEpisode> {
        val document = response.asJsoupSafely()
        val title = document.selectFirst("h1.entry-title, h1.title")?.text().orEmpty()
        return listOf(
            SEpisode.create().apply {
                setUrlWithoutDomain(response.request.url.toString())
                name = title.ifBlank { "Episode" }
                episode_number = 1f
            },
        )
    }

    override fun episodeListSelector(): String = throw UnsupportedOperationException()
    override fun episodeFromElement(element: Element): SEpisode = throw UnsupportedOperationException()

    // ============================= Video =============================

    override fun videoListParse(response: Response): List<Video> {
        val document = response.asJsoupSafely()
        val scripts = document.select("script").map { it.data() }
        val fileMatch = scripts.firstNotNullOfOrNull { JWPLAYER_FILE.find(it) } ?: return emptyList()
        val mp4 = fileMatch.groupValues[1].trim().replace(" ", "%20")
        val videoHeaders = headersBuilder().set("Referer", "$baseUrl/").build()
        return listOf(Video(url = mp4, quality = "Default", videoUrl = mp4, headers = videoHeaders))
    }

    override fun videoListSelector(): String = throw UnsupportedOperationException()
    override fun videoFromElement(element: Element): Video = throw UnsupportedOperationException()
    override fun videoUrlParse(document: Document): String = throw UnsupportedOperationException()

    private fun Response.asJsoupSafely(): Document =
        org.jsoup.Jsoup.parse(this.peekBody(Long.MAX_VALUE).string(), this.request.url.toString())

    // =========================== Preferences ===========================

    override fun setupPreferenceScreen(screen: PreferenceScreen) {
        EditTextPreference(screen.context).apply {
            key = PREF_DOMAIN
            title = "Domain"
            summary = "Website address without a trailing slash. Changes take effect immediately."
            dialogTitle = "Domain"
            setDefaultValue(DEFAULT_DOMAIN)
            setOnPreferenceChangeListener { _, newValue ->
                runCatching {
                    val cleaned = (newValue as String).trim().trimEnd('/')
                    require(cleaned.startsWith("http"))
                    preferences.edit().putString(PREF_DOMAIN, cleaned).apply()
                    true
                }.getOrDefault(false)
            }
        }.also(screen::addPreference)
    }

    companion object {
        private const val PREF_DOMAIN = "pref_domain"
        private const val DEFAULT_DOMAIN = "https://hentaigasm.com"

        // Generic UA — version-less enough to not drift, recognizable enough for hgasmN CDNs.
        private const val DESKTOP_UA =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

        // jwplayer("player_01").setup({ file: "https://hgasmN.com/<Title>.mp4", ... })
        private val JWPLAYER_FILE = Regex("""file:\s*"([^"]+\.mp4)"""")

        // (display label, ?orderby value)
        private val SORT_VALUES = arrayOf("date", "views", "rand")
        private val SORT_LABELS = arrayOf("Newest", "Most viewed", "Random")

        // (display label, /genre/<slug>/ slug). First entry is "All" with empty slug.
        private val GENRES: List<Pair<String, String>> = listOf(
            "All" to "",
            "3D" to "3d",
            "Anal" to "anal",
            "Blowjob" to "blowjob",
            "Bondage" to "bondage",
            "Boobjob" to "boobjob",
            "Cheating" to "cheating",
            "Creampie" to "creampie",
            "Demon" to "demon",
            "Exclusive" to "exclusive",
            "Exhibition" to "exhibition",
            "Fantasy" to "fantasy",
            "Footjob" to "footjob",
            "Futanari" to "futanari",
            "Gangbang" to "gangbang",
            "Harem" to "harem",
            "Incest" to "incest",
            "Maid" to "maid",
            "Masturbation" to "masturbation",
            "Monster" to "monster",
            "Motion Anime" to "motion-anime",
            "Nurse" to "nurse",
            "Preview" to "preview",
            "Rape" to "rape",
            "School" to "school",
            "Season" to "season",
            "Series" to "series",
            "Sex Toy" to "sex-toy",
            "Swimsuit" to "swimsuit",
            "Tentacle" to "tentacle",
            "Uncensored" to "uncensored",
            "Vanilla" to "vanilla",
            "Virgin" to "virgin",
            "X-Ray" to "x-ray",
            "Yuri" to "yuri",
        )
    }
}
