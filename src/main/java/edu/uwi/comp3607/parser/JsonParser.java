package edu.uwi.comp3607.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uwi.comp3607.model.Question;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonParser implements FileParser {

    @Override
    public List<Question> parse(File file) throws Exception {
        List<Question> questions = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(file);
        JsonNode arr = root.isArray() ? root : root.get("questions");
        if (arr == null || !arr.isArray()) {
            throw new IllegalArgumentException("Invalid JSON format: expected an array or 'questions' field.");
        }

        for (JsonNode node : arr) {
            String category = getRequired(node, "Category");
            int value = getRequiredInt(node, "Value");
            String text = getRequired(node, "Question");

       
            String optionA = getOption(node, "Options", "A");
            String optionB = getOption(node, "Options", "B");
            String optionC = getOption(node, "Options", "C");
            String optionD = getOption(node, "Options", "D");

            // Correct answer
            String correctAnswer = null;
            if (node.has("CorrectAnswer")) {
                correctAnswer = node.get("CorrectAnswer").asText().trim();
            } else if (node.has("Answer")) {
                correctAnswer = node.get("Answer").asText().trim();
            } else {
                throw new IllegalArgumentException("Missing answer field: expected 'CorrectAnswer' or 'Answer'.");
            }

            questions.add(new Question(category, value, text,
                    optionA, optionB, optionC, optionD, correctAnswer));
        }

        return questions;
    }

    private String getRequired(JsonNode node, String field) {
        if (!node.has(field)) throw new IllegalArgumentException("Missing required field: " + field);
        return node.get(field).asText();
    }

    private int getRequiredInt(JsonNode node, String field) {
        if (!node.has(field)) throw new IllegalArgumentException("Missing required field: " + field);
        return node.get(field).asInt();
    }

    private String getOption(JsonNode node, String parent, String key) {
        JsonNode optionsNode = node.get(parent);
        return (optionsNode != null && optionsNode.has(key)) ? optionsNode.get(key).asText() : null;
    }
}