package com.xadneil.client.net;

public enum RecvOpcode implements Opcode {

	DRAW(0),
	DISCARD(1),
	WRONG_TURN(2),
	DRAW7(3),
	GAME_START(4),
	YOUR_TURN(5),
	DISPLAY_TURN(6),
	BOARD(9),
	PLAY(10),
	POINTS(11),
	END_ROUND(12),
	END_REQUIREMENTS(13),
	PLAY_OTHER(14),
	CLEAR(15),
	LOGIN(16);

	RecvOpcode(int value) {
		this.value = value;
	}

	private final int value;

	@Override
	public byte getValue() {
		return (byte) value;
	}
}