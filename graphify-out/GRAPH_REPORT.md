# Graph Report - .  (2026-06-03)

## Corpus Check
- Corpus is ~6,858 words - fits in a single context window. You may not need a graph.

## Summary
- 107 nodes · 118 edges · 19 communities (14 shown, 5 thin omitted)
- Extraction: 76% EXTRACTED · 22% INFERRED · 2% AMBIGUOUS · INFERRED: 26 edges (avg confidence: 0.77)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_App Entry & Navigation|App Entry & Navigation]]
- [[_COMMUNITY_CameraX & Media3 Effects|CameraX & Media3 Effects]]
- [[_COMMUNITY_ViewModel & UI State|ViewModel & UI State]]
- [[_COMMUNITY_Architecture & Patterns|Architecture & Patterns]]
- [[_COMMUNITY_Media3 Effect Subclasses|Media3 Effect Subclasses]]
- [[_COMMUNITY_Build Configuration|Build Configuration]]
- [[_COMMUNITY_FX Layer & Repository|FX Layer & Repository]]
- [[_COMMUNITY_Compose UI Previews|Compose UI Previews]]
- [[_COMMUNITY_Camera UI Components|Camera UI Components]]
- [[_COMMUNITY_Navigation Route Keys|Navigation Route Keys]]
- [[_COMMUNITY_Data Repository Interface|Data Repository Interface]]
- [[_COMMUNITY_XEffect Executor|XEffect Executor]]
- [[_COMMUNITY_XViewModel Camera|XViewModel Camera]]
- [[_COMMUNITY_Root Build Logic|Root Build Logic]]

## God Nodes (most connected - your core abstractions)
1. `GlEffect (Direct Subclass of Effect)` - 10 edges
2. `MainScreen()` - 6 edges
3. `MainScreen Composable (ViewModel-driven)` - 6 edges
4. `MainScreen Internal Composable (data renderer)` - 5 edges
5. `MainScreenViewModel` - 5 edges
6. `XViewfinder Composable` - 5 edges
7. `CameraX Camera Pipeline` - 5 edges
8. `fx Package (Effects/Utilities)` - 5 edges
9. `XCamTheme()` - 4 edges
10. `XScreen (XViewModel variant)` - 4 edges

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

## Communities (19 total, 5 thin omitted)

### Community 0 - "App Entry & Navigation"
Cohesion: 0.17
Nodes (8): Greeting(), MainScreen(), MainScreenPortraitPreview(), MainScreenPreview(), MainScreenTest, XCamTheme(), MainActivity, MainNavigation()

### Community 1 - "CameraX & Media3 Effects"
Cohesion: 0.24
Nodes (13): AGENTS.md â€” Project Guidance, CameraX Camera Pipeline, CameraXViewfinder (Compose Integration), androidx.media3.common.Effect (Marker Interface), fx Package (Effects/Utilities), getDurationAfterEffectApplied(durationUs: Long): Long, Presentation (Frame Geometry Controller), Preview (CameraX Preview UseCase) (+5 more)

### Community 2 - "ViewModel & UI State"
Cohesion: 0.24
Nodes (7): Error, Loading, MainScreenUiState, MainScreenViewModel, Success, FakeMyModelRepository, MainScreenViewModelTest

### Community 3 - "Architecture & Patterns"
Cohesion: 0.29
Nodes (11): AGENTS.md Project Guidance, Compose + ViewModel Architectural Pattern, DataRepository Interface, DefaultDataRepository, FakeMyModelRepository Test Double, App Launcher Icon Assets (multi-density), MainScreen Composable (ViewModel-driven), MainScreenViewModel (+3 more)

### Community 4 - "Media3 Effect Subclasses"
Cohesion: 0.25
Nodes (9): AlphaScale (Translucency Effect), Brightness (Frame Modifier), Contrast (RgbMatrix Subclass), Crop (Vertex Shader Crop), GlEffect (Direct Subclass of Effect), GlMatrixTransformation (4x4 Vertex Shader Matrix), GlShaderProgram, MatrixTransformation (3x3 Vertex Shader Matrix) (+1 more)

### Community 5 - "Build Configuration"
Cohesion: 0.25
Nodes (8): App Module Build Script, MainActivity, MainNavigation Composable, Main NavKey, XCam NavKey, Gradle Settings Script, Color Palette Constants, XCamTheme Composable

### Community 6 - "FX Layer & Repository"
Cohesion: 0.32
Nodes (8): DataRepository Interface, DefaultDataRepository, XCamScreen Composable, XEffect Executor Provider, XScreen (Navigation variant), XScreen (XViewModel variant), XViewModel, XViewfinder Composable

### Community 7 - "Compose UI Previews"
Cohesion: 0.33
Nodes (7): MainScreenTest (Compose UI Instrumentation), Greeting Composable, MainScreen Internal Composable (data renderer), MainScreenPortraitPreview Composable, MainScreenPreview Composable, Custom Material 3 Typography, XCamTheme Composable Wrapper

### Community 8 - "Camera UI Components"
Cohesion: 0.33
Nodes (3): XCamScreen(), XScreen(), XViewfinder()

## Ambiguous Edges - Review These
- `MainNavigation Composable` → `XScreen (Navigation variant)`  [AMBIGUOUS]
  app/src/main/kotlin/com/greeffer/xcam/Navigation.kt · relation: calls
- `androidx.media3.common.Effect (Marker Interface)` → `XEffect (Executor Utility)`  [AMBIGUOUS]
  app/src/main/kotlin/com/greeffer/xcam/fx/XEffect.kt · relation: semantically_similar_to

## Knowledge Gaps
- **26 isolated node(s):** `Main`, `XCam`, `DataRepository`, `DefaultDataRepository`, `XEffect` (+21 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `MainNavigation Composable` and `XScreen (Navigation variant)`?**
  _Edge tagged AMBIGUOUS (relation: calls) - confidence is low._
- **What is the exact relationship between `androidx.media3.common.Effect (Marker Interface)` and `XEffect (Executor Utility)`?**
  _Edge tagged AMBIGUOUS (relation: semantically_similar_to) - confidence is low._
- **Why does `GlEffect (Direct Subclass of Effect)` connect `Media3 Effect Subclasses` to `CameraX & Media3 Effects`?**
  _High betweenness centrality (0.024) - this node is a cross-community bridge._
- **Why does `Greeting()` connect `App Entry & Navigation` to `Camera UI Components`?**
  _High betweenness centrality (0.016) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `MainScreen()` (e.g. with `.setup()` and `MainNavigation()`) actually correct?**
  _`MainScreen()` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Main`, `XCam`, `DataRepository` to the rest of the system?**
  _26 weakly-connected nodes found - possible documentation gaps or missing edges._