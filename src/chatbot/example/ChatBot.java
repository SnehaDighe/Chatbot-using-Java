package chatbot.example;

public class ChatBot {

	public static String quote = null; //variable to get data from client.

	/**
	 * Main method call for chatbot.
	 * @param args
	 */
	public static void main(String[] args){
		
		new WebsocketServer().start(); //start the server.

	}
}