package com.xadneil.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * A simple JDialog for sending a nickname to the server.
 * 
 * @author Daniel
 */
@SuppressWarnings("serial")
public class Login extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JTextField nameField;
	private final JLabel lblError;

	/**
	 * Create the dialog.
	 */
	public Login(final Main game) {
		super(game.gameFrame, "Login", true);
		setLocationRelativeTo(game.gameFrame);
		setPreferredSize(new Dimension(250, 150));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JLabel lblUsername = new JLabel("Nickname");
				panel.add(lblUsername);
			}
			{
				nameField = new JTextField();
				panel.add(nameField);
				nameField.setColumns(15);
			}
		}
		{
			lblError = new JLabel("");
			contentPanel.add(lblError);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						game.login(nameField.getText());
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		pack();
	}

	/**
	 * Sets the success of the name selection. If successful, exit. Otherwise,
	 * display a message.
	 * 
	 * @param success
	 *            success or failure
	 */
	public void setSuccess(boolean success) {
		if (success) {
			dispose();
		} else {
			new Thread() {
				public void run() {
					lblError.setText("That name is already being used.");
					try {
						Thread.sleep(3500);
					} catch (InterruptedException e) {
					}
					lblError.setText("");
				}
			}.start();
		}
	}
}
