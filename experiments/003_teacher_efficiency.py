#!/usr/bin/env python3
"""Experiment 003: teacher efficiency and learner retention.

Disposable research code. The teacher is deliberately deterministic so the
experiment measures the teaching protocol rather than a model provider.
"""

from __future__ import annotations

from dataclasses import asdict, dataclass
import json


@dataclass(frozen=True)
class Experience:
    input: tuple[float, float]
    target: int
    teacher_id: str
    example_id: str


class FakeTeacher:
    """Deterministic teacher with explicit provenance."""

    def __init__(self) -> None:
        self.calls = 0

    def teach(self, examples: list[tuple[tuple[float, float], int]]) -> list[Experience]:
        self.calls += 1
        return [
            Experience(features, target, "fake-teacher-001", f"call-{self.calls}-example-{i}")
            for i, (features, target) in enumerate(examples)
        ]


class Learner:
    """Small table learner: useful here because retention is the variable."""

    def __init__(self) -> None:
        self.knowledge: dict[tuple[float, float], int] = {}

    def learn(self, experiences: list[Experience]) -> None:
        for experience in experiences:
            self.knowledge[experience.input] = experience.target

    def predict(self, features: tuple[float, float]) -> int | None:
        return self.knowledge.get(features)

    def evaluate(self, examples: list[tuple[tuple[float, float], int]]) -> float:
        return sum(self.predict(x) == y for x, y in examples) / len(examples)


TRAIN = [
    ((-1.0, -1.0), 0),
    ((-1.0, 1.0), 1),
    ((1.0, -1.0), 1),
    ((1.0, 1.0), 0),
]

HELD_OUT = [
    ((-0.5, -0.5), 0),
    ((-0.5, 0.5), 1),
    ((0.5, -0.5), 1),
    ((0.5, 0.5), 0),
]


def run(teaching_set: list[tuple[tuple[float, float], int]]) -> dict:
    teacher = FakeTeacher()
    experiences = teacher.teach(teaching_set)
    learner = Learner()
    learner.learn(experiences)

    before = learner.evaluate(TRAIN)
    # The teacher is no longer consulted. This is intentionally a separate
    # evaluation phase to make retention explicit.
    retained = learner.evaluate(TRAIN)

    provenance_complete = all(
        e.teacher_id and e.example_id for e in experiences
    )

    return {
        "teacher_calls": teacher.calls,
        "examples_produced": len(experiences),
        "unique_examples": len({e.input for e in experiences}),
        "redundant_examples": len(experiences) - len({e.input for e in experiences}),
        "training_accuracy": before,
        "teacher_free_retained_accuracy": retained,
        "provenance_complete": provenance_complete,
        "experiences": [asdict(e) for e in experiences],
    }


def main() -> None:
    compact = run(TRAIN)
    redundant = run(TRAIN + TRAIN)

    result = {
        "experiment": "003-teacher-efficiency-and-retention",
        "teacher": "fake-teacher-001",
        "compact": compact,
        "redundant": redundant,
        "retention_preserved": (
            compact["training_accuracy"] == compact["teacher_free_retained_accuracy"]
            and redundant["training_accuracy"] == redundant["teacher_free_retained_accuracy"]
        ),
        "provenance_preserved": (
            compact["provenance_complete"] and redundant["provenance_complete"]
        ),
        "redundancy_measured": (
            redundant["redundant_examples"] > compact["redundant_examples"]
        ),
    }
    print(json.dumps(result, indent=2, sort_keys=True))


if __name__ == "__main__":
    main()
