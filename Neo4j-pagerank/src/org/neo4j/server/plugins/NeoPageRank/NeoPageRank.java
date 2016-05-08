package org.neo4j.server.plugins.NeoPageRank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.server.plugins.Description;
import org.neo4j.server.plugins.Name;
import org.neo4j.server.plugins.Parameter;
import org.neo4j.server.plugins.PluginTarget;
import org.neo4j.server.plugins.ServerPlugin;
import org.neo4j.server.plugins.Source;
import org.neo4j.tooling.GlobalGraphOperations;
@Description("Pagerank for neo4j")
public class NeoPageRank extends ServerPlugin {

	public GraphDatabaseService db;
	
	
	@Name("pagerank")
	@Description("Pagerank implimentation for neo4j")
	@PluginTarget(GraphDatabaseService.class)
	public Iterable<Node> PageRank(@Source GraphDatabaseService db, 
			@Parameter(name = "itterations") Integer ittr,
			@Parameter(name = "damping/teleportation") Double dampingfactor
			) {		
		Transaction t =db.beginTx();
		this.db = db;
		//initialize nodelist
		Map<Node, Double> nodeWeightRankList;
		ArrayList<Node> nodes = getNodes(db);
		
		nodeWeightRankList = rank(nodes, dampingfactor, ittr);
		Iterable<Node> result =  getLabeledNodes(nodeWeightRankList);
		t.success();
		t.close();
		return result;
		
	}
	
	public Iterable<Node> getLabeledNodes(Map<Node, Double> list) {
		Iterable<Node> result = new ArrayList<Node>();
		for(Map.Entry<Node, Double> m : list.entrySet()) {
			Double d = m.getValue();
			RankLabel label = new RankLabel("rank", d);
			m.getKey().addLabel(label);
		}
		
		return result;
		
	}
		
	public Map<Node, Double> rank(ArrayList<Node> PR, double dampingfactor, int itterations) {
		
		double sinkPR;
		Map<Node, Double> newPR = new HashMap<Node, Double>();
		
		//initialize
		for(int x=0;x<PR.size();x++) {
			newPR.put(PR.get(x), (double) (1/PR.size()));
		}
		
		int counter=0;
		while(counter < itterations ) {
			sinkPR = 0;
			Iterator<Node> nodes = PR.iterator();
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
				newPR.put(currentNode, pagerank); //update pagerank
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
	
	
	
}
