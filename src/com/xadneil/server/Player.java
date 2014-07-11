package com.xadneil.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.xadneil.client.Card;
import com.xadneil.client.net.Packet;
import com.xadneil.server.net.SocketReader;

/**
 * Class representing a connection to a player and his hand and foot data
 * 
 * @author Daniel
 */
public class Player {

	private final SocketReader socketReader;
	private final int number;
	private List<Card> hand, foot;
	private boolean inFoot = false;

	/**
	 * Class Constructor
	 * 
	 * @param socket
	 *            the socket connection to the player
	 * @param number
	 *            the player number (0 to max - 1)
	 */
	public Player(Socket socket, int number) {
		this.socketReader = new SocketReader(socket, this);
		this.number = number;
		resetCards();
	}

	/**
	 * Begins listening to the player socket and responding
	 */
	public void startListening() {
		socketReader.start();
	}

	/**
	 * Gets the player's foot if it exists, or null if not.
	 * 
	 * @return the player's foot
	 */
	public List<Card> getFoot() {
		return foot;
	}

	/**
	 * Gets the hand from which the player is currently playing
	 * 
	 * @return the current hand
	 */
	public List<Card> getHand() {
		return hand;
	}

	/**
	 * Gets the player number for this player (0 to max - 1)
	 * 
	 * @return the player number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Closes the connection to the player. Generally irreversible.
	 */
	public void disconnect() {
		socketReader.close();
	}

	/**
	 * Sends a packet to the player
	 * 
	 * @param packet
	 *            the packet to send
	 */
	public void send(Packet packet) {
		try {
			socketReader.getSocket().getOutputStream()
					.write(packet.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets whether or not the player has finished playing his hand and moved on
	 * to his foot. (Players in their foot have their foot moved to their hand)
	 * 
	 * @return whether or not the player is in his foot
	 */
	public boolean isInFoot() {
		return inFoot;
	}

	/**
	 * Signals that the player has finished his hand and is now moving into his
	 * foot
	 * 
	 * @see com.xadneil.server.Player#isInFoot()
	 */
	public void setInFoot() {
		this.inFoot = true;
		hand = foot;
		foot = null;
	}

	/**
	 * Resets current hand, foot, and in-foot status, and creates an empty hand
	 * and foot.
	 */
	public void resetCards() {
		hand = new ArrayList<>();
		foot = new ArrayList<>();
		inFoot = false;
	}
}