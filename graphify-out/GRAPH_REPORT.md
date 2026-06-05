# Graph Report - .  (2026-06-05)

## Corpus Check
- 40 files · ~8,693 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 173 nodes · 194 edges · 28 communities (19 shown, 9 thin omitted)
- Extraction: 83% EXTRACTED · 16% INFERRED · 1% AMBIGUOUS · INFERRED: 31 edges (avg confidence: 0.77)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Navigation & Main UI|Navigation & Main UI]]
- [[_COMMUNITY_Build Config & Data Layer|Build Config & Data Layer]]
- [[_COMMUNITY_MainScreen ViewModel & State|MainScreen ViewModel & State]]
- [[_COMMUNITY_CameraX & Media3 Pipeline|CameraX & Media3 Pipeline]]
- [[_COMMUNITY_Architecture & Launcher Assets|Architecture & Launcher Assets]]
- [[_COMMUNITY_Media3 GL Effects Subclasses|Media3 GL Effects Subclasses]]
- [[_COMMUNITY_Sepia Shader Effect|Sepia Shader Effect]]
- [[_COMMUNITY_Vignette Shader Effect|Vignette Shader Effect]]
- [[_COMMUNITY_XCam ViewModel & State|XCam ViewModel & State]]
- [[_COMMUNITY_Shader Base Classes|Shader Base Classes]]
- [[_COMMUNITY_UI Previews & Theme|UI Previews & Theme]]
- [[_COMMUNITY_Camera Scaffold & Filters|Camera Scaffold & Filters]]
- [[_COMMUNITY_XScreen & XViewfinder|XScreen & XViewfinder]]
- [[_COMMUNITY_ResourceUiState Extension|ResourceUiState Extension]]
- [[_COMMUNITY_MainScreenTest|MainScreenTest]]
- [[_COMMUNITY_DataRepository & XCameraFilter|DataRepository & XCameraFilter]]
- [[_COMMUNITY_Navigation Keys|Navigation Keys]]
- [[_COMMUNITY_CameraWithMedia3EffectScreen (Dead)|CameraWithMedia3EffectScreen (Dead)]]
- [[_COMMUNITY_XEffect Executor|XEffect Executor]]
- [[_COMMUNITY_XViewModel Surface Provider|XViewModel Surface Provider]]
- [[_COMMUNITY_Root Gradle Build Script|Root Gradle Build Script]]

## God Nodes (most connected - your core abstractions)
1. `GlEffect (Direct Subclass of Effect)` - 10 edges
2. `MainScreen()` - 8 edges
3. `MainScreen Composable (ViewModel-driven)` - 6 edges
4. `CopyShaderProgram` - 6 edges
5. `ClassicSepiaShaderProgram` - 5 edges
6. `FilterSelectorCameraScreen()` - 5 edges
7. `VignetterShaderProgram` - 5 edges
8. `XCamScreen()` - 5 edges
9. `XCam()` - 5 edges
10. `Greeting()` - 5 edges

## Surprising Connections (you probably didn't know these)
- `XEffect (Executor Utility)` --semantically_similar_to--> `androidx.media3.common.Effect (Marker Interface)`  [AMBIGUOUS] [semantically similar]
  app/src/main/kotlin/com/greeffer/xcam/fx/XEffect.kt → raw/developer_android_com_reference_kotlin_androidx_media3_common_Effect.md
- `Presentation (Frame Geometry Controller)` --semantically_similar_to--> `SurfaceRequest (CameraX Frame Surface)`  [INFERRED] [semantically similar]
  raw/developer_android_com_reference_kotlin_androidx_media3_common_Effect.md → app/src/main/kotlin/com/greeffer/xcam/fx/XViewModel.kt
- `CameraXViewfinder (Compose Integration)` --part_of--> `CameraX Camera Pipeline`  [INFERRED]
  app/src/main/kotlin/com/greeffer/xcam/fx/XViewfinder.kt → AGENTS.md
- `Preview (CameraX Preview UseCase)` --part_of--> `CameraX Camera Pipeline`  [INFERRED]
  app/src/main/kotlin/com/greeffer/xcam/fx/XViewModel.kt → AGENTS.md
- `MainScreen Composable (ViewModel-driven)` --conceptually_related_to--> `Compose + ViewModel Architectural Pattern`  [INFERRED]
  app/src/main/kotlin/com/greeffer/xcam/ui/main/MainScreen.kt → AGENTS.md

## Hyperedges (group relationships)
- **Media3 Effect Class Hierarchy** — effect_media3_interface, gleffect, glshaderprogram, alphascale, brightness, crop, rgbmatrix, contrast [EXTRACTED 1.00]
- **CameraX Compose Viewfinder Pipeline** — xscreen_composable, xviewfinder_composable, cameraxviewfinder_compose, xviewmodel, surfacerequest, preview_camerax [INFERRED 0.85]
- **Frame Geometry Control Pattern** — presentation, crop, glmatrixtransformation, matrixtransformation, cameraxviewfinder_compose [INFERRED 0.70]

## Communities (28 total, 9 thin omitted)

### Community 0 - "Navigation & Main UI"
Cohesion: 0.21
Nodes (9): Greeting(), MainScreen(), MainScreenPortraitPreview(), MainScreenPreview(), XCamTheme(), XCam(), XCamScreen(), MainActivity (+1 more)

### Community 1 - "Build Config & Data Layer"
Cohesion: 0.14
Nodes (16): App Module Build Script, DataRepository Interface, DefaultDataRepository, XCamScreen Composable, XEffect Executor Provider, XScreen (Navigation variant), XScreen (XViewModel variant), XViewModel (+8 more)

### Community 2 - "MainScreen ViewModel & State"
Cohesion: 0.21
Nodes (7): Error, Loading, MainScreenUiState, MainScreenViewModel, Success, FakeMyModelRepository, MainScreenViewModelTest

### Community 3 - "CameraX & Media3 Pipeline"
Cohesion: 0.27
Nodes (12): AGENTS.md â€” Project Guidance, CameraX Camera Pipeline, CameraXViewfinder (Compose Integration), androidx.media3.common.Effect (Marker Interface), fx Package (Effects/Utilities), getDurationAfterEffectApplied(durationUs: Long): Long, Preview (CameraX Preview UseCase), SurfaceRequest (CameraX Frame Surface) (+4 more)

### Community 4 - "Architecture & Launcher Assets"
Cohesion: 0.29
Nodes (11): AGENTS.md Project Guidance, Compose + ViewModel Architectural Pattern, DataRepository Interface, DefaultDataRepository, FakeMyModelRepository Test Double, App Launcher Icon Assets (multi-density), MainScreen Composable (ViewModel-driven), MainScreenViewModel (+3 more)

### Community 5 - "Media3 GL Effects Subclasses"
Cohesion: 0.22
Nodes (10): AlphaScale (Translucency Effect), Brightness (Frame Modifier), Contrast (RgbMatrix Subclass), Crop (Vertex Shader Crop), GlEffect (Direct Subclass of Effect), GlMatrixTransformation (4x4 Vertex Shader Matrix), GlShaderProgram, MatrixTransformation (3x3 Vertex Shader Matrix) (+2 more)

### Community 8 - "XCam ViewModel & State"
Cohesion: 0.22
Nodes (5): Error, Loading, Success, XCamUiState, XCamViewModel

### Community 10 - "UI Previews & Theme"
Cohesion: 0.33
Nodes (7): MainScreenTest (Compose UI Instrumentation), Greeting Composable, MainScreen Internal Composable (data renderer), MainScreenPortraitPreview Composable, MainScreenPreview Composable, Custom Material 3 Typography, XCamTheme Composable Wrapper

### Community 11 - "Camera Scaffold & Filters"
Cohesion: 0.38
Nodes (4): CameraScaffold(), takePicture(), FilterSelectorCameraScreen(), takePicture()

### Community 12 - "XScreen & XViewfinder"
Cohesion: 0.33
Nodes (3): XCamScreen(), XScreen(), XViewfinder()

### Community 13 - "ResourceUiState Extension"
Cohesion: 0.47
Nodes (5): asResourceState(), Error, Loading, ResourceUiState, Success

### Community 15 - "DataRepository & XCameraFilter"
Cohesion: 0.5
Nodes (3): DataRepository, DefaultDataRepository, XCameraFilter

## Ambiguous Edges - Review These
- `MainNavigation Composable` → `XScreen (Navigation variant)`  [AMBIGUOUS]
  app/src/main/kotlin/com/greeffer/xcam/Navigation.kt · relation: calls
- `androidx.media3.common.Effect (Marker Interface)` → `XEffect (Executor Utility)`  [AMBIGUOUS]
  app/src/main/kotlin/com/greeffer/xcam/fx/XEffect.kt · relation: semantically_similar_to

## Knowledge Gaps
- **33 isolated node(s):** `Main`, `XCam`, `DataRepository`, `XCameraFilter`, `DefaultDataRepository` (+28 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **9 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `MainNavigation Composable` and `XScreen (Navigation variant)`?**
  _Edge tagged AMBIGUOUS (relation: calls) - confidence is low._
- **What is the exact relationship between `androidx.media3.common.Effect (Marker Interface)` and `XEffect (Executor Utility)`?**
  _Edge tagged AMBIGUOUS (relation: semantically_similar_to) - confidence is low._
- **Why does `Greeting()` connect `Navigation & Main UI` to `XScreen & XViewfinder`?**
  _High betweenness centrality (0.016) - this node is a cross-community bridge._
- **Why does `MainScreen()` connect `Navigation & Main UI` to `MainScreenTest`?**
  _High betweenness centrality (0.016) - this node is a cross-community bridge._
- **Why does `XCam()` connect `Navigation & Main UI` to `Camera Scaffold & Filters`?**
  _High betweenness centrality (0.014) - this node is a cross-community bridge._
- **Are the 3 inferred relationships involving `MainScreen()` (e.g. with `.setup()` and `MainNavigation()`) actually correct?**
  _`MainScreen()` has 3 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Main`, `XCam`, `DataRepository` to the rest of the system?**
  _33 weakly-connected nodes found - possible documentation gaps or missing edges._