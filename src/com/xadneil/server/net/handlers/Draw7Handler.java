package com.xadneil.server.net.handlers;

import com.xadneil.client.net.Packet;
import com.xadneil.server.Player;
import com.xadneil.server.Server;
import com.xadneil.server.net.PacketHandler;
import com.xadneil.server.net.SendOpcode;

public class Draw7Handler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Player player) {
		if (player.getNumber() != Server.getInstance().getTurn()) {
			player.send(new Packet(SendOpcode.WRONG_TURN));
		} else {
			Server.getInstance().pickUpDiscard();
		}
	}
}