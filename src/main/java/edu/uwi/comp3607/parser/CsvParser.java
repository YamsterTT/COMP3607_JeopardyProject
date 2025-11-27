package edu.uwi.comp3607.parser;

import edu.uwi.comp3607.model.Question;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CsvParser implements FileParser {

    @Override
    public List<Question> parse(File file) throws Exception {
        List<Question> questions = new ArrayList<>();

        try (Reader in = new FileReader(file);
             CSVParser csvParser = new CSVParser(
                     in,
                     CSVFormat.Builder.create()
                             .setHeader()           
                             .setSkipHeaderRecord(true)
                             .setTrim(true)          
                             .build())) {

            for (CSVRecord r : csvParser) {
                String category = r.get("Category");
                int value = Integer.parseInt(r.get("Value"));
                String text = r.get("Question");

                // Extract options
                String optionA = r.isMapped("OptionA") ? r.get("OptionA") : null;
                String optionB = r.isMapped("OptionB") ? r.get("OptionB") : null;
                String optionC = r.isMapped("OptionC") ? r.get("OptionC") : null;
                String optionD = r.isMapped("OptionD") ? r.get("OptionD") : null;

                // Extract correct answer (letter)
                String correctAnswer;
                if (r.isMapped("CorrectAnswer")) {
                    correctAnswer = r.get("CorrectAnswer").trim();
                } else if (r.isMapped("Answer")) {
                    correctAnswer = r.get("Answer").trim();
                } else {
                    throw new IllegalArgumentException(
                        "No valid answer column found. Expected 'CorrectAnswer' or 'Answer'."
                    );
                }

                // Create Question with options
                questions.add(new Question(category, value, text,
                        optionA, optionB, optionC, optionD, correctAnswer));
            }
        }

        return questions;
    }
}