# Efficient Machine

**Platform:** Android  
**Genre:** casual optimization / factory puzzle / incremental  
**Status:** pre-production + playable HTML core prototype  
**Core promise:** *Build a machine. Watch it run. Find the bottleneck. Rebuild it. Make the number explode.*

Efficient Machine is a lightweight Android game built around a programmable grid. A SOURCE emits particles. The player places a small set of modules—turns, boosters, splitters, multipliers, mergers, buffers and later advanced devices—to route particles into one or more RECEIVERs as efficiently as possible.

The game must be immediately readable, playable with one finger, visually generated in code, and deep enough that a good player can outperform a new player through better designs—not merely through larger upgrade numbers.

## Non-negotiable product rules

- Native Android, Kotlin + Jetpack Compose.
- No Unity, Godot, Blender, sprite packs or mandatory bitmap art.
- Core rendering is generated in code: grid, shapes, particles, trails, glow, text and simple procedural effects.
- The simulation must be deterministic and separate from rendering.
- Every powerful module must have a trade-off.
- Progression unlocks new possibilities, not only bigger percentages.
- Short sessions must feel productive; long sessions must support deep optimization.
- Monetization must not interrupt the core optimization loop.
- The game must remain understandable even after dozens of systems are unlocked.

## Repository map

- `prototype/` — current HTML proof-of-concept.
- `docs/01_PRODUCT_VISION.md` — product thesis and audience.
- `docs/02_GAME_DESIGN_DOCUMENT.md` — complete game design.
- `docs/03_CORE_SIMULATION.md` — deterministic simulation rules.
- `docs/04_MODULE_SYSTEM.md` — module catalogue and trade-offs.
- `docs/05_CONTRACTS_DIFFICULTY.md` — goals, procedural contracts and fairness.
- `docs/06_PROGRESSION_PRESTIGE.md` — long-term progression.
- `docs/07_META_MACHINE.md` — machine-inside-machine endgame.
- `docs/08_ANDROID_TECH_SPEC.md` — Android implementation specification.
- `docs/09_ARCHITECTURE.md` — modules, state and code boundaries.
- `docs/10_BALANCE_ECONOMY.md` — formulas and balancing principles.
- `docs/11_BUSINESS_MONETIZATION.md` — business model and monetization.
- `docs/12_ANALYTICS_EXPERIMENTS.md` — KPIs and tests.
- `docs/13_QA_PLAN.md` — functional and simulation QA.
- `docs/14_ROADMAP.md` — staged development plan.
- `docs/15_PLAY_STORE_LAUNCH.md` — launch and store plan.
- `docs/16_RESEARCH_SOURCES.md` — research notes and source links.
- `TODO.md` — current execution backlog.
- `AGENTS.md` — mandatory instructions for coding agents.

## First milestone

Do **not** start with prestige, ads, cloud, PvP or a giant module library. The first Android milestone must prove one thing:

> With SOURCE, RECEIVER, TURN, BOOST, ×2 and SPLIT, does the player voluntarily rebuild the machine to improve OUTPUT?

If that is not fun, meta systems will not save the product.
