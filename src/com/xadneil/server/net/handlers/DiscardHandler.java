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
	public void handlePacket(Packet packet, Player player) {
		if (player.getNumber() != Server.getInstance().getTurn()) {
			player.send(new Packet(SendOpcode.WRONG_TURN));
		} else {
			if (!Server.getInstance().isDown()) {
				int score = 0;
				for (ArrayList<Group> l : Server.getInstance().getBoard()
						.values()) {
					for (Group g : l)
						score += g.getScore();
				}
				if (score >= Server.getInstance().getPointsToMeld()) {
					Server.getInstance().goDown();
				} else if (score != 0) {
					// meaning they tried to meld (cards on board) but failed
					player.send(PacketCreator.points(Server.getInstance()
							.getPointsToMeld()));
					return;
				}
			} else if (player.getHand().size() == 1 && player.getFoot() == null) {
				// check game is allowed to end
				int numClean = 0, numDirty = 0;
				for (ArrayList<Group> l : Server.getInstance().getBoard()
						.values()) {
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
			Server.getInstance().discard(packet.getCard());
		}
	}
}