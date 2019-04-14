package com.huawei.pojo;

import java.io.Serializable;

public class Cross implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1105766012745466569L;
	private int crossID;
	private int roadID_1;
	private int roadID_2;
	private int roadID_3;
	private int roadID_4;
	
	
	/**
	 * 		clockwise id 1234
	 * 
	 * @param crossID
	 * @param roadID_1
	 * @param roadID_2
	 * @param roadID_3
	 * @param roadID_4
	 */
	public Cross(int crossID, int roadID_1, int roadID_2, int roadID_3, int roadID_4) {
		super();
		this.crossID = crossID;
		this.roadID_1 = roadID_1;
		this.roadID_2 = roadID_2;
		this.roadID_3 = roadID_3;
		this.roadID_4 = roadID_4;
	}
	public int getCrossID() {
		return crossID;
	}
	public void setCrossID(int crossID) {
		this.crossID = crossID;
	}
	public int getRoadID_1() {
		return roadID_1;
	}
	public void setRoadID_1(int roadID_1) {
		this.roadID_1 = roadID_1;
	}
	public int getRoadID_2() {
		return roadID_2;
	}
	public void setRoadID_2(int roadID_2) {
		this.roadID_2 = roadID_2;
	}
	public int getRoadID_3() {
		return roadID_3;
	}
	public void setRoadID_3(int roadID_3) {
		this.roadID_3 = roadID_3;
	}
	public int getRoadID_4() {
		return roadID_4;
	}
	public void setRoadID_4(int roadID_4) {
		this.roadID_4 = roadID_4;
	}
	
}
