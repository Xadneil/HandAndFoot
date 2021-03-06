package com.xadneil.client;

/**
 * Class for representing a playing card. Has rank, suit, and points
 * 
 * @author Daniel
 */
public class Card implements Comparable<Card> {

	private final String name;
	private final Suit suit;
	private final int points;
	private final int rank;

	/**
	 * Card Constructor
	 * 
	 * @param rank
	 *            the rank of the card (1-14)
	 * @param suit
	 *            the suit of the card
	 */
	public Card(int rank, Suit suit) {
		this.rank = rank;
		this.suit = suit;
		String temp = null;
		switch (rank) {
		case 0:
			temp = "";
			break;
		case 1:
			temp = "a";
			break;
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			temp = String.valueOf(rank);
			break;
		case 10:
			temp = "t";
			break;
		case 11: // jack
		case 14: // joker (filename is distinguished by UNDEFINED Suit)
			temp = "j";
			break;
		case 12:
			temp = "q";
			break;
		case 13:
			temp = "k";
			break;
		default:
			throw new RuntimeException();
		}
		this.name = temp + suit.myName;
		switch (rank) {
		case 0:
			// discard blank placeholder
			this.points = 0;
			break;
		case 1:
		case 2:
			this.points = 20;
			break;
		case 3:
			this.points = suit.red ? 500 : 0;
			break;
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
			this.points = 5;
			break;
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
			this.points = 10;
			break;
		case 14:
			this.points = 50;
			break;
		default:
			throw new RuntimeException("Invalid rank " + rank);
		}
	}

	/**
	 * Copy Constructor
	 * 
	 * @param toCopy
	 */
	public Card(Card toCopy) {
		this(toCopy.rank, toCopy.suit);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += rank;
		hash = 60 * hash + (this.suit != null ? this.suit.ordinal() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof Card)) {
			return false;
		}
		Card card = (Card) other;
		return card.getSuit() == suit && card.getRank() == rank;
	}

	/**
	 * Gets the suit of the card
	 * 
	 * @return the suit
	 */
	public Suit getSuit() {
		return suit;
	}

	/**
	 * Gets the rank of the card
	 * 
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Name of the card for image-loading purposes
	 * 
	 * @return the name of the card
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the points of the card.
	 * 
	 * @return the points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Gets whether or not the card is wild. 2s and jokers are wild.
	 * 
	 * @return wild wild
	 */
	public boolean isWild() {
		return rank == 14 || rank == 2;
	}

	/**
	 * Gets the sorting group for the card
	 * 
	 * @return the sorting group
	 */
	public Main.Sorting getSorting() {
		if (rank == 3) {
			if (suit.red) {
				return Main.Sorting.RED3S;
			} else {
				return Main.Sorting.BLACK3S;
			}
		} else if (isWild()) {
			return Main.Sorting.WILDS;
		} else
			return Main.Sorting.OTHERS;
	}

	@Override
	public int compareTo(Card o) {
		if (o == null) {
			throw new NullPointerException();
		}
		if (getSorting() == o.getSorting()) {
			int sign = Main.ascending ? 1 : -1;
			if (rank == 1 && o.rank != 1) {
				return sign;
			} else if (o.rank == 1 && rank != 1) {
				return -sign;
			}
			return sign * Integer.compare(rank, o.getRank());
		} else {
			return Integer.compare(getSorting().order, o.getSorting().order);
		}
	}

	@Override
	public String toString() {
		return getName();
	}
}
