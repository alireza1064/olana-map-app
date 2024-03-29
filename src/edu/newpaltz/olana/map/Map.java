package edu.newpaltz.olana.map;
//Jon Davin
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Map is an object which is mapped to a particular row in the Map table
 */
public class Map implements Parcelable {
	private int imageLoadState;
	private int id, width, height;
	private double min_longitude, max_latitude, lon_per_pixel, lat_per_pixel;
	private String name, ekey, fname, url, polygon_points, description;
	private Context myContext;
	private Polygon polygon;
	private TEA tea; 
	
	/**
	 * Returns a map object with the given application context
	 */
	 
	public Map(Context c) {
		imageLoadState = 0;
		myContext = c;
	}
	
	/**
	 * Sets a value of this instance based on the column number in the SQLite database.
	 * Probably not the best method of doing this because as the database schema changes, so does
	 * this method. 
	 * @param column The column number of the table
	 * @param val A String value to assign to the selected value
	 */
	public void setVal(int column, String val) {
		switch(column) {
			case 0: this.id = Integer.parseInt(val); break;
			case 1: this.name = val; break;
			case 2: this.ekey = val; break;
			case 3: this.fname = val; break;
			case 4: this.url = val; break;
			case 9: this.polygon_points = val; break;
			case 10: this.description = val; break;
			default: break;
		}
	}
	
	
	/**
	 * Sets a value based on the column number. Overloaded method, uses a double value in this case. See other
	 * method for more comments
	 * @param column The column number of the table
	 * @param val The double value to assign
	 */
	public void setVal(int column, double val) {
		switch(column) {
			case 5: this.min_longitude = val; break;
			case 6: this.max_latitude = val; break;
			case 7: this.lat_per_pixel = val; break;
			case 8: this.lon_per_pixel = val; break;
			default: break;
		}
	}
	
	/**
	 * Sets a value based on the column number. Overloaded method, uses a int value in this case. See other
	 * method for more comments
	 * @param column The column number of the table
	 * @param val The double value to assign
	 */
	public void setVal(int column, int val) {
			switch(column) {
			case 11: this.width = val; break;
			case 12: this.height = val; break;
			default: break;
		}
	}	
	
	public int getMapMidX(){
		
		return width/2;
	}
	
	public int getMapMidY(){
		return height/2;
	}
	
	/**
	 * @return The unique id of the row for this map
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return The name/title of the map
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The encryption key for the map file
	 */
	public String getEkey() {
		return ekey;
	}

	/**
	 * @return The filename of the map when it's stored on the phone
	 */
	public String getFname() {
		return fname;
	}

	/**
	 * @return The URL of the map image where it will be downloaded from
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return The minimum longitude covered by the map image (from the world file)
	 */
	public double getMinLongitude() { 
		return min_longitude;
	}
	
	/**
	 * @return The maximum latitude covered by the map image (from the world file)
	 */
	public double getMaxLatitude() {
		return max_latitude;
	}
	
	// Maximum longitude and minimum latitude are calculated later on
	
	/**
	 * @return The latitude amount per pixel in the image (from the world file)
	 */
	public double getLatPerPixel() {
		return lat_per_pixel;
	}
	
	/**
	 * @return The longitude amount per pixel in the image (from the world file)
	 */
	public double getLonPerPixel() {
		return lon_per_pixel;
	}
	
	/**
	 * @return The height of the image
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @return The width of the image
	 */
	public int getWidth() {
		return width;
	}	
	
	/**
	 * @return The load state of the image
	 */
	public int getImageLoadState() { 
		return imageLoadState;
	}
	
	/**
	 * @return The polygon for this map
	 */
	public Polygon getPolygon() {
		String [] points = polygon_points.split(";");
		int[] xCoords = new int[points.length];
		int[] yCoords = new int[points.length];
		for(int i = 0; i < points.length; i++) {
			if(points[i] != "") { // just in case...
				String [] point = points[i].split(",");
				xCoords[i] = Integer.parseInt(point[0]);
				yCoords[i] = Integer.parseInt(point[1]);
			}
		}
		polygon = new Polygon(xCoords, yCoords, 4); // hardcoding a 4, assume area is always a square
		return polygon;
	}
	
