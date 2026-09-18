package org.flossware.neural.ai;

import java.util.Objects;

public record TeachingResponse(String answer, double confidence) {
    public TeachingResponse {
        Objects.requireNonNull(answer, "answer");
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("confidence must be between 0 and 1");
        }
    }
}
