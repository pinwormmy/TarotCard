# Repository Guidelines

## Project Structure & Module Organization
The Gradle wrapper, version catalog (`gradle/libs.versions.toml`), and root `build.gradle.kts` configure the single `app` module. Kotlin source lives under `app/src/main/java/com/pinwormmy/tarotcard`, grouped by responsibility: `assets/` for data loaders, `data/` for models and repositories, `navigation/` for the Compose `NavGraph`, and `ui/` with `components`, `screens`, `state`, and `theme`. Resources reside in `app/src/main/res`, and tarot metadata sits in `app/src/main/assets/tarot_data.json`. Unit and UI tests use the mirrored `app/src/test` and `app/src/androidTest` trees.

## Build, Test, and Development Commands
- `./gradlew :app:assembleDebug` – compile the Compose app against `compileSdk 35`.
- `./gradlew :app:installDebug` – deploy the debug build to a connected device/emulator.
- `./gradlew :app:testDebugUnitTest` – run JVM tests (JUnit 4) under `app/src/test`.
- `./gradlew :app:connectedAndroidTest` – execute Espresso/Compose instrumentation suites.
- `./gradlew lint` – run Android lint and Compose static checks before review.

## Coding Style & Naming Conventions
Kotlin 2.0.21 with Compose Material 3 is the default. Use 4-space indentation, trailing commas in multi-line literals, and `UpperCamelCase` for composables/view-models (e.g., `SpreadFlowViewModel`), `lowerCamelCase` for parameters/state, and `SCREAMING_SNAKE_CASE` for compile-time constants. Prefer immutable `val`, sealed data flows, and descriptive names that match tarot terminology. Keep composables small and previewable; extensions belong near their feature folder.

## Testing Guidelines
Write JVM tests beside the code they cover in `app/src/test`, naming files `*Test.kt`. UI, navigation, and accessibility checks belong in `app/src/androidTest` using `@RunWith(AndroidJUnit4::class)` plus `composeTestRule`. Target ≥80% coverage on domain logic (models, repository, `SpreadStep`). Every new screen must include at least one screenshot or semantics assertion, and flaky tests should be annotated with `@Ignore` and an issue link.

## Commit & Pull Request Guidelines
Recent history favors concise, imperative Korean summaries (`드로우 기능 작업 완료`). Follow the same style: start with the feature area, then the change. Each PR should include: purpose paragraph, list of key changes, linked issue (if any), screenshots or screen recordings for UI, and notes about manual tarot spread verification. Rebase before opening the PR and ensure CI (`assembleDebug`, `lint`, `testDebugUnitTest`) is green.

## Security & Configuration Tips
Never commit `local.properties` or API secrets; only the SDK path belongs there. Keep sensitive deck updates in `tarot_data.json` vetted before merge, since it ships in clear text. If you introduce remote data or analytics, guard it behind build-time flags (`BuildConfig`). Validate third-party libraries through the version catalog before use.

##  Note
Human-readable documentation and comments may be written in Korean for clarity,
while all source code and technical identifiers remain in English.