	/**
	 * @return The description of this map
	 */
	public String getDescription() {
		return description;
	}
	
	
	/**
	 * Generate filename from the image URL
	 * @return The filename of the image of this map
	 */
	public String getFilename() {
		return url.substring(url.lastIndexOf('/') + 1);
	}

	/**
	 * Unused method that is required by Parcable type
	 * @return 0 by default
	 */
	@Override
	public int describeContents() {
		return 0;
	}
	
	/**
	 * @return True if the map is downloaded, False otherwise
	 */
	public boolean isDownloaded() {
		try {
			// File exists, set image load state as loaded
			myContext.openFileInput(getFilename() );  //changed here
		} catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Download image, if needed, from the URL of this instance into our data/data folder and
	 * then encrypt the image file
	 */
	public void loadImage() {
	//	tea = new TEA(getEkey().getBytes());	
		try {
			// File exists, set image load state as loaded
			myContext.openFileInput(getFilename() ); 							//changed here
			imageLoadState = 1; // image is done loading!
			return;
		} catch (FileNotFoundException e) {
			imageLoadState = 3;
			// File does not exist, attempt to load from URL
			URL myURL = null;
			try {
				myURL = new URL(url);
			} catch (MalformedURLException me) {
				imageLoadState = 2; // error loading/downloading image
				return;
			}
		
			try {
				URLConnection ucon = myURL.openConnection();
				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
	//changed line below
				FileOutputStream f = myContext.openFileOutput(getFilename() , Context.MODE_WORLD_READABLE);
				int nRead;
	
				byte[] data = new byte[1024];
				while((nRead = is.read(data, 0, data.length)) != -1) {
					f.write(data, 0, nRead);
					//f.flush();
				}
				
				is.close();
				bis.close();
					
			} catch (Exception exp) {
				imageLoadState = 2;
				return; // Exit here - don't try to write invalid/no data to phone
			}			
			
		}
		
		System.gc();
		imageLoadState = 1; // 1 = image fully downloaded and loaded
	}
	
	/**
	 * Decrypt the image so that it can be read in as a bitmap
	 */
	
	
	public byte[] getDecryptedImage(Context c) {
		tea = new TEA(getEkey().getBytes());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			final InputStream is = c.openFileInput(getFilename());  //changed here
			byte[] b = new byte[1024];
			int bytesRead;
			
			// Decrypt the top bytes
			byte[] dec = new byte[1028];
			if(is.read(dec, 0, dec.length) != -1) {
				dec = tea.decrypt(dec);
				bos.write(dec, 0, dec.length);
			}
			
			// Read the rest of the file
			while ((bytesRead = is.read(b, 0, b.length)) != -1) {
				bos.write(b, 0, bytesRead);
			}
			is.close();
			
		} catch(FileNotFoundException e) {
			return null;
		} catch(IOException e) {
			return null;
		}
		
		return bos.toByteArray();
	}
	
	/**
	 * Temporarily decrpyts the image on the disk so that we can send the filename to our
	 * native Bitmap decoder
	 */
	/*
	
	public void decryptImage(Context c) {
		tea = new TEA(getEkey().getBytes());
		try {
			final FileOutputStream os = c.openFileOutput(getFilename(), Context.MODE_WORLD_READABLE);
			final InputStream is = c.openFileInput(getFilename() );		//changed here
			byte[] b = new byte[1024];
			int bytesRead;
			
			// Decrypt the top bytes
			byte[] dec = new byte[1028];
			if(is.read(dec, 0, dec.length) != -1) {
				dec = tea.decrypt(dec);
				os.write(dec, 0, dec.length);
			}
			
			// Read the rest of the file
			while ((bytesRead = is.read(b, 0, b.length)) != -1) {
				os.write(b, 0, bytesRead);
			}
			is.close();
			
		} catch(FileNotFoundException e) {
			// Do something
		} catch(IOException e) {
			// Do something
		}
		
	}
	*/
	
	/**
	 * Required by Parceable type. An inner class that creates a parcable from the Map object
	 */
	public static final Parcelable.Creator<Map> CREATOR = new Parcelable.Creator<Map>() {

		@Override
		public Map createFromParcel(Parcel source) {
			return new Map(source);
		}

		@Override
		public Map[] newArray(int size) {
			return new Map[size];
		}
		
	};
	
	/**
	 * A parcel is a faster Serializable type, so as such we are writing out are data into a format
	 * that can be moved around quickly
	 */
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(name);
		out.writeString(ekey);
		out.writeString(fname);
		out.writeString(url);
		out.writeDouble(min_longitude);
		out.writeDouble(max_latitude);
		out.writeDouble(lat_per_pixel);
		out.writeDouble(lon_per_pixel);
		out.writeString(polygon_points);
		out.writeString(description);
		out.writeInt(width);
		out.writeInt(height);
	}
	
	/**
	 * A private constructor used in the Parcable inner class to create a map from a Parcable object
	 */
	private Map(Parcel in) {
		id = in.readInt();
		name = in.readString();
		ekey = in.readString();
		fname = in.readString();
		url = in.readString();
		min_longitude = in.readDouble();
		max_latitude = in.readDouble();
		lat_per_pixel = in.readDouble();
		lon_per_pixel = in.readDouble();
		polygon_points = in.readString();
		description = in.readString();
		width = in.readInt();
		height = in.readInt();
	}

	
	
	/**
	 * Static method to get all maps from the SQLite database
	 * @param c The current application context
	 * @return An ArrayList<Map> with all the maps in it
	 */
	public static ArrayList<Map> getAllMaps(Context c) throws SQLiteException{
		MapDatabaseHelper mdb = MapDatabaseHelper.getDBInstance(c);
        try {
        	mdb.createDatabase();
        } catch (IOException e) {
        	//Log.d("DEBUG", "Error creating database..."); // CHANGEME
        	return null;
        }
        
        // Open the sqlite database
        try {
        	mdb.openDatabase();
        } catch (IOException e) {
        	//Log.d("DEBUG", "Error opening database..."); // CHANGEME
        	return null;
        }
        
        // Generate the AlertDialog that will list all of the maps. Also put the information on
        // the maps into an ArrayList so that it's accessible when the users selects a map
        String query = "SELECT * FROM map";
        return mdb.selectFromDatabase(query, null);		
	}
	
	
	
	/**
	 * Static method to get all downloaded maps from the SQLite database
	 * @param c The current application context
	 * @return An ArrayList<Map> with all the downlaoded maps
	 */
	public static ArrayList<Map> getDownloadedMaps(Context c) throws SQLiteException, IOException {
		ArrayList<Map> results = new ArrayList<Map>();
		ArrayList<Map> allMaps = Map.getAllMaps(c);
		for(int i = 0; i < allMaps.size(); i++) {
			if(allMaps.get(i).isDownloaded()) {
				results.add(allMaps.get(i));
			}
		}
		return results;
	}
	
	/**
	 * Static method to get all undownloaded maps from the SQLite database
	 * @param c The current application context
	 * @return An ArrayList<Map> with all the undownloaded maps
	 */
	public static ArrayList<Map> getUndownloadedMaps(Context c) throws SQLiteException, IOException {
		ArrayList<Map> results = new ArrayList<Map>();
		ArrayList<Map> allMaps = Map.getAllMaps(c);
		for(int i = 0; i < allMaps.size(); i++) {
			if(allMaps.get(i).isDownloaded()) {
				results.add(allMaps.get(i));
			}
		}
		return results;
	}	
}
