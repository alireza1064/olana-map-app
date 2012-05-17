package edu.newpaltz.nynjmohonk;
//Jon Davin
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.util.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


/**
 * The activity which displays the main menu for our app. It includes a few buttons to
 * view a map, exit, or (eventually) change settings.
 */
public class MainMenu extends Activity {

	private Button openMap, closeApp, downloadMaps;
	private AlertDialog mapChoice;
	private ProgressDialog d = null;
	private Map currentMap = null;
	File dbFile;
	private boolean dialogShowing;
	//private String mapDatabaseURL = "https://wyvern.cs.newpaltz.edu/~n02486417/map_app/maps.sqlite";
	//private String locDatabaseURL = "https://wyvern.cs.newpaltz.edu/~n02486417/map_app/locs.sqlite";
	//private String noteDatabaseURL = "https://wyvern.cs.newpaltz.edu/~n02486427/map_app/notification_info.sqlite";

	//private int currentDB;

	/**
	 * Create an instance of the main menu, including copying the database (if needed) and reading the maps from the
	 * map table. Create the onClickListener for the Select Map button.
	 */
 	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		dialogShowing = false; 
		dbFile = new File(MyApplication.dbPaths[0]);
		if(!dbFile.exists()){
			Thread t2 = new Thread(downloadDatabase);
			t2.start();
		}
		
		// Open map button shows the select dialog for a map
		openMap = (Button) findViewById(R.id.launchMap);
		openMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					final ArrayList<Map> results = Map.getDownloadedMaps(MainMenu.this);
					if(results.size() > 0) {
						final CharSequence [] names = new CharSequence[results.size()];
						for(int i = 0; i < results.size(); i++) {
							names[i] = results.get(i).getName();
						}

						AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
						builder.setTitle("Select A Map");
						builder.setItems(names, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int item) {
								//Log.d("OnCreate in MainMenu","Going into the MapViewActivity");		
								// Show dialog symbolizing loading of image (or downloading of image)
								mapChoice.dismiss();
								currentMap = results.get(item);
								Intent i = new Intent(MainMenu.this, MapViewActivity.class);
								i.putExtra("myMap", currentMap);
								MyApplication.midPointX = currentMap.getMapMidX();
								MyApplication.midPointY = currentMap.getMapMidY();
								d = ProgressDialog.show(MainMenu.this, "", "Loading map...");
								MainMenu.this.startActivity(i); // start activity on main thread	
							}
						});
						mapChoice = builder.create();			
						mapChoice.show();
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
						builder.setMessage("No maps downloaded.\nClick \"Download Maps\" first.")
						.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						});
						AlertDialog a = builder.create();
						a.show();		
					}
				} catch(Exception e) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
					builder.setMessage("Your map database is corrupted, so a new one will be downloaded.")
					.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ProgressDialog.show(MainMenu.this, "", "Downloading new database...");
							Thread t = new Thread(downloadDatabase);
							t.start();
						}
					});
					AlertDialog a = builder.create();
					a.show();							
				}  
			}
		});

		// Close app button exits the application
		closeApp = (Button) findViewById(R.id.exit);
		closeApp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		// Clear data button clears out any downloaded maps and re-copies the database
		downloadMaps = (Button)findViewById(R.id.download_maps);
		downloadMaps.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MainMenu.this.startActivity(new Intent(MainMenu.this, DownloadMapsActivity.class)); // start activity on main thread	
			}
		});
		
		/*
		// Check if the database exists
		File f = new File(MyApplication.dbPaths[0]);
		if(!f.exists()) {
			// Either first run or something else is wrong - download the latest database
			d = ProgressDialog.show(this, "", "Map database does not yet exist.\nDownloading now...");
			      	//moved the call for download databases from here
		} else {
			// Check for a new database (maybe do in background eventually?)
			//d = ProgressDialog.show(this, "", "Checking for new database...");
			Thread t3 = new Thread(newDatabaseCheck);
			t3.start();
		}
		*/
		
		//to here
		//Thread t2 = new Thread(downloadDatabase);
		//t2.start(); 
	}

	/**
	 * Create the options menu at the main screen
	 * @param menu The menu we want to create
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	/*@Override
    public void onUserLeaveHint() {
    	finish();
    }*/

	@Override
	public void onPause() {
		super.onPause();
		if(d != null) {
			d.dismiss();
		}
	}

	public void onResume() {
		super.onResume();
	}

	public void onDestroy() {
		super.onDestroy();
		// Kill the app - completely!
		System.runFinalizersOnExit(true);
		System.exit(0);
		//android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * Setup the event responders for the options selected. 
	 * @param item The item that has been selected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.new_database:
			d = ProgressDialog.show(this, "", "Downloading new database");
			dialogShowing = true;
			Thread t2 = new Thread(downloadDatabase);
			t2.start();
			break;
		case R.id.clear_cache:
			d = ProgressDialog.show(this, "", "Clearing data");
			Thread t1 = new Thread(clearCache);
			t1.start();
			break;
		case R.id.support:
			startActivity(new Intent(this, Support.class));
			break;
		case R.id.info:
			startActivity(new Intent(this, Information.class));
			break;
		}
		return true;
	}	

	// Thread to delete downloaded maps and clear out and re-copy the database
	private Runnable clearCache = new Runnable() {
		public void run() {
			// Delete map files
			File f = MainMenu.this.getFilesDir();
			File[] allFiles = f.listFiles(); 
			for(int i = 0; i < allFiles.length; i++) {
				allFiles[i].delete();
			}


			MainMenu.this.deleteDatabase("maps.sqlite");

			// Copy database again
			Thread t2 = new Thread(downloadDatabase);
			t2.start();    

			// Close progress dialog on main UI thread
			MainMenu.this.runOnUiThread(new Runnable() {
				public void run() {
					d.dismiss();						
				}
			});
		}
	};

	// Start a background thread that downloads the image (if needed) and loads the map and shows
	// our MapViewActivity
	private Runnable downloadDatabase = new Runnable() {
		public void run() {		

			for(int i =0;i<MyApplication.dbPaths.length;i++){			
				
				if(i==0){
					Log.v("main menu","downloading map DB");
					MapDatabaseHelper.downloadDB(MainMenu.this);
					while(MapDatabaseHelper.getLoadState() == 0); // wait while the image is in an unknown state
					while(MapDatabaseHelper.getLoadState() == 3); // wait while image actually downloads...
					switch(MapDatabaseHelper.getLoadState()) {
					case 1:
						// Database loaded successfully
						Log.v("MainMenu","map DB download successful");
						MainMenu.this.runOnUiThread(new Runnable() {
							public void run() {
								//d.dismiss();	
							}
						});
						break;
					case 2:
						// There was some error in downloading the image
						connectionError();
						break;
					}
				}else if(i ==1){
					Log.v("main menu","downloading loc DB");
					LocDBHelper.downloadDB(MainMenu.this);
					while(LocDBHelper.getLoadState() == 0); // wait while the image is in an unknown state
					while(LocDBHelper.getLoadState() == 3); // wait while image actually downloads...
					switch(LocDBHelper.getLoadState()) {
					case 1:
						// Database loaded successfully
						Log.v("MainMenu","loc DB download successful");
						MainMenu.this.runOnUiThread(new Runnable() {
							public void run() {
								//d.dismiss();	
							}
						});
						break;
					case 2:
						// There was some error in downloading the image
						connectionError();
						break;
					}
				}else{
					Log.v("main menu","downloading note DB");
					NoteDBHelper.downloadDB(MainMenu.this);
					while(NoteDBHelper.getLoadState() == 0); // wait while the image is in an unknown state
					while(NoteDBHelper.getLoadState() == 3); // wait while image actually downloads...
					switch(NoteDBHelper.getLoadState()) {
					case 1:
						// Database loaded successfully
						Log.v("MainMenu","note DB download successful");
						MainMenu.this.runOnUiThread(new Runnable() {
							public void run() {
								if(dialogShowing==true){
									d.dismiss();	
								}									
							}
						});
						break;
					case 2:
						// There was some error in downloading the image
						connectionError();
						break;
					}
				}
				
			}
			
			
			
		}

		public void connectionError() {
			Log.v("MainMenu","download dbs error");
			MainMenu.this.runOnUiThread(new Runnable() {
				public void run() {
					d.dismiss();
					// Show an alert dialog that shows an error in downloading the map
					AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
					builder.setMessage("There was an error while downloading the database.\nCheck your network settings and try again.")
					.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
					AlertDialog a = builder.create();
					a.show();							
				}
			});
		}
		
		
		
	};    

	// Check the last modified date of the current database against the last modified date of the remote database
	// to see if there is a new database available
	//not currently used due to issues, program will run by re-downloading DB each time it is run
	@SuppressWarnings("unused")
	private Runnable newDatabaseCheck = new Runnable() {
		public void run() {
			URL url = null;
			URLConnection conn = null;
			Log.v("MainMenu","URL and URL connection created");
			File f ;
			for(int i = 0 ;i<MyApplication.dbURLs.length;i++){
				//currentDB =  i;
				f = new File(MyApplication.dbPaths[i]);
				try {
					url = new URL(MyApplication.dbURLs[i]);
					conn = url.openConnection();
					//conn.addRequestProperty("content-length", ""+f.length());
					//conn.addRequestProperty("last-modified", ""+f.lastModified());

					conn.setIfModifiedSince(f.lastModified()+1);					
					conn.setRequestProperty("content-length", ""+f.length());



					Log.v("MainMenu","connection opened and last modified set");
				} catch (MalformedURLException e) {
					// Should not happen - URL is hardcoded.
					Log.v("MainMenu","caught Malformed URL Exception");
				} catch(IOException e) {
					// Error connecting to our URL, so just tell the user there was error checking for new DB
					Log.v("MainMenu","caught IO Exception");
					connectionError();
					return;
				}

				if(conn.getContentLength() == -1) {
					Log.v("MainMenu","connection length is -1");
					connectionError();
					return;
				}
				//content-length

				//current problem area!
				long remoteModified = 0;
				String lastModified = conn.getHeaderField("last-modified");

				try {
					Date date = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH).parse(lastModified);
					Log.v("MainMenu","date parse success");
					remoteModified = date.getTime();

				} catch (ParseException e) {
					// Hopefully never happens
					Log.v("MainMenu","Parse exception caught");
					MainMenu.this.runOnUiThread(new Runnable() {
						public void run() {
							d.dismiss(); 												
						}
					});	
					return;
					//current problem area!	


				}
				// File f = new File(MyApplication.dbPaths[i]);
				if(f.exists() && f.lastModified() < remoteModified) {
					// New database exists!


					MainMenu.this.runOnUiThread(new Runnable() {
						public void run() {
							d.dismiss();
							// Either first run or something else is wrong - download the latest database
							d = ProgressDialog.show(MainMenu.this, "", "New database available.\nDownloading now...");

							Thread t2 = new Thread(downloadDatabase);
							t2.start();  												
						}
					});		
					return;
				} else {
					// Just do nothing. No new db available
					MainMenu.this.runOnUiThread(new Runnable() {
						public void run() { if(d != null) d.dismiss(); }
					});
				}
			}


		}
		public void connectionError() {
			Log.v("MainMenu","new DB check error");
			MainMenu.this.runOnUiThread(new Runnable() {

				public void run() {
					d.dismiss();
					// Show an alert dialog that shows an error in downloading the map
					AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
					builder.setMessage("There was an error while checking for a new database.\nApp will proceed as normal if database already exists.")
					.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {}
					});
					AlertDialog a = builder.create();
					a.show();							
				}
			});			  		
		}
	};
}