# 08 — Android Technical Specification

## Platform

Android-first native application.

## Proposed stack

- Kotlin.
- Jetpack Compose for UI.
- Compose Canvas / drawing APIs for gameplay rendering.
- Coroutines + StateFlow for orchestration/state exposure.
- ViewModel for screen-level state holder.
- DataStore for lightweight preferences/settings.
- Room only when structured persistent content/history justifies it.
- JUnit for pure simulation tests.
- Android instrumented/UI tests for input/render integration.

## Why Compose Canvas

Gameplay visuals are lines, cells, modules, particles, trails, glow and text. Compose provides custom drawing/Canvas primitives directly, which matches the project's no-asset requirement and keeps the stack native.

## Orientation

Portrait-first. Landscape/tablet may be adaptive later but must not dictate MVP.

## Performance targets

Initial targets:
- smooth 60 fps on mid/low-range modern Android devices;
- simulation independent from display frame rate;
- no per-frame unbounded allocations;
- cap active visual particles independently from economic throughput if necessary;
- gracefully reduce trails/glow on slower devices while preserving simulation accuracy.

## Rendering model

Simulation may contain 10,000 logical units/sec later, but renderer does not need to draw 10,000 individual particles. Introduce visual aggregation only when required:
- logical particle/packet simulation remains authoritative;
- renderer samples or batches trails;
- visual density communicates throughput without altering economy.

## Input

MVP:
- tap inventory module;
- tap cell to place;
- tap module to select;
- rotate button/tap gesture;
- remove/move;
- undo.

Avoid precision drag requirements on small screens until tested.

## Persistence

Versioned save schema with:
- current machine;
- unlocked modules;
- contract state;
- progression;
- blueprints;
- settings;
- economy timestamp for offline progress.

Autosave after meaningful changes, debounced to avoid excessive writes.

## Audio/haptics

Programmatic/generated or small legally owned audio resources later. Haptics:
- place;
- connect/activate;
- contract complete;
- overload/invalid action.

Audio/haptics must be independently toggleable.

## Accessibility

- Do not encode module identity only by color.
- Scalable HUD text.
- Reduced motion setting.
- Haptic/audio alternatives.
- High-contrast mode later if needed.
