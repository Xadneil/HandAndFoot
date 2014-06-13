package com.xadneil.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;

/**
 * JPanel for displaying the enemy board
 * 
 * @author Daniel
 */
@SuppressWarnings("serial")
public class EnemyBoard extends JPanel implements ComponentListener {

	/**
	 * Pixels separating consecutive vertical cards
	 */
	private final int verticalStagger = 25;

	private final int cardWidth = 73;

	private final int boardSeparation = 6;
	private final int horizEdgeToEdge = cardWidth + boardSeparation;
	private final int borderSize = 4;
	/**
	 * Width and Height
	 */
	private int w, h;
	private Map<Integer, ArrayList<Group>> board = new HashMap<>();

	/**
	 * Class Constructor
	 */
	public EnemyBoard() {
		super();
		addComponentListener(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Construct Board Basics
		g2.setColor(new Color(0, 175, 0));
		g2.fillRect(0, 0, w, h);
		g2.setColor(Color.BLACK);

		// For designer compatibility
		if (Beans.isDesignTime())
			return;

		// Draw board
		int j = 0; // # of cards horizontally
		for (int i = 1; i <= 13; i++) {
			// Ignore 2 and 3, no groups for them
			if (i == 2 || i == 3) {
				continue;
			}
			if (board.containsKey(i)) {
				for (Group group : board.get(i)) {
					if (group.getCards().size() < 7) {
						// group is not collapsed
						int k = 0; // # of cards vertically
						for (Card c : group.getCards()) {
							g2.drawImage(Surface.imageBuffer.get(c), borderSize
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
						g2.drawImage(
								Surface.imageBuffer.get(group.getCoverCard()),
								borderSize + horizEdgeToEdge * j, borderSize,
								null);
					}
				}
				j++;
			}
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		Dimension dimension = this.getSize();
		Insets insets = this.getInsets();
		w = dimension.width - insets.left - insets.right;
		h = dimension.height - insets.top - insets.bottom;
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

	public Map<Integer, ArrayList<Group>> getBoard() {
		return board;
	}
}
