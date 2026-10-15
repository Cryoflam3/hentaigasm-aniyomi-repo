# Hentaigasm Extension for Aniyomi

<p align="center">
  <img src="res/mipmap-xxxhdpi/ic_launcher.png" alt="Hentaigasm Logo" width="120">
</p>

<p align="center">
  <strong>NSFW hentai extension for Aniyomi / Anikku</strong>
</p>

---

## 📖 About

This is an **Aniyomi extension** that provides access to [Hentaigasm](https://hentaigasm.com). Browse, search and stream content directly within the Aniyomi app.

⚠️ **NSFW** — Adults only.

### Features

- ✅ **Browse & Search** — full free-text search via the site's WordPress search
- ✅ **Sort filter** — Date (newest), Views (popular), Random
- ✅ **Genre filter** — 33 genres (3D, Anal, Blowjob, Bondage, …, Yuri)
- ✅ **Configurable domain** — change the base URL from the source settings if the site ever migrates (effective immediately, no restart needed)
- ✅ **Direct streaming** — single-quality MP4 extracted from the inline JWPlayer setup, no intermediate extractor

---

## 🚀 Installation

### Prerequisites

- **[Anikku](https://github.com/komikku-app/anikku)** (or Aniyomi) installed on your Android device
- Android 6.0 (API 23) or higher

### Installation Steps

1. Download the latest `.apk` file from the [Releases](../../releases) page
2. Open **Anikku** / **Aniyomi** app
3. Go to **Browse** → **Extensions**
4. Tap the **Install** button (📦 icon) in the top right
5. Select the downloaded `.apk` file
6. Grant necessary permissions if prompted
7. The extension will appear in your **Sources** list

> Or add the aggregated repo at [goddivor/anime-extensions-repo](https://github.com/goddivor/anime-extensions-repo) to receive auto-updates.

---

## 🛠️ Technical Details

### Built With

- **Language**: Kotlin
- **Min SDK**: Android 6.0 (API 23)
- **Framework**: Aniyomi Extension API
- **Architecture**: ParsedAnimeHttpSource + ConfigurableAnimeSource

### Extraction Flow

1. HTML scraping of `hentaigasm.com` (stock WordPress install) for browse / search / details
2. Each post = one episode (each WP post is a self-contained hentai release)
3. Player URL extracted via regex on the inline `jwplayer("player_01").setup({ file: "<MP4_URL>" })` script
4. Direct MP4 served by `hgasmN.com` CDNs — no further decryption

---

## ⚙️ Settings

- **Domaine** — root URL of the site (no trailing slash). Default: `https://hentaigasm.com`. Change it here if the site ever migrates; it's read on every request, so the new domain takes effect immediately.

---

## 🐛 Troubleshooting

### "No videos available" when opening an episode
- The `hgasmN.com` CDNs occasionally rotate. Refresh the source list.
- If persistent, check the source settings → Domaine: make sure it matches the live site.

### Extension not showing in sources
- Make sure you installed the correct `.apk` file
- Restart the Aniyomi/Anikku app

---

## 📝 License

This extension is provided as-is for personal use.

## ⚠️ Disclaimer

Not affiliated with Hentaigasm or Aniyomi. Educational purposes only. You must comply with your local laws regarding adult content.
