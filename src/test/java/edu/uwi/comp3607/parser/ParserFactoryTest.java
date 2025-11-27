package edu.uwi.comp3607.parser;

import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class ParserFactoryTest {

    @Test
    void testGetParser() {
        assertTrue(ParserFactory.getParser(new File("questions.csv")) instanceof CsvParser);
        assertTrue(ParserFactory.getParser(new File("questions.json")) instanceof JsonParser);
        assertTrue(ParserFactory.getParser(new File("questions.xml")) instanceof XmlParser);

        assertThrows(IllegalArgumentException.class,
                () -> ParserFactory.getParser(new File("questions.txt")));
    }
}
