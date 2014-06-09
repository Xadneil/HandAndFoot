package com.xadneil.client;

public enum Suit {

    HEART(true, "h"),
    DIAMOND(true, "d"),
    SPADE(false, "s"),
    CLUB(false, "c"),
    UNDEFINED(false, "");

    Suit(boolean red, String name) {
        this.red = red;
        this.myName = name;
    }
    public final boolean red;
    public final String myName;
}