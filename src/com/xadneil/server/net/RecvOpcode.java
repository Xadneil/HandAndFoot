package com.xadneil.server.net;

import com.xadneil.client.net.Opcode;

/**
 * Opcodes that are sent by the client and received by the server
 * 
 * @author Daniel
 */
public enum RecvOpcode implements Opcode {

	DRAW(0),
	DISCARD(1),
	DRAW7(2),
	PING(3),
	PLAY_GROUP(4),
	PLAY_CARD(5),
	LOGIN(6),
	END_TURN(7),
	POINTS(8);

	RecvOpcode(int value) {
		this.value = value;
	}

	private final int value;

	@Override
	public byte getValue() {
		return (byte) value;
	}
}