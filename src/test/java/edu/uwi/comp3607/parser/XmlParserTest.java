package edu.uwi.comp3607.parser;

import edu.uwi.comp3607.model.Question;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class XmlParserTest {

    private XmlParser parser;
    private File tempFile;

    @BeforeEach
    void setUp() throws Exception {
        parser = new XmlParser();

        tempFile = File.createTempFile("test-questions", ".xml");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("<JeopardyQuestions>\n");
            writer.write("  <QuestionItem>\n");
            writer.write("    <Category>Math</Category>\n");
            writer.write("    <Value>100</Value>\n");
            writer.write("    <QuestionText>2+2=?</QuestionText>\n");
            writer.write("    <Options>\n");
            writer.write("      <OptionA>1</OptionA>\n");
            writer.write("      <OptionB>2</OptionB>\n");
            writer.write("      <OptionC>3</OptionC>\n");
            writer.write("      <OptionD>4</OptionD>\n");
            writer.write("    </Options>\n");
            writer.write("    <CorrectAnswer>D</CorrectAnswer>\n");
            writer.write("  </QuestionItem>\n");
            writer.write("</JeopardyQuestions>");
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
