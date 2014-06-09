package com.xadneil.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * A "discovery" server. Responds to UDP probe packets, telling of a server
 * hosted here. Should be started as a new Thread.
 * 
 * @author Daniel
 */
public class Discovery implements Runnable {

	private DatagramSocket socket;
	private static final int DISCOVERY_PORT = 17246;

	@Override
	public void run() {
		try {
			socket = new DatagramSocket(DISCOVERY_PORT,
					InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			byte buffer[] = new byte[1024];
			while (!socket.isClosed()) {
				DatagramPacket receive = new DatagramPacket(buffer,
						buffer.length);
				socket.receive(receive);
				String message = new String(receive.getData()).trim();
				if (message.equals("HF_DISCOVERY_REQUEST")) {
					System.out.println("Responding to discover");
					byte[] data = ("HF_DISCOVERY_RESPONSE " + Server.PORT + " Hand and Foot Game")
							.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(data, 0,
							data.length, receive.getAddress(),
							receive.getPort());
					socket.send(sendPacket);
				}
				buffer = new byte[1024];
			}
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// ignore
		}
	}

	public void close() {
		if (socket != null) {
			socket.close();
		}
	}
}
