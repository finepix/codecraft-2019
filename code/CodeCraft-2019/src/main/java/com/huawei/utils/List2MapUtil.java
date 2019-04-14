package com.huawei.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.pojo.Car;
import com.huawei.pojo.Cross;
import com.huawei.pojo.PresetAnswer;
import com.huawei.pojo.Road;

/**
 * @author lenovo02
 *
 */
public class List2MapUtil {
	
	/**
	 * @param cars_list
	 * @return
	 */
	public static Map<Integer, Car> convertCarList2Map(List<Car> cars_list) {
		Map<Integer, Car> cars_map = new HashMap<>();
		for (Car car : cars_list) {
			cars_map.put(car.getCar_id(), car);
		}
		
		return cars_map;
	}
	
	/**
	 * @param road_list
	 * @return
	 */
	public static Map<Integer, Road> convertRoadList2Map(List<Road> roads_list) {
		Map<Integer, Road> road_map = new HashMap<>();
		for (Road road : roads_list) {
			road_map.put(road.getRoadID(), road);
		}
		
		return road_map;
	}
	
	/**
	 * @param crosses_list
	 * @return
	 */
	public static Map<Integer, Cross> convertCrossList2Map(List<Cross> crosses_list) {
		Map<Integer, Cross> cross_map = new HashMap<>();
		for (Cross cross : crosses_list) {
			cross_map.put(cross.getCrossID(), cross);
		}
		
		return cross_map;
	}

	public static Map<Integer, PresetAnswer> convertPresetAnswersList2Map(List<PresetAnswer> answers) {
		Map<Integer, PresetAnswer> presetAnswersMap = new HashMap<>();
		for (PresetAnswer presetAnswer : answers) {
			presetAnswersMap.put(presetAnswer.getCarID(), presetAnswer);
		}
		
		return presetAnswersMap;
	}

}
