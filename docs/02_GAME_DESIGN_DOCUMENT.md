# 02 — Game Design Document

## Core screen

Portrait grid with:
- SOURCE on one side;
- RECEIVER on another side;
- modules on cells;
- animated particles flowing through cells;
- compact HUD: FLOW, VALUE, POWER, OUTPUT;
- current CONTRACT target;
- bottom module tray.

## Base loop

`observe → place/rotate → watch → measure → identify bottleneck → rebuild → improve output → complete contract → unlock/earn → next contract`

The player is never required to tap particles. The machine operates continuously.

## Fundamental quantities

### FLOW
Particles delivered per second.

### VALUE
Average economic value per delivered particle.

### OUTPUT
Main production score.

Recommended base definition:

`OUTPUT = delivered_value_sum / measurement_window_seconds`

This avoids fake arithmetic: if ×2 doubles particle value, the actual delivered value rises.

### POWER
Active module consumption. Machine cannot exceed `powerCapacity` unless an explicit overclock mechanic is active.

## Placement model

- Grid is discrete.
- Module occupies one cell unless later specified.
- Tap inventory module → tap cell to place.
- Tap placed module to select.
- Tap rotate control or gesture to rotate.
- Drag reposition may be introduced only if it remains reliable on small screens.
- Moving modules is free during early game; later challenge contracts may limit edits.

## Base modules

MVP modules:
- SOURCE
- RECEIVER
- TURN
- BOOST
- ×2 MULTIPLIER
- SPLITTER

Next modules:
- MERGE
- BUFFER

Advanced candidates only after core validation:
- FILTER
- GATE
- TELEPORT
- CONVERTER
- CLOCK/SYNC
- OVERCLOCK
- ROUTER

## Why optimization remains interesting

Every module manipulates at least one of:
- path length;
- throughput;
- particle value;
- speed;
- timing;
- branching;
- energy use;
- space.

No module may dominate all dimensions.

## Session structure

### 20–60 seconds
Player tweaks one module and watches the result.

### 2–5 minutes
Player completes or nearly completes a contract.

### 10–20 minutes
Player redesigns a machine after unlocking a new interaction.

### Days/weeks
Player unlocks module families, blueprints, larger grids, new source/receiver configurations and prestige cores.

## Failure

Normal play does not need traditional “Game Over”. Failure is:
- contract target not met within optional constraints;
- power exceeded;
- design becomes inefficient;
- challenge timer expires for timed modes.

The default sandbox always permits rebuilding.

## Teaching

No text wall. First sequence:
1. SOURCE already aims at RECEIVER.
2. Player sees base output.
3. Game highlights one empty cell and gives ×2.
4. Place it; output visibly doubles.
5. Give BOOST with power cost.
6. Give TURN and offset target.
7. Give SPLITTER and introduce branch question.

Each new module is taught by a tiny live experiment.
