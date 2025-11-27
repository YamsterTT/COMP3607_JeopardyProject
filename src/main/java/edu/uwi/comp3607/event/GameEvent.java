package edu.uwi.comp3607.event;

public class GameEvent {
    private final String playerId;
    private final String activity;
    private final String category;
    private final Integer value;
    private final String answerGiven;
    private final String result;
    private final Integer scoreAfter;

    public GameEvent(String playerId, String activity, String category, Integer value,
                     String answerGiven, String result, Integer scoreAfter) {

        this.playerId = playerId;
        this.activity = activity;
        this.category = category;
        this.value = value;
        this.answerGiven = answerGiven;
        this.result = result;
        this.scoreAfter = scoreAfter;
    }

    public String getPlayerId() { return playerId; }
    public String getActivity() { return activity; }
    public String getCategory() { return category; }
    public Integer getValue() { return value; }
    public String getAnswerGiven() { return answerGiven; }
    public String getResult() { return result; }
    public Integer getScoreAfter() { return scoreAfter; }
}
