# AGENTS.md

Guidance for AI agents (and humans) working on **Tip Calculator for Wear OS** (`com.camerongineer.tipcalculatorwear`).

## Project overview

- Kotlin + Jetpack Compose Wear OS app (Wear Compose Material 1.7.1, Compose BOM 2026.09.00).
- `compileSdk`/`targetSdk` 37, `minSdk` 30, `versionCode` 33 (`versionName` 1.7.2 — shipped via the CI publish pipeline). Keep behavior (touch, haptics) identical when modernizing.
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
- `.github/workflows/wear-emulator-tests.yml` — the primary test gate. Matrix of 4 emulator legs:
  - Wear OS 3 (API 30, x86), Wear OS 4 (API 33, x86_64), Wear OS 5 (API 34, x86_64), Wear OS 6 (API 36, android-wear-signed, x86_64)
  - Each leg runs `connectedDebugAndroidTest` (38 instrumentation tests, including the split-count clamp walk and a settings/picker/device-region/language set), then a monkey crawl (the CI equivalent of Robo testing), then fails if the app package logged a `FATAL EXCEPTION`.
  - Every run uploads XML/HTML results, per-test logcats, and emulator screenshots + focused-window dumps as artifacts (7-day retention).
  - The aggregate job `wear-emulator-tests` (`needs:` all matrix legs) is the single status context required by branch protection — add new gates into the matrix, don't invent new required check names.
  - Firebase Test Lab was permanently removed: Robo requires billing/Blaze (no card); the emulator matrix + monkey crawl is the replacement. Run Robo crawls manually from the user's own Google credentials when needed, never from CI.
- `.github/workflows/publish-play.yml` — automated publishing to Google Play's **internal testing** track:
  - Triggers on any master push (and `workflow_dispatch`). It reads `versionCode` from `app/build.gradle.kts` and compares against the master state before the push (`github.event.before`, falling back to `HEAD~1` when that commit is unresolvable, e.g. after a force-push): **publishes only when `versionCode` increased**; otherwise the job exits early (a merge without a version bump is a no-op release). Never compare only `HEAD` vs `HEAD~1` — a multi-commit push hides the bump that way.
  - Publishing refuses to run until the `CI` and `Wear OS Emulator Tests` runs for the *same commit SHA* conclude `success` (polls via `gh api`, ~55 min deadline). A red check blocks the release.
  - Builds `:app:bundleRelease` signed through the `ciPlay` signing config, which only activates when `CI_KEYSTORE_FILE` etc. are set — locally (and in the plain `CI` workflow) release stays unsigned exactly as before.
  - Uploads the AAB + the R8 `mapping.txt` (release is minified; without mapping, Play Console stack traces are unreadable). Track: `wear:internal` (the Play API scopes tracks per form factor — plain `internal` is the phone track and rejects watch-feature artifacts), status `completed`, `releaseName` = versionName.
  - Required GitHub secrets: `PLAY_SERVICE_ACCOUNT_JSON` (Play API service-account key JSON), `PLAY_KEYSTORE_BASE64` (base64 of the upload keystore), `PLAY_KEYSTORE_PASSWORD`, `PLAY_KEYSTORE_ALIAS`, `PLAY_KEYSTORE_KEY_PASSWORD`. The keystore must be the upload key registered with Play App Signing. **Never commit keystores** (`*.jks`/`*.keystore` are gitignored).
  - Release flow: bump `versionCode` + `versionName` in a PR → merge → publish runs automatically after CI + emulator gates pass. Promote internal → closed/production manually in Play Console.

## Play release notes (mandatory process + delivery format)

When the user asks for release notes, follow these steps in order:

1. **Establish the baseline from git, never from assumption.** Find the last release users actually received: `git tag`, then `git log -S "versionName" -- app/build.gradle.kts` to map versionName↔versionCode history, and `git show <rev>:app/build.gradle.kts` at key commits to pin the version/minSdk values at each step. Beware commits whose versionName matches an already-released name (e.g. rotary input shipped inside 1.5.0-era versionCodes) — verify before calling anything "new", and flag ambiguous ones to the user.
2. **Enumerate and classify the delta.** `git log --oneline --no-merges <baseline>..origin/master` plus the uncommitted work heading into this release. Read commit bodies for specifics (which currencies, old→new minSdk). Classify each: user-facing big change → list it; everything else → exclude.
3. **Content policy.** One line per big change a user notices. Include: new language support (a feature), currency/region support, platform requirement changes stated plainly (e.g. "support has been dropped for older versions of Wear OS" when minSdk rises), and translation corrections as ONE summary line (e.g. "Corrected mistranslations and improved wording in all supported languages") when they landed after the baseline — reuse the established line from prior notes when it exists, and never itemize per-locale wording tweaks. Exclude: theme-only changes, internal/CI work, Play-compliance fixes. Layout changes stay vague and outcome-only (e.g. "adjustments to number pad sizing") — never promise bigger buttons; sizes can also shrink on some devices.
4. **Draft English first, then translate** to every store language. Translations must read naturally (not calqued) and must match the app's own in-app glossary in `values-*/strings.xml` — e.g. Trinkgeld/Aufteilen (de), propina/división (es), pourboire/partage (fr), gorjeta/divisão (pt), टिप/बंटवारा (hi). Keep the brand name "Tip Calculator" untranslated in body text.
5. **Verify programmatically before presenting:** count each block's characters (normalize CRLF out first) — every language ≤ 500 (Play's limit); shrink by rewording, not by dropping items.
6. **Deliver as ONE copyable code block** in chat, using Play's tag format: `<en-US>…</en-US>`, `<de-DE>…</de-DE>`, `<es-US>…</es-US>`, `<fr-FR>…</fr-FR>`, `<pt-BR>…</pt-BR>`, `<hi-IN>…</hi-IN>`. Inside each tag block: plain text, one change per line — no bullets/dashes, no intro sentence, no blank lines, no counts or markers inside the tags (counts go in the surrounding chat text only).
7. **Never commit release notes to the repository** — they are pasted manually into Play Console (Release › Release notes per language). Keep the working copy outside the worktree, e.g. `%TEMP%\opencode\release_notes_v<version>_play.txt`.
8. **Apply the user's wording corrections** (tone, specificity, what counts as relevant) to the notes AND to this section's policy when they reveal a new preference.

## User collaboration rules (mandatory)

1. **Never create PRs or issues unless the user explicitly asks.** This includes `gh pr create`, `gh issue create`, and any equivalent. Pushing a branch as a prerequisite is fine; opening the PR/issue itself needs an explicit request. Show the planned actions and wait for approval.
2. **Always show a draft of what you are about to do first** for any outward-facing or irreversible action: PR/issue bodies, commit messages, Play Console submissions/review notes, version bumps/releases, and befores merging or deleting anything. Post the exact draft text and let the user approve or edit it before running the action.
3. When writing PR/issue text from PowerShell/shell, avoid inline backtick-style code spans that need escaping — mangled escapes have produced unreadable bodies before. Prefer a plain-text body, or write the body to a file and pass it via `gh pr create --body-file`.

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

Watch shapes/skins for verification (skins are folders in the system image):

```powershell
Get-ChildItem "$env:ANDROID_HOME\system-images\android-33\android-wear\x86_64\skins"   # android-30/34 similar
```

android-33 skins (folder names): WearLargeRound, WearSmallRound, WearSquare, WearRect (android-30 x86 also has AndroidWearRound/360x360…480x480 and AndroidWearSquare240x240/320x320). Observed dp: 454x454@320 → 227dp round, 384x384@320 → 192dp round, 360x360@320 → 180dp square, 240x240@240 → 160dp square; round skins set `ro.emulator.circular=1`. Build one AVD per skin with `avdmanager create avd -n <name> -k "system-images;android-33;android-wear;x86_64"`, then launch that AVD with `-skin <SkinName>` to switch shape/size without rebuilding the image.

Screenshot workflow per config: `adb -s <serial> install -r app\build\outputs\apk\debug\app-debug.apk`, launch via `adb shell "monkey -p com.camerongineer.tipcalculatorwear -c android.intent.category.LAUNCHER 1"`, then `adb exec-out screencap -p` piped binary-safe (PowerShell: `Start-Process adb -RedirectStandardOutput file.png -NoNewWindow`; `Out-File -Encoding Byte` is PS5.1-only and `| Out-File` mangles bytes). Wear boots show a "Starting…" splash — wait ~45 s after `pidof system_server` before screenshotting, else the shot is the splash.
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
