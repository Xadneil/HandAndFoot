package com.xadneil.server.net.handlers;

import java.util.ArrayList;

import com.xadneil.client.Group;
import com.xadneil.client.net.Packet;
import com.xadneil.server.Player;
import com.xadneil.server.Server;
import com.xadneil.server.net.PacketCreator;
import com.xadneil.server.net.PacketHandler;
import com.xadneil.server.net.SendOpcode;

public class DiscardHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Player player, Server server) {
		if (player.getNumber() != server.getTurn()) {
			player.send(new Packet(SendOpcode.WRONG_TURN));
		} else {
			if (!server.isDown()) {
				int score = 0;
				for (ArrayList<Group> l : server.getBoard().values()) {
					for (Group g : l)
						score += g.getScore();
				}
				if (score >= server.getPointsToMeld()) {
					server.goDown();
				} else if (score != 0) {
					// meaning they tried to meld (cards on board) but failed
					player.send(PacketCreator.points(server.getPointsToMeld()));
					return;
				}
			} else if (player.getHand().size() == 1 && player.getFoot() == null) {
				// check game is allowed to end
				int numClean = 0, numDirty = 0;
				for (ArrayList<Group> l : server.getBoard().values()) {
					for (Group g : l) {
						if (g.getCards().size() >= 7) {
							if (g.isClean())
								numClean++;
							else
								numDirty++;
						}
					}
				}
				if (numClean < 2 || numDirty < 2) {
					player.send(PacketCreator.endRequirements(numClean,
							numDirty));
				}
			}
			server.discard(packet.getCard());
		}
	}
}