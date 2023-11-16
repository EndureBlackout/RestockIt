//Copyright (C) 2011-2014 Chris Price (xCP23x)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.cp23.restockit;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.cp23.restockit.enums.ListType;

class ContUtils {
    
    public static int getCurrentItems(Material item, Block cont) { //How many of item items are in container cont?
        int items = 0;
        Inventory inv = getInventory(cont);
        int stacks = inv.getSize(); //How many stacks?
        for(int x=0; x<stacks; x++) {  //Check each stack
            ItemStack stack = inv.getItem(x);
            if(stack == null) continue; //Continue to next stack
            if(stack.getType() == item) {
                items = items + stack.getAmount();
            }
        }
        return items;
    }
    
    public static Inventory getInventory(Block container){ //Returns the inventory of the InventoryHolder provided
        return ((InventoryHolder)container.getState()).getInventory(); 
    }
    
    public static boolean isSignAboveCont(Block cont) {
        if(RestockIt.isContainer(cont.getType())) {  //Is it a container?
            Block sign = SignUtils.getSignAboveCont(cont);
            if(sign.getType().toString().toLowerCase().contains("sign")) { //Is there a sign above?
                String line = ((Sign) sign.getState()).getSide(Side.FRONT).getLine(1);
                if(SignUtils.isRIsign(line)) return true;  //Is it a RestockIt sign?
            }
        }
        return false;
    }
    
    public static boolean isSignBelowCont(Block cont) {
        if(RestockIt.isContainer(cont.getType())) {
            Block sign = SignUtils.getSignBelowCont(cont);
            if(sign.getType().toString().toLowerCase().contains("sign")) { //Is there a sign below?
                String line = ((Sign) sign.getState()).getSide(Side.FRONT).getLine(1);
                if(SignUtils.isRIsign(line)) return true;  //Is it a RestockIt sign?
            }
        }
        return false;
    }
    
    public static boolean isRICont(Block cont) {
        return (isSignBelowCont(cont) || isSignAboveCont(cont));
    }
    
    public static boolean isAlreadyRICont(Sign sign) {
        //Check below the sign
        Block block = sign.getWorld().getBlockAt(sign.getX(), sign.getY()-1, sign.getZ());
        int x = sign.getX(), y = sign.getY(), z = sign.getZ();
        Block posSign = block.getWorld().getBlockAt(x, y-2, z); //Possibly a sign
        if (RestockIt.isContainer(block.getType()) && (posSign.getType().toString().toLowerCase().contains("sign"))) {
            return (SignUtils.isRIsign(((Sign)posSign.getState()).getSide(Side.FRONT).getLine(1)));
        }
        
        //Check above the sign
        block = sign.getWorld().getBlockAt(sign.getX(), sign.getY()+1, sign.getZ());
        posSign = block.getWorld().getBlockAt(x, y+2, z); //Possibly a sign
        if (RestockIt.isContainer(block.getType()) && (posSign.getType().toString().toLowerCase().contains("sign"))) {
            return(SignUtils.isRIsign(((Sign)posSign.getState()).getSide(Side.FRONT).getLine(1)));
        }
        return false;
    }
    
    public static Block getContFromSign(Sign sign) {
        //Try chest below sign first
        Block cont = sign.getWorld().getBlockAt(sign.getX(), sign.getY() - 1, sign.getZ());
        if(RestockIt.isContainer(cont.getType())) return cont;
        
        //Then chest above sign
        cont = sign.getWorld().getBlockAt(sign.getX(), sign.getY() + 1, sign.getZ());
        if(RestockIt.isContainer(cont.getType())) return cont;
        
        return null;
    }
    
    public static Block getDoubleChest(Block cont){
        //Return chest if there is one next to the block given
        if(!RestockIt.isInList(cont.getType(), ListType.DOUBLE)) return null; //Only continue for containers that can be doubled
        int x = cont.getX(), y = cont.getY(), z = cont.getZ();
        World world = cont.getWorld();
        if(RestockIt.isInList(world.getBlockAt(x+1, y, z).getType(), ListType.DOUBLE)) return world.getBlockAt(x+1, y, z);
        if(RestockIt.isInList(world.getBlockAt(x-1, y, z).getType(), ListType.DOUBLE)) return world.getBlockAt(x-1, y, z);
        if(RestockIt.isInList(world.getBlockAt(x, y, z+1).getType(), ListType.DOUBLE)) return world.getBlockAt(x, y, z+1);
        if(RestockIt.isInList(world.getBlockAt(x, y, z-1).getType(), ListType.DOUBLE)) return world.getBlockAt(x, y, z-1);
        return null;
    }
    
    public static void fillCont(Block cont, String line){
        Material mat = SignUtils.getMaterial(line);
        Inventory inv = getInventory(cont);
        Short damage = SignUtils.getDamage(line);
        
        if (mat != Material.AIR) { //Trying to fill a ccontainer with air will crash the client (but not the server)
            Block dc = getDoubleChest(cont); //The other chest if it's a double chest
            
            int stackSize = mat.getMaxStackSize(); //Get stack size (snowball stack != stone)
            ItemStack stack = new ItemStack(mat, stackSize); //Make the ItemStack
            if(damage >= 0 && stack instanceof Damageable) {((Damageable)stack).setDamage(damage);} //Incorporate damage (remember, -1 = no damage value)
            int stacks = dc!= null ? inv.getSize() / 2 : inv.getSize();  //Get inventory size
            
            for(int x=0; x<stacks; x++) inv.setItem(x, stack);//Fill the chest
            
            if(getDoubleChest(cont)!=null) {
                
                if(ContUtils.isRICont(dc)){
                    //Prepare stuff for second chest if it's different
                    Sign sign = SignUtils.getSignFromCont(dc);
                    line = sign.getSide(Side.FRONT).getLine(2);
                    mat = SignUtils.getMaterial(line);
                    damage = SignUtils.getDamage(line);
                    stackSize = mat.getMaxStackSize();
                    stack = new ItemStack(mat, stackSize);
                    if(damage >= 0) ((Damageable)stack).setDamage(damage);
                }
                for(int x=stacks;x<stacks*2; x++) inv.setItem(x, stack); //Fill the other half
            }
            
        } else inv.clear(); //Clear inventory if air is requested (see, there WAS a reason for setting it as air)
    }
}
