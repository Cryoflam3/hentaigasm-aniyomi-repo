# Hentaigasm for Nekoyomi

Hentaigasm 14.3 fixes the French filter and settings text. It keeps the icon and personal signing key introduced in 14.2. The repository's APK and icon paths have also been fixed.

## Add the repository

In Nekoyomi, open **More → Settings → Browse → Anime extension repos**, tap **Add**, and paste:

```text
https://raw.githubusercontent.com/Cryoflam3/hentaigasm-aniyomi-repo/main/index.min.json
```

Refresh **Browse → Anime Extensions**. Enable **Show NSFW content** if Hentaigasm does not appear.

You can also [download the APK directly](https://raw.githubusercontent.com/Cryoflam3/hentaigasm-aniyomi-repo/main/apk/aniyomi-all.hentaigasm-v14.3.apk).

## Already using Hentaigasm?

**Already using our 14.2?** Update to 14.3 normally. Both use the same signing key; no uninstall is needed.

Versions 14.2 and 14.3 use a different signing key from the original 14.1 APK. Android will not install it over that older APK as a normal update.

Back up your library and source settings before uninstalling the old extension. Then install 14.3 and restore any settings you need. If Nekoyomi asks you to trust the extension, check this certificate fingerprint:

```text
f2c157f8a9afe9e18210f63fdd804b4a7dfdf0389bb01d8415d5c272f59fa6c6
```

Future builds need to use this same key to update this installation. The private key is kept outside GitHub.

## What changed?

Version 14.3 changes the filter note, sorting options, the “All” genre option and domain settings to English. The compiled APK was checked for the English labels and the absence of the old French text. It uses versionCode 3. Search parameters, genre URLs and source identity are unchanged.

The earlier 14.2 release introduced:

- New crimson and violet H/play icon.
- Version 14.2, with Android versionCode 2.
- A new personal signing key.
- Correct APK and icon download paths, plus repository metadata with the actual signing fingerprint.

The package name and NSFW setting are unchanged. The source's browsing and video-extraction code was not changed.

## What has been tested?

The release build completed successfully. Checks passed for the APK signature, alignment, package name, version, anime-extension metadata, source class and icon.

The live JSON files, APK and icon returned HTTP 200. The downloaded files matched the checked build. See [live download results](live-validation.json) and [build validation](validation.json).

Installation, browsing and playback have not been tested on a phone. A successful build and download do not guarantee that the source website or its video servers will work.

APK SHA-256:

```text
72ddaaed7e12e28f0d569899d9f9e39748acd917942d0582a4926601e6137763
```

## Credits and permission

The extension comes from [aniyomi-addons/hentaigasm-ext](https://github.com/aniyomi-addons/hentaigasm-ext). The build setup comes from [Cursedyomi](https://github.com/Claudemirovsky/cursedyomi-extensions), which uses Apache-2.0. Their original notices are in [attribution](attribution/).

The Hentaigasm source README specifies personal use. Cryoflam3 confirmed permission from its developer to publish modified APKs on October 15, 2026. No separate permission document was independently verified. This confirmation does not grant other people a new redistribution license; the original authors retain their rights.
