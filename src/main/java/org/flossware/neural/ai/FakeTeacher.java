package org.flossware.neural.ai;

import java.util.Map;
import java.util.Objects;

public final class FakeTeacher implements Teacher {
    private final Map<String, TeachingResponse> answers;

    public FakeTeacher(Map<String, TeachingResponse> answers) {
        this.answers = Map.copyOf(Objects.requireNonNull(answers, "answers"));
    }

    @Override
    public TeachingResponse teach(TeachingRequest request) {
        return answers.getOrDefault(request.question(),
                new TeachingResponse("UNKNOWN", 0.0));
    }
}
