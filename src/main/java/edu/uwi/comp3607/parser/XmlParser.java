package edu.uwi.comp3607.parser;

import edu.uwi.comp3607.model.Question;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlParser implements FileParser {

    @Override
    public List<Question> parse(File file) throws Exception {
        List<Question> questions = new ArrayList<>();

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(file);
        doc.getDocumentElement().normalize();

        NodeList items = doc.getElementsByTagName("QuestionItem");
        if (items == null || items.getLength() == 0) {
            throw new IllegalArgumentException("Invalid XML structure: expected <QuestionItem> elements.");
        }

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);

            String category = text(item, "Category");
            int value = Integer.parseInt(text(item, "Value"));
            String text = text(item, "QuestionText");

            Element options = (Element) item.getElementsByTagName("Options").item(0);
            String optionA = options != null ? text(options, "OptionA") : null;
            String optionB = options != null ? text(options, "OptionB") : null;
            String optionC = options != null ? text(options, "OptionC") : null;
            String optionD = options != null ? text(options, "OptionD") : null;

            // Correct answer (letter A/B/C/D)
            String correctAnswer;
            if (exists(item, "CorrectAnswer")) {
                correctAnswer = text(item, "CorrectAnswer").trim();
            } else if (exists(item, "Answer")) {
                // fallback if some files use <Answer>
                correctAnswer = text(item, "Answer").trim();
            } else {
                throw new IllegalArgumentException("Missing <CorrectAnswer> (or <Answer>) in XML.");
            }

            questions.add(new Question(category, value, text,
                    optionA, optionB, optionC, optionD, correctAnswer));
        }

        return questions;
    }

    private static boolean exists(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        return list != null && list.getLength() > 0;
    }

    private static String text(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list == null || list.getLength() == 0 || list.item(0) == null) {
            throw new IllegalArgumentException("Missing required tag: <" + tag + ">");
        }
        return list.item(0).getTextContent();
    }
}