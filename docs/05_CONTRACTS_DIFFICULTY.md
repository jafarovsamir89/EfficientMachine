# 05 — Contracts and Difficulty

## Why contracts exist

Pure sandbox optimization can become directionless. Contracts give a short-term purpose while preserving multiple valid solutions.

## Contract grammar

A contract is generated from:
- target metric;
- threshold;
- optional constraints;
- available module set;
- grid/source/receiver layout;
- reward.

## Contract families

### Output
`Reach OUTPUT >= X`

### Flow
`Deliver FLOW >= X particles/sec`

### Value
`Average delivered VALUE >= X`

### Efficiency
`Reach OUTPUT >= X with POWER <= Y`

### Compact
`Reach OUTPUT >= X using <= N modules`

### Restricted
`Reach target without BOOST` or with limited copies of a module.

### Dual receiver
Feed two targets with independent minimums.

### Burst
Deliver a specified value inside a short measurement window.

## Difficulty

Difficulty should come from tighter trade-offs, not from hiding rules.

A generated contract is valid only if the solver/validation harness can demonstrate at least one solution within the current player's unlocked module set and constraints.

We do **not** need a perfect optimal solver initially. We need:
- feasibility checks for handcrafted/MVP contracts;
- simulation-based search for generated contracts later;
- telemetry showing how many attempts players need.

## Near-goal psychology

Targets should often land where reasonable first layouts reach 70–95% of the goal. This invites optimization without making the answer trivial.

Do not fake values. The machine simulation remains authoritative.

## Rewards

Early rewards:
- module unlock;
- power capacity;
- grid feature;
- blueprint slot;
- core currency later.

Avoid early shower of currencies. One main progression currency is enough for MVP.
