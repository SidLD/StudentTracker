package Interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import Swing.RoundPanel;
import Swing.TableDark;
import Swing.TextFieldAnimation;
import Utilities.Mailing;
import Utilities.Student;
import database.SqlLite;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

class StudentEmail{
	public String name;
	public String email;
	public StudentEmail() {}
}
public class MailingPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	private int width;
	private int height;
	private TextFieldAnimation searchBar;
	private TableDark studentTable;
	private JScrollPane studentTableScrollPane = new JScrollPane();
	private TableDark selectedStudentTable;
	private JScrollPane selectedTableScrollPane = new JScrollPane();
	LinkedList<StudentEmail> selectedEmails = new LinkedList<StudentEmail>();

	private int selectedStudent = -1;
	private int removeSelectedStudent = -1;
 
	
	public MailingPanel(int width, int height) {
		this.width = width;
		this.height = height;
//		width = 1150;
//		height = 671;
		setBackground(new Color(0, 0, 0));
		setSize(width, height);
		setLayout(null);
		
		searchBar = new TextFieldAnimation();
		searchBar.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				search();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				search();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				search();
			}
			private void search() {
				try {
					String searchQuery = searchBar.getText();
					LinkedList<Student> result = null;
					if(searchQuery.equals("*")) {
						result = new SqlLite().getAllStudent();
						updateStudentTable(result);	
					}else if(searchQuery.equals("")) {

					}else {
						 result = new SqlLite().getStudent(searchQuery);
						updateStudentTable(result);	
					}
				}catch(Exception e2) {
				}
			}
		});
		
			searchBar.setBounds(30, 65, 222, 48);
			add(searchBar);
			searchBar.setText("Search...");
			searchBar.setFont(new Font("Tahoma", Font.PLAIN, 20));
			searchBar.setColumns(10);
			
		
			JPanel linkListPanel = new RoundPanel();
			linkListPanel.setBounds(20, searchBar.getY() + 100, width/3, height/2);
			linkListPanel.setLayout(null);
			add(linkListPanel);
			
			JPanel listTablePanel = new RoundPanel();
			listTablePanel.setBounds(0, 0,  width/3, height/3 * 2);
			listTablePanel.setLayout(new BorderLayout(0, 0));
			linkListPanel.add(listTablePanel);
			
			studentTable = new TableDark();
			studentTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					selectedStudent = studentTable.getSelectedRow();
				}
			});
			studentTable.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"NAME", "GMAIL"
					}
				));
			studentTable.getColumnModel().getColumn(0).setPreferredWidth(25);
			JTableHeader statusTableHeader = studentTable.getTableHeader();
			statusTableHeader.setFont(new Font("Arial", Font.BOLD, 15));
			listTablePanel.add(statusTableHeader, BorderLayout.NORTH);
			studentTableScrollPane .setBounds(0, 0,  width/3, height/3 * 2);
			studentTableScrollPane.setViewportView(studentTable);
			listTablePanel.add(studentTableScrollPane, BorderLayout.CENTER);
			
			JPanel SelectedList = new RoundPanel();
			SelectedList.setBounds(linkListPanel.getWidth() + linkListPanel.getX() + 100, searchBar.getY() + 100, width/3, height/2);
			SelectedList.setLayout(null);
			add(SelectedList);
			
			JPanel SelectedTablePanel = new RoundPanel();
			SelectedTablePanel.setBounds(0, 0,  width/3, height/3 * 2);
			SelectedTablePanel.setLayout(new BorderLayout(0, 0));
			SelectedList.add(SelectedTablePanel);
			
			selectedStudentTable = new TableDark();
			selectedStudentTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					removeSelectedStudent = selectedStudentTable.getSelectedRow();
				}
			});
			selectedStudentTable.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"NAME", "GMAIL"
					}
				));
			studentTable.getColumnModel().getColumn(0).setPreferredWidth(25);
			JTableHeader selectedHeader = studentTable.getTableHeader();
			selectedHeader.setFont(new Font("Arial", Font.BOLD, 15));
			SelectedTablePanel.add(selectedHeader, BorderLayout.NORTH);
			selectedTableScrollPane .setBounds(0, 0,  width/3, height/3 * 2);
			selectedTableScrollPane.setViewportView(selectedStudentTable);
			SelectedTablePanel.add(selectedTableScrollPane, BorderLayout.CENTER);
			
			JButton addStudentBtn = new JButton("Add to Mailing");
			addStudentBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(selectedStudent == -1) {
						JOptionPane.showMessageDialog(null, "Please select a student");
					}else {
						StudentEmail newStudentEmail = new StudentEmail();
						newStudentEmail.name = studentTable.getValueAt(selectedStudent, 0).toString();
						newStudentEmail.email = studentTable.getValueAt(selectedStudent, 1).toString();
						selectedEmails.add(newStudentEmail);
						updateSelectedStudentTable();
					}
				}
			});
			addStudentBtn.setFont(new Font("Tahoma", Font.PLAIN, 15));
			addStudentBtn.setBounds(142, 530, 143, 30);
			add(addStudentBtn);
			
			JButton btnRemoveFromMailing = new JButton("Remove From Mailing");
			btnRemoveFromMailing.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(removeSelectedStudent == -1) {
						StudentEmail selectedStudentEmail = new StudentEmail();
						selectedStudentEmail.name = selectedStudentTable.getValueAt(removeSelectedStudent, 0).toString();
						selectedStudentEmail.email = selectedStudentTable.getValueAt(removeSelectedStudent, 1).toString();
						selectedEmails.remove(selectedStudentEmail);
						updateSelectedStudentTable();
					}else {
						JOptionPane.showMessageDialog(null, "Please Select Student");
					}
				}
			});
			btnRemoveFromMailing.setFont(new Font("Tahoma", Font.PLAIN, 15));
			btnRemoveFromMailing.setBounds(610, 530, 173, 30);
			add(btnRemoveFromMailing);
			
			JButton btnSendMail = new JButton("Send Mail");
			btnSendMail.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnSendMail.setEnabled(false);
					btnSendMail.setText("Sending Mail");
					String link = "";
					try {
						link = JOptionPane.showInputDialog(null,"Input Form Link");
						if(link.isEmpty()) {
							Mailing mail = new Mailing();
							String email = "ohspadm@gmail.com";
							String machineKey = "ylbm ebxg jtdo ykaj";
//							String password = "passwordinesanohsp";
							
							for (StudentEmail studentEmail : selectedEmails) {
								
								mail.sendMail(email, machineKey, studentEmail.email, "Hello There Please answer this Form : "+link);
								
							}
						}else {
							JOptionPane.showMessageDialog(null, "Invalid Form Link or Empty");
						}
					}catch(Exception e4) {
					}
					btnSendMail.setText("Send Mail");
					btnSendMail.setEnabled(true);
				}
			});
			btnSendMail.setFont(new Font("Tahoma", Font.PLAIN, 20));
			btnSendMail.setBounds(735, 61, 151, 59);
			add(btnSendMail);
			
			JLabel resultLabel = new JLabel("Result");
			resultLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
			resultLabel.setForeground(Color.WHITE);
			resultLabel.setBounds(addStudentBtn.getX(), linkListPanel.getY() - 40, 159, 30);
			add(resultLabel);
			
			JLabel selectedLabel = new JLabel("Selected");
			selectedLabel.setHorizontalAlignment(SwingConstants.CENTER);
			selectedLabel.setForeground(Color.WHITE);
			selectedLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			selectedLabel.setBounds(btnRemoveFromMailing.getX(), SelectedList.getY() - 40, 159, 30);
			add(selectedLabel);
			
			
			
	}

	private void updateSelectedStudentTable() {
		DefaultTableModel model = (DefaultTableModel) selectedStudentTable.getModel();
		model.setRowCount(0);
		Object[] row = new Object[2];
		for(int i = 0; i < selectedEmails.size(); i++) {
			row[0] = selectedEmails.get(i).name;
			row[1] = selectedEmails.get(i).email;
			model.addRow(row);
		}
		
	}
	private void updateStudentTable(LinkedList<Student> resultStudent) {
		DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
		model.setRowCount(0);
		Object[] row = new Object[2];
		for(int i = 0; i < resultStudent.size(); i++) {
			row[0] = resultStudent.get(i).lastName+", "+resultStudent.get(i).firstName + ", "+resultStudent.get(i).middleName;
			row[1] = resultStudent.get(i).email;
			model.addRow(row);
		}
	}
}
