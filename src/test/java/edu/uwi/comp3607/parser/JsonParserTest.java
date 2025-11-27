package edu.uwi.comp3607.parser;

import edu.uwi.comp3607.model.Question;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    private JsonParser parser;
    private File tempFile;

    @BeforeEach
    void setUp() throws Exception {
        parser = new JsonParser();

        tempFile = File.createTempFile("test-questions", ".json");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("[{\"Category\":\"Math\",\"Value\":100,\"Question\":\"2+2=?\",\"OptionA\":\"1\",\"OptionB\":\"2\",\"OptionC\":\"3\",\"OptionD\":\"4\",\"CorrectAnswer\":\"D\"}]");
        }
    }

    @Test
    void testParse() throws Exception {
        List<Question> questions = parser.parse(tempFile);

        assertNotNull(questions);
        assertEquals(1, questions.size());

        Question q = questions.get(0);
        assertEquals("Math", q.getCategory());
        assertEquals(100, q.getValue());
        assertEquals("2+2=?", q.getText());
        assertEquals("1", q.getOptionA());
        assertEquals("2", q.getOptionB());
        assertEquals("3", q.getOptionC());
        assertEquals("4", q.getOptionD());
        assertEquals("D", q.getAnswer());
    }
}
