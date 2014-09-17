import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JFrame;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import graph.Argument;
import graph.Role;

public class GraphReader {
	
	/////////////////////////////////
	// set this password
	private static String mongoPass = "";
	/////////////////////////////////
	
	
	private static String mongoAddr = "172.22.204.184";
	private static int mongoPort = 22222;
	private static String mongoDBName = "all";
	private static String collName = "srl";
	private static String mongoUser = "reader";
	
	
	
	
	public static TreeMap<String, List<DirectedSparseGraph<Argument, Role>>> readGraphsFromFile(String fullFilePath){
		
		File file = new File(fullFilePath);
		
		TreeMap<String, List<DirectedSparseGraph<Argument, Role>>> graphsMap = null;
		try{
			// don't use buffering
			InputStream fis = new FileInputStream(file);
			ObjectInput input = new ObjectInputStream(fis);//buffer);

			graphsMap = 
					(TreeMap<String, List<DirectedSparseGraph<Argument, Role>>>)
						input.readObject();					
		}catch(Exception e){
			System.out.println("could not deserialize annotations map: ");
			e.printStackTrace();
		}
		
		return graphsMap;
		
		
	}
	
	private static String makeLongKey(String graphFileName){		
		File file = new File(graphFileName);		
		System.out.println(file.getPath());
		String[] parts = file.getName().split("-");
		return "http://" + parts[0] + ".wikipedia.org/wiki?curid=" + parts[1].substring(0,parts[1].length()-6);
	}
	
	
	private static String getSentence(String docId, String sentenceId){
	
		
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient( mongoAddr , mongoPort );
		} catch (UnknownHostException e) {
			System.out.println("unknown host exception getting mongo db client");
			e.printStackTrace();
		}
		//			mongoClient.setWriteConcern(WriteConcern.JOURNALED);
		DB db = mongoClient.getDB(mongoDBName);
		boolean auth = db.authenticate(mongoUser, mongoPass.toCharArray());
		if( ! auth ){
			System.out.println("could not authenticate mongo db access");
		}else{

			DBCollection collSrl = db.getCollection(collName);
			
			BasicDBObject query = new BasicDBObject("_id", docId);	
			DBCursor dbResult = null;

			dbResult = collSrl.find(query).limit(1);

			if(dbResult.size() == 0){
				System.out.println("");
				return null;
			}else{
				
				BasicDBObject dbDoc = (BasicDBObject)dbResult.next();
				SAXBuilder builder = new SAXBuilder();
				String srlString = dbDoc.get("srlAnnot").toString();
				Document jdomDoc = null;
				try {
					jdomDoc = (Document) builder.build(new StringReader(srlString));
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				XPathFactory xpf = XPathFactory.instance();
				XPathExpression<Element> expr = xpf.compile("/item/sentences/sentence[@id='" + sentenceId + "']/text", Filters.element());
				Element sentText = expr.evaluateFirst(jdomDoc);
				return sentText.getText();
			}

			
		}

		
		return null;
		
	}
	
	public static void readAndVisualizeGraphs(String graphFilePath) {
		readGraphsFromFile(graphFilePath);
		String longKey = makeLongKey(graphFilePath);
		
		TreeMap<String, List<DirectedSparseGraph<Argument, Role>>> graphsMap = readGraphsFromFile(graphFilePath);
		for (String s : graphsMap.keySet()){
			List<DirectedSparseGraph<Argument, Role>> l = graphsMap.get(s);
			for (DirectedSparseGraph g : l){
				visualizeGraph(g, getSentence(longKey, s.split("-")[2]));
			}
		}
	}
	
	
	
	private static void visualizeGraph(Graph g, String sentence){
		Layout<Argument, Role> layout = new CircleLayout(g);
		layout.setSize(new Dimension(300,300));
		VisualizationViewer<Argument, Role> vv = new VisualizationViewer<Argument, Role>(layout);
		vv.setPreferredSize(new Dimension(700, 500));
		// Show vertex and edge labels
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

		// Create our "custom" mouse here. We start with a PluggableGraphMouse
		// Then add the plugins you desire.
		PluggableGraphMouse gm = new PluggableGraphMouse(); 
		gm.add(new TranslatingGraphMousePlugin(MouseEvent.BUTTON1_MASK));
		gm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1.1f, 0.9f));
		gm.add(new PickingGraphMousePlugin<Argument, Role>(MouseEvent.BUTTON1_MASK, MouseEvent.BUTTON2_MASK));

		vv.setGraphMouse(gm); 
		JFrame frame = new JFrame(sentence);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true); 
	}
}
