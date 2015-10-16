package i5.las2peer.services.videoAdapter;

import java.io.IOException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;


/*import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;*/


/**
 * 
 * Establishes the connection for the XMPP message transfer. 
 * Uses 'xmpp.properties' file for configuration.
 *
 */
public class XMPP {
	
	
	private XMPPTCPConnection connection = null;
	private ConnectionListener connListener = null;
	
	public XMPP(){
		super();
	}
	
	/**
	 * Establishes the connection for the XMPP message transfer.
	 * @return XMPPConnection
	 */
	public XMPPConnection getConnection(){
		
		System.out.println("Get Connection");
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
			//XMPPConnection.DEBUG_ENABLED = false;
			
			XMPPTCPConnectionConfiguration.Builder connConfiguration = XMPPTCPConnectionConfiguration.builder();
			connConfiguration.setServiceName(service);
			connConfiguration.setHost(server);
			connConfiguration.setPort(Integer.parseInt(port));
			connConfiguration.setUsernameAndPassword(username, password);
			connConfiguration.setSecurityMode(SecurityMode.disabled);
			connConfiguration.allowEmptyOrNullUsernames();
			
			//ConnectionConfiguration connConfiguration = new ConnectionConfiguration(server, Integer.parseInt(port), service);
			connection = new XMPPTCPConnection(connConfiguration.build());
			
			//configure provider manager for extra Parsers
			//configureProviderManager(ProviderManager.getInstance());
			
			if(connection==null)
				System.out.println("Connection is null (1)");
			else
				System.out.println("Connection is not null (1)");
			
			try {
				if(connection.isConnected())
					System.out.println("Connection is connected (1.5)");
				else
					connection.connect();
				
				if(connection==null)
					System.out.println("Connection is null (2)");
				else if(connection.isConnected())
					System.out.println("Connection is connected (2)");
				else
					System.out.println("Connection is not null (2)");
				
				//login
				//if(!connection.isAuthenticated())
					//connection.login(username, password);
				connection.login();
				
				System.out.println("XMPPClient"+ "Logged in as " + connection.getUser());
			
				//configurePacketListeners();
				
				//configureFileManager();
				
				
			}catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SmackException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return connection;
	}
	//AbstractXMPPConnection con
	
	public void deleteConnection(){
		
		System.out.println("Delete Connection");
		if(connection.isConnected())
			connection.disconnect();
	}
	
	/*private void configureProviderManager(ProviderManager pm) {
		
	}
	
	private void configurePacketListeners(){
		
	} 
	
	
	private void configureFileManager(){
		FileTransferManager manager = new FileTransferManager(getConnection());    
	}*/
}
