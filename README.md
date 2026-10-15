# Cryoflam3 Hentaigasm 14.2

Rebuilt Hentaigasm with a new icon, versionCode 2 and a new owner-controlled signing key.

## Repository URL

https://raw.githubusercontent.com/Cryoflam3/hentaigasm-aniyomi-repo/main/index.min.json

Add this URL under Nekoyomi anime extension repositories. Do not use `/repo.json/index.min.json`.

## Migration required

This APK has a different signer from the original 14.1. It cannot be installed over the old APK as an ordinary update. Back up library/source settings before uninstalling the old Hentaigasm extension. Install 14.2, verify/trust the certificate when prompted, and restore settings as needed. Keep the same personal key for future updates.

Certificate SHA256: `f2c157f8a9afe9e18210f63fdd804b4a7dfdf0389bb01d8415d5c272f59fa6c6`

APK SHA256: `305a3bd7175b77c5e3ed765b82e3c37a681d53da4b724e5c2cb809637f97b7aa`

## Validation and limits

Release compilation, APK signature, zip alignment, package/version metadata, anime feature, NSFW flag, source-class presence and new icon passed checks. Device installation, source loading, playback and in-place updates remain untested. No claim that all playback bugs have been fixed.

## Attribution and permission

Original source: https://github.com/aniyomi-addons/hentaigasm-ext. Original authors retain their rights. Build scaffold: https://github.com/Claudemirovsky/cursedyomi-extensions (Apache-2.0). The owner Cryoflam3 confirmed permission from the Hentaigasm developer to publicly publish modified APKs on 2026-10-15; no separate permission document was independently verified. This statement does not grant third parties a new blanket redistribution license. Original notices are preserved under `attribution/`.

Only icon/version and build configuration changed; the scraper logic was not repaired or runtime-tested. Signing keys/passwords are private and excluded from this repository.
