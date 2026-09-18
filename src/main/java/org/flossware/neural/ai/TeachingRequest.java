package org.flossware.neural.ai;

import java.util.Objects;

public record TeachingRequest(String task, String question) {
    public TeachingRequest {
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(question, "question");
    }
}
