package i5.las2peer.services.videoAdapter;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.GET;
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
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import com.arangodb.entity.GraphEntity;
//import i5.las2peer.services.videoCompiler.idGenerateClient.IdGenerateClientClass;
//import org.junit.experimental.theories.ParametersSuppliedBy;

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
	
	
	

	private DatabaseManager dbm;
	private String epUrl;
	
	GraphEntity graphNew;
	
	
	

	public AdapterClass() {
		// read and set properties values
		setFieldValues();

		if (!epUrl.endsWith("/")) {
			epUrl += "/";
		}
		// instantiate a database manager to handle database connection pooling
		// and credentials
		//dbm = new DatabaseManager(username, password, host, port, database);
	}

	
	@GET
	@Path("getPlaylist")
	public String getPlaylist(@QueryParam(name = "search", defaultValue = "*" ) String searchString){

		System.out.println("SEARCH: "+searchString);
		dbm = new DatabaseManager();
		dbm.init(driverName, databaseServer, port, database, username, password, hostName);
		String annotations = getAnnotations(searchString);
		
		
		
	    
		
		//System.out.println(GreatCircleCalculation.distance(32.9697, -96.80322, 29.46786, -98.53506, 'M') + " Miles\n");
		//System.out.println(GreatCircleCalculation.distance(32.9697, -96.80322, 29.46786, -98.53506, 'K') + " Kilometers\n");
		//System.out.println(GreatCircleCalculation.distance(32.9697, -96.80322, 29.46786, -98.53506, 'N') + " Nautical Miles\n");
		
		return annotations;
	}
	
	
	private String getAnnotations(String searchString){
		System.out.println("An1");
		CloseableHttpResponse response = null;
		URI request = null;
		JSONArray finalResult = null;
		 int size;
		//videos[0] = new String("http://137.226.58.2:8888/v1/AUTH_451035e5f9504a878946697522070c43/public/lnikkila_37db04e1-d757-4d8e-a115-4e9e16e445ea.mp4");
		//videos[1] = new String("http://137.226.58.2:8888/v1/AUTH_451035e5f9504a878946697522070c43/public/lnikkila_f3d7c17e-1178-44d8-99e1-07b0ca5d0d21.mp4");
		//videos[2] = new String("http://137.226.58.2:8888/v1/AUTH_451035e5f9504a878946697522070c43/public/merja_cbc86557-31d6-4dc5-b532-74d8fe80eb34.mp4");
		try {
			
			// Get Annotations
			request = new URI("http://192.168.0.10:8083/annotations/annotations?q="+searchString.replaceAll(" ", ",")+"&part=duration,objectCollection,location,objectId,text,time,title,keywords&collection=TextTypeAnnotation");
			
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
			// Remove the non-video annotations
			int i=0;
			while(!finalResult.isNull(i)){
				JSONObject object = finalResult.getJSONObject(i);
				if(!"Videos".equals(object.getString("objectCollection"))){
					finalResult.remove(i);
			    }
				else{
					//System.out.println("An2");
					// Get the object Ids from the response
					objectIds[i] = new String(object.getString("objectId"));
					
					//System.out.println("TIME: "+ Float.valueOf(object.getString("time")));
					
					time[i] = Float.valueOf(object.getString("time"));
					duration[i] = Float.valueOf(object.getString("duration"));
					
					i++;
					System.out.println("An3");
				}
			}
			
			
			// Temporary code, to limit entries to 3
			/*int j=3;
			while(!finalResult.isNull(j)){
					finalResult.remove(j);
			}*/
			
			System.out.println("An4");
			// The size of the following arrays would be 'size', but for now it is 'j' 
			size=i;
			//int j = size;
			String[] videos = new String[size];
			
			// Once again, 'j' is temporary, it will be 'size' 
			videos = getVideoURLs(objectIds,size);
			
			System.out.println("An5");
			
			for(int k=0;k<size;k++){
				float endtime = duration[k]+time[k];
				videos[k]+="#t="+time[k]+","+endtime;
			}
			
			//videos[0]+="#t=6,16";
			//videos[1]+="#t=7,25";
			//videos[2]+="#t=3,14";
			
			JSONObject object;
			
			for(int k=0;k<size;k++){
				object = finalResult.getJSONObject(k);
				object.append("videoURL", videos[k]);
			}
			FOSPClass fpc = new FOSPClass();
			finalResult = fpc.applyPreferences(finalResult, driverName, databaseServer, port, database, username, password, hostName);
			
			RelevanceSorting rsort = new RelevanceSorting();
			LocationSorting lsort = new LocationSorting();
			System.out.println("RELEVANCE");
			finalResult = rsort.sort(finalResult, searchString);
			double userLat = 50.7743273, userLong = 6.1065564;
			finalResult = lsort.sort(finalResult, userLat, userLong);
			
			System.out.println("check");
			
			
			
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
		
		return finalResult.toString();

	}
	
	
	private String[] getVideoURLs(String[] objectIds, int size){
		
		String[] videos = new String[size];
		CloseableHttpResponse response = null;
		URI request = null;
		
		try {
			
			for(int k=0;k<size;k++){
				
				request = new URI("http://192.168.0.10:8081/video-details/videos/"+objectIds[k]+"?part=url");
				
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet get = new HttpGet(request);
				
				response = httpClient.execute(get);
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				
				StringBuilder content = new StringBuilder();
				String line;
				
				while (null != (line = rd.readLine())) {
				    content.append(line);
				}
				
				JSONObject object = new JSONObject(content.toString());
				
				videos[k] = new String(object.getString("url"));
				System.out.println("VIDEO: "+videos[k]);
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

		return videos;
	}
	
	
	/*@GET
	@Path("videoSegment")
	public HttpResponse videoSegment(){
		
		//VideoSplitter v=new VideoSplitter(new File("D:/Pictures/Performances/Minute_Countdown_Timer.mp4"),1);
		String file1 = "D:/Pictures/Performances/Minute_Countdown_Timer.mp4"; 
		String file2 = "D:/Pictures/Performances/Minute_Countdown_Timer.mp4"; 
		String mergefile = "D:/Pictures/Performances/Minute_Countdown_Timer_joined.mp4";
		
		//AdvancedVideoSplitter(new File("D:/Pictures/Performances/Performance2.mp4"),1);
		try {
			System.out.println("hello");
			
			VideoJoiner.join(file1, file2, mergefile);
			
			//v.splitFiles(1.4, 1.8);
			System.out.println("hello2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}*/
	
	
	
	
	
	
	
	
	
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
