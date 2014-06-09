package com.xadneil.server.net;

import com.xadneil.client.net.Packet;
import com.xadneil.server.Player;

public interface PacketHandler {

    public void handlePacket(Packet packet, Player player);
}