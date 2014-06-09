package com.xadneil.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.Beans;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.xadneil.client.Group.Result;

/**
 * Displays the card game using a custom-drawn GUI. Performs game actions based
 * on mouse events
 * 
 * @author Daniel
 */
@SuppressWarnings("serial")
public final class Surface extends JPanel implements MouseMotionListener,
		MouseListener, ComponentListener {

	/**
	 * Pixels separating consecutive horizontal cards
	 */
	private final int horizontalStagger = 15;
	/**
	 * Pixels separating consecutive vertical cards
	 */
	private final int verticalStagger = 25;

	private final int cardWidth = 73;
	private final int cardHeight = 97;

	private final int boardSeparation = 6;
	private final int horizEdgeToEdge = cardWidth + boardSeparation;
	private final int borderSize = 4;
	private final int statusSize = this.getFont().getSize() + 2 * borderSize;
	private int mouseX, mouseY;
	/**
	 * Card-relative mouse coordinates
	 */
	private int pressX, pressY;
	/**
	 * JPanel Width and Height
	 */
	private int w, h;
	private String turnName;
	private int floatingIndex;
	private boolean leftDown = false;
	private boolean floating = false;
	public final static Map<Card, BufferedImage> imageBuffer = new HashMap<>();
	private Main game;
	private int round = 1;

	// <displayIndex, (rank, id)>
	private SortedMap<Integer, Pair> displayGroups = new TreeMap<>();
	private boolean isAlive = true;

	private static class Pair {
		public int rank, id;

		public Pair(int rank, int id) {
			this.rank = rank;
			this.id = id;
		}
	}

	/**
	 * Default constructor for designer use. Do not use.
	 */
	public Surface() {
		if (!Beans.isDesignTime())
			throw new RuntimeException("Illegal Surface constructor. Use \"new Surface(Main game)\" instead.");
	}

	/**
	 * Class constructor
	 * 
	 * @param game
	 *            the Hand and Foot game to display
	 */
	public Surface(Main game) {
		super();
		this.game = game;
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
	}

	/**
	 * Reads the game images from their files and stores them in
	 * <code>imageBuffer</code>
	 */
	public static void loadImages() {
		Card card;
		for (Suit s : Suit.values()) {
			if (s != Suit.UNDEFINED) {
				for (int i = 1; i <= 13; i++) {
					card = new Card(i, s);
					try {
						imageBuffer.put(card, ImageIO.read(Main.class
								.getResource("/com/xadneil/images/"
										+ card.getName() + ".gif")));
					} catch (IOException ex) {
						ex.printStackTrace();
						System.exit(0);
					}
				}
			} else {
				for (int i = 0; i < 2; i++) {
					card = new Card(14, s);
					try {
						imageBuffer.put(card, ImageIO.read(Main.class
								.getResource("/com/xadneil/images/"
										+ card.getName() + ".gif")));
					} catch (IOException ex) {
						ex.printStackTrace();
						System.exit(0);
					}
				}
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Construct Board Basics
		g2.setColor(new Color(0, 175, 0)); // medium green
		g2.fillRect(0, 0, w, h);
		g2.setColor(Color.BLACK);
		g2.drawLine(0, h - cardHeight - borderSize * 2, w, h - cardHeight
				- borderSize * 2);
		g2.drawLine(0, h, w, h);
		g2.drawLine(borderSize + cardWidth + boardSeparation / 2, 0, borderSize
				+ cardWidth + boardSeparation / 2, h - cardHeight - borderSize
				* 2);
		g2.drawLine(0, (int) (borderSize * 1.5) + cardHeight, borderSize
				+ cardWidth + boardSeparation / 2, (int) (borderSize * 1.5)
				+ cardHeight);
		// Draw turn number
		g2.drawString("Current Turn: " + (turnName == null ? "" : turnName)
				+ "  Round: " + round, borderSize, h + this.getFont().getSize()
				+ borderSize - 1);

		// For designer compatibility
		if (Beans.isDesignTime())
			return;

		if (!leftDown && floating) {
			floating = false;
		}

		if (leftDown
				&& !floating /* && !groupFloating */
				&& mouseY > h - cardHeight - borderSize
				&& mouseY < h - borderSize
				&& mouseX - borderSize < ((game.getHand().isEmpty()) ? 0
						: horizontalStagger * (game.getHand().size() - 1)
								+ cardWidth)) {
			floating = true;
			floatingIndex = (mouseX - borderSize) / horizontalStagger;
			if (floatingIndex >= game.getHand().size()) {
				floatingIndex = game.getHand().size() - 1;
			}
			pressX = (mouseX - borderSize) - floatingIndex * horizontalStagger;
			pressY = (mouseY + borderSize) - (h - cardHeight);
		}

		// Draw board and discard
		int j = 1;
		for (int i = 0; i <= 13; i++) {
			if (i == 0 && game.getDiscard() != null) {
				g2.drawImage(imageBuffer.get(game.getDiscard()), borderSize,
						borderSize, null);
				continue;
			}
			if (2 <= i && i <= 3) {
				continue;
			}
			// only i = 4 to 13
			if (game.getBoard().containsKey(i)) {
				for (Group group : game.getBoard().get(i)) {
					if (displayGroups.get(j) == null
							|| displayGroups.get(j).rank != group.getRank()
							|| displayGroups.get(j).id != group.getId())
						displayGroups.put(j,
								new Pair(group.getRank(), group.getId()));
					if (group.getCards().size() < 7) {
						// group is not collapsed
						int k = 0;
						for (Card c : group.getCards()) {
							g2.drawImage(imageBuffer.get(c), borderSize
									+ horizEdgeToEdge * j, borderSize
									+ verticalStagger * k, null);
							k++;
						}
					} else {
						// group is collapsed
						if (group.getCoverCard() == null) {
							Suit suit;
							if (group.isClean()) {
								suit = new Random().nextBoolean() ? Suit.DIAMOND
										: Suit.HEART;
							} else {
								suit = new Random().nextBoolean() ? Suit.CLUB
										: Suit.SPADE;
							}
							group.setCoverCard(new Card(group.getRank(), suit));
						}
						g2.drawImage(imageBuffer.get(group.getCoverCard()),
								borderSize + horizEdgeToEdge * j, borderSize,
								null);
					}
				}
				j++;
			}
		}
		// Draw staging area
		j = 0;
		if (game.getStaging() != null)
			for (Card c : game.getStaging()) {
				g2.drawImage(imageBuffer.get(c), borderSize, borderSize * 2
						+ cardHeight + verticalStagger * j + 1, null);
				j++;
			}
		// Draw hand
		for (int i = 0; i < game.getHand().size(); i++) {
			if (floating && i == floatingIndex) {
				continue;
			}
			Card c = game.getHand().get(i);
			g2.drawImage(imageBuffer.get(c),
					borderSize + horizontalStagger * i, h - cardHeight
							- borderSize, null);
		}
		// draw floating card
		if (floating) {
			g2.drawImage(imageBuffer.get(game.getHand().get(floatingIndex)),
					mouseX - pressX, mouseY - pressY, cardWidth, cardHeight,
					null);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		if (e.getButton() == MouseEvent.BUTTON1 && isAlive) {
			leftDown = true;
		}
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!isAlive) {
			leftDown = false;
			floating = false;
			floatingIndex = -1;
			return;
		}
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftDown = false;
			if (floating) {
				if (mouseX < borderSize + cardWidth + boardSeparation / 2
						&& mouseY > (int) (borderSize * 1.5) + cardHeight
						&& mouseY < h - cardHeight - borderSize * 2) {
					// handle move to staging
					Card c = game.getHand().get(floatingIndex);
					Group.Result res;
					if (game.getStaging() != null) {
						if ((res = game.getStaging().addCards(Arrays.asList(c))) == Group.Result.SUCCESS) {
							game.getHand().remove(floatingIndex);
						} else {
							JOptionPane.showMessageDialog(game.gameFrame,
									res.message, "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						if (c.isWild() || c.getRank() == 3) {
							JOptionPane.showMessageDialog(game.gameFrame,
									"You cannot start a group with that card.",
									"Error", JOptionPane.ERROR_MESSAGE);
							floatingIndex = -1;
							floating = false;
							repaint();
							return;
						}
						Group g = new Group(c.getRank(), -1);
						if (Result.SUCCESS != g.addCards(Arrays.asList(c))) {
							throw new RuntimeException(
									"Failed to create staging group");
						}
						game.setStaging(g);
						game.getHand().remove(floatingIndex);
					}
				} else if (mouseX > borderSize + cardWidth + boardSeparation
						/ 2
						&& mouseY < h - cardHeight - borderSize * 2) {
					// handle direct play
					List<Entry<Integer, Pair>> temp = new ArrayList<>(
							displayGroups.entrySet());
					if (temp.isEmpty()) {
						floatingIndex = -1;
						floating = false;
						repaint();
						return;
					}
					int index = (mouseX
							- (borderSize + cardWidth + boardSeparation / 2) - borderSize)
							/ horizEdgeToEdge;
					if (index >= temp.size()) {
						index = temp.size() - 1;
					}
					int id = temp.get(index).getValue().id;
					int rank = temp.get(index).getValue().rank;
					game.setPending(game.getHand().get(floatingIndex));
					game.playCard(floatingIndex, rank, id);
				} else if (mouseX < borderSize + cardWidth + boardSeparation
						/ 2
						&& mouseY < (int) (borderSize * 1.5) + cardHeight) {
					// handle discard
					game.discard(floatingIndex);
				}
				floatingIndex = -1;
				floating = false;
			}
		}
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Sets the current player's name to be displayed in the status bar
	 * 
	 * @param name
	 *            the current player's name
	 */
	public void setTurnName(String name) {
		turnName = name;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		Dimension dimension = this.getSize();
		Insets insets = this.getInsets();
		w = dimension.width - insets.left - insets.right;
		h = dimension.height - insets.top - insets.bottom;
		// make room for status bar
		h -= statusSize;
		if (h < borderSize) {
			h = borderSize;
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	/**
	 * Sets the round number which will be displayed in the status bar
	 * 
	 * @param round
	 *            the round to display
	 */
	public void setRound(int round) {
		this.round = round;
	}

	/**
	 * Sets whether the surface will respond to mouse events
	 * 
	 * @param isAlive
	 *            whether to respond to mouse events
	 */
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	/**
	 * Gets whether the user's mouse is holding a card which is floating above
	 * everything
	 * 
	 * @return the floating status
	 */
	public boolean isFloating() {
		return floating;
	}
}