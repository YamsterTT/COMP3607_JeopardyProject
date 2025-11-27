package edu.uwi.comp3607.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    private Question question;

    @BeforeEach
    void setUp() {
        question = new Question("Math", 100, "2+2=?", "1", "2", "3", "4", "D");
    }

    @Test
    void testInitialization() {
        assertEquals("Math", question.getCategory());
        assertEquals(100, question.getValue());
        assertEquals("2+2=?", question.getText());
        assertEquals("D", question.getAnswer());
        assertTrue(question.hasOptions());
        assertFalse(question.isAnswered());
    }

    @Test
    void testSetAnswered() {
        question.setAnswered(true);
        assertTrue(question.isAnswered());
    }
}