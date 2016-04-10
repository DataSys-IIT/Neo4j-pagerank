import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.Direction;
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
		
		
		
		return null;
		
	}

	
	
	public Map<Node, Double> rank(ArrayList<Node> PR, int dampingfactor, int itterations) {
		
		boolean converged = false;
		double sinkPR;
		Map<Node, Double> newPR = new HashMap<Node, Double>();
		
		//initialize
		for(int x=0;x<PR.size();x++) {
			newPR.put(PR.get(x), (double) (1/PR.size()));
		}
		
		int counter=0;
		while(counter < itterations ) {
			sinkPR = 0;
			Iterator nodes = PR.iterator();
			//iterate over 'sink nodes' with no outlinks
			while(nodes.hasNext()) {
				Node currentnode = (Node) nodes.next();
				
				boolean issink = true;
				for(Relationship r : currentnode.getRelationships()) {
					if(r.getStartNode().equals(currentnode)) {
						issink = false;
					}
				}
				
				if(issink) {
					sinkPR += (Double) newPR.get(currentnode);
				}
			}
			
			ArrayList<Double> newprtmp = new ArrayList<Double>();
			nodes = PR.iterator();
			//iterate over all nodes
			while(nodes.hasNext()) {
				double pagerank;
				Node currentNode = (Node) nodes.next();
				pagerank = (1-dampingfactor)/PR.size();
				pagerank += dampingfactor * (sinkPR/PR.size());
				for(Relationship n : currentNode.getRelationships(Direction.INCOMING)) {
					Node startnode = n.getStartNode();
					pagerank += dampingfactor * newPR.get(startnode)/startnode.getDegree(Direction.OUTGOING);
				}
				newPR.replace(currentNode, pagerank); //update pagerank
			}
			
			counter++;
		}
		return newPR;
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
