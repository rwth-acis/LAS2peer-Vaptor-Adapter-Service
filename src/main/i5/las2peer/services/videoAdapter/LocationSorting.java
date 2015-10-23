package i5.las2peer.services.videoAdapter;

import i5.las2peer.api.Service;
import i5.las2peer.services.videoAdapter.util.GreatCircleCalculation;
import i5.las2peer.services.videoAdapter.util.LocationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.arangodb.entity.GraphEntity;


/**
 * LAS2peer Service
 * 
 * 
 * 
 * 
 */

public class LocationSorting extends Service {

	GraphEntity graphNew;

	public LocationSorting() {
		
	}
	
	public JSONArray initializeSort(JSONArray finalResult, String location,
			boolean isCurrentLocation, boolean locationBool){
		
		double lat, lng;
		
		if(isCurrentLocation){
			
			//System.out.println("lat: "+latlng[0]+"lng: "+latlng[1]);
			String latlng[] = location.split("-");
			System.out.println("lat: "+latlng[0]+"lng: "+latlng[1]);
			lat = Double.parseDouble(latlng[0]);
			lng = Double.parseDouble(latlng[1]);
		}
		else{
			LocationService ls = new LocationService();
			double[] userltln = ls.getLongitudeLatitude(location);
			lat = userltln[0];
			lng = userltln[1];
		}
			
			
		
		
		//System.out.println("Location Sort");
		LocationService tDirectionService = new LocationService();
		int i=0;
		double[] distance = new double[finalResult.length()];
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		JSONArray sortedJsonArray = new JSONArray();
		
		//System.out.println(finalResult.toString());
		
		while(!finalResult.isNull(i)){
			JSONObject object = finalResult.getJSONObject(i);
			
			//System.out.println("ANNOTATION "+i);
			
		    double array[] = tDirectionService.getLongitudeLatitude(object.getString("location"));
			distance[i] = GreatCircleCalculation.distance(array[0], array[1], lat, lng, 'M');
			
			object.put("distance", distance[i]);
			object.put("Latitude", array[0]);
			object.put("Longitude", array[1]);
			//System.out.println("distance: "+distance[i]);
			
	        jsonValues.add(object);
			
			i++;
		}
		
		
		if(locationBool){
			return sortedJsonArray = sorting(finalResult, jsonValues);
		}
		else{
			for (int j = 0; j < finalResult.length(); j++) {
		        sortedJsonArray.put(jsonValues.get(j));
		    }
			return sortedJsonArray;
		}
		
	}
	
	public JSONArray sorting(JSONArray finalResult, List<JSONObject> jsonValues){
		
		JSONArray sortedJsonArray = new JSONArray();
		
		Collections.sort( jsonValues, new Comparator<JSONObject>() {
	        //You can change "Name" with "ID" if you want to sort by ID
	        private static final String KEY_NAME = "distance";

	        public int compare(JSONObject a, JSONObject b) {
	            
	        	double valA=0;
	        	double valB=0;

	        	try {
	                
	            	valA = (Double) a.get(KEY_NAME);
	            	valB = (Double) b.get(KEY_NAME);
	            		            }
	            catch (JSONException e) {
	                //do something
	            }
	            return Double.compare(valA, valB);
	        }
	    });

	    for (int j = 0; j < finalResult.length(); j++) {
	        sortedJsonArray.put(jsonValues.get(j));
	    }
		
		//System.out.println("SORTED:"+sortedJsonArray.toString());
		
		
		return sortedJsonArray;
	}
	
}
