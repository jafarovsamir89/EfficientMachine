# 04 — Module System

The module system is the main content generator. New modules must create new topology or optimization questions.

## MVP modules

| Module | Effect | Cost / downside | Skill question |
|---|---|---|---|
| SOURCE | Emits particles | fixed cadence | route efficiently |
| RECEIVER | Converts delivery to output | fixed target | maximize arrivals/value |
| TURN | changes direction 90° | path length / small latency | spend space for routing |
| BOOST | increases speed | meaningful power cost | throughput vs power |
| ×2 | doubles particle value | adds processing latency + power | value vs flow |
| SPLITTER | duplicates/branches stream | output value split or throughput/power penalty | when branching is beneficial |

## Recommended splitter rule

Avoid a free “clone value from nothing”. A fair starting rule:
- one input particle creates two outputs;
- total value is conserved before later bonuses: each child gets ~50% input value;
- splitter itself consumes power;
- branching can still be valuable because routes can be processed differently and later merged.

Alternative versions may be A/B tested, but do not ship infinite-value duplication accidentally.

## MERGE

Two or more inputs combine.

Possible rule:
- waits briefly for a partner particle;
- combined value is sum of inputs;
- throughput limited;
- synchronization becomes a design challenge.

## BUFFER

Stores a small queue and releases particles on a cadence.

Trade-off:
- consumes space/power;
- adds delay;
- enables synchronization and burst contracts.

## FILTER

Routes particles based on a visible property/tag.

Do not introduce until particles have meaningful categories.

## GATE

Opens/closes on a simple clock or signal.

This begins the “logic machine” layer and should appear significantly later than the base game.

## TELEPORT

Links two distant cells.

Powerful because it destroys path-length constraints. Therefore it must be expensive, limited in count, or add latency.

## Module unlock philosophy

A new module is good when it makes the player reopen an old blueprint and say:

> “Wait, now I can build this completely differently.”

A module is weak if it only says:

> “Same machine, but +15%.”
