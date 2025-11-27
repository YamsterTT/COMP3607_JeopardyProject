package edu.uwi.comp3607.event;

public interface GameObserver {
    void onEvent(GameEvent evt);
    default void close() {}
}
