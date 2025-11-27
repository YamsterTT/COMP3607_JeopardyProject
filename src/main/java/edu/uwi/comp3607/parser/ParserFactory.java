package edu.uwi.comp3607.parser;

import java.io.File;

public class ParserFactory {

    public static FileParser getParser(File f) {
        String name = f.getName().toLowerCase();

        if (name.endsWith(".csv")) return new CsvParser();
        if (name.endsWith(".json")) return new JsonParser();
        if (name.endsWith(".xml")) return new XmlParser();

        throw new IllegalArgumentException("Unsupported file format: " + name);
    }
}
