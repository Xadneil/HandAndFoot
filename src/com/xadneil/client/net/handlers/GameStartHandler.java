package com.xadneil.client.net.handlers;

import java.net.Socket;

import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketHandler;


public class GameStartHandler implements PacketHandler {

    @Override
    public void handlePacket(Packet packet, Socket s, Main game) {
        for (int i = 0; i < 11; i++) {
            game.getHand().add(packet.getCard());
        }
        game.redraw();
    }
}