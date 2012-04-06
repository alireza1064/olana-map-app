package edu.newpaltz.nynjmohonk;

import android.content.Context;

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
	

}
