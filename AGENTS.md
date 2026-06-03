# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project Snapshot

- Android app with a single module: `:app`
- Kotlin + Jetpack Compose (Material 3)
- Navigation stack uses Navigation 3 (`androidx.navigation3`), not string-route Navigation Compose patterns
- Toolchain: AGP 9.0.1, Gradle 9.1.0, Kotlin 2.3.20, Java toolchain 17
- SDK levels: minSdk 24, target/compileSdk 36

## Source of Truth

- Dependency and plugin versions: `gradle/libs.versions.toml`
- App build config: `app/build.gradle.kts`
- Entry point: `app/src/main/java/com/example/xcam/MainActivity.kt`
- Navigation wiring: `app/src/main/java/com/example/xcam/Navigation.kt`
- Camera integration: `app/src/main/java/com/example/xcam/CameraXViewfinder.kt`

## Common Commands

Use from repository root.

### Windows (PowerShell)

- `./gradlew.bat projects`
- `./gradlew.bat build`
- `./gradlew.bat test`
- `./gradlew.bat connectedAndroidTest`
- `./gradlew.bat installDebug`

### macOS/Linux

- `./gradlew projects`
- `./gradlew build`
- `./gradlew test`
- `./gradlew connectedAndroidTest`
- `./gradlew installDebug`

## Coding Conventions Inferred From Current Code

- Keep UI code in `app/src/main/java/com/example/xcam/ui/main`
- Keep non-UI data logic in `app/src/main/java/com/example/xcam/data`
- Keep effects/utilities in `app/src/main/java/com/example/xcam/fx`
- Compose + ViewModel is the active pattern for screen state
- Edge-to-edge is already enabled; preserve this behavior in activity/screen updates

## Guardrails For Edits

- Prefer version-catalog aliases (`libs.*`) for dependencies. Do not hardcode dependency coordinates in module build files unless explicitly requested.
- When adding CameraX artifacts, add aliases in `gradle/libs.versions.toml` first, then reference them in `app/build.gradle.kts`.
- Keep Kotlin/Compose/AGP versions aligned with existing catalog and plugin setup.
- Do not introduce a DI framework (for example Hilt/Koin) unless explicitly requested.
- Follow existing package naming and file placement conventions.

## Validation Workflow

After dependency or Gradle changes, run:

1. `./gradlew.bat projects`
2. `./gradlew.bat :app:assembleDebug`

After Kotlin/Compose code changes, run:

1. `./gradlew.bat :app:compileDebugKotlin`
2. Relevant tests if modified (`test` and/or `connectedAndroidTest`)
