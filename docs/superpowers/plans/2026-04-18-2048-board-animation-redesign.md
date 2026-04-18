# 2048 Board Animation Redesign Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the current refresh-based 2048 board with a result-driven animated board that supports polished slide, merge, and spawn effects without display bugs.

**Architecture:** Introduce a pure move-result engine that reports stable tile identities and animation events, then render that result through a dedicated board view and animator instead of rebuilding `GridPane` cells after every move. Keep `MainGame` as the scene shell while moving board rules, visuals, and animation timing into focused classes.

**Tech Stack:** Java 22 preview, JavaFX controls/media, JUnit 5, Maven

---

### Task 1: Add rule-engine tests for move result data

**Files:**
- Create: `src/test/java/com/cz/game2048super/BoardMoveResultTest.java`
- Modify: `src/test/java/com/cz/game2048super/GameControllerTest.java`

- [ ] Step 1: Write failing tests for slide, merge, double-merge, and spawn metadata.
- [ ] Step 2: Run targeted tests and verify they fail for missing move-result types and APIs.
- [ ] Step 3: Implement the minimal move-result model and engine surface needed by the tests.
- [ ] Step 4: Re-run targeted tests and verify they pass.

### Task 2: Build the new board engine and compatibility wrapper

**Files:**
- Create: `src/main/java/com/cz/game2048super/BoardState.java`
- Create: `src/main/java/com/cz/game2048super/BoardPosition.java`
- Create: `src/main/java/com/cz/game2048super/MoveDirection.java`
- Create: `src/main/java/com/cz/game2048super/TileDescriptor.java`
- Create: `src/main/java/com/cz/game2048super/TileMove.java`
- Create: `src/main/java/com/cz/game2048super/MergeEvent.java`
- Create: `src/main/java/com/cz/game2048super/SpawnEvent.java`
- Create: `src/main/java/com/cz/game2048super/MoveResult.java`
- Create: `src/main/java/com/cz/game2048super/GameEngine.java`
- Modify: `src/main/java/com/cz/game2048super/GameController.java`

- [ ] Step 1: Add the new immutable result and position types.
- [ ] Step 2: Implement `GameEngine` with stable tile ids and 2048 merge rules.
- [ ] Step 3: Update `GameController` to delegate to the new engine while preserving the old public methods used elsewhere.
- [ ] Step 4: Run rule-engine tests plus existing controller tests and verify they pass.

### Task 3: Add animation-plan tests and build the board motion model

**Files:**
- Create: `src/test/java/com/cz/game2048super/BoardAnimationPlanTest.java`
- Create: `src/main/java/com/cz/game2048super/MotionTokens.java`
- Create: `src/main/java/com/cz/game2048super/AnimationPlan.java`
- Create: `src/main/java/com/cz/game2048super/BoardAnimationPlanner.java`

- [ ] Step 1: Write failing tests for animation ordering, merge collapse, and delayed spawn.
- [ ] Step 2: Run targeted tests and verify they fail for missing planner APIs.
- [ ] Step 3: Implement the planner and motion tokens with pure data output.
- [ ] Step 4: Re-run animation-plan tests and verify they pass.

### Task 4: Build the new board visuals and animator

**Files:**
- Create: `src/main/java/com/cz/game2048super/TileSkin.java`
- Create: `src/main/java/com/cz/game2048super/TileView.java`
- Create: `src/main/java/com/cz/game2048super/BoardView.java`
- Create: `src/main/java/com/cz/game2048super/BoardAnimator.java`
- Modify: `src/main/java/com/cz/game2048super/Diamond.java`

- [ ] Step 1: Implement the upgraded tile skin system and reusable `TileView`.
- [ ] Step 2: Build `BoardView` with a static board background layer and dynamic tile layer.
- [ ] Step 3: Implement `BoardAnimator` to consume `AnimationPlan` via JavaFX transitions only using transform/opacity.
- [ ] Step 4: Keep `Diamond` as a compatibility wrapper or retire it cleanly if no longer needed.

### Task 5: Integrate the new board into MainGame

**Files:**
- Modify: `src/main/java/com/cz/game2048super/MainGame.java`
- Modify: `src/test/java/com/cz/game2048super/MainGameTest.java`

- [ ] Step 1: Replace direct `GridPane` rebuild logic with `BoardView` + `BoardAnimator`.
- [ ] Step 2: Update input handling to use the new move-result flow and animation lock state.
- [ ] Step 3: Ensure win/game-over/save flows only trigger after animation settling.
- [ ] Step 4: Update tests for the new move flow and compatibility behavior.

### Task 6: Verify, polish, and document deltas

**Files:**
- Modify: `docs/superpowers/specs/2026-04-18-2048-board-animation-redesign-design.md` if implementation deviates

- [ ] Step 1: Run `.\mvnw.cmd test` and verify the full suite passes.
- [ ] Step 2: Run a local app launch check if feasible and validate rapid input, double merge, restart, and mode compatibility.
- [ ] Step 3: Review the spec for implementation deviations and update it only if behavior changed.
- [ ] Step 4: Summarize completed work and residual risks with verification evidence.
