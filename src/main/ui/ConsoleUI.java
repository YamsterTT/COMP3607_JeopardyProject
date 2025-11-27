package edu.uwi.comp3607.ui;

import edu.uwi.comp3607.engine.GameEngine;
import edu.uwi.comp3607.engine.GameEngine.AnswerResult;
import edu.uwi.comp3607.model.Question;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleUI {

    private final GameEngine engine;
    private final Scanner sc = new Scanner(System.in);

    
    public ConsoleUI(GameEngine engine) {
        this.engine = engine;
    }

    /**
     * Start the console game loop.
     * @param questionFile the file containing the questions (CSV/XLSX etc.)
     */
    public void start(File questionFile) throws Exception {

        // Load questions
        engine.loadQuestions(questionFile);

        // Ask for number of players
        System.out.println("How many players? (1 to 4)");
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("QUIT")) {
            printReportAndExit();
            return;
        }
        int n = Integer.parseInt(input);
        engine.logPlayerCount(n);

        System.out.println(); // spacing

        // Ask for player names
        List<String> playerNames = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            System.out.println("Enter player " + i + " name:");
            String name = sc.nextLine().trim();
            if (name.equalsIgnoreCase("QUIT")) {
                printReportAndExit();
                return;
            }
            playerNames.add(name);
            // Generate a simple unique player ID
            engine.addPlayer("P" + i + "-" + UUID.randomUUID().toString().substring(0, 6), name);

            System.out.println(); // spacing after each name
        }

        String summaryPath = "game_summary.txt";
        engine.initSummaryReport(summaryPath, playerNames);

        // Main loop
        while (!engine.allAnswered()) {

            String pid = engine.nextPlayerId();
            System.out.println("\nPlayer turn: " + engine.playerName(pid));

            // Display categories
            List<String> cats = engine.categories();
            System.out.println("Categories:");
            for (int i = 0; i < cats.size(); i++) {
                System.out.println((i + 1) + " - " + cats.get(i));
            }

            // Category selection with validation loop
            int catIndex = -1;
            while (true) {
                System.out.print("Choose category between 1 and " + cats.size() + ": ");
                String catInput = sc.nextLine().trim();
                if (catInput.equalsIgnoreCase("QUIT")) {
                    printReportAndExit();
                    return;
                }
                try {
                    catIndex = Integer.parseInt(catInput);
                    if (catIndex >= 1 && catIndex <= cats.size()) {
                        break; // valid input
                    } else {
                        System.out.println("Invalid category number! Please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number!");
                }
            }

            String cat = cats.get(catIndex - 1);
            engine.logCategorySelection(pid, cat);

            System.out.println(); // spacing

            List<Integer> vals = engine.valuesForCategory(cat);
            if (vals.isEmpty()) {
                System.out.println("No available values in this category. Choose another.");
                continue;
            }
            System.out.println("Choose the amount of points you would like to attempt:");
            for (int i = 0; i < vals.size(); i++) {
                System.out.println((i + 1) + " - " + vals.get(i));
            }

            int valIndex = -1;
            while (true) {
                System.out.print("Choose point value between 1 and " + vals.size() + ": ");
                String valInput = sc.nextLine().trim();
                if (valInput.equalsIgnoreCase("QUIT")) {
                    printReportAndExit();
                    return;
                }
                try {
                    valIndex = Integer.parseInt(valInput);
                    if (valIndex >= 1 && valIndex <= vals.size()) {
                        break; // valid input
                    } else {
                        System.out.println("Invalid value number! Please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number!");
                }
            }

            int val = vals.get(valIndex - 1);

            Question q = engine.pickQuestion(cat, val);
            if (q == null) {
                System.out.println("Invalid selection or question already answered!");
                continue;
            }

            engine.playerPickedQuestion(pid, q);

            // Show question and options
            System.out.println("\nQuestion: " + q.getText());
            if (q.hasOptions()) {
                System.out.println("A) " + q.getOptionA());
                System.out.println("B) " + q.getOptionB());
                System.out.println("C) " + q.getOptionC());
                System.out.println("D) " + q.getOptionD());
                System.out.println("(Type A/B/C/D or the full option text)");
            }

            System.out.print("Your answer: ");
            String ans = sc.nextLine().trim();
            if (ans.equalsIgnoreCase("QUIT")) {
                printReportAndExit();
                return;
            }

            // Evaluate answer and show feedback
            AnswerResult result = engine.answerQuestion(pid, ans);
            String correctness = result.correct ? "Correct!" : "Incorrect.";
            String deltaStr = (result.delta >= 0 ? "+" : "") + result.delta;

            String expectedInfo = "";
            if (!result.correct && result.expectedLetter != null) {
                expectedInfo = " Correct answer: " + result.expectedLetter +
                        (result.expectedText != null ? " (" + result.expectedText + ")" : "");
            }

            System.out.println(correctness + " " + deltaStr + " points. Total: " + result.total + "." + expectedInfo);

            // Append detailed turn entry to summary TXT (always to the enforced file)
            engine.appendTurnToReport(
                summaryPath,
                engine.playerName(pid),
                cat,
                val,
                q.getText(),
                ans,
                result.correct,
                result.delta,
                result.total
            );
        }

        // Append final scores & winner to summary and print report
        engine.appendFinalSummary(summaryPath);
        printReportAndExit();
    }

    private void printReportAndExit() {
        System.out.println("\n--- Jeopardy Game Report ---");
        System.out.println(engine.buildReport());

        // Show winner
        String winnerName = engine.getWinnerName();
        int winnerScore = engine.getWinnerScore();
        System.out.println("\nWinner: " + winnerName + " with " + winnerScore + " points!");

        engine.close();
        System.out.println("\nEvent log saved: game_event_log.csv");
        System.out.println("Summary report saved: game_summary.txt");
    }

    // Convenience factory if you prefer constructing UI here (optional):
    public static ConsoleUI createDefault() throws Exception {
        String logPath = "game_event_log.csv";
        String summaryPath = "game_summary.txt";
        GameEngine engine = new GameEngine(logPath, summaryPath);
        return new ConsoleUI(engine);
    }
}
