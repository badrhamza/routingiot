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

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Badr Hamza
 */
public class JsonNetworkReader {
    
    private JsonNetworkReader() {
        
    }
    
    public static Network getNetwork(String fileName) {
        JSONParser parser = new JSONParser();

        Network network = new Network();
        try (Reader reader = new FileReader(fileName)) {

            JSONObject networkJson = (JSONObject) parser.parse(reader);
            // Creating nodes :
            network.setSheetColumns( (Long) ((JSONObject)networkJson.get("grid_definition")).get("columns_count") );
            network.setSheetRows( (Long) ((JSONObject)networkJson.get("grid_definition")).get("rows_count") );
            
            JSONObject jo;
            Iterator<JSONObject> nodeIterator = ((JSONArray)((JSONObject)networkJson.get("network_definition")).get("nodes")).iterator();
            while (nodeIterator.hasNext()) {
                Node node = new Node();
                jo = nodeIterator.next();
                node.setName((String)jo.get("name"));
                node.setPosition((Long)jo.get("position"));
                node.setProcessingLatence((Long)jo.get("processing_latence"));
                node.setType((String)jo.get("type"));
                node.setConstructor((String)jo.get("constructor"));
                
                network.addNode(node);
            }
            
            Iterator<JSONObject> linkIterator = ((JSONArray)((JSONObject)networkJson.get("network_definition")).get("links")).iterator();
            while (linkIterator.hasNext()) {
                Link link = new Link();
                jo = linkIterator.next();
                //link.setName((String)jo.get("name"));
                link.setSpeed(Link.convertSpeed((Long)jo.get("speed"), (String)jo.get("unit")));
                link.setLatence((Long)jo.get("latence"));
                link.setType((String)jo.get("type"));
                link.setFromNode((String)jo.get("from"));
                link.setToNode((String)jo.get("to"));
                
                network.addLink(link);
            }
            
        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        
        return network;
    }
}
