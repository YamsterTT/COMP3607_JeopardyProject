package edu.uwi.comp3607.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameEventTest {

    @Test
    void testGameEventGetters() {
        GameEvent event = new GameEvent(
                "p1",
                "Answer Question",
                "Math",
                100,
                "D",
                "Correct",
                200
        );

        assertEquals("p1", event.getPlayerId());
        assertEquals("Answer Question", event.getActivity());
        assertEquals("Math", event.getCategory());
        assertEquals(100, event.getValue());
        assertEquals("D", event.getAnswerGiven());
        assertEquals("Correct", event.getResult());
        assertEquals(200, event.getScoreAfter());
    }
}
