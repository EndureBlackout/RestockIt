//Copyright (C) 2014 Dr Daniel Naylor (dualspiral)
//Original RestockIt Copyright (c) 2011-2014 Chris Price (xCP23x)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.cp23.restockit.enums;

/**
 * Enum that holds the various permissions suffixes.
 */
public enum PermissionEnum {
    /**
     * Permission suffix for creating a RestockIt container.
     */
    CREATED(".created"),
    
    /**
     * Permission suffix for opening a RestockIt container.
     */
    OPENED(".opened"),
    
    /**
     * Permission suffix for destroying a RestockIt container.
     */
    DESTROYED(".destroyed"),
    
    /**
     * Permission suffix for bypassing the blacklist.
     */
    BLACKLIST_BYPASS(".blacklist.bypass");
    
    private final String permissionSuffix;
    
    private PermissionEnum(String perm) {
        this.permissionSuffix = perm;
    }
    
    /**
     * Gets the permission suffix associated with the selected enumeration.
     * @return The suffix.
     */
    public String getPermissionSuffix() {
        return permissionSuffix;
    }
}
