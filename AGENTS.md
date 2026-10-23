# AGENTS.md

Guidance for AI agents (and humans) working on **Tip Calculator for Wear OS** (`com.camerongineer.tipcalculatorwear`).

## Project overview

- Kotlin + Jetpack Compose Wear OS app (Wear Compose Material 1.7.1, Compose BOM 2026.09.00).
- `compileSdk`/`targetSdk` 37, `minSdk` 30, `versionCode` 31 (`versionName` 1.7.0). Keep behavior (touch, haptics) identical when modernizing.
- Single Compose UI entry: `MainActivity` → `TipCalcApp`. UI semantics (content descriptions) double as the automation surface — do not change them without updating `app/src/androidTest/.../TipCalcScreenTest.kt`.

## Environment setup (Windows)

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"   # use the Studio JBR, not a system JDK
$env:ANDROID_HOME = "C:\Users\c-em\AppData\Local\Android\Sdk"
```

`adb`/`emulator` are not on PATH — call them via `$ANDROID_HOME\platform-tools\adb.exe` etc.

## Branching & CI

- PRs target `master`. Branch protection blocks merges until the required checks `build` and `wear-emulator-tests` complete.
- `.github/workflows/master.yml` — builds on `master`.
- `.github/workflows/wear-emulator-tests.yml` — the primary test gate. Matrix of 3 emulator legs:
  - Wear OS 3 (API 30, x86), Wear OS 4 (API 33, x86_64), Wear OS 5 (API 34, x86_64)
  - Each leg runs `connectedDebugAndroidTest` (7 instrumentation tests, including a 101-click slider stress test), then a monkey crawl (the CI equivalent of Robo testing), then fails if the app package logged a `FATAL EXCEPTION`.
  - The aggregate job `wear-emulator-tests` (`needs:` all matrix legs) is the single status context required by branch protection — add new gates into the matrix, don't invent new required check names.
- `.github/workflows/firebase-tests.yml` — **parked, manual dispatch only.** Firebase Test Lab (Robo) requires billing/Blaze, which this project cannot enable (no card). Run Robo manually from the user's own Google credentials (Firebase console) or via local `gcloud`/monkey instead. Do not re-enable FTL from CI.

## Testing rules (mandatory)

1. **Run tests after every change that may be breaking** (manifest, Gradle config, Compose code, tests, workflows). Minimum local gate:
   ```powershell
   ./gradlew.bat :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin :app:assembleDebug
   ```
2. Before pushing such a change, run the full instrumentation suite locally against a Wear OS 4 (API 33) AVD:
   ```powershell
   ./gradlew.bat :app:connectedDebugAndroidTest --console=plain
   ```
   Requires a booted AVD. AVDs `WearOS33` (and `test33`) exist locally; create more with:
   ```powershell
   & "$env:ANDROID_HOME\cmdline-tools\latest\bin\avdmanager.bat" create avd -n <name> -k "system-images;android-33;android-wear-cn;x86_64"
   ```
   Boot: `$env:ANDROID_HOME\emulator\emulator.exe -avd WearOS33` (~25 s to `adb wait-for-device` + boot complete). If multiple emulators are running, Gradle tests on all of them — kill extras with `adb emu kill` or pin with `$env:ANDROID_SERIAL`.
3. HTML results land in `app/build/reports/androidTests/connected/debug/`.

## CI workflow gotchas (the easy traps)

- **`reactivecircus/android-emulator-runner@v2` executes the `script:` block line-by-line** (each line a separate `sh -c`). Multi-line `if/fi` blocks and `\`-continuations are invalid there. Keep every shell statement on one line (semicolon-chaining is fine). Multi-line is OK only inside `run:` blocks that the runner does not consume.
- **AGP uninstalls the app APK after `connectedDebugAndroidTest`.** The monkey crawl step must reinstall first: `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
- **Monkey needs a launchable activity**: `MainActivity` carries `<category android:name="android.intent.category.MONKEY" />` in the manifest (Wear images don't resolve this app through LAUNCHER the way phones do). Without it monkey aborts with "No activities found to run".
- **Never gate CI on a blanket `FATAL EXCEPTION` grep.** The Wear images themselves crash (`com.google.android.gms`, `NfcAdapter is null` on API 30) during idle. The crash gate filters for the app package only: `grep -A 2 "FATAL EXCEPTION" monkey-logcat.txt | grep "tipcalculatorwear" && exit 1 ; echo "no crashes found"` (note the polarity — a bare `grep -q ... && { exit 1; }` returns non-zero on the no-crash path and fails clean legs).
- **Unplug the emulator's battery before the suite.** The Wear system image periodically replaces the foreground activity with a full-screen `ChargingActivity` (charging-started experience) whenever the synthetic battery reports charging; under CI's slow software rendering this tears the compose test's activity mid-run ("No compose hierarchies found"). Fix (already in the workflow): `adb shell dumpsys battery unplug ; adb shell dumpsys battery set status 3` right after boot, before `connectedDebugAndroidTest`.
- CI emulators use software rendering (swiftshader). The API 34/33 legs can show rare tearDown-only flakes ("No compose hierarchies found in the app") on fast UI-churn tests; `tearDown` in `TipCalcScreenTest` is deliberately best-effort (`waitUntil` 10s inside `runCatching`) to absorb transient composition recreation. Don't "simplify" it back to a hard assertion.

## Conventions

- Commits/PR titles: `type(scope): summary` (e.g. `ci(test): ...`, `fix(wear): ...`).
- Dependency upgrades go through the BOMs first: update `androidx.compose:compose-bom` / Wear Compose versions in `app/build.gradle.kts`, then verify with the local test gate before pushing.
