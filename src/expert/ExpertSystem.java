package expert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** A deterministic rule engine: all mandatory rules must match for a position. */
public final class ExpertSystem {
    private ExpertSystem() {}

    public record Applicant(boolean pythonCourse, boolean softwareCourse, boolean agileCourse,
            boolean git, boolean bachelorsCS, boolean mastersCS, boolean pmi,
            BigDecimal pythonYears, BigDecimal dataYears, BigDecimal agileYears,
            BigDecimal managementYears, BigDecimal expertYears, BigDecimal architectureYears) {
        public Applicant {
            for (BigDecimal value : new BigDecimal[]{pythonYears, dataYears, agileYears,
                    managementYears, expertYears, architectureYears}) {
                if (value == null || value.signum() < 0) {
                    throw new IllegalArgumentException("Experience must be a nonnegative number.");
                }
            }
        }
    }

    public record Rule(String explanation, Predicate<Applicant> test) {}
    public record Position(String name, List<Rule> required, List<Rule> desired) {}
    public record Result(String position, List<String> met, List<String> missing,
            List<String> desiredMet, List<String> desiredMissing) {
        public boolean qualified() { return missing.isEmpty(); }
    }

    private static boolean atLeast(BigDecimal value, int years) {
        return value.compareTo(BigDecimal.valueOf(years)) >= 0;
    }

    public static final List<Position> POSITIONS = List.of(
        new Position("Entry-Level Python Engineer", List.of(
            new Rule("Completed Python coursework", Applicant::pythonCourse),
            new Rule("Completed Software Engineering coursework", Applicant::softwareCourse),
            new Rule("Bachelor's degree in Computer Science", Applicant::bachelorsCS)), List.of(
            new Rule("Completed an Agile course", Applicant::agileCourse))),
        new Position("Python Engineer", List.of(
            new Rule("At least 3 years of Python development", a -> atLeast(a.pythonYears(), 3)),
            new Rule("At least 1 year of data development", a -> atLeast(a.dataYears(), 1)),
            new Rule("Experience in Agile projects (more than 0 years)", a -> a.agileYears().signum() > 0),
            new Rule("Bachelor's degree in Computer Science", Applicant::bachelorsCS)), List.of(
            new Rule("Has used Git", Applicant::git))),
        new Position("Project Manager", List.of(
            new Rule("At least 3 years managing software projects", a -> atLeast(a.managementYears(), 3)),
            new Rule("At least 2 years of experience in Agile projects", a -> atLeast(a.agileYears(), 2)),
            new Rule("PMI Lean Project Management Certification", Applicant::pmi)), List.of()),
        new Position("Senior Knowledge Engineer", List.of(
            new Rule("At least 4 years of Python development", a -> atLeast(a.pythonYears(), 4)),
            new Rule("At least 2 years developing Expert Systems", a -> atLeast(a.expertYears(), 2)),
            new Rule("At least 2 years of data architecture", a -> atLeast(a.architectureYears(), 2)),
            new Rule("At least 2 years of data development", a -> atLeast(a.dataYears(), 2)),
            new Rule("Master's degree in Computer Science", Applicant::mastersCS)), List.of())
    );

    public static BigDecimal parseYears(String input, String label) {
        String text = input == null ? "" : input.trim();
        if (!text.matches("[0-9]+(?:\\.[0-9]+)?")) {
            throw new IllegalArgumentException(label + ": enter a nonnegative number such as 0, 1, or 2.5.");
        }
        return new BigDecimal(text);
    }

    public static boolean parseAnswer(String answer, String label) {
        if ("Yes".equals(answer)) return true;
        if ("No".equals(answer)) return false;
        throw new IllegalArgumentException(label + ": choose Yes or No.");
    }

    public static List<Result> evaluate(Applicant applicant) {
        List<Result> results = new ArrayList<>();
        for (Position position : POSITIONS) {
            List<String> met = new ArrayList<>(), missing = new ArrayList<>();
            List<String> desiredMet = new ArrayList<>(), desiredMissing = new ArrayList<>();
            for (Rule rule : position.required()) {
                (rule.test().test(applicant) ? met : missing).add(rule.explanation());
            }
            for (Rule rule : position.desired()) {
                (rule.test().test(applicant) ? desiredMet : desiredMissing).add(rule.explanation());
            }
            results.add(new Result(position.name(), List.copyOf(met), List.copyOf(missing),
                List.copyOf(desiredMet), List.copyOf(desiredMissing)));
        }
        return List.copyOf(results);
    }

    public static String report(Applicant applicant) {
        List<Result> results = evaluate(applicant);
        StringBuilder out = new StringBuilder("APPLICANT QUALIFICATION RESULTS\n\n");
        out.append("QUALIFIED POSITIONS\n");
        boolean any = false;
        for (Result result : results) if (result.qualified()) {
            any = true;
            out.append("\n").append(result.position()).append("\nAll mandatory requirements met:\n");
            result.met().forEach(s -> out.append("  + ").append(s).append('\n'));
            appendDesired(out, result);
        }
        if (!any) out.append("None based on the supplied information.\n");
        out.append("\nNOT QUALIFIED POSITIONS\n");
        any = false;
        for (Result result : results) if (!result.qualified()) {
            any = true;
            out.append("\n").append(result.position()).append("\nUnmet mandatory requirements:\n");
            result.missing().forEach(s -> out.append("  - ").append(s).append('\n'));
            if (!result.met().isEmpty()) {
                out.append("Requirements already met:\n");
                result.met().forEach(s -> out.append("  + ").append(s).append('\n'));
            }
            appendDesired(out, result);
        }
        if (!any) out.append("None. You qualify for all four positions.\n");
        out.append("\nRULE NOTES\nDesired skills never disqualify an applicant.\n")
            .append("Degrees are checked separately; only degrees in Computer Science count.\n")
            .append("Experience may overlap across categories; do not add categories together.\n")
            .append("Senior Knowledge Engineer requires 2 years each of data architecture and data development.\n");
        return out.toString();
    }

    private static void appendDesired(StringBuilder out, Result result) {
        result.desiredMet().forEach(s -> out.append("  Desired skill present: ").append(s).append('\n'));
        result.desiredMissing().forEach(s -> out.append("  Optional skill absent (does not disqualify): ").append(s).append('\n'));
    }
}
