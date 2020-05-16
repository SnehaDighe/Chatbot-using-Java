package chatbot.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;


public class MongoDBOperations {

	private static Mongo mongo = new Mongo("localhost", 27017);//mongodb local host.
	private static DB db = mongo.getDB("test");//database name is declared here.
	static List<BasicDBObject> Data = new ArrayList<>();//array list to store data in mongodb.
	private static GsonBuilder gsonBuilderHistory = new GsonBuilder();//initialize gson.
	private static Gson gson_history = gsonBuilderHistory.create();//gson variable.
	static String historyObject;//variable to maintain history.
	
	/**
	 * This method gets the data from MongoDB and converts it in to json.
	 */
	public static void mongoDBQueries() {
		gson_history = new GsonBuilder().setPrettyPrinting().create();
		historyObject = gson_history.toJson(Data);
	}
}
