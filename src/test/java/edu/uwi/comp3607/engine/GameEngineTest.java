package edu.uwi.comp3607.engine;

import edu.uwi.comp3607.model.Player;
import edu.uwi.comp3607.model.Question;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    private GameEngine engine;

    @BeforeEach
    void setUp() throws Exception {
        engine = new GameEngine("game_event_log.csv", "game_summary.txt");
    }

    @Test
    void testEngineInitialization() {
        assertNotNull(engine, "Engine should be initialized");
        assertNotNull(engine.getCurrentCaseId(), "Case ID should not be null");
    }

    @Test
    void testAddPlayer() {
        engine.addPlayer("p1", "Alice");
        engine.addPlayer("p2", "Bob");

        List<Player> players = engine.getPlayers();
        assertEquals(2, players.size(), "There should be 2 players");
        assertEquals("Alice", engine.playerName("p1"));
        assertEquals("Bob", engine.playerName("p2"));
    }

    @Test
    void testNextPlayerId() {
        engine.addPlayer("p1", "Alice");
        engine.addPlayer("p2", "Bob");

        String first = engine.nextPlayerId();
        String second = engine.nextPlayerId();
        String third = engine.nextPlayerId();

        assertEquals("p1", first);
        assertEquals("p2", second);
        assertEquals("p1", third);
    }

    @Test
    void testAnswerQuestion() {
        engine.addPlayer("p1", "Alice");

        Question q = new Question("History", 100, "Capital of France?",
                "Paris", "London", "Berlin", "Rome", "A");
        engine.playerPickedQuestion("p1", q);

        GameEngine.AnswerResult result1 = engine.answerQuestion("p1", "A");
        assertTrue(result1.correct);
        assertEquals(100, result1.delta);
        assertEquals(100, result1.total);

        Question q2 = new Question("Math", 50, "2 + 2 = ?", "4");
        engine.playerPickedQuestion("p1", q2);

        GameEngine.AnswerResult result2 = engine.answerQuestion("p1", "5");
        assertFalse(result2.correct);
        assertEquals(-50, result2.delta);
        assertEquals(50, result2.total);
    }

    @Test
    void testBuildReport() {
        engine.addPlayer("p1", "Alice");
        engine.addPlayer("p2", "Bob");

        Question q = new Question("Science", 200, "H2O is?", "Water");
        engine.playerPickedQuestion("p1", q);
        engine.answerQuestion("p1", "Water");

        String report = engine.buildReport();
        assertNotNull(report);
        assertTrue(report.contains("Alice"));
        assertTrue(report.contains("Turn-by-turn"));
    }
}
