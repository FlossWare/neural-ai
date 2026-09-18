import java.util.List;

public final class TeacherToLearner {
    private static final int MAX_PASSES = 100;

    record Signal(double[] features, boolean label, String note) {
        Signal {
            features = features.clone();
        }

        @Override
        public double[] features() {
            return features.clone();
        }
    }

    interface Teacher {
        List<Signal> teach();
    }

    static final class DeterministicTeacher implements Teacher {
        @Override
        public List<Signal> teach() {
            return List.of(
                    new Signal(new double[]{0, 0}, false, "both features absent"),
                    new Signal(new double[]{1, 0}, true, "first feature present"),
                    new Signal(new double[]{0, 1}, true, "second feature present"));
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

        Learner(State state) {
            this.weights = state.weights();
            this.bias = state.bias();
            this.learningRate = state.learningRate();
        }

        boolean predict(double... features) {
            return activation(features) >= 0.0;
        }

        int train(List<Signal> signals) {
            int updates = 0;
            for (Signal signal : signals) {
                boolean actual = predict(signal.features());
                if (actual != signal.label()) {
                    double error = signal.label() ? 1.0 : -1.0;
                    double[] x = signal.features();
                    for (int i = 0; i < weights.length; i++) {
                        weights[i] += learningRate * error * x[i];
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

        private double activation(double... features) {
            if (features.length != weights.length) {
                throw new IllegalArgumentException("feature count changed");
            }
            double sum = bias;
            for (int i = 0; i < weights.length; i++) {
                sum += weights[i] * features[i];
            }
            return sum;
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

    public static void main(String[] args) {
        var teacher = new DeterministicTeacher();
        var signals = teacher.teach();
        var learner = new Learner(2, 1.0);
        var initialState = learner.state();

        int updates = 0;
        int passes = 0;
        while (passes < MAX_PASSES && countCorrect(learner, signals) < signals.size()) {
            updates += learner.train(signals);
            passes++;
        }

        var finalState = learner.state();
        var heldOut = new Signal(new double[]{1, 1}, true, "held out");
        var trainingCorrect = countCorrect(learner, signals);
        var heldOutCorrect = learner.predict(heldOut.features()) == heldOut.label() ? 1 : 0;

        check(trainingCorrect == signals.size(), "training accuracy");
        check(heldOutCorrect == 1, "held-out accuracy");
        check(passes > 0, "training passes");
        check(passes <= MAX_PASSES, "convergence limit");

        var restored = new Learner(finalState);
        check(restored.predict(heldOut.features()) == learner.predict(heldOut.features()),
                "restored held-out prediction");

        System.out.printf(
                "signals=%d%ninitial-state=%s%nupdates=%d%npasses=%d%ntraining-accuracy=%d/%d%nheld-out-accuracy=%d/1%nfinal-state=%s%n",
                signals.size(),
                initialState,
                updates,
                passes,
                trainingCorrect, signals.size(),
                heldOutCorrect,
                finalState);
        System.out.println("Experiment 002 PASSED");
    }

    private static int countCorrect(Learner learner, List<Signal> signals) {
        int correct = 0;
        for (Signal signal : signals) {
            if (learner.predict(signal.features()) == signal.label()) {
                correct++;
            }
        }
        return correct;
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }
}
