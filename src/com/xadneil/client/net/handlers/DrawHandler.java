package com.xadneil.client.net.handlers;

import java.net.Socket;

import com.xadneil.client.Card;
import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketHandler;

public class DrawHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Socket s, Main game) {
		Card c1 = packet.getCard();
		Card c2 = packet.getCard();
		game.getHand().add(c1);
		game.getHand().add(c2);
		game.redraw();
	}
}