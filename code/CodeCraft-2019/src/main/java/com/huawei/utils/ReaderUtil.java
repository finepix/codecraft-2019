package com.huawei.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReaderUtil {
	private static ReaderUtil readerUtil = null;
	private static FileReader fileReader = null;
	
	/**
	 * 		singleton
	 */
	private ReaderUtil() {
		
	}
	
	/**
	 * 		get instance
	 * @return
	 */
	public static ReaderUtil getInstance() {
		if(readerUtil == null)
			return new ReaderUtil();
		return readerUtil;
	}

	/**
	 * 		
	 * @param filePath
	 * @return
	 */
	public FileReader getFileReader(String filePath) {
		try {
			fileReader = new FileReader(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return fileReader;
	}
	
	/**
	 * @param reader
	 */
	public static void closeFileReader(FileReader reader) {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
