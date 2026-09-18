# Experiment 001: Teacher signals

## Hypothesis

A teaching system can be represented as a stream of bounded signals that a learner can consume without knowing which model or provider produced them.

## Initial signal

Start with a deterministic example and optional correction:

- input features
- expected label
- optional explanation or rationale
- optional feedback after a learner prediction

The first implementation does not need a network client or model SDK.

## Teaching cycle

1. teacher presents an example
2. learner predicts
3. teacher supplies expected result or correction
4. learner updates its state
5. learner is evaluated separately on held-out examples

The learner must remain responsible for its own state and learning rule.

## Important separation

The teacher produces information. It does not own the learner's internal state.

A future AI model may generate the teaching signal, but the learner should consume the signal through a stable, language-neutral concept rather than through an AI-provider-specific API.

## Measurements

Track at least:

- learner performance before teaching
- learner performance after teaching
- number and type of teaching signals
- teacher disagreement or correction rate when applicable
- held-out performance
- retained performance after persistence and reload

## Not part of Experiment 001

- a specific model provider
- Loom integration
- embeddings
- prompt orchestration framework
- copying teacher weights
- distributed training

## Next experiment candidates

- replace deterministic examples with AI-generated examples
- compare direct labels with explanations
- introduce teacher corrections after incorrect predictions
- measure noisy or contradictory teacher signals
- connect a real AI teacher through a disposable adapter
- eventually route that adapter through loom-ai
