# 13 — QA Plan

## Simulation tests

For every module:
- correct direction transform;
- correct power usage;
- correct particle/value transform;
- boundary behavior;
- disabled/over-cap behavior;
- simultaneous particle behavior.

## Determinism

- identical seed + commands = identical hash after 1K/10K ticks.
- replay recorded command streams in CI.

## Stress

- splitter-heavy layouts;
- loops/recirculation;
- maximum module count;
- high logical throughput;
- long offline duration;
- repeated save/load.

## Economy

- no negative currency;
- no overflow;
- offline timestamp manipulation handled conservatively;
- prestige rewards deterministic and bounded.

## UI/device

Test:
- small phones;
- 60/90/120 Hz displays;
- different density/font scale;
- process kill/restore;
- rotation if orientation lock changes;
- background/foreground;
- touch near cell boundaries;
- rapid repeated placement/undo.

## Gameplay QA questions

After each milestone, human testers answer:
1. What is the goal?
2. Why did output rise/fall?
3. What would you change next?
4. Did the game ever feel unfair?
5. Did you want to keep optimizing after meeting the minimum target?

If players cannot answer #2, stop adding features and fix readability.
