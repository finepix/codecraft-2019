package com.huawei;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.huawei.pojo.Answer;
import com.huawei.pojo.Car;
import com.huawei.pojo.Cross;
import com.huawei.pojo.PresetAnswer;
import com.huawei.pojo.Road;
import com.huawei.utils.CopyUtil;
import com.huawei.utils.DataInputOutput;
import com.huawei.utils.DepartureTimeSetTool;
import com.huawei.utils.GraphAlgorithm;
import com.huawei.utils.SearchTool;

public class Main {
	
	/**
	 * TODO: 按比赛时的底图大小设置
	 * */
	private static final int MAX_ROAD_SIZE_FOR_GRAPH_1 = 241;
	
	/**
	 *  非预置车辆的发车时间，应当与预置车辆有很大的间隔
	 */
	private static int DEPARTURE_TIME_FOR_NO_PRESET_CARS = 800;
	/**
	 * TODO: 预置车辆的最晚发车时间，（同上）
	 */
	private static int LAST_DEPARTURE_TIME_FOR_PRESET_CARS = 756;
	/**
	 * 每一批次大概发多少辆车
	 */
	private static int APPROXIMATE_CARS_EVERY_PIECE = 1000;
	
	/**
	 * 基准时间间隔
	 */
	private static int TIME_INTERVAL_FOR_EVERY_PIECE_FOR_BASE_SPEED = 100;
	/**
	 *  预置分段内 发车数量（一般和总体一样）（同上）
	 * 
	 */
	private static int NUMS_OF_CARS_GOING_INTO_PRESET_TIME = 1000;
	/**
	 * 基准速度，用于计算时间间隔，将速度快的分段时间间隔调小
	 */
	private static final int BASE_SPEED = 8;
	
	/*------------------------------------------封路参数--------------------------------------------------------------*/
	// TODO: 封路（最后调试）
	// 是否封路
	private static boolean isDebugForBlockingNodes = false;
	// 封路列表
	private static List<Integer> blockingNodes = new ArrayList<>();

	/*------------------------------------------速度时间间隔参数----------------------------------------------------------*/
	private static boolean isDebugForSpeedTimeInterval = false;
	/*-----------------------------------------------function for use-----------------------------------------------*/
	
	/**
	 * 	test for reconstructed graph
	 * 	
	 * @param graph
	 */
	public static void testEdgesByPrint2Console(Graph<Cross, DefaultWeightedEdge> graph) {

        Set<DefaultWeightedEdge> edgeSet = graph.edgeSet();
        for (DefaultWeightedEdge defaultWeightedEdge : edgeSet) {
        	System.out.println(graph.getEdgeWeight(defaultWeightedEdge));
		}
	}
	
	/**
	 *  图对象进行深度克隆
	 * @param graph 当前需要克隆的图
	 * @param targetGraph  	需要转换到的图
	 */
	public static Graph<Cross, DefaultWeightedEdge> deepCopy(Graph<Cross, DefaultWeightedEdge> originGraph) {
		
		Graph<Cross, DefaultWeightedEdge> targetGraph = CopyUtil.deepCloneObject(originGraph);
		return targetGraph;
	}
	
	
	/*----------------------------------------------main function----------------------------------------------------*/
	
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args ) throws IOException
    {
        if (args.length != 5) {
            return;
        }

        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String presetAnswerPath = args[3];
        String answerPath = args[4];

        // TODO: time counter
        long startTime = System.currentTimeMillis();
        
        /**
         * 		read files
         * 
         * */
        Map<Integer, Car> cars_map = DataInputOutput.getCarsMap(carPath);
        Map<Integer, Cross> crosses_map = DataInputOutput.getCrossesMap(crossPath);
        Map<Integer, Road> roads_map = DataInputOutput.getRoadsMap(roadPath);
        Map<Integer, PresetAnswer> preset_answers_map = DataInputOutput.getPresetAnswersMap(presetAnswerPath);
        
        // ModelsManagement manager = ModelsManagement.getManager();
        
        /**
         * TODO: 图一和图二的参数设置
         * 
         * */
        if(roads_map.size() == MAX_ROAD_SIZE_FOR_GRAPH_1) {
        	// 非预置车辆发车时间
        	DEPARTURE_TIME_FOR_NO_PRESET_CARS = 400;	// 400
        	// 每一批次发车数量
        	APPROXIMATE_CARS_EVERY_PIECE = 1000;
        	/**
        	 * TODO：预置时间内发车数量
        	 * */
        	NUMS_OF_CARS_GOING_INTO_PRESET_TIME = 1000;
        	// 每次发车间隔
        	TIME_INTERVAL_FOR_EVERY_PIECE_FOR_BASE_SPEED = 30;
        	// TODO： 预置车辆最晚发车时间
        	LAST_DEPARTURE_TIME_FOR_PRESET_CARS = 323;	 // 323
        	
        	/**
        	 * TODO:封路路口添加
        	 * 
        	 * */
        	if (isDebugForBlockingNodes) {
            	//blockingNodes.add(1160);
			}
        	
        }
        else {
        	// 参数设置同上
        	DEPARTURE_TIME_FOR_NO_PRESET_CARS = 800;
        	// 每一批次发车数量
        	APPROXIMATE_CARS_EVERY_PIECE = 1000;
        	// 预置时间内 发车数量
        	NUMS_OF_CARS_GOING_INTO_PRESET_TIME = 1000;
        	// 基准时间间隔
        	TIME_INTERVAL_FOR_EVERY_PIECE_FOR_BASE_SPEED = 50; 
        	// TODO： 预置车辆最晚发车时间
        	LAST_DEPARTURE_TIME_FOR_PRESET_CARS = 756;
        	
        	/**
        	 * TODO: 封路路口添加
        	 * 
        	 * */
        	if (isDebugForBlockingNodes) {
            	blockingNodes.add(982);
			}
        }
        
        
        /**
         * TODO: car with priority first
         * 
         * */
        // 总答案列表
        List<Answer> answers = new ArrayList<>();
        
        /**
         * step a: split into 2 parts based on preset
         * */ 
        
        List<Car> preset_car_list = new ArrayList<>();
        List<Car> no_preset_car_list = new ArrayList<>();
        for (Car car : cars_map.values()) {
			if( car.getPreset() == 1)
				preset_car_list.add(car);
			else
				no_preset_car_list.add(car);
		}
        
        /**
         * step b: sort preset list by : priority -> speed; preset cars sorted by: departure time
         * */ 
        
        // details in Car object function: compare()
        Collections.sort(no_preset_car_list);
        
        // 优先车辆的速度排序： 慢 - 快
//        int indexForNoPriority = 0;
//        for(int idx = 0; idx < no_preset_car_list.size() - 1; idx++)
//        	if (no_preset_car_list.get(idx).getPriority() == 0) {
//				indexForNoPriority = idx;
//				break;
//			}
//        // 子段排序
//        List<Car> subListForPriority = no_preset_car_list.subList(0, indexForNoPriority);
//        Collections.sort(subListForPriority, (carA, carB) -> carA.getSpeed() - carB.getSpeed());
//        
        // 测试排序结果
//        for(int idx = 0; idx < no_preset_car_list.size() - 1; idx++){
//        	System.out.println(no_preset_car_list.get(idx).getPriority() + ", " + no_preset_car_list.get(idx).getSpeed());
//            if (idx == subListForPriority.size()) {
//				System.out.println("stop");
//			}
//        }
        	
        // sorted by departure time
        Collections.sort(preset_car_list, 
			  (carA, carB) -> preset_answers_map.get(carA.getCar_id()).getDepartureTime() - preset_answers_map.get(carB.getCar_id()).getDepartureTime());
        
        /**
         * step c: prepare for every piece
         * */ 
        int departure_time_now = 1;
        int departure_time_next = 0;
        
        // 非预置车辆的索引
        int index_for_none_preset_now = 0;
        // 在预置车辆中的索引位置
        int index_for_preset_cars_now = 0;
        // 下一个索引位置
        int index_for_preset_cars_next = 0;
        // 预置车辆在当前的发车档期内的数量
        int cars_count_in_preset = 0;
        // 需要从非预置车辆中提取的车的数量 	: APPROXIMATE_CARS_EVERY_PIECE - cars_count_in_preset
        int cars_in_none_preset_count = 0;
        
        /**
         * step d: arrange car in preset time
         * */ 
        
        // 安排第一个，以及重构图
        // construct graph (origin graph)
        Graph<Cross, DefaultWeightedEdge> graph = GraphAlgorithm.buildGraph(crosses_map, roads_map);
        
        int ID = preset_car_list.get(0).getCar_id();
		graph = GraphAlgorithm.rebuiltGraphByGivenRoute(graph, preset_answers_map.get(ID).getRoute(), roads_map);
		
        while(departure_time_now < LAST_DEPARTURE_TIME_FOR_PRESET_CARS) {        	
        	departure_time_next = departure_time_now + TIME_INTERVAL_FOR_EVERY_PIECE_FOR_BASE_SPEED;
        	
        	// count cars in preset during the time interval
        	index_for_preset_cars_next = SearchTool.binarySearch(preset_car_list, departure_time_next + 1, preset_answers_map);
        	
        	cars_count_in_preset = index_for_preset_cars_next - index_for_preset_cars_now;
        	
        	/**
        	 * 安排非预置的车辆
        	 * */ 
        	// TODO：置零
        	cars_in_none_preset_count = 0;
        	if(cars_count_in_preset > NUMS_OF_CARS_GOING_INTO_PRESET_TIME) {
        		/**
        		 * TODO： 将预置车辆写入结果集中
        		 * */
        		for(int idx = index_for_preset_cars_now + 1; idx <= index_for_preset_cars_next; idx++) {
        			int carID = preset_car_list.get(idx).getCar_id();
        			graph = GraphAlgorithm.rebuiltGraphByGivenRoute(graph, preset_answers_map.get(carID).getRoute(), roads_map);
        		}
        		// 更新当前索引
        		index_for_preset_cars_now = index_for_preset_cars_next;
        		index_for_none_preset_now += cars_in_none_preset_count;
        		departure_time_now = departure_time_next;
        	}else{
        		/**
        		 * 	安排非预置车辆
        		 * */
        		cars_in_none_preset_count = NUMS_OF_CARS_GOING_INTO_PRESET_TIME - cars_count_in_preset;
        		// 添加预置到结果结合，并更新路径
        		for(int idx = index_for_preset_cars_now + 1; idx <= index_for_preset_cars_next; idx++) {
        			int carID = preset_car_list.get(idx).getCar_id();
        			graph = GraphAlgorithm.rebuiltGraphByGivenRoute(graph, preset_answers_map.get(carID).getRoute(), roads_map);
        			
        		}
        		
        		// 安排非预置车辆
        		// TODO： 微调边界区域
        		for(int idx = index_for_none_preset_now; idx < index_for_none_preset_now + cars_in_none_preset_count; idx++) {
        			Car car = no_preset_car_list.get(idx);

        			List<Integer> route = GraphAlgorithm.getShortestPathForGivenCar(car, graph, crosses_map);
        			
        			/**
        			 * TODO: 封路，当调整不下去时，对于当前道路进行封路处理
        			 * 
        			 * */
        			if(isDebugForBlockingNodes) {
        				//route = GraphAlgorithm.getShortestPathForGivenCarByBlockingNodes(car, graph, blockingNodes, roads_map, crosses_map);
        			}
        			
        			// 设置答案
        			Answer answer = new Answer();
    				answer.setRoute(route);
    				answer.setDepartureTime(departure_time_now);
    				answer.setCarID(car.getCar_id());
    				// 添加到结果集中
    	        	answers.add(answer);
    	        	
    				// reconstruct graph
    	        	graph = GraphAlgorithm.rebuiltGraphByGivenCarID(graph, car.getCar_id());
        		}
        	}

        	// 更新当前索引
    		index_for_preset_cars_now = index_for_preset_cars_next;
    		index_for_none_preset_now += cars_in_none_preset_count;
    		departure_time_now = departure_time_next;
        }
        
        /**
         * step e: arrange rest cars out of preset time
         * */
        
        departure_time_now = DEPARTURE_TIME_FOR_NO_PRESET_CARS;
        // 下一次索引
        int index_for_none_preset_next = 0;
        
        // TODO：重构图（每一次或者每几次重构一次）
        graph = GraphAlgorithm.buildGraph(crosses_map, roads_map);
        
        // TODO：边界调试
        while(index_for_none_preset_next + APPROXIMATE_CARS_EVERY_PIECE < no_preset_car_list.size() - 1) {
        	
            /*graph = GraphAlgorithm.buildGraph(crosses_map, roads_map);*/
        	// 当前段
        	List<Answer> tmpPieceAnswersList = new ArrayList<>();
        	
        	// 计算下一次的索引
        	index_for_none_preset_next = index_for_none_preset_now + APPROXIMATE_CARS_EVERY_PIECE;
        	// 为当前段分配路径并重新修改路径权重
        	for(int idx = index_for_none_preset_now; idx < index_for_none_preset_next; idx++) {
        		Car car = no_preset_car_list.get(idx);
    			List<Integer> route = GraphAlgorithm.getShortestPathForGivenCar(car, graph, crosses_map);
    			
    			/**
    			 * TODO: 封路，当调整不下去时，对于当前道路进行封路处理
    			 * 
    			 * */
    			if(isDebugForBlockingNodes) {
    				route = GraphAlgorithm.getShortestPathForGivenCarByBlockingNodes(car, graph, blockingNodes, roads_map, crosses_map);
    			}
    			
    			// 设置答案
    			Answer answer = new Answer();
				answer.setRoute(route);
				answer.setDepartureTime(departure_time_now);
				answer.setCarID(car.getCar_id());
				// 添加到当前结果集中
				tmpPieceAnswersList.add(answer);
	        	
				// reconstruct graph
	        	graph = GraphAlgorithm.rebuiltGraphByGivenCarID(graph, car.getCar_id());
        	}
        	
        	/**
        	 * 设置路径分段
        	 * */
        	DepartureTimeSetTool.setDepartureTimeByTimeConsume(tmpPieceAnswersList, departure_time_now);
        	
        	// 添加到总的结果集中
        	answers.addAll(tmpPieceAnswersList);
        	// 更新当前索引
        	index_for_none_preset_now = index_for_none_preset_next;
        	
        	/**
        	 * TODO: 调整时间间隔系数，当当前队列第一个不是优先级车辆，再设置小间隔
        	 * */
        	if (isDebugForSpeedTimeInterval && cars_map.get(tmpPieceAnswersList.get(0).getCarID()).getPriority() != 1) {
        		// 统计当前piece的答案的平均速度，然后为下一个分段设置合适的时间间隔
            	double avgSpeed = DepartureTimeSetTool.calculateAvgOfAnswersPiece(tmpPieceAnswersList, cars_map);
            	departure_time_now += DepartureTimeSetTool.getTimeIntervalForCurrentPiece(avgSpeed, TIME_INTERVAL_FOR_EVERY_PIECE_FOR_BASE_SPEED, BASE_SPEED);
            }
        	else
        		departure_time_now += TIME_INTERVAL_FOR_EVERY_PIECE_FOR_BASE_SPEED;
        }
        
        // 为剩下的部分分配路径
        for (int idx = index_for_none_preset_now; idx < no_preset_car_list.size(); idx++) {
        	Car car = no_preset_car_list.get(idx);
			List<Integer> route = GraphAlgorithm.getShortestPathForGivenCar(car, graph, crosses_map);
			
			// 设置答案
			Answer answer = new Answer();
			answer.setRoute(route);
			answer.setDepartureTime(departure_time_now);
			answer.setCarID(car.getCar_id());
			// 添加到结果集中
        	answers.add(answer);
        	
			// reconstruct graph
        	graph = GraphAlgorithm.rebuiltGraphByGivenCarID(graph, car.getCar_id());
		}
        
        /*testEdgesByPrint2Console(graph);*/
        
        /**
         * writing files
         * 
         * */
        
        // 不包含预置车辆
        for (Answer answer : answers) {
        	if(cars_map.get(answer.getCarID()).getPreset() == 1) {
        		/**
        		 * 	TODO： 预置车辆，这里pass
        		 * */
        	}
        	
        	// 与计划出发时间判断
        	if(answer.getDepartureTime() < cars_map.get(answer.getCarID()).getPlanTime()) 
				answer.setDepartureTime(cars_map.get(answer.getCarID()).getPlanTime());
        	
        	// 输出
        	DataInputOutput.outputAnswer2File(answer, answerPath);
		}
        
        // close output stream
        DataInputOutput.closePrintWriter();
        
        // TODO: delete time counter
        long endTime = System.currentTimeMillis();
        System.out.println("time consuming:" + (endTime - startTime));
    }

}