# Experiment 001: Teacher signals

## Executable experiment

The experiment uses a deterministic teacher that emits labeled examples. No model API, SDK, provider, or build framework is required.

The purpose is to establish the teaching signal independently from the learner implementation. A future AI teacher can produce the same conceptual signal.

Run the experiment from its directory with a JDK:

```
javac TeacherSignal.java && java TeacherSignal
```

Expected output includes `Experiment 001 PASSED`.

## Signal

The first signal contains:

- feature values
- expected binary label
- optional teacher note

The learner remains responsible for interpreting the signal and changing its own state.
