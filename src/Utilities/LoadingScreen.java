package Utilities;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import javax.swing.SwingConstants;

public class LoadingScreen extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JProgressBar progressBar;
	JLabel label;
	public JFrame frame;
	
	
	public LoadingScreen() {
		setUndecorated(true);
		setBounds(100, 100, 450, 300);
		setLayout(null);
		
		label = new JLabel("Starting");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Tahoma", Font.PLAIN, 20));
		label.setBounds(60, 25, 307, 98);
		this.add(label);
		
		progressBar = new JProgressBar(0, 100);
		progressBar.setBounds(32, 158, 377, 42);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.add(progressBar);

		requestFocus();
		setVisible(true);
	}
	public void setLabelForProgress(String status, int progress) {
		label.setText(status);
		progressBar.setValue(progress);
		this.revalidate();
		this.repaint();
	}
}
