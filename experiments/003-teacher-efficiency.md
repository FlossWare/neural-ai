# Experiment 003: Teacher efficiency and learner retention

## Hypothesis

A bounded learner can acquire the same target behavior from a compact, higher-signal teaching set with fewer examples than from a redundant teaching set, while retaining that behavior after teacher access is removed.

The experiment measures teaching efficiency rather than model quality.

## Experimental design

Both scenarios teach the same bounded OR-like rule:

- `[0,0] -> false`
- `[1,0] -> true`
- `[0,1] -> true`
- held out: `[1,1] -> true`

Two deterministic teachers produce the same underlying information:

1. **Compact**: one copy of each teaching example.
2. **Redundant**: three copies of each teaching example, with distinct experience IDs and provenance.

The learner consumes only features and labels. Provenance is retained on every experience but is deliberately not part of the learning input.

Training stops when all teaching examples are classified correctly or a bounded maximum is reached. The teacher is not returned from the training operation. Evaluation therefore occurs against a learner that has retained its state without teacher access.

## Measurements

For each scenario record:

- teacher calls
- examples produced
- unique examples
- redundant examples
- training time
- training passes
- training accuracy
- teacher-free held-out accuracy
- provenance completeness

## Acceptance criteria

- deterministic experiment
- both teaching sets produce a trained learner
- compact and redundant teaching-set sizes are quantitatively different
- redundant examples are identified explicitly
- provenance is complete for every experience
- held-out evaluation occurs without teacher access
- both learners retain the held-out behavior
- results are printed as evidence rather than reduced to a single pass/fail claim

## Deliberately deferred

- real AI teachers
- model-provider APIs
- Loom / loom-ai
- cross-repository dependencies
- generalized experience libraries
- persistent storage
- randomized teacher behavior
- production learner abstractions

The experiment remains disposable until the evidence demonstrates a stable concept worth extracting.
