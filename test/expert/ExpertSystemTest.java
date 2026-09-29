package expert;

import java.math.BigDecimal;
import java.util.List;

/** Dependency-free regression tests with an independent qualification oracle. */
public final class ExpertSystemTest {
    private static int checks;
    private static void check(boolean value, String message) {
        checks++;
        if (!value) throw new AssertionError(message);
    }
    private static BigDecimal n(String value) { return new BigDecimal(value); }
    private static boolean bit(int mask, int bit) { return (mask & (1 << bit)) != 0; }
    private static void invalid(Runnable action, String label) {
        try { action.run(); }
        catch (IllegalArgumentException expected) { checks++; return; }
        throw new AssertionError("Accepted invalid input: " + label);
    }
    public static void main(String[] args) {
        for (String value : new String[]{"", " ", "-1", "NaN", "Infinity", "1e3", "three", "1,5", "1.2.3", ".5", "2."}) {
            invalid(() -> ExpertSystem.parseYears(value, "Years"), value);
        }
        invalid(() -> ExpertSystem.parseYears(null, "Years"), "null years");
        for (String value : new String[]{"BS", "Bachelors", "yes", "", "Select..."}) {
            invalid(() -> ExpertSystem.parseAnswer(value, "Degree"), value);
        }
        invalid(() -> ExpertSystem.parseAnswer(null, "Degree"), "null answer");
        check(ExpertSystem.parseAnswer("Yes", "Degree"), "Yes");
        check(!ExpertSystem.parseAnswer("No", "Degree"), "No");
        check(ExpertSystem.parseYears(" 2.5 ", "Years").equals(n("2.5")), "trim and decimal");
        check(ExpertSystem.parseYears("0", "Years").signum() == 0, "zero");

        int cases = 0;
        // Every education/coursework/certification combination and boundaries of every numeric rule.
        for (int mask = 0; mask < 128; mask++)
        for (String py : new String[]{"0", "2.99", "3", "3.99", "4"})
        for (String data : new String[]{"0", "0.99", "1", "1.99", "2"})
        for (String agile : new String[]{"0", "0.01", "1.99", "2"})
        for (String manage : new String[]{"0", "2.99", "3"})
        for (String expert : new String[]{"0", "1.99", "2"})
        for (String arch : new String[]{"0", "1.99", "2"}) {
            ExpertSystem.Applicant a = new ExpertSystem.Applicant(bit(mask, 0), bit(mask, 1), bit(mask, 2),
                bit(mask, 3), bit(mask, 4), bit(mask, 5), bit(mask, 6), n(py), n(data), n(agile), n(manage), n(expert), n(arch));
            List<ExpertSystem.Result> results = ExpertSystem.evaluate(a);
            int[] missing = {
                (bit(mask, 0) ? 0 : 1) + (bit(mask, 1) ? 0 : 1) + (bit(mask, 4) ? 0 : 1),
                (Double.parseDouble(py) >= 3 ? 0 : 1) + (Double.parseDouble(data) >= 1 ? 0 : 1)
                    + (Double.parseDouble(agile) > 0 ? 0 : 1) + (bit(mask, 4) ? 0 : 1),
                (Double.parseDouble(manage) >= 3 ? 0 : 1) + (Double.parseDouble(agile) >= 2 ? 0 : 1) + (bit(mask, 6) ? 0 : 1),
                (Double.parseDouble(py) >= 4 ? 0 : 1) + (Double.parseDouble(expert) >= 2 ? 0 : 1)
                    + (Double.parseDouble(arch) >= 2 ? 0 : 1) + (Double.parseDouble(data) >= 2 ? 0 : 1) + (bit(mask, 5) ? 0 : 1)
            };
            int[] total = {3, 4, 3, 5};
            check(results.size() == 4, "Four positions");
            for (int i = 0; i < 4; i++) {
                check(results.get(i).qualified() == (missing[i] == 0), "Qualification mismatch at position " + i);
                check(results.get(i).missing().size() == missing[i], "Missing reasons at position " + i);
                check(results.get(i).met().size() + missing[i] == total[i], "Rule accounting at position " + i);
            }
            check(results.get(0).desiredMet().size() == (bit(mask, 2) ? 1 : 0), "Agile desired skill");
            check(results.get(1).desiredMet().size() == (bit(mask, 3) ? 1 : 0), "Git desired skill");
            cases++;
        }
        ExpertSystem.Applicant empty = new ExpertSystem.Applicant(false, false, false, false, false, false, false,
            n("0"), n("0"), n("0"), n("0"), n("0"), n("0"));
        String report = ExpertSystem.report(empty);
        check(report.contains("None based on the supplied information."), "No-match message");
        for (ExpertSystem.Result result : ExpertSystem.evaluate(empty)) {
            check(report.contains(result.position()), "Position appears in report");
            for (String reason : result.missing()) check(report.contains(reason), "Reason appears in report");
        }
        System.out.println("PASS: " + cases + " applicant combinations; " + checks + " assertions.");
    }
}
