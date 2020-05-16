package chatbot.example;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.xml.sax.SAXException;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

public class WebsocketServer extends WebSocketServer {

	//tcp port and initialize connection variables.
	private static int TCP_PORT = 4444; 

	private Set<WebSocket> conns; 

	/**
	 * Constructor for WebsocketServer.
	 */
	public WebsocketServer() {
		super(new InetSocketAddress(TCP_PORT));
		conns = new HashSet<>();
	}
	
	/**
	 * Methods to handle open connection from the server.
	 */
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conns.add(conn);
		System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}
	
	/**
	 * Method to handle close connection from the server.
	 */
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		conns.remove(conn);
		System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}
	
	/**
	 * Method to send and receive message from client.
	 */
	@Override
	public void onMessage(WebSocket conn, String message) {
		try {
			System.out.println("Message from client: " + message);  
			
			//check message from client.
			if(message.compareTo("Ping") != 0) { //check if its a relevant messge.
				if(message.compareTo("showHistory") == 0){ //if clients wants to see history.
					for (WebSocket sock : conns) {
						sock.send(MongoDBOperations.historyObject); //get history from database and send to client.
					}
				}else {
					//if clients wants to do a fresh search.
					ChatBot.quote = message; //get client query
					LuceneClass.dataToClient = null;
					LuceneClass.luceneValues(); //get results for the query from xml file
					for (WebSocket sock : conns) {
						sock.send(LuceneClass.dataToClient); //send the results of search to client.
					}
				}
			}
		} catch (SAXException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to handle error in server side connections.
	 */
	@Override
	public void onError(WebSocket conn, Exception ex) {
		//ex.printStackTrace();
		if (conn != null) {
			conns.remove(conn);
			// do some thing if required
		}
		System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}
	
	/**
	 * Method to handle when the server connection has started.
	 */
	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}
}
