package com.xadneil.client.net.handlers;

import java.net.Socket;

import javax.swing.JOptionPane;

import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketHandler;


public class EndRoundHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Socket s, Main game) {
		int my = packet.getInt();
		int yours = packet.getInt();
		int round = packet.getInt();
		JOptionPane.showMessageDialog(game.gameFrame,
				"Round " + round + " over. Your team score: " + my + ". Other team score: "
						+ yours + ".");
		game.setButtons(false);
		game.setRound(round);
	}
}
