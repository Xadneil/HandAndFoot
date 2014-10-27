package com.xadneil.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.xadneil.client.Card;
import com.xadneil.client.Group;
import com.xadneil.client.Main;
import com.xadneil.client.Suit;
import com.xadneil.client.net.Packet;
import com.xadneil.server.net.PacketCreator;

/**
 * A stateful server for coordinating a game of Hand and Foot.
 * <p>
 * <code>turn</code> is the main state element; certain methods return
 * differently depending on its value
 * 
 * @author Daniel
 */
public class Server {

	public static final int PORT = 5677;
	private int numPlayers;
	private Player[] players;
	private String[] names;
	/**
	 * 2 boards being stored (ArrayList), Each board (Hashmap) maps card rank
	 * (Integer) to a list of groups (ArrayList&lt;Group&gt;)
	 */
	private List<HashMap<Integer, ArrayList<Group>>> board = new ArrayList<HashMap<Integer, ArrayList<Group>>>(
			2);
	private boolean[] down = new boolean[2];
	/**
	 * Main state element: which player is playing now
	 */
	private int turn = 0;
	private int round = 0;
	/**
	 * Discard pile
	 */
	private List<Card> discard;
	/**
	 * Draw pile
	 */
	private LinkedList<Card> draw;
	private ServerSocket server;
	/**
	 * Team score
	 */
	private int[] score = new int[2];
	private PrintStream out = System.out;
	private Discovery discovery;
	private Thread discoveryThread;

	/**
	 * Class constructor
	 */
	public Server() {
		discard = new ArrayList<>();
		draw = new LinkedList<>();
		score[0] = 0;
		score[1] = 0;
	}

	/**
	 * Starts a round, shuffling and dealing cards, and sending start packets
	 */
	public void startRound() {
		// clear round-dependent data
		draw.clear();
		discard.clear();
		board.clear();
		board.add(new HashMap<Integer, ArrayList<Group>>());
		board.add(new HashMap<Integer, ArrayList<Group>>());
		down[0] = false;
		down[1] = false;
		for (Player p : players) {
			p.resetCards();
		}
		// create and shuffle draw pile
		for (int i = 0; i < 5; i++) {
			draw.addAll(new Deck());
		}
		Collections.shuffle(draw);
		// extract hands and feet for all players
		for (Player p : players) {
			for (int i = 0; i < 11; i++) {
				p.getFoot().add(draw.removeFirst());
				p.getHand().add(draw.removeFirst());
			}
		}
		// send players their starting hands
		for (int i = 0; i < numPlayers; i++) {
			players[i].send(PacketCreator.gameStart(players[i].getHand()));
		}
		// do initial turn handling
		players[0].send(PacketCreator.displayTurn(names[turn], true));
		sendExcept(PacketCreator.displayTurn(names[turn], false), players[0]);
		sendAll(PacketCreator.discardNotify(new Card(0, Suit.UNDEFINED)));
	}

	/**
	 * Gets the points needed to meld given the current round number
	 * 
	 * @return the points needed to meld
	 */
	public int getPointsToMeld() {
		switch (round) {
		case 0:
			return 50;
		case 1:
			return 90;
		case 2:
			return 120;
		case 3:
			return 150;
		default:
			return 0;
		}
	}

	/**
	 * Marks the current team as down (able to play any cards)
	 */
	public void goDown() {
		down[turn % 2] = true;
	}

	/**
	 * Sets a player's name for human identification. Game will start when all
	 * players have given a name.
	 * 
	 * @param name
	 *            player name
	 * @param number
	 *            player number
	 * @return success, if there is no duplicate
	 */
	public boolean setName(String name, int number) {
		names[number] = name;
		boolean start = true;
		int numDuplicates = 0;
		for (String n : names) {
			if (n == null) {
				start = false;
			} else if (n.equalsIgnoreCase(name)) {
				numDuplicates++;
			}
		}
		if (numDuplicates > 1) {
			return false;
		}
		if (start) {
			out.println("All names received. Starting game.");
			startRound();
		}
		return true;
	}

	/**
	 * Returns a copy of the current team's board for hypothetical modification
	 * 
	 * @return a copy of the current team's board
	 */
	public HashMap<Integer, ArrayList<Group>> copyBoard() {
		HashMap<Integer, ArrayList<Group>> old = board.get(turn % 2);
		HashMap<Integer, ArrayList<Group>> ret = new HashMap<>(old.size());
		for (Map.Entry<Integer, ArrayList<Group>> entry : old.entrySet()) {
			ret.put(entry.getKey(), new ArrayList<Group>());
			for (Group g : entry.getValue())
				ret.get(entry.getKey()).add(new Group(g));
		}
		return ret;
	}

	/**
	 * Helper class for starting the server as part of the client
	 * 
	 * @author Daniel
	 */
	public static class IntegratedServer {
		public Main game;
		public PrintStream stream;

		public IntegratedServer(Main game, PrintStream stream) {
			this.game = game;
			this.stream = stream;
		}
	}

	/**
	 * Starts a dedicated server. Accepts clients and starts discovery server
	 * 
	 * @param numPlayers
	 *            number of players
	 */
	public void host(int numPlayers) {
		host(numPlayers, null);
	}

	/**
	 * Starts an integrated server. Accepts clients and starts discovery server
	 * 
	 * @param numPlayers
	 *            number of players
	 * @param clientDetails
	 *            Client details if integrated server, null otherwise
	 */
	public void host(int numPlayers, final IntegratedServer clientDetails) {
		this.numPlayers = numPlayers;
		players = new Player[numPlayers];
		names = new String[numPlayers];
		// Redirect output stream if part of client
		if (clientDetails != null) {
			out = clientDetails.stream;
		}
		try {
			server = new ServerSocket(PORT);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(0);
		}
		new Thread("Acceptor") {
			@Override
			public void run() {
				out.println("Server online. Waiting for player connections...");
				// let client know it is OK to connect now
				if (clientDetails != null) {
					clientDetails.game.serverCallback(server.getInetAddress(),
							PORT);
				}

				// Accept players
				for (int i = 0; i < Server.this.numPlayers; i++) {
					if (server.isClosed()) {
						break;
					}
					final Socket s;
					try {
						s = server.accept(); // blocks
						s.setTcpNoDelay(true);
						out.println("Player " + (i + 1)
								+ " connected from address "
								+ s.getInetAddress().getHostAddress());
					} catch (IOException ex) {
						out.println("Error while accepting connection to player "
								+ (i + 1) + ". Retrying.");
						ex.printStackTrace();
						// don't increment i for retry
						i--;
						continue;
					}
					final int j = i;
					new Thread("Player " + j + " startup") {
						@Override
						public void run() {
							players[j] = new Player(s, j, Server.this);
						}
					}.start();
				}

				// done accepting players, close servers
				try {
					server.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				discovery.close();
				out.println("All players connected. Waiting for all names.");
				// game will not start until all players send names (async)
			}
		}.start();
		// start discovery server, where players can find local games
		discovery = new Discovery();
		discoveryThread = new Thread(discovery, "Discovery");
		discoveryThread.start();
	}

	/**
	 * Gets the server's current turn
	 * 
	 * @return the current turn
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * Draws 2 cards for the current player
	 */
	public void draw() {
		Card c1 = draw.removeFirst();
		Card c2 = draw.removeFirst();
		players[turn].getHand().add(c1);
		players[turn].getHand().add(c2);
		players[turn].send(PacketCreator.draw(c1, c2));
	}

	/**
	 * Ends the turn for the current player, begins next player's turn
	 */
	public void endTurn() {
		turn++;
		if (turn >= numPlayers) {
			turn = 0;
		}
		players[turn].send(PacketCreator.displayTurn(names[turn], true));
		sendExcept(PacketCreator.displayTurn(names[turn], false), players[turn]);
	}

	/**
	 * Gets the board for the current team
	 * 
	 * @return the board
	 */
	public Map<Integer, ArrayList<Group>> getBoard() {
		return board.get(turn % 2);
	}

	/**
	 * Gets the down state for the current team
	 * 
	 * @return the down state
	 */
	public boolean isDown() {
		return down[turn % 2];
	}

	/**
	 * Gets the card showing on top of the discard pile
	 * 
	 * @return the card
	 */
	public Card getTopDiscard() {
		if (!discard.isEmpty()) {
			return discard.get(0);
		}
		return null;
	}

	/**
	 * Does the discard of the current player, checks to send him his foot,
	 * checks for the end of the round, and signals the next turn.
	 * 
	 * @param card
	 *            the card to discard
	 */
	public void discard(Card card) {
		// remove card from hand
		if (players[turn].getHand().remove(card)) {
			discard.add(card);
			sendAll(PacketCreator.discardNotify(card));
		} else {
			out.println("Discard Error. Card " + card
					+ " was not in player's hand.");
			System.exit(1);
			return;
		}
		// send foot if empty
		checkAndSendFoot();

		// detect endgame
		if (players[turn].isInFoot() && players[turn].getHand().isEmpty()) {
			score[0] += endRoundScore(0);
			score[1] += endRoundScore(1);

			if (round < 4) {
				Packet packet;
				for (Player p : players) {
					boolean team = p.getNumber() % 2 == 0;
					// round + 1 for human readable
					packet = PacketCreator.endRound(team ? score[0] : score[1],
							team ? score[1] : score[0], round + 1);
					p.send(packet);
				}
				new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(8000);
						} catch (InterruptedException e) {
						}
						startRound();
					}
				}.start();
			}
			round++;
		} else {
			endTurn();
		}
	}

	/**
	 * If the necessary conditions are met, send the current player his foot
	 */
	public void checkAndSendFoot() {
		if (!players[turn].isInFoot() && players[turn].getHand().isEmpty()) {
			players[turn].setInFoot();
			// send player new hand
			players[turn]
					.send(PacketCreator.gameStart(players[turn].getHand()));
		}
	}

	/**
	 * Picks up 7 cards from the discard pile and gives them to the current
	 * player. Unexpected situations (not enough discards, player doesn't have 2
	 * matching cards, etc.) are handled.
	 */
	public void pickUpDiscard() {
		if (discard.size() < 7) {
			players[turn].send(PacketCreator.draw7(null, null));
			return;
		}
		int numCards = 0;
		int rank = discard.get(discard.size() - 1).getRank();
		for (Card c : players[turn].getHand()) {
			if (c.getRank() == rank) {
				numCards++;
			}
			if (numCards == 2) {
				break;
			}
		}
		if (numCards < 2) {
			players[turn].send(PacketCreator.draw7(null, null));
			return;
		}
		Card[] ret = new Card[7];
		for (int i = 0; i < 7; i++) {
			Card temp = discard.remove(discard.size() - 1);
			ret[i] = temp;
			players[turn].getHand().add(temp);
		}

		players[turn].send(PacketCreator.draw7(ret, getTopDiscard()));
		if (discard.isEmpty())
			sendAll(PacketCreator.discardNotify(new Card(0, Suit.UNDEFINED)));
		else
			sendAll(PacketCreator
					.discardNotify(discard.get(discard.size() - 1)));
	}

	/**
	 * Gets the score for the round that just finished
	 * 
	 * @param team
	 *            the team to score (0 or 1)
	 * @return the team's score
	 */
	public int endRoundScore(int team) {
		int ret = 0;
		for (ArrayList<Group> a : board.get(team).values()) {
			for (Group g : a) {
				ret += g.getScore();
			}
		}
		for (int i = 0; i < numPlayers; i++) {
			if (i % 2 != team)
				continue;
			for (Card c : players[i].getFoot()) {
				ret -= c.getPoints();
			}
			for (Card c : players[i].getHand()) {
				ret -= c.getPoints();
			}
		}
		return ret;
	}

	/**
	 * Sends a packet to all the players
	 * 
	 * @param p
	 *            the packet
	 */
	public void sendAll(Packet p) {
		for (Player player : players) {
			player.send(p);
		}
	}

	/**
	 * Sends a packet to every player except the specified one
	 * 
	 * @param p
	 *            the packet
	 * @param player
	 *            the player to exclude
	 */
	public void sendExcept(Packet p, Player player) {
		for (Player currPlayer : players) {
			if (currPlayer != player)
				currPlayer.send(p);
		}
	}

	/**
	 * Plays a card on all players except the current one
	 * 
	 * @param c
	 *            the card to play
	 * @param rank
	 *            the rank of the group played on
	 * @param id
	 *            the id of the group played on
	 */
	public void playOthers(Card c, int rank, int id) {
		for (Player p : players) {
			if (p.getNumber() != turn) {
				p.send(PacketCreator.playOtherCard(
						p.getNumber() % 2 == turn % 2, c, rank, id));
			}
		}
	}

	/**
	 * Plays a group on all players except the current one
	 * 
	 * @param g
	 *            the group to play
	 */
	public void playOthers(Group g) {
		for (Player p : players) {
			if (p.getNumber() != turn) {
				p.send(PacketCreator.playOtherGroup(
						p.getNumber() % 2 == turn % 2, g));
			}
		}
	}

	/**
	 * Clear a board in case a player fails to meld
	 * 
	 * @param team
	 */
	public void clearBoard(int team) {
		board.get(team).clear();
		for (Player player : players) {
			player.send(PacketCreator.clear(player.getNumber() % 2 == team));
		}
	}

	/**
	 * Disconnects everyone and stops all threads.
	 */
	public void stop() {
		for (Player p : players) {
			if (p != null) {
				p.disconnect();
			}
		}
		if (discoveryThread.isAlive()) {
			discovery.close();
		}
		if (server != null && !server.isClosed()) {
			try {
				server.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Main method for command-line server invocation. Optional argument: a
	 * positive, even integer for number of players, must be 4 or 6 for standard
	 * play.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		int numPlayers;
		if (args.length > 0) {
			try {
				numPlayers = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println(args[0] + " is not a valid number.");
				System.exit(0);
				return;
			}
			if (numPlayers % 2 != 0) {
				System.out.println("There must be an even number of players.");
				System.exit(0);
			}
			if (numPlayers < 0) {
				System.out
						.println("There must be a positive number of players.");
				System.exit(0);
			}
		} else {
			numPlayers = 2; // TODO testing
		}
		(new Server()).host(numPlayers);
	}
}
