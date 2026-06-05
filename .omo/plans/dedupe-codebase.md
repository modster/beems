# Dedupe xcam codebase (surgical refactor)

## TL;DR

> **Quick Summary**: Extract three shared abstractions (`CameraScaffold`, `Flow.asResourceState`, `CopyShaderProgram`) and fix stale test package paths. Behavior is preserved end-to-end; no new features, no new dependencies, no new tests.
>
> **Deliverables**:
> - New `app/src/main/kotlin/com/greeffer/xcam/ui/common/ResourceUiState.kt` (sealed interface + extension function)
> - New `app/src/main/kotlin/com/greeffer/xcam/fx/x/CameraScaffold.kt` (composable + `takePicture` helper)
> - New `app/src/main/kotlin/com/greeffer/xcam/fx/x/CopyShaderProgram.kt` (abstract base for shader programs)
> - Edits to `MainScreenViewModel.kt`, `XCamViewModel.kt` (consume extension; typealiases)
> - Edits to `FilterSelectorCameraScreen.kt` (delegate to `CameraScaffold`)
> - Edits to `ClassicSepiaEffect.kt` + `VignetterShaderProgram.kt` (extend base)
> - Move + fix `MainScreenTest.kt` and `MainScreenViewModelTest.kt`
>
> **Estimated Effort**: Short
> **Parallel Execution**: YES - 3 waves
> **Critical Path**: Task 1 (extension function) -> Task 5 (ViewModels consume it) -> Task 6 (scaffold refactor) -> F1-F4

---

## Context

### Original Request
"dedupe this codebase"

### Interview Summary
**Key Decisions**:
- Scope = A (code dedup) + B (asset dedup where relevant) + C (fix test package paths). Skip D (gradle cleanup).
- Dead-code disposition = KEEP `CameraWithMedia3EffectScreen.kt`, `XViewfinder.kt`, `sepiaSamples.kt`, and the unused `XCamViewModel` methods. Refactor only wired code.
- UiState shape = `Flow<T>.asResourceState(scope)` extension function (not a generic sealed type forced on consumers).
- Camera abstraction = `CameraScaffold` composable with `bottomBar: @Composable () -> Unit` slot.

**Research Findings**:
- Two near-identical camera screens (`FilterSelectorCameraScreen` 271 lines, `CameraWithMedia3EffectScreen` 194 lines) share ~80% scaffolding: permission flow, Media3 effect, UseCaseGroup binding, takePicture helper. Source: read both files.
- Two near-identical ViewModels + UiState sealed interfaces share the `stateIn` builder pattern. Source: read both files.
- Two near-identical shader programs share GlProgram init + drawFrame + release scaffolding. Source: read both files.
- Test files are in `com/example/empty_activity/...` directories but declare `package com.greeffer.xcam.ui.main` and reference a corrupted `com.examplepackage` symbol. Source: directory listing + file read.

### Metis Review
**Identified Gaps** (resolved):
- E5 (constructor reference on typealias): Resolved by making the extension self-contained (`map { ResourceUiState.Success(it) }`) so callers don't pass a `::Success` ref.
- E3 (ImageCapture config differs): Resolved by adding `imageCaptureConfig: (ImageCapture.Builder) -> Unit = {}` lambda to `CameraScaffold`.
- E2 (effects reactivity): Resolved by `LaunchedEffect(media3Effects) { media3Effect.setEffects(media3Effects) }` in scaffold.
- A5 (corrupted import): Resolved by file move + string fix.
- A3 (typealias preserves `when` patterns): `typealias XCamUiState = ResourceUiState<List<String>>` is transparent to existing `is XCamUiState.Loading` checks in `XCamScreen.kt`.

---

## Work Objectives

### Core Objective
Surgically extract three shared abstractions from the xcam Android Compose codebase to remove ~60-80% duplication across camera screens, ViewModels, and shader programs. Behavior must be 100% preserved.

### Concrete Deliverables
- `app/src/main/kotlin/com/greeffer/xcam/ui/common/ResourceUiState.kt` (NEW)
- `app/src/main/kotlin/com/greeffer/xcam/fx/x/CameraScaffold.kt` (NEW, contains `CameraScaffold` + `takePicture` helper)
- `app/src/main/kotlin/com/greeffer/xcam/fx/x/CopyShaderProgram.kt` (NEW, contains `CopyShaderEffect` abstract + `CopyShaderProgram` abstract)
- `app/src/main/kotlin/com/greeffer/xcam/ui/main/MainScreenViewModel.kt` (MODIFY - use extension, add typealias)
- `app/src/main/kotlin/com/greeffer/xcam/fx/x/XCamViewModel.kt` (MODIFY - use extension, add typealias)
- `app/src/main/kotlin/com/greeffer/xcam/fx/x/FilterSelectorCameraScreen.kt` (MODIFY - delegate to `CameraScaffold`)
- `app/src/main/kotlin/com/greeffer/xcam/fx/x/ClassicSepiaEffect.kt` (MODIFY - extend `CopyShaderProgram`)
- `app/src/main/kotlin/com/greeffer/xcam/fx/x/VignetterShaderProgram.kt` (MODIFY - extend `CopyShaderProgram`)
- `app/src/test/java/com/greeffer/xcam/ui/main/MainScreenViewModelTest.kt` (MOVE + FIX corruption)
- `app/src/androidTest/java/com/greeffer/xcam/ui/main/MainScreenTest.kt` (MOVE + FIX corruption)
- Empty `app/src/test/java/com/example/empty_activity/` + `app/src/androidTest/java/com/example/empty_activity/` directories removed.

### Definition of Done
- [ ] `./gradlew.bat :app:compileDebugKotlin` exits 0
- [ ] `./gradlew.bat :app:assembleDebug` exits 0
- [ ] `./gradlew.bat :app:lintDebug` shows no NEW warnings vs. pre-refactor baseline
- [ ] `./gradlew.bat :app:testDebugUnitTest` runs and `MainScreenViewModelTest` passes (still asserts Loading on first emission)
- [ ] `./gradlew.bat :app:compileDebugAndroidTestKotlin` exits 0 (test compiles, even if no device for `connectedAndroidTest`)
- [ ] `git diff` shows negative or zero net line count for the refactored files (excluding new files)
- [ ] Dead files (`XViewfinder.kt`, `sepiaSamples.kt`, `CameraWithMedia3EffectScreen.kt`) compile and are byte-equivalent to pre-refactor (untouched)

### Must Have
- All three new abstractions compile and are wired into the live callers.
- Both ViewModels' `uiState: StateFlow<XxxUiState>` fields have the same externally-observable behavior (initial Loading, then Success/Failure).
- `FilterSelectorCameraScreen` retains its public `() -> Unit` signature so `XCamScreen.kt:71` continues to compile.
- Test files are reachable at the correct package path; their package declarations and imports are syntactically valid Kotlin.
- No new dependencies, no DI framework, no version bumps.

### Must NOT Have (Guardrails)
- DO NOT modify `CameraWithMedia3EffectScreen.kt` body or imports (kept as dead-but-compiling reference per user decision).
- DO NOT modify `XViewfinder.kt`, `sepiaSamples.kt` (kept as dead-but-compiling).
- DO NOT delete the unused fields/methods in `XCamViewModel` (`_surfaceRequests`, `surfaceRequests`, `produceSurfaceRequests`, `onTap`) - they stay.
- DO NOT modify `XCamScreen.kt` (out of scope for this pass; refactor in a follow-up if desired).
- DO NOT modify `MainActivity.kt`, `Navigation.kt`, `NavigationKeys.kt`, `MainScreen.kt`, `Theme.kt`, `Type.kt`, `Color.kt`, `DataRepository.kt`, `app/build.gradle.kts`.
- DO NOT introduce typealiases that BREAK the `when (state) { is XCamUiState.Loading -> ... }` pattern in `XCamScreen.kt`. Typealiases must be transparent.
- DO NOT add new tests (refactor rule; existing tests must still pass).
- DO NOT change the public `XCamViewModel(...)` constructor signature or `MainScreenViewModel(...)` constructor signature (callers in `XCamScreen.kt` and `MainScreen.kt` use them).
- DO NOT change the wire-format of `XCameraFilter` enum or `DataRepository` interface (DataRepository.kt is the only consumer of these in the live code path).

### Spec Framework Integration

> *No SDD framework detected. No `openspec/`, no `.specify/`, no `_bmad/` directory in this repo. Omit.*

---

## Verification Strategy (MANDATORY)

> **ZERO HUMAN INTERVENTION** - ALL verification is agent-executed.

### Test Decision
- **Infrastructure exists**: YES (JUnit4 unit test + `createAndroidComposeRule` androidTest, but both are templates with broken imports).
- **Automated tests**: NONE ADDED (refactor: behavior preserved, no new test logic). Existing tests are fixed in Wave 1 so they continue to compile and the unit test continues to pass.
- **Framework**: JUnit4 (existing). No new test deps.
- **If TDD**: N/A (refactor).

