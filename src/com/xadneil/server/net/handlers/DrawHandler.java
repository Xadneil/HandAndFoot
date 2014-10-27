package com.xadneil.server.net.handlers;

import com.xadneil.client.net.Packet;
import com.xadneil.server.Player;
import com.xadneil.server.Server;
import com.xadneil.server.net.PacketHandler;
import com.xadneil.server.net.SendOpcode;

public class DrawHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Player player, Server server) {
		if (player.getNumber() != server.getTurn()) {
			player.send(new Packet(SendOpcode.WRONG_TURN));
		} else {
			server.draw();
		}
	}
}