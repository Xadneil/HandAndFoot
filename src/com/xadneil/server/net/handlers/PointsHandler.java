package com.xadneil.server.net.handlers;

import java.util.ArrayList;

import com.xadneil.client.Card;
import com.xadneil.client.Group;
import com.xadneil.client.net.Packet;
import com.xadneil.server.Player;
import com.xadneil.server.Server;
import com.xadneil.server.net.PacketHandler;

public class PointsHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Player player, Server server) {
		for (ArrayList<Group> l : server.getBoard().values()) {
			for (Group g : l)
				for (Card c : g) {
					player.getHand().add(c);
				}
		}
		server.clearBoard(player.getNumber() % 2);
	}
}
