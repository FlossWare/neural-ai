# Neural AI

Experimental research combining neural learners with AI models as teachers and knowledge sources.

## Core hypothesis

An existing AI model can act as a persistent knowledge source or teacher from which an independent neural learner acquires useful representations, without copying the teacher's weights.

The initial flow is:

```
AI model / teacher
       |
       v
 knowledge / examples / feedback
       |
       v
 neural learner
       |
       v
 learned state
       |
       v
 evaluation
```

## Initial research

The first experiments will deliberately favor evidence over infrastructure.

A disposable model adapter may be used before Loom is sufficiently mature for this workload. Once the teacher boundary stabilizes, it should migrate toward the appropriate Loom/loom-ai contracts.

## Relationship to other FlossWare projects

- `neural`: neural learning foundations and experiments.
- `neural-ai`: AI teachers, knowledge acquisition, and collective learning experiments.
- `loom`: execution substrate.
- `loom-ai`: AI execution contracts and model-provider semantics.

## Status

Research / experimental.


## First experiment

The initial implementation establishes a narrow teacher boundary:

- `Teacher` accepts a task and question and returns an explicit answer plus confidence.
- `FakeTeacher` makes acquisition deterministic and testable.
- `TeacherDataset` turns teacher interaction into repeatable acquired knowledge.

No model SDK is required yet. The eventual model-backed implementation will sit behind this boundary and can later be replaced by the Loom/loom-ai model-provider contracts without changing the research model.
