package com.huawei.utils;

import java.util.HashMap;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.huawei.pojo.Cross;
import com.huawei.pojo.Road;

public class ModelsManagement {
	
	private static ModelsManagement management = new ModelsManagement();
	
	private ModelsManagement() {
		
	}
	
	public static ModelsManagement getManager() {
		if(management != null)
				return management;
		return new ModelsManagement();
	}
	
	
	/**
	 * cross (a, b) -> road 
	 */
	public HashMap<String, Road> cross2road = new HashMap<>();
	/**
	 * cross(a, b) -> j graph edge
	 */
	public HashMap<String, DefaultWeightedEdge> pairCrossID2edge = new HashMap<>();
	
	/**
	 * carID -> graph path
	 */
	public HashMap<Integer, GraphPath<Cross, DefaultWeightedEdge>> carID2GraphPath = new HashMap<>();

}
