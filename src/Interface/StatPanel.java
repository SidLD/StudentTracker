package Interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JPanel;

import Swing.RoundPanel;
import Utilities.StatusData;
import database.SqlLite;
import javaswingdev.chart.ModelPieChart;
import javaswingdev.chart.PieChart;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.time.Year;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

import Chart.CurveChart;
import Chart.GaugeChart;
import Chart.ModelChart;

public class StatPanel extends JPanel {
	private Chart.LineChart chart;
	private JPanel TimeLinePanel;
	int width, height;
	private PieChart statusPieChart;
	private LinkedList<String> status;
	private PieChart genderPieChart;
	private GaugeChart updateCount;

	public StatPanel(int widthTemp, int heightTemp, Chart.LineChart chartTemp) {
//		widthTemp = 1150;
//		heightTemp = 671;
		width = widthTemp;
		height = heightTemp;
		System.out.println(width+ " - "+height);
		setBackground(new Color(0, 0, 0));
		setSize(width, height);
		setLayout(null);
		
		chart = chartTemp;
		
		TimeLinePanel = new JPanel();
		TimeLinePanel.setBounds(10, 10, width-100, (height/2) - 20);
		TimeLinePanel.setBackground(Color.BLACK);
        TimeLinePanel.setLayout(null);
		add(TimeLinePanel);
		
		JButton updateDataBtn = new JButton("Update Data");
		updateDataBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initChart();
				initPieChart();
				initNumberOfUpdateThisYear(Year.now().getValue());
			}
		});
		updateDataBtn.setBounds(20, 330, 111, 23);
		add(updateDataBtn);
		
		JPanel OtherStatPanel = new RoundPanel();
		OtherStatPanel.setBackground(Color.BLACK);
		OtherStatPanel.setBounds(10, TimeLinePanel.getHeight() + 25, width-20, height/2 - 75);
		add(OtherStatPanel);
		OtherStatPanel.setLayout(null);
		
		JPanel PiePanel = new RoundPanel();
		PiePanel.setBounds(0, 0, OtherStatPanel.getWidth() -90, OtherStatPanel.getHeight());
		OtherStatPanel.add(PiePanel);
		PiePanel.setLayout(null);
		
		statusPieChart = new PieChart();
		statusPieChart.setFont(new Font("Tahoma", Font.PLAIN, 13));
		statusPieChart.setBounds(0, -15, PiePanel.getWidth()/3, PiePanel.getHeight());
		PiePanel.add(statusPieChart);
		
		genderPieChart = new PieChart();
		genderPieChart.setFont(new Font("Tahoma", Font.PLAIN, 13));
		genderPieChart.setBounds((PiePanel.getWidth()/3), -15, PiePanel.getWidth()/3, PiePanel.getHeight() );
		PiePanel.add(genderPieChart);
		
		updateCount = new Chart.GaugeChart();
		updateCount.setFont(new Font("Tahoma", Font.PLAIN, 14));
		updateCount.setBounds(((PiePanel.getWidth()/3) * 2) + 50, 15, PiePanel.getWidth()/4, PiePanel.getHeight()-50);
		PiePanel.add(updateCount);
		
		JLabel statusLabel = new JLabel("Status");
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		statusLabel.setBounds((statusPieChart.getWidth()/2 + statusPieChart.getX()) - 104, 223, 208, 26);
		PiePanel.add(statusLabel);
		
		JLabel genderLabel = new JLabel("Gender");
		genderLabel.setHorizontalAlignment(SwingConstants.CENTER);
		genderLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		genderLabel.setBounds((genderPieChart.getWidth()/2 + genderPieChart.getX()) - 104, 223, 208, 26);
		PiePanel.add(genderLabel);
		
		initChart();
		initPieChart();
	
		initNumberOfUpdateThisYear(Year.now().getValue());
	}
	
	private void initNumberOfUpdateThisYear(int year) {
		int numberOfUpdateThisYear = new SqlLite().getUpdateCount( year);
		int maxStudent = new SqlLite().getStudentCount();
		updateCount.resetMessage();
		updateCount.setMessage(" or "+numberOfUpdateThisYear+" out of "+maxStudent+" update this Year "+year);
		float temp = (float) numberOfUpdateThisYear / maxStudent;
		double percentage = temp * 100;
		System.out.println(temp);
		updateCount.setValue((int)percentage); 
	}
	private void initPieChart() {
		statusPieChart.clearData();
		genderPieChart.clearData();
		Random rand = new Random();
	    float r;
	    float g;
	    float b;
		Color randomColor;
		for (String string : status) {
		     r = rand.nextFloat();
		     g = rand.nextFloat();
		     b = rand.nextFloat();
		     randomColor = new Color(r, g, b);
			        
		    int statusId = new SqlLite().getStatusId(string);
			int count = new SqlLite().getStatusCount(statusId);
			statusPieChart.addData(new ModelPieChart(string, count, randomColor ));
		}
	    r = rand.nextFloat();
	    g = rand.nextFloat();
	    b = rand.nextFloat();
	    randomColor = new Color(r, g, b);
		int maleCount = new SqlLite().getGenderCount("M");
		genderPieChart.addData(new ModelPieChart("Male", maleCount, randomColor ));
		
		r = rand.nextFloat();
		g = rand.nextFloat();
		b = rand.nextFloat();
		randomColor = new Color(r, g, b);
		int femaleCount = new SqlLite().getGenderCount("F");
		genderPieChart.addData(new ModelPieChart("Female", femaleCount, randomColor ));

	}

	private int countSum(LinkedList<Integer> temp) {
		int sum = 0;
		for (Integer integer : temp) {
			sum += integer;
		}
		return sum;
	}
	private void initChart() {
		chart.clear();
		chart.setName("TimeLine");
		chart.setBounds(0, 0, 1000, 200);
		
	        LinkedList<Integer> years = new SqlLite().getYears();
	        status = new SqlLite().getAllStatus();
	        Random rand = new Random();
	        for (String tempStatus : status) {
	        	float r = rand.nextFloat();
	        	float g = rand.nextFloat();
	        	float b = rand.nextFloat();
	        	Color randomColor = new Color(r, g, b);
	        	randomColor.brighter();
		        chart.addLegend(tempStatus, randomColor, randomColor);
			}
			for (Integer year : years) {
				LinkedList<Double> data = new LinkedList<Double>();
				int sum = 0;
				LinkedList<Integer> countList = new LinkedList<Integer>();
				for (String tempStatus : status) {	
					int count = new SqlLite().getStatusCountInYear(year, tempStatus);
					countList.add(count);
				}
		        sum = countSum(countList);
				for (Integer count : countList) {
					double temp = count;
					data.add(temp);
				}
				System.out.println(countList.toString());
		        chart.addData(new ModelChart(year.toString(), getDoubleArray(data)));
			}
			chart.start();
	        TimeLinePanel.setLayout(new BorderLayout(0, 0));
	        TimeLinePanel.add(chart);
	        
		}
	private double[] getDoubleArray(LinkedList<Double> d) {
		double result[] = new double[d.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = d.get(i);
		}
		return result;
	}
}
