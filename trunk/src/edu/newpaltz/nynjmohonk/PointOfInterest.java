package edu.newpaltz.nynjmohonk;

import android.content.Context;




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
	
	
}
