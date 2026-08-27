# 07 — Meta-Machine Endgame

## Core concept

A successful lower-level machine can be **compressed into a block** whose behavior is derived from its measured, validated performance.

Example:

A detailed factory producing a stable `125K output/s` becomes a component `[A]` in a higher-level board.

The player can then combine `[A]`, `[B]`, `[C]` with higher-order modules.

## Why this matters

This creates potentially enormous longevity without requiring thousands of handcrafted levels or graphical assets.

The player retains ownership of old work: old machines are not discarded; they become infrastructure.

## Compression rules

A compressed machine should store:
- blueprint hash/version;
- stable input/output profile;
- latency;
- throughput limits;
- power/maintenance cost at parent tier;
- optional special properties.

Never simply store “output = number” if the parent system can exploit timing/flow distinctions; preserve enough behavior to keep engineering meaningful.

## Tiers

Names are placeholders:
1. Machine
2. Factory
3. Plant
4. Complex
5. Network
6. Grid

Do not build these before the base game has proven retention.

## Sharing

Blueprints and compressed machines can later use compact deterministic codes:
- version;
- seed/config;
- module placements;
- upgrade state;
- checksum.

This supports daily challenges, community optimization and replay without large asset uploads.
