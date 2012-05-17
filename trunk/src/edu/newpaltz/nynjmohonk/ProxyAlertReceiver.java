package edu.newpaltz.nynjmohonk;
//Jon Davin
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.util.Log;

public class ProxyAlertReceiver extends BroadcastReceiver {

	public static Context myContext;
	private AlertDialog alert;
	private WebView webview;
	public static Intent intent1;
	

	public ProxyAlertReceiver(Context c){
		myContext = c;
	}
	
	
	@Override
	public void onReceive(Context context, final Intent intent) {
		Log.v("PAR","Received proxy alert");
		
		intent1 = intent;
		myContext =context;
		
		MyApplication.callBack.postAtFrontOfQueue(MyApplication.poiAlert);

	}

}
