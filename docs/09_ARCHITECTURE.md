# 09 — Architecture

## Principle

The renderer is replaceable. The simulation is not.

## Suggested modules/packages

### `simulation`
Pure Kotlin/JVM where possible.
- tick engine
- particles
- modules
- topology
- metrics
- contracts validation
- deterministic RNG

### `domain`
- progression
- economy
- blueprint model
- prestige rules

### `data`
- save repository
- DataStore/Room adapters
- migrations

### `ui`
- Compose screens
- HUD
- module tray
- dialogs

### `render`
- machine Canvas
- visual particle system
- trails/effects
- hit testing / coordinate transforms

### `app`
- navigation
- DI/bootstrap
- lifecycle

## State flow

`User event → ViewModel/Controller → simulation/domain command → new snapshot → StateFlow → Compose UI/render`

UI must not directly modify modules/particles.

## Simulation snapshots

Keep snapshots compact. Rendering can read:
- module positions/states;
- sampled visual particles;
- metrics;
- effects/events.

Do not expose mutable engine collections to UI.

## Time

- Simulation: fixed ticks.
- Rendering: frame time.
- Offline economy: injected wall clock.

These are separate concepts.

## Replay/debug

Record optional command streams:
`tick + command + parameters`.

This allows:
- deterministic bug reproduction;
- daily challenge verification;
- future replay/share features;
- regression tests for balance.
