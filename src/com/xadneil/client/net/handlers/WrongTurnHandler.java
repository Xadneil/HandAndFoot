package com.xadneil.client.net.handlers;

import java.net.Socket;

import javax.swing.JOptionPane;

import com.xadneil.client.Main;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketHandler;


public class WrongTurnHandler implements PacketHandler {

    @Override
    public void handlePacket(Packet packet, Socket s, Main game) {
        JOptionPane.showMessageDialog(game.gameFrame, "It is not your turn.");
    }
}