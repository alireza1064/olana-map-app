package edu.newpaltz.nynjmohonk;

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

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class PointOfInterest  {
	private int id, radius;
	private double lat, longe;
	private String loc_name;
	private Context myContext;
	private int compState;
	private Intent i;
	
	
	public PointOfInterest(Context c){
		myContext = c;
		compState = 0;
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
}
