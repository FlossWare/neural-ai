import java.util.List;

public final class TeacherSignal {
    record Signal(double[] features, boolean label, String note) {
        Signal {
            features = features.clone();
        }
        @Override public double[] features() { return features.clone(); }
    }

    interface Teacher {
        List<Signal> teach();
    }

    static final class DeterministicTeacher implements Teacher {
        @Override public List<Signal> teach() {
            return List.of(
                    new Signal(new double[]{0, 0}, false, "both features absent"),
                    new Signal(new double[]{1, 0}, true, "first feature present"),
                    new Signal(new double[]{0, 1}, true, "second feature present"));
        }
    }

    public static void main(String[] args) {
        var signals = new DeterministicTeacher().teach();

        check(signals.size() == 3, "teacher emits three signals");
        check(!signals.get(0).label(), "zero example is negative");
        check(signals.get(1).label(), "first feature example is positive");
        check(signals.get(2).label(), "second feature example is positive");

        System.out.println("signals=" + signals.size());
        System.out.println("signal-format=features,label,note");
        System.out.println("Experiment 001 PASSED");
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
    }
}
