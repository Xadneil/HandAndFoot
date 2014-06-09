package com.xadneil.client.net.handlers;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import com.xadneil.client.Card;
import com.xadneil.client.Group;
import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketHandler;

public class PlayHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Socket s, Main game) {
		boolean isCard = packet.getBoolean(); // play card or group
		if (packet.getBoolean()) { // success
			if (isCard) {
				int id = packet.getInt();
				Card c = game.getPending();
				Group.Result res = game.getBoard().get(c.getRank()).get(id)
						.addCards(Arrays.asList(c));
				if (res != Group.Result.SUCCESS) {
					throw new RuntimeException(
							"Failed to play single card. Card: " + c + ", id: "
									+ id);
				}
				game.getHand().remove(c);
				game.setPending(null);
				game.redraw();
			} else {
				int id = packet.getInt();
				Group toPlay = game.getStaging();
				toPlay.setId(id);
				if (!game.getBoard().containsKey(toPlay.getRank())) {
					if (id != 0) {
						throw new RuntimeException("Invalid first group ID ("
								+ id + ")");
					}
					game.getBoard().put(toPlay.getRank(),
							new ArrayList<Group>());
				}
				game.getBoard().get(toPlay.getRank()).add(toPlay);
				game.setStaging(null);
				game.redraw();
			}
		} else {
			int ordinal = packet.getInt();
			Group.Result res = Group.Result.values()[ordinal];
			JOptionPane.showMessageDialog(game.gameFrame, res.message, "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
