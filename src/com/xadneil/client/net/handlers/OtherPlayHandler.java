package com.xadneil.client.net.handlers;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.xadneil.client.Card;
import com.xadneil.client.Group;
import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketHandler;

public class OtherPlayHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Socket s, Main game) {
		boolean isCard = packet.getBoolean(); // card or group
		Map<Integer, ArrayList<Group>> board;
		if (packet.getBoolean()) { // my partner
			board = game.getBoard();
		} else { // enemy team
			board = game.getEnemyBoard();
		}
		if (isCard) {
			Card c = packet.getCard();
			int rank = packet.getInt();
			int id = packet.getInt();
			board.get(rank).get(id).addCards(Arrays.asList(c));
		} else {
			Group g = packet.getGroup();
			if (!board.containsKey(g.getRank())) {
				board.put(g.getRank(), new ArrayList<Group>());
			}
			if (board.get(g.getRank()).size() != g.getId()) {
				throw new RuntimeException("Group ID Mismatch");
			}
			board.get(g.getRank()).add(g);
		}
		game.redraw();
	}
}
