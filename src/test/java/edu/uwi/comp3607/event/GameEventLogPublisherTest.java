package edu.uwi.comp3607.event;

import org.junit.jupiter.api.*;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

class GameEventLogPublisherTest {

    private GameEventLogPublisher publisher;
    private GameEvent event;

    @BeforeEach
    void setUp() {
        publisher = new GameEventLogPublisher();
        event = new GameEvent("p1", "Answer", "Math", 100, "A", "Correct", 100);
    }

    @Test
    void testRegisterAndPublish() {
        AtomicBoolean called = new AtomicBoolean(false);

        publisher.register(evt -> {
            called.set(true);
            assertEquals("p1", evt.getPlayerId());
        });

        publisher.publish(event);
        assertTrue(called.get());
    }

    @Test
    void testCloseAll() {
        AtomicBoolean closed = new AtomicBoolean(false);

        publisher.register(new GameObserver() {
            @Override
            public void onEvent(GameEvent evt) {}
            @Override
            public void close() { closed.set(true); }
        });

        publisher.closeAll();
        assertTrue(closed.get());
    }
}
