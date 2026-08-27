# 12 — Analytics and Experiments

## North-star behavior

The strongest early signal is not installs or ad impressions. It is:

> Does a player voluntarily rebuild an already-working machine to make it more efficient?

## Core events

- `tutorial_started`
- `module_placed(type)`
- `module_rotated(type)`
- `module_removed(type)`
- `machine_reset`
- `output_improved(old,new,deltaPct)`
- `contract_started(id,family,target)`
- `contract_completed(id,attempts,time,peakOutput)`
- `module_unlocked(type)`
- `blueprint_saved`
- `session_end(duration,edits,contracts,peakOutput)`

## Early KPIs

Before monetization:
- tutorial completion;
- time to first meaningful output improvement;
- number of voluntary edits after first contract is already technically solvable;
- contracts completed per session;
- session length distribution;
- D1 / D7 retention;
- percentage returning to improve personal best;
- fraction of players using multiple different layouts.

## Red flags

- players place modules once and then only wait;
- one layout/module combination dominates;
- output growth is driven more by upgrades than design;
- players cannot explain why output changed;
- contract completion feels random;
- tutorial requires long text.

## High-value experiments

1. Splitter conserves value vs small branching bonus.
2. Power capacity low vs medium.
3. Free movement vs edit-cost challenge mode.
4. Contract target visibility as percentage/progress bar vs raw value.
5. Unlock SPLITTER at 5, 10 or 20 minutes.
6. Offline reward cap duration.
