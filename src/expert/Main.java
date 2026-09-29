package expert;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Scanner;

/** Runs entirely in IntelliJ's Run console using standard input and output. */
public final class Main {
    private Main() {}

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner input, PrintStream out) {
        out.println("APPLICANT EXPERT SYSTEM");
        out.println("Type Yes or No for each question, then press Enter.");
        out.println("For experience, enter years such as 0 or 2.5.");
        out.println("Type quit at any prompt to exit.\n");
        try {
            do {
                boolean pythonCourse = askYesNo(input, out, "Completed Python coursework?");
                boolean softwareCourse = askYesNo(input, out, "Completed Software Engineering coursework?");
                boolean agileCourse = askYesNo(input, out, "Completed an Agile course? (optional skill)");
                boolean git = askYesNo(input, out, "Have you used Git? (optional skill)");
                out.println("Answer each degree question separately. Only Computer Science degrees count.");
                boolean bachelors = askYesNo(input, out, "Hold a Bachelor's degree in Computer Science?");
                boolean masters = askYesNo(input, out, "Hold a Master's degree in Computer Science?");
                boolean pmi = askYesNo(input, out, "Hold the PMI Lean Project Management Certification?");
                out.println("\nEXPERIENCE: Use 0 for none. Categories may overlap.");
                BigDecimal pythonYears = askYears(input, out, "Python development");
                BigDecimal dataYears = askYears(input, out, "Data development");
                BigDecimal agileYears = askYears(input, out, "Agile projects");
                BigDecimal managementYears = askYears(input, out, "Managing software projects");
                BigDecimal expertYears = askYears(input, out, "Developing Expert Systems");
                BigDecimal architectureYears = askYears(input, out, "Data architecture");
                ExpertSystem.Applicant applicant = new ExpertSystem.Applicant(pythonCourse,
                    softwareCourse, agileCourse, git, bachelors, masters, pmi, pythonYears,
                    dataYears, agileYears, managementYears, expertYears, architectureYears);
                out.println("\n" + ExpertSystem.report(applicant));
            } while (askYesNo(input, out, "Evaluate another applicant?"));
            out.println("Goodbye.");
        } catch (InputEnded ex) {
            out.println("\nSession ended. Any incomplete questionnaire was not evaluated.");
        }
    }

    private static String read(Scanner input, PrintStream out, String prompt) {
        out.print(prompt);
        out.flush();
        if (!input.hasNextLine()) throw new InputEnded();
        String value = input.nextLine().trim();
        if (value.equalsIgnoreCase("quit")) throw new InputEnded();
        return value;
    }

    private static boolean askYesNo(Scanner input, PrintStream out, String question) {
        while (true) {
            String value = read(input, out, question + " [Yes/No]: ");
            if (value.equalsIgnoreCase("yes")) return true;
            if (value.equalsIgnoreCase("no")) return false;
            out.println("Invalid answer. Enter Yes or No, spelled out (not Y, N, or degree abbreviations such as BS).");
        }
    }

    private static BigDecimal askYears(Scanner input, PrintStream out, String label) {
        while (true) {
            String value = read(input, out, label + " (years): ");
            try { return ExpertSystem.parseYears(value, label); }
            catch (IllegalArgumentException ex) { out.println("Invalid answer. " + ex.getMessage()); }
        }
    }

    private static final class InputEnded extends RuntimeException {}
}
