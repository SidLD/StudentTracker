package Interface;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import GoogleForm.Main;
import Swing.RoundPanel;
import Swing.TableDark;
import Utilities.Link;
import Utilities.LoadingScreen;
import Utilities.Status;
import database.SqlLite;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.LinkedList;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GoogleFormPanel extends JPanel {

	private int height;
	private int width;
	private JTextField urlTextField;
	private boolean isGenerate = false;
	private JTable statusTable;
	private JTable formLinkTable;

	private JScrollPane statusTableScrollPane = new javax.swing.JScrollPane();
	private JScrollPane formTableScrollPane = new javax.swing.JScrollPane();
	private final String defaultLink = "https://docs.google.com/forms/d/";
	private JTextField formIdField;

	private int selectedStatus = -1;
	/**
	 * Create the panel.
	 */
	public GoogleFormPanel(int width, int height) {
		this.width = width;
		this.height = height;
		setBackground(new Color(0, 0, 0));
		setSize(this.width, this.height);
		setLayout(null);
		
		JButton generateFormBtn = new JButton("Generate Form");
		generateFormBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					isGenerate = true;
					generateFormBtn.setEnabled(!isGenerate);
					
//					LoadingScreen sc = new LoadingScreen();

					generateFormBtn.setText("Getting Token");
//					sc.setLabelForProgress("Getting Credentials", 20);
					String token = Main.getAccessToken();
					generateFormBtn.setText("Generating Form");
					
//					sc.setLabelForProgress("Generating Form ID", 40);
					String formId = Main.createNewForm(token);
					
					LinkedList<String> status = new SqlLite().getAllStatus();
			

					generateFormBtn.setText("Publishing Form");
//					sc.setLabelForProgress("Publishing Form", 60);
					Main.publishForm(formId, token);


					generateFormBtn.setText("Adding Questions");
//					sc.setLabelForProgress("Setting Up Questions", 80);
					Main.transformInQuiz(formId, token);
					Main.addItemToQuiz(
			                 "Status",
			                 status,
			                 "","RADIO",
			                 formId,
			                 token
			         );
					
					Main.addItemToQuiz(
			                 "Status Detail",
			                 null,
			                 "","CHOICE_TYPE_UNSPECIFIED",
			                 formId,
			                 token
			         );
					Main.addItemToQuiz(
			                 "Name format* (Last Name, First Name, Middle Name)",
			                 null,
			                 "","CHOICE_TYPE_UNSPECIFIED",
			                 formId,
			                 token
			         );
					
					generateFormBtn.setText("Generating Link");
					String preUrl = defaultLink+formId;
					

					generateFormBtn.setText("Saving FormID");
					new SqlLite().saveGoogleLink(formId);
					

//					sc.setLabelForProgress("Saving Form Link", 100);
					urlTextField.setText(preUrl);
					formIdField.setText(formId);
//					sc.frame.dispose();
					

					updateTable();
					isGenerate = false;
					generateFormBtn.setText("Generate Form");
					generateFormBtn.setEnabled(!isGenerate);
					
					
			
					
				} catch (IOException | GeneralSecurityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		generateFormBtn.setBounds(82, 70, 132, 35);
		add(generateFormBtn);
		
		urlTextField = new JTextField();
		urlTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		urlTextField.setBounds(325, 67, 400, 41);
		add(urlTextField);
		urlTextField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Link :");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(217, 70, 98, 35);
		add(lblNewLabel);
		
		JPanel linkListPanel = new RoundPanel();
		linkListPanel.setBounds(20, generateFormBtn.getY() + 150, width/3 * 2, height/2);
		linkListPanel.setLayout(null);
		add(linkListPanel);
		
		JPanel listTablePanel = new RoundPanel();
		listTablePanel.setBounds(0, 0,  width/3 * 2, height/3 * 2);
		listTablePanel.setLayout(new BorderLayout(0, 0));
		linkListPanel.add(listTablePanel);
		
		formLinkTable = new TableDark();
		formLinkTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					int selectedLink = formLinkTable.getSelectedRow();
					String formId = formLinkTable.getValueAt(selectedLink, 1).toString();
					formIdField.setText(formId);
					urlTextField.setText(defaultLink+formId);
					
				} catch (Exception e2) {
					// TODO: handle exception
				}
				
			}
		});
		formLinkTable.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"DATE", "FORM ID"
				}
			));
		formLinkTable.getColumnModel().getColumn(0).setPreferredWidth(25);
		JTableHeader formTableHeader = formLinkTable.getTableHeader();
		formTableHeader.setFont(new Font("Arial", Font.BOLD, 15));
		listTablePanel.add(formTableHeader, BorderLayout.NORTH);
		formTableScrollPane.setBounds(0, 0,  width/3, height/3 * 2);
		formTableScrollPane.setViewportView(formLinkTable);
		listTablePanel.add(formTableScrollPane, BorderLayout.CENTER);
		
		formIdField = new JTextField();
		formIdField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		formIdField.setColumns(10);
		formIdField.setBounds(325, 140, 400, 41);
		add(formIdField);
		
		JLabel lblFormId = new JLabel("Form ID :");
		lblFormId.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFormId.setForeground(Color.WHITE);
		lblFormId.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblFormId.setBounds(217, 140, 98, 35);
		add(lblFormId);
		
		
		JPanel StatusPanel = new RoundPanel();
		StatusPanel.setBounds(listTablePanel.getWidth() + listTablePanel.getX() + 50, generateFormBtn.getY(), width/5, height/2);
		StatusPanel.setLayout(null);
		add(StatusPanel);
		
		JPanel StatusTablePanel = new RoundPanel();
		StatusTablePanel.setBounds(0, 0,  width/5, height/3 * 2);
		StatusTablePanel.setLayout(new BorderLayout(0, 0));
		StatusPanel.add(StatusTablePanel);
		
		statusTable = new TableDark();
		statusTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					selectedStatus = statusTable.getSelectedRow();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		statusTable.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"ID", "STATUS"
				}
			));
		
//		formLinkTable.getColumnModel().getColumn(0).setPreferredWidth(25);
//		JTableHeader formTableHeader = formLinkTable.getTableHeader();
//		formTableHeader.setFont(new Font("Arial", Font.BOLD, 15));
//		listTablePanel.add(formTableHeader, BorderLayout.NORTH);
//		formTableScrollPane.setBounds(0, 0,  width/3, height/3 * 2);
//		formTableScrollPane.setViewportView(formLinkTable);
//		listTablePanel.add(formTableScrollPane, BorderLayout.CENTER);
		
		statusTable.getColumnModel().getColumn(0).setPreferredWidth(25);
		JTableHeader statusTableHeader2 = statusTable.getTableHeader();
		statusTableHeader2.setFont(new Font("Arial", Font.BOLD, 15));
		StatusTablePanel.add(statusTableHeader2, BorderLayout.NORTH);
		statusTableScrollPane.setBounds(0, 0,  width/5, height/3 * 2);
		statusTableScrollPane.setViewportView(statusTable);
		StatusTablePanel.add(statusTableScrollPane, BorderLayout.CENTER);
		
		JButton addStatusBtn = new JButton("Add Status");
		addStatusBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String newStatus = JOptionPane.showInputDialog(null, "Input new Status");
					if(!newStatus.isEmpty()) {
						if(new SqlLite().addStatus(newStatus)) {
							JOptionPane.showMessageDialog(null, newStatus+" is added");
							updateStatusTable();
						}else {
							JOptionPane.showMessageDialog(null, "Fail");
						}
					}else {
						JOptionPane.showMessageDialog(null, "Invalid Input");
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		addStatusBtn.setBounds(StatusPanel.getX(), StatusPanel.getY() + StatusPanel.getHeight() + 50, 89, 23);
		add(addStatusBtn);
		
		JButton editStatusBtn = new JButton("Edit Status");
		editStatusBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedStatus == -1) {
					JOptionPane.showMessageDialog(null, "Please select a status");
				}else {
					String newStatus = JOptionPane.showInputDialog(null, "Input new Status");
					if(!newStatus.isEmpty()) {
						String status = statusTable.getValueAt(selectedStatus, 1).toString();
						int id = new SqlLite().getStatusId(status);
						if(new SqlLite().updateStatus(id,newStatus)) {
							JOptionPane.showMessageDialog(null, newStatus+" is updated");
							updateStatusTable();
						}else {
							JOptionPane.showMessageDialog(null, "Fail");
						}
					}else {
						JOptionPane.showMessageDialog(null, "Invalid Input");
					}
				}
			}
		});
		editStatusBtn.setBounds(StatusPanel.getX() + StatusPanel.getWidth() - 89, StatusPanel.getY() + StatusPanel.getHeight() + 50, 89, 23);
		add(editStatusBtn);
		
		updateTable();
		updateStatusTable();
	}
	private void updateStatusTable() {
		DefaultTableModel model = (DefaultTableModel) statusTable.getModel();
		LinkedList<String> status = new SqlLite().getAllStatus();
		model.setRowCount(0);
		Object[] row = new Object[2];
		for(int i = 0; i < status.size(); i++) {
			row[0] = i + 1;
			row[1] = status.get(i);
			model.addRow(row);
		}
	}
	
	private void updateTable() {
		LinkedList<Link> links = new SqlLite().getGoogleLinks();
		DefaultTableModel model = (DefaultTableModel) formLinkTable.getModel();
		model.setRowCount(0);
		Object[] row = new Object[2];
		for(int i = 0; i < links.size(); i++) {
			row[0] = links.get(i).date;
			row[1] = links.get(i).link;
			model.addRow(row);
		}
	}
}
