package edu.newpaltz.nynjmohonk;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.location.LocationManager;




public class PointOfInterest  {

	private int id, radius;
	private double lat, longe;
	private String loc_name;
	private Context myContext;




	public PointOfInterest(Context c){
		myContext = c;		
	}

	public double getLat(){
		return lat;
	}

	public double getLong(){
		return longe;
	}

	public int getID(){
		return id;
	}

	public int getRadius(){
		return radius;
	}

	public String getLocName(){
		return loc_name;
	}



	public void setVal(int column, int val){
		switch(column){
		case 0: this.id = val; break;
		case 4: this.radius = val; break;
		default: break;
		}

	}

	public void setVal(int column, double val){
		switch(column){
		case 2: this.lat = val; break;
		case 3: this.longe = val; break;
		default: break;
		}

	}

	public void setVal(int column, String val){
		switch(column){
		case 1: this.loc_name = val; break;
		default: break;
		}
	}

	public static ArrayList<PointOfInterest> getAllPoints(Context c,LocationManager loc) throws SQLiteException{

		LocDBHelper ldb = LocDBHelper.getDBInstance(c);
		try {
			ldb.createDatabase();
		} catch (IOException e) {
			//Log.d("DEBUG", "Error creating database..."); // CHANGEME
			return null;
		}

		// Open the sqlite database
		try {
			ldb.openDatabase();
		} catch (IOException e) {
			//Log.d("DEBUG", "Error opening database..."); // CHANGEME
			return null;
		}

		// Generate the AlertDialog that will list all of the maps. Also put the information on
		// the maps into an ArrayList so that it's accessible when the users selects a map


		//This needs to be fixed!!  
		//It needs something to pull the DB table name from the locs DB to specify correct query String
		
		//query = "SELECT * FROM locs where map = "+MyApplication.currentMap;
		
		String query = "SELECT * FROM locs ";
		return ldb.generatePoints(query, null,loc);		
	}



}
