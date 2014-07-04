package com.xadneil.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A list of cards that satisfy either rank or wildness.
 * <p>
 * A valid group must adhere to the following rules. An invalid group may be
 * constructed and used on the client side, but must be valid before being sent
 * to the server.
 * <p>
 * <ul>
 * <il>A Group must consist of at least three cards.<br>
 * <il>All natural (non-wild) cards must be of the same rank.<br>
 * <il>The number of wild cards, if any, must be less than the number of
 * naturals.<br>
 * <il>If a Group contains any wild cards, it is considered "dirty", otherwise
 * it is "clean".<br>
 * <il>Dirty groups can contain a maximum of seven cards. Clean groups can hold
 * unlimited cards.<br>
 * <il>When a group reaches seven cards, the group is visually compacted and a
 * red or black card of the appropriate rank is selected as the cover card for
 * clean and dirty groups, respectively. The group is now complete.<br>
 * <il>Completed clean groups are worth 500 points, dirty groups 300 points.
 * </ul>
 * 
 * @author Daniel
 */
public class Group implements Iterable<Card> {

	private final List<Card> cards;
	private Card coverCard;
	private boolean clean = true;
	private final int rank;
	private int id;

	/**
	 * Class Constructor
	 * 
	 * @param rank
	 *            the rank of the group
	 * @param id
	 *            the id of the group (the index, starting at 0 for the first,
	 *            of multiple groups of the same rank)
	 */
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
		this.cards = _cards;
	}

	/**
	 * Gets the rank of the group
	 * 
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Gets the ID of the group
	 * 
	 * @return the ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Adds a list of cards to the group. Checks the ranks of the cards, the
	 * wild to natural ratio, the dirtiness of the group, and the seven card
	 * limit for dirty groups.
	 * 
	 * @param add
	 *            the list of cards to add
	 * @return the result of the addition
	 */
	public final Result addCards(List<Card> add) {
		// chack card ranks
		for (Card c : add) {
			if (c.getRank() != rank && !c.isWild()) {
				return Result.WRONG_CARD;
			}
		}
		// check dirtiness
		if (clean) {
			for (Card c : add) {
				if (c.isWild()) {
					clean = false;
					break;
				}
			}
		}
		// check seven card limit for dirties
		if (!clean && cards.size() + add.size() > 7) {
			return Result.TOO_MANY;
		}
		// checks natural to wild ratio
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

	/**
	 * Gets the cards in the group
	 * 
	 * @return the cards
	 */
	public List<Card> getCards() {
		return cards;
	}

	/**
	 * Gets whether the group is clean or dirty
	 * 
	 * @return clean or dirty
	 */
	public boolean isClean() {
		return clean;
	}

	@Override
	public Iterator<Card> iterator() {
		return cards.iterator();
	}

	/**
	 * Gets the score of this group including bonus points for a completed group
	 * and the point values of the cards.
	 * 
	 * @return the score of the group
	 */
	public int getScore() {
		int ret = 0;
		if (cards.size() >= 7) {
			if (clean) {
				ret += 500;
			} else {
				ret += 300;
			}
		}
		for (Card c : cards) {
			ret += c.getPoints();
		}
		return ret;
	}

	/**
	 * The result of adding cards to the group. Includes readable error
	 * messages.
	 * <p>
	 * <ul>
	 * <li>SUCCESS = ""<br>
	 * <li>TOO_MANY = "There are too many cards in this group."<br>
	 * <li>WILD_LIMIT = "You may not play a wild card on this group."<br>
	 * <li>WRONG_CARD = "That card is not the correct rank."<br>
	 * <li>NOT_ENOUGH = "There must be at least three cards in a group"<br>
	 * </ul>
	 * 
	 * @author Daniel
	 * 
	 */
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

	/**
	 * Gets the cover card for this completed group
	 * 
	 * @return the cover card
	 */
	public Card getCoverCard() {
		return coverCard;
	}

	/**
	 * Sets the cover card for this completed group
	 * 
	 * @param coverCard
	 *            the cover card
	 */
	public void setCoverCard(Card coverCard) {
		this.coverCard = coverCard;
	}

	/**
	 * Sets the new ID of the group
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
}