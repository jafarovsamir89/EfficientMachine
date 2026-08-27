# 10 — Balance and Economy

## Central balancing equation

The game must never allow one stat to dominate permanently.

Conceptually:

`OUTPUT = f(flow, value, delivery_success, timing)`

subject to:

`powerUsed <= powerCapacity`

and physical/grid constraints.

## Module trade-off budget

Every module should be balanced across:
- power;
- latency;
- footprint;
- throughput cap;
- value transformation;
- inventory/copy limit if needed.

## Example starting values (NOT final)

These are placeholders for prototype tuning:
- SOURCE: 1 particle / 0.67 sec.
- particle value: 1.
- TURN: +small path latency, 0–1 power.
- BOOST: ×1.5 speed, 10 power.
- ×2: ×2 value, +processing delay, 15 power.
- SPLITTER: 2 branches, value conserved across children, 8 power.
- starting power capacity: ~30–40.

The point is to force a meaningful choice between BOOST and ×2 rather than allowing both everywhere.

## Large numbers

Eventually use a big-number representation supporting:
- compact suffix display (`K`, `M`, `B`, `T`, scientific later);
- deterministic comparison;
- no floating overflow;
- stable save serialization.

Do not introduce large-number complexity before needed.

## Economy currencies

MVP should have at most:
- progression currency / research points.

Later:
- prestige Cores.

Premium currency is optional and should not exist until monetization design is validated.

## Anti-inflation

Do not let offline income make active optimization irrelevant.

Offline progression should accelerate return, not replace gameplay.
