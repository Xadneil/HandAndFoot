package com.xadneil.server.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xadneil.client.Card;
import com.xadneil.client.Group;
import com.xadneil.client.Suit;
import com.xadneil.client.net.Packet;

/**
 * Factory class for creating packets
 * 
 * @author Daniel
 */
public class PacketCreator {

	public static Packet draw(Card c1, Card c2) {
		Packet ret = new Packet(SendOpcode.DRAW);
		ret.addCard(c1);
		ret.addCard(c2);
		return ret;
	}

	public static Packet discardNotify(Card c) {
		Packet ret = new Packet(SendOpcode.DISCARD);
		ret.addCard(c);
		return ret;
	}

	public static Packet draw7(Card[] cards, Card discardTop) {
		Packet ret = new Packet(SendOpcode.DRAW7);
		ret.add(cards != null); // success
		if (cards == null) {
			return ret;
		}
		for (int i = 0; i < 7; i++) {
			ret.addCard(cards[i]);
		}
		if (discardTop == null) {
			ret.addCard(new Card(0, Suit.UNDEFINED));
		} else {
			ret.addCard(discardTop);
		}
		return ret;
	}

	public static Packet gameStart(List<Card> hand) {
		Packet ret = new Packet(SendOpcode.GAME_START);
		for (Card c : hand) {
			ret.addCard(c);
		}
		return ret;
	}

	public static Packet displayTurn(String turn, boolean yourTurn) {
		Packet ret = new Packet(SendOpcode.DISPLAY_TURN);
		ret.add(turn);
		ret.add(yourTurn);
		return ret;
	}

	public static Packet board(Map<Integer, ArrayList<Group>> board) {
		Packet ret = new Packet(SendOpcode.BOARD);
		ret.addInt(board.size());
		for (Map.Entry<Integer, ArrayList<Group>> rank : board.entrySet()) {
			ret.add(rank.getKey().byteValue());
			ret.addInt(rank.getValue().size());
			for (Group group : rank.getValue()) {
				ret.add((byte) group.getRank());
				ret.addInt(group.getCards().size());
				for (Card c : group.getCards()) {
					ret.addCard(c);
				}
			}
		}
		return ret;
	}

	public static Packet play(boolean isCard, boolean success, int id) {
		Packet ret = new Packet(SendOpcode.PLAY);
		ret.add(isCard);
		ret.add(success);
		ret.addInt(id);
		return ret;
	}

	public static Packet points(int points) {
		Packet ret = new Packet(SendOpcode.POINTS);
		ret.addInt(points);
		return ret;
	}

	public static Packet endRound(int team1, int team2, int round) {
		Packet ret = new Packet(SendOpcode.END_ROUND);
		ret.addInt(team1);
		ret.addInt(team2);
		ret.addInt(round);
		return ret;
	}

	public static Packet endRequirements(int numClean, int numDirty) {
		Packet ret = new Packet(SendOpcode.END_REQUIREMENTS);
		ret.addInt(numClean);
		ret.addInt(numDirty);
		return ret;
	}

	public static Packet playOtherCard(boolean team, Card c, int rank, int id) {
		Packet ret = new Packet(SendOpcode.PLAY_OTHER);
		ret.add(true); // card, not group
		ret.add(team);
		ret.addCard(c);
		ret.addInt(rank);
		ret.addInt(id);
		return ret;
	}

	public static Packet playOtherGroup(boolean team, Group g) {
		Packet ret = new Packet(SendOpcode.PLAY_OTHER);
		ret.add(false); // group, not card
		ret.add(team);
		ret.addGroup(g);
		return ret;
	}

	public static Packet clear(boolean team) {
		Packet ret = new Packet(SendOpcode.CLEAR);
		ret.add(team);
		return ret;
	}

	public static Packet login(boolean success) {
		Packet ret = new Packet(SendOpcode.LOGIN);
		ret.add(success);
		return ret;
	}
}