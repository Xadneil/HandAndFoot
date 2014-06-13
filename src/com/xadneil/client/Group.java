package com.xadneil.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A list of cards that satisfy either rank or wildness. A valid group must
 * consist of at least three cards. All natural (non-wild) cards must be of the
 * same rank. The number of wild cards, if any, must be less than the number of
 * naturals. If a Group contains any wild cards, it is considered "dirty",
 * otherwise it is "clean". Dirty groups can contain a maximum of seven cards.
 * Clean groups can hold unlimited cards. //TODO finish
 * 
 * @author Daniel
 * 
 */
public class Group implements Iterable<Card> {

	private List<Card> cards;
	private Card coverCard;
	private boolean clean = true;
	private final int rank;
	private int id;

	public Group(int rank, int id) {
		cards = new ArrayList<>();
		this.rank = rank;
		this.id = id;
	}

	/**
	 * Copy Constructor
	 * 
	 * @param toCopy
	 *            the group to copy
	 */
	public Group(Group toCopy) {
		this.rank = toCopy.rank;
		this.id = toCopy.id;
		List<Card> _cards = new ArrayList<>();
		for (Card c : toCopy) {
			_cards.add(new Card(c));
		}
		addCards(_cards);
	}

	public int getRank() {
		return rank;
	}

	public int getId() {
		return id;
	}

	public final Result addCards(List<Card> add) {
		for (Card c : add) {
			if (c.getRank() != rank && !c.isWild()) {
				return Result.WRONG_CARD;
			}
		}
		if (clean) {
			for (Card c : add) {
				if (c.isWild()) {
					clean = false;
					break;
				}
			}
		}
		if (!clean && cards.size() + add.size() > 7) {
			return Result.TOO_MANY;
		}
		int wildCount = 0;
		List<Card> temp = new ArrayList<>(cards);
		temp.addAll(add);
		for (Card c : temp) {
			if (c.isWild()) {
				wildCount++;
			}
		}
		if (wildCount > temp.size() - wildCount) {
			return Result.WILD_LIMIT;
		}
		cards.addAll(add);
		return Result.SUCCESS;
	}

	public List<Card> getCards() {
		return cards;
	}

	public boolean isClean() {
		return clean;
	}

	@Override
	public Iterator<Card> iterator() {
		return cards.iterator();
	}

	public int getScore() {
		int ret = 0;
		if (clean && cards.size() >= 7) {
			ret += 500;
		}
		if (!clean && cards.size() == 7) {
			ret += 300;
		}
		for (Card c : cards) {
			ret += c.getPoints();
		}
		return ret;
	}

	public enum Result {

		SUCCESS(""),
		TOO_MANY("There are too many cards in this group."),
		WILD_LIMIT("You may not play a wild card on this group."),
		WRONG_CARD("That card is not the correct rank."),
		NOT_ENOUGH("There must be at least three cards in a group");
		public String message;

		private Result(String message) {
			this.message = message;
		}
	}

	public Card getCoverCard() {
		return coverCard;
	}

	public void setCoverCard(Card coverCard) {
		this.coverCard = coverCard;
	}

	public void setId(int id) {
		this.id = id;
	}
}