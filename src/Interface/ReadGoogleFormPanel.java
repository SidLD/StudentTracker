package Interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.services.forms.v1.model.ListFormResponsesResponse;

import GoogleForm.Main;
import Swing.RoundPanel;
import Swing.TableDark;
import Utilities.Student;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.awt.event.ActionEvent;


class ResponseData{
	public String name;
	public int year;
	public String details;
	public String event;
	public ResponseData() {}
	@Override
	public String toString() {
		return "ResponseData [name=" + name + ", year=" + year + ", details=" + details + ", event=" + event + "]";
	}
}

public class ReadGoogleFormPanel extends JPanel {

	private int width;
	private int height;
	private JTextField textField;
	private TableDark statusTable;
	private JScrollPane statusTableScrollPane = new JScrollPane();

	/**
	 * Create the panel.
	 * @param height 
	 * @param width 
	 */
	public ReadGoogleFormPanel(int width, int height) {
		this.width = width;
		this.height = height;
		setBackground(new Color(0, 0, 0));
		setSize(this.width, this.height);
		setLayout(null);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textField.setBounds(150, 50, 452, 54);
		add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Form ID :");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel.setBounds(50, 50, 90, 52);
		add(lblNewLabel);
		
		JButton getResponseBtn = new JButton("Get Responses");
		getResponseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getResponseBtn.setEnabled(false);
				String url = textField.getText();
				String token;
				LinkedList<ResponseData> dataList = new LinkedList<ResponseData>();
				try {
					token = Main.getAccessToken();
					ListFormResponsesResponse data = Main.readResponses(url, token);
					
					JSONObject res = new JSONObject(data);
					JSONArray responses = res.getJSONArray("responses");
					for (int i = 0; i < responses.length(); i++) {
						ResponseData newData = new ResponseData();
						JSONObject response = responses.getJSONObject(i);
						JSONObject answers = response.getJSONObject("answers");
						LinkedList<String> answersCode = new LinkedList<String>();
						Iterator<?> answerIds = answers.keys();
						while(answerIds.hasNext()) {
							answersCode.add(answerIds.next().toString());
						}
						int answerIndex = 0;
						for (String code : answersCode) {
							JSONObject codeAnswer = answers.getJSONObject(code);
							JSONObject textAnswers = codeAnswer.getJSONObject("textAnswers");
							JSONArray finalAnswer = textAnswers.getJSONArray("answers");
							JSONObject value = finalAnswer.getJSONObject(0);
							System.out.println(value.getString("value") + " - "+answerIndex);
							switch (answerIndex) {
//							1uM7NJVoTKFfpTgTO7eEUx2tJZgN6W9triq2MuMW81oY
							case 0: {
								newData.event = value.getString("value");
								break;
							}
							case 1: {
								newData.name = value.getString("value");
								break;
							}
							case 2: {
								newData.details = value.getString("value");
								break;
							}
							default:
								throw new IllegalArgumentException("Unexpected value: " + answerIndex);
							}
							answerIndex++;
						}
						dataList.add(newData);
						
					}
					updateTable(dataList);
						
				} catch (IOException | JSONException e1) {
					JOptionPane.showMessageDialog(null, "Your clock must be on time or contact the developer");
				}

				getResponseBtn.setEnabled(true);
				
			}
		});
		getResponseBtn.setFont(new Font("Tahoma", Font.PLAIN, 15));
		getResponseBtn.setBounds(textField.getX() + textField.getWidth() + 30, 50, 140, 54);
		add(getResponseBtn);
		
		JPanel linkListPanel = new RoundPanel();
		linkListPanel.setBounds(20, getResponseBtn.getY() + 150, width/3 * 2, height/2);
		linkListPanel.setLayout(null);
		add(linkListPanel);
		
		JPanel listTablePanel = new RoundPanel();
		listTablePanel.setBounds(0, 0,  width/3 * 2, height/3 * 2);
		listTablePanel.setLayout(new BorderLayout(0, 0));
		linkListPanel.add(listTablePanel);
		
		statusTable = new TableDark();
		statusTable.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"NAME", "STATUS","DETAILS"
				}
			));
		statusTable.getColumnModel().getColumn(0).setPreferredWidth(25);
		JTableHeader statusTableHeader = statusTable.getTableHeader();
		statusTableHeader.setFont(new Font("Arial", Font.BOLD, 15));
		listTablePanel.add(statusTableHeader, BorderLayout.NORTH);
		statusTableScrollPane .setBounds(0, 0,  width/3, height/3 * 2);
		statusTableScrollPane.setViewportView(statusTable);
		listTablePanel.add(statusTableScrollPane, BorderLayout.CENTER);
	}
	
	private void updateTable(LinkedList<ResponseData> data) {
		DefaultTableModel model = (DefaultTableModel) statusTable.getModel();
		model.setRowCount(0);
		Object[] row = new Object[4];
		for(int i = 0; i < data.size(); i++) {
			row[0] = data.get(i).name;
			row[1] = data.get(i).event;
			row[2] = data.get(i).details;
			model.addRow(row);
		}
	}
}
