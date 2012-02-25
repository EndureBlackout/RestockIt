//@author Chris Price (xCP23x)

package org.xcp23x.restockit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class listeners implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block chest = event.getClickedBlock();
            if(chest.getType() == Material.CHEST) {
                Block sign = signUtils.getSignFromChest(chest);
                String line2 = ((Sign)sign.getState()).getLine(2);
                String line3 = ((Sign)sign.getState()).getLine(3);
                eventTriggered(chest, line2, line3, sign);
            }
        }
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Block sign = event.getBlock();
        Player player = event.getPlayer();
        String[] lines = event.getLines();
        String line0 = lines[0], line1 = lines[1], line2 = lines[2], line3 = lines[3];
        
        if(signUtils.isRIsign(line0)) {
            event.setLine(3, line2);
            event.setLine(2, line1);
            event.setLine(1, line0);
            event.setLine(0, "");
        }
        
        if(signUtils.isRIsign(line1)){
            
            if(chestUtils.getChestFromSign(sign) == null){
                signUtils.dropSign(sign);
                playerUtils.sendPlayerMessage(player, 6);
                return;
            }
            
            if(chestUtils.isAlreadyRIchest(sign)) {
                playerUtils.sendPlayerMessage(player, 1);
                signUtils.dropSign(sign);
                return;
            }
            
            if(signUtils.isIncinerator(line2)) {
                eventTriggered(chestUtils.getChestFromSign(sign),line2,line3,sign);
                return;
            }
            
            if(signUtils.line2hasErrors(line2, player)) {
                playerUtils.sendPlayerMessage(player, 7);
                signUtils.dropSign(sign);
                return;
            }
            
            if(!playerUtils.hasPermissions(player, chestUtils.getChestFromSign(sign), sign)){
                signUtils.dropSign(sign);
                playerUtils.sendPlayerMessage(player, 2, chestUtils.getChestFromSign(sign).getType().name().toLowerCase());
                return;
            }
            eventTriggered(chestUtils.getChestFromSign(sign), line2, line3, sign);
        }
    }
    
    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        Block block = event.getBlock();
        if(block.getType() == Material.DISPENSER) {   //Make sure the dispensable dispensee was dispensed by a dispenser
            if(chestUtils.isRIchest(block)) {
                Block sign = signUtils.getSignFromChest(block);
                String line2 = ((Sign)sign.getState()).getLine(2);
                String line3 = ((Sign)sign.getState()).getLine(3);
                eventTriggered(block, line2, line3, sign);
            }
        }
    }
    
    private void eventTriggered(Block chest, String line2, String line3, Block sign){
        if(signUtils.isDelayedSign(line3)){
            scheduler.startSchedule(sign, signUtils.getPeriod(line3));
        } else chestUtils.fillChest(chest, line2);
    }
}
