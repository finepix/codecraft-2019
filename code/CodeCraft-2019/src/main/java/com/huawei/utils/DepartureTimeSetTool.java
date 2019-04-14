package com.huawei.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.huawei.pojo.Answer;
import com.huawei.pojo.Car;

public class DepartureTimeSetTool {

	// singleton instance for managing (a, b) -> road etc.
	private static ModelsManagement modelsManager = ModelsManagement.getManager();
	
	// 
	private static final int K_FOLDS_FOR_PIECE = 4;
	private static final int TIME_INTERVAL = 2;

	/**
	 * 为每一段设置发车顺序
	 * @param tmpPieceAnswersList
	 * @param departure_time_now	当前发车时间
	 */
	public static void setDepartureTimeByTimeConsume(List<Answer> tmpPieceAnswersList, int departure_time_now) {
		// 排序：路径长度
//		Collections.sort(tmpPieceAnswersList, 
//				(ans1, ans2) -> modelsManager.carID2GraphPath.get(ans1.getCarID()).getLength() - 
//				modelsManager.carID2GraphPath.get(ans2.getCarID()).getLength());
		
		// 排序；耗时
		Collections.sort(tmpPieceAnswersList, 
				(ans1, ans2) -> new Double(modelsManager.carID2GraphPath.get(ans1.getCarID()).getWeight()).compareTo(modelsManager.carID2GraphPath.get(ans2.getCarID()).getWeight()));
		
		// test
//		for (Answer answer : tmpPieceAnswersList) {
//			System.out.println(modelsManager.carID2GraphPath.get(answer.getCarID()).getWeight());
//		}
		
		int total = tmpPieceAnswersList.size();
		int pieceLength = total / K_FOLDS_FOR_PIECE;
		int departureTime = -1;
		for(int idx = 0; idx < tmpPieceAnswersList.size(); idx++) {
			departureTime = departure_time_now + idx / pieceLength * TIME_INTERVAL;
			tmpPieceAnswersList.get(idx).setDepartureTime(departureTime);
		}
	}

	/**
	 * 计算当前分段的平均时间
	 * @param tmpPieceAnswersList
	 * @param cars_map 
	 * @return
	 */
	public static double calculateAvgOfAnswersPiece(List<Answer> tmpPieceAnswersList, Map<Integer, Car> cars_map) {
		
		if (tmpPieceAnswersList.size() == 0) {	 // 当当前piece为0时直接跳出
			return 1;
		}
		
		double sumSpeed = 0.0;
		for (Answer answer : tmpPieceAnswersList) {
			sumSpeed += cars_map.get(answer.getCarID()).getSpeed();
		}
		double avgSpeed = sumSpeed / tmpPieceAnswersList.size();
		
		return avgSpeed;
	}

	/**
	 * 
	 * 为当前分段设置合适的时间
	 * @param avgSpeed
	 * @param TIME_INTERVAL_FOR_EVERY_PIECE_FOR_BASE_SPEED
	 * @param BASE_SPEED
	 * @return
	 */
	public static int getTimeIntervalForCurrentPiece(double avgSpeed, int TIME_INTERVAL_FOR_EVERY_PIECE_FOR_BASE_SPEED,
			int BASE_SPEED) {
		
		// 若速度小于基准速度，那么不调整时间间隔
		if (avgSpeed <= BASE_SPEED) {
			return TIME_INTERVAL_FOR_EVERY_PIECE_FOR_BASE_SPEED;
		}
		
		// timeInterval = baseInterval / ( (speedNow - 1) / baseSpeed)
		int timeInterval = (int) (TIME_INTERVAL_FOR_EVERY_PIECE_FOR_BASE_SPEED / ((avgSpeed - 1) / BASE_SPEED));
		return timeInterval;
	}
}







