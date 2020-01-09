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


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.JPanel;

/**
 *
 * @author Badr Hamza
 */
public class NetworkTracer {
    
    private Network network;
    private final JPanel panel = new NetworkPanel() {
        {
            setBackground(Color.white);
        }
    };
    private Point[] positionsArray;
    
    public NetworkTracer() {
    }

    public JPanel getPanel() {
        return panel;
    }
    
    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
        
        panel.repaint();
        /* ... */
    }
    
    public void highLight(ArrayList<String> nodes) {
        ArrayList<Link> filteredList = new ArrayList(
                network.getLinks().stream().filter(new Predicate<Link>() {
                    @Override
                    public boolean test(Link t) {
                        boolean a = false;
                        for (int i = 0; i < nodes.size() - 1; i++) {
                            if ( (nodes.get(i).equals("\"" + t.getFromNode() + "\"") && 
                                    nodes.get(i+1).equals("\"" + t.getToNode() + "\""))  || 
                                    (nodes.get(i).equals("\"" + t.getToNode()+ "\"") && 
                                    nodes.get(i+1).equals("\"" + t.getFromNode()+ "\""))) return true;
                        }
                        return false;
                    }
                }).collect(Collectors.toList()));
        for ( Link link : filteredList ) {
            link.setHighlighted(true);
        }
        panel.repaint();
    }
    
    public void resetHighlight() {
        for ( Link link : network.getLinks()) {
            if (link.isHighlighted()) {
                link.setHighlighted(false);
            }
        }
        panel.repaint();
    }
    
    public void highLight(String link) {
        
    }
    
    private static void linkStyle(String linkType, Graphics2D g2d) {
        float[] dashingPattern1 = {2f, 2f};
        Stroke stroke;
        
        switch (linkType) {
            default:
                g2d.setColor(Color.gray);
                stroke = new BasicStroke(3f);
                break;
            case "Wifi":
                g2d.setColor(Color.gray);
                stroke = new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 4.0f, dashingPattern1, 4.0f);
                break;
            case "Serial":
                g2d.setColor(Color.blue);
                stroke = new BasicStroke(3f);
                break;
            case "Fiber":
                g2d.setColor(Color.orange);
                stroke = new BasicStroke(3f);
                break;
        }
        g2d.setStroke(stroke);
    }
    
    private static void nodeStyle(String nodeType, Graphics2D g2d) {
        switch (nodeType) {
            case "Terminal":
                g2d.setColor(Color.blue);
                break;
            case "Server":
                g2d.setColor(Color.green);
                break;
            case "Router":
                g2d.setColor(Color.MAGENTA);
                break;
        }
    }
    
    private class NetworkPanel extends JPanel {
        
        @Override
        protected void paintComponent(Graphics g) {
            update((Graphics2D) g);
        }
        
        private void update(Graphics2D g2d) {
            int X, Y;
            int padding = 60;
            int nodeRadius = 50;
            Stroke stroke;
            positionsArray = new Point[network.getSheetColumns().intValue() * network.getSheetRows().intValue()];
            for (int i = 0; i < network.getSheetColumns() * network.getSheetRows(); i++) {
                X = padding + (int) (((panel.getWidth() - 2*padding)/(network.getSheetColumns()-1))*(i%network.getSheetColumns()));
                Y = padding + (int) (((panel.getHeight()- 2*padding)/(network.getSheetRows()-1))*(i/network.getSheetColumns()));
                positionsArray[i] = new Point(X, Y);
            }
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
            ArrayList<Link> ls = network.getLinks();
            for (int i = 0; i < network.getLinks().size(); i++) {
                Line2D link = new Line2D.Float(positionsArray[network.getNode(ls.get(i).getFromNode()).getPosition().intValue()], 
                        positionsArray[network.getNode(ls.get(i).getToNode()).getPosition().intValue()]);
                
                if (ls.get(i).isHighlighted()) {
                    g2d.setColor(Color.red);
                    stroke = new BasicStroke(10f);
                    g2d.setStroke(stroke);
                } else {
                    linkStyle(ls.get(i).getType(), g2d);
                }
                g2d.draw(link);
            }
            
            g2d.setColor(Color.orange);
            ArrayList<Node> ns = network.getNodes();
            for (int i = 0; i < network.getNodes().size(); i++) {
                Ellipse2D node = new Ellipse2D.Float(positionsArray[ns.get(i).getPosition().intValue()].x-nodeRadius/2, 
                        positionsArray[ns.get(i).getPosition().intValue()].y-nodeRadius/2,
                        nodeRadius,nodeRadius);
                nodeStyle(ns.get(i).getType(), g2d);
                g2d.fill(node);
                g2d.setColor(Color.black);
                g2d.drawString(ns.get(i).getName(), positionsArray[ns.get(i).getPosition().intValue()].x - 5*ns.get(i).getName().length(),
                        positionsArray[ns.get(i).getPosition().intValue()].y - 30);
            }
        }
    }
    
    
}
