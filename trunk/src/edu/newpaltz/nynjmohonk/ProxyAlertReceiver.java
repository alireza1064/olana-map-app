package edu.newpaltz.nynjmohonk;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.content.DialogInterface;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.util.Log;
//import java.util.logging.*;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.app.Activity;
//import android.content.DialogInterface.OnCancelListener;



public class ProxyAlertReceiver extends BroadcastReceiver {
	//Logger log;
	private Context myContext;
	private AlertDialog alert;
	private WebView webview;
	//private final boolean KEY_PROXIMITY_ENTERING = false;

	public ProxyAlertReceiver(Context c){
		myContext = c;
	}
	
	
	@Override
	public void onReceive(Context context, final Intent intent) {
		Log.v("PAR","Received proxy alert");

		if(intent.getBooleanExtra("KEY_PROXIMITY_ENTERING",false)==true){
			AlertDialog.Builder builder = new AlertDialog.Builder(myContext) ;
			try{
				Log.v("PAR","Making info choice box");
				builder.setMessage(""+NoteDBHelper.getNoteBuilderByName(NoteDBHelper.getNoteStore(),intent.getStringExtra(
				"loc_name")).getShort_info()).setNegativeButton("OK", new DialogInterface.OnClickListener() {

					//log.info("box made and displayed");

					public void onClick(DialogInterface dialog, int which) {
						Log.v("PAR","OK button clicked");
						alert.dismiss();

					}
				}).setPositiveButton("More info", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Log.v("PAR","More info button clicked");
						//Intent moreInfo = new Intent(Intent.ACTION_VIEW,Uri.parse(""+NoteDBHelper.getNoteBuilder().getLong_info()));
						webview = new WebView(myContext);
						webview.getSettings().setJavaScriptEnabled(true);
						webview.setWebViewClient(new WebViewClient(){
							public void onReceivedError(WebView web, int errorCode,String description, String failedURL){
								Toast.makeText(myContext,description,Toast.LENGTH_SHORT).show();
							}
						});
						try{
							Log.v("PAR","Page launching");
							webview.loadUrl(""+NoteDBHelper.getNoteBuilderByName(NoteDBHelper.getNoteStore(),intent.getStringExtra(
							"loc_name")).getLong_info());
							Log.v("PAR","Page launched");
						}catch(Exception e){
							System.err.println(e);
						}
					}
				});


			}catch(Exception e){
				System.err.println(e);
			}
		}

	}

}
