package com.xadneil.server.net.handlers;

import com.xadneil.client.net.Packet;
import com.xadneil.server.net.PacketCreator;
import com.xadneil.server.Player;
import com.xadneil.server.Server;
import com.xadneil.server.net.PacketHandler;

public class LoginHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Player player) {
		String username = packet.getString();
		boolean success = Server.getInstance().setName(username,
				player.getNumber());
		player.send(PacketCreator.login(success));
	}
}
