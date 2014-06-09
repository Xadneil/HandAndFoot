package com.xadneil.client.net;

import java.net.Socket;

import com.xadneil.client.Main;

/**
 * Class for handling a received packet
 * 
 * @author Daniel
 */
public interface PacketHandler {

	/**
	 * Handles a packet
	 * 
	 * @param packet
	 *            the packet to handle
	 * @param s
	 *            the socket to the server
	 * @param game
	 *            the game to access and/or change
	 */
	public void handlePacket(Packet packet, Socket s, Main game);
}