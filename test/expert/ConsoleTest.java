package expert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** End-to-end console tests using simulated keyboard input. */
public final class ConsoleTest {
    private static String session(String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Main.run(new Scanner(input), new PrintStream(buffer, true, StandardCharsets.UTF_8));
        return buffer.toString(StandardCharsets.UTF_8);
    }
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        String all = "Yes\n".repeat(7) + "4\n".repeat(6);
        String none = "No\n".repeat(7) + "0\n".repeat(6);
        check(session(all + "No\n").contains("You qualify for all four positions."), "All qualified");
        check(session(none + "No\n").contains("None based on the supplied information."), "None qualified");
        String retry = session("maybe\nYes\nYes\nNo\nNo\nBS\nyes\nNo\nNo\n-1\nabc\n3\n1\n0.5\n0\n0\n0\nNo\n");
        check(retry.contains("degree abbreviations such as BS"), "Invalid degree answer feedback");
        check(retry.contains("enter a nonnegative number"), "Invalid numeric feedback");
        check(retry.contains("\nPython Engineer\nAll mandatory requirements met:"), "Retry preserves answer alignment");
        check(retry.contains("Optional skill absent (does not disqualify): Has used Git"), "Optional skills");
        String repeat = session(all + "Yes\n" + none + "No\n");
        check(repeat.split("APPLICANT QUALIFICATION RESULTS", -1).length == 3, "Two applicant evaluations");
        for (String partial : new String[]{"", "Yes\n", "quit\n", "Yes\nquit\n"}) {
            String result = session(partial);
            check(result.contains("Session ended"), "EOF or quit exits gracefully");
            check(!result.contains("APPLICANT QUALIFICATION RESULTS"), "No partial evaluation");
        }
        check(session(all + "No\n").endsWith("Goodbye." + System.lineSeparator()), "Normal exit");
        System.out.println("PASS: console input, retries, results, multiple applicants, and graceful exit.");
    }
}
