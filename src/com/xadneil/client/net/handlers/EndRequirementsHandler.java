package com.xadneil.client.net.handlers;

import java.net.Socket;

import javax.swing.JOptionPane;

import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketHandler;


public class EndRequirementsHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Socket s, Main game) {
		int numClean = packet.getInt();
		int numDirty = packet.getInt();
		JOptionPane
				.showMessageDialog(
						game.gameFrame,
						"You cannot make that play. To go out, you must have at "
								+ "least 2 clean and 2 dirty groups. You currently have "
								+ numClean + " and " + numDirty + ".", "Error",
						JOptionPane.ERROR_MESSAGE);
	}
}
