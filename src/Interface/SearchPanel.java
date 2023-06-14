package Interface;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import Swing.CustomeTextField;
import Swing.RoundPanel;
import Swing.TableDark;
import Swing.TextFieldAnimation;
import Utilities.Status;
import Utilities.Student;
import database.SqlLite;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.SwingConstants;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.FlowLayout;
import javax.swing.border.TitledBorder;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JTextArea;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;

public class SearchPanel extends JPanel {
	private LinkedList<String> statustList = new SqlLite().getAllStatus();
	private JTextField searchBar;
	private JTextField firstNameField;
	private JTextField lastNameField;
	private JComboBox genderComboBox;
	private int selectedStudent = -1;
	private int selectedStatus = -1;
	private JScrollPane studentTableScrollPane = new javax.swing.JScrollPane();
	private JScrollPane statusTableScrollPane = new javax.swing.JScrollPane();
	private JTable studentTable;
	private boolean isEdited = false;
	private boolean isEventChange = false;
	private JTable statusTable;
	private JTextField middleField;
	private CustomeTextField emailField;
	private JLabel idLabel;

	//1150-671
	public SearchPanel(int width, int height, JFrame frame) {
//		width = 1150;
//		height = 671;
		setBackground(new Color(0, 0, 0));
		setSize(width, height);
		setLayout(null);
		
		// -------------------------------------------- Side Bar, TextFields and Buttons
		JPanel sideBar = new RoundPanel();
		sideBar.setBounds(0, 5, width/4 - 5, 600);
		sideBar.setBackground((new Color(0, 0, 0)));
		add(sideBar);
		sideBar.setLayout(null);
		
		frame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent evt) {
		    	String changes = isEdited || isEventChange ? "There are unsaved changes, do you still want to exit?": "Are you sure you want to exit?";
		        int resp = JOptionPane.showConfirmDialog(frame, changes,
		            "Exit?", JOptionPane.YES_NO_OPTION);

		        if (resp == JOptionPane.YES_OPTION) {
		            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		        } else {
		            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		        }
		    }
		});
		
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
					}else if(searchQuery.equals("")) {
						
					}else {
						 result = new SqlLite().getStudent(searchQuery);
					}
					System.out.println(result);
					if(result != null) {
						updateStudentTable(result);		
					}else {
						clearStudentTable();
					}
					clearStatusTable();
					selectedStudent = -1;
					selectedStatus = -1;
					isEdited = false;
					isEventChange = false;
					firstNameField.setText("");
					lastNameField.setText("");
					middleField.setText("");
					emailField.setText("");
					genderComboBox.setSelectedIndex(0);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});

		searchBar.setBounds(30, 65, 152, 48);
		sideBar.add(searchBar);
		searchBar.setText("Search...");
		searchBar.setFont(new Font("Tahoma", Font.PLAIN, 20));
		searchBar.setColumns(10);
		
		JButton editBtn = new JButton("Save Edit");
		editBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String id = idLabel.getText();
				String firstName = firstNameField.getText();
				String middleName = middleField.getText();
				String lastName = lastNameField.getText();
				String email = emailField.getText();
				String sex = genderComboBox.getSelectedItem().toString().equals("Female") ? "F" : "M";
				
				int choice = JOptionPane.showConfirmDialog(null, "Do you want to change student "+firstName+" ?");
				if(JOptionPane.YES_OPTION == choice) {
					if(new SqlLite().updateStudent(id, firstName, middleName, lastName, email, sex)) {
						JOptionPane.showMessageDialog(null, "Success");
					}else {
						JOptionPane.showMessageDialog(null, "Failed");
					}
				}
				
			}
		});
		editBtn.setBounds(30, 510, 106, 33);
		sideBar.add(editBtn);
		
		JButton deleteBtn = new JButton("Delete");
		deleteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedStudent > -1) {

					int selectedStudentId = (int) studentTable.getValueAt(selectedStudent, 0);
					String name = studentTable.getValueAt(selectedStudent, 1).toString() + studentTable.getValueAt(selectedStudent, 2).toString();
					int choice = JOptionPane.showConfirmDialog(null, "Do you want to delete student "+name);
					if(JOptionPane.YES_OPTION == choice) {
						if(new SqlLite().deleteStudent(selectedStudentId)) {
							JOptionPane.showMessageDialog(null, "Student "+name+" is Deleted");
							clearStatusTable();
							DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
							model.removeRow(selectedStudent);
							selectedStudent = -1;
							isEdited = false;
							isEventChange = false;
						}else{
							JOptionPane.showMessageDialog(null, "Fail");
						}
					}
				}
			}
		});
		deleteBtn.setBounds(146, 510, 112, 33);
		sideBar.add(deleteBtn);
		
		firstNameField = new CustomeTextField("First Name");
		firstNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedStudent > -1) {
					isEdited = true;
					System.out.println("Student Edited");
				}
			}
		});
		firstNameField.setBounds(40, 134, 222, 50);
		sideBar.add(firstNameField);
		firstNameField.setColumns(10);
		
		lastNameField =  new CustomeTextField("Last Name");
		lastNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedStudent > -1) {
					isEdited = true;
					System.out.println("Student Edited");
				}
			}
		});
		lastNameField.setColumns(10);
		lastNameField.setBounds(40, 207, 222, 50);
		sideBar.add(lastNameField);
		
		String[] genderList = {"---","Male", "Female"};
		genderComboBox = new JComboBox<Object>(genderList);
		genderComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(selectedStudent > -1) {
					isEdited = true;
					System.out.println("Student Edited");
				}
			}
		});
		genderComboBox.setBounds(117, 436, 121, 33);
		sideBar.add(genderComboBox);
		
		JLabel lblNewLabel_2 = new JLabel("Gender :");
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_2.setBounds(49, 434, 58, 33);
		sideBar.add(lblNewLabel_2);
		
		JLabel lblNewLabel = new JLabel("Search * to get all Student");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(36, 11, 222, 68);
		sideBar.add(lblNewLabel);
		
		middleField = new CustomeTextField("Middle Name");
		middleField.setColumns(10);
		middleField.setBounds(40, 279, 222, 50);
		sideBar.add(middleField);
		
		emailField = new CustomeTextField("Email");
		emailField.setColumns(10);
		emailField.setBounds(40, 350, 222, 50);
		sideBar.add(emailField);
		
		idLabel = new JLabel("");
		idLabel.setBounds(117, 411, 46, 14);
		idLabel.setVisible(false);
		sideBar.add(idLabel);
		
		JButton updateStatusBtn = new JButton("Status");
		updateStatusBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statustList = new SqlLite().getAllStatus();
			}
		});
		updateStatusBtn.setBounds(192, 62, 80, 51);
		sideBar.add(updateStatusBtn);
		
		JPanel ResultListPanel = new JPanel();
		ResultListPanel.setBackground(Color.BLACK);
		ResultListPanel.setBounds(sideBar.getWidth(), 31, 440, 540);
		add(ResultListPanel);
		ResultListPanel.setLayout(new BorderLayout(0, 0));
		
		
		studentTable = new TableDark();
		studentTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					selectedStudent = studentTable.getSelectedRow();
					firstNameField.setText(studentTable.getValueAt(selectedStudent, 1).toString());
					lastNameField.setText(studentTable.getValueAt(selectedStudent, 3).toString());
					middleField.setText(studentTable.getValueAt(selectedStudent, 2).toString());
					emailField.setText(studentTable.getValueAt(selectedStudent, 4).toString());
					idLabel.setText(studentTable.getValueAt(selectedStudent, 0).toString());
					String sex = studentTable.getValueAt(selectedStudent, 5).toString();
					int sexForComboBox = sex == "M" ? 1 : 2;
					System.out.println(sex);
					genderComboBox.setSelectedIndex(sexForComboBox);
					int studentId = (int)studentTable.getValueAt(selectedStudent, 0);
					LinkedList<Status> status = new SqlLite().getStatus(studentId);
					updateStatusTable(status);

					
					
					isEdited = false;
					isEventChange = false;

				} catch (Exception e2) {
				e2.printStackTrace();
				}
			}
		});
		studentTable.setBackground(Color.GRAY);
		studentTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "First Name", "Middle Name","Last Name", "Email", "Sex"
			}
		));
		studentTable.getColumnModel().getColumn(0).setPreferredWidth(30);

        JTableHeader tableHeader = studentTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 15));
        ResultListPanel.add(tableHeader, BorderLayout.NORTH);
        studentTableScrollPane.setViewportView(studentTable);
		ResultListPanel.add(studentTableScrollPane, BorderLayout.CENTER);
		
		JPanel StatusListPanel = new RoundPanel();
		StatusListPanel.setBounds(ResultListPanel.getWidth() + ResultListPanel.getX()  + 20, 31, 310, 544);
		add(StatusListPanel);
		StatusListPanel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("STATUS");
		lblNewLabel_1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 20));
		lblNewLabel_1.setBounds(46, 23, 216, 64);
		StatusListPanel.add(lblNewLabel_1);
		
		JPanel statusTablePanel = new JPanel();
		statusTablePanel.setBounds(35, 203, 240, 312);
		StatusListPanel.add(statusTablePanel);
		statusTablePanel.setLayout(new BorderLayout(0, 0));
		
		statusTable = new JTable();
		statusTable.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(selectedStudent > -1) {
					isEventChange = true;
					System.out.println("Status Change");
				}
			}
		});
		statusTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectedStatus = statusTable.getSelectedRow();
			}
		});
		statusTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Status", "Detail", "Year"
			}
		));
		statusTable.getColumnModel().getColumn(0).setPreferredWidth(25);
		JTableHeader statusTableHeader = statusTable.getTableHeader();
		statusTableHeader.setFont(new Font("Arial", Font.BOLD, 15));
		statusTablePanel.add(statusTableHeader, BorderLayout.NORTH);
		statusTableScrollPane.setViewportView(statusTable);
		statusTablePanel.add(statusTableScrollPane, BorderLayout.CENTER);
		
		JButton deleteEventBtn = new JButton("Delete Status");
		deleteEventBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedStatus > -1) {
					int selectedEventID = (int) statusTable.getValueAt(selectedStatus, 0);
					if(new SqlLite().deleteStatus(selectedEventID)) {
						int choice = JOptionPane.showConfirmDialog(null, "Are you sure to delete Event "+statusTable.getValueAt(selectedStatus, 1)+"?");
						if(choice == JOptionPane.YES_OPTION) {
							DefaultTableModel model = (DefaultTableModel) statusTable.getModel();
							model.removeRow(selectedStatus);
							selectedStatus = -1;
							isEventChange = false;
							JOptionPane.showMessageDialog(null, "Success");
						}
					}else{
						JOptionPane.showMessageDialog(null, "Fail");
					}
						
				}else {
					JOptionPane.showMessageDialog(null, "Select Status First");
				}
			}
		});
		deleteEventBtn.setBounds(102, 158, 106, 33);
		StatusListPanel.add(deleteEventBtn);
		
		JButton addEventBtn = new JButton("Add Status");
		addEventBtn.setBounds(169, 114, 106, 33);
		StatusListPanel.add(addEventBtn);
		
		JButton editEventBtn = new JButton("Edit Status");
		editEventBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedStatus > -1) {
					Status newStatus = new Status();
					newStatus.id = (int) statusTable.getValueAt(selectedStatus, 0);
					newStatus.event = (String) statusTable.getValueAt(selectedStatus, 1);
					newStatus.detail = (String) statusTable.getValueAt(selectedStatus, 2);
					newStatus.date = (int) statusTable.getValueAt(selectedStatus, 3);
					
					JPanel newEventPanel = new RoundPanel();
					newEventPanel.setLayout(null);
					newEventPanel.setSize(new Dimension(200, 200));
					
					JLabel lblFirstName = new JLabel("Event :");
					lblFirstName.setFont(new Font("Tahoma", Font.PLAIN, 15));
					lblFirstName.setBounds(20, 35, 145, 43);
					newEventPanel.add(lblFirstName);
					
					JLabel lblLastName = new JLabel("Detail :");
					lblLastName.setFont(new Font("Tahoma", Font.PLAIN, 15));
					lblLastName.setBounds(20, 108, 145, 43);
					newEventPanel.add(lblLastName);
					
					JLabel lblYear = new JLabel("Year :");
					lblYear.setFont(new Font("Tahoma", Font.PLAIN, 15));
					lblYear.setBounds(20, 151, 145, 43);
					newEventPanel.add(lblYear);
					
					JComboBox<Object> statusComboBox = new JComboBox<Object>(statustList.toArray());
					statusComboBox.setBounds(113, 35, 145, 43);
					newEventPanel.add(statusComboBox);
					

					JTextArea detailField = new JTextArea(newStatus.detail);
					JScrollPane scrollpane = new JScrollPane(detailField);
					scrollpane.setBounds(113, 85, 200, 60);
					newEventPanel.add(scrollpane);
					
					JTextField yearField = new JTextField(Integer.toString(newStatus.date));
					yearField.setColumns(10);
					yearField.setBounds(113, 159, 150, 30);
					newEventPanel.add(yearField);
					UIManager.put("OptionPane.minimumSize",new Dimension(400, 250));      
					int result = JOptionPane.showConfirmDialog(null, newEventPanel, "Update Status",JOptionPane.OK_CANCEL_OPTION);

					UIManager.put("OptionPane.minimumSize",new Dimension(200, 100)); 
					if (result == JOptionPane.OK_OPTION) {
						String event = statusComboBox.getSelectedItem().toString();
						System.out.println(event);
						int eventId = new SqlLite().getStatusId(event);
						System.out.println(eventId);
						String detail = detailField.getText();
						try {
							int year = Integer.parseInt(yearField.getText());

							newStatus.eventId = eventId;
							newStatus.detail = detail;
							newStatus.date = year;
						
							String message = new SqlLite().updateStatus(newStatus);
							JOptionPane.showMessageDialog(null, message);
						} catch (Exception e2) {
							JOptionPane.showMessageDialog(null, "Invalid Inputs");
						}
				    }
					
					
					
					
//					String result = new SqlLite().updateStatus(newStatus);
//					JOptionPane.showMessageDialog(null, result);
					isEventChange = false;
				}else {
					JOptionPane.showMessageDialog(null, "Select Status First");
				}
			}
		});
		editEventBtn.setBounds(46, 114, 106, 33);
		StatusListPanel.add(editEventBtn);
		
		addEventBtn.addActionListener(new ActionListener() {
			private JComboBox<Object> statusComboBox;

			public void actionPerformed(ActionEvent e) {
				try {
					int studentId = (int)studentTable.getValueAt(selectedStudent, 0);
					
					JPanel newEventPanel = new RoundPanel();
					newEventPanel.setLayout(null);
					newEventPanel.setSize(new Dimension(200, 200));
					
					JLabel lblFirstName = new JLabel("Event :");
					lblFirstName.setFont(new Font("Tahoma", Font.PLAIN, 15));
					lblFirstName.setBounds(20, 35, 145, 43);
					newEventPanel.add(lblFirstName);
					
					JLabel lblLastName = new JLabel("Detail :");
					lblLastName.setFont(new Font("Tahoma", Font.PLAIN, 15));
					lblLastName.setBounds(20, 108, 145, 43);
					newEventPanel.add(lblLastName);
					
					JLabel lblYear = new JLabel("Year :");
					lblYear.setFont(new Font("Tahoma", Font.PLAIN, 15));
					lblYear.setBounds(20, 151, 145, 43);
					newEventPanel.add(lblYear);
					
					statusComboBox = new JComboBox<Object>(statustList.toArray());
					statusComboBox.setBounds(113, 35, 145, 43);
					newEventPanel.add(statusComboBox);
					

					JTextArea detailField = new JTextArea();
					JScrollPane scrollpane = new JScrollPane(detailField);
					scrollpane.setBounds(113, 85, 200, 60);
					newEventPanel.add(scrollpane);
			
					JTextField yearField = new JTextField();
					yearField.setColumns(10);
					yearField.setBounds(113, 159, 150, 30);
					newEventPanel.add(yearField);
					
					UIManager.put("OptionPane.minimumSize",new Dimension(400, 250));      
					int result = JOptionPane.showConfirmDialog(null, newEventPanel, "Add Status ",JOptionPane.OK_CANCEL_OPTION);

					UIManager.put("OptionPane.minimumSize",new Dimension(200, 100)); 
					if (result == JOptionPane.OK_OPTION) {
						String event = statusComboBox.getSelectedItem().toString();
						int eventId = new SqlLite().getStatusId(event);
						System.out.println(eventId);
						String detail = detailField.getText();
						int year = Integer.parseInt(yearField.getText());
						if(new SqlLite().addEvent(studentId, eventId, detail, year)) {
							JOptionPane.showMessageDialog(null, "Success");
						}
				    }
				}
				catch(Exception e2) {
					JOptionPane.showMessageDialog(null, "Something Went Wrong");
				}
			}
		});
		
	
	}
	public void updateStudentTable(LinkedList<Student> resultStudent) {
		DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
		model.setRowCount(0);
		Object[] row = new Object[6];
		for(int i = 0; i < resultStudent.size(); i++) {
			row[0] = resultStudent.get(i).id;
			row[1] = resultStudent.get(i).firstName;
			row[2] = resultStudent.get(i).middleName;
			row[3] = resultStudent.get(i).lastName;
			row[4] = resultStudent.get(i).email;
			row[5] = resultStudent.get(i).sex;
			model.addRow(row);
		}
	}
	public void updateStatusTable(LinkedList<Status> resultStatus) {
		DefaultTableModel model = (DefaultTableModel) statusTable.getModel();
		model.setRowCount(0);
		Object[] row = new Object[4];
		for(int i = 0; i < resultStatus.size(); i++) {
			row[0] = resultStatus.get(i).id;
			row[1] = resultStatus.get(i).event;
			row[2] = resultStatus.get(i).detail;
			row[3] = resultStatus.get(i).date;
			model.addRow(row);
		}
	}
	public void clearStatusTable() {
		DefaultTableModel model = (DefaultTableModel) statusTable.getModel();
		model.setRowCount(0);
	};
	public void clearStudentTable() {
		DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
		model.setRowCount(0);
	}
}

