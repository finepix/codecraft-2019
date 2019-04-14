package com.huawei.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.huawei.pojo.Answer;
import com.huawei.pojo.Car;
import com.huawei.pojo.Cross;
import com.huawei.pojo.PresetAnswer;
import com.huawei.pojo.Road;

public class DataInputOutput {
	// tool for getting buffer reader
	private static ReaderUtil readerUtil = ReaderUtil.getInstance();
	private static ModelsManagement modelsManager = ModelsManagement.getManager();
	// file writer
	private static PrintWriter writer = null;
	
	/**
	 * 
	 * @return 
	 */
	public static ModelsManagement getModelsManager() {
		return modelsManager;
	}
	
	
	/**
	 * 		write answer to file
	 * 	
	 * @param answer 	
	 * @param path		path for answers to go
	 * @throws FileNotFoundException 
	 */
	public static void outputAnswer2File(Answer answer, String path) throws FileNotFoundException {
		if(writer == null)
			writer = new PrintWriter(path);
		// for simple use
		String outputString = answer.toString();
		
		writer.write(outputString);
		writer.flush();
	}
	
	/**
	 * 		close print writer(after functions: outputAnswer2File)
	 */
	public static void closePrintWriter() {
		if(writer != null) {
			writer.flush();
			writer.close();
		}
	}
	
	
	/**
	 * 		format data into arrays
	 * 
	 * @param line
	 * @return
	 */
	private static String[] formatInputData(String line) {
		line = line.substring(1, line.length() - 1).trim().replaceAll(" ", "");
		return line.split(",");
	}
	
	/**
	 *		get hash map for roads
	 * @param path
	 * @return 
	 * @throws IOException
	 */
	public static Map<Integer, Road> getRoadsMap(String path) throws IOException {
		// read road info from file and save into list
		List<Road> roads_list = getRoadsFromFile(path);
		
		// add (a, b) -> road, get a road for only a pair of crosses id
		for (Road road : roads_list) {
			String tmp_idx = road.getFrom() + "," + road.getTo();
			modelsManager.cross2road.put(tmp_idx, road);
			if(road.isDuplex()) {
				tmp_idx = road.getTo() + "," + road.getFrom();
				modelsManager.cross2road.put(tmp_idx, road);
			}
		}
		
		// convert road list to map for convenient search
		return List2MapUtil.convertRoadList2Map(roads_list);
	}
	
	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, Cross> getCrossesMap(String path) throws IOException {
		List<Cross> crosses_list = getCrossesFromFile(path);
		
		return List2MapUtil.convertCrossList2Map(crosses_list);
	}

	
	
	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, Car> getCarsMap(String path) throws IOException {
		List<Car> cars_list = getCarsFromFile(path);
		
		return List2MapUtil.convertCarList2Map(cars_list);
	}
	
	
	public static Map<Integer, PresetAnswer> getPresetAnswersMap(String path) throws IOException {
		List<PresetAnswer> answers = getPresetAnswerFromFile(path);
		
		return List2MapUtil.convertPresetAnswersList2Map(answers);
		
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<PresetAnswer> getPresetAnswerFromFile(String path) throws IOException {
		
		List<PresetAnswer> presetAnswers = new ArrayList<>();
		List<Integer> route = null;
		
		FileReader fileReader = readerUtil.getFileReader(path);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while((line = bufferedReader.readLine()) != null) {
			// jump when face #
			if(line.startsWith("#"))
				continue;
			String[] info = formatInputData(line);
			int carID = Integer.parseInt(info[0]);
			int departureTime = Integer.parseInt(info[1]);
			route = new ArrayList<>();
			for(int idx = 2; idx < info.length; idx ++)
				route.add(Integer.parseInt(info[idx]));
			PresetAnswer answer = new PresetAnswer(carID, departureTime, route);
			presetAnswers.add(answer);
		}
		
		bufferedReader.close();
		fileReader.close();
		return presetAnswers;
		
	}
	
	
	
	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<Road> getRoadsFromFile(String path) throws IOException{
		List<Road> roads = new ArrayList<>();
		FileReader fileReader = readerUtil.getFileReader(path);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while((line = bufferedReader.readLine()) != null) {
			if(line.startsWith("#"))
				continue;
			String[] info = formatInputData(line);
			Road road = new Road(Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]), Integer.parseInt(info[3]), 
					Integer.parseInt(info[4]), Integer.parseInt(info[5]), Integer.parseInt(info[6]));
			
			roads.add(road);
		}
		
		bufferedReader.close();
		fileReader.close();
		return roads;
	}
	
	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<Cross> getCrossesFromFile(String path) throws IOException{
		List<Cross> crosses = new ArrayList<>();
		FileReader fileReader = readerUtil.getFileReader(path);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while((line = bufferedReader.readLine()) != null) {
			if(line.startsWith("#"))
				continue;
			String[] info = formatInputData(line);
			Cross cross = new Cross(Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]), Integer.parseInt(info[3]), 
					Integer.parseInt(info[4]));
			
			crosses.add(cross);
		}
		
		bufferedReader.close();
		fileReader.close();
		return crosses;
	}
	
	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<Car> getCarsFromFile(String path) throws IOException{
		List<Car> cars = new ArrayList<>();
		FileReader fileReader = readerUtil.getFileReader(path);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while((line = bufferedReader.readLine()) != null) {
			if(line.startsWith("#"))
				continue;
			String[] info = formatInputData(line);
			Car car = new Car(Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]), Integer.parseInt(info[3]), 
					Integer.parseInt(info[4]), Integer.parseInt(info[5]), Integer.parseInt(info[6]));
			
			cars.add(car);
		}
		
		bufferedReader.close();
		fileReader.close();
		return cars;
	}
	
}
