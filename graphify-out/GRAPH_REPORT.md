# Graph Report - beems  (2026-06-05)

## Corpus Check
- 23 files · ~8,692 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 144 nodes · 156 edges · 25 communities (17 shown, 8 thin omitted)
- Extraction: 79% EXTRACTED · 19% INFERRED · 1% AMBIGUOUS · INFERRED: 30 edges (avg confidence: 0.77)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `503c2a91`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 24|Community 24]]

## God Nodes (most connected - your core abstractions)
1. `GlEffect (Direct Subclass of Effect)` - 10 edges
2. `MainScreen()` - 7 edges
3. `MainScreen Composable (ViewModel-driven)` - 6 edges
4. `ClassicSepiaShaderProgram` - 5 edges
5. `VignetterShaderProgram` - 5 edges
6. `MainScreen Internal Composable (data renderer)` - 5 edges
7. `MainScreenViewModel` - 5 edges
8. `XViewfinder Composable` - 5 edges
9. `CameraX Camera Pipeline` - 5 edges
10. `fx Package (Effects/Utilities)` - 5 edges

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

## Communities (25 total, 8 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.17
Nodes (11): Greeting(), MainScreen(), MainScreenPortraitPreview(), MainScreenPreview(), XCamTheme(), FilterSelectorCameraScreen(), takePicture(), XCam() (+3 more)

### Community 1 - "Community 1"
Cohesion: 0.14
Nodes (16): App Module Build Script, DataRepository Interface, DefaultDataRepository, XCamScreen Composable, XEffect Executor Provider, XScreen (Navigation variant), XScreen (XViewModel variant), XViewModel (+8 more)

### Community 2 - "Community 2"
Cohesion: 0.27
Nodes (12): AGENTS.md â€” Project Guidance, CameraX Camera Pipeline, CameraXViewfinder (Compose Integration), androidx.media3.common.Effect (Marker Interface), fx Package (Effects/Utilities), getDurationAfterEffectApplied(durationUs: Long): Long, Preview (CameraX Preview UseCase), SurfaceRequest (CameraX Frame Surface) (+4 more)

### Community 3 - "Community 3"
Cohesion: 0.24
Nodes (7): Error, Loading, MainScreenUiState, MainScreenViewModel, Success, FakeMyModelRepository, MainScreenViewModelTest

### Community 4 - "Community 4"
Cohesion: 0.29
Nodes (11): AGENTS.md Project Guidance, Compose + ViewModel Architectural Pattern, DataRepository Interface, DefaultDataRepository, FakeMyModelRepository Test Double, App Launcher Icon Assets (multi-density), MainScreen Composable (ViewModel-driven), MainScreenViewModel (+3 more)

### Community 5 - "Community 5"
Cohesion: 0.22
Nodes (10): AlphaScale (Translucency Effect), Brightness (Frame Modifier), Contrast (RgbMatrix Subclass), Crop (Vertex Shader Crop), GlEffect (Direct Subclass of Effect), GlMatrixTransformation (4x4 Vertex Shader Matrix), GlShaderProgram, MatrixTransformation (3x3 Vertex Shader Matrix) (+2 more)

### Community 6 - "Community 6"
Cohesion: 0.25
Nodes (5): Error, Loading, Success, XCamUiState, XCamViewModel

### Community 9 - "Community 9"
Cohesion: 0.33
Nodes (7): MainScreenTest (Compose UI Instrumentation), Greeting Composable, MainScreen Internal Composable (data renderer), MainScreenPortraitPreview Composable, MainScreenPreview Composable, Custom Material 3 Typography, XCamTheme Composable Wrapper

### Community 10 - "Community 10"
Cohesion: 0.33
Nodes (3): XCamScreen(), XScreen(), XViewfinder()

### Community 11 - "Community 11"
Cohesion: 0.5
Nodes (3): DataRepository, DefaultDataRepository, XCameraFilter

## Ambiguous Edges - Review These
- `MainNavigation Composable` → `XScreen (Navigation variant)`  [AMBIGUOUS]
  app/src/main/kotlin/com/greeffer/xcam/Navigation.kt · relation: calls
- `androidx.media3.common.Effect (Marker Interface)` → `XEffect (Executor Utility)`  [AMBIGUOUS]
  app/src/main/kotlin/com/greeffer/xcam/fx/XEffect.kt · relation: semantically_similar_to

## Knowledge Gaps
- **31 isolated node(s):** `Main`, `XCam`, `DataRepository`, `XCameraFilter`, `DefaultDataRepository` (+26 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **8 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `MainNavigation Composable` and `XScreen (Navigation variant)`?**
  _Edge tagged AMBIGUOUS (relation: calls) - confidence is low._
- **What is the exact relationship between `androidx.media3.common.Effect (Marker Interface)` and `XEffect (Executor Utility)`?**
  _Edge tagged AMBIGUOUS (relation: semantically_similar_to) - confidence is low._
- **Why does `MainScreen()` connect `Community 0` to `Community 12`?**
  _High betweenness centrality (0.017) - this node is a cross-community bridge._
- **Why does `Greeting()` connect `Community 0` to `Community 10`?**
  _High betweenness centrality (0.015) - this node is a cross-community bridge._
- **Why does `GlEffect (Direct Subclass of Effect)` connect `Community 5` to `Community 2`?**
  _High betweenness centrality (0.013) - this node is a cross-community bridge._
- **Are the 3 inferred relationships involving `MainScreen()` (e.g. with `.setup()` and `MainNavigation()`) actually correct?**
  _`MainScreen()` has 3 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Main`, `XCam`, `DataRepository` to the rest of the system?**
  _31 weakly-connected nodes found - possible documentation gaps or missing edges._