package edu.newpaltz.nynjmohonk;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MyApplication extends Application {

	public static boolean firstDnld;
	public static String currentMap;
	public static int midPointX;
	public static int midPointY;
	public static int currentDB;
	public static String[] dbURLs = new String[3];
	public static String[] dbPaths = new String[3];
	public static Handler callBack = new Handler(); 
	private static AlertDialog alert;
	private static WebView webview;
	private static Context myContext;

	public void onCreate(){
		super.onCreate();
		firstDnld = true;
		dbURLs[0] = "https://wyvern.cs.newpaltz.edu/~n02486417/map_app/maps.sqlite";
		dbURLs[1] = "https://wyvern.cs.newpaltz.edu/~n02486417/map_app/locs.sqlite";
		dbURLs[2] = "https://wyvern.cs.newpaltz.edu/~n02486427/map_app/notification_info.sqlite";

		dbPaths[0] =  "/data/data/" + this.getApplicationContext().getPackageName() + "/databases/maps.sqlite";
		dbPaths[1] =  "/data/data/" + this.getApplicationContext().getPackageName() + "/databases/locs.sqlite";
		dbPaths[2] =  "/data/data/" + this.getApplicationContext().getPackageName() + "/databases/notification_info.sqlite";

		Runnable poiAlert = new Runnable(){
			public void run(){

				//	if(ProxyAlertReceiver.intent1.getBooleanExtra("KEY_PROXIMITY_ENTERING",false)==true){
				AlertDialog.Builder builder = new AlertDialog.Builder(ProxyAlertReceiver.myContext) ;
				try{
					Log.v("PAR","Making info choice box");
					builder.setMessage(""+NoteDBHelper.getNoteBuilderByName(NoteDBHelper.getNoteStore(),
							ProxyAlertReceiver.intent1.getStringExtra("loc_name")).getShort_info())
							.setNegativeButton("OK", new DialogInterface.OnClickListener() {

								//log.info("box made and displayed");

								public void onClick(DialogInterface dialog, int which) {
									Log.v("PAR","OK button clicked");
									alert.dismiss();

								}
							}).setPositiveButton("More info", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {
									Log.v("PAR","More info button clicked");
									//Intent moreInfo = new Intent(Intent.ACTION_VIEW,Uri.parse(""+NoteDBHelper.getNoteBuilder().getLong_info()));
									webview = new WebView(ProxyAlertReceiver.myContext);
									webview.getSettings().setJavaScriptEnabled(true);
									webview.setWebViewClient(new WebViewClient(){
										public void onReceivedError(WebView web, int errorCode,String description, String failedURL){
											Toast.makeText(ProxyAlertReceiver.myContext,description,Toast.LENGTH_SHORT).show();
										}
									});
									try{
										Log.v("PAR","Page launching");
										webview.loadUrl(""+NoteDBHelper.getNoteBuilderByName(NoteDBHelper.getNoteStore(),
												ProxyAlertReceiver.intent1.getStringExtra("loc_name")).getLong_info());
										Log.v("PAR","Page launched");
									}catch(Exception e){
										System.err.println(e);
									}
								}
							}).show();


				}catch(Exception e){
					System.err.println(e);
				}
			}
			//	}

		};


	}


}
