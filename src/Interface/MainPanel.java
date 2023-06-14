package Interface;

import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.LayoutManager;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import Packages.JnaFileChooser;
import Packages.JnaFileChooser.Mode;
import Packages.RoundPanel;
import Packages.TableDark;
import Swing.GlassPanePopup;
import Utilities.ButtonMenu;
import Utilities.Mailing;
import Utilities.Status;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Utilities.Student;
import database.SqlLite;
import javax.swing.JRadioButton;
//import src.OverviewPanel;

public class MainPanel {

	private JFrame frame;
	private Color mainColor = new Color(0, 102, 255);
	private Chart.LineChart chart = new Chart.LineChart();
	
	private JPanel StudentTrackingPanel;
	private JPanel StatPanel;
	private JPanel GoogleFormPanel;
	private JPanel ReadFormPanel;
	private JPanel MailingPanel;
	private boolean isStatusChange = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainPanel window = new MainPanel();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainPanel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		chart.setLocation(10, 11);
		frame = new JFrame();
		
		frame.setBackground(new Color(0, 0, 51));
		frame.getContentPane().setBackground(Color.BLACK);
//		frame.setUndecorated(true);
		frame.setBounds(50, 50 ,1300, 650);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);

		
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		JPanel SideBar = new JPanel();
		SideBar.setBounds(0, 0, 216, frame.getHeight());
		SideBar.setOpaque(false);
		frame.getContentPane().add(SideBar);
		RoundPanel SideBarMenu = new RoundPanel();
		SideBarMenu.setBackground(mainColor);
		SideBarMenu.setBounds(5, 110, 205, 497);
		SideBar.add(SideBarMenu);

		JTabbedPane MainPanel = new JTabbedPane(JTabbedPane.TOP);
		MainPanel.setBounds(216, -24, 1150, 671);
		MainPanel.setBorder(null);
		MainPanel.setBackground(Color.BLACK);
		MainPanel.setOpaque(false);
		frame.getContentPane().add(MainPanel);
		
		StatPanel = new StatPanel(MainPanel.getWidth(), MainPanel.getHeight() , chart);
		MainPanel.addTab("Stat Panel", null, StatPanel, null);
		
		StudentTrackingPanel = new SearchPanel(MainPanel.getWidth(), MainPanel.getHeight(), this.frame);
		MainPanel.addTab("Search Panel", null, StudentTrackingPanel, null);

		GoogleFormPanel = new GoogleFormPanel(MainPanel.getWidth(), MainPanel.getHeight());
		MainPanel.addTab("Google Form", null, GoogleFormPanel, null);
		
		ReadFormPanel = new ReadGoogleFormPanel(MainPanel.getWidth(), MainPanel.getHeight());
		MainPanel.addTab("Google Form Read", null, ReadFormPanel, null);
		
		MailingPanel = new MailingPanel(MainPanel.getWidth(), MainPanel.getHeight());
		MainPanel.addTab("Mailing Read", null, MailingPanel, null);
		
		
		MainPanel.setSelectedIndex(0);
		
		JButton overviewBtn = new ButtonMenu();
		overviewBtn.setIcon(new ImageIcon(getClass().getResource("/img/overview.png")));
		overviewBtn.setText("  "+"Overview");
		overviewBtn.setBounds(10, 21, 188, 60);
		overviewBtn.setFont(new Font("Tahoma", Font.PLAIN, 17));
		overviewBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainPanel.setSelectedIndex(0);
				chart.start();
			}
		});
		SideBar.setLayout(null);
		SideBarMenu.setLayout(null);
		SideBarMenu.add(overviewBtn);
		
		JButton studentViewBtn = new ButtonMenu();
		studentViewBtn.setIcon(new ImageIcon(getClass().getResource("/img/search.png")));
		studentViewBtn.setText("  "+"Student");
		studentViewBtn.setFont(new Font("Tahoma", Font.PLAIN, 17));
		studentViewBtn.setBounds(10, 79,188, 60);
		studentViewBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainPanel.setSelectedIndex(1);
			}
		});
		studentViewBtn.setFont(new Font("Tahoma", Font.PLAIN, 17));
		SideBarMenu.add(studentViewBtn);
		
		ButtonMenu generateFormBtn = new ButtonMenu();
		generateFormBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainPanel.setSelectedIndex(2);
			}
		});
		generateFormBtn.setIcon(new ImageIcon(getClass().getResource("/img/export.png")));
		generateFormBtn.setText(" Form & Status");
		generateFormBtn.setFont(new Font("Tahoma", Font.PLAIN, 17));
		generateFormBtn.setBounds(10, 195, 188, 60);
		SideBarMenu.add(generateFormBtn);
		
		
		ButtonMenu backUpBtn = new ButtonMenu();
		backUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XSSFWorkbook workbook = new XSSFWorkbook();
			    XSSFSheet sheet = workbook.createSheet("Students Data");
				LinkedList<Student> students = new SqlLite().getAllStudent();
				String header[] = {"Student Id", "First Name", "Last Name", "Gender", "Status ->", "Detail ->", "Year ->"};
				int rowCountHeader = -1;
				int colCountHeader = -1;
				Row rowHeader = sheet.createRow(++rowCountHeader);
				for (String string : header) {
					Cell cellHeader = rowHeader.createCell(++colCountHeader);
					cellHeader.setCellValue((String) string);
				}
				int rowCount = 0;
				for (Student student : students) {
					Row row = sheet.createRow(++rowCount);
					int columnCount = -1;
						Cell cellId = row.createCell(++columnCount);
						cellId.setCellValue((Integer) student.id);
						Cell cellFirstName = row.createCell(++columnCount);
						cellFirstName.setCellValue((String) student.firstName);
						Cell cellLastName = row.createCell(++columnCount);
						cellLastName.setCellValue((String) student.lastName);
						Cell cellGender = row.createCell(++columnCount);
						cellGender.setCellValue((String) student.sex);
					
					LinkedList<Status> Status = new SqlLite().getStatus(student.id);
					for (Status tempStatus : Status) {
						int columnCountForStatus = columnCount;
						Cell cellEvent = row.createCell(++columnCountForStatus);
						cellEvent.setCellValue((String) tempStatus.event);
						Cell cellDetail = row.createCell(++columnCountForStatus);
						cellDetail.setCellValue((String) tempStatus.detail);
						Cell cellYear = row.createCell(++columnCountForStatus);
						cellYear.setCellValue((Integer) tempStatus.date);
						if(!(tempStatus == Status.getLast())) {
							row = sheet.createRow(++rowCount);
						}
		            }
				}
				JnaFileChooser jnaCh = new JnaFileChooser();
				jnaCh.addFilter("Folder", ".dir");
				jnaCh.setMode(Mode.Directories);
		        boolean save = jnaCh.showOpenDialog(frame);
		        if (save) {
		        	String fileName = jnaCh.getSelectedFile().getName();
		        	String filePath = jnaCh.getSelectedFile().getAbsolutePath();
		        	System.out.println(filePath + " - "+fileName);
		        	try (FileOutputStream outputStream = new FileOutputStream(filePath+"\\Student.xlsx")) {
			            workbook.write(outputStream);
			            JOptionPane.showMessageDialog(null, "Success");
		        	} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
		        		e1.printStackTrace();
		        	} catch (IOException e1) {
		        	// TODO Auto-generated catch block
		        		e1.printStackTrace();
		        	}
		        }
			}
		});
		backUpBtn.setIcon(new ImageIcon(getClass().getResource("/img/backup.png")));
		backUpBtn.setText("BackUp Excel");
		backUpBtn.setFont(new Font("Tahoma", Font.PLAIN, 17));
		backUpBtn.setBounds(10, 426, 188, 60);
		SideBarMenu.add(backUpBtn);
		
		ButtonMenu newStudentBtn = new ButtonMenu();
		newStudentBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JPanel testPanel = new RoundPanel();
				testPanel.setLayout(null);
				testPanel.setSize(new Dimension(200, 400));
				
				JLabel lblFirstName = new JLabel("First Name :");
				lblFirstName.setFont(new Font("Tahoma", Font.PLAIN, 15));
				lblFirstName.setBounds(20, 65, 145, 43);
				testPanel.add(lblFirstName);
				
				JLabel lblLastName = new JLabel("Last Name :");
				lblLastName.setFont(new Font("Tahoma", Font.PLAIN, 15));
				lblLastName.setBounds(20, 108, 145, 43);
				testPanel.add(lblLastName);
			
				JLabel lblMiddleName = new JLabel("Middle Name :");
				lblMiddleName.setFont(new Font("Tahoma", Font.PLAIN, 15));
				lblMiddleName.setBounds(20, 108 + 43, 145, 43);
				testPanel.add(lblMiddleName);
				
				JLabel lblEmail = new JLabel("Email :");
				lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 15));
				lblEmail.setBounds(20, 108 + 43 + 43, 145, 43);
				testPanel.add(lblEmail);
			
				
				JTextField firstNameField = new JTextField();
				firstNameField.setBounds(113, 73, 150, 30);
				testPanel.add(firstNameField);
				firstNameField.setColumns(10);
				
				JTextField lastNameField = new JTextField();
				lastNameField.setColumns(10);
				lastNameField.setBounds(113, 116, 150, 30);
				testPanel.add(lastNameField);
				
				JTextField middleNameField = new JTextField();
				middleNameField.setColumns(10);
				middleNameField.setBounds(113, 116+43, 150, 30);
				testPanel.add(middleNameField);
				
				JTextField emailField = new JTextField();
				emailField.setColumns(10);
				emailField.setBounds(113, 116 + 43 +43, 150, 30);
				testPanel.add(emailField);
				
				JRadioButton femaleRadioBtn = new JRadioButton("Female");
				JRadioButton maleRadioBtn = new JRadioButton("Male");
				
				maleRadioBtn.setBounds(20,  245, 80, 23);
				maleRadioBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {	
						femaleRadioBtn.setSelected(false);
						maleRadioBtn.setSelected(true);
					}
				});
				testPanel.add(maleRadioBtn);
				
				femaleRadioBtn.setBounds(113, 245, 100, 23);
				femaleRadioBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {	
						maleRadioBtn.setSelected(false);
						femaleRadioBtn.setSelected(true);
					}
				});
				testPanel.add(femaleRadioBtn);
				
				
				testPanel.setVisible(true);
			
	
				UIManager.put("OptionPane.minimumSize",new Dimension(400, 400));      
				int result = JOptionPane.showConfirmDialog(null, testPanel, "New Student ",JOptionPane.OK_CANCEL_OPTION);
				UIManager.put("OptionPane.minimumSize",new Dimension(200, 100));  
				if (result == JOptionPane.OK_OPTION) {
					String firstName = firstNameField.getText();
					String lastName = lastNameField.getText();
					String middleName = middleNameField.getText();
					String email = emailField.getText();

			        if(firstName.length() < 3 || lastName.length() < 3 || !(femaleRadioBtn.isSelected() || maleRadioBtn.isSelected())) {
			        	JOptionPane.showMessageDialog(null, "Invalid Input");
			        }else {
			        	String sex = femaleRadioBtn.isSelected() ? "F" : "M";
			        	if(new SqlLite().addStudent(firstName, middleName, lastName, sex, email)) {
			        		JOptionPane.showMessageDialog(null, "Success");
			        	}else{
			        		JOptionPane.showMessageDialog(null, "Fail or Student Might Already be existing");
			        	}
			        }
			    }
			}
		});
		newStudentBtn.setIcon(new ImageIcon(getClass().getResource("/img/student.png")));
		newStudentBtn.setText("  "+"New Student");
		newStudentBtn.setFont(new Font("Tahoma", Font.PLAIN, 17));
		newStudentBtn.setBounds(10, 137, 188, 60);
		SideBarMenu.add(newStudentBtn);
		
		ButtonMenu readFormBtn = new ButtonMenu();
		readFormBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainPanel.setSelectedIndex(3);
			}
		});
		readFormBtn.setIcon(new ImageIcon(getClass().getResource("/img/import.png")));
		readFormBtn.setText(" Get Responses");
		readFormBtn.setFont(new Font("Tahoma", Font.PLAIN, 17));
		readFormBtn.setBounds(10, 315, 188, 60);
		SideBarMenu.add(readFormBtn);
		
		ButtonMenu mailingBtn = new ButtonMenu();
		mailingBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainPanel.setSelectedIndex(4);	
			}
		});
		mailingBtn.setIcon(new ImageIcon(getClass().getResource("/img/export.png")));
		mailingBtn.setText(" Mailing");
		mailingBtn.setFont(new Font("Tahoma", Font.PLAIN, 17));
		mailingBtn.setBounds(10, 251, 188, 60);
		SideBarMenu.add(mailingBtn);
		
	
		
		JPanel SidebarIntro = new RoundPanel();
		SidebarIntro.setBackground(mainColor);
		SidebarIntro.setBounds(5, 5, 205, 100);
		SideBar.add(SidebarIntro);
		SidebarIntro.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("OHSP ");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		lblNewLabel.setBounds(40, 17, 120, 30);
		SidebarIntro.add(lblNewLabel);
		
		JLabel lblAdmin = new JLabel("Student Tracker");
		lblAdmin.setForeground(Color.WHITE);
		lblAdmin.setFont(new Font("Arial", Font.PLAIN, 15));
		lblAdmin.setBounds(40, 47, 120, 30);
		SidebarIntro.add(lblAdmin);
	}
}
