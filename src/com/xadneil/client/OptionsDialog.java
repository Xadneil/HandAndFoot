package com.xadneil.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

/**
 * A JDialog for adjusting the card sorting style.
 * 
 * @author Daniel
 */
@SuppressWarnings("serial")
public class OptionsDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final ButtonGroup buttonGroup = new ButtonGroup();
	@SuppressWarnings("rawtypes")
	private final JList list;
	private final JRadioButton rdbtnAscending;
	private final JRadioButton rdbtnDescending;
	private final JSlider slider;

	/**
	 * Create the dialog.
	 * 
	 * @param main
	 *            a reference to the game
	 * @param values
	 *            the initial ordering to display
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public OptionsDialog(final Main main, final String[] values) {
		super(main.gameFrame, "Options", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//setPreferredSize(new Dimension(210, 195));
		setLocationRelativeTo(main.gameFrame);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			DefaultListModel listModel = new DefaultListModel();
			for (String s : values)
				listModel.addElement(s);
			list = new JList(listModel);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setDragEnabled(true);
			list.setTransferHandler(new DragHandler());
			list.setDropMode(DropMode.INSERT);
			contentPanel.add(list, BorderLayout.NORTH);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// sets the order of the Sorting enum based on the order
						// of the strings in the list. Pretty inefficient, but
						// is small enough not to matter.
						byte[] ret = new byte[5];
						for (byte i = 0; i < 4; i++) {
							String current = (String) list.getModel()
									.getElementAt(i);
							if (current.equals(Main.Sorting.RED3S.str)) {
								ret[Main.Sorting.RED3S.ordinal()] = i;
								Main.Sorting.RED3S.order = i;
							} else if (current.equals(Main.Sorting.BLACK3S.str)) {
								ret[Main.Sorting.BLACK3S.ordinal()] = i;
								Main.Sorting.BLACK3S.order = i;
							} else if (current.equals(Main.Sorting.WILDS.str)) {
								ret[Main.Sorting.WILDS.ordinal()] = i;
								Main.Sorting.WILDS.order = i;
							} else if (current.equals(Main.Sorting.OTHERS.str)) {
								ret[Main.Sorting.OTHERS.ordinal()] = i;
								Main.Sorting.OTHERS.order = i;
							}
						}
						ret[4] = (byte) (rdbtnAscending.isSelected() ? 0 : 1);
						
						main.storePrefs(ret, slider.getValue());
						dispose();
					}
				});
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
				buttonPane.add(cancelButton);
			}
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.WEST);
			{
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				{
					JPanel panel_1 = new JPanel();
					panel.add(panel_1);
					rdbtnAscending = new JRadioButton("Ascending");
					panel_1.add(rdbtnAscending);
					rdbtnAscending.setSelected(true);
					buttonGroup.add(rdbtnAscending);
					rdbtnDescending = new JRadioButton("Descending");
					panel_1.add(rdbtnDescending);
					buttonGroup.add(rdbtnDescending);
					if (!Beans.isDesignTime()) {
						rdbtnAscending.setSelected(Main.ascending);
						rdbtnDescending.setSelected(!Main.ascending);
					}
				}
				{
					JPanel panel_1 = new JPanel();
					panel.add(panel_1);
					{
						JLabel lblNewLabel = new JLabel("Card Size");
						panel_1.add(lblNewLabel);
					}
					{
						slider = new JSlider();
						slider.setValue(73);
						if (!Beans.isDesignTime()) {
							slider.setValue(Main.width);
						}
						slider.setMaximum(150);
						slider.setMinimum(50);
						panel_1.add(slider);
					}
				}
			}
		}
		pack();
	}
}
