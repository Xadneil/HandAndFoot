package com.xadneil.client.net.handlers;

import java.net.Socket;

import com.xadneil.client.Card;
import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketHandler;

public class DiscardHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Socket s, Main game) {
		Card d = packet.getCard();
		game.setDiscard(d.getRank() == 0 ? null : d);
		game.doDiscard();
		game.redraw();
	}
}