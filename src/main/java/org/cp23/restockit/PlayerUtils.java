//Copyright (C) 2011-2014 Chris Price (xCP23x)
//Portions Copyright (C) 2014 Dr Daniel Naylor (dualspiral)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.cp23.restockit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cp23.restockit.permissionmanager.RIPermissionManager;

/**
 * A collection of utilities to assist in player tasks.
 */
final class PlayerUtils extends RestockIt {
    //Colours for use in player messages:
    private static final ChatColor bCol = ChatColor.DARK_AQUA; //Bracket colour
    private static final ChatColor rCol = ChatColor.GRAY; // RestockIt colour
    private static final ChatColor eCol = ChatColor.DARK_RED; //Error colour
    private static final ChatColor dCol = ChatColor.RED;//Description colour
    private static final ChatColor sCol = ChatColor.GOLD; //String colour
    
    /**
     * Sends the player a message with an error code.
     * @param player The player causing the error.
     * @param number The error code.
     */
    public static void sendPlayerMessage(Player player, int number) {
        sendPlayerMessage(player, number, "");
    }
    
    /**
     * Sends the player a message with an error code.
     * @param player The player causing the error.
     * @param number The error code.
     * @param string A string containing additional data.
     */
    public static void sendPlayerMessage(Player player, int number, String string) {
        RestockIt.debug(player.getName() + " sent error message " + number + " with data: " + string);
        
        String colouredString = String.format("%s%s", sCol, string);
        
        //Give the error code here, so we only have to do it once
        player.sendMessage(String.format("%s[%sRestockIt%s]%s Error %s:", bCol, rCol, bCol, eCol, number));
        switch(number){
            case 1:
                player.sendMessage(dCol + "This is already a RestockIt container");
                break;
            case 2:
                player.sendMessage(dCol + "You do not have permission to create a RestockIt " + colouredString);
                break;
            case 3:
                player.sendMessage(colouredString + dCol + " is not a valid item ID");
                break;
            case 4:
                player.sendMessage(colouredString + dCol + " is not a valid item name");
                break;
            case 5:
                player.sendMessage(colouredString + dCol + " is not a valid damage value");
                break;
            case 6:
                player.sendMessage(dCol + "No chest was found below or above the sign");
                break;
            case 7:
                player.sendMessage(dCol + "You do not have permission to use " + colouredString);
                break;
            case 8:
                player.sendMessage(dCol + "You do not have permission to open a RestockIt " + colouredString);
                break;
            case 9:
                player.sendMessage(dCol + "You do not have permission to destroy a RestockIt " + colouredString);
                break;
            default:
                player.sendMessage(dCol + "Unspecified Error");
                break;
        }
    }
    
    /**
     * Checks if a player has the specified permission.
     * @param riperm The @link {RIPerm} to check.
     * @return <code>true</code> if the player has the permission.
     */
    public static boolean hasPermissions(RIperm riperm){
        String perm = riperm.getPerm();
        String depperm = riperm.getDeprecatedPerm();
        debug("Permission: " + perm);
        debug("Deprecated Permission: " + depperm);
        Player player = riperm.getPlayer();
        
        // Use our Permission Manager to handle the permission check.
        return RIPermissionManager.getPermissionManager().hasPermission(player, perm, depperm);
    }
    
    /**
     * Empty private constructor to prevent instantiation of the class.
     */
    private PlayerUtils() {}
}
