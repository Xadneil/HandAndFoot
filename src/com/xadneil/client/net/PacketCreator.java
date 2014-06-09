package com.xadneil.client.net;

import com.xadneil.client.Card;
import com.xadneil.client.Group;

/**
 * Factory class for creating packets
 * 
 * @author Daniel
 */
public class PacketCreator {

	public static Packet draw() {
		return new Packet(SendOpcode.DRAW);
	}

	public static Packet draw7() {
		return new Packet(SendOpcode.DRAW7);
	}

	public static Packet play(Group staging) {
		Packet ret = new Packet(SendOpcode.PLAY_GROUP);
		ret.addGroup(staging);
		return ret;
	}

	public static Packet play(Card c, int rank, int id) {
		Packet ret = new Packet(SendOpcode.PLAY_CARD);
		ret.addInt(id);
		ret.addInt(rank);
		ret.addCard(c);
		return ret;
	}

	public static Packet login(String username, char[] password) {
		Packet ret = new Packet(SendOpcode.LOGIN);
		ret.add(username);
		ret.add(new String(password));
		return ret;
	}

	public static Packet points() {
		return new Packet(SendOpcode.POINTS);
	}

	public static Packet discard(Card c) {
		Packet ret = new Packet(SendOpcode.DISCARD);
		ret.addCard(c);
		return ret;
	}
}
