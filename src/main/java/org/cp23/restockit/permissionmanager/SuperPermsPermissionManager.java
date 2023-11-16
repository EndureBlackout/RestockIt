//Copyright (C) 2014 Dr Daniel Naylor (dualspiral)
//Original RestockIt Copyright (c) 2011-2014 Chris Price (xCP23x)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.cp23.restockit.permissionmanager;

import org.bukkit.entity.Player;

/**
 * Class that implements @link { RIPermissionManager } to use Bukkit SuperPerms.
 */
class SuperPermsPermissionManager extends RIPermissionManager {
    
    /**
     * Gets whether the player has the defined permission.
     * @param player The @link { Player } to check.
     * @param permission The permission to check for.
     * @return <code>true</code> if the player has the requested permission.
     */    
    @Override
    protected boolean checkPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }
    
    @Override
    protected String getPermissionName() {
        return "SuperPerms";
    }
    
}
