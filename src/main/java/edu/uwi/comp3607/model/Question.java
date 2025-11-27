package edu.uwi.comp3607.model;

public class Question {
    private final String category;
    private final int value;
    private final String text;
    private final String answer;   

    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;

    private boolean answered = false;

    public Question(String category, int value, String text,
                    String optionA, String optionB, String optionC, String optionD,
                    String answer) {
        this.category = category;
        this.value = value;
        this.text = text;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.answer = answer;
    }

    public Question(String category, int value, String text, String answer) {
        this(category, value, text, null, null, null, null, answer);
    }

    public String getCategory() { return category; }
    public int getValue() { return value; }
    public String getText() { return text; }
    public String getAnswer() { return answer; }

    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }

    public boolean isAnswered() { return answered; }


    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean hasOptions() {
        return optionA != null || optionB != null || optionC != null || optionD != null;
    }
}