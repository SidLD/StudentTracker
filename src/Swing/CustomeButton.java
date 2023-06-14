package Swing;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;

public class CustomeButton extends JButton {

	/**
	 * Create the panel.
	 */
	public CustomeButton(String text, Color color) {
		setText(text);
		setBackground(color);
	}

}
