package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

import org.apache.poi.ss.formula.functions.Now;

import Utilities.Link;
import Utilities.Status;
import Utilities.Student;

public class SqlLite {
	
	private Connection conn;
	
	public boolean connect() {
		try {
			
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:database.db");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean addStudent(String firstName, String middleName,String lastName,String sex, String email) {
		if(connect()) {
			int res = -1;
			try {
				PreparedStatement stmt = conn.prepareStatement("INSERT INTO students(first_name,middle_name, last_name, sex, email) Values(?, ?, ?)");
				stmt.setString(1, firstName);
				stmt.setString(2, middleName);
				stmt.setString(3, lastName);
				stmt.setString(4, sex);
				stmt.setString(5, email);
				res = stmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			closeConnection();
			return res == -1 ? false: true;
		}else {
			return false;	
		}
	}

	public boolean closeConnection() {
		try {
			conn.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	public LinkedList<Student> getStudent(String searchQuery) {
		LinkedList<Student> students = new LinkedList<Student>();
		if(connect()) {
			String queryStudent = "SELECT * from students where students.first_name LIKE ? or students.last_name LIKE ?";
			try {
				PreparedStatement stmt = conn.prepareStatement(queryStudent);
				stmt.setString(1, searchQuery + "%");
				stmt.setString(2, searchQuery + "%");
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					Student newStudent = new Student();
					newStudent.id = result.getInt("id");
					newStudent.firstName = result.getString("first_name");
					newStudent.lastName = result.getString("last_name");
					newStudent.middleName = result.getString("middle_name");
					newStudent.sex = result.getString("sex");
					newStudent.email = result.getString("email");
					students.add(newStudent);
				}
				closeConnection();
			} catch (SQLException e) {
				closeConnection();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return students;
	}
	
	public LinkedList<Status> getStatus(int studentId) {
		LinkedList<Status> status = new LinkedList<Status>();
		if(connect()) {
			String queryStatus = "SELECT events.id, status.name, events.detail, events.year from events, status "
					+ "WHERE events.status_id = status.id "
					+ "and events.student_id = ? "
					+ "order by year ";
			try {
				PreparedStatement stmt = conn.prepareStatement(queryStatus);
				stmt.setInt(1, studentId);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					Status newStatus = new Status();
					newStatus.id = result.getInt("id");
					newStatus.event = result.getString("name");
					newStatus.detail = result.getString("detail");
					newStatus.date = Integer.parseInt(result.getString("year"));
					status.add(newStatus);
				}

				closeConnection();
			} catch (SQLException e) {

				closeConnection();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return status;
	}
	public boolean addEvent(int studentId, int eventId, String detail, int year) {
		if(connect()) {
			String queryStatus = "INSERT into events(student_id, status_id, detail, year) VALUES (?, ? , ?, ?)";
			try {
				PreparedStatement stmt = conn.prepareStatement(queryStatus);
				stmt.setInt(1, studentId);
				stmt.setInt(2, eventId);
				stmt.setString(3, detail);
				stmt.setInt(4, year);
				int result = stmt.executeUpdate();
				closeConnection();
				boolean isSuccess = result > 0 ? true : false;
				return isSuccess;
			} catch (SQLException e) {

				closeConnection();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	public String updateStatus(Status status) {
		String resultString = "";
				String query = "update events set status_id = ?, detail = ?, year = ? where id = ?;";
				try {
					connect();
					PreparedStatement stmt = conn.prepareStatement(query);
					stmt.setInt(1, status.eventId);
					stmt.setString(2, status.detail);
					stmt.setInt(3, status.date);
					stmt.setInt(4, status.id);
					int result = stmt.executeUpdate();
					stmt.close();
					closeConnection();
					boolean isSuccess = result > 0 ? true : false;
					resultString += isSuccess ? "Success = "+status.id : "Fail "+status.id ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
			}
		
		return resultString;
	}
	public boolean deleteStatus(int selectedEventID) {
		boolean isSuccess = false;
		String query = "Delete from events where id = ?";
		try {
			connect();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, selectedEventID);
			int result = stmt.executeUpdate();
			stmt.close();
			closeConnection();
			isSuccess = result > 0 ? true : false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}
	public boolean deleteStudent(int selectedStudentID) {
		boolean isSuccess = false;
		String query = "Delete from students where id = ?";
		deleteAllStatus(selectedStudentID);
		try {
			connect();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, selectedStudentID);
			int result = stmt.executeUpdate();
			stmt.close();
			closeConnection();
			isSuccess = result > -1 ? true : false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	private void deleteAllStatus(int studentId) {
		String query = "Delete from events where student_id = ?";
		try {
			connect();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, studentId);
			stmt.executeUpdate();
			stmt.close();
			closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public LinkedList<Integer> getYears(){
		LinkedList<Integer> years = new LinkedList<Integer>();
		if(connect()) {
			String query = "select DISTINCT year from events order by year";
			try {
				connect();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					years.add(result.getInt("year"));
				}
				stmt.close();
				closeConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return years;
	}
	public LinkedList<String> getAllStatus() {
		LinkedList<String> status = new LinkedList<String>();
		if(connect()) {
			String query = "select DISTINCT name from status";
			try {
				connect();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					status.add(result.getString("name"));
				}
				stmt.close();
				closeConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return status;
	}

	public int getStatusCountInYear(Integer year, String tempStatus) {
		int queryResult = -1;
		if(connect()) {
			String query = "select count(events.id) from events,status where events.status_id = status.id  and events.year = ? and status.name = ?";
			try {
				connect();
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setString(2, tempStatus);
				stmt.setInt(1, year);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					queryResult = result.getInt("count(events.id)");
				}
				stmt.close();
				closeConnection();
				return queryResult;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return queryResult;
	}
	public int getAllStatusCount() {
		int queryResult = -1;
		if(connect()) {
			String query = "select count(id) from events";
			try {
				connect();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					queryResult = result.getInt("count(id)");
				}
				stmt.close();
				closeConnection();
				return queryResult;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return queryResult;
	}
	public int getGenderCount(String gender) {
		int queryResult = -1;
		if(connect()) {
			String query = "select count(sex) from students where students.sex = ?";
			try {
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setString(1, gender);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					queryResult = result.getInt("count(sex)");
				}
				stmt.close();
				closeConnection();
				return queryResult;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return queryResult;
	}
	public LinkedList<Student> getAllStudent() {
		LinkedList<Student> students = new LinkedList<Student>();
		if(connect()) {
			String queryStudent = "SELECT * from students";
			try {
				PreparedStatement stmt = conn.prepareStatement(queryStudent);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					Student newStudent = new Student();
					newStudent.id = result.getInt("id");
					newStudent.firstName = result.getString("first_name");
					newStudent.lastName = result.getString("last_name");
					newStudent.sex = result.getString("sex");
					students.add(newStudent);
				}
				closeConnection();
			} catch (SQLException e) {
				closeConnection();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return students;
	}
	public int getUpdateCount(int year) {
		int queryResult = -1;
		if(connect()) {
			String query = "select count(students.id) from students, events where students.id = events.student_id and events.year = ?";
			try {
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setInt(1, year);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					queryResult = result.getInt("count(students.id)");
				}
				stmt.close();
				closeConnection();
				return queryResult;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return queryResult;
	}
	public int getStudentCount() {
		int queryResult = -1;
		if(connect()) {
			String query = "select count(id) from students";
			try {
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					queryResult = result.getInt("count(id)");
				}
				stmt.close();
				closeConnection();
				return queryResult;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return queryResult;
	}
	public int getStatusId(String event) {
		int statusId = -1;;
		if(connect()) {
			String queryStatus = "SELECT id from status where name = ? ";
			try {
				PreparedStatement stmt = conn.prepareStatement(queryStatus);
				stmt.setString(1, event);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					statusId = result.getInt("id");
				}

				closeConnection();
			} catch (SQLException e) {

				closeConnection();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return statusId;
	}
	public int getStatusCount(int id) {
		int queryResult = 0;
		if(connect()) {
			String query = "select count(id) from events WHERE status_id = ?";
			try {
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setInt(1, id);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					queryResult = result.getInt("count(id)");
				}
				stmt.close();
				closeConnection();
				return queryResult;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return queryResult;
	}
	public void saveGoogleLink(String preUrl) {
		if(connect()) {
			String queryStatus = "INSERT into google(link, date) VALUES (?,?)";

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			 LocalDateTime now = LocalDateTime.now();  
			
			try {
				PreparedStatement stmt = conn.prepareStatement(queryStatus);
				stmt.setString(1, preUrl);
				stmt.setString(2, dtf.format(now));
				int result = stmt.executeUpdate();
				closeConnection();
			} catch (SQLException e) {
				closeConnection();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public LinkedList<Link> getGoogleLinks(){
		LinkedList<Link> links = new LinkedList<Link>();
		if(connect()) {
			String queryStudent = "select * from google order by date DESC";
			try {
				PreparedStatement stmt = conn.prepareStatement(queryStudent);
				ResultSet result = stmt.executeQuery();
				while(result.next()) {
					Link newLink = new Link();
					newLink.link = result.getString("link");
					newLink.date = result.getString("date");
					links.add(newLink);
				}
				closeConnection();
			} catch (SQLException e) {
				closeConnection();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return links;
	}
	public boolean updateStudent(String id, String firstName, String middleName, String lastName, String email, String sex) {
		String query = "update students set first_name = ?, middle_name = ?, last_name = ?, email = ?, sex = ? where id = ?;";
		try {
			connect();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, firstName);
			stmt.setString(2, middleName);
			stmt.setString(3, lastName);
			stmt.setString(4, email);
			stmt.setString(5, sex);

			stmt.setInt(6, Integer.parseInt(id));
			int result = stmt.executeUpdate();
			stmt.close();
			closeConnection();
			boolean isSuccess = result > 0 ? true : false;
			return isSuccess;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
	}
		return false;
	}
	public boolean addStatus(String newStatus) {
		if(connect()) {
			int res = -1;
			try {
				PreparedStatement stmt = conn.prepareStatement("INSERT INTO status(name) Values(?)");
				stmt.setString(1, newStatus);
				res = stmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			closeConnection();
			return res == -1 ? false: true;
		}else {
			return false;	
		}
	}
	public boolean updateStatus(int id, String newStatus) {
		if(connect()) {
			int res = -1;
			try {
				PreparedStatement stmt = conn.prepareStatement("update status set name = ? where id = ?");
				stmt.setString(1, newStatus);
				stmt.setInt(2, id);
				res = stmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			closeConnection();
			return res == -1 ? false: true;
		}else {
			return false;	
		}
	}
	
}
