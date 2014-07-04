package com.xadneil.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.xadneil.server.Server;

/**
 * A JDialog for selecting the number of players for an integrated server
 * 
 * @author Daniel
 */
@SuppressWarnings("serial")
public class NumPlayers extends JDialog {
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final JRadioButton btn4Players;

	/**
	 * Create the dialog.
	 */
	public NumPlayers(final Main game) {
		super(game.gameFrame, "Number of Players", true);
		setPreferredSize(new Dimension(200, 130));
		setLocationRelativeTo(game.gameFrame);
		{
			JPanel panel = new JPanel();
			panel.setBorder(null);
			getContentPane().add(panel, BorderLayout.CENTER);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			btn4Players = new JRadioButton("4 Players");
			buttonGroup.add(btn4Players);
			btn4Players.setSelected(true);
			btn4Players.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(btn4Players);

			JRadioButton rdbtn6Players = new JRadioButton("6 Players");
			buttonGroup.add(rdbtn6Players);
			rdbtn6Players.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(rdbtn6Players);
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(null);
			getContentPane().add(panel, BorderLayout.SOUTH);

			JButton btnOk = new JButton("OK");
			btnOk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int numPlayers = btn4Players.isSelected() ? 4 : 6;
					Server.getInstance().host(numPlayers,
							new Server.IntegratedServer(game, System.out));
					dispose();
				}
			});
			btnOk.requestFocus();
			panel.add(btnOk);

			JButton btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			panel.add(btnCancel);
		}
		pack();
	}
}
