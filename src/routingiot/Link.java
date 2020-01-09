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

import java.awt.Color;

/**
 *
 * @author Badr Hamza
 */
public class Link {
    
    private String Name;
    private Long latence;
    private Long speed;
    private String type;
    private String fromNode;
    private String toNode;
    private boolean isHighlighted;
    
    public static Long convertSpeed(Long speed, String unit) {
        int factor;
        switch (unit) {
            case "B":
                factor = 1;
                break;
            case "K":
                factor = 1024;
                break;
            case "M":
                factor = 1024*1024;
                break;
            case "G":
                factor = 1024*1024*1024;
                break;
            case "T":
                factor = 1024*1024*1024*1024;
                break;
            default:
                factor = 1;
        }
        return factor * speed;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public Long getLatence() {
        return latence;
    }

    public void setLatence(Long latence) {
        this.latence = latence;
    }

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getFromNode() {
        return fromNode;
    }

    public void setFromNode(String fromNode) {
        this.fromNode = fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }

    
    
    
    
}
