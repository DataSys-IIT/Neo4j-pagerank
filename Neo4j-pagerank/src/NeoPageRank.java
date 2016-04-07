import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.Description;
import org.neo4j.server.plugins.Name;
import org.neo4j.server.plugins.ServerPlugin;
import org.neo4j.server.plugins.Source;
import org.neo4j.tooling.GlobalGraphOperations;
public class NeoPageRank extends ServerPlugin {

	public GraphDatabaseService db;
	
	
	@Name("NeoPageRank")
	public Iterable<Node> PageRank(@Source GraphDatabaseService db) {
		int itterations = 10; //hardcoded temporarily 
		
		this.db = db;
		//initialize nodelist
		Map<Node, Double> nodeWeightRankList = new HashMap<Node,Double>();
		ArrayList<Node> nodes = getNodes(db);
		for(Node n : nodes) {
			nodeWeightRankList.put(n, 1.0);
		}
		
		
		return null;
		
	}
	
	
	private double update(Node n) {
		
		return 0.0;
	}
	
	
	public void afterStep() {
		
		
	}
	
	public void collectDisapearingPotential(Node n) {
		
		
	}
	
	private ArrayList<Node> getNodes(GraphDatabaseService db) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		try(Transaction tx = db.beginTx()) {
			
			for(Node node : GlobalGraphOperations.at(db).getAllNodes()) {
				nodes.add(node);
			}
			tx.success();
		}
		return nodes;
	}
	
	private ArrayList<Relationship> getRelationships(GraphDatabaseService db) {
		ArrayList<Relationship> relations = new ArrayList<Relationship>();
		try(Transaction tx = db.beginTx()) {
			
			for(Relationship relation : GlobalGraphOperations.at(db).getAllRelationships()) {
				relations.add(relation);
			}
			tx.success();
		}
		
		return relations;
	}
	
	
}
