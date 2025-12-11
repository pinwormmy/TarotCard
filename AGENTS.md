# Repository Guidelines

## Project Structure & Module Organization
The Gradle wrapper, version catalog (`gradle/libs.versions.toml`), and root `build.gradle.kts` configure the single `app` module. Kotlin source lives under `app/src/main/java/com/pinwormmy/tarotcard`, grouped by responsibility: `assets/` for data loaders, `data/` for models and repositories, `navigation/` for the Compose `NavGraph`, and `ui/` with `components`, `screens`, `state`, and `theme`. Resources reside in `app/src/main/res`, and tarot metadata sits in `app/src/main/assets/tarot_data.json`. Unit and UI tests use the mirrored `app/src/test` and `app/src/androidTest` trees.

## Build, Test, and Development Commands
**(Note for Agent: Do NOT execute these commands in the sandbox environment. The sandbox lacks the necessary Android SDK and network permissions. Only generate the code and explicitly ask the user to run the commands locally.)**

- `./gradlew :app:assembleDebug` – compile the Compose app against `compileSdk 35`.
- `./gradlew :app:installDebug` – deploy the debug build to a connected device/emulator.
- `./gradlew :app:testDebugUnitTest` – run JVM tests (JUnit 4) under `app/src/test`.
- `./gradlew :app:connectedAndroidTest` – execute Espresso/Compose instrumentation suites.
- `./gradlew lint` – run Android lint and Compose static checks.

## Coding Style & Naming Conventions
Kotlin 2.0.21 with Compose Material 3 is the default. Use 4-space indentation, trailing commas in multi-line literals, and `UpperCamelCase` for composables/view-models (e.g., `SpreadFlowViewModel`), `lowerCamelCase` for parameters/state, and `SCREAMING_SNAKE_CASE` for compile-time constants. Prefer immutable `val`, sealed data flows, and descriptive names that match tarot terminology. Keep composables small and previewable; extensions belong near their feature folder.

## Testing Guidelines
**Role Division:** The Agent is responsible for **writing** test code, while the User is responsible for **executing** it locally.

- **Logic Verification (Automated):** The Agent must generate JUnit tests for all domain logic, models, and repositories (target ≥80% coverage).
- **UI Verification (Manual + Automated):**
    - The Agent writes basic semantic tests for screens.
    - **Visual Check Prompt:** For any UI/UX changes, the Agent must explicitly prompt the developer to perform a visual check via Compose Previews or a device.
- **Execution:** The User runs tests using Android Studio shortcuts (e.g., `Ctrl+Shift+R`) or Gradle tasks defined above.

## Commit & Pull Request Guidelines
Recent history favors concise, imperative Korean summaries (`드로우 기능 작업 완료`). Follow the same style: start with the feature area, then the change.

- **Local Checks (User Responsibility):** Before committing, the User must manually ensure that:
    1. IDE Code Analysis (Logic/Style) passes.
    2. Unit Tests (`testDebugUnitTest`) pass.
    3. The project builds successfully (`assembleDebug`).
- **UI Changes:** If the commit modifies UI, explicitly mention that manual visual verification is required/performed.
- **CI Validation:** Ensure the full CI pipeline (`assembleDebug`, `lint`, `testDebugUnitTest`) is green before merging.

## Security & Configuration Tips
Never commit `local.properties` or API secrets; only the SDK path belongs there. Keep sensitive deck updates in `tarot_data.json` vetted before merge, since it ships in clear text. If you introduce remote data or analytics, guard it behind build-time flags (`BuildConfig`). Validate third-party libraries through the version catalog before use.

## Note
Human-readable documentation and comments may be written in Korean for clarity,
while all source code and technical identifiers remain in English.