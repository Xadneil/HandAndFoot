package com.xadneil.client.net;

import java.util.ArrayList;
import java.util.List;

import com.xadneil.client.Card;
import com.xadneil.client.Group;
import com.xadneil.client.Suit;

/**
 * A class that helps read and write a string of bytes: a packet
 * 
 * @author Daniel
 */
@SuppressWarnings("serial")
public final class Packet extends ArrayList<Byte> {

	// set to 1 to avoid getting opcode
	int position = 1;

	/**
	 * Creates a packet with an initial opcode
	 * 
	 * @param opcode
	 *            the initial opcode
	 */
	public Packet(Opcode opcode) {
		super();
		super.add(opcode.getValue());
	}

	/**
	 * Creates a packet from a string of bytes
	 * 
	 * @param data
	 *            the bytes
	 */
	public Packet(byte[] data) {
		super(data.length);
		for (byte b : data) {
			super.add(b);
		}
	}

	/**
	 * Adds a byte to the packet
	 * 
	 * @param b
	 *            the byte
	 */
	public void add(byte b) {
		super.add(b);
	}

	/**
	 * Adds an int to the packet
	 * 
	 * @param i
	 *            the int
	 */
	public void addInt(int i) {
		super.add((byte) (i & 0xFF));
		super.add((byte) ((i >>> 8) & 0xFF));
		super.add((byte) ((i >>> 16) & 0xFF));
		super.add((byte) ((i >>> 24) & 0xFF));
	}

	/**
	 * Adds a string of bytes to the packet
	 * 
	 * @param a
	 *            the bytes
	 */
	public void add(byte[] a) {
		for (int i = 0; i < a.length; i++) {
			super.add(a[i]);
		}
	}

	/**
	 * Adds a String to the packet
	 * 
	 * @param s
	 *            the String
	 */
	public void add(String s) {
		byte[] data = s.getBytes();
		addInt(data.length);
		add(data);
	}

	/**
	 * Adds a boolean to the packet
	 * 
	 * @param b
	 *            the boolean
	 */
	public void add(boolean b) {
		add((byte) (b ? 1 : 0));
	}

	/**
	 * Adds a card to the packet
	 * 
	 * @param c
	 *            the card
	 */
	public void addCard(Card c) {
		add((byte) c.getRank());
		add((byte) c.getSuit().ordinal());
	}

	/**
	 * Adds a group to the packet
	 * 
	 * @param group
	 *            the group
	 */
	public void addGroup(Group group) {
		add((byte) group.getRank());
		add((byte) group.getId());
		addInt(group.getCards().size());
		for (Card c : group) {
			addCard(c);
		}
	}

	/**
	 * Converts this packet to a string of bytes to be sent over a socket
	 * 
	 * @return the byte array
	 */
	public byte[] toByteArray() {
		byte[] ret = new byte[size()];
		for (int i = 0; i < size(); i++) {
			ret[i] = get(i);
		}
		return ret;
	}

	/**
	 * Gets the opcode of this packet (the first byte)
	 * 
	 * @return the opcode
	 */
	public byte getOpcode() {
		return get(0);
	}

	/**
	 * Gets a boolean from the packet
	 * 
	 * @return the boolean
	 */
	public boolean getBoolean() {
		byte b = getByte();
		return b == 1;
	}

	/**
	 * Gets a String from the packet
	 * 
	 * @return the String
	 */
	public String getString() {
		int size = getInt();
		int termination = size + position;
		byte[] temp = new byte[size];
		int i = 0;
		while (position < termination) {
			temp[i] = get(position);
			position++;
			i++;
		}
		return new String(temp);
	}

	/**
	 * Gets a card from the packet
	 * 
	 * @return the card
	 */
	public Card getCard() {
		Card ret;
		int rank = getByte();
		Suit suit = Suit.values()[getByte()];
		ret = new Card(rank, suit);
		return ret;
	}

	/**
	 * Gets a group from the packet
	 * 
	 * @return the group
	 */
	public Group getGroup() {
		Group ret;
		int rank = getByte();
		int id = getByte();
		ret = new Group(rank, id);
		int size = getInt();
		List<Card> temp = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			temp.add(getCard());
		}
		if (Group.Result.SUCCESS != ret.addCards(temp)) {
			throw new RuntimeException("Failed to construct group from packet");
		}
		return ret;
	}

	/**
	 * Gets a byte from the packet
	 * 
	 * @return the byte
	 */
	public byte getByte() {
		return get(position++);
	}

	/**
	 * Gets an int from the packet
	 * 
	 * @return the int
	 */
	public int getInt() {
		int b1 = get(position++);
		int b2 = get(position++) << 8;
		int b3 = get(position++) << 16;
		int b4 = get(position++) << 24;
		return b1 + b2 + b3 + b4;
	}
}