package edu.newpaltz.nynjmohonk;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.content.DialogInterface;
import android.webkit.WebViewClient;
import android.widget.Toast;
//import android.net.Uri;
//import android.app.Activity;
//import android.location.LocationManager;
//import android.content.DialogInterface.OnCancelListener;


public class ProxyAlertReceiver extends BroadcastReceiver {
	private Context myContext;
	private AlertDialog alert;
	private WebView webview;


	@Override
	public void onReceive(Context context, Intent intent) {
		AlertDialog.Builder builder = new AlertDialog.Builder(myContext) ;
		builder.setMessage(""+NoteDBHelper.getNoteBuilder().getShort_info()).setNegativeButton("OK", new DialogInterface.OnClickListener() {


			public void onClick(DialogInterface dialog, int which) {
				alert.dismiss();

			}
		}).setPositiveButton("More info", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				
				//Intent moreInfo = new Intent(Intent.ACTION_VIEW,Uri.parse(""+NoteDBHelper.getNoteBuilder().getLong_info()));
				webview = new WebView(myContext);
				webview.getSettings().setJavaScriptEnabled(true);
				webview.setWebViewClient(new WebViewClient(){
					public void onReceivedError(WebView web, int errorCode,String description, String failedURL){
						Toast.makeText(myContext,description,Toast.LENGTH_SHORT).show();
					}
				});
				webview.loadUrl(""+NoteDBHelper.getNoteBuilder().getLong_info());
				
			}
		});
	}




}
