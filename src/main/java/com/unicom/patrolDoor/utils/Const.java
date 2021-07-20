package com.unicom.patrolDoor.utils;

/* 
 * Created by Billy Lu 2020.12.18
 * To read patrol.properties.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
//import java.util.List;
import java.util.Properties;

public class Const {
	public static String ROOT_PATH = null; // 磁盘根目录路�?
	public static String Result_PATH = null;// 项目根路�?
	public static String Report_Path = null;
	public static String Zip_Path = null;
	//public static String HEAD_URL = null;// 项目根路�?
	public static String Conclusion_file = null;
	
	Const() {
		loadRootPath();
	}

	public static void loadRootPath() {
		System.out.println("loadRootPath");
		Properties prop = new Properties();
		InputStream in = Const.class.getResourceAsStream("/patrol.properties");
		try {
			prop.load(in);
			ROOT_PATH = prop.getProperty("root_path").trim();
			File RootDir = new File(ROOT_PATH);
			if (!RootDir.exists()) {
				//RootDir.mkdirs();
				System.out.println("Cannot find root path.");
			}
			Result_PATH = ROOT_PATH + prop.getProperty("result_folder").trim();
			File streamDir = new File(Result_PATH);
			if (!streamDir.exists()) {
				streamDir.mkdirs();
				System.out.println("Cannot find result path." + Result_PATH);
			}
			Report_Path = ROOT_PATH + prop.getProperty("report_folder").trim();
			File reportDir = new File(Report_Path);
			if (!reportDir.exists()) {
				reportDir.mkdirs();
				System.out.println("Cannot find report path." + Report_Path);
			}
			Zip_Path = ROOT_PATH + prop.getProperty("zip_folder").trim();
			File zipDir = new File(Zip_Path);
			if (!zipDir.exists()) {
				zipDir.mkdirs();
				System.out.println("Cannot find zip path." + Zip_Path);
			}
			/*Conclusion_file = ROOT_PATH + prop.getProperty("conclusion_file").trim();
			File finalFile = new File(Conclusion_file);
			if (!finalFile.exists()) {
				streamDir.mkdirs();
				System.out.println("Cannot find final file." + Conclusion_file);
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
