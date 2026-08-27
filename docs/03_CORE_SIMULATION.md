# 03 — Core Simulation Specification

## Goal

The simulation is the product. It must be deterministic, explainable and independent from frame rate.

## Fixed tick

Use a fixed simulation tick, e.g. 20 or 30 Hz initially. Rendering may run at display refresh rate and interpolate visual positions.

Never tie production to rendered frames.

## Entity model

### Particle
Suggested state:
- `id`
- `cell`
- `progressInCell` [0..1)
- `direction`
- `speed`
- `value`
- `tags` / future channel info
- `spawnTick`

### Module
Suggested state:
- `id`
- `type`
- `cell`
- `rotation`
- `enabled`
- configuration if module supports it

### Machine
- grid dimensions
- module map
- power capacity
- current tick
- active particles
- receiver counters
- seeded RNG only where explicitly required

## Processing order per tick

1. Accept queued player commands at tick boundary.
2. Recalculate topology if layout changed.
3. Determine active modules and power validity.
4. Spawn particles according to source cadence.
5. Advance particles.
6. Resolve cell-boundary crossings in deterministic order.
7. Apply module transformations.
8. Resolve receiver deliveries.
9. Emit simulation events.
10. Build immutable snapshot/metrics.

Stable ordering must be specified for simultaneous events so replay produces identical results.

## Metrics

Maintain rolling windows:
- delivered particles/sec;
- delivered value/sec = OUTPUT;
- average particle value;
- active particle count;
- wasted particles/sec;
- power used;
- module utilization;
- bottleneck hints for analytics/debug.

## Determinism tests

Given:
- same machine state;
- same seed;
- same commands at same ticks;

Then after N ticks:
- particle states equal;
- delivered totals equal;
- output metrics equal.

## Safety caps

Prevent runaway simulations:
- maximum active particle count;
- bounded splitter recursion per tick;
- output uses `Long`/safe decimal strategy, then large-number abstraction when needed;
- detect loops that never reach a sink and mark them as recirculating rather than leaking memory.

## Offline simulation

Do not simulate every tick while app was closed.

Use a validated approximation model for stable machines:
- capture steady-state production rate after a warm-up period;
- offline gain = rate × eligible duration × offline factor;
- cap duration initially (e.g. several hours) for economy control.

If the machine is unstable/oscillating, use a bounded deterministic fast-forward or conservative estimate.
