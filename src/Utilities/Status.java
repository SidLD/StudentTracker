package Utilities;

public class Status {
	public int id;
	public int studentId;
	public String event;
	public String detail;
	public int date;
	public int eventId;
	public Status() {}
	public Status(int id, int studentId, String event, String detail, int date) {
		this.id = id;
		this.studentId = studentId;
		this.event = event;
		this.detail = detail;
		this.date = date;
	}
	@Override
	public String toString() {
		return "Status [id=" + id + ", studentId=" + studentId + ", event=" + event + ", detail=" + detail + ", date="
				+ date + "]";
	}
}
