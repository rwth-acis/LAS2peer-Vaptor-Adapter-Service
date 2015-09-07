package i5.las2peer.services.videoAdapter;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
//import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.GET;
//import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.Consumes;
import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.PathParam;
import i5.las2peer.restMapper.annotations.Produces;
import i5.las2peer.restMapper.annotations.QueryParam;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.annotations.swagger.ApiInfo;
import i5.las2peer.restMapper.annotations.swagger.ApiResponse;
import i5.las2peer.restMapper.annotations.swagger.ApiResponses;
import i5.las2peer.restMapper.annotations.swagger.Summary;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.services.videoAdapter.database.DatabaseManager;
import i5.las2peer.services.videoAdapter.util.LocationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.arangodb.entity.GraphEntity;

import org.apache.commons.io.IOUtils;

import com.nimbusds.oauth2.sdk.Response;


















//import i5.las2peer.services.videoCompiler.idGenerateClient.IdGenerateClientClass;
//import org.junit.experimental.theories.ParametersSuppliedBy;
//import com.sun.jersey.multipart.FormDataParam;
//import com.sun.jersey.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * LAS2peer Service
 * 
 * 
 * 
 * 
 */
@Path("adapter")
@Version("0.1")
@ApiInfo(title = "Adapter Service", 
	description = "<p>A RESTful service for Adaptation for Vaptor.</p>", 
	termsOfServiceUrl = "", 
	contact = "siddiqui@dbis.rwth-aachen.de", 
	license = "MIT", 
	licenseUrl = "") 
	
public class AdapterClass extends Service {

	private String port;
	//private String host;
	private String username;
	private String password;
	private String database;
	private String databaseServer;
	private String driverName;
	private String hostName;
	private String useUniCode;
	private String charEncoding;
	private String charSet;
	private String collation;
	
	//public static String userPreferenceService;
	//public static String recommenderService;
	//public static String annotationContext;
	//public static String analyticsService;

	private DatabaseManager dbm;
	private String epUrl;
	
	GraphEntity graphNew;
	
	//private String[] currentAdaptationStatus;
	//private int id=0;
	

	public AdapterClass() {
		// read and set properties values
		setFieldValues();
		//id=0;

		if (!epUrl.endsWith("/")) {
			epUrl += "/";
		}
		// instantiate a database manager to handle database connection pooling
		// and credentials
		//dbm = new DatabaseManager(username, password, host, port, database);
	}

	@POST
	@Path("")
	//@Consumes(MediaType.TEXT_PLAIN)
	//public String postUserProfile(@HeaderParam(name="username" , defaultValue = "*") String username, @HeaderParam(name = "location", defaultValue = "*" ) String location, 
			//@HeaderParam(name = "language", defaultValue = "*" ) String language, @HeaderParam(name = "duration", defaultValue = "*" ) String duration){

	public Response recommend(@QueryParam(defaultValue = "", name = "recommend") boolean recommended){
		
		
		return null;
		
	}
	

	
	@GET
	@Path("getPlaylist")
	public String getPlaylist(@QueryParam(name="sub" , defaultValue = "*") String subId, 
			@QueryParam(name="username" , defaultValue = "*") String username, 
			@QueryParam(name = "search", defaultValue = "*" ) String searchString,
			@QueryParam(name = "lat", defaultValue = "*" ) String lat,
			@QueryParam(name = "lng", defaultValue = "*" ) String lng){

		System.out.println("Adapter Service Checkpoint:0 -- request received"
				+ " - User: "+username+" - Search Query: "+searchString);
		//id++;
		
		
		FutureTask<String> future = new FutureTask<>(new Adapt(searchString, username, lat, lng));
		future.run();
		String annotations = "No Annotation";
		
		try {
			
			annotations = future.get();
	        //System.out.println("Result="+result);

	    } catch (InterruptedException | ExecutionException e) {
	    	
	    	System.out.println("EXCEPTION!!!");
	        e.printStackTrace();
	    }
		
		//String annotations = getAndAdapt(searchString, username, id++);
	    
		return annotations;
	}


	

	// ================= Swagger Resource Listing & API Declarations
	// =====================

	@GET
	@Path("api-docs")
	@Summary("retrieve Swagger 1.2 resource listing.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Swagger 1.2 compliant resource listing"),
			@ApiResponse(code = 404, message = "Swagger resource listing not available due to missing annotations."), })
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse getSwaggerResourceListing() {
		return RESTMapper.getSwaggerResourceListing(this.getClass());
	}

	@GET
	@Path("api-docs/{tlr}")
	@Produces(MediaType.APPLICATION_JSON)
	@Summary("retrieve Swagger 1.2 API declaration for given top-level resource.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Swagger 1.2 compliant API declaration"),
			@ApiResponse(code = 404, message = "Swagger API declaration not available due to missing annotations."), })
	public HttpResponse getSwaggerApiDeclaration(@PathParam("tlr") String tlr) {
		return RESTMapper.getSwaggerApiDeclaration(this.getClass(), tlr, epUrl);
	}

	/**
	 * Method for debugging purposes. Here the concept of restMapping validation
	 * is shown. It is important to check, if all annotations are correct and
	 * consistent. Otherwise the service will not be accessible by the
	 * WebConnector. Best to do it in the unit tests. To avoid being
	 * overlooked/ignored the method is implemented here and not in the test
	 * section.
	 * 
	 * @return true, if mapping correct
	 */
	public boolean debugMapping() {
		String XML_LOCATION = "./restMapping.xml";
		String xml = getRESTMapping();

		try {
			RESTMapper.writeFile(XML_LOCATION, xml);
		} catch (IOException e) {
			e.printStackTrace();
		}

		XMLCheck validator = new XMLCheck();
		ValidationResult result = validator.validate(xml);

		if (result.isValid())
			return true;
		return false;
	}

	/**
	 * This method is needed for every RESTful application in LAS2peer. There is
	 * no need to change!
	 * 
	 * @return the mapping
	 */
	public String getRESTMapping() {
		String result = "";
		try {
			result = RESTMapper.getMethodsAsXML(this.getClass());
		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;
	}
	
}






class Adapt implements Callable<String>{
	
	//private String[] currentAdaptationStatus;
	//private ArrayList<String> currentAdaptationStatus = new ArrayList<String>();
	private String searchString;
	private String username;
	private String lat;
	private String lng;
	//private int id;
	
	
	private String userPreferenceService = "http://localhost:7075/preference";
	private String annotationContext = "http://eiche:7073/annotations/annotationContexts";
	private String analyticsService = "http://localhost:7076/analytics";
	
	
	public Adapt(String searchString, String username, String lat, String lng) {
		// TODO Auto-generated constructor stub
		
		this.searchString = searchString;
		this.username = username;
		this.lat=lat;
		this.lng=lng;
		//this.id = id;
		//System.out.println("id value: "+id);
		//currentAdaptationStatus = new String[1000];
	}


	
	// Get various information from different services and produce adaptive results 
	public String call() throws Exception{
		
		XMPP xmpp = new XMPP();
	    XMPPConnection connection = xmpp.getConnection();
		Chat chat = connection.getChatManager().createChat
				(username+"@role-sandbox.eu", new MessageListener() {
					
					public void processMessage(Chat chat, Message message) {
						
					}
					
		});
	
				
		//currentAdaptationStatus[id] = "Searching for "+ searchString;
		chat.sendMessage("Searching for "+ searchString);
		System.out.println("Adapter Service Checkpoint:1 -- started: getAndAdapt().");
		CloseableHttpResponse response = null;
		URI request = null;
		JSONArray finalResult = null;
		int size;
		
		try {
			
			// Get the annotations
			request = new URI("http://eiche:7073/annotations/annotations?q="+searchString.replaceAll(" ", ",")+"&part=duration,weight,id,objectCollection,domain,location,objectId,text,time,title,keywords&collection=TextTypeAnnotation");
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet get = new HttpGet(request);
			response = httpClient.execute(get);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder content = new StringBuilder();
			String line;
			while (null != (line = rd.readLine())) {
			    content.append(line);
			}
			
			// Parse Response to JSON
			finalResult = new JSONArray(content.toString());
			String[] objectIds = new String[finalResult.length()];
			float time[] = new float[finalResult.length()];
			float duration[] = new float[finalResult.length()];
			
			//currentAdaptationStatus[id] = finalResult.length()+" results obtained!";
			chat.sendMessage(finalResult.length()+" results obtained!");
			
			
			// Remove non-video annotations & get time and duration
			System.out.println("Adapter Service Checkpoint:2 -- removing non-video annotations.");
			int i=0;
			while(!finalResult.isNull(i)){
				JSONObject object = finalResult.getJSONObject(i);
				
				if(!"Videos".equals(object.getString("objectCollection"))){
					finalResult.remove(i);
			    }
				else{
					// Get the object Ids from the response
					objectIds[i] = new String(object.getString("objectId"));
					//System.out.println("TIME: "+ Float.valueOf(object.getString("time")));
					time[i] = Float.valueOf(object.getString("time"));
					duration[i] = Float.valueOf(object.getString("duration"));
					i++;
				}
			}
			System.out.println("Adapter Service Checkpoint:3 -- non-video annotations removed.");
			//currentAdaptationStatus[id] = finalResult.length()+" video results found!";
			chat.sendMessage(finalResult.length()+" video results found!");
			
			
			// Get Video URLs and their Languages from the video details service 
			size=i;
			String[] videos = new String[size];
			String[] languages = new String[size];
			
			videos = getVideoURLs(objectIds,size);
			languages = getVideoLang(objectIds,size);
			
			
			
			// Add play time and duration to the url
			for(int k=0;k<size;k++){
				float endtime = duration[k]+time[k];
				videos[k]+="#t="+time[k]+","+endtime;
			}
			
			
			
			// Add URLs and languages to the json objects
			JSONObject object;
			for(int k=0;k<size;k++){
				object = finalResult.getJSONObject(k);
				object.append("videoURL", videos[k]);
				object.put("lang", languages[k]);
			}
			
			

			// Apply user preferences
			System.out.println("Adapter Service Checkpoint:4 -- Applying User Preferences.");
			//currentAdaptationStatus[id] = "Applying User Preferences.";
			chat.sendMessage("Applying User Preferences.");
			finalResult = applyPreferences(finalResult, searchString, username, chat);
			System.out.println("Adapter Service Checkpoint:5 -- User Preferences applied.");
			
			
			
			// Save the search query and search results for recommendation 
			/*dbm = new DatabaseManager();
			dbm.init(driverName, databaseServer, port, database, this.username, password, hostName);
			dbm.saveSearch(searchString, finalResult.toString(), username);*/
			
			connection.disconnect();
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return IOUtils.toString(finalResult, StandardCharsets.UTF_8);
		return finalResult.toString();

	}
	
	
	
	
	// Get URL information for each segment
	private String[] getVideoURLs(String[] objectIds, int size){
		
		String[] videos = new String[size];
		CloseableHttpResponse response = null;
		URI request = null;
		
		try {
			
			for(int k=0;k<size;k++){
				
				// Get video details
				request = new URI("http://eiche:7071/video-details/videos/"+objectIds[k]+"?part=url,language");
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet get = new HttpGet(request);
				response = httpClient.execute(get);
				
				// Parse the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuilder content = new StringBuilder();
				String line;
				while (null != (line = rd.readLine())) {
				    content.append(line);
				}
				JSONObject object = new JSONObject(content.toString());
				
				// Save in a String array
				videos[k] = new String(object.getString("url"));
			}
			
		} catch (URISyntaxException e) {
			
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		return videos;
	}
	
	// Get language information for each segment
	private String[] getVideoLang(String[] objectIds, int size){
		
		String[] languages = new String[size];
		CloseableHttpResponse response = null;
		URI request = null;
		
		try {
			
			for(int k=0;k<size;k++){
				
				// Get video details
				request = new URI("http://eiche:7071/video-details/videos/"+objectIds[k]+"?part=url,language");				
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet get = new HttpGet(request);
				response = httpClient.execute(get);
				
				// Parse the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuilder content = new StringBuilder();
				String line;
				while (null != (line = rd.readLine())) {
				    content.append(line);
				}
				JSONObject object = new JSONObject(content.toString());
				
				// Save in a String array
				languages[k] = new String(object.getString("language"));
			}
			
		} catch (URISyntaxException e) {
			//System.out.println(e);
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return languages;
	}

	// Adapt the results based on user preferences
	private JSONArray applyPreferences(JSONArray finalResult, String searchString, 
			String username, Chat chat){
		
		
		try {
			
			// GETTING USER PREFERENCES
			
			System.out.println("Adapter Service Checkpoint:4a -- Getting User Preferences.");
			String preferenceString = getResponse(userPreferenceService+"?username="+username);
			JSONObject preferencesJSON = new JSONObject(preferenceString);
	

	
			// LANGUAGE FILTERING
			
			System.out.println("Adapter Service Checkpoint:4b -- Applying language filtering.");
			//currentAdaptationStatus[id] = "Applying language filtering...";
			chat.sendMessage("Applying language filtering...");
			//currentAdaptationStatus.add(id, "Applying language filtering...");
			languageFiltering(finalResult, preferencesJSON.getString("language"));
			System.out.println(finalResult.toString());
			
			// RELEVANCE ORDERING
	
			System.out.println("Adapter Service Checkpoint:4c -- Applying relevance ordering.");
			//currentAdaptationStatus[id] = "Applying relevance ordering...";
			chat.sendMessage("Applying relevance ordering...");
			//currentAdaptationStatus.add(id, "Applying relevance ordering...");
			RelevanceSorting rsort = new RelevanceSorting();
			finalResult = rsort.sort(finalResult, searchString);
			System.out.println(finalResult.toString());
			
			// LOCATION ORDERING
			
			System.out.println("Adapter Service Checkpoint:4d -- Applying location ordering.");
			//currentAdaptationStatus[id] = "Applying location ordering...";
			chat.sendMessage("Applying location ordering...");
			LocationSorting lsort = new LocationSorting();
			if(lat.equals("*")){
				finalResult = lsort.sort(finalResult, preferencesJSON.getString("location"), 
						false);
			}
			else{
				finalResult = lsort.sort(finalResult, lat+"-"+lng, true);
			}
			System.out.println(finalResult.toString());
	
			
			// WEIGHT ORDERING
			
			System.out.println("Adapter Service Checkpoint:4e -- Applying segment weight ordering.");
			//currentAdaptationStatus[id] = "Applying segment weight ordering...";
			chat.sendMessage("Applying segment weight ordering...");
			// get and sort w.r.t weight
			finalResult = weightSort(getWeight(finalResult));
			
			System.out.println(finalResult);
			
			// TRIMMING BASED ON PREFERRED DURATION
			
			int i=0, currentDuration = 0;
			//int duration = Integer.parseInt(preferencesJSON.getString("duration").replace("\n", ""));
			
			// Converting the duration into minutes
			String time = preferencesJSON.getString("duration").replace("\n", ""); //mm:ss
			String[] units = time.split(":"); //will break the string up into an array
			int minutes = Integer.parseInt(units[0]); //first element
			int seconds = Integer.parseInt(units[1]); //second element
			int duration = 60*minutes + seconds; //add up our values
			//System.out.println("minutes: "+minutes);
			//System.out.println("seconds: "+seconds);
			System.out.println("duration: "+duration);
			
			while(!finalResult.isNull(i)){
				System.out.println("inside duration trimming");
				System.out.println("Current duration: "+currentDuration);
				if(currentDuration<=duration){
					
					JSONObject object = finalResult.getJSONObject(i);
					
					currentDuration += Integer.parseInt(object.getString("duration")
							.replace("\n", ""));
					i++;
				}
				else{
					
					finalResult.remove(i);
				}
				
				
			}
			
			System.out.println(finalResult.toString());
		
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finalResult;
		
	}
	
	// Filter out all the segments with different language than the user preferred language
	private JSONArray languageFiltering(JSONArray finalResult, String userLang){
		
		int i=0;
		while(!finalResult.isNull(i)){
			JSONObject object = finalResult.getJSONObject(i);
			if(!userLang.equals(object.getString("lang"))){
				System.out.println("Adapter Service Checkpoint:4b(a) -- User Language: "+userLang+" -- Video Language: "+object.getString("lang"));
				finalResult.remove(i);
		    }
			else{
				i++;
			}
		}
		return finalResult;
	}
	
	// Get weight for all the selected segments
	private JSONArray getWeight(JSONArray finalResult){
		
		String preferenceString;
		JSONObject object = new JSONObject();
		
		int i=0;
		while(!finalResult.isNull(i)){
			object = finalResult.getJSONObject(i);
			
			// Getting the annotation context to obtain the edge id
			preferenceString = getResponse(annotationContext+"/"+object.getString("objectId")+"/"+object.getString("id"));

			// Parsing the response
			JSONArray annContext = new JSONArray(preferenceString);
			JSONObject annContextObject = annContext.getJSONObject(0);
			
			// Getting the Edge Id
			String edgeId = annContextObject.getString("id");
			
			// Getting the Weight for the segment
			String weight = getResponse(analyticsService+"/"+"weight?edge="+edgeId);
			
			// Adding it to the JSON Object
			object.put("weight", Integer.parseInt(weight.replace("\n", "")));
			object.put("edgeId", Integer.parseInt(edgeId.replace("\n", "")));
			i++;
			
		}
		return finalResult;
	}
	
	// Order the segments based on the Weight field in the JSON Objects
	private JSONArray weightSort(JSONArray finalResult){
		
		int i=0;
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		JSONArray sortedJsonArray = new JSONArray();
		
		while(!finalResult.isNull(i)){
			JSONObject object = finalResult.getJSONObject(i);
	        jsonValues.add(object);
			i++;
		}
		
		Collections.sort(jsonValues, new Comparator<JSONObject>() {
	        //You can change "Name" with "ID" if you want to sort by ID
	        private static final String KEY_NAME = "weight";

	        public int compare(JSONObject a, JSONObject b) {
	        	int valA=0;
	        	int valB=0;
	        	
	            try {
	            	System.out.println(a.get(KEY_NAME));
	            	
	            	valA =  (Integer) a.get(KEY_NAME);
	            	valB =  (Integer) b.get(KEY_NAME);
	            }
	            catch (JSONException e) {
	            	System.out.println(e);
	            	
	                //do something
	            }
	            //System.out.println("valA "+valA);
	            return -Integer.compare(valA, valB);
	            //return valA.compareTo(valB);
	            //if you want to change the sort order, simply use the following:
	            //return -valA.compareTo(valB);
	        }
	    });

	    for (int j = 0; j < finalResult.length(); j++) {
	        sortedJsonArray.put(jsonValues.get(j));
	    }
		return sortedJsonArray;
	}
	
	// Get response from the given uri
	private String getResponse(String uri){
		
		CloseableHttpResponse response = null;
		URI httpRequest;
		String preferenceString = null;
		
		try {
			httpRequest = new URI(uri);
		
			CloseableHttpClient httpPreferenceService = HttpClients.createDefault();
			HttpGet getPreferences = new HttpGet(httpRequest);
			response = httpPreferenceService.execute(getPreferences);
			
	        HttpEntity entity = response.getEntity();
	        
	        if (entity != null) {
	            InputStream instream = entity.getContent();
	            preferenceString = convertStreamToString(instream);
	            //System.out.println("RESPONSE: " + preferenceString);
	            instream.close();
	        }
        
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return preferenceString;
		
	}
	
	private static String convertStreamToString(InputStream is) {
		
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	
	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	
	

}





