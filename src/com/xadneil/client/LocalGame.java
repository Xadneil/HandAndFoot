package com.xadneil.client;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.xadneil.client.net.Discovery;
import com.xadneil.client.net.Discovery.GameDetails;

@SuppressWarnings("serial")
public class LocalGame extends JDialog {

	private JList<Discovery.GameDetails> list;
	private JButton btnRefresh, btnJoinGame;
	private Main game;
	private JLabel lblLoading;

	/**
	 * Create the dialog.
	 */
	public LocalGame(Main game) {
		super(game.gameFrame, "Local Games", true);
		this.game = game;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[] { 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		getContentPane().setLayout(gridBagLayout);

		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Discovery.run(LocalGame.this);
				lblLoading.setText("Loading...");
			}
		});
		GridBagConstraints gbc_btnRefresh = new GridBagConstraints();
		gbc_btnRefresh.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRefresh.insets = new Insets(5, 5, 0, 5);
		gbc_btnRefresh.gridx = 0;
		gbc_btnRefresh.gridy = 0;
		getContentPane().add(btnRefresh, gbc_btnRefresh);

		list = new JList<>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (list.getSelectedIndex() == -1) {
					btnJoinGame.setEnabled(false);
				} else {
					btnJoinGame.setEnabled(true);
				}
			}
		});

		lblLoading = new JLabel("Loading...");
		GridBagConstraints gbc_lblLoading = new GridBagConstraints();
		gbc_lblLoading.anchor = GridBagConstraints.WEST;
		gbc_lblLoading.insets = new Insets(5, 5, 0, 0);
		gbc_lblLoading.gridx = 1;
		gbc_lblLoading.gridy = 0;
		getContentPane().add(lblLoading, gbc_lblLoading);

		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.weightx = 1.0;
		gbc_list.gridwidth = 2;
		gbc_list.weighty = 1.0;
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.insets = new Insets(5, 5, 5, 0);
		gbc_list.gridx = 0;
		gbc_list.gridy = 1;
		getContentPane().add(list, gbc_list);

		btnJoinGame = new JButton("Join Game");
		btnJoinGame.setEnabled(false);
		btnJoinGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// function in case of additions later
				joinGame();
			}
		});
		GridBagConstraints gbc_btnJoinGame = new GridBagConstraints();
		gbc_btnJoinGame.insets = new Insets(0, 5, 5, 5);
		gbc_btnJoinGame.fill = GridBagConstraints.BOTH;
		gbc_btnJoinGame.gridx = 0;
		gbc_btnJoinGame.gridy = 2;
		getContentPane().add(btnJoinGame, gbc_btnJoinGame);

		Discovery.run(this);
	}

	private void joinGame() {
		game.connect(list.getSelectedValue());
		dispose();
	}

	public void setGames(final List<GameDetails> games) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				lblLoading.setText("");
				list.setListData(games.toArray(new Discovery.GameDetails[] {}));
			}
		});
	}
}
