package edu.uwi.comp3607.event;

import java.util.ArrayList;
import java.util.List;

public class GameEventLogPublisher {

    private final List<GameObserver> observers = new ArrayList<>();

    public void register(GameObserver o) {
        observers.add(o);
    }

    public void publish(GameEvent evt) {
        for (GameObserver o : observers) {
            o.onEvent(evt);
        }
    }

    public void closeAll() {
        for (GameObserver o : observers) {
            o.close();
        }
    }
}
