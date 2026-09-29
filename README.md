# Applicant Expert System — IntelliJ Console Edition

This Java project asks questions and prints all results directly in IntelliJ IDEA's Run console. It does not start an EXE, open a Swing window, or require external libraries.

## Run in IntelliJ

1. Extract `ApplicantExpertSystem-IntelliJ-Console.zip` to a new folder.
2. In IntelliJ, choose **Open** and select the extracted `ApplicantExpertSystem-Console` folder containing `.idea` and `ApplicantExpertSystem.iml`.
3. Under **File > Project Structure > Project**, select an installed **JDK 17 or newer** if SDK `17` is not configured. Keep language level at Java 17.
4. Open `src/expert/Main.java` and click the green Run arrow beside `main`, or select the **Applicant Expert System** run configuration and click Run.
5. Click inside the **Run** console, type your answer, and press Enter after each question. Results appear in that same console.

Type **Yes** or **No** in full; capitalization does not matter. Degree questions ask specifically about CS bachelor's and master's degrees. An answer such as `BS` is rejected with instructions to enter Yes or No. Experience accepts nonnegative years, such as `0`, `1`, or `2.5`; invalid answers are rejected and the same question is repeated. Type `quit` at any question to exit. After an evaluation, you can evaluate another applicant or answer No to finish.

## Qualification rules

All required skills and qualifications must be met. Desired skills are reported but never disqualify an applicant.

| Position | Mandatory requirements | Desired skills |
| --- | --- | --- |
| Entry-Level Python Engineer | Python coursework, Software Engineering coursework, bachelor's in CS | Agile course |
| Python Engineer | 3 years Python development, 1 year data development, more than zero years Agile project experience, bachelor's in CS | Used Git |
| Project Manager | 3 years managing software projects, 2 years Agile project experience, PMI Lean Project Management Certification | None listed |
| Senior Knowledge Engineer | 4 years Python development, 2 years developing Expert Systems, 2 years each in data architecture and data development, master's in CS | None listed |

Numeric minimums are inclusive. Degrees are checked independently; a master's does not automatically establish a CS bachelor's. Experience can overlap across categories. Professional experience does not automatically imply coursework. The certification name is retained from the assignment.

## Files and tests

- `src/expert/Main.java`: console questions, input validation, repeat/exit behavior, and output.
- `src/expert/ExpertSystem.java`: fact records, mandatory/desired rules, inference, and explanations.
- `test/expert/ExpertSystemTest.java`: 345,600 combinations of facts and experience boundaries.
- `test/expert/ConsoleTest.java`: console integration, invalid-input retries, multiple applicants, and exit behavior.

Use the **Rule Tests** and **Console Tests** run configurations to verify the project. Tests use main methods, not JUnit. The rule engine evaluates every position independently using logical AND across its mandatory rules and reports every unmet condition.
