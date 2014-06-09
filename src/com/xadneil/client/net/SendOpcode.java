package com.xadneil.client.net;

public enum SendOpcode implements Opcode {

	DRAW(0),
	DISCARD(1),
	DRAW7(2),
	PING(3),
	PLAY_GROUP(4),
	PLAY_CARD(5),
	LOGIN(6),
	END_TURN(7),
	POINTS(8);

	SendOpcode(int value) {
		this.value = value;
	}

	private final int value;

	@Override
	public byte getValue() {
		return (byte) value;
	}
}