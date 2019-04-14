package com.huawei.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.huawei.pojo.Car;
import com.huawei.pojo.Cross;
import com.huawei.pojo.Road;

public class GraphAlgorithm {
	
	// singleton instance for managing (a, b) -> road etc.
	private static ModelsManagement modelsManager = ModelsManagement.getManager();
	
	// penalty for every arranged road
	private final static double PENALTY_ON_EDGE = 0.01;
	private static boolean isDebugForEdgePenalty = false;
	
	/**
	 * TODO:封路(调整)
	 * */
//	private final static double MAX_LENGTH_FOR_BLOCKING = Double.MAX_VALUE;
	private final static double MAX_LENGTH_FOR_BLOCKING = 1;
	
	/**
	 * @return
	 */
	public static ModelsManagement getModelsManager() {
		return modelsManager;
	}

	/**
	 * 
	 * @param crosses_map
	 * @param roads_map
	 * @return
	 */
	public static Graph<Cross, DefaultWeightedEdge> buildGraph(Map<Integer, Cross> crosses_map, Map<Integer, Road> roads_map) {
		// TODO Auto-generated method stub
		Graph<Cross, DefaultWeightedEdge> graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		// travel vertex
		for (Integer crossID : crosses_map.keySet()) {
			Cross cross = crosses_map.get(crossID);
			graph.addVertex(cross);
		}
		// travel edges
		for (Integer roadID : roads_map.keySet()) {
			
//			if(roadID == 5326)
//				System.out.println("test");
			
			Road road = roads_map.get(roadID);
			Cross cross_1 = crosses_map.get(road.getFrom());
			Cross cross_2 = crosses_map.get(road.getTo());
			DefaultWeightedEdge edge = graph.addEdge(cross_1, cross_2);
			// weight = length / speed(not car speed)
			graph.setEdgeWeight(edge, road.getLength() / road.getSpeed());
			
			String tmpInputKey = cross_1.getCrossID() + "," + cross_2.getCrossID();
			modelsManager.pairCrossID2edge.put(tmpInputKey, edge);
			
			if(road.isDuplex()) {
				DefaultWeightedEdge edge2 = graph.addEdge(cross_2, cross_1);
				graph.setEdgeWeight(edge2, road.getLength() / road.getSpeed());
				
				// add reverse road to manager
				tmpInputKey = cross_2.getCrossID() + "," + cross_1.getCrossID();
				modelsManager.pairCrossID2edge.put(tmpInputKey, edge2);
			}
		}
		return graph;
	}
	
	
	/**
	 * 
	 * @param car
	 * @param graph
	 * @param crosses_map 
	 * @return 
	 */
	public static List<Integer> getShortestPathForGivenCar(Car car, Graph<Cross, DefaultWeightedEdge> graph, Map<Integer, Cross> crosses_map) {
		List<Integer> route = new ArrayList<>();
		
		DijkstraShortestPath<Cross, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);
		Cross source_cross = crosses_map.get(car.getSourceID());
		Cross target_cross = crosses_map.get(car.getTargetID());
		
		GraphPath<Cross, DefaultWeightedEdge> path = shortestPath.getPath(source_cross, target_cross);
		
		// 没有找到path，重新返回
		if(path == null)
			return route;
		
		// put object into manager
		modelsManager.carID2GraphPath.put(car.getCar_id(), path);
		
		// convert vertex list to edges
		List<Cross> vertexList = path.getVertexList();
		for(int idx = 0; idx < vertexList.size() - 1; idx++) {
			int tmpCrossID_1 = vertexList.get(idx).getCrossID();
			int tmpCrossID_2 = vertexList.get(idx + 1).getCrossID();

			String tmpKey = tmpCrossID_1 + "," + tmpCrossID_2;
			Road road = modelsManager.cross2road.get(tmpKey);
			
			route.add(road.getRoadID());
		}
		
		return route;
	}

//	/**
//	 * 	修改过后的寻找最短路径算法（为deep copy graph使用）
//	 * @param car
//	 * @param graph
//	 * @param crosses_map 
//	 * @return 
//	 */
//	public static List<Integer> getShortestPathForGivenCarOnDeepCopyGraph(Car car, Graph<Cross, DefaultWeightedEdge> graph) {
//		List<Integer> route = new ArrayList<>();
//		DijkstraShortestPath<Cross, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);
//		
//		Cross sourceCross = null;
//		Cross targetCross = null;
//		
//		Set<Cross> vertexSet = graph.vertexSet();
//		for (Cross cross : vertexSet) {
//			if (cross.getCrossID() == car.getSourceID()) {
//				sourceCross = cross;
//			}
//			if (cross.getCrossID() == car.getTargetID()) {
//				targetCross = cross;
//			}
//		}
//		
//		GraphPath<Cross, DefaultWeightedEdge> path = shortestPath.getPath(sourceCross, targetCross);
//		
//		// put object into manager
//		modelsManager.carID2GraphPath.put(car.getCar_id(), path);
//		
//		// convert vertex list to edges
//		List<Cross> vertexList = path.getVertexList();
//		for(int idx = 0; idx < vertexList.size() - 1; idx++) {
//			int tmpCrossID_1 = vertexList.get(idx).getCrossID();
//			int tmpCrossID_2 = vertexList.get(idx + 1).getCrossID();
//
//			String tmpKey = tmpCrossID_1 + "," + tmpCrossID_2;
//			Road road = modelsManager.cross2road.get(tmpKey);
//			
//			route.add(road.getRoadID());
//		}
//		
//		return route;
//	}
	
	
	/**
	 * 		reconstruct graph for preset answers
	 * 
	 * 
	 * @param graph
	 * @param route
	 * @param roads_map 
	 * @return
	 */
	public static Graph<Cross, DefaultWeightedEdge> rebuiltGraphByGivenRoute(Graph<Cross, DefaultWeightedEdge> graph, List<Integer> route,
			Map<Integer, Road> roads_map) {
		// TODO
		if (route.size() < 2) {
			return graph;
		}
		
		List<Integer> vertexList = new  ArrayList<>();
		
		Road road0 = roads_map.get(route.get(0));
		Road road1 = roads_map.get(route.get(1));
		
		int road0FromID = road0.getFrom();
		int road0ToID = road0.getTo();
		int road1FromID = road1.getFrom();
		int road1ToID = road1.getTo();
		
		if(road0ToID == road1FromID || road0ToID == road1ToID) {
			vertexList.add(road0FromID);
			vertexList.add(road0ToID);
		}
		else {
			vertexList.add(road0ToID);
			vertexList.add(road0FromID);
		}
		
		for(int idx = 1; idx < route.size(); idx ++) {
			Road road = roads_map.get(route.get(idx));
			
			int roadFromID = road.getFrom();
			int roadToID = road.getTo();
			
			int lastID = vertexList.get(vertexList.size() - 1);
			if(roadFromID == lastID) {
				vertexList.add(roadToID);
			}else {
				vertexList.add(roadFromID);
			}
		}
		
		// travel edges
		for(int idx = 0; idx < vertexList.size() - 1; idx++) {
			int tmpCrossID_1 = vertexList.get(idx);
			int tmpCrossID_2 = vertexList.get(idx + 1);
			
			String tmpKey = tmpCrossID_1 + "," + tmpCrossID_2;
			DefaultWeightedEdge edge = modelsManager.pairCrossID2edge.get(tmpKey);
			/**
			 * TODO: 调整 权重
			 * */
			// double weight = graph.getEdgeWeight(edge) + PENALTY_ON_EDGE;
			Road road = modelsManager.cross2road.get(tmpKey);
			double weight = graph.getEdgeWeight(edge) + getChannelPenalty(PENALTY_ON_EDGE, road);
			
			graph.setEdgeWeight(edge, weight);
		}
		
		return graph;
		
	}
	
	/**
	 * @param penaltyOnEdge
	 * @param road
	 * @return
	 */
	private static double getChannelPenalty(double penaltyOnEdge, Road road) {
		
		if (isDebugForEdgePenalty) {
			return penaltyOnEdge / (road.getChannel() + 1);
		}
		
		return penaltyOnEdge;
	}

	/**
	 * 
	 * @param graph
	 * @param graphPath
	 * @return
	 */
	public static Graph<Cross, DefaultWeightedEdge> rebuiltGraphByGivenCarID(Graph<Cross, DefaultWeightedEdge> graph, Integer carID) {
		
		GraphPath<Cross, DefaultWeightedEdge> graphPath = modelsManager.carID2GraphPath.get(carID);
		
		List<Cross> vertexList = graphPath.getVertexList();
		for(int idx = 0; idx < vertexList.size() - 1; idx++) {
			int tmpCrossID_1 = vertexList.get(idx).getCrossID();
			int tmpCrossID_2 = vertexList.get(idx + 1).getCrossID();
			
			String tmpKey = tmpCrossID_1 + "," + tmpCrossID_2;
			DefaultWeightedEdge edge = modelsManager.pairCrossID2edge.get(tmpKey);
			
			/**
			 * TODO: 惩戒调整
			 * 
			 * */
//			double weight = graph.getEdgeWeight(edge) + PENALTY_ON_EDGE;
			Road road = modelsManager.cross2road.get(tmpKey);
			double weight = graph.getEdgeWeight(edge) + getChannelPenalty(PENALTY_ON_EDGE, road);
			
			graph.setEdgeWeight(edge, weight);
		}
		
		return graph;
		
	}

//	/**
//	 * 给定封路列表封路
//	 * @param deepCopy
//	 * @param blockingNodes
//	 * @param crosses_map
//	 * @param roads_map 
//	 */
//	public static void blockCrossByGivenBlockingList(Graph<Cross, DefaultWeightedEdge> graph,
//			List<Integer> blockingNodes, Map<Integer, Cross> crosses_map, Map<Integer, Road> roads_map) {
//		
//		List<Double> WeightsList = new ArrayList<>();
//		
//		for (Integer crossID : blockingNodes) {
//			Cross cross = crosses_map.get(crossID);
//			if(cross == null)		// 为空指针返回
//				return;
//			List<Double> weightsPreserve = blockOneCross(graph, cross, roads_map, crosses_map);
//			WeightsList.addAll(weightsPreserve);
//		}
//	}

	/**
	 * 
	 * 封一个路口
	 * @param graph 
	 * @param cross
	 * @param roads_map
	 * @param crosses_map 
	 * @return 
	 */
	private static Map<String, Double> blockOneCross(Graph<Cross, DefaultWeightedEdge> graph, Cross cross, Map<Integer, Road> roads_map, Map<Integer, Cross> crosses_map) {
		
		Map<String, Double> preserveWeight = new HashMap<>();
		
		int roadID_1 = cross.getRoadID_1();
		int roadID_2 = cross.getRoadID_2();
		int roadID_3 = cross.getRoadID_3();
		int roadID_4 = cross.getRoadID_4();
		if (roadID_1 != -1) {
			Map<String, Double> weight_1 = blockOneRoad(graph, roadID_1, roads_map, crosses_map);
			preserveWeight.putAll(weight_1);
		}
		if (roadID_2 != -1) {
			Map<String, Double> weight_2 = blockOneRoad(graph, roadID_2, roads_map, crosses_map);
			preserveWeight.putAll(weight_2);
		}
		if (roadID_3 != -1) {
			Map<String, Double> weight_3 = blockOneRoad(graph, roadID_3, roads_map, crosses_map);
			preserveWeight.putAll(weight_3);
		}
		if (roadID_4 != -1) {
			Map<String, Double> weight_4 = blockOneRoad(graph, roadID_4, roads_map, crosses_map);
			preserveWeight.putAll(weight_4);
		}
		
		return preserveWeight;
	}

	/**
	 * 封一个道路
	 * @param graph 
	 * @param roadID_1
	 * @param roads_map
	 * @param crosses_map 
	 * @return 
	 */
	private static Map<String, Double> blockOneRoad(Graph<Cross, DefaultWeightedEdge> graph, int roadID, Map<Integer, Road> roads_map, Map<Integer, Cross> crosses_map) {
		if(roadID == -1) // 若为-1，直接返回
			return null;
		
		Map<String, Double> crossesID2EdgeWeight = new HashMap<>();
		
		double originWeight = 0.0;
		
		Road road = roads_map.get(roadID);
		Cross sourceCross = crosses_map.get(road.getFrom());
		Cross targetCross = crosses_map.get(road.getTo());
		
		// 目标或者源节点没找到，直接返回，还封你妹呢
		if(sourceCross == null || targetCross == null) 
			return null;
		
		DefaultWeightedEdge edge = graph.getEdge(sourceCross, targetCross);
		// 保存原始权重
		originWeight = graph.getEdgeWeight(edge);
		String tmpKey = sourceCross.getCrossID() + "," + targetCross.getCrossID();
		crossesID2EdgeWeight.put(tmpKey, originWeight);
		
		// 设置权重为无穷
		graph.setEdgeWeight(edge, MAX_LENGTH_FOR_BLOCKING + originWeight);
		
		// test
		/*double weight_2 = graph.getEdgeWeight(edge);
		System.out.println(weight_1 + "change weight:" + weight_2);*/
		
		// 若道路为双向的，那么继续封
		if(road.isDuplex()) {
			DefaultWeightedEdge edgeReverse = graph.getEdge(targetCross, sourceCross);
			// 保存原始权重
			originWeight = graph.getEdgeWeight(edgeReverse);
			tmpKey = targetCross.getCrossID() + "," +  sourceCross.getCrossID();
			crossesID2EdgeWeight.put(tmpKey, originWeight);
			
			// 设置权重为无穷
//			graph.setEdgeWeight(edgeReverse, MAX_LENGTH_FOR_BLOCKING);
			graph.setEdgeWeight(edgeReverse, MAX_LENGTH_FOR_BLOCKING + originWeight);
		}
		
		return crossesID2EdgeWeight;
	}

	/**
	 *  在阻断的图上面进行寻路：
	 *  step 1：封锁道路， 保存道路的权重（有向图）
	 *  step 2：在封锁的道路上寻路
	 *  step 3：按保存的道路权重将道路还原
	 *  step 4：若在封锁的道路上面没有找到路径，则在原图再跑一次
	 * @param car
	 * @param graph
	 * @param blockingNodes
	 * @param roads_map
	 * @param crosses_map
	 * @return
	 */
	public static List<Integer> getShortestPathForGivenCarByBlockingNodes(Car car,Graph<Cross, DefaultWeightedEdge> graph, 
															List<Integer> blockingNodes, Map<Integer, Road> roads_map, Map<Integer, Cross> crosses_map) {
		
		Map<String, Double> weightsMap = new HashMap<>();
		// 封路并保存
		for (Integer crossID : blockingNodes) {
			
			Cross cross = crosses_map.get(crossID);
			if(cross == null)		// 为空指针返回
				continue;
			
			Map<String, Double> weightsPreserve = blockOneCross(graph, cross, roads_map, crosses_map);
			weightsMap.putAll(weightsPreserve);

		}
		// 寻路
		List<Integer> route = GraphAlgorithm.getShortestPathForGivenCar(car, graph, crosses_map);
		// 还原 tmpKey: cross(a, b)
		for (String tmpKey : weightsMap.keySet()) {
			double weight = weightsMap.get(tmpKey);
			
			DefaultWeightedEdge edge = modelsManager.pairCrossID2edge.get(tmpKey);

//			double weight_1 = graph.getEdgeWeight(edge);
			
			graph.setEdgeWeight(edge, weight);
			
//			double weight_2 = graph.getEdgeWeight(edge);
//			System.out.println(weight_1 + "----" + weight_2);
		}
		
		// 判断route是否为空，空路径表示没有找到路径，使用原图进行寻路操作
		if (route.size() == 0) {
			route = GraphAlgorithm.getShortestPathForGivenCar(car, graph, crosses_map);
		}
		
		return route;
	}
}





