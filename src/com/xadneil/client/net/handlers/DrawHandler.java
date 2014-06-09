package com.xadneil.client.net.handlers;

import java.net.Socket;

import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketHandler;


public class DrawHandler implements PacketHandler {

    @Override
    public void handlePacket(Packet packet, Socket s, Main game) {
        game.getHand().add(packet.getCard());
        game.getHand().add(packet.getCard());
        game.redraw();
    }
}