package i5.las2peer.services.videoAdapter;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.Consumes;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.DELETE;
import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.PUT;
import i5.las2peer.restMapper.annotations.QueryParam;
import i5.las2peer.restMapper.annotations.swagger.Notes;
import i5.las2peer.restMapper.annotations.swagger.ResourceListApi;
import i5.las2peer.security.Context;
import i5.las2peer.security.UserAgent;
import i5.las2peer.services.videoAdapter.database.DatabaseManager;
import i5.las2peer.services.videoAdapter.idGenerateClient.IdGenerateClientClass;
import i5.las2peer.restMapper.annotations.GET;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.PathParam;
import i5.las2peer.restMapper.annotations.Produces;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.annotations.swagger.ApiInfo;
import i5.las2peer.restMapper.annotations.swagger.ApiResponses;
import i5.las2peer.restMapper.annotations.swagger.ApiResponse;
import i5.las2peer.restMapper.annotations.swagger.Summary;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
//import i5.las2peer.services.videoCompiler.idGenerateClient.IdGenerateClientClass;








import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import org.junit.experimental.theories.ParametersSuppliedBy;


















import net.minidev.json.JSONValue;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.CursorResultSet;
import com.arangodb.entity.CursorEntity;
import com.arangodb.entity.DeletedEntity;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.EdgeEntity;
import com.arangodb.entity.GraphEntity;
import com.arangodb.util.MapBuilder;

import org.json.*;

import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * LAS2peer Service
 * 
 * 
 * 
 * 
 */

public class RelevanceSorting extends Service {

	//private DatabaseManager dbm;

	GraphEntity graphNew;

	public RelevanceSorting() {
		
	}

	public JSONArray sort(JSONArray finalResult, String SearchQuery){
		System.out.println("Apply");
		
		int i=0;
		int[] relevanceFactor = new int[finalResult.length()];
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		JSONArray sortedJsonArray = new JSONArray();
		
		System.out.println(finalResult.toString());
		
		while(!finalResult.isNull(i)){
			JSONObject object = finalResult.getJSONObject(i);
			
			System.out.println(i);
			
			relevanceFactor[i] = getRelevanceFactor(SearchQuery, object.getString("keywords"));
			relevanceFactor[i]+= getRelevanceFactor(SearchQuery, object.getString("text"));
			relevanceFactor[i]+= getRelevanceFactor(SearchQuery, object.getString("title"));
			
			object.put("relevanceFactor", relevanceFactor[i]);
			System.out.println("relevanceFactor: "+relevanceFactor[i]);
			
	        jsonValues.add(object);
			
			i++;
		}
		
		Collections.sort( jsonValues, new Comparator<JSONObject>() {
	        //You can change "Name" with "ID" if you want to sort by ID
	        private static final String KEY_NAME = "relevanceFactor";

	        public int compare(JSONObject a, JSONObject b) {
	            
	        	int valA=0;
	        	int valB=0;
	        	

	            try {
	                
	            	valA = (Integer) a.get(KEY_NAME);
	            	valB = (Integer) b.get(KEY_NAME);
	            		            }
	            catch (JSONException e) {
	                //do something
	            }
	            return -Integer.compare(valA, valB);
	            //return valA.compareTo(valB);
	            //if you want to change the sort order, simply use the following:
	            //return -valA.compareTo(valB);
	        }
	    });

	    for (int j = 0; j < finalResult.length(); j++) {
	        sortedJsonArray.put(jsonValues.get(j));
	    }
		
		System.out.println("SORTED:"+sortedJsonArray.toString());
		
		
		return sortedJsonArray;
	}
	
	private int getRelevanceFactor(String SearchQuery, String Annotation){
		
		String[] queryList = Annotation.split(" ");
		int relevanceFactor=0;
		
		for(int i=0;i<queryList.length;i++){
			if (Annotation.contains(queryList[i])){
				relevanceFactor++;
			}
		}
		return(relevanceFactor);
		
	}
}
