# AGENTS.md — mandatory project rules

This file is authoritative for any coding agent working in this repository.

## Product constraints

1. This is an Android-first casual optimization game.
2. Do not introduce Unity, Godot, libGDX, Flame, Blender, sprite packs or a custom native engine without an explicit architecture decision approved by the project owner.
3. Prefer Kotlin + Jetpack Compose + Compose Canvas/custom drawing.
4. Do not add bitmap/sprite dependencies for gameplay visuals. Gameplay must remain representable with shapes, lines, text, gradients, particles and procedural animation.
5. The game must remain one-finger friendly in portrait orientation.
6. Never add a mechanic merely to increase content volume. Every mechanic must create a new decision.
7. Every strong module must have a cost or downside: power, latency, value loss, space, heat, synchronization requirement, limited inventory, or another visible trade-off.
8. Avoid opaque randomness. If RNG affects a contract or module reward, it must be seeded and testable.
9. Never make progression depend primarily on stat inflation. Prefer new topology, new constraints and new interactions.
10. No forced interstitial ad after each contract/run. Monetization must be optional/rewarded or cosmetic until later validation.

## Engineering constraints

- Keep `simulation` independent of Android UI.
- Simulation runs on fixed ticks and accepts commands/events.
- Renderer reads immutable snapshots; renderer must not mutate simulation state.
- Use deterministic seeded RNG.
- All economy calculations use explicit units and pure functions where possible.
- Core simulation must be unit-testable on the JVM without emulator/device.
- Do not use wall-clock time inside deterministic simulation; inject clocks/tick counts.
- Save versioned game state. Migration is mandatory once public builds exist.
- Compose UI follows unidirectional data flow: events up, state down.
- Avoid allocation-heavy work in per-frame rendering loops.
- Profile before introducing native/C++ code.

## Definition of done for a gameplay feature

A feature is not done until:
- rules are documented;
- failure/edge cases are specified;
- deterministic tests exist;
- at least one balancing metric is emitted;
- UI feedback explains the result without a tutorial paragraph;
- no new soft-lock is possible;
- it works after process death/save restore where relevant.

## Product quality filter

Before merging a mechanic, answer:
1. Can a new player understand what happened visually?
2. Does this create a meaningful choice?
3. Can a skilled player exploit it better than a novice?
4. Can the player lose value because of an unseen rule?
5. Does it increase cognitive load more than strategic depth?

If #4 is yes or #5 is yes, redesign before shipping.
