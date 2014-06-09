package com.xadneil.server.net.handlers;

import java.util.ArrayList;
import java.util.HashMap;

import com.xadneil.client.Card;
import com.xadneil.client.Group;
import com.xadneil.client.net.Packet;
import com.xadneil.server.Player;
import com.xadneil.server.Server;
import com.xadneil.server.net.PacketCreator;
import com.xadneil.server.net.PacketHandler;
import com.xadneil.server.net.SendOpcode;

public class PlayGroupHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Player player) {
		if (player.getNumber() != Server.getInstance().getTurn()) {
			player.send(new Packet(SendOpcode.WRONG_TURN));
			return;
		}
		Group group = packet.getGroup();
		if (group.getCards().size() < 3) {
			player.send(PacketCreator.play(false, false,
					Group.Result.NOT_ENOUGH.ordinal()));
			return;
		}
		if (player.isInFoot()
				&& player.getHand().size() == 1 + group.getCards().size()) {
			HashMap<Integer, ArrayList<Group>> board = Server.getInstance()
					.copyBoard();
			if (!board.containsKey(group.getRank())) {
				board.put(group.getRank(), new ArrayList<Group>());
			}
			board.get(group.getRank()).add(group);

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
				player.send(PacketCreator.endRequirements(numClean, numDirty));
				return;
			}
		}
		for (Card c : group) {
			player.getHand().remove(c);
		}
		if (!Server.getInstance().getBoard().containsKey(group.getRank())) {
			Server.getInstance().getBoard()
					.put(group.getRank(), new ArrayList<Group>());
		}
		int id = Server.getInstance().getBoard().get(group.getRank()).size();
		group.setId(id);
		Server.getInstance().getBoard().get(group.getRank()).add(group);
		player.send(PacketCreator.play(false /* group */, true /* success */, id));
		Server.getInstance().checkAndSendFoot();
		Server.getInstance().playOthers(group);
	}
}