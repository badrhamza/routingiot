/*
 * Copyright (C) 2020 Badr Hamza
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package routingiot;

import java.util.ArrayList;
import java.util.HashMap;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import static org.neo4j.driver.v1.Values.parameters;

/**
 *
 * @author Badr Hamza
 */
public class Network implements AutoCloseable {
    
    private final Driver driver;

    @Override
    public void close() throws Exception {
        
    }
    
    private Long sheetColumns;
    private Long sheetRows;
    
    private final HashMap<String, Node> nodes = new HashMap();
    private final ArrayList<Link> links = new ArrayList();
    
    public class EncpsInt {
        public int i = 0;
    }
    
    public Network() {
        driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic("neo4j", "badr") );
        // MATCH (n) DETACH DELETE n
        try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<String>() {
                @Override
                public String execute( Transaction tx )
                {
                    tx.run("MATCH (n) DETACH DELETE n");
                    return "";
                }
            } );
            
        }
    }
    public enum criterias {
        BY_LATENCE, BY_SPEED
    }
    
    public ArrayList<String> getPath(String fromNode, String toNode) {
        // ici on met notre requête sur le base de données Neo4J, et l'on retourne
        // une liste des chemins approuvés
        final String graphRequest;
        
        graphRequest = "MATCH (start{Name:$FromNode}), (end{Name:$ToNode}) " +
                            "WHERE (start:Terminal OR start:Server) AND (end:Terminal OR end:Server) " +
                            "CALL algo.shortestPath.stream(start, end, 'Latence') " +
                            "YIELD nodeId, cost RETURN algo.asNode(nodeId).Name AS Name, cost AS Latence";
        
        ArrayList<String> pathNode = new ArrayList<>();
        final Integer lc;
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run(graphRequest,
                            parameters( "FromNode", fromNode, "ToNode", toNode ) );
                    for (Record m : result.list()) {   //.single().get( 0 ).asString();
                        pathNode.add( m.get("Name").toString() );
                        //lct += m.get("Latence").asInt();
                    }
                    //lc = 3;
                    return "";
                }
            } );
        }
        return pathNode;
    }
    
    public Long getSheetColumns() {
        return sheetColumns;
    }

    public void setSheetColumns(Long sheetColumns) {
        this.sheetColumns = sheetColumns;
    }

    public Long getSheetRows() {
        return sheetRows;
    }

    public void setSheetRows(Long sheetRows) {
        this.sheetRows = sheetRows;
    }

    public ArrayList<Node> getNodes() {
        final ArrayList<Node> n = new ArrayList<>();
        n.addAll(nodes.values());
        return n;
    }

    public ArrayList<Link> getLinks() {
        final ArrayList<Link> l = new ArrayList<>();
        l.addAll(links);
        return l;
    }
    
    public Node getNode(String name) {
        return nodes.get(name);
    }
    
    public void addNode(Node node) {
        // MATCH (n) DETACH DELETE n
        try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<String>() {
                @Override
                public String execute( Transaction tx )
                {
                    tx.run( 
                            "CREATE (n:" + node.getType() + "{Name: $Name, Constructor: $Constructor, Latence: $Latence})",
                            parameters( "Name", node.getName(), 
                                    "Constructor", node.getConstructor(), 
                                    "Latence", node.getProcessingLatence())
                    );
                    return "";
                }
            } );
            
        }
        nodes.put(node.getName(), node);
    }
    
    public void addLink(Link link) {
        try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<String>() {
                @Override
                public String execute( Transaction tx )
                {
                    tx.run( 
                            "MATCH (a), (b) WHERE (a:Router OR a:Terminal OR a:Server) AND (b:Router OR b:Terminal OR b:Server) AND "
                                    + "a.Name= $FromName AND b.Name = $ToName "
                                    + "CREATE (a)-[:"+ link.getType() +" {Latence:$Latence, Speed:$Speed}]->(b) ",
                            parameters(
                                    "FromName", link.getFromNode(), 
                                    "ToName", link.getToNode(),
                                    "Latence", link.getLatence(),
                                    "Speed", link.getSpeed()) 
                    );
                    return "";
                }
            } );
            
        }
        links.add(link);
    }
    
}
