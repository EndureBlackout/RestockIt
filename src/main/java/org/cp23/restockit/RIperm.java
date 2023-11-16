//Copyright (C) 2011-2014 Chris Price (xCP23x)
//Portions Copyright (C) 2014 Dr Daniel Naylor (dualspiral)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.cp23.restockit;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.cp23.restockit.enums.ListType;
import org.cp23.restockit.enums.PermissionEnum;

class RIperm{
    
    private PermissionEnum perm;
    private Block block=null;
    private String line=null;
    private Player player=null;

    /**
     * Constructs this permission object.
     * @param bl The block this represents.
     * @param pl The player this object represents.
     * @param ln The string that represents the action.
     * @param p The permission this object represents.
     */
    public RIperm(Block bl, Player pl, String ln, PermissionEnum p){
        block = bl;
        player = pl;
        line = ln;
        perm = p;
    }
    
    /**
     * Constructs this permission object.
     * @param bl The block this represents.
     * @param pl The player this object represents.
     * @param sgn The sign that contains the RestockIt arguments.
     * @param p The permission this object represents.
     */
    public RIperm(Block bl, Player pl, Sign sgn, PermissionEnum p){
        block = bl;
        player = pl;
        line = sgn.getSide(Side.FRONT).getLine(2);
        perm = p;
    }

    /**
     * Gets the permission this object represents.
     * @return The @link { PermissionEnum }
     */
    public PermissionEnum getPermission() {
        return perm;
    }
    
    /**
     * Sets the permission for this object.
     * @param p The @link{ PermissionEnum }. Cannot be null.
     */
    public void setPermission(PermissionEnum p) {
        if (p == null) {
            throw new NullPointerException();
        }
        
        perm = p;
    }
    
    /**
     * Gets the @link {Player} this object represents.
     * @return The player.
     */
    public Player getPlayer(){
        return player;
    }
    
    /**
     * Gets the permission this object represents.
     * @return A @link{ String } representation of the permission.
     */
    public String getPerm(){
        if (perm == PermissionEnum.BLACKLIST_BYPASS) {
            return String.format("restockit%s", perm.getPermissionSuffix());
        }
        
        return String.format("restockit.%s%s", getBlockType(), perm.getPermissionSuffix());
    }
    
    /**
     * Gets the deprecated permission.
     * @return The permission.
     */
    public String getDeprecatedPerm(){
        // There are no deprecated permissions for blacklist pypass.
        if (perm == PermissionEnum.BLACKLIST_BYPASS) {
            return null;
        }
        
        
        if(RestockIt.isInList(block.getType(), ListType.SINGLE) || RestockIt.isInList(block.getType(), ListType.DOUBLE)) {
            return String.format("restockit.chest%s", perm.getPermissionSuffix());
        }
        
        return getPerm();
    }
    
    /**
     * Gets the RestockIt name for a block.
     * @return The name of the block.
     */
    public String getBlockType(){
        if(line.equalsIgnoreCase("incinerator")) {
            return "incinerator";
        }
        
        if(RestockIt.isInList(block.getType(), ListType.DISPENSERS)) {
            return "dispenser";
        }
        
        if(RestockIt.isContainer(block.getType())) {
            return "container";
        }
        
        return block.getType().toString().toLowerCase();
    }
    
}
