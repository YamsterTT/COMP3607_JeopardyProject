package test.java.edu.uwi.comp3607.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("p1", "Alice");
    }

    @Test
    void testInitialization() {
        assertEquals("p1", player.getId());
        assertEquals("Alice", player.getName());
        assertEquals(0, player.getScore());
    }

    @Test
    void testAddScore() {
        player.addScore(50);
        assertEquals(50, player.getScore());
        player.addScore(25);
        assertEquals(75, player.getScore());
    }
}
