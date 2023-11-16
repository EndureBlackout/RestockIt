//Copyright (C) 2014 Dr Daniel Naylor (dualspiral)
//Original RestockIt Copyright (c) 2011-2014 Chris Price (xCP23x)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.cp23.restockit.permissionmanager;

import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.cp23.restockit.RestockIt;

/**
 * Abstract class that defines the contract for a class that hooks 
 * into a permission manager.
 */
public abstract class RIPermissionManager {
    
    /**
     * The permissions manager to use when determining permissions.
     */
    private static RIPermissionManager permissionsManager;
    
    /**
     * Gets the correct permissions manager.
     * @return The requested @link { RIPermissionManager }
     */
    public static RIPermissionManager getPermissionManager() {
        if (permissionsManager != null) {
            return RIPermissionManager.permissionsManager;
        }
        
        return RIPermissionManager.createPermissionsManager();
    }
    
    /**
     * Factory method for setting the @link { RIPermissionManager } singleton.
     * @return The selected @link { RIPermissionManager }.
     */
    public static RIPermissionManager createPermissionsManager() {
        
        // We currently support three methods for permissions,
        // * PermissionsEx        
        // * Vault
        // * SuperPerms
    	if (VaultPermissionManager.isAvailable()) {
            permissionsManager = new VaultPermissionManager();
        } else {
            permissionsManager = new SuperPermsPermissionManager();
        }
        
        return permissionsManager;
    }
    
    /**
     * Clears the permissions manager, forcing it to be re-registered on the 
     * next pass.
     */
    public static void clearPermissionsManager() {
        permissionsManager = null;
    }
    
    /**
     * Gets the permission name.
     * @return The permission.
     */
    protected abstract String getPermissionName();
    
    /**
     * Gets whether the player has the defined permission.
     * @param player The @link { Player } to check.
     * @param permission The permission to check for.
     * @return <code>true</code> if the player has the requested permission.
     */
    protected abstract boolean checkPermission(Player player, String permission);    
    
    /**
     * Gets whether the player has the specified permission or deprecated permission.
     * @param player The @link {Player} to check.
     * @param permission The permission they should have.
     * @param deprecatedPermission The deprecated permission that they could have.
     * @return <code>true</code> if the player has the rights for the specified permission, 
     *         <code>false</code> otherwise.
     */
    public final boolean hasPermission(Player player, String permission, String deprecatedPermission) {
        RestockIt.debug("Using " + getPermissionName());
        
        // Catching the Ops Override Blacklist scenario.
        if(RestockIt.plugin.getConfig().getBoolean("opsOverrideBlacklist") && player.isOp() && "restockit.blacklist.bypass".equals(permission)) {
            return true;
        }
        
        // Otherwise, hand over to our implementation for the normal permission.
        if (checkPermission(player, permission)) {
            return true;
        }
        
        // Throw it over to the deprecated permission.
        if (checkPermission(player, deprecatedPermission)) {
            // Warn the console that the permission is now deprecated.
            warnDepPermissions(deprecatedPermission, permission);
            return true;
        }
        
        // No permission
        return false;
    }
    
    private void warnDepPermissions(String depperm, String permission){
        RestockIt.plugin.getLogger().log(Level.WARNING, "Using deprecated permission: {0}", depperm);
        RestockIt.plugin.getLogger().log(Level.INFO, "[RestockIt] Use {0} instead", permission);
        RestockIt.plugin.getLogger().info("[RestockIt] See http://dev.bukkit.org/server-mods/restockit/ for more info");
    }
}
