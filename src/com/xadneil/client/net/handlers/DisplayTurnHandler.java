package com.xadneil.client.net.handlers;

import java.net.Socket;

import javax.swing.JOptionPane;

import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketHandler;

public class DisplayTurnHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Socket s, Main game) {
		game.setTurnName(packet.getString());
		if (packet.getBoolean()) { // my turn
			JOptionPane.showMessageDialog(game.gameFrame, "It is your turn now.");
	        game.setButtons(true);
		}
		game.redraw();
	}
}