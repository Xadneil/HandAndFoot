package com.xadneil.client.net;

import com.xadneil.client.net.handlers.ClearHandler;
import com.xadneil.client.net.handlers.DiscardHandler;
import com.xadneil.client.net.handlers.DisplayTurnHandler;
import com.xadneil.client.net.handlers.Draw7Handler;
import com.xadneil.client.net.handlers.DrawHandler;
import com.xadneil.client.net.handlers.EndRequirementsHandler;
import com.xadneil.client.net.handlers.EndRoundHandler;
import com.xadneil.client.net.handlers.GameStartHandler;
import com.xadneil.client.net.handlers.LoginHandler;
import com.xadneil.client.net.handlers.OtherPlayHandler;
import com.xadneil.client.net.handlers.PlayHandler;
import com.xadneil.client.net.handlers.PointsHandler;
import com.xadneil.client.net.handlers.WrongTurnHandler;
import com.xadneil.client.net.handlers.YourTurnHandler;

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
		register(RecvOpcode.WRONG_TURN, new WrongTurnHandler());
		register(RecvOpcode.DRAW7, new Draw7Handler());
		register(RecvOpcode.GAME_START, new GameStartHandler());
		register(RecvOpcode.YOUR_TURN, new YourTurnHandler());
		register(RecvOpcode.DISPLAY_TURN, new DisplayTurnHandler());
		register(RecvOpcode.PLAY, new PlayHandler());
		register(RecvOpcode.POINTS, new PointsHandler());
		register(RecvOpcode.END_ROUND, new EndRoundHandler());
		register(RecvOpcode.END_REQUIREMENTS, new EndRequirementsHandler());
		register(RecvOpcode.PLAY_OTHER, new OtherPlayHandler());
		register(RecvOpcode.CLEAR, new ClearHandler());
		register(RecvOpcode.LOGIN, new LoginHandler());
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
	public synchronized static PacketProcessor instance() {
		if (instance == null) {
			instance = new PacketProcessor();
		}
		return instance;
	}
}