# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project Snapshot

- Android app with a single module: `:app`
- Kotlin + Jetpack Compose (Material 3)
- Navigation stack uses Navigation 3 (`androidx.navigation3`), not string-route Navigation Compose patterns
- Toolchain: AGP 9.2.1, Gradle 9.4.1, Kotlin 2.3.20, Java toolchain 17
- SDK levels: minSdk 24, target/compileSdk 37

## Source of Truth

- Dependency and plugin versions: `gradle/libs.versions.toml`
- App build config: `app/build.gradle.kts`
- Entry point: `app/src/main/kotlin/com/greeffer/xcam/MainActivity.kt`
- Navigation wiring: `app/src/main/kotlin/com/greeffer/xcam/Navigation.kt`
- Navigation keys: `app/src/main/kotlin/com/greeffer/xcam/NavigationKeys.kt`
- Camera integration: `app/src/main/kotlin/com/greeffer/xcam/fx/x/CameraWithMedia3EffectScreen.kt`

## Common Commands

Use from repository root.

### uv

Use uv for all python related tasks. 

- `uv run ...`
- `uv tool ...`

For eaxample `graphify`: 

```pwsh

PS C:\Users\User> uv run graphify --help
Usage: graphify <command>

Commands:
  install [--platform P]  copy skill to platform config dir (claude|windows|codex|opencode|aider|claw|droid|trae|trae-cn|gemini|cursor|antigravity|hermes|kiro|pi)
  path "A" "B"            shortest path between two nodes in graph.json
    --graph <path>          path to graph.json (default graphify-out/graph.json)
  explain "X"             plain-language explanation of a node and its neighbors
    --graph <path>          path to graph.json (default graphify-out/graph.json)
  clone <github-url>      clone a GitHub repo locally and print its path for /graphify
  merge-driver <base> <current> <other>  git merge driver: union-merge two graph.json files (set up via hook install)
  merge-graphs <g1> <g2>  merge two or more graph.json files into one cross-repo graph
    --out <path>            output path (default: graphify-out/merged-graph.json)
    --branch <branch>       checkout a specific branch (default: repo default)
    --out <dir>             clone to a custom directory (default: ~/.graphify/repos/<owner>/<repo>)
  add <url>               fetch a URL and save it to ./raw, then update the graph
    --author "Name"         tag the author of the content
    --contributor "Name"    tag who added it to the corpus
    --dir <path>            target directory (default: ./raw)
  watch <path>            watch a folder and rebuild the graph on code changes
  update <path>           re-extract code files and update the graph (no LLM needed)
    --force                 overwrite graph.json even if the rebuild has fewer nodes
                            (also: GRAPHIFY_FORCE=1 env var; use after refactors that delete code)
  cluster-only <path>     rerun clustering on an existing graph.json and regenerate report
    --no-viz                skip graph.html generation (useful for >5000 node graphs / CI)
  query "<question>"       BFS traversal of graph.json for a question
    --dfs                   use depth-first instead of breadth-first
    --context C             explicit edge-context filter (repeatable)
    --budget N              cap output at N tokens (default 2000)
    --graph <path>          path to graph.json (default graphify-out/graph.json)
  save-result             save a Q&A result to graphify-out/memory/ for graph feedback loop
    --question Q            the question asked
    --answer A              the answer to save
    --type T                query type: query|path_query|explain (default: query)
    --nodes N1 N2 ...       source node labels cited in the answer
    --memory-dir DIR        memory directory (default: graphify-out/memory)
  check-update <path>     check needs_update flag and notify if semantic re-extraction is pending (cron-safe)
  tree                    emit a D3 v7 collapsible-tree HTML for graph.json
    --graph PATH            path to graph.json (default graphify-out/graph.json)
    --output HTML           output path (default graphify-out/GRAPH_TREE.html)
    --root PATH             filesystem root for the hierarchy
    --max-children N        cap children per node (default 200)
    --top-k-edges N         per-symbol outbound edges in inspector (default 12)
    --label NAME            project label in header
  extract <path>          headless full extraction (AST + semantic LLM) for CI/scripts
    --backend B             kimi|claude (default: whichever API key is set)
    --out DIR               output dir (default: <path>); writes <DIR>/graphify-out/
    --no-cluster            skip clustering, write raw extraction only
  benchmark [graph.json]  measure token reduction vs naive full-corpus approach
  ...

  ```

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

- Keep UI code in `app/src/main/kotlin/com/greeffer/xcam/ui/main`
- Keep non-UI data logic in `app/src/main/kotlin/com/greeffer/xcam/data`
- Keep effects/utilities in `app/src/main/kotlin/com/greeffer/xcam/fx`
- Compose + ViewModel is the active pattern for screen state
- Navigation destinations are typed `NavKey` objects (`@Serializable`) in `app/src/main/kotlin/com/greeffer/xcam/NavigationKeys.kt`
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
