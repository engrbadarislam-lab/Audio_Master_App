# AudioMaster

Studio-grade audio mastering for Android — music, podcasts, beats and video.

**Package:** `com.lw.audiomaster`  ·  **Publisher:** logicworms  ·  **Monetization:** Play Billing (IAP only, no ads)

## Stack
Kotlin 2.2.10 · Jetpack Compose + Material 3 · AGP 8.9.1 · Gradle 8.11.1 · KSP · compileSdk/targetSdk 36 · minSdk 24
Room · DataStore · Navigation Compose · Play Billing 7.1.1 · manual DI (ServiceLocator/AppContainer)

## Screens (17)
Splash · Onboarding (4-page pager) · Paywall · Home · Import · Editor (A/B compare) ·
Presets (12 genre/voice) · Equalizer (6-band) · Volume & Limiter · Export · Export Success ·
Library · Project Detail · Settings · Language (10) · About · Help/FAQ

## Pro model
- Subscription: `audiomaster_pro` (base plans `monthly` / `yearly`)
- One-time: `audiomaster_lifetime`
- Pro gates: pro presets, WAV/24-bit/FLAC export, Maximum loudness, stereo width + warmth.

## Ads (AdMob + Meta Audience Network mediation)
- **App Open** on foreground resume; **App-entry Interstitial** (once per launch); **every-3-clicks Interstitial**; **Interstitial after export**; **Collapsible Banner** (Library, Settings); **Native ad** in the Home feed. All share one `AdGate` (45s) so interstitials never stack.
- **UMP consent** gathered on launch before the SDK initializes.
- **Pro removes all ads**: every surface checks `container.isProNow` / `vm.isPro` — banners, native and full-screen ads all early-return.
- `USE_TEST_ADS = BuildConfig.DEBUG`. Paste real unit IDs (app-open / banner / interstitial / native) in `data/ads/AdIds.kt`, and your real **AdMob App ID** in `app/build.gradle.kts` (`admobAppId`).
- Meta via **AdMob mediation** — add Meta as a source and set placement IDs in the **AdMob dashboard**; no code. Adapter `com.google.ads.mediation:facebook:6.20.0.0`, SDK `play-services-ads:24.4.0`.

## Localization
- **Language-first flow**: on first launch the app shows a language picker *before* onboarding; the choice is applied app-wide via `LocaleHelper` (config wrapper in `attachBaseContext` + `recreate()`).
- **Shipped languages**: English (base) + Spanish, Hindi, French, German, Russian — `res/values-es|hi|fr|de|ru/strings.xml`.
- Externalized screens: onboarding, language, paywall, home, settings. Deeper tool screens (editor/EQ/volume/export/presets/detail) still hold inline English and can be moved to `strings.xml` next — non-breaking, they simply stay English until then.
- Change language later in **Settings → Language**.


## Firebase (Analytics + Remote Config)
- **Analytics**: `data/analytics/Analytics.kt` logs a `screen_view` for every nav destination and fires the canonical Firebase `purchase` event for each acknowledged Play purchase (deduped by purchase token, price pulled from cached `ProductDetails`).
- **Force-update gate**: `data/config/ForceUpdateManager.kt` reads `min_version_code` from Remote Config and, when it exceeds `BuildConfig.VERSION_CODE`, shows a **non-dismissible** blocking screen (`ForceUpdateScreen`) over the whole app with a button to the Play listing. Set `min_version_code` in the Firebase console to the lowest build you still allow.
- **Setup**: the `com.google.gms.google-services` plugin is applied and `firebase-bom` + `firebase-analytics` + `firebase-config` are wired. A **placeholder `app/google-services.json` is included so the project builds** — replace it with the real one from your Firebase project (package `com.lw.audiomaster`).

## 16 KB page-size compliance
- `packaging { jniLibs { useLegacyPackaging = false } }` is set, and the app ships no first-party native code. Bundled native libs come only from `play-services-ads` and Media3, both on 16 KB-aligned versions.

## Wiring notes / next steps (stubs marked in code)
1. **Real file picker (SAF)** in `ImportScreen` — currently loads demo sources.
2. **Real DSP** in `data/audio/AudioEngine.kt` — waveform/loudness are deterministic stubs; swap with your engine.
3. **Purchase launch is wired** — `PaywallScreen` now launches the real Play flow for the selected tier
   (monthly / yearly / lifetime) via `vm.purchase(activity, plan)`, falling back to a local demo-unlock only
   when Play returns no product data (debug / no Play account).
4. Provide real Play Console product IDs and test on a device signed with the upload key.
5. (Optional, your standard) Firebase Remote Config force-update gate — not included; add if desired.

SUBS and INAPP are queried in separate `queryProductDetailsAsync` calls. Edge-to-edge insets handled
per-screen via `systemBarsPadding()`. 16 KB page alignment via `jniLibs { useLegacyPackaging = false }`.
