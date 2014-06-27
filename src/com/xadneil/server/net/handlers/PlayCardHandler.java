package com.xadneil.server.net.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.xadneil.client.Card;
import com.xadneil.client.Group;
import com.xadneil.client.net.Packet;
import com.xadneil.server.Player;
import com.xadneil.server.Server;
import com.xadneil.server.net.PacketCreator;
import com.xadneil.server.net.PacketHandler;
import com.xadneil.server.net.SendOpcode;

public class PlayCardHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Player player) {
		if (player.getNumber() != Server.getInstance().getTurn()) {
			player.send(new Packet(SendOpcode.WRONG_TURN));
		}
		int id = packet.getInt();
		int rank = packet.getInt();
		Card c = packet.getCard();

		if (player.isInFoot() && player.getHand().size() == 2) {
			HashMap<Integer, ArrayList<Group>> board = Server.getInstance()
					.copyBoard();
			Group.Result res = board.get(rank).get(id)
					.addCards(Arrays.asList(c));
			if (res == Group.Result.SUCCESS) {
				// check game is allowed to end
				int numClean = 0, numDirty = 0;
				for (ArrayList<Group> l : board.values()) {
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
					return;
				}
			}
		}
		Group.Result res = Server.getInstance().getBoard().get(rank)
				.get(id).addCards(Arrays.asList(c));
		// TODO Still some discontinuity here (fixed?)
		boolean success = res == Group.Result.SUCCESS;

		if (success) {
			boolean sanity = player.getHand().remove(c);
			if (!sanity)
				throw new RuntimeException("Could not play single card");
			Server.getInstance().checkAndSendFoot();
		}
		player.send(PacketCreator.play(true, success,
				success ? id : res.ordinal()));
		if (success) {
			Server.getInstance().playOthers(c, rank, id);
		}
	}
}
