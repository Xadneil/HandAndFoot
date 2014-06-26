package com.xadneil.client.net;

/**
 * Opcode interface. An opcode is the first byte in a received or sent packet,
 * indicating what operation the packet is associated with.
 * 
 * @author Daniel
 * @see com.xadneil.client.net.SendOpcode
 * @see com.xadneil.client.net.RecvOpcode
 * @see com.xadneil.server.net.SendOpcode
 * @see com.xadneil.server.net.RecvOpcode
 */
public interface Opcode {
	/**
	 * Gets the byte value of the opcode
	 * 
	 * @return the opcode value
	 */
	public byte getValue();
}
