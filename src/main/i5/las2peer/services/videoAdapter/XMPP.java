package i5.las2peer.services.videoAdapter;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;


/**
 * 
 * Establishes the connection for the XMPP message transfer. 
 * Uses 'xmpp.properties' file for configuration.
 *
 */
public class XMPP {
	
	
	private XMPPConnection connection = null;
	private ConnectionListener connListener = null;
	
	public XMPP(){
		super();
	}
	
	/**
	 * Establishes the connection for the XMPP message transfer.
	 * @return XMPPConnection
	 */
	public XMPPConnection getConnection(){
		
		String server, port, service, username, password;
		//String INPUT_FILE = "xmpp";
		
		/*server = GetProperty.getParam("server", INPUT_FILE);
		port = GetProperty.getParam("port", INPUT_FILE);
		service = GetProperty.getParam("service", INPUT_FILE);
		username = GetProperty.getParam("user", INPUT_FILE);
		password = GetProperty.getParam("password", INPUT_FILE);*/
		
		server = "137.226.232.165";
		port = "5222";
		service = "role-sandbox.eu";
		username = "vaptor";
		password = "vaptorpassword";
		
		//username = "f0ad30cb-b7c8-48c3-ad30-cbb7c838c33d";
		//password = "_qqsMpPLrYbld5A4u65HcA";
		
		if(connection == null){

			//XMPPConnection.DEBUG_ENABLED = true;
			XMPPConnection.DEBUG_ENABLED = false;
			
			ConnectionConfiguration connConfiguration = new ConnectionConfiguration(server, Integer.parseInt(port), service);
			connection = new XMPPConnection(connConfiguration);
			
			//configure provider manager for extra Parsers
			configureProviderManager(ProviderManager.getInstance());
			
			try {
				connection.connect();
				
				//login
				connection.login(username, password);
				System.out.println("XMPPClient"+ "Logged in as " + connection.getUser());
			
				configurePacketListeners();
				
				configureFileManager();
				
				
			}catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return connection;
	}
	
	private void configureProviderManager(ProviderManager pm) {
		
	}
	
	private void configurePacketListeners(){
		
	} 
	
	
	private void configureFileManager(){
		FileTransferManager manager = new FileTransferManager(getConnection());    
	}
}
