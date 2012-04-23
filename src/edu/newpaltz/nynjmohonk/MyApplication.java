package edu.newpaltz.nynjmohonk;

import android.app.Application;

public class MyApplication extends Application {
	
	public static int midPointX;
	public static int midPointY;
	public static int currentDB;
	public static String[] dbURLs = new String[3];
	public static String[] dbPaths = new String[3];
	
	public void onCreate(){
		super.onCreate();
	
	dbURLs[0] = "https://wyvern.cs.newpaltz.edu/~n02486417/map_app/maps.sqlite";
	dbURLs[1] = "https://wyvern.cs.newpaltz.edu/~n02486417/map_app/locs.sqlite";
	dbURLs[2] = "https://wyvern.cs.newpaltz.edu/~n02486427/map_app/notification_info.sqlite";
	
	dbPaths[0] =  "/data/data/" + this.getApplicationContext().getPackageName() + "/databases/maps.sqlite";
	dbPaths[1] =  "/data/data/" + this.getApplicationContext().getPackageName() + "/databases/locs.sqlite";
	dbPaths[2] =  "/data/data/" + this.getApplicationContext().getPackageName() + "/databases/notification_info.sqlite";
	
}}
