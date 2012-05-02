package edu.newpaltz.nynjmohonk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.*;
//import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.LocationManager;
//import android.location.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * A simple SQLite helper class that copies, connects to and reads from our database of maps.
 *
 */
public class LocDBHelper extends SQLiteOpenHelper {
	static //private Location loc;
	Logger log;
	private static String DB_PATH = ""; 
	private static final String DB_NAME = "newpaltzcampus_locs.sqlite";
	private final Context myContext;
	private SQLiteDatabase myDatabase;	
	private static LocDBHelper myDBConnection;
	private static String proxStore[];
	private static PointOfInterest[] POIStore;
	private PointOfInterest p;
	
	/**
	 * Generate a MapDatabaseHelper instance within the context of this application. Also sets the path
	 * to the SQLite database file based on the application context
	 * @param context The current application context
	 */
	public LocDBHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		DB_PATH = "/data/data/" + context.getApplicationContext().getPackageName() + "/databases/";
	}

	/**
	 * Gets an instance of MapDatabaseHelper which will be connected to our SQLite database
	 * @param context The current application context
	 * @return A connection to the database
	 */
	public static synchronized LocDBHelper getDBInstance(Context context) {
		if (myDBConnection == null) {
			myDBConnection = new LocDBHelper(context);
		}
		return myDBConnection;
	}
	
	/**
	 * Copies the database to the phone, if needed. If there are any errors in copying the database, such as writing
	 * to the phone, an IOException is thrown.
	 * @throws IOException
	 */
	public void createDatabase() throws IOException {
		boolean dbExist = databaseExists();
		if (dbExist) {
			// database already exists - do nothing
		} else {
			this.getReadableDatabase();
			try {
				copyDatabase();
			} catch (IOException e) {
				throw new IOException("Error copying database");
			}
		}
	}
	
	/**
	 * Setup database folders
	 */
	public void setupDatabase() {
		boolean dbExist = databaseExists();
		if(!dbExist) this.getReadableDatabase();
	}
	
	/**
	 * Checks if the database has already been copied to the phone's local filesystem
	 * @return True if the database is already copied and false otherwise
	 */
	public boolean databaseExists() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does not yet exist
		}
		
		if(checkDB != null) {
			checkDB.close();
		}
		
		return checkDB != null;
	}
	
	/**
	 * Does the actual database copy, from reading the file in our assets to writing each by to our
	 * new file that is stored on the phone. If there are any errors reading/writing, an IOException
	 * is thrown
	 * @throws IOException
	 */
	private void copyDatabase() throws IOException {
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		String outFilename = DB_PATH + DB_NAME;
		OutputStream myOutput = new FileOutputStream(outFilename);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}
	
	/**
	 * Reads the database from the phone
	 * @throws IOException
	 */
	public void openDatabase() throws IOException {
		String myPath = DB_PATH + DB_NAME;
		myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}
	
	/**
	 * Closes the connection to the SQLite database
	 */
	@Override
	public synchronized void close() {
		if (myDatabase != null) {
			myDatabase.close();
		}
		super.close();
	}

	/**
	 * Method required because we are extending SQLiteOpenHelper
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {}

	/**
	 * Method required because we are extending SQLiteOpenHelper
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
	
	/**
	 * Gets, creates and sets the data of the points of interest and 
	 * then generates a proximity alert based on that data
	 * @param query The SQL SELECT query on the Map table
	 * @param selectionArgs A list of selection arguments, if applicable
	 * @param a location manager to generate the proximity alerts
	 */
	
	public ArrayList<PointOfInterest> generatePoints(String query, String[] selectionArgs, LocationManager loc) {

		//query = "SELECT * FROM NPC_locs";
		ArrayList<PointOfInterest> results = new ArrayList<PointOfInterest>();
		proxStore = new String[128];
		int pendIntFlag = 1073741824;  // indicated single usage only!
		int t = 0;
		Cursor c = myDatabase.rawQuery(query, selectionArgs);
		if(c.moveToFirst()) {
			do {
				 p = new PointOfInterest(myContext);
				for(int i = 0; i < c.getColumnCount(); i++) {
					if(c.getString(i) != null) {
						if(i ==2 || i==3) {
							// For longitude values we want to use getDouble() to preserve accuracy
							p.setVal(i, c.getDouble(i));
						} else if(i == 0 || i == 4) {
							p.setVal(i, c.getInt(i));
						} else if(i==1){
							p.setVal(i, c.getString(i));
						}else{
							//do nothing
						}
					}
				}
				POIStore[t] = p;
				//proxStore[t] = 
				addProxyAlert(loc,p.getLat(),p.getLong(),p.getRadius(),myContext, pendIntFlag,p.getLocName());
				t++;
				log.info("POI and Proxy point"+t+" genreated and stored");
				results.add(p);
			} while(c.moveToNext());
		}
		c.close();
		c.deactivate();
		myDatabase.close();
		return results;
	}
	//THIS MIGHT NEED TO BE CHANGED!!! specifically the intent sections
	public static void addProxyAlert(LocationManager loc, double lat, double longe, 
			int radius,Context c, int flag, String loc_name){
		
		loc.addProximityAlert(lat, longe, radius, -1, PendingIntent.getActivity(
				c, 0, new Intent().putExtra(loc_name, loc_name), flag));
		
		log.info("Intent set");
		
		//return ""+timeActivated+";"+loc_name+";"+lat+";"+longe+";"+radius;
	}
	
	public static PointOfInterest[] getPOIStore(){
		return POIStore;
	}
	
	public static String[] getProxStore(){
		return proxStore;
	}
	
	private static int dbLoadState = 0;

	
	/**
	 * Return the current load state of the database
	 */
	public static int getLoadState() {
		return dbLoadState;
	}
	
	/**
	 * Download the database from a hardcoded URL location so that we can have the most up-to-date map files
	 */
	public static void downloadDB(Context c) {	
		DB_PATH = "/data/data/" + c.getApplicationContext().getPackageName() + "/databases/";
		dbLoadState = 3;
		String downloadURL = c.getString(R.string.locs_database_url);
		HttpGet httpRequest = null;
		String dbFilename = DB_PATH + DB_NAME;

		// Setup the URL request
		URL myURL = null;
		try {
			myURL = new URL(downloadURL);
		} catch (MalformedURLException me) {
			dbLoadState = 2; // error loading/downloading image
			return;
		}
		
		// Download the image from the URL given
		try {
			httpRequest = new HttpGet(myURL.toURI());
		} catch (URISyntaxException exp) {
			dbLoadState = 2;
			return;
		}
				
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse)httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
			InputStream instream = bufHttpEntity.getContent();
			int nRead;

			MapDatabaseHelper mdb = MapDatabaseHelper.getDBInstance(c);
	        mdb.setupDatabase();
	
			FileOutputStream f = new FileOutputStream(dbFilename + ".tmp");
			
			// Write out to a file, make sure to encrypt the first bit of the file
			byte[] data = new byte[16384];
			while((nRead = instream.read(data, 0, data.length)) != -1) {
				f.write(data, 0, nRead);
			}
			
			f.flush();
			f.close();
			instream.close();
        
		} catch (Exception exp) {
			dbLoadState = 2;
			return; // Exit here - don't try to write invalid/no data to phone
		}	
		
		// If we have reached this point, we can assume the database downloaded correctly (right?)
		// So we can overwrite the old database with the new database
		
		// Delete the old database (if it exists)
		File f = new File(dbFilename);
		if(f.exists()) {
			f.delete();
		}
		
	
		// Move the new database
		f = new File(dbFilename + ".tmp");
		if(f.exists()) {
			f.renameTo(new File(dbFilename));
		}
	
		
		dbLoadState = 1;
		
	}
	
}
