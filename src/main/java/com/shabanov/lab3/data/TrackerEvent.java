package com.shabanov.lab3.data;

public enum TrackerEvent {
    STARTED("started", 1),
    STOPPED("stopped", 2),
    COMPLETED("completed", 3),
    EMPTY("",4);

    private final String string;

    private final int integer;

    private TrackerEvent(String str, int intg) {
        string = str;
        integer = intg;
    }

    public String getEventString () {
        return string;
    }

    public int getEventInt () {
        return integer;
    }
}
