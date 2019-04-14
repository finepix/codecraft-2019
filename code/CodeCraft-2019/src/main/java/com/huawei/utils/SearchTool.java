package com.huawei.utils;

import java.util.List;
import java.util.Map;

import com.huawei.pojo.Car;
import com.huawei.pojo.PresetAnswer;

public class SearchTool {
	
	/**
	 * @param cars
	 * @param key_departure_time
	 * @param preset_answers_map
	 * @return
	 */
	public static int binarySearch(List<Car> cars, int key_departure_time, Map<Integer, PresetAnswer> preset_answers_map) {
		/*List<Integer> test_departure = new ArrayList<>();
		for (Car car : cars) {
			test_departure.add(preset_answers_map.get(car.getCar_id()).getDepartureTime());
		}*/
		
		int left = 0;
		int right = cars.size() - 1;
		
		int mid = 0;
		while(left < right) {
			mid = (left + right) / 2;
			int carID = cars.get(mid).getCar_id();
			
			if(preset_answers_map.get(carID).getDepartureTime() <= key_departure_time)
				left = mid + 1;
			else
				right = mid - 1;
		}
		
		return right;
	}

}
