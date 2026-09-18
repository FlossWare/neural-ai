package org.flossware.neural.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TeacherDataset {
    private final Teacher teacher;

    public TeacherDataset(Teacher teacher) {
        this.teacher = Objects.requireNonNull(teacher, "teacher");
    }

    public List<TeachingResponse> acquire(String task, List<String> questions) {
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(questions, "questions");
        var responses = new ArrayList<TeachingResponse>(questions.size());
        for (var question : questions) {
            responses.add(teacher.teach(new TeachingRequest(task, question)));
        }
        return List.copyOf(responses);
    }
}
