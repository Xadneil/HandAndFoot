package com.xadneil.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.xadneil.client.Card;
import com.xadneil.client.net.Packet;
import com.xadneil.server.net.SocketReader;

public class Player {

	private final SocketReader socket;
	private final int number;
	private List<Card> hand, foot;
	private boolean inFoot = false;

	public Player(Socket socket, int number) {
		this.socket = new SocketReader(socket, this);
		this.number = number;
		hand = new ArrayList<>();
		foot = new ArrayList<>();
	}

	public void startListening() {
		socket.start();
	}

	public List<Card> getFoot() {
		return foot;
	}

	public List<Card> getHand() {
		return hand;
	}

	public SocketReader getSocket() {
		return socket;
	}

	public int getNumber() {
		return number;
	}

	public void disconnect() {
		socket.close();
	}

	public void send(Packet packet) {
		try {
			getSocket().getSocket().getOutputStream()
					.write(packet.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isInFoot() {
		return inFoot;
	}

	public void setInFoot() {
		this.inFoot = true;
		hand = foot;
		foot = null;
	}
}