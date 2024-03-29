package edu.newpaltz.nynjmohonk;
//Jon Davin
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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
public class NoteDBHelper extends SQLiteOpenHelper {
	static Logger log;
	private static String DB_PATH = ""; 
	private static final String DB_NAME = "notification_info.sqlite";
	private static Context myContext;
	private SQLiteDatabase myDatabase;	
	private static NoteDBHelper myDBConnection;
	private static NoteBuilder[] NoteStore;
	
	
	//private static String[] proxStore;
	private static NoteBuilder n;
	
	
	/*public static void main(String[] args){
		NoteDBHelper note = new NoteDBHelper(myContext);
		log.info("NoteDBHelper created");
		//proxStore = LocDBHelper.getProxStore();
		note.downloadDB(myContext);
		log.info("Note DB downloaded");
		note.generateNotes("SELECT * FROM NP_loc_info", null);
		log.info("Notes generated");
		
		
	}*/
	
	public static NoteBuilder[] getNoteStore(){
		return NoteStore;
	}
	
	public static NoteBuilder getNoteBuilderByName(NoteBuilder[] NB, String name){
		for(int i = 0;i<NB.length;i++){
			if(NB[i].getName().equals(name)){
				return NB[i];
			}
		}
		
		return null;
	}
	
	
	/**
	 * Generate a MapDatabaseHelper instance within the context of this application. Also sets the path
	 * to the SQLite database file based on the application context
	 * @param context The current application context
	 */
	@SuppressWarnings("static-access")
	public NoteDBHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		DB_PATH = "/data/data/" + context.getApplicationContext().getPackageName() + "/databases/";
		//this.generateNotes("SELECT * FROM NP_loc_info", null);
	
	}
	
	

	/**
	 * Gets an instance of MapDatabaseHelper which will be connected to our SQLite database
	 * @param context The current application context
	 * @return A connection to the database
	 */
	public static synchronized NoteDBHelper getDBInstance(Context context) {
		if (myDBConnection == null) {
			myDBConnection = new NoteDBHelper(context);
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
		String myPath = DB_PATH + DB_NAME;
		File dbFile = new File(myPath);
		return dbFile.exists();
		
		/*
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does not yet exist
			//this.setupDatabase();
		}
		
		if(checkDB != null) {
			checkDB.close();
		}
		
		return checkDB != null;
		*/
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
	 * Returns an ArrayList of Map objects that are returned by the SQL SELECT query. This method assumes
	 * that the select query is accessing the Map table 
	 * @param query The SQL SELECT query on the Map table
	 * @param selectionArgs A list of selection arguments, if applicable
	 * @return An ArrayList of map objects corresponding to our SELECT query
	 */
	// Select query function 
	public ArrayList<NoteBuilder> generateNotes(String query, String[] selectionArgs) {
		
		//query = "SELECT * FROM NP_loc_info";
		int t = 0;
		ArrayList<NoteBuilder> results = new ArrayList<NoteBuilder>();
		Cursor c = myDatabase.rawQuery(query, selectionArgs);
		if(c.moveToFirst()) {
			do {
				 n = new NoteBuilder(myContext);
			
				for(int i = 0; i < c.getColumnCount(); i++) {
					if(c.getString(i) != null) {
						if(i == 0) {
							n.setVal(i, c.getInt(i));
						} else if(i>0&&i<4) {
							n.setVal(i, c.getString(i));
						}else{
							//do nothing
						}
					}
				}
				results.add(n);
			//	NoteStore[t]=n;
				
				Log.v("NOTE","Note "+t+" Gereated and stored");
				t++;
			} while(c.moveToNext());
		}
		c.close();
		c.deactivate();
		myDatabase.close();
		return results;
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
		String downloadURL = c.getString(R.string.Note_database_url);
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

			NoteDBHelper ndb = NoteDBHelper.getDBInstance(c);
	        ndb.setupDatabase();
	
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

