package com.xadneil.client;

/**
 * An enum of the 4 possible suits in a card game, including a redness value.
 * 
 * @author Daniel
 */
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