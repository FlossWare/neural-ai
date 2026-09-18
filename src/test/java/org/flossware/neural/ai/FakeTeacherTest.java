package org.flossware.neural.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FakeTeacherTest {
    @Test
    void acquiresRepeatableKnowledge() {
        var teacher = new FakeTeacher(Map.of(
                "2 + 2", new TeachingResponse("4", 1.0),
                "3 + 3", new TeachingResponse("6", 1.0)));
        var dataset = new TeacherDataset(teacher);

        assertEquals(
                List.of(new TeachingResponse("4", 1.0), new TeachingResponse("6", 1.0)),
                dataset.acquire("arithmetic", List.of("2 + 2", "3 + 3")));
    }

    @Test
    void unknownKnowledgeIsExplicit() {
        var teacher = new FakeTeacher(Map.of());
        var response = teacher.teach(new TeachingRequest("arithmetic", "9 + 9"));
        assertEquals("UNKNOWN", response.answer());
        assertEquals(0.0, response.confidence());
    }
}
