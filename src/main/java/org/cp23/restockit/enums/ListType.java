//Copyright (C) 2014 Dr Daniel Naylor (dualspiral)
//Original RestockIt Copyright (c) 2011-2014 Chris Price (xCP23x)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.cp23.restockit.enums;

/**
 * Enumeration that defines each config list type.
 */
public enum ListType {

    /**
     * The Blacklist.
     */
    BLACKLIST("blacklist"),
    
    /**
     * The single chest list.
     */
    SINGLE("singleContainers list"),
    
    /**
     * The double chest list.
     */
    DOUBLE("doubleContainers list"),
    
    /**
     * The dispenser list.
     */
    DISPENSERS("dispensers list");
    
    private final String listName;
   
    /**
     * Constructs the list.
     * @param listName The name of the list.
     */
    private ListType(String listName) {
        this.listName = listName;
    }
    
    /**
     * Gets the name of the list.
     * @return The name of the list.
     */
    @Override
    public final String toString() {
        return listName;
    }
}
