package com.xadneil.client.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.xadneil.client.LocalGame;

public class Discovery {
	private static final int DISCOVERY = 17246;

	public static void run(final LocalGame dialog) {
		new Thread("Discovery Send") {
			@Override
			public void run() {
				DatagramSocket socket = null;
				byte[] data = "HF_DISCOVERY_REQUEST".getBytes();
				DatagramPacket packet;
				try {
					packet = new DatagramPacket(data, 0, data.length,
							InetAddress.getByName("255.255.255.255"), DISCOVERY);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					return;
				}
				try {
					socket = new DatagramSocket();
					socket.setBroadcast(true);
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
					if (socket != null && !socket.isClosed())
						socket.close();
					return;
				}
				byte buffer[] = new byte[1024];
				DatagramPacket receive = new DatagramPacket(buffer,
						buffer.length);

				List<GameDetails> games = new ArrayList<>();
				try {
					socket.setSoTimeout(2000);
					while (true) {
						socket.receive(receive);
						String message = new String(receive.getData()).trim();
						if (message.startsWith("HF_DISCOVERY_RESPONSE")) {
							String sPort = message.substring(message
									.indexOf(' ') + 1);
							String title = sPort
									.substring(sPort.indexOf(' ') + 1);
							sPort = sPort.substring(0, sPort.indexOf(' '));
							int port = Integer.parseInt(sPort);
							games.add(new GameDetails(receive.getAddress(),
									port, title));
						}
					}
				} catch (SocketTimeoutException e) {
					dialog.setGames(games);
					socket.close();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					socket.close();
					return;
				}
			}
		}.start();
	}

	public static class GameDetails {
		public InetAddress address;
		public int port;
		public String title;

		public GameDetails(InetAddress address, int port, String title) {
			this.address = address;
			this.port = port;
			this.title = title;
		}

		@Override
		public String toString() {
			return address.getHostAddress() + ":" + port + " " + title;
		}
	}
}
