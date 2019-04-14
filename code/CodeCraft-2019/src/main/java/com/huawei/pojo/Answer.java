package com.huawei.pojo;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.huawei.utils.ModelsManagement;

public class Answer implements Comparable<Answer>{
	

	// singleton instance for managing (a, b) -> road etc.
	private static ModelsManagement modelsManager = ModelsManagement.getManager();
	
	private int carID;
	private int departureTime;
	private List<Integer> route = new ArrayList<>();
	
	public Answer() {
	}
	
	/**
	 * 	construct answer object by given presetAnswer
	 * 
	 * @param presetAnswer
	 */
	public Answer(PresetAnswer presetAnswer) {
		this.carID = presetAnswer.getCarID();
		this.departureTime = presetAnswer.getDepartureTime();
		this.route = presetAnswer.getRoute();
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
	
	/* for simple output
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("(" + carID + ", ");
		buffer.append(departureTime);
		for (Integer roadID : route) {
			buffer.append(", " + roadID);
		}
		buffer.append(")\n");
		return buffer.toString(); 		// .replaceAll(" ", "")
	}

	/* 方便排序
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Answer ans2) {
		int ans2ID = ans2.getCarID();
		GraphPath<Cross, DefaultWeightedEdge> pathForAns2 = modelsManager.carID2GraphPath.get(ans2ID);
		GraphPath<Cross, DefaultWeightedEdge> path = modelsManager.carID2GraphPath.get(this.carID);
		if(pathForAns2.getWeight() > path.getWeight())
			return 1;
		else
			return 0;
	}
	
}
