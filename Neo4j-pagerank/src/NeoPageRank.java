import java.util.ArrayList;

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

	@Name("NeoPageRank")
	public Iterable<Node> PageRank(@Source GraphDatabaseService db) {
		
		
		
		
		
		
		return null;
		
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
