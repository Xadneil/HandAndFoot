package com.xadneil.client;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;

import com.xadneil.client.net.Discovery.GameDetails;
import com.xadneil.client.net.Network;
import com.xadneil.client.net.Packet;
import com.xadneil.client.net.PacketCreator;
import com.xadneil.server.Server;

/**
 * Hand and Foot client
 * 
 * @author Daniel
 */
public class Main {

	public JFrame gameFrame;
	/**
	 * Default port
	 */
	private static int PORT = 5677;
	private Network network;
	private List<Card> hand = new ArrayList<>();
	private Group staging;
	private Map<Integer, ArrayList<Group>> board = new HashMap<>();
	private EnemyBoard enemyBoard = new EnemyBoard();
	public JFrame enemyFrame;
	private Card discard;
	private Surface surface;
	private JButton btnDraw2Cards, btnDraw7Cards;
	private Card pending;
	private boolean hasDrawn;
	private int toDiscard = -1;
	public static boolean local = true;
	private static byte[] sorting;
	public static boolean ascending = true;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					Surface.loadImages();
					window.gameFrame.setVisible(true);
					window.enemyFrame.setVisible(true);
					window.postInit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		loadPrefs();
		initialize();
		initializeEnemyBoard();
	}

	private void initializeEnemyBoard() {
		enemyFrame = new JFrame();
		enemyFrame.setTitle("Enemy Board");
		enemyFrame.setBounds(0, 500, 800, 238);
		enemyFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		enemyFrame.getContentPane().add(enemyBoard);
	}

	/**
	 * Notify frames and custom drawings of initial dimensions
	 */
	public void postInit() {
		surface.componentResized(null);
		enemyBoard.componentResized(null);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		gameFrame = new JFrame();
		gameFrame.setTitle("Hand and Foot");
		gameFrame.setBounds(0, 0, 800, 500);
		gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		gameFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		JMenuBar menuBar = new JMenuBar();
		gameFrame.setJMenuBar(menuBar);

		JMenu mnGame = new JMenu("Game");
		menuBar.add(mnGame);

		JMenuItem mntmConnect = new JMenuItem("Connect to IP Game");
		mntmConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					InetAddress address;
					if (local) {
						address = InetAddress.getLocalHost();
					} else {
						String ip = JOptionPane
								.showInputDialog(gameFrame, "IP: ", "Connect",
										JOptionPane.QUESTION_MESSAGE);
						try {
							address = InetAddress.getByName(ip);
						} catch (UnknownHostException ex) {
							JOptionPane.showMessageDialog(gameFrame,
									"That IP Address is not valid.");
							return;
						}
					}
					connect(address);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		});

		JMenuItem mntmConnectToLocal = new JMenuItem("Connect to Local Game");
		mntmConnectToLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new LocalGame(Main.this).setVisible(true);
			}
		});
		mnGame.add(mntmConnectToLocal);
		mnGame.add(mntmConnect);

		JMenuItem mntmOptions = new JMenuItem("Options");
		mntmOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] values = new String[4];
				for (int i = 0; i < 4; i++) {
					values[Sorting.values()[i].order] = Sorting.values()[i].str;
				}
				CustomSort cs = new CustomSort(Main.this, values);
				cs.setVisible(true);
				cs.setLocationRelativeTo(gameFrame);
			}
		});

		JMenuItem mntmHostGame = new JMenuItem("Host Game");
		mntmHostGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Server.getInstance().host(2 /* TODO testing or poll */,
						new Server.IntegratedServer(Main.this, System.out));
			}
		});
		mnGame.add(mntmHostGame);
		mnGame.add(mntmOptions);
		mnGame.add(new JPopupMenu.Separator());

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		mnGame.add(mntmExit);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0 };
		gameFrame.getContentPane().setLayout(gridBagLayout);

		btnDraw7Cards = new JButton("Draw 7 Cards from Discard");
		btnDraw7Cards.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				network.send(PacketCreator.draw7());
				hasDrawn = true;
				setButtons(false);
			}
		});
		GridBagConstraints gbc_btnDrawCards_1 = new GridBagConstraints();
		gbc_btnDrawCards_1.insets = new Insets(5, 0, 5, 0);
		gbc_btnDrawCards_1.anchor = GridBagConstraints.EAST;
		gbc_btnDrawCards_1.fill = GridBagConstraints.VERTICAL;
		gbc_btnDrawCards_1.gridx = 2;
		gbc_btnDrawCards_1.gridy = 0;
		gameFrame.getContentPane().add(btnDraw7Cards, gbc_btnDrawCards_1);
		btnDraw7Cards.setEnabled(false);

		btnDraw2Cards = new JButton("Draw 2 Cards");
		btnDraw2Cards.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				network.send(PacketCreator.draw());
				hasDrawn = true;
				setButtons(false);
			}
		});
		GridBagConstraints gbc_btnDrawCards = new GridBagConstraints();
		gbc_btnDrawCards.insets = new Insets(5, 0, 5, 5);
		gbc_btnDrawCards.anchor = GridBagConstraints.WEST;
		gbc_btnDrawCards.fill = GridBagConstraints.VERTICAL;
		gbc_btnDrawCards.gridx = 1;
		gbc_btnDrawCards.gridy = 0;
		gameFrame.getContentPane().add(btnDraw2Cards, gbc_btnDrawCards);
		btnDraw2Cards.setEnabled(false);

		JButton btnPlayGroup = new JButton("Play Group");
		btnPlayGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (staging != null)
					network.send(PacketCreator.play(staging));
			}
		});
		GridBagConstraints gbc_btnPlayGroup = new GridBagConstraints();
		gbc_btnPlayGroup.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPlayGroup.weighty = 1.0;
		gbc_btnPlayGroup.anchor = GridBagConstraints.SOUTH;
		gbc_btnPlayGroup.insets = new Insets(0, 5, 5, 5);
		gbc_btnPlayGroup.gridx = 0;
		gbc_btnPlayGroup.gridy = 1;
		gameFrame.getContentPane().add(btnPlayGroup, gbc_btnPlayGroup);

		surface = new Surface(this);
		GridBagConstraints gbc_surface = new GridBagConstraints();
		gbc_surface.gridheight = 3;
		gbc_surface.weightx = 1.0;
		gbc_surface.weighty = 1.0;
		gbc_surface.gridwidth = 2;
		gbc_surface.fill = GridBagConstraints.BOTH;
		gbc_surface.gridx = 1;
		gbc_surface.gridy = 1;
		gameFrame.getContentPane().add(surface, gbc_surface);

		JButton btnRemoveGroup = new JButton("Remove Group");
		btnRemoveGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (staging != null) {
					for (Card c : staging) {
						hand.add(c);
					}
					staging = null;
					redraw();
				}
			}
		});
		GridBagConstraints gbc_btnRemoveGroup = new GridBagConstraints();
		gbc_btnRemoveGroup.weighty = 1.0;
		gbc_btnRemoveGroup.anchor = GridBagConstraints.NORTH;
		gbc_btnRemoveGroup.insets = new Insets(0, 5, 5, 5);
		gbc_btnRemoveGroup.gridx = 0;
		gbc_btnRemoveGroup.gridy = 2;
		gameFrame.getContentPane().add(btnRemoveGroup, gbc_btnRemoveGroup);

		JButton btnSortHand = new JButton("Sort Hand");
		btnSortHand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Don't want sort to affect hand indexes while floating a card
				// or waiting on discard.
				// We use absolute index for those calculations
				if (!surface.isFloating() && toDiscard == -1) {
					Collections.sort(hand);
					redraw();
				}
			}
		});
		GridBagConstraints gbc_btnSortHand = new GridBagConstraints();
		gbc_btnSortHand.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSortHand.insets = new Insets(0, 5, 60, 5);
		gbc_btnSortHand.gridx = 0;
		gbc_btnSortHand.gridy = 3;
		gameFrame.getContentPane().add(btnSortHand, gbc_btnSortHand);
	}

	/**
	 * Notifies the client that the server is (almost) ready to accept
	 * connections, so wait shortly and connect
	 * 
	 * @param address
	 *            the local server address
	 * @param port
	 *            the local port
	 */
	public void serverCallback(final InetAddress address, final int port) {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(200); // wait for server to start blocking
				} catch (InterruptedException e) {
				}
				PORT = port;
				connect(address);
			}
		}.start();
	}

	/**
	 * Connect to a hand and foot server on the specified address and port
	 * 
	 * @param gameDetails
	 *            the address and port to connect to
	 */
	public void connect(GameDetails gameDetails) {
		PORT = gameDetails.port;
		connect(gameDetails.address);
	}

	/**
	 * Connect to a hand and foot server on the specified address, port
	 * <code>PORT</code>
	 * 
	 * @param i
	 *            the address to connect to
	 */
	public void connect(final InetAddress i) {
		new SwingWorker<Boolean, Object>() {
			@Override
			public Boolean doInBackground() throws Exception {
				try {
					Socket s = new Socket();
					s.connect(new InetSocketAddress(i, PORT), 4000);

					s.setTcpNoDelay(true);
					// set up socket monitoring and sending capability
					network = new Network(s, Main.this);
					network.start();
				} catch (SocketTimeoutException e) {
					JOptionPane.showMessageDialog(gameFrame,
							"The server could not be reached. (Timeout)");
					return false;
				} catch (IOException e) {
					JOptionPane.showMessageDialog(
							gameFrame,
							"An error occurred. "
									+ (e.getCause() == null ? e.getMessage()
											: e.getCause().getMessage()));
					e.printStackTrace();
					return false;
				}
				return true;
			}

			@Override
			public void done() {
				try {
					if (get())
						new Login(Main.this).setVisible(true);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}

	// from Surface
	public void discard(int index) {
		if (!hasDrawn) {
			JOptionPane.showMessageDialog(gameFrame, "You must draw first");
			return;
		}
		if (staging != null) {
			int answer = JOptionPane.showConfirmDialog(gameFrame,
					"You have some cards in the staging area. "
							+ "Are you sure you want to discard?");
			if (answer != JOptionPane.YES_OPTION) {
				return;
			}
		}
		setButtons(false);
		toDiscard = index;

		Card c = hand.get(index);
		network.send(PacketCreator.discard(c));
	}

	// from handler
	public void doDiscard() {
		if (toDiscard != -1) {
			hasDrawn = false;
			hand.remove(toDiscard);
			toDiscard = -1;
		}
	}

	public final List<Card> copyHand(List<Card> toCopy) {
		List<Card> ret = new ArrayList<>();
		for (Card c : toCopy) {
			ret.add(new Card(c));
		}
		return ret;
	}

	public void killSurface() {
		surface.setAlive(false);
	}

	public void login(String username, char[] password) {
		network.send(PacketCreator.login(username, password));
	}

	private void loadPrefs() {
		sorting = Preferences.userNodeForPackage(getClass()).getByteArray(
				"SORTING", null);
		if (sorting == null) {
			sorting = new byte[] { 0, 1, 2, 3, 0 };
			Preferences.userNodeForPackage(getClass()).putByteArray("SORTING",
					sorting);
		} else {
			for (int i = 0; i < 4; i++) {
				Sorting.values()[i].order = sorting[i];
			}
			ascending = sorting[4] == 0;
		}
	}

	public void storePrefs(byte[] sorting) {
		Main.sorting = sorting;
		Preferences.userNodeForPackage(getClass()).putByteArray("SORTING",
				sorting);
		ascending = sorting[4] == 0;
	}

	public List<Card> getHand() {
		return hand;
	}

	public void setPending(Card c) {
		pending = c;
	}

	public Card getPending() {
		return pending;
	}

	public Map<Integer, ArrayList<Group>> getBoard() {
		return board;
	}

	public Map<Integer, ArrayList<Group>> getEnemyBoard() {
		return enemyBoard.getBoard();
	}

	public void setDiscard(Card c) {
		discard = c;
	}

	public Card getDiscard() {
		return discard;
	}

	public void reset() {
		board.clear();
		hand.clear();
		close();
	}

	public void redraw() {
		surface.repaint();
		enemyFrame.repaint();
	}

	public void close() {
		if (network != null) {
			network.close();
		}
	}

	public void sendPacket(Packet p) {
		try {
			network.getSocket().getOutputStream().write(p.toByteArray());
		} catch (IOException ex) {
		}
	}

	private void exit() {
		close();
		System.exit(0);
	}

	public void setTurn(String playerName) {
		surface.setTurnName(playerName);
	}

	public void setRound(int round) {
		surface.setRound(round);
	}

	public void setStaging(Group group) {
		staging = group;
	}

	public Group getStaging() {
		return staging;
	}

	public void setButtons(boolean enabled) {
		btnDraw2Cards.setEnabled(enabled);
		btnDraw7Cards.setEnabled(enabled);
		hasDrawn = !enabled;
	}

	public void playCard(int index, int rank, int id) {
		sendPacket(PacketCreator.play(hand.get(index), rank, id));
	}

	public static enum Sorting {
		RED3S("Red 3s"),
		BLACK3S("Black 3s"),
		WILDS("Wild Cards"),
		OTHERS("Other Cards");
		public int order;
		public String str;

		private Sorting(String s) {
			this.str = s;
			// default ordering
			order = this.ordinal();
		}
	}
}
