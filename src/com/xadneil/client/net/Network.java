package com.xadneil.client.net;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import com.xadneil.client.Main;

/**
 * Class for receiving and sending packets to the server
 * 
 * @author Daniel
 */
public class Network extends Thread {

	private final Socket socket;
	private boolean active = false;
	private final Main game;

	/**
	 * Class Constructor
	 * 
	 * @param s
	 *            the socket to the server
	 * @param game
	 *            the game to relay messages
	 */
	public Network(Socket s, Main game) {
		super("Socket reader");
		this.socket = s;
		this.game = game;
	}

	/**
	 * Get the raw socket to the server
	 * 
	 * @return
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
	 * Stop the socket receiver
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
		final byte[] buffer = new byte[4096];
		while (active) {
			final int bytesRead;
			try {
				bytesRead = socket.getInputStream().read(buffer);
			} catch (IOException ex) {
				close();
				break;
			}
			if (bytesRead != -1) {
				byte[] smaller = new byte[bytesRead];
				System.arraycopy(buffer, 0, smaller, 0, bytesRead);
				delegatePacket(new Packet(smaller), socket);
			} else {
				close();
				JOptionPane.showMessageDialog(game.gameFrame,
						"The server has teminated the connection.");
				game.killSurface();
			}
		}
	}

	/**
	 * Create a new thread to handle a packet
	 * 
	 * @param packet
	 *            the packet to handle
	 * @param s
	 *            the socket for communication back to the server
	 */
	private void delegatePacket(final Packet packet, final Socket s) {
		new Thread(packet.getOpcode() + " processor") {
			@Override
			public void run() {
				PacketHandler h = PacketProcessor.instance().getHandler(
						packet.getOpcode());
				if (h != null)
					h.handlePacket(packet, s, game);
				else
					System.out.println("Unknown opcode " + packet.getOpcode());
			}
		}.start();
	}

	/**
	 * Sends a packet to the server
	 * 
	 * @param packet
	 *            the packet
	 */
	public void send(Packet packet) {
		try {
			socket.getOutputStream().write(packet.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}