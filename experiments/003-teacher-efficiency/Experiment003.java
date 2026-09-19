import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Experiment003 {
    private static final int MAX_PASSES = 100;
    private static final double LEARNING_RATE = 1.0;

    record Experience(String id, double[] features, boolean label, String provenance) {
        Experience {
            if (id == null || id.isBlank() || provenance == null || provenance.isBlank()) {
                throw new IllegalArgumentException("experience id and provenance are required");
            }
            features = features.clone();
        }

        @Override
        public double[] features() {
            return features.clone();
        }
    }

    interface Teacher {
        List<Experience> teach();
    }

    static final class CompactTeacher implements Teacher {
        private int calls;

        @Override
        public List<Experience> teach() {
            calls++;
            return List.of(
                    experience("compact-001", 0, 0, false),
                    experience("compact-002", 1, 0, true),
                    experience("compact-003", 0, 1, true));
        }

        int calls() {
            return calls;
        }
    }

    static final class RedundantTeacher implements Teacher {
        private int calls;

        @Override
        public List<Experience> teach() {
            calls++;
            var experiences = new java.util.ArrayList<Experience>();
            for (int repetition = 1; repetition <= 3; repetition++) {
                experiences.add(experience("redundant-%d-001".formatted(repetition), 0, 0, false));
                experiences.add(experience("redundant-%d-002".formatted(repetition), 1, 0, true));
                experiences.add(experience("redundant-%d-003".formatted(repetition), 0, 1, true));
            }
            return List.copyOf(experiences);
        }

        int calls() {
            return calls;
        }
    }

    static final class Learner {
        private final double learningRate;
        private final double[] weights;
        private double bias;

        Learner(int featureCount, double learningRate) {
            if (featureCount < 1 || !(learningRate > 0.0)) {
                throw new IllegalArgumentException("featureCount and learningRate must be positive");
            }
            this.weights = new double[featureCount];
            this.learningRate = learningRate;
        }

        boolean predict(double... features) {
            if (features.length != weights.length) {
                throw new IllegalArgumentException("feature count changed");
            }
            double activation = bias;
            for (int i = 0; i < weights.length; i++) {
                activation += weights[i] * features[i];
            }
            return activation >= 0.0;
        }

        int train(List<Experience> experiences) {
            int updates = 0;
            for (Experience experience : experiences) {
                if (predict(experience.features()) != experience.label()) {
                    double error = experience.label() ? 1.0 : -1.0;
                    double[] features = experience.features();
                    for (int i = 0; i < weights.length; i++) {
                        weights[i] += learningRate * error * features[i];
                    }
                    bias += learningRate * error;
                    updates++;
                }
            }
            return updates;
        }

        State state() {
            return new State(weights.clone(), bias, learningRate);
        }
    }

    record State(double[] weights, double bias, double learningRate) {
        State {
            weights = weights.clone();
        }

        @Override
        public double[] weights() {
            return weights.clone();
        }
    }

    record Result(
            int teacherCalls,
            int examples,
            int uniqueExamples,
            int redundantExamples,
            long trainingNanos,
            int passes,
            int updates,
            int trainingCorrect,
            int heldOutCorrect,
            boolean provenanceComplete,
            State finalState) {
    }

    public static void main(String[] args) {
        var compact = run("compact", new CompactTeacher());
        var redundant = run("redundant", new RedundantTeacher());

        check(compact.examples() == 3, "compact teaching size");
        check(redundant.examples() == 9, "redundant teaching size");
        check(compact.uniqueExamples() == 3, "compact unique examples");
        check(redundant.uniqueExamples() == 3, "redundant unique examples");
        check(compact.redundantExamples() == 0, "compact redundancy");
        check(redundant.redundantExamples() == 6, "redundant redundancy");
        check(compact.teacherCalls() == 1 && redundant.teacherCalls() == 1, "teacher calls");
        check(compact.trainingCorrect() == compact.examples(), "compact training accuracy");
        check(redundant.trainingCorrect() == redundant.examples(), "redundant training accuracy");
        check(compact.heldOutCorrect() == 1, "compact teacher-free retention");
        check(redundant.heldOutCorrect() == 1, "redundant teacher-free retention");
        check(compact.provenanceComplete() && redundant.provenanceComplete(), "provenance completeness");

        System.out.printf(
                "scenario=compact teacher-calls=%d examples=%d unique=%d redundant=%d training-nanos=%d passes=%d updates=%d training-accuracy=%d/%d teacher-free-held-out=%d/1 provenance-complete=%s%n",
                compact.teacherCalls(), compact.examples(), compact.uniqueExamples(), compact.redundantExamples(),
                compact.trainingNanos(), compact.passes(), compact.updates(),
                compact.trainingCorrect(), compact.examples(), compact.heldOutCorrect(), compact.provenanceComplete());

        System.out.printf(
                "scenario=redundant teacher-calls=%d examples=%d unique=%d redundant=%d training-nanos=%d passes=%d updates=%d training-accuracy=%d/%d teacher-free-held-out=%d/1 provenance-complete=%s%n",
                redundant.teacherCalls(), redundant.examples(), redundant.uniqueExamples(), redundant.redundantExamples(),
                redundant.trainingNanos(), redundant.passes(), redundant.updates(),
                redundant.trainingCorrect(), redundant.examples(), redundant.heldOutCorrect(), redundant.provenanceComplete());

        System.out.printf("example-efficiency=compact:%d redundant:%d%n",
                compact.examples(), redundant.examples());
        System.out.println("Experiment 003 PASSED");
    }

    private static Result run(String scenario, Teacher teacher) {
        List<Experience> experiences = teacher.teach();
        int teacherCalls = calls(teacher);
        check(teacherCalls == 1, scenario + " teacher call count");

        var learner = new Learner(2, LEARNING_RATE);
        long start = System.nanoTime();
        int updates = 0;
        int passes = 0;

        while (passes < MAX_PASSES && countCorrect(learner, experiences) < experiences.size()) {
            updates += learner.train(experiences);
            passes++;
        }
        long trainingNanos = System.nanoTime() - start;

        int trainingCorrect = countCorrect(learner, experiences);
        int uniqueExamples = uniqueExamples(experiences);
        int redundantExamples = experiences.size() - uniqueExamples;
        boolean provenanceComplete = provenanceComplete(experiences);
        var finalState = learner.state();

        // The Teacher reference is deliberately not returned. Evaluation happens
        // through the learned state only, after teacher access has ended.
        teacher = null;

        var heldOut = new Experience(scenario + "-held-out", new double[]{1, 1}, true, "evaluation");
        int heldOutCorrect = learner.predict(heldOut.features()) == heldOut.label() ? 1 : 0;

        check(trainingCorrect == experiences.size(), scenario + " convergence");
        check(passes > 0 && passes <= MAX_PASSES, scenario + " convergence bound");

        return new Result(
                teacherCalls,
                experiences.size(),
                uniqueExamples,
                redundantExamples,
                trainingNanos,
                passes,
                updates,
                trainingCorrect,
                heldOutCorrect,
                provenanceComplete,
                finalState);
    }

    private static int calls(Teacher teacher) {
        if (teacher instanceof CompactTeacher compact) {
            return compact.calls();
        }
        if (teacher instanceof RedundantTeacher redundant) {
            return redundant.calls();
        }
        throw new IllegalArgumentException("unknown teacher");
    }

    private static Experience experience(String id, double x, double y, boolean label) {
        return new Experience(id, new double[]{x, y}, label, "deterministic-teacher");
    }

    private static int countCorrect(Learner learner, List<Experience> experiences) {
        int correct = 0;
        for (Experience experience : experiences) {
            if (learner.predict(experience.features()) == experience.label()) {
                correct++;
            }
        }
        return correct;
    }

    private static int uniqueExamples(List<Experience> experiences) {
        Set<String> unique = new HashSet<>();
        for (Experience experience : experiences) {
            unique.add(key(experience));
        }
        return unique.size();
    }

    private static String key(Experience experience) {
        return java.util.Arrays.toString(experience.features()) + ":" + experience.label();
    }

    private static boolean provenanceComplete(List<Experience> experiences) {
        return experiences.stream().allMatch(
                experience -> !experience.id().isBlank() && !experience.provenance().isBlank());
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }
}
