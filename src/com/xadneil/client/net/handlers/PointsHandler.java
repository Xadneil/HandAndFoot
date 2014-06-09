package com.xadneil.client.net.handlers;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.xadneil.client.Card;
import com.xadneil.client.Group;
import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketCreator;
import com.xadneil.client.net.PacketHandler;


public class PointsHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Socket s, Main game) {
		int answer = JOptionPane.showConfirmDialog(game.gameFrame,
				"Not enough points to meld. You need " + packet.getInt()
						+ ". Would you like to reset the board?",
				"Not Enough Points", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			// put board cards back in hand
			for (ArrayList<Group> groups : game.getBoard().values()) {
				for (Group g : groups)
					for (Card c : g)
						game.getHand().add(c);
			}
			game.getBoard().clear();
			game.redraw();
			// tell the server we are resetting the board
			try {
				s.getOutputStream().write(PacketCreator.points().toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
