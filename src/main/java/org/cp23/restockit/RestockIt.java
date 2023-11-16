//Copyright (C) 2011-2014 Chris Price (xCP23x)
//Portions Copyright (C) 2014 Dr Daniel Naylor (dualspiral)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.cp23.restockit;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.cp23.restockit.enums.ListType;
import org.cp23.restockit.permissionmanager.RIPermissionManager;

public class RestockIt extends JavaPlugin {
    
    public static RestockIt plugin;
    private static boolean debugEnabled = false;
    private static boolean schedDebugEnabled = false;
    
    private static Map<ListType, List<String>> lists;
    
    /**
     * Runs when the plugin enables.
     */
    @Override
    public void onEnable(){
        plugin = this;
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        
        //Prepare the config
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.loadConfig();
    }
    
    /**
     * Runs when the plugin disables.
     */
    @Override
    public void onDisable(){
        // Clear the lists in the enums.
        lists = null;
        
        // Clear the permissions manager.
        RIPermissionManager.clearPermissionsManager();
    }
    
    /**
     * Loads the config file.
     */
    public void loadConfig() {
        // Clear the lists in the enums.
        lists = new EnumMap<ListType, List<String>>(ListType.class);
        
        //Load config
        lists.put(ListType.BLACKLIST, RestockIt.plugin.getConfig().getStringList("blacklist"));
        lists.put(ListType.SINGLE, RestockIt.plugin.getConfig().getStringList("singleContainers"));
        lists.put(ListType.DOUBLE, RestockIt.plugin.getConfig().getStringList("doubleContainers"));
        lists.put(ListType.DISPENSERS, RestockIt.plugin.getConfig().getStringList("dispensers"));
        
        //Check config for errors (i.e. force any errors to be logged to console)
        isInList(Material.AIR, ListType.BLACKLIST);
        isInList(Material.AIR, ListType.SINGLE);
        isInList(Material.AIR, ListType.DOUBLE);
        isInList(Material.AIR, ListType.DISPENSERS);
        
        // Check for debugging.
        debugEnabled = this.getConfig().getBoolean("debugMessages");
        schedDebugEnabled = this.getConfig().getBoolean("MOARdebug");
        if(debugEnabled) {
            getLogger().info("Basic debug messages enabled");
        }
        
        if(schedDebugEnabled) {
            getLogger().info("Scheduler debug messages enabled");
        }
    }
    
    /**
     * Reloads the config file, and loads the new values in.
     */
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.loadConfig();
    }
    
    /**
     * Checks to see if a material is a container.
     * @param mat The material to check.
     * @return <code>true</code> if so. 
     */
    public static boolean isContainer(Material mat){
        return isInList(mat, ListType.DISPENSERS) || isInList(mat, ListType.SINGLE) || isInList(mat, ListType.DOUBLE);
    }
    
    /**
     * Gets whether the specified material is in the specified list.
     * @param mat The material to check.
     * @param type The list to check.
     * @return <code>true</code> if so.
     */
    public static boolean isInList(Material mat, ListType type){
        List<String> list = lists.get(type);
        
        // For each item in the list...
        for(String blItem : list) {
            // If we get an invalid item, warn the user.
            if(SignUtils.getType(blItem).equals(SignUtils.badDamage) || SignUtils.getType(blItem).equals(SignUtils.notMaterial) || SignUtils.getType(blItem).equals(SignUtils.incinerator)) {
                RestockIt.plugin.getLogger().warning(String.format("Error in %s: %s not recognised - Ignoring", type.toString(), blItem));
            } else if (mat.toString().equals(SignUtils.getType(blItem))) {
                // If we get an item in the list, then return.
                return true;
            }
        }
        return false;
    }
    
    public static void debug(String msg){
        if(debugEnabled == true){
            RestockIt.plugin.getLogger().log(Level.INFO, "[DEBUG]: {0}", msg);
        }
    }
    
    public static void debugSched(String msg){
        if(schedDebugEnabled == true){
            RestockIt.plugin.getLogger().log(Level.INFO, "[SCHEDULER-DEBUG]: {0}", msg);
        }
    }
}
