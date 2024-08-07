//Copyright (C) 2011-2014 Chris Price (xCP23x)
//This software uses the GNU GPL v2 license
//See http://github.com/xCP23x/RestockIt/blob/master/README and http://github.com/xCP23x/RestockIt/blob/master/LICENSE for details

package org.cp23.restockit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class SignUtils {
	public static final String badDamage = "BadDamage";
	public static final String notMaterial = "NotMaterial";
	public static final String incinerator = "Incinerator";

	public static Material getMaterial(String line) {
		Material result = Material.AIR;

		if (getType(line) != badDamage || getType(line) != notMaterial || getType(line) != incinerator) {
			result = Material.getMaterial(getType(line));
		}

		// Return material from line, else AIR
		return result;
	}

	public static short getDamage(String line) {
		// Return damage from line, else 0
		String dmgStr = (line.contains(":") ? line : line + ":0").split(":")[1];

		return Short.parseShort(dmgStr);
	}

	public static String getType(String line) {

		// Returns:
		// 0 if it's an incinerator
		// -1 if it's and int, but not an ID
		// -2 if it's not an int or an ID
		// -3 if there's a bad damage value
		// Int ID of item if it's valid

		if (line.equalsIgnoreCase("Incinerator")) {
			return incinerator;
		}
		Material t;
		String sa[] = (line.indexOf(":") == 1 ? line : line + ":0").split(":");

		if ((t = Material.getMaterial(sa[0].toUpperCase())) == null) {
			try {
				if ((t = Material.getMaterial(sa[0])) == null) {
					return notMaterial; // it's an int, but not an valid Material ID.
				}
			} catch (NumberFormatException ex) {
				return badDamage; // it's not an int or ID.
			}
		}

		try {
			Short.parseShort(sa[1]);
		} catch (NumberFormatException ex) {
			return null; // problem with damage value
		}

		return t.toString();
	}

	// Currently Unused
	public static void setMaxItems(int items, Sign sign) {
		String part = sign.getSide(Side.FRONT).getLine(3).split(",")[1];
		sign.getSide(Side.FRONT).setLine(3, items + "," + part);
		sign.update();
	}

	public static int getMaxItems(String line, Material mat) {
		String str = line.contains(",") ? line.split(",")[0].toLowerCase().replaceAll(" ", "") : null; // Split the
																										// line, else
																										// give null
		if (str == null)
			return 0;

		try {
			if (str.contains("i") || str.contains("items") || str.contains("item")) { // If they've specified a number
																						// of items
				return Integer.parseInt(str.replaceAll("items", "").replaceAll("i", "").replaceAll("item", ""));
			}

			if (str.contains("s") || str.contains("stacks") || str.contains("stack")) {// If they've specified a number
																						// of stacks
				return Integer.parseInt(str.replaceAll("stack", "").replaceAll("s", "")) * mat.getMaxStackSize();
			}

			// Assume they mean they want x amount of items
			return Integer.parseInt(str);
		} catch (NumberFormatException ex) {
		} // Catch anything that may have gone wrong

		return 0;
	}

	// Currently Unused
	public static void setPeriod(float period, Sign sign) {
		String part1 = sign.getSide(Side.FRONT).getLine(3).split(",")[0];
		String part2 = period % 20 == 0 ? period / 20 + "s" : period + "t";
		sign.getSide(Side.FRONT).setLine(3, part1 + ", " + part2);
		sign.update();
	}

	public static int getPeriod(String line) {
		// If they specify seconds, multiply by 20, if not, give the raw value
		try {
			String periodStr = line.contains(",") ? line.split(",")[1].replaceAll(" ", "") : "0";
			int returnInt = periodStr.contains("s") ? (int) Double.parseDouble(periodStr.replaceAll("s", "")) * 20
					: Integer.parseInt(periodStr);
			if (returnInt > 0)
				return returnInt;
		} catch (NumberFormatException ex) {
		}
		return 0;
	}

	public static boolean isRIsign(String line) {
		// Check if line 1 says it's a RestockIt sign.
		String str = line.toLowerCase();
		return (str.contains("restockit") || str.contains("restock it") || str.contains("full chest")
				|| str.contains("full dispenser"));
	}

	public static void dropSign(Sign sign) {
		// Remove the sign and drop one at its location
		Location loc = sign.getWorld().getBlockAt(sign.getX(), sign.getY(), sign.getZ()).getLocation();
		loc.getBlock().setType(Material.AIR);
		
		sign.getWorld().dropItem(loc, new ItemStack(Material.OAK_SIGN, 1));
	}

	public static boolean isDelayedSign(String line, Material mat) {
		return getMaxItems(line, mat) != 0 && getPeriod(line) != 0;
	}

	public static boolean isIncinerator(String line) {
		// Check if it's an incinerator
		return line.toLowerCase().contains("incinerator");
	}

	public static Sign getSignFromCont(Block cont) {
		// Fairly simple, does what it says
		if (ContUtils.isSignAboveCont(cont))
			return (Sign)getSignAboveCont(cont).getState();
		if (ContUtils.isSignBelowCont(cont))
			return (Sign)getSignBelowCont(cont).getState();
		return null;
	}

	public static Block getSignAboveCont(Block cont) {
		return cont.getWorld().getBlockAt(cont.getX(), cont.getY() + 1, cont.getZ());
	}

	public static Block getSignBelowCont(Block cont) {
		return cont.getWorld().getBlockAt(cont.getX(), cont.getY() - 1, cont.getZ());
	}

	public static boolean line2hasErrors(String line, Player player) {

		// A frontend for getType(), this tells the player what's going on
		switch (getType(line)) {
		case badDamage:
			PlayerUtils.sendPlayerMessage(player, 5, line.split(":")[1]);
			return true;
		case notMaterial:
			PlayerUtils.sendPlayerMessage(player, 4, line.contains(":") ? line.split(":")[0] : line);
			return true;
		case incinerator:
			PlayerUtils.sendPlayerMessage(player, 3, line.contains(":") ? line.split(":")[0] : line);
			return true;
		}
		return false;
	}
}
