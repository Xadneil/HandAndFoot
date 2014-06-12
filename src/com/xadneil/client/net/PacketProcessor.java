package com.xadneil.client.net;

import com.xadneil.client.net.handlers.*;

/**
 * Class for assigning receive opcodes to packet handlers, storing, and
 * accessing packet handlers for use
 * 
 * @author Daniel
 */
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
		registerHandler(RecvOpcode.WRONG_TURN, new WrongTurnHandler());
		registerHandler(RecvOpcode.DRAW7, new Draw7Handler());
		registerHandler(RecvOpcode.GAME_START, new GameStartHandler());
		registerHandler(RecvOpcode.YOUR_TURN, new YourTurnHandler());
		registerHandler(RecvOpcode.DISPLAY_TURN, new DisplayTurnHandler());
		registerHandler(RecvOpcode.PLAY, new PlayHandler());
		registerHandler(RecvOpcode.POINTS, new PointsHandler());
		registerHandler(RecvOpcode.END_ROUND, new EndRoundHandler());
		registerHandler(RecvOpcode.END_REQUIREMENTS,
				new EndRequirementsHandler());
		registerHandler(RecvOpcode.PLAY_OTHER, new OtherPlayHandler());
		registerHandler(RecvOpcode.CLEAR, new ClearHandler());
		registerHandler(RecvOpcode.LOGIN, new LoginHandler());
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
		PacketHandler handler = handlers[opcode];
		if (handler != null) {
			return handler;
		}
		return null;
	}

	private void registerHandler(RecvOpcode code, PacketHandler handler) {
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