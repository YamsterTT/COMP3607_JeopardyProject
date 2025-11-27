package edu.uwi.comp3607.parser;

import edu.uwi.comp3607.model.Question;
import java.io.File;
import java.util.List;

public interface FileParser {
    List<Question> parse(File file) throws Exception;
}
