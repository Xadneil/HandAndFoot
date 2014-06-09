package com.xadneil.client.net.handlers;

import java.net.Socket;

import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketHandler;

public class ClearHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Socket s, Main game) {
		if (packet.getBoolean()) { // clear my board
			game.getBoard().clear();
		} else { // clear enemy's board
			game.getEnemyBoard().clear();
		}
		game.redraw();
	}
}
