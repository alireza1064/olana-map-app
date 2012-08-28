package edu.newpaltz.nynjmohonk;

import android.app.AlertDialog;
import android.app.Application;
import android.os.Handler;
import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.Toast;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.util.Log;

public class MyApplication extends Application {

	public static boolean firstDnld;
	public static String currentMap;
	public static int midPointX;
	public static int midPointY;
	public static int currentDB;
	public static String[] dbURLs = new String[2];
	public static String[] dbPaths = new String[2];
	public static Handler callBack = new Handler(); 
	public static AlertDialog alert;
	public static WebView webview;
	//private static Context myContext;
	static Runnable poiAlert;

	public void onCreate(){
		super.onCreate();
		firstDnld = true;
		dbURLs[0] = "https://wyvern.cs.newpaltz.edu/~n02486417/map_app/maps.sqlite";
		dbURLs[1] = "https://wyvern.cs.newpaltz.edu/~n02486417/map_app/locs.sqlite";
		//dbURLs[2] = "https://wyvern.cs.newpaltz.edu/~n02486427/map_app/notification_info.sqlite";

		dbPaths[0] =  "/data/data/" + this.getApplicationContext().getPackageName() + "/databases/maps.sqlite";
		dbPaths[1] =  "/data/data/" + this.getApplicationContext().getPackageName() + "/databases/locs.sqlite";
		//dbPaths[2] =  "/data/data/" + this.getApplicationContext().getPackageName() + "/databases/notification_info.sqlite";

		


	}


}
