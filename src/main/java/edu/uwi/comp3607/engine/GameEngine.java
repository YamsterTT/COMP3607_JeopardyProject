
package edu.uwi.comp3607.engine;

import edu.uwi.comp3607.event.*;
import edu.uwi.comp3607.logging.CsvEventLogger;
import edu.uwi.comp3607.model.Player;
import edu.uwi.comp3607.model.Question;
import edu.uwi.comp3607.parser.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class GameEngine {
    private final GameEventLogPublisher publisher;
    private final CsvEventLogger logger;
    private final LinkedHashMap<String, Player> players = new LinkedHashMap<>();
    private final Map<String, List<Question>> board = new LinkedHashMap<>();
    private final List<String> turnHistory = new ArrayList<>();
    private final Map<String, Question> selected = new HashMap<>();
    private String[] order;
    private int idx = 0;

    // Single source of truth for Case ID
    private final String caseId;

    /**
     * Always pass "game_event_log.csv" and "game_summary.txt" here.
     */
    public GameEngine(String logPath, String summaryPath) throws Exception {
        this.publisher = new GameEventLogPublisher();

        //  Compute next GAME ID strictly from "game_summary.txt"
        this.caseId = getNextCaseId(summaryPath);

        // Ensure the CSV logger uses the SAME ID
        this.logger = new CsvEventLogger(logPath, caseId);
        this.publisher.register(logger);

        publisher.publish(new GameEvent(null, "Start Game", null, null, null, null, null));
    }

    public String getCurrentCaseId() {
        return caseId;
    }

    private String getNextCaseId(String summaryFilePath) throws Exception {
        // Enforce the filename to avoid path mismatch resetting to 1
        final String enforcedName = "game_summary.txt";
        Path p = Path.of(enforcedName);

        int nextNumber = 1;

        if (Files.exists(p)) {
            List<String> lines = Files.readAllLines(p);
            final String prefix = "Case ID: GAME";

            for (int i = lines.size() - 1; i >= 0; i--) {
                String line = lines.get(i);
                if (line.startsWith(prefix)) {
                    String numPart = line.substring(prefix.length()).trim();
                    try {
                        int current = Integer.parseInt(numPart);
                        nextNumber = current + 1;
                    } catch (NumberFormatException ignored) {
                    }
                    break;
                }
            }
        }

        return "GAME" + nextNumber;
    }

    public void initSummaryReport(String path, List<String> playerNames) throws Exception {
        final String enforcedName = "game_summary.txt";
        Path summaryPath = Path.of(enforcedName);

        StringBuilder sb = new StringBuilder();
        sb.append("\nJEOPARDY PROGRAMMING GAME REPORT\n")
          .append("================================\n\n")
          .append("Case ID: ").append(caseId).append("\n\n")
          .append("Players: ").append(String.join(", ", playerNames)).append("\n\n")
          .append("Gameplay Summary:\n")
          .append("-----------------\n");
        Files.writeString(summaryPath, sb.toString(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        publisher.publish(new GameEvent(null, "Generate Event Log",
                enforcedName, null, "Header Written", null, null));
    }

    public void appendTurnToReport(String path, String playerName, String category, int value,
                                   String questionText, String answer, boolean correct, int delta, int totalScore) throws Exception {
        final String enforcedName = "game_summary.txt";
        Path summaryPath = Path.of(enforcedName);

        String correctness = correct ? "Correct" : "Incorrect";
        String deltaStr = (delta >= 0 ? "+" : "") + delta;
        int turnNumber = turnHistory.size();
        StringBuilder sb = new StringBuilder();
        sb.append("Turn ").append(turnNumber).append(": ")
          .append(playerName).append(" selected ").append(category)
          .append(" for ").append(value).append(" pts\n")
          .append("Question: ").append(questionText).append("\n")
          .append("Answer: ").append(answer).append(" — ").append(correctness)
          .append(" (").append(deltaStr).append(" pts)\n")
          .append("Score after turn: ").append(playerName).append(" = ").append(totalScore).append("\n\n");
        Files.writeString(summaryPath, sb.toString(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void appendFinalSummary(String path) throws Exception {
        final String enforcedName = "game_summary.txt";
        Path summaryPath = Path.of(enforcedName);

        StringBuilder sb = new StringBuilder();
        sb.append("Final Scores:\n");
        for (Player p : players.values()) {
            sb.append(p.getName()).append(": ").append(p.getScore()).append("\n");
        }
        sb.append("\nWinner: ").append(getWinnerName())
          .append(" with ").append(getWinnerScore()).append(" points!\n");
        Files.writeString(summaryPath, sb.toString(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    // -------------------- Game logic --------------------

    public void loadQuestions(File file) throws Exception {
        publisher.publish(new GameEvent(null, "Load File", file.getName(), null, null, null, null));
        FileParser parser = ParserFactory.getParser(file);
        List<Question> questions = parser.parse(file);
        for (Question q : questions) {
            board.computeIfAbsent(q.getCategory(), k -> new ArrayList<>()).add(q);
        }
        publisher.publish(new GameEvent(null, "File Loaded Successfully", file.getName(), null, null, "Success", null));
    }

    public void addPlayer(String id, String name) {
        Player p = new Player(id, name);
        players.put(id, p);
        publisher.publish(new GameEvent(id, "Enter Player Name", null, null, name, null, p.getScore()));
    }

    public void logPlayerCount(int count) {
        publisher.publish(new GameEvent(null, "Select Player Count", null, null, null, String.valueOf(count), null));
    }

    public boolean allAnswered() {
        return board.values().stream()
                    .flatMap(List::stream)
                    .allMatch(Question::isAnswered);
    }

    public List<String> categories() {
        return new ArrayList<>(board.keySet());
    }

    public List<Integer> valuesForCategory(String cat) {
        return board.getOrDefault(cat, Collections.emptyList())
                    .stream().filter(q -> !q.isAnswered())
                    .map(Question::getValue)
                    .distinct().sorted().toList();
    }

    public Question pickQuestion(String cat, int val) {
        for (Question q : board.getOrDefault(cat, Collections.emptyList())) {
            if (!q.isAnswered() && q.getValue() == val) return q;
        }
        return null;
    }

    public void playerPickedQuestion(String playerId, Question q) {
        selected.put(playerId, q);
        publisher.publish(new GameEvent(playerId, "Select Question",
                q.getCategory(), q.getValue(), null, null,
                players.get(playerId).getScore()));
    }

    public void logCategorySelection(String playerId, String category) {
        publisher.publish(new GameEvent(playerId, "Select Category",
                category, null, null, null, players.get(playerId).getScore()));
    }

    public static class AnswerResult {
        public final boolean correct;
        public final int delta;
        public final int total;
        public final String expectedLetter;
        public final String expectedText;
        public final String normalizedAnswerLetter;

        public AnswerResult(boolean correct, int delta, int total,
                            String expectedLetter, String expectedText,
                            String normalizedAnswerLetter) {
            this.correct = correct;
            this.delta = delta;
            this.total = total;
            this.expectedLetter = expectedLetter;
            this.expectedText = expectedText;
            this.normalizedAnswerLetter = normalizedAnswerLetter;
        }
    }

    public AnswerResult answerQuestion(String playerId, String rawAnswer) {
        Question q = selected.get(playerId);
        if (q == null) return new AnswerResult(false, 0, players.get(playerId).getScore(),
                null, null, null);
        String userLetter = normalizeToLetter(q, rawAnswer);
        String expectedLetter = q.getAnswer();
        boolean correct = expectedLetter != null && userLetter != null
                ? expectedLetter.equalsIgnoreCase(userLetter)
                : q.getAnswer().equalsIgnoreCase(rawAnswer.trim());
        q.setAnswered(true);
        int delta = correct ? q.getValue() : -q.getValue();
        Player p = players.get(playerId);
        p.addScore(delta);
        String result = correct ? "Correct" : "Incorrect";
        String expectedText = optionTextForLetter(q, expectedLetter);
        turnHistory.add(p.getName() + " answered: " + rawAnswer +
                " (" + result + "), value=" + q.getValue() +
                ", delta=" + delta +
                ", score=" + p.getScore());
        publisher.publish(new GameEvent(playerId, "Answer Question",
                q.getCategory(), q.getValue(), rawAnswer, result, p.getScore()));
        publisher.publish(new GameEvent(playerId, "Score Updated",
                null, null, null, null, p.getScore()));
        selected.remove(playerId);
        return new AnswerResult(correct, delta, p.getScore(), expectedLetter, expectedText, userLetter);
    }

    private String normalizeToLetter(Question q, String input) {
        if (input == null) return null;
        String ans = input.trim();
        if (ans.length() == 1) {
            char c = Character.toUpperCase(ans.charAt(0));
            if (c >= 'A' && c <= 'D') return String.valueOf(c);
        }
        if (q.getOptionA() != null && ans.equalsIgnoreCase(q.getOptionA())) return "A";
        if (q.getOptionB() != null && ans.equalsIgnoreCase(q.getOptionB())) return "B";
        if (q.getOptionC() != null && ans.equalsIgnoreCase(q.getOptionC())) return "C";
        if (q.getOptionD() != null && ans.equalsIgnoreCase(q.getOptionD())) return "D";
        return null;
    }

    public String optionTextForLetter(Question q, String letter) {
        if (letter == null) return null;
        return switch (letter.toUpperCase()) {
            case "A" -> q.getOptionA();
            case "B" -> q.getOptionB();
            case "C" -> q.getOptionC();
            case "D" -> q.getOptionD();
            default -> null;
        };
    }

    public String nextPlayerId() {
        if (order == null) {
            order = players.keySet().toArray(new String[0]);
            idx = 0;
        }
        String pid = order[idx];
        idx = (idx + 1) % order.length;
        publisher.publish(new GameEvent(pid, "Select Player Turn",
                null, null, null, null, players.get(pid).getScore()));
        return pid;
    }

    public String playerName(String id) {
        return players.get(id).getName();
    }

    public String buildReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Jeopardy Game Report\n");
        sb.append("===================\n\n");
        sb.append("Final Scores:\n");
        for (Player p : players.values()) {
            sb.append(p.getName()).append(" : ").append(p.getScore()).append("\n");
        }
        sb.append("\nTurn-by-turn:\n");
        for (String s : turnHistory) sb.append(s).append("\n");
        return sb.toString();
    }

    public void generateTxtReport(String path) throws Exception {
        String report = buildReport();
        Files.writeString(Path.of(path), report);
        publisher.publish(new GameEvent(null, "Generate Report",
                path, null, "Generated", null, null));
    }

    public void close() {
        publisher.publish(new GameEvent(null, "Exit Game",
                null, null, null, null, null));
        publisher.closeAll();
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public String getWinnerName() {
        return players.values().stream()
                .max(Comparator.comparingInt(Player::getScore))
                .map(Player::getName)
                .orElse("No Winner");
    }

    public int getWinnerScore() {
        return players.values().stream()
                .max(Comparator.comparingInt(Player::getScore))
                .map(Player::getScore)
                .orElse(0);
    }
}