### QA Policy
Every task MUST include agent-executed QA scenarios (see TODO template below).
Evidence saved to `graphify-out/evidence/dedupe/task-{N}-{scenario-slug}.{ext}`.

- **Android (Kotlin/Compose)**: Use Bash (gradlew) - Run build, run unit test, parse output, check exit code.
- **UI smoke**: Use Bash (gradlew) for `assembleDebug` + `compileDebugKotlin` and `compileDebugAndroidTestKotlin` (does not need a device to compile).
- **Manual device test**: NOT in scope. The refactor preserves behavior; final verification is build + compile + unit test.

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately - foundations, MAX PARALLEL):
├── Task 1: ResourceUiState + asResourceState extension [quick]
├── Task 2: CameraScaffold + takePicture helper [quick]
├── Task 3: CopyShaderProgram abstract base [quick]
└── Task 4: Test file move + corruption fix [quick]

Wave 2 (After Wave 1 - consume the new abstractions, MAX PARALLEL):
├── Task 5: MainScreenViewModel + XCamViewModel consume asResourceState (depends: 1) [quick]
├── Task 6: FilterSelectorCameraScreen delegates to CameraScaffold (depends: 2) [quick]
└── Task 7: ClassicSepiaEffect + VignetterShaderProgram extend CopyShaderProgram (depends: 3) [quick]

Wave FINAL (After Wave 2 - 4 parallel reviews, then user okay):
├── Task F1: Plan compliance audit (oracle)
├── Task F2: Code quality review (unspecified-high)
├── Task F3: Build + unit-test verification (unspecified-high)
└── Task F4: Scope fidelity check (deep)
-> Present results -> Get explicit user okay

Critical Path: Task 1 -> Task 5 -> F1-F4
Parallel Speedup: ~50% faster than sequential
Max Concurrent: 4 (Wave 1)
```

### Dependency Matrix
- **1**: - - 5, 2 (no upstream; needed by 5)
- **2**: - - 6 (no upstream; needed by 6)
- **3**: - - 7 (no upstream; needed by 7)
- **4**: - - F1-F4 (no upstream; needed by reviewers)
- **5**: 1 - F1-F4
- **6**: 2 - F1-F4
- **7**: 3 - F1-F4

### Agent Dispatch Summary
- **Wave 1**: 4 `quick` tasks (small, well-bounded extractions or file moves)
- **Wave 2**: 3 `quick` tasks (small caller refactors, one each per new abstraction)
- **FINAL**: 4 reviews (`oracle`, `unspecified-high`, `unspecified-high`, `deep`)

---

## TODOs

> Implementation + Test = ONE Task. Never separate.
> EVERY task MUST have: Recommended Agent Profile + Parallelization info + QA Scenarios.
> **A task WITHOUT QA Scenarios is INCOMPLETE. No exceptions.**
> **FORMAT**: Task labels MUST use bare numbers: `1.`, `2.`, `3.`
> Final Verification Wave labels MUST use `F1.`, `F2.`, etc.

- [ ] 1. Create `ResourceUiState` sealed interface and `Flow.asResourceState` extension

  **What to do**:
  - Create `app/src/main/kotlin/com/greeffer/xcam/ui/common/ResourceUiState.kt` (mkdir `ui/common/` first).
  - File contents (exact):
    ```kotlin
    package com.greeffer.xcam.ui.common

    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.SharingStarted
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.catch
    import kotlinx.coroutines.flow.map
    import kotlinx.coroutines.flow.stateIn

    sealed interface ResourceUiState<out T> {
        data object Loading : ResourceUiState<Nothing>
        data class Error(val throwable: Throwable) : ResourceUiState<Nothing>
        data class Success<T>(val data: T) : ResourceUiState<T>
    }

    fun <T> Flow<T>.asResourceState(
        scope: CoroutineScope,
        started: SharingStarted = SharingStarted.WhileSubscribed(5000),
    ): StateFlow<ResourceUiState<T>> =
        this
            .map<T, ResourceUiState<T>> { ResourceUiState.Success(it) }
            .catch { emit(ResourceUiState.Error(it)) }
            .stateIn(scope, started, ResourceUiState.Loading)
    ```
  - Verify the file compiles in isolation (we won't run the full build here - that's Wave 2's job after consumers exist).

  **Must NOT do**:
  - Do NOT modify any existing file in this task.
  - Do NOT introduce a generic helper for non-`ResourceUiState` shapes.
  - Do NOT use `kotlin.Result` or other wrappers.

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Single new file, ~25 lines, no consumer coupling yet.
  - **Skills**: none
    - No domain skill applies (pure Kotlin flow extension, no Android-specific knowledge needed).

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 2, 3, 4)
  - **Blocks**: Task 5 (consumes this extension)
  - **Blocked By**: None (can start immediately)

  **References**:
  - `app/src/main/kotlin/com/greeffer/xcam/ui/main/MainScreenViewModel.kt:13-21` - existing `stateIn` chain that this extension will replace
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/XCamViewModel.kt:18-22` - same `stateIn` chain, second consumer
  - `app/src/main/kotlin/com/greeffer/xcam/ui/main/MainScreenViewModel.kt:23-33` - existing `MainScreenUiState` sealed interface that this generic version replaces
  - Kotlin Flow API: `kotlinx.coroutines.flow.stateIn` (start at `SharingStarted.WhileSubscribed(5000)` matches existing behavior exactly)

  **Acceptance Criteria**:
  - [ ] File exists at `app/src/main/kotlin/com/greeffer/xcam/ui/common/ResourceUiState.kt`
  - [ ] `package` declaration is `com.greeffer.xcam.ui.common`
  - [ ] `ResourceUiState<out T>` is `sealed interface` with `Loading` (data object), `Error` (data class with `throwable: Throwable`), `Success<T>` (data class with `data: T`)
  - [ ] `Flow<T>.asResourceState(scope, started = WhileSubscribed(5000))` returns `StateFlow<ResourceUiState<T>>`
  - [ ] `asResourceState` chain uses `.map { Success(it) }.catch { emit(Error(it)) }.stateIn(scope, started, Loading)` - same order as existing ViewModel code

  **QA Scenarios (MANDATORY)**:
  ```
  Scenario: New file exists at correct path
    Tool: Bash (filesystem)
    Preconditions: Repo at HEAD (or pre-refactor state)
    Steps:
      1. Run: `Get-ChildItem -LiteralPath "app/src/main/kotlin/com/greeffer/xcam/ui/common" -ErrorAction SilentlyContinue | Select-Object Name`
      2. Assert: `ResourceUiState.kt` is listed
    Expected Result: Single file `ResourceUiState.kt` exists in the new `ui/common/` directory.
    Failure Indicators: Directory not found, file not listed, multiple files.
    Evidence: graphify-out/evidence/dedupe/task-1-file-exists.txt

  Scenario: New file is syntactically valid Kotlin
    Tool: Bash (gradle)
    Preconditions: Task 1 file written
    Steps:
      1. Run: `./gradlew.bat :app:compileDebugKotlin --no-daemon 2>&1 | Tee-Object -FilePath "graphify-out/evidence/dedupe/task-1-compile.txt"`
      2. Assert: stdout contains `BUILD SUCCESSFUL` AND no `error:` lines
    Expected Result: Compilation passes - the new file integrates with the existing module without unresolved references.
    Failure Indicators: `Unresolved reference: ResourceUiState`, `Unresolved reference: asResourceState`, `BUILD FAILED`.
    Evidence: graphify-out/evidence/dedupe/task-1-compile.txt
  ```

  **Commit**: YES (groups with Wave 1 batch: 1, 2, 3, 4)
  - Message: `refactor(camera,ui,fx): extract shared abstractions and fix test paths`
  - Files: `app/src/main/kotlin/com/greeffer/xcam/ui/common/ResourceUiState.kt` (new)
  - Pre-commit: `./gradlew.bat :app:compileDebugKotlin` (must exit 0)

- [ ] 2. Create `CameraScaffold` composable and `takePicture` helper

  **What to do**:
  - Create `app/src/main/kotlin/com/greeffer/xcam/fx/x/CameraScaffold.kt` (file in existing `fx/x/` package - no new directory).
  - File contents (exact, with annotations):
    ```kotlin
    package com.greeffer.xcam.fx.x

    import android.Manifest
    import android.content.Context
    import android.content.pm.PackageManager
    import android.net.Uri
    import android.util.Log
    import androidx.activity.compose.rememberLauncherForActivityResult
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.annotation.OptIn
    import androidx.camera.compose.CameraXViewfinder
    import androidx.camera.core.CameraEffect.IMAGE_CAPTURE
    import androidx.camera.core.CameraEffect.PREVIEW
    import androidx.camera.core.CameraEffect.VIDEO_CAPTURE
    import androidx.camera.core.CameraSelector
    import androidx.camera.core.ImageCapture
    import androidx.camera.core.ImageCaptureException
    import androidx.camera.core.Preview
    import androidx.camera.core.SurfaceRequest
    import androidx.camera.core.UseCaseGroup
    import androidx.camera.lifecycle.ProcessCameraProvider
    import androidx.camera.media3.effect.Media3Effect
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.material3.Button
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.LocalContext
    import androidx.core.content.ContextCompat
    import androidx.lifecycle.compose.LocalLifecycleOwner
    import androidx.media3.common.Effect
    import androidx.media3.common.util.UnstableApi
    import java.io.File
    import java.text.SimpleDateFormat
    import java.util.Date
    import java.util.Locale
    import java.util.concurrent.Executor

    /**
     * Shared camera scaffold. Encapsulates the CameraX permission flow, Media3 effect pipeline,
     * use-case-group binding, surface request handling, and capture file persistence.
     *
     * Callers supply a [bottomBar] composable (capture button + optional filter selector) and
     * an optional [imageCaptureConfig] to customise the [ImageCapture] use case. The scaffold
     * hands the bottomBar a [onCapture] callback that, when invoked, persists a JPEG to the
     * app's cache directory.
     */
    @OptIn(UnstableApi::class)
    @Composable
    fun CameraScaffold(
        media3Effects: List<Effect>,
        modifier: Modifier = Modifier,
        imageCaptureConfig: ImageCapture.Builder.() -> Unit = {},
        logTag: String = "CameraScaffold",
        bottomBar: @Composable (onCapture: () -> Unit) -> Unit = {},
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
        var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
        var hasCameraPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            )
        }
        var requestedPermission by remember { mutableStateOf(false) }
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted -> hasCameraPermission = granted }

        val preview = remember { Preview.Builder().build() }
        val imageCapture = remember {
            ImageCapture.Builder().apply(imageCaptureConfig).build()
        }
        val media3Effect = remember {
            Media3Effect(
                context,
                IMAGE_CAPTURE or PREVIEW or VIDEO_CAPTURE,
                mainExecutor,
            ) { error -> Log.e(logTag, "Media3 execution error: ${error.message}", error) }
        }

        LaunchedEffect(hasCameraPermission, requestedPermission) {
            if (!hasCameraPermission && !requestedPermission) {
                requestedPermission = true
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        LaunchedEffect(hasCameraPermission) {
            if (!hasCameraPermission) return@LaunchedEffect
            preview.setSurfaceProvider { request -> surfaceRequest = request }
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageCapture)
                .addEffect(media3Effect)
                .build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    useCaseGroup,
                )
            } catch (e: Exception) {
                Log.e(logTag, "Camera initialization binding failed", e)
            }
        }

        LaunchedEffect(media3Effects) {
            media3Effect.setEffects(media3Effects)
        }

        val onCapture = remember(imageCapture) {
            { takePicture(context, imageCapture, mainExecutor, logTag = logTag) }
        }

        Box(modifier = modifier.fillMaxSize()) {
            if (!hasCameraPermission) {
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.align(Alignment.Center),
                ) { Text("Grant camera permission") }
                return@Box
            }
            surfaceRequest?.let { request ->
                CameraXViewfinder(surfaceRequest = request, modifier = Modifier.fillMaxSize())
            }
            bottomBar(onCapture)
        }
    }

    /** Saves a JPEG to [Context.getCacheDir] with a timestamped filename and logs the result. */
    internal fun takePicture(
        context: Context,
        imageCapture: ImageCapture,
        executor: Executor,
        filenamePrefix: String = "IMG",
        logTag: String = "CameraScaffold",
    ) {
        val photoFile = File(
            context.cacheDir,
            "${filenamePrefix}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg",
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d(logTag, "Photo saved: ${outputFileResults.savedUri ?: Uri.fromFile(photoFile)}")
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(logTag, "Photo capture failed", exception)
                }
            },
        )
    }
    ```
  - **This task creates the file only.** Refactor of `FilterSelectorCameraScreen` to call it is Task 6.

  **Must NOT do**:
  - Do NOT modify `FilterSelectorCameraScreen.kt` in this task.
  - Do NOT modify `CameraWithMedia3EffectScreen.kt`.
  - Do NOT introduce a new dependency.

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: One new file. Most of the work is mechanical transcription from two existing screens, not design.
  - **Skills**: none
    - Pure Kotlin/Compose file; no domain-specific knowledge required beyond what is already in the two source files.

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 3, 4)
  - **Blocks**: Task 6 (consumes this composable)
  - **Blocked By**: None

  **References**:
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/FilterSelectorCameraScreen.kt:67-145` - permission flow, Media3 effect, UseCaseGroup binding (copy with parameterization)
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/FilterSelectorCameraScreen.kt:243-271` - `takePicture` private function (becomes top-level internal in this task)
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/CameraWithMedia3EffectScreen.kt:44-134` - identical scaffolding (the source we're deduping against)
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/CameraWithMedia3EffectScreen.kt:171-194` - second `takePicture` variant (becomes one helper with filenamePrefix param)
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/FilterSelectorCameraScreen.kt:148-240` - outer Box layout that becomes `bottomBar` slot
  - AndroidX Compose API: `androidx.camera.compose.CameraXViewfinder`, `androidx.activity.compose.rememberLauncherForActivityResult`
  - CameraX: `androidx.camera.lifecycle.ProcessCameraProvider`, `androidx.camera.core.UseCaseGroup`

  **Acceptance Criteria**:
  - [ ] File exists at `app/src/main/kotlin/com/greeffer/xcam/fx/x/CameraScaffold.kt`
  - [ ] `package com.greeffer.xcam.fx.x`
  - [ ] Public `@Composable fun CameraScaffold(media3Effects, modifier, imageCaptureConfig, logTag, bottomBar)` is defined (no `onCaptureClick` parameter; the `bottomBar` slot receives an `onCapture: () -> Unit` callback)
  - [ ] Internal `fun takePicture(context, imageCapture, executor, filenamePrefix, logTag)` is defined at file scope
  - [ ] `Log.e(logTag, ...)` is used for errors (no hardcoded `"FilterSelector"` or `"CameraMedia3"` tag)
  - [ ] `@OptIn(UnstableApi::class)` annotation on `CameraScaffold`
  - [ ] `Media3Effect` receives `IMAGE_CAPTURE or PREVIEW or VIDEO_CAPTURE` (same as both originals)
  - [ ] Permission flow uses `ActivityResultContracts.RequestPermission()` (same as both originals)
  - [ ] `bottomBar: @Composable (onCapture: () -> Unit) -> Unit` - the scaffold invokes `bottomBar(onCapture)` where `onCapture` is a no-arg lambda that, when called, runs `takePicture(context, imageCapture, mainExecutor, logTag = logTag)`. This lets callers wire their capture button to a callback that closes over the scaffold's own `imageCapture` without exposing the `ImageCapture` ref directly.
  - [ ] Inside `CameraScaffold` body, after `imageCapture` is created via `remember { ImageCapture.Builder().apply(imageCaptureConfig).build() }`, the file declares:
    ```kotlin
    val onCapture = remember(imageCapture) {
        { takePicture(context, imageCapture, mainExecutor, logTag = logTag) }
    }
    ```
    and the `Box` content invokes `bottomBar(onCapture)`.

  **Final scaffold signature (single source of truth — applies to Task 2's file write and Task 6's call site)**:
  ```kotlin
  @OptIn(UnstableApi::class)
  @Composable
  fun CameraScaffold(
      media3Effects: List<Effect>,
      modifier: Modifier = Modifier,
      imageCaptureConfig: ImageCapture.Builder.() -> Unit = {},
      logTag: String = "CameraScaffold",
      bottomBar: @Composable (onCapture: () -> Unit) -> Unit = {},
  ) { ... }
  ```
  `onCaptureClick` is NOT a separate parameter. The `onCapture: () -> Unit` callback IS the contract. Callers receive it inside their `bottomBar` lambda and invoke it from their capture button's `onClick`. `takePicture` stays as a top-level internal helper (no API change for the helper).

  **QA Scenarios (MANDATORY)**:
  ```
  Scenario: New file exists at correct path
    Tool: Bash (filesystem)
    Steps:
      1. Run: `Get-ChildItem -LiteralPath "app/src/main/kotlin/com/greeffer/xcam/fx/x/CameraScaffold.kt"`
      2. Assert: file resolves
    Expected Result: File present.
    Failure Indicators: File not found.
    Evidence: graphify-out/evidence/dedupe/task-2-file-exists.txt

  Scenario: Module still compiles with new file (no consumers yet)
    Tool: Bash (gradle)
    Preconditions: Tasks 1, 3, 4 also done
    Steps:
      1. Run: `./gradlew.bat :app:compileDebugKotlin --no-daemon 2>&1 | Tee-Object -FilePath "graphify-out/evidence/dedupe/task-2-compile.txt"`
      2. Assert: BUILD SUCCESSFUL, no `error:` lines
    Expected Result: New file compiles standalone.
    Failure Indicators: Unresolved references, syntax errors.
    Evidence: graphify-out/evidence/dedupe/task-2-compile.txt
  ```

  **Commit**: YES (groups with Wave 1 batch: 1, 2, 3, 4)
  - Message: `refactor(camera,ui,fx): extract shared abstractions and fix test paths`
  - Files: `app/src/main/kotlin/com/greeffer/xcam/fx/x/CameraScaffold.kt` (new)
  - Pre-commit: `./gradlew.bat :app:compileDebugKotlin` (must exit 0)

- [ ] 3. Create `CopyShaderProgram` abstract base class

  **What to do**:
  - Create `app/src/main/kotlin/com/greeffer/xcam/fx/x/CopyShaderProgram.kt` (file in existing `fx/x/` package).
  - File contents (exact):
    ```kotlin
    package com.greeffer.xcam.fx.x

    import android.content.Context
    import android.opengl.GLES20
    import androidx.annotation.OptIn
    import androidx.media3.common.VideoFrameProcessingException
    import androidx.media3.common.util.GlProgram
    import androidx.media3.common.util.Size
    import androidx.media3.common.util.UnstableApi
    import androidx.media3.effect.BaseGlShaderProgram
    import androidx.media3.effect.GlEffect
    import androidx.media3.effect.GlShaderProgram

    /**
     * Base for shader-based [GlEffect]s that pair the standard copy vertex shader with a
     * caller-supplied fragment shader and want a fixed draw loop. Subclasses only need to
     * implement [configureExtraUniforms] (e.g. resolution, color matrix).
     */
    @OptIn(UnstableApi::class)
    abstract class CopyShaderEffect(
        private val fragmentShaderAssetPath: String,
    ) : GlEffect {
        override fun toGlShaderProgram(context: Context, useHdr: Boolean): GlShaderProgram =
            CopyShaderProgram(context, fragmentShaderAssetPath, useHdr)
    }

    @OptIn(UnstableApi::class)
    abstract class CopyShaderProgram(
        context: Context,
        fragmentShaderAssetPath: String,
        useHdr: Boolean,
    ) : BaseGlShaderProgram(useHdr, 1) {

        private val glProgram: GlProgram

        init {
            try {
                glProgram = GlProgram(
                    context,
                    VERTEX_SHADER_ASSET_PATH,
                    fragmentShaderAssetPath,
                )
            } catch (e: Exception) {
                throw VideoFrameProcessingException(
                    "Shader compilation failed for ${fragmentShaderAssetPath}",
                    e,
                )
            }
        }

        /** Configure any per-frame uniforms (e.g. resolution, color matrix) before the draw. */
        protected abstract fun configureExtraUniforms(
            glProgram: GlProgram,
            inputWidth: Int,
            inputHeight: Int,
        )

        /** Optional hook for subclasses that need to issue raw GL calls (e.g. glDrawArrays). */
        protected open fun drawQuad() {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        }

        override fun configure(inputWidth: Int, inputHeight: Int): Size {
            configureExtraUniforms(glProgram, inputWidth, inputHeight)
            return Size(inputWidth, inputHeight)
        }

        override fun drawFrame(inputTexId: Int, presentationTimeUs: Long) {
            glProgram.use()
            glProgram.setSamplerTexIdUniform("uTexSampler", inputTexId, 0)
            glProgram.bindAttributesAndUniforms()
            drawQuad()
        }

        override fun release() {
            super.release()
            glProgram.delete()
        }

        private companion object {
            const val VERTEX_SHADER_ASSET_PATH = "shaders/vertex_shader_copy.glsl"
        }
    }
    ```
  - **This task creates the file only.** Refactor of `ClassicSepiaEffect.kt` + `VignetterShaderProgram.kt` to extend it is Task 7.

  **Must NOT do**:
  - Do NOT modify the existing shader program files in this task.
  - Do NOT introduce a new dependency.
  - Do NOT change the GLSL asset paths or shader logic.

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Single new file, templated extraction.
  - **Skills**: none

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 2, 4)
  - **Blocks**: Task 7 (consumes this base)
  - **Blocked By**: None

  **References**:
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/ClassicSepiaEffect.kt:13-66` - the GlEffect wrapper + BaseGlShaderProgram subclass with the `init { GlProgram(...) }`, `drawFrame` with `setSamplerTexIdUniform("uTexSampler", ...)` + `bindAttributesAndUniforms`, `release` that calls `glProgram.delete()`. Template-method extraction target.
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/VignetterShaderProgram.kt:14-59` - same scaffolding, with extra `uResolution` uniform and a `glDrawArrays` call in `drawFrame`.
  - Media3 API: `androidx.media3.effect.BaseGlShaderProgram`, `androidx.media3.common.util.GlProgram`
  - The shared vertex shader path `shaders/vertex_shader_copy.glsl` is hard-coded in both originals - becomes the `VERTEX_SHADER_ASSET_PATH` constant in the base.

  **Acceptance Criteria**:
  - [ ] File exists at `app/src/main/kotlin/com/greeffer/xcam/fx/x/CopyShaderProgram.kt`
  - [ ] `package com.greeffer.xcam.fx.x`
  - [ ] Public `abstract class CopyShaderEffect(fragmentShaderAssetPath: String) : GlEffect`
  - [ ] Public `abstract class CopyShaderProgram(context, fragmentShaderAssetPath, useHdr) : BaseGlShaderProgram(useHdr, 1)`
  - [ ] Protected abstract `fun configureExtraUniforms(glProgram, inputWidth, inputHeight)`
  - [ ] Protected open `fun drawQuad()` defaulting to `glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)`
  - [ ] `init` block calls `GlProgram(context, "shaders/vertex_shader_copy.glsl", fragmentShaderAssetPath)` and wraps failures in `VideoFrameProcessingException`
  - [ ] `drawFrame` calls `use()`, `setSamplerTexIdUniform("uTexSampler", inputTexId, 0)`, `bindAttributesAndUniforms()`, then `drawQuad()`
  - [ ] `release` calls `super.release()` then `glProgram.delete()`

  **QA Scenarios (MANDATORY)**:
  ```
  Scenario: New file exists at correct path
    Tool: Bash (filesystem)
    Steps:
      1. Run: `Get-ChildItem -LiteralPath "app/src/main/kotlin/com/greeffer/xcam/fx/x/CopyShaderProgram.kt"`
      2. Assert: file resolves
    Expected Result: File present.
    Failure Indicators: File not found.
    Evidence: graphify-out/evidence/dedupe/task-3-file-exists.txt

  Scenario: Module compiles with new file
    Tool: Bash (gradle)
    Preconditions: Tasks 1, 2, 4 also done
    Steps:
      1. Run: `./gradlew.bat :app:compileDebugKotlin --no-daemon 2>&1 | Tee-Object -FilePath "graphify-out/evidence/dedupe/task-3-compile.txt"`
      2. Assert: BUILD SUCCESSFUL
    Expected Result: New file compiles alongside the existing shader program files (which still extend `BaseGlShaderProgram` directly, not the new base).
    Failure Indicators: Unresolved references to `GlProgram`, `BaseGlShaderProgram`, `VideoFrameProcessingException`.
    Evidence: graphify-out/evidence/dedupe/task-3-compile.txt
  ```

  **Commit**: YES (groups with Wave 1 batch: 1, 2, 3, 4)
  - Message: `refactor(camera,ui,fx): extract shared abstractions and fix test paths`
  - Files: `app/src/main/kotlin/com/greeffer/xcam/fx/x/CopyShaderProgram.kt` (new)
  - Pre-commit: `./gradlew.bat :app:compileDebugKotlin` (must exit 0)

- [ ] 4. Move test files to correct package paths and fix corruption

  **What to do**:
  - Create directories: `app/src/test/java/com/greeffer/xcam/ui/main/` and `app/src/androidTest/java/com/greeffer/xcam/ui/main/`.
  - Move file `app/src/test/java/com/example/empty_activity/ui/main/MainScreenViewModelTest.kt` to `app/src/test/java/com/greeffer/xcam/ui/main/MainScreenViewModelTest.kt` (use `Move-Item`).
  - Move file `app/src/androidTest/java/com/example/empty_activity/ui/main/MainScreenTest.kt` to `app/src/androidTest/java/com/greeffer/xcam/ui/main/MainScreenTest.kt` (use `Move-Item`).
  - Edit `MainScreenViewModelTest.kt`:
    - Line 3 currently: `import com.examplepackage com.greeffer.xcam.data.DataRepository`
    - Change to: `import com.greeffer.xcam.data.DataRepository`
  - Edit `MainScreenTest.kt`:
    - Line 10 currently: `/** UI tests for [com.examplepackage com.greeffer.xcam.ui.main.MainScreen]. */`
    - Change to: `/** UI tests for [com.greeffer.xcam.ui.main.MainScreen]. */`
  - Remove empty directories: `app/src/test/java/com/example/empty_activity/` and `app/src/androidTest/java/com/example/empty_activity/` (use `Remove-Item -Recurse` if empty; if not empty, leave them and flag in evidence).
  - Verify the test sources still compile via `./gradlew.bat :app:compileDebugUnitTestKotlin :app:compileDebugAndroidTestKotlin`.

  **Must NOT do**:
  - Do NOT change test logic or assertions. Only fix directory + corruption.
  - Do NOT add new tests.
  - Do NOT delete the `FAKE_DATA` constant or `FakeMyModelRepository` class.
  - Do NOT rename the test classes.

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Pure file moves + 2 string fixes + verification.
  - **Skills**: none

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 2, 3)
  - **Blocks**: F1-F4 reviewers (need tests compiling to verify nothing broke)
  - **Blocked By**: None

  **References**:
  - `app/src/test/java/com/example/empty_activity/ui/main/MainScreenViewModelTest.kt:1-29` - source (full read done)
  - `app/src/androidTest/java/com/example/empty_activity/ui/main/MainScreenTest.kt:1-25` - source (full read done)
  - Existing `package com.greeffer.xcam.ui.main` declaration in both files (line 1) - matches target directory
  - `app/build.gradle.kts:79-91` - test dependencies (junit, kotlinx-coroutines-test, espresso) - no changes needed
  - `app/build.gradle.kts:14` - `applicationId = "com.greeffer.xcam"` - confirms canonical package name

  **Acceptance Criteria**:
  - [ ] `app/src/test/java/com/greeffer/xcam/ui/main/MainScreenViewModelTest.kt` exists
  - [ ] `app/src/androidTest/java/com/greeffer/xcam/ui/main/MainScreenTest.kt` exists
  - [ ] `app/src/test/java/com/example/empty_activity/...` no longer contains the test file (may be empty dir or removed)
  - [ ] `app/src/androidTest/java/com/example/empty_activity/...` no longer contains the test file (may be empty dir or removed)
  - [ ] `MainScreenViewModelTest.kt` line 3 reads `import com.greeffer.xcam.data.DataRepository` (no `com.examplepackage` prefix)
  - [ ] `MainScreenTest.kt` line 10 comment contains `[com.greeffer.xcam.ui.main.MainScreen]` (no `com.examplepackage` prefix)
  - [ ] `MainScreenViewModelTest.kt` line 1 still reads `package com.greeffer.xcam.ui.main` (unchanged)
  - [ ] `MainScreenTest.kt` line 1 still reads `package com.greeffer.xcam.ui.main` (unchanged)
  - [ ] `./gradlew.bat :app:compileDebugUnitTestKotlin` exits 0
  - [ ] `./gradlew.bat :app:compileDebugAndroidTestKotlin` exits 0
  - [ ] `./gradlew.bat :app:testDebugUnitTest` runs `MainScreenViewModelTest.uiState_initiallyLoading` and PASSES
  - [ ] No file under `app/src` contains the literal substring `com.examplepackage` (run a grep)

  **QA Scenarios (MANDATORY)**:
  ```
  Scenario: Test files at correct paths
    Tool: Bash (filesystem)
    Steps:
      1. Run: `Test-Path -LiteralPath "app/src/test/java/com/greeffer/xcam/ui/main/MainScreenViewModelTest.kt"` and `Test-Path -LiteralPath "app/src/androidTest/java/com/greeffer/xcam/ui/main/MainScreenTest.kt"`
      2. Assert: both return `True`
    Expected Result: Both test files at correct package paths.
    Failure Indicators: One or both resolve to `False`.
    Evidence: graphify-out/evidence/dedupe/task-4-files-moved.txt

  Scenario: No `com.examplepackage` corruption remains
    Tool: Grep
    Steps:
      1. Run: `grep -r "com.examplepackage" app/src/ || echo "NOT_FOUND"`
      2. Assert: output is `NOT_FOUND`
    Expected Result: Zero matches anywhere under `app/src/`.
    Failure Indicators: Any match indicates an unfixed corruption.
    Evidence: graphify-out/evidence/dedupe/task-4-no-corruption.txt

  Scenario: Test sources compile
    Tool: Bash (gradle)
    Steps:
      1. Run: `./gradlew.bat :app:compileDebugUnitTestKotlin :app:compileDebugAndroidTestKotlin --no-daemon 2>&1 | Tee-Object -FilePath "graphify-out/evidence/dedupe/task-4-compile.txt"`
      2. Assert: BUILD SUCCESSFUL, no `error:` lines
    Expected Result: Both test source sets compile.
    Failure Indicators: `Unresolved reference: com.examplepackage`, `Unresolved reference: empty_activity`, `BUILD FAILED`.
    Evidence: graphify-out/evidence/dedupe/task-4-compile.txt

  Scenario: Unit test passes
    Tool: Bash (gradle)
    Steps:
      1. Run: `./gradlew.bat :app:testDebugUnitTest --no-daemon 2>&1 | Tee-Object -FilePath "graphify-out/evidence/dedupe/task-4-unittest.txt"`
      2. Assert: BUILD SUCCESSFUL AND output contains `MainScreenViewModelTest` with at least 1 successful test
    Expected Result: Unit test still runs and passes after the file move + corruption fix.
    Failure Indicators: BUILD FAILED, no tests found, test failure.
    Evidence: graphify-out/evidence/dedupe/task-4-unittest.txt
  ```

  **Commit**: YES (groups with Wave 1 batch: 1, 2, 3, 4)
  - Message: `refactor(camera,ui,fx): extract shared abstractions and fix test paths`
  - Files: test file moves (tracked as renames by git) + corruption fixes
  - Pre-commit: `./gradlew.bat :app:compileDebugKotlin :app:compileDebugUnitTestKotlin :app:compileDebugAndroidTestKotlin` (all must exit 0)

- [ ] 5. Refactor `MainScreenViewModel` and `XCamViewModel` to use `asResourceState` + typealiases

  **What to do**:
  - Edit `app/src/main/kotlin/com/greeffer/xcam/ui/main/MainScreenViewModel.kt`:
    - Add import: `import com.greeffer.xcam.ui.common.ResourceUiState`
    - Add import: `import com.greeffer.xcam.ui.common.asResourceState`
    - Add at top of file (after imports, before class): `typealias MainScreenUiState = ResourceUiState<List<String>>`
    - Replace the `sealed interface MainScreenUiState { ... }` block (lines 23-33) with: nothing (the typealias replaces it; the sealed members `Loading`/`Success`/`Error` are inherited from `ResourceUiState`).
    - Replace the body of `uiState` (lines 16-20):
      ```kotlin
      val uiState: StateFlow<MainScreenUiState> = dataRepository.data.asResourceState(viewModelScope)
      ```
    - Remove now-unused imports: `SharingStarted`, `map`, `catch`, `stateIn`, `Success` (the explicit `Success` import in line 6).
    - **Keep** the `dataRepository: DataRepository` constructor parameter (consumers in `MainScreen.kt` and the test pass `DataRepository` / `FakeMyModelRepository`).
  - Edit `app/src/main/kotlin/com/greeffer/xcam/fx/x/XCamViewModel.kt`:
    - Add imports: `com.greeffer.xcam.ui.common.ResourceUiState`, `com.greeffer.xcam.ui.common.asResourceState`
    - Add at top (after imports, before class): `typealias XCamUiState = ResourceUiState<List<String>>`
    - Replace `sealed interface XCamUiState { ... }` block (lines 47-57) with: nothing.
    - Replace the `uiState` body (lines 18-22):
      ```kotlin
      val uiState: StateFlow<XCamUiState> = dataRepository.data.asResourceState(viewModelScope)
      ```
    - Remove now-unused imports: `SharingStarted`, `map`, `catch`, `stateIn`.
    - **Keep** `dataRepository: DefaultDataRepository` constructor parameter (consumer in `XCamScreen.kt` passes `DefaultDataRepository()`).
    - **DO NOT TOUCH** `_surfaceRequests`, `surfaceRequests`, `produceSurfaceRequests`, `onTap`, or the `// fun focusOnPoint(...)` comment block. These are kept-dead per guardrail.

  **Must NOT do**:
  - Do NOT change constructor signatures of either ViewModel.
  - Do NOT touch any other file in this task.
  - Do NOT delete the unused/dead fields in `XCamViewModel` (user chose to keep dead code).
  - Do NOT change `DataRepository` or `DefaultDataRepository` or `XCameraFilter`.
  - Do NOT change the `produceSurfaceRequests` private method or the `onTap` empty function.

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Two small file edits, mostly deletion of boilerplate + one-line state construction.
  - **Skills**: none

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 6, 7)
  - **Blocks**: F1-F4
  - **Blocked By**: Task 1 (the extension function)

  **References**:
  - `app/src/main/kotlin/com/greeffer/xcam/ui/main/MainScreenViewModel.kt:1-33` - whole file (read done)
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/XCamViewModel.kt:1-57` - whole file (read done)
  - `app/src/main/kotlin/com/greeffer/xcam/ui/common/ResourceUiState.kt` - new file from Task 1 (the extension function)
  - `app/src/main/kotlin/com/greeffer/xcam/ui/main/MainScreen.kt:20` - `MainScreenViewModel(DefaultDataRepository())` call site; constructor signature must remain
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/XCamScreen.kt:27` - `XCamViewModel(DefaultDataRepository())` call site; constructor signature must remain
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/XCamScreen.kt:30-41` - `when (state) { is XCamUiState.Loading -> ... is XCamUiState.Success -> ... is XCamUiState.Error -> ... }` - the typealias MUST preserve these `is XCamUiState.<Member>` patterns (typealiases are transparent in Kotlin, so this works).
  - `app/src/main/kotlin/com/greeffer/xcam/ui/main/MainScreen.kt:23-37` - same `when (state) { is MainScreenUiState.<Member> -> ... }` pattern in `MainScreen.kt`. Typealias preserves it.

  **Acceptance Criteria**:
  - [ ] `MainScreenViewModel.kt` does not contain the literal `sealed interface MainScreenUiState` (replaced by typealias)
  - [ ] `MainScreenViewModel.kt` does not contain `SharingStarted`, `kotlinx.coroutines.flow.map`, `kotlinx.coroutines.flow.catch`, `kotlinx.coroutines.flow.stateIn` imports
  - [ ] `MainScreenViewModel.kt` line for `uiState` is exactly: `val uiState: StateFlow<MainScreenUiState> = dataRepository.data.asResourceState(viewModelScope)`
  - [ ] `MainScreenViewModel.kt` contains `typealias MainScreenUiState = ResourceUiState<List<String>>`
  - [ ] `XCamViewModel.kt` does not contain the literal `sealed interface XCamUiState` (replaced by typealias)
  - [ ] `XCamViewModel.kt` does not contain `SharingStarted`, `kotlinx.coroutines.flow.map`, `kotlinx.coroutines.flow.catch`, `kotlinx.coroutines.flow.stateIn` imports
  - [ ] `XCamViewModel.kt` line for `uiState` is exactly: `val uiState: StateFlow<XCamUiState> = dataRepository.data.asResourceState(viewModelScope)`
  - [ ] `XCamViewModel.kt` contains `typealias XCamUiState = ResourceUiState<List<String>>`
  - [ ] `XCamViewModel.kt` still contains `_surfaceRequests`, `surfaceRequests`, `produceSurfaceRequests`, `onTap` (untouched)
  - [ ] Neither ViewModel's constructor signature changed (still takes `DataRepository` / `DefaultDataRepository`)

  **QA Scenarios (MANDATORY)**:
  ```
  Scenario: Module compiles after ViewModel refactor
    Tool: Bash (gradle)
    Preconditions: Tasks 1, 2, 3, 4, 6, 7 all done
    Steps:
      1. Run: `./gradlew.bat :app:compileDebugKotlin --no-daemon 2>&1 | Tee-Object -FilePath "graphify-out/evidence/dedupe/task-5-compile.txt"`
      2. Assert: BUILD SUCCESSFUL, no `error:` lines
    Expected Result: The typealiases are transparent - `is XCamUiState.Loading` checks in `XCamScreen.kt:31` still resolve.
    Failure Indicators: `Unresolved reference: Loading` (would mean the typealias lost the sealed members), `Unresolved reference: Success`/`Error`, or `Type mismatch` on the `uiState` field type.
    Evidence: graphify-out/evidence/dedupe/task-5-compile.txt

  Scenario: Existing unit test still passes
    Tool: Bash (gradle)
    Preconditions: Task 4 done
    Steps:
      1. Run: `./gradlew.bat :app:testDebugUnitTest --no-daemon 2>&1 | Tee-Object -FilePath "graphify-out/evidence/dedupe/task-5-unittest.txt"`
      2. Assert: BUILD SUCCESSFUL AND `MainScreenViewModelTest` reports at least 1 passing test
    Expected Result: `MainScreenViewModelTest.uiState_initiallyLoading` still asserts the initial `Loading` state. The refactored `asResourceState` returns `ResourceUiState.Loading` initially, which the typealias exposes as `MainScreenUiState.Loading` - so `assertEquals(viewModel.uiState.first(), MainScreenUiState.Loading)` still passes.
    Failure Indicators: BUILD FAILED, no test result, test failure with `expected: Loading actual: Success` (would mean the extension's initial value was lost).
    Evidence: graphify-out/evidence/dedupe/task-5-unittest.txt
  ```

  **Commit**: YES (groups with Wave 2 batch: 5, 6, 7)
  - Message: `refactor(camera,ui,fx): wire callers to new shared abstractions`
  - Files: `MainScreenViewModel.kt`, `XCamViewModel.kt`
  - Pre-commit: `./gradlew.bat :app:compileDebugKotlin :app:testDebugUnitTest` (must exit 0)

- [ ] 6. Refactor `FilterSelectorCameraScreen` to delegate to `CameraScaffold`

  **What to do**:
  - Edit `app/src/main/kotlin/com/greeffer/xcam/fx/x/FilterSelectorCameraScreen.kt`:
    - Keep the function signature: `@OptIn(UnstableApi::class) @Composable fun FilterSelectorCameraScreen()` - no parameter changes (XCamScreen.kt:71 calls it as `FilterSelectorCameraScreen()`).
    - Keep imports that the bottomBar / filter-selector UI still uses (e.g. `Column`, `Row`, `Card`, `LazyRow`, `Color`, etc.).
    - Remove imports that move to `CameraScaffold.kt`: `CameraXViewfinder`, `CameraEffect.IMAGE_CAPTURE/PREVIEW/VIDEO_CAPTURE`, `CameraSelector`, `ImageCapture`, `ImageCaptureException`, `Preview`, `SurfaceRequest`, `UseCaseGroup`, `ProcessCameraProvider`, `Media3Effect`, `LocalLifecycleOwner`, `Box`, `fillMaxSize`, `Button`, `Text`, `mutableStateOf`, `remember`, `getValue`, `setValue`, `rememberLauncherForActivityResult`, `ActivityResultContracts`, `Manifest`, `PackageManager`, `ContextCompat`, `Composable`, `LaunchedEffect`, `OptIn`, `UnstableApi`, `Alignment`, `Modifier`, `LocalContext`, `R`, `Log`, `SimpleDateFormat`, `Date`, `Locale`, `File`.
    - **Add** imports: `CameraScaffold`, `takePicture` (the new internal helper), and the import for `Effect` from `androidx.media3.common.Effect`.
    - The full new body of `FilterSelectorCameraScreen` (per the Task 2 final scaffold signature):

  The `FilterSelectorCameraScreen` body becomes:
  ```kotlin
  @OptIn(UnstableApi::class)
  @Composable
  fun FilterSelectorCameraScreen() {
      var selectedFilter: XCameraFilter by remember { mutableStateOf(XCameraFilter.NONE) }
      val context = LocalContext.current
      val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
      val selectedFilterEffects = selectedFilter.getMedia3Effects()

      CameraScaffold(
          media3Effects = selectedFilterEffects,
          modifier = Modifier.fillMaxSize(),
          logTag = "FilterSelector",
      ) { onCapture ->
          // Bottom control bar
          Column(
              modifier = Modifier
                  .align(Alignment.BottomCenter)
                  .fillMaxWidth()
                  .background(Color.Black.copy(alpha = 0.4f))
                  .padding(vertical = 24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
          ) {
              LazyRow(
                  contentPadding = PaddingValues(horizontal = 16.dp),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
                  modifier = Modifier.fillMaxWidth(),
              ) {
                  items(XCameraFilter.values()) { filter ->
                      val isSelected = filter == selectedFilter
                      Card(
                          colors = CardDefaults.cardColors(
                              containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
                          ),
                          modifier = Modifier
                              .width(85.dp)
                              .height(55.dp)
                              .clickable(
                                  indication = null,
                                  interactionSource = remember { MutableInteractionSource() }
                              ) { selectedFilter = filter }
                      ) {
                          Box(
                              contentAlignment = Alignment.Center,
                              modifier = Modifier.fillMaxSize()
                          ) {
                              Text(
                                  text = filter.displayName,
                                  color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.White,
                                  style = MaterialTheme.typography.bodyMedium,
                                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                              )
                          }
                      }
                  }
              }
              Spacer(modifier = Modifier.height(20.dp))
              // Circular capture button - clicking invokes the scaffold-provided onCapture callback
              Box(
                  modifier = Modifier
                      .size(72.dp)
                      .clip(CircleShape)
                      .background(Color.White)
                      .clickable(
                          indication = null,
                          interactionSource = remember { MutableInteractionSource() }
                      ) { onCapture() },
                  contentAlignment = Alignment.Center,
              ) {
                  Box(
                      modifier = Modifier
                          .size(62.dp)
                          .clip(CircleShape)
                          .background(Color.Black.copy(alpha = 0.1f))
                  )
              }
          }
      }
  }
  ```
  (Note: `selectedFilter` and `selectedFilterEffects` extraction preserves the original behavior where `LaunchedEffect(selectedFilter) { media3Effect.setEffects(selectedFilter.getMedia3Effects()) }` ran on each filter change. The new scaffold's `LaunchedEffect(media3Effects) { media3Effect.setEffects(media3Effects) }` triggers on every filter change because `selectedFilterEffects` is recomputed each recomposition. Behavior equivalent.)

  **Must NOT do**:
  - Do NOT change the public signature `fun FilterSelectorCameraScreen()` (XCamScreen.kt:71 calls it as `FilterSelectorCameraScreen()`).
  - Do NOT change any visual aspect of the bottom bar (filter selector or capture button shape).
  - Do NOT touch `XCamScreen.kt`.
  - Do NOT touch `CameraWithMedia3EffectScreen.kt`.

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: One file refactor, ~150 lines of UI transcription. Self-contained.
  - **Skills**: none

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 5, 7)
  - **Blocks**: F1-F4
  - **Blocked By**: Task 2 (the scaffold). Also requires the Task-2 amendment above to be applied to `CameraScaffold.kt`.

  **References**:
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/FilterSelectorCameraScreen.kt:67-241` - the whole current function (read done)
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/FilterSelectorCameraScreen.kt:67-145` - the scaffolding to extract into `CameraScaffold`
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/FilterSelectorCameraScreen.kt:147-240` - the bottom bar (filter selector + capture button) that becomes the `bottomBar` slot
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/FilterSelectorCameraScreen.kt:243-271` - `takePicture` private function (now a top-level internal in CameraScaffold.kt)
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/XCamScreen.kt:71` - call site: `FilterSelectorCameraScreen()` - signature must be preserved
  - `app/src/main/kotlin/com/greeffer/xcam/data/DataRepository.kt:15-32` - `XCameraFilter` enum + `getMedia3Effects()` extension - unchanged

  **Acceptance Criteria**:
  - [ ] `FilterSelectorCameraScreen.kt` is significantly shorter than 271 lines (target: <80 lines; the bottom bar alone is ~50 lines + the new scaffold call)
  - [ ] The function signature is still `@OptIn(UnstableApi::class) @Composable fun FilterSelectorCameraScreen()` - no params
  - [ ] The bottom bar is identical in visual structure to the original (LazyRow filter selector, Spacer, circular capture button)
  - [ ] `CameraScaffold` is called with `media3Effects = selectedFilter.getMedia3Effects()` (same dynamic behavior as the original `LaunchedEffect(selectedFilter)` switch)
  - [ ] `CameraScaffold` is called with `logTag = "FilterSelector"` (preserves the original log tag for grep-ability)
  - [ ] The capture button still calls `takePicture(context, imageCapture, mainExecutor, "FilterSelector")` - the filename prefix reverts to the default `"IMG"` (same as the original `FilterSelectorCameraScreen` line 251)
  - [ ] No duplicate permission flow / Media3 init / `ProcessCameraProvider` / `UseCaseGroup` code remains in `FilterSelectorCameraScreen.kt`
  - [ ] Imports that moved to `CameraScaffold.kt` are no longer in `FilterSelectorCameraScreen.kt`

  **QA Scenarios (MANDATORY)**:
  ```
  Scenario: Module compiles after FilterSelector refactor
    Tool: Bash (gradle)
    Preconditions: Tasks 1, 2, 3, 4, 5, 7 all done
    Steps:
      1. Run: `./gradlew.bat :app:compileDebugKotlin --no-daemon 2>&1 | Tee-Object -FilePath "graphify-out/evidence/dedupe/task-6-compile.txt"`
      2. Assert: BUILD SUCCESSFUL
    Expected Result: `FilterSelectorCameraScreen()` still resolves from `XCamScreen.kt:71`.
    Failure Indicators: `FilterSelectorCameraScreen` is missing or has wrong signature, `Unresolved reference: takePicture` (would mean the import in the new file is missing), `Unresolved reference: CameraScaffold`.
    Evidence: graphify-out/evidence/dedupe/task-6-compile.txt

  Scenario: File line count shrunk
    Tool: Bash (filesystem)
    Steps:
      1. Run: `(Get-Content -LiteralPath "app/src/main/kotlin/com/greeffer/xcam/fx/x/FilterSelectorCameraScreen.kt" | Measure-Object -Line).Lines`
      2. Assert: line count < 100
    Expected Result: Substantial reduction from the original 271 lines.
    Failure Indicators: Line count >= 250 (means the scaffolding wasn't actually extracted).
    Evidence: graphify-out/evidence/dedupe/task-6-linecount.txt
  ```

  **Commit**: YES (groups with Wave 2 batch: 5, 6, 7)
  - Message: `refactor(camera,ui,fx): wire callers to new shared abstractions`
  - Files: `FilterSelectorCameraScreen.kt`
  - Pre-commit: `./gradlew.bat :app:compileDebugKotlin` (must exit 0)

- [ ] 7. Refactor `ClassicSepiaEffect` and `VignetterShaderProgram` to extend `CopyShaderProgram`

  **What to do**:
  - Replace `app/src/main/kotlin/com/greeffer/xcam/fx/x/ClassicSepiaEffect.kt` with:
    ```kotlin
    package com.greeffer.xcam.fx.x

    import androidx.annotation.OptIn
    import androidx.media3.common.util.GlProgram
    import androidx.media3.common.util.UnstableApi

    @OptIn(UnstableApi::class)
    class ClassicSepiaEffect : CopyShaderEffect(FRAGMENT_SHADER_ASSET_PATH) {
        override fun configureExtraUniforms(
            glProgram: GlProgram,
            inputWidth: Int,
            inputHeight: Int,
        ) {
            glProgram.setFloatsUniform("uColorMatrix", SEPIA_MATRIX)
        }

        private companion object {
            const val FRAGMENT_SHADER_ASSET_PATH = "shaders/sepia_fragment.glsl"
            val SEPIA_MATRIX = floatArrayOf(
                0.393f, 0.349f, 0.272f, 0f, 0.769f, 0.686f, 0.534f, 0f,
                0.189f, 0.168f, 0.131f, 0f, 0f, 0f, 0f, 1f,
            )
        }
    }
    ```
  - Replace `app/src/main/kotlin/com/greeffer/xcam/fx/x/VignetterShaderProgram.kt` with:
    ```kotlin
    package com.greeffer.xcam.fx.x

    import androidx.annotation.OptIn
    import androidx.media3.common.util.GlProgram
    import androidx.media3.common.util.UnstableApi

    @OptIn(UnstableApi::class)
    class VignetterEffect : CopyShaderEffect(FRAGMENT_SHADER_ASSET_PATH) {
        override fun configureExtraUniforms(
            glProgram: GlProgram,
            inputWidth: Int,
            inputHeight: Int,
        ) {
            glProgram.setFloatsUniform("uResolution", floatArrayOf(inputWidth.toFloat(), inputHeight.toFloat()))
        }

        private companion object {
            const val FRAGMENT_SHADER_ASSET_PATH = "shaders/vignette_fragment.glsl"
        }
    }
    ```
  - Note: the sepia file previously had two top-level classes (`ClassicSepiaEffect` + `ClassicSepiaShaderProgram`). The new file has only `ClassicSepiaEffect` (the program class is inherited from the base).
  - The vignette file had two classes (`VignetterEffect` + `VignetterShaderProgram`). New file has only `VignetterEffect`.
  - Both new files declare `import androidx.annotation.OptIn` and `import androidx.media3.common.util.UnstableApi` for the `@OptIn(UnstableApi::class)` annotation; no longer need `GlProgram` ctor or `BaseGlShaderProgram`.

  **Must NOT do**:
  - Do NOT change the GLSL asset paths (`shaders/sepia_fragment.glsl`, `shaders/vignette_fragment.glsl`).
  - Do NOT change the color matrix values.
  - Do NOT change the GLSL fragment shader files themselves (they live in `app/src/main/assets/shaders/`).
  - Do NOT touch the `DataRepository.kt` `XCameraFilter` enum or its `getMedia3Effects()` function (it still references `ClassicSepiaEffect()` and `VignetterEffect()`).
  - Do NOT touch `sepiaSamples.kt` (kept as dead code per user decision).

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Two small file replacements, mechanical extraction.
  - **Skills**: none

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 5, 6)
  - **Blocks**: F1-F4
  - **Blocked By**: Task 3 (the base class)

  **References**:
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/ClassicSepiaEffect.kt:1-67` - whole file (read done)
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/VignetterShaderProgram.kt:1-60` - whole file (read done)
  - `app/src/main/kotlin/com/greeffer/xcam/fx/x/CopyShaderProgram.kt` - new file from Task 3
  - `app/src/main/kotlin/com/greeffer/xcam/data/DataRepository.kt:15-32` - `XCameraFilter.SEPIA -> ClassicSepiaEffect()` and `XCameraFilter.VIGNETTE -> VignetterEffect()` - same constructor calls
  - `app/src/main/assets/shaders/sepia_fragment.glsl` and `app/src/main/assets/shaders/vignette_fragment.glsl` - unchanged

  **Acceptance Criteria**:
  - [ ] `ClassicSepiaEffect.kt` is <40 lines
  - [ ] `VignetterShaderProgram.kt` is <40 lines (kept the old filename for minimal git churn)
  - [ ] Neither file declares `BaseGlShaderProgram` or `GlProgram` ctor
  - [ ] Both files declare `@OptIn(UnstableApi::class)`
  - [ ] Sepia effect sets `uColorMatrix` uniform to the same 16-float matrix as the original (line 31 of the original file)
  - [ ] Vignette effect sets `uResolution` uniform to `floatArrayOf(inputWidth.toFloat(), inputHeight.toFloat())` (same as original line 32)
  - [ ] GLSL asset paths unchanged
  - [ ] `XCameraFilter.getMedia3Effects()` in `DataRepository.kt:23-31` still references `ClassicSepiaEffect()` and `VignetterEffect()` with no constructor args (matching the new constructors)

  **QA Scenarios (MANDATORY)**:
  ```
  Scenario: Module compiles with both shader effects extending base
    Tool: Bash (gradle)
    Preconditions: Tasks 1, 2, 3, 4, 5, 6 all done
    Steps:
      1. Run: `./gradlew.bat :app:compileDebugKotlin --no-daemon 2>&1 | Tee-Object -FilePath "graphify-out/evidence/dedupe/task-7-compile.txt"`
      2. Assert: BUILD SUCCESSFUL
    Expected Result: Sepia and Vignette classes inherit from `CopyShaderEffect` / `CopyShaderProgram` without `GlProgram` construction errors. The `DataRepository.kt` enum's `XCameraFilter.SEPIA -> ClassicSepiaEffect()` call still resolves.
    Failure Indicators: `Unresolved reference: GlProgram` in subclass, `Unresolved reference: BaseGlShaderProgram`, `ClassicSepiaEffect` constructor mismatch.
    Evidence: graphify-out/evidence/dedupe/task-7-compile.txt

  Scenario: Asset path is still present
    Tool: Bash (filesystem)
    Steps:
      1. Run: `Test-Path -LiteralPath "app/src/main/assets/shaders/sepia_fragment.glsl"` AND `Test-Path -LiteralPath "app/src/main/assets/shaders/vignette_fragment.glsl"` AND `Test-Path -LiteralPath "app/src/main/assets/shaders/vertex_shader_copy.glsl"`
      2. Assert: all three return `True`
    Expected Result: The GLSL asset files (unchanged by this refactor) still exist on disk so the GL programs can find them.
    Failure Indicators: Any missing file means the shader will fail at runtime with a GlProgram ctor error.
    Evidence: graphify-out/evidence/dedupe/task-7-assets.txt
  ```

  **Commit**: YES (groups with Wave 2 batch: 5, 6, 7)
  - Message: `refactor(camera,ui,fx): wire callers to new shared abstractions`
  - Files: `ClassicSepiaEffect.kt`, `VignetterShaderProgram.kt`
  - Pre-commit: `./gradlew.bat :app:compileDebugKotlin` (must exit 0)

---

## Final Verification Wave (MANDATORY - after ALL implementation tasks)

> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.
> **Do NOT auto-proceed after verification. Wait for user's explicit approval before marking work complete.**
> **Never mark F1-F4 as checked before getting user's okay.** Rejection or user feedback -> fix -> re-run -> present again -> wait for okay.

- [ ] F1. **Plan Compliance Audit** - `oracle`
  Read the plan end-to-end. For each "Must Have": verify implementation exists (read file, gradle build). For each "Must NOT Have": search codebase for forbidden patterns - reject with file:line if found. Check evidence files exist in `graphify-out/evidence/dedupe/`. Compare deliverables against plan.
  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`

- [ ] F2. **Code Quality Review** - `unspecified-high`
  Run `./gradlew.bat :app:compileDebugKotlin` + `:app:assembleDebug` + `:app:lintDebug`. Review all changed files for: `as any`/`@ts-ignore` (Kotlin: `as Any`/`@Suppress`), empty catches, AI slop (excessive comments, over-abstraction, generic names). Verify dead files are byte-equivalent (git diff shows no changes to them).
  Output: `Build [PASS/FAIL] | Lint [PASS/FAIL] | Tests [N pass/N fail] | Files [N clean/N issues] | VERDICT`

- [ ] F3. **Build + Unit Test Verification** - `unspecified-high`
  From clean state: run `./gradlew.bat clean :app:assembleDebug :app:testDebugUnitTest :app:compileDebugAndroidTestKotlin`. Capture all stdout. Verify `MainScreenViewModelTest` runs (1+ tests, 0 failures) and androidTest compiles. Save evidence to `graphify-out/evidence/dedupe/final-qa/`.
  Output: `Build [PASS/FAIL] | UnitTest [N/N pass] | AndroidTestCompile [PASS/FAIL] | VERDICT`

- [ ] F4. **Scope Fidelity Check** - `deep`
  For each task: read "What to do", read actual diff (`git diff` excluding new files). Verify 1:1 - everything in spec was built (no missing), nothing beyond spec was built (no creep). Check "Must NOT do" compliance. Detect cross-task contamination: did task 6 touch task 7's files? Flag unaccounted changes. Confirm dead files (`CameraWithMedia3EffectScreen.kt`, `XViewfinder.kt`, `sepiaSamples.kt`) are byte-equivalent to HEAD~1.
  Output: `Tasks [N/N compliant] | Contamination [CLEAN/N issues] | Unaccounted [CLEAN/N files] | DeadFilesUntouched [YES/NO] | VERDICT`

---

## Commit Strategy

- **Wave 1 commit**: `refactor(camera,ui,fx): extract shared abstractions and fix test paths` - new files + test moves (single commit, all four Wave-1 tasks land atomically so the build stays green at every commit boundary).
- **Wave 2 commit**: `refactor(camera,ui,fx): wire callers to new shared abstractions` - callers consume the new helpers.
- Pre-commit for each: `./gradlew.bat :app:compileDebugKotlin` (must exit 0).

---

## Success Criteria

### Verification Commands
```bash
./gradlew.bat :app:compileDebugKotlin          # Expected: BUILD SUCCESSFUL
./gradlew.bat :app:assembleDebug               # Expected: BUILD SUCCESSFUL
./gradlew.bat :app:testDebugUnitTest           # Expected: BUILD SUCCESSFUL, MainScreenViewModelTest passes
./gradlew.bat :app:compileDebugAndroidTestKotlin  # Expected: BUILD SUCCESSFUL
./gradlew.bat :app:lintDebug                   # Expected: no NEW warnings vs pre-refactor
git diff --stat HEAD~2 HEAD                     # Expected: 3 new files + 4 modified + 2 moved; net line count delta <= 0 for modified files
```

### Final Checklist
- [ ] All "Must Have" present
- [ ] All "Must NOT Have" absent
- [ ] All three new abstractions compiled and used by callers
- [ ] Both ViewModels' `uiState` field is one line: `dataRepository.data.asResourceState(viewModelScope)`
- [ ] `FilterSelectorCameraScreen` body is <40 lines (was 271) and is almost entirely `CameraScaffold(...)` call
- [ ] `ClassicSepiaEffect.kt` + `VignetterShaderProgram.kt` each have <30 lines of subclass code (init + the per-effect uniform)
- [ ] Test files at `app/src/test/java/com/greeffer/xcam/ui/main/` and `app/src/androidTest/java/com/greeffer/xcam/ui/main/`
- [ ] No `com.examplepackage` strings anywhere
- [ ] Dead files (`CameraWithMedia3EffectScreen.kt`, `XViewfinder.kt`, `sepiaSamples.kt`) byte-equal to pre-refactor
