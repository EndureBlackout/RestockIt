//Copyright (C) 2011-2014 Chris Price (xCP23x)
//Portions Copyright (C) 2014 Dr Daniel Naylor (dualspiral)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.cp23.restockit;

import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

final class Scheduler {
    
    private static final HashMap<Block, Integer> schedules = new HashMap<Block, Integer>(); //This stores task IDs (int) with the blocks they are running on
    
    /**
     * Starts a schedule for the chosen block.
     * @param block The block to operate a schedule around.
     * @param period The period of the schedules.
     */
    public static void startSchedule(final Block block, int period) {

        //It's not running, start it
        if (schedules.get(block) == null || schedules.get(block) == 0) { 
            RestockIt.debugSched("Schedule started for block at " + getCoords(block));
            
            // Schedule the task.
            int taskID = RestockIt.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
                    RestockIt.plugin, 
                    new ScheduleRunner(block),
                    0,
                    period);
            
            if (taskID == -1) {
                RestockIt.debug("Schedule FAILED to start for block at " + getCoords(block));
                return;
            } 
            
            // Add the task to the scheduler.
            schedules.put(block, taskID);

        } else {
            RestockIt.debugSched("Tried to start schedule for block at " + getCoords(block) + ", but it's already running");
        }
    }
    
    /**
     * Stops a schedule for a particular block
     * @param block The block to stop the schedule for.
     */
    public static void stopSchedule(Block block) {
        if(schedules.containsKey(block)){
            RestockIt.debugSched("Removing schedule for block at " + getCoords(block));
            int i = schedules.get(block); //Get bukkit task ID
            if (i != 0) {  //If it's 0, it's not running
                RestockIt.plugin.getServer().getScheduler().cancelTask(i); //Cancel task
                schedules.remove(block); //Remove entry from HashMap
            }
        } else {
            RestockIt.debugSched("Tried to remove schedule for block at " + getCoords(block)+ ", but it doesn't exist");
        }
    }
    
    /**
     * Gets a string representation of the co-ords of a block.
     * @param block The block.
     * @return The representation.
     */
    private static String getCoords(Block block){
        return block.getX() + "," + block.getY() + "," + block.getZ();
    }
    
    /**
     * Private helper class that holds the runnable.
     */
    private static class ScheduleRunner implements Runnable {
        
        private final Block block;
        
        public ScheduleRunner(Block block) {
            this.block = block;
        }
        
        /**
         * Runs the scheduled task.
         */
        @Override
        public void run() {
            //If no sign is there, stop (the player may have removed it mid-count)
            if (!block.getType().toString().toLowerCase().contains("sign")) {
                Scheduler.stopSchedule(block);
                RestockIt.debugSched("Sign removed, cancelling schedule at " + getCoords(block));
                return;
            }

            // We know the object is a sign.
            Sign sign = (Sign)block.getState();
            Block cont = ContUtils.getContFromSign(sign);
            String line2 = sign.getSide(Side.FRONT).getLine(2);
            String line3 = sign.getSide(Side.FRONT).getLine(3);

            if(ContUtils.getCurrentItems(SignUtils.getMaterial(line2), cont) >= SignUtils.getMaxItems(line3, SignUtils.getMaterial(line2))) {
                RestockIt.debugSched("Container is full at " + getCoords(block));
                //The chest has reached its limit
                return;
            }

            //Add item to chest
            ItemStack stack = new ItemStack(SignUtils.getMaterial(line2), 1);
            
            // If the damage parameter is set
            Short damage = SignUtils.getDamage(line2);
            if (damage >= 0 && stack instanceof Damageable) {
                // Set the damage of the item.
                ((Damageable)stack).setDamage(damage);
            }
            
            // Add item
            ContUtils.getInventory(cont).addItem(stack);
            RestockIt.debugSched("Added " + SignUtils.getMaterial(line2).toString() + " to block at " + getCoords(block));
        }
        
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Scheduler() {}
}
