//Copyright (C) 2011-2012 Chris Price (xCP23x)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.xcp23x.restockit;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

class RIcont {
    //Class for RestockIt containers (any references to cont mean container)
    
    private Block cont;
    private InventoryHolder ivh;
    private Material item;
    private int maxItems,fillRate,x,y,z,damage;
    
    public RIcont(Block block){
        //Make sure it's a chest or dispenser
        if(block != null && (block.getType() == Material.CHEST || block.getType() == Material.DISPENSER)){
            //Set things we can set at the moment
            cont = block;
            ivh = (InventoryHolder)cont.getState();
            x = cont.getX(); y = cont.getY(); z = cont.getZ();
            if(isRIcont()){
                //Set things related to restockit conts
                maxItems = (getMaxItems() == 0) ? null : getMaxItems();
                fillRate = (getFillRate() == 0) ? null : getFillRate();
                item = getItem();
            }
            
            
        } else RestockIt.debug("Null or non-container block given to RIcont");
    }
    
    public Boolean isRIcont(){
        if(cont == null) return false;
        if(cont.getType() == Material.CHEST || cont.getType() == Material.DISPENSER){
            RIsign sign = getRIsign();
            //If it's a RestockIt sign, it must be a RI cont (if it's not, it could be a saved cont)
            if(sign != null && sign.isRIsign()) return true;
            
            //TODO: Code for loading data from saved chests
            
            
            
        }
        return false;
    }
    
    public int getMaxItems(){
        //Get max items from sign or from saved data
        return 0;
    }
    
    public void setMaxItems(int items){
        //Set max items to sign or saved data
    }
    
    public int getFillRate(){
        //Get fill rate from sign or from saved data
        return 0;
    }
    
    public void setFillRate(int rate){
        //Set fill rate to sign or saved data
    }
    
    public Material getItem(){
        //Get item from sign or from saved data
        return null;
    }
    
    public void setItem(Material mat){
        //Set item to sign or saved data
    }
    
    public int getDamage(){
        //Get damage value from sign or from saved data
        return 0;
    }
    
    public RIsign getRIsign(){
        //Check above cont
        Block block = cont.getWorld().getBlockAt(cont.getX(), cont.getY() +1, cont.getZ());
        RIsign sign = new RIsign(block);
        if(sign.isRIsign()) return sign;
        
        //Check below cont
        block = cont.getWorld().getBlockAt(cont.getX(), cont.getY() -1, cont.getZ());
        sign = new RIsign(block);
        if(sign.isRIsign()) return sign;
        
        return null;
    }
    
    public Block getDoubleChest(){
        if(cont.getType() != Material.CHEST) return null;
        int x = cont.getX(), y = cont.getY(), z = cont.getZ();
        World world = cont.getWorld();
        if(world.getBlockAt(x+1, y, z).getType() == Material.CHEST) return world.getBlockAt(x+1, y, z);
        if(world.getBlockAt(x-1, y, z).getType() == Material.CHEST) return world.getBlockAt(x-1, y, z);
        if(world.getBlockAt(x, y, z+1).getType() == Material.CHEST) return world.getBlockAt(x, y, z+1);
        if(world.getBlockAt(x, y, z-1).getType() == Material.CHEST) return world.getBlockAt(x, y, z-1);
        return null;
    }
    
    public Boolean isDoubleChest(){
        return (getDoubleChest() != null);
    }
    
    public int getCurrentCount(Material item){
        int items = 0;
        Inventory inv = ivh.getInventory();
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

}
