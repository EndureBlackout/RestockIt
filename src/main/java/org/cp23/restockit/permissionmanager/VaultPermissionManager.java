//Copyright (C) 2014 Dr Daniel Naylor (dualspiral)
//Original RestockIt Copyright (c) 2011-2014 Chris Price (xCP23x)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.cp23.restockit.permissionmanager;


import static org.bukkit.Bukkit.getServer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.milkbowl.vault.permission.Permission;

/**
 * Class that implements @link { RIPermissionManager } to use Vault.
 */
class VaultPermissionManager extends RIPermissionManager {
    
    /**
     * The Vault Permission Manager.
     */
    private final Permission permissionManager;

    /**
     * Constructs the VaultPermissionManager.
     */
    VaultPermissionManager() {
        permissionManager = 
                getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider();
    }
    
    /**
     * Determines whether Vault is available.
     * @returns <code>true</code> if Vault is on the server.
     */ 
    static boolean isAvailable() {
       return Bukkit.getServer().getPluginManager().isPluginEnabled("Vault") 
                && Bukkit.getServer().getPluginManager().getPlugin("Vault") != null;
    }
    
    
    /**
     * Gets whether the player has the defined permission.
     * @param player The @link { Player } to check.
     * @param permission The permission to check for.
     * @return <code>true</code> if the player has the requested permission.
     */
    @Override
    protected boolean checkPermission(Player player, String permission) {
        return permissionManager.has(player.getWorld(), player.getName(), permission);
    }
    
    @Override
    protected String getPermissionName() {
        return "Vault";
    }
}
