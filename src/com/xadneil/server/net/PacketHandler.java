package com.xadneil.server.net;

import com.xadneil.client.net.Packet;
import com.xadneil.server.Player;
import com.xadneil.server.Server;

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
	 * @param player
	 *            the player that the packet came from
	 */
	public void handlePacket(Packet packet, Player player, Server server);
}