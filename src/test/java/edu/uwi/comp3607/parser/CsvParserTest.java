package test.java.edu.uwi.comp3607.parser;

import edu.uwi.comp3607.model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {

    private CsvParser parser;

    @BeforeEach
    void setUp() {
        parser = new CsvParser(); // Initialize before each test
    }

    @Test
    void testParse() throws Exception {
        // Create a temporary CSV file
        File tempFile = File.createTempFile("test-questions", ".csv");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Category,Value,Question,OptionA,OptionB,OptionC,OptionD,CorrectAnswer\n");
            writer.write("Math,100,2+2=?,1,2,3,4,D\n");
        }

        // Parse the CSV
        List<Question> questions = parser.parse(tempFile);

        // Assertions
        assertNotNull(questions, "Parsed questions list should not be null");
        assertEquals(1, questions.size(), "There should be exactly 1 question");

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