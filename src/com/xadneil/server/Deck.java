package com.xadneil.server;

import java.util.ArrayList;

import com.xadneil.client.Card;
import com.xadneil.client.Suit;

@SuppressWarnings("serial")
public class Deck extends ArrayList<Card> {

    public Deck() {
        super(54);
        for (Suit s : Suit.values()) {
            if (s != Suit.UNDEFINED) {
                for (int i = 1; i <= 13; i++) {
                    add(new Card(i, s));
                }
            } else {
                for (int i = 0; i < 2; i++) {
                    add(new Card(14, s));
                }
            }
        }
    }
}