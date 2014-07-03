package com.xadneil.server.net;

import com.xadneil.server.net.handlers.DiscardHandler;
import com.xadneil.server.net.handlers.Draw7Handler;
import com.xadneil.server.net.handlers.DrawHandler;
import com.xadneil.server.net.handlers.LoginHandler;
import com.xadneil.server.net.handlers.PlayCardHandler;
import com.xadneil.server.net.handlers.PlayGroupHandler;
import com.xadneil.server.net.handlers.PointsHandler;

/**
 * Class for assigning receive opcodes to packet handlers and storing and
 * accessing packet handlers for use
 * 
 * @author Daniel
 */
public class PacketProcessor {

	private static PacketProcessor instance;
	private PacketHandler[] handlers;

	private PacketProcessor() {
		// finds max opcode value from RecvOpcode.
		// this is done to eliminate any ordering requirements.
		int maxRecvOp = 0;
		for (RecvOpcode op : RecvOpcode.values()) {
			if (op.getValue() > maxRecvOp) {
				maxRecvOp = op.getValue();
			}
		}
		handlers = new PacketHandler[maxRecvOp + 1];

		// register all handlers
		register(RecvOpcode.DRAW, new DrawHandler());
		register(RecvOpcode.DISCARD, new DiscardHandler());
		register(RecvOpcode.DRAW7, new Draw7Handler());
		register(RecvOpcode.PLAY_CARD, new PlayCardHandler());
		register(RecvOpcode.PLAY_GROUP, new PlayGroupHandler());
		register(RecvOpcode.LOGIN, new LoginHandler());
		register(RecvOpcode.POINTS, new PointsHandler());
	}

	/**
	 * Gets the packet handler associated with the specified opcode
	 * 
	 * @param opcode
	 *            the opcode
	 * @return the packet handler
	 */
	public PacketHandler getHandler(byte opcode) {
		if (opcode > handlers.length) {
			return null;
		}
		return handlers[opcode];
	}

	private void register(RecvOpcode code, PacketHandler handler) {
		try {
			handlers[code.getValue()] = handler;
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Access the singleton packet processor
	 * 
	 * @return the packet processor
	 */
	public synchronized static PacketProcessor getProcessor() {
		if (instance == null) {
			instance = new PacketProcessor();
		}
		return instance;
	}
}