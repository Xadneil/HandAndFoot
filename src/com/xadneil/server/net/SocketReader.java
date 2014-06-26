package com.xadneil.server.net;

import java.io.IOException;
import java.net.Socket;

import com.xadneil.client.net.Packet;
import com.xadneil.server.Player;

/**
 * Class for receiving and sending packets to a player
 * 
 * @author Daniel
 */
public class SocketReader extends Thread {

	private final Socket socket;
	private boolean active = false;
	private final Player player;

	/**
	 * Class Constructor
	 * 
	 * @param s
	 *            the player's socket
	 * @param player
	 *            the player associated with this reader
	 */
	public SocketReader(Socket s, Player player) {
		super(s.getInetAddress().getHostAddress() + " reader");
		this.socket = s;
		this.player = player;
	}

	/**
	 * Gets the socket that is being read
	 * 
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	@Override
	public void start() {
		active = true;
		super.start();
	}

	/**
	 * Stops reading the socket
	 */
	public void close() {
		active = false;
		try {
			socket.close();
		} catch (IOException ex) {
		}
	}

	@Override
	public void run() {
		while (active) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			try {
				bytesRead = socket.getInputStream().read(buffer);
			} catch (IOException ex) {
				close();
				break;
			}
			if (bytesRead != -1) {
				byte[] smaller = new byte[bytesRead];
				System.arraycopy(buffer, 0, smaller, 0, bytesRead);
				delegatePacket(new Packet(smaller));
			} else {
				System.out.println("Socket stream closed.");
				close();
			}
		}
	}

	/**
	 * Create a thread to handle a received packet
	 * 
	 * @param packet
	 *            the packet to handle
	 */
	private void delegatePacket(final Packet packet) {
		new Thread(socket.getInetAddress().getHostAddress() + " processor") {

			@Override
			public void run() {
				PacketHandler h = PacketProcessor.getProcessor().getHandler(
						packet.getOpcode());
				if (h != null)
					try {
						h.handlePacket(packet, player);
					} catch (Exception e) {
						e.printStackTrace();
					}
				else
					System.out.println("Unknown opcode " + packet.getOpcode());
			}
		}.start();
	}
}