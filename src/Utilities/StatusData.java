package Utilities;

import java.awt.Color;

public class StatusData {
	String status;
	Color color;
	int count;
	public StatusData() {}
	
	public StatusData(String status, Color color, int count) {
		this.status = status;
		this.color = color;
		this.count = count;
	}

	@Override
	public String toString() {
		return "StatusData [status=" + status + ", color=" + color + ", count=" + count + "]";
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
