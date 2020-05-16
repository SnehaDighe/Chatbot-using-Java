package chatbot.example;

import java.io.File;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException; 

public class LuceneClass{

	private static GsonBuilder gsonBuilder = new GsonBuilder();//intitialize for gson conversion.
	private static Gson gson = gsonBuilder.create(); //gson variable.
	public static String filePath = "src/Test.xml"; //path to the xml file.
	public static File xmlFile = new File(filePath);//reading the file.
	static String dataToClient = null;//variable to store json for client.
	static int search_result_count = 20; //limit the search results to first 20 records.
	
	/**
	 * This method implements
	 * 1. Lucene indexing.
	 * 2. Gets the data by parsing test.xml file for the keyword entered at client side.
	 * 3. Stores the data related to search by converting it into json format.
	 * 4. Stores the data for the search in MongoDB for maintaining history.
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static void luceneValues() throws SAXException, ParserConfigurationException{
		try {
			// 0. Specify the analyzer for tokenizing text. The same analyzer should be used for indexing and searching
			StandardAnalyzer analyzer = new StandardAnalyzer();
			FSDirectory index = FSDirectory.open(Paths.get("/Users/sneha/eclipse-workspace/met-cs622-assignment-final-project-sneha-dighe/output"));//output directory for index
			IndexWriterConfig config = new IndexWriterConfig(analyzer);

			// 1. create the index
			IndexWriter w = new IndexWriter(index, config);

			//2. parsing xml data.
			org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);//parsing the file.
			NodeList article = doc.getElementsByTagName("PubmedArticle");//getting the details for Article tag name.
			NodeList title = doc.getElementsByTagName("Title");//getting the details for title tag name.

			//3. Adding data to lucene document.
			for (int i = 0; i < article.getLength(); ++i) {  //executing for loop to check for the required keyword.	
				if (article.item(i).getTextContent().contains(ChatBot.quote)) { //check if the keyword is present in the article.
					addDoc(w, ChatBot.quote, title.item(i).getTextContent()); //get the title containing keyword and add it to the document.
				}
			}


			w.close(); //close the writer.

			// 4. query
			String querystr = new String(ChatBot.quote); //query for the keyword entered.
			// the "title" arg specifies the default field to use
			// when no field is explicitly specified in the query.
			Query q = new QueryParser("keyword", analyzer).parse(querystr); //parse the keyword entered.

			// 5. search
			int hitsPerPage = 500000; //maximum hits per search allowed.
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs docs = searcher.search(q, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;


			ArrayList<String> mylist = new ArrayList<String>();//arraylist to add results for keyword search.

			// 6. display results
			System.out.println("Found " + hits.length + " hits.");
			for(int i=0; i<hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				System.out.println((i + 1) + ". title: " + d.get("data") + "\n keyword: " + d.get("keyword"));
				if(mylist.size() < search_result_count) {
					mylist.add(d.get("data")); //store data in arraylist.
					MongoDBOperations.Data.add(new BasicDBObject(d.get("keyword"), d.get("data"))); //store data in mongodb.
				}
			}

			//7. Arraylist conversion to json.
			Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
			String prettyJson = prettyGson.toJson(mylist);
			
			//8. Store and retrieve json data for client side.
			dataToClient = prettyJson;
			MongoDBOperations.mongoDBQueries();
			
			// reader can only be closed when there
			// is no need to access the documents any more.
			reader.close();

		} catch(IOException ex){
			ex.printStackTrace();
		} catch (ParseException ex2) {
			ex2.printStackTrace();
		}
		//return prettyJson;
	}

	/**
	 * Method for Adding keyword and data/title to document.
	 * @param w
	 * @param keyword
	 * @param data
	 * @throws IOException
	 */
	public static void addDoc(IndexWriter w, String keyword, String data) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("keyword", keyword, Field.Store.YES));
		doc.add(new StringField("data", data, Field.Store.YES));
		w.addDocument(doc);
	}


}
