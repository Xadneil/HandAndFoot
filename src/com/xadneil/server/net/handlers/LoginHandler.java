package com.xadneil.server.net.handlers;

import com.xadneil.client.net.Packet;
import com.xadneil.server.Login;
import com.xadneil.server.Player;
import com.xadneil.server.Server;
import com.xadneil.server.net.PacketHandler;

public class LoginHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Player player) {
		String username = packet.getString();
		String password = packet.getString();
		if (Login.check(username, password)) {
			Server.getInstance().setName(username, player.getNumber());
		}
	}
}
