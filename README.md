# neural-ai

Experimental research combining neural learners with AI models as teachers and knowledge sources.

The purpose of this repository is to study how an existing AI system can provide useful teaching signals to an independently implemented learner without making the learner a copy of the teacher.

## Research direction

The eventual flow is:

AI teacher -> teaching signal -> neural learner -> learned state -> evaluation

A teaching signal may be an example, label, correction, explanation, generated challenge, or other evidence that helps a learner improve.

## Relationship to neural

- neural owns learning mechanisms, learner state, and evaluation.
- neural-ai owns experiments involving AI teachers and knowledge acquisition.

neural should not depend on neural-ai. neural-ai may eventually depend on the language-neutral concepts established by neural.

## Relationship to Loom

Loom and loom-ai are execution infrastructure, not the research subject. Early experiments may use disposable model adapters or direct API calls. Once the experiments reveal a stable need for execution contracts, those interactions can move toward Loom and loom-ai.

## Current experiment

Experiment 001 defines the smallest useful teacher signal: deterministic examples and feedback that can be consumed by a learner without requiring an AI provider.

This lets us validate the teaching side before spending effort on model-provider integration.

## Research discipline

Each experiment should state:

1. hypothesis
2. teacher signal
3. learner interaction
4. evaluation method
5. observations
6. what changes about the next experiment

AI-generated output is evidence for an experiment, not automatically ground truth. Teacher quality and learner behavior must be measured separately.
