# Experiment 002: Teacher signal to learner

## Hypothesis

A deterministic teaching signal with features and a label can be consumed by an independent learner to acquire the same bounded behavior demonstrated by the learner experiment in `neural`.

This experiment tests the missing link between the two Experiment 001 baselines:

`teacher -> signal -> learner -> evaluation`

It does not test an AI model, a provider API, a durable cross-repository contract, or Loom integration.

## Experimental design

The deterministic teacher emits the same three labeled examples used by Experiment 001:

- `[0,0] -> false`
- `[1,0] -> true`
- `[0,1] -> true`

The learner consumes the signals without seeing the teacher's notes. It owns its own weights, bias, and learning state.

The learner trains for deterministic passes until all teaching examples are classified correctly or a bounded maximum is reached.

A separate held-out example `[1,1] -> true` is then evaluated.

The learner state is captured and restored into a fresh learner, which must produce the same held-out prediction.

## What this proves

If the experiment passes, the result is evidence that the minimal signal representation established by Experiment 001 can carry enough information for an independent learner to acquire this bounded rule.

It does not prove that the representation is generally sufficient for arbitrary learning tasks.

## What remains deliberately deferred

- real AI teachers
- model-provider APIs or SDKs
- importing `neural` as a build dependency
- a shared production signal library
- persistent storage
- Loom / loom-ai
- generalized learner abstractions

The local learner is disposable experimental code. If later experiments demonstrate a stable cross-repository concept, extract that concept then rather than designing it in advance.

## Run

```
javac TeacherToLearner.java && java TeacherToLearner
```

Expected output includes `Experiment 002 PASSED`.
