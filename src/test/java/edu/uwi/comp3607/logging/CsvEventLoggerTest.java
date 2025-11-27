package edu.uwi.comp3607.logging;

import edu.uwi.comp3607.event.GameEvent;
import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvEventLoggerTest {

    private File tempFile;
    private CsvEventLogger logger;

    @BeforeEach
    void setUp() throws Exception {
        tempFile = File.createTempFile("test_log", ".csv");
        tempFile.deleteOnExit();
        logger = new CsvEventLogger(tempFile.getAbsolutePath(), "GAME1");
    }

    @Test
    void testHeaderWritten() throws Exception {
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertFalse(lines.isEmpty());
        assertEquals("Case_ID,Player_ID,Activity,Timestamp,Category,Question_Value,Answer_Given,Result,Score_After_Play",
                lines.get(0));
    }

    @Test
    void testOnEventWritesLine() throws Exception {
        GameEvent event = new GameEvent("p1", "TestActivity", "Math", 100,
                "Answer", "Correct", 100);
        logger.onEvent(event);

        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertEquals(2, lines.size()); // header + 1 event

        String writtenLine = lines.get(1);
        assertTrue(writtenLine.contains("GAME1"));
        assertTrue(writtenLine.contains("p1"));
        assertTrue(writtenLine.contains("TestActivity"));
        assertTrue(writtenLine.contains("Math"));
        assertTrue(writtenLine.contains("100"));
        assertTrue(writtenLine.contains("Answer"));
        assertTrue(writtenLine.contains("Correct"));
        assertTrue(writtenLine.contains("100"));
    }

    @Test
    void testClose() {
        logger.close();
        assertTrue(tempFile.exists()); // file still exists
    }
}
