package com.xadneil.client.net.handlers;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JOptionPane;

import com.xadneil.client.Card;
import com.xadneil.client.Group;
import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketCreator;
import com.xadneil.client.net.PacketHandler;

public class Draw7Handler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Socket s, Main game) {
		if (!packet.getBoolean()) { // success
			JOptionPane.showMessageDialog(game.gameFrame,
					"You cannot do that.", "Error", JOptionPane.ERROR_MESSAGE);
			game.setButtons(true);
			return;
		}
		int rank = 0;
		Group group = null;
		for (int i = 0; i < 7; i++) {
			Card temp = packet.getCard();
			if (i == 0) {
				rank = temp.getRank();
				int id = 0;
				if (game.getBoard().containsKey(rank)) {
					id = game.getBoard().get(rank).size();
				}
				group = new Group(rank, id);
				group.addCards(Collections.singletonList(temp));
			} else {
				game.getHand().add(temp);
			}
		}
		if (group == null) {
			throw new RuntimeException("Error Constructing Draw7 Group");
		}
		int i = 0;
		for (Iterator<Card> it = game.getHand().iterator(); it.hasNext();) {
			if (i >= 2) {
				break;
			}
			Card c = it.next();
			if (c.getRank() == rank) {
				group.addCards(Collections.singletonList(c));
				it.remove();
				i++;
			}
		}
		game.setStaging(group);
		try {
			s.getOutputStream().write(PacketCreator.play(group).toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		game.redraw();
	}
}