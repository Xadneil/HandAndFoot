package com.xadneil.server.net;

import com.xadneil.server.net.handlers.*;

public class PacketProcessor {

    private static PacketProcessor instance;
    private PacketHandler[] handlers;

    private PacketProcessor() {
        int maxRecvOp = 0;
        for (RecvOpcode op : RecvOpcode.values()) {
            if (op.getValue() > maxRecvOp) {
                maxRecvOp = op.getValue();
            }
        }
        handlers = new PacketHandler[maxRecvOp + 1];
        registerHandler(RecvOpcode.DRAW, new DrawHandler());
        registerHandler(RecvOpcode.DISCARD, new DiscardHandler());
        registerHandler(RecvOpcode.DRAW7, new Draw7Handler());
        registerHandler(RecvOpcode.PLAY_CARD, new PlayCardHandler());
        registerHandler(RecvOpcode.PLAY_GROUP, new PlayGroupHandler());
        registerHandler(RecvOpcode.LOGIN, new LoginHandler());
        registerHandler(RecvOpcode.POINTS, new PointsHandler());
    }

    public PacketHandler getHandler(byte packetId) {
        if (packetId > handlers.length) {
            return null;
        }
        PacketHandler handler = handlers[packetId];
        if (handler != null) {
            return handler;
        }
        return null;
    }

    public void registerHandler(RecvOpcode code, PacketHandler handler) {
        try {
            handlers[code.getValue()] = handler;
        } catch (ArrayIndexOutOfBoundsException e) {
        	e.printStackTrace();
        	System.exit(1);
        }
    }

    public synchronized static PacketProcessor getProcessor() {
        if (instance == null) {
            instance = new PacketProcessor();
        }
        return instance;
    }
}