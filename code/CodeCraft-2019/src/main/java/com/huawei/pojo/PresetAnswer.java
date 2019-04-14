package com.huawei.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 		same as answer
 * 
 * @author lenovo02
 *
 */
public class PresetAnswer {
	private int carID;
	private int departureTime;
	private List<Integer> route = new ArrayList<>();
	
	
	
	/**
	 * 		
	 * @param carID
	 * @param departureTime
	 * @param route
	 */
	public PresetAnswer(int carID, int departureTime, List<Integer> route) {
		super();
		this.carID = carID;
		this.departureTime = departureTime;
		this.route = route;
	}
	public int getCarID() {
		return carID;
	}
	public void setCarID(int carID) {
		this.carID = carID;
	}
	public int getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(int departureTime) {
		this.departureTime = departureTime;
	}
	public List<Integer> getRoute() {
		return route;
	}
	public void setRoute(List<Integer> route) {
		this.route = route;
	}
	@Override
	public String toString() {
		return "PresetAnswer [carID=" + carID + ", departureTime=" + departureTime + ", route=" + route + "]";
	}

	
}
