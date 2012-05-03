package edu.newpaltz.nynjmohonk;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.location.LocationManager;

public class NoteBuilder {
	private Context myContext;
	private int ID;
	private String name, short_info, long_info;
	
	
	public NoteBuilder(Context c){
		myContext = c;
	}
	
	public int getID(){
		return ID;
	}
	
	public String getName(){
		return name;
	}
	
	public String getShort_info(){
		return short_info;
	}
	
	public String getLong_info(){
		return long_info;
	}
	
	public void setVal(int column, int val){
		switch(column){
		case 0 : this.ID = val; break;
		default: break;
		}
	}
	
	public void setVal(int column, String val){
		switch(column){
		case 1: this.name = val; break;
		case 2: this.short_info = val; break;
		case 3: this.long_info = val; break;
		default: break;
		}
	}
	
	public static ArrayList<NoteBuilder> getAllNotes(Context c) throws SQLiteException{

		NoteDBHelper ndb = NoteDBHelper.getDBInstance(c);
		try {
			ndb.createDatabase();
		} catch (IOException e) {
			//Log.d("DEBUG", "Error creating database..."); // CHANGEME
			return null;
		}

		// Open the sqlite database
		try {
			ndb.openDatabase();
		} catch (IOException e) {
			//Log.d("DEBUG", "Error opening database..."); // CHANGEME
			return null;
		}

		// Generate the AlertDialog that will list all of the maps. Also put the information on
		// the maps into an ArrayList so that it's accessible when the users selects a map


		//This needs to be fixed!!  
		//It needs something to pull the DB table name from the locs DB to specify correct query String
		
		//String query = "SELECT * FROM note_info where map = "+MyApplication.currentMap;
		
		String query = "SELECT * FROM note_info";
		return ndb.generateNotes(query, null);		
	}
	

}
