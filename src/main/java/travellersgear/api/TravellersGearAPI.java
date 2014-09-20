package travellersgear.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TravellersGearAPI
{
	/**
	 * @return The NBTTagCompound under which all TRPG data is saved
	 */
	public static NBTTagCompound getTravellersNBTData(EntityPlayer player)
	{
		if(!player.getEntityData().hasKey("TravellersRPG"))
			player.getEntityData().setTag("TravellersRPG", new NBTTagCompound());
		return player.getEntityData().getCompoundTag("TravellersRPG");
	}

	/*
	 * ====== INVENTORY ======
	 */

	/**@param player The targeted player
	 * @return The TRPG Extended inventory, consisting of Cloak(0), Shoulders(1), Vambraces(2), TitleScroll(3
	 */
	public static ItemStack[] getExtendedInventory(EntityPlayer player)
	{
		ItemStack[] ret = new ItemStack[4];
		NBTTagList inv = getTravellersNBTData(player).getTagList("Inventory", 10);
		if(inv!=null)
		{
			for (int i=0; i<inv.tagCount(); i++)
			{
				NBTTagCompound nbttagcompound = inv.getCompoundTagAt(i);
				int slot = nbttagcompound.getByte("Slot") & 0xFF;
				ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);
				if (itemstack != null && slot<ret.length)
					ret[slot] = itemstack;
			}
		}
		return ret;
	}
	/**@param player The targeted player
	 * @param inv The TRPG Extended inventory, consisting of Cloak(0), Shoulders(1), Vambraces(2), TitleScroll(3
	 */
	public static void setExtendedInventory(EntityPlayer player, ItemStack[] inv)
	{
		if(player==null||inv==null)
			return;

		NBTTagList list = new NBTTagList();
		for (int i=0; i<inv.length; i++)
			if(inv[i]!=null)
			{
				NBTTagCompound invSlot = new NBTTagCompound();
				invSlot.setByte("Slot", (byte)i);
				inv[i].writeToNBT(invSlot);
				list.appendTag(invSlot);
			}
		getTravellersNBTData(player).setTag("Inventory", list);
	}

	/**@param player The targeted player
	 * @return The tile currently equipped on the player
	 */
	public static String getTitleForPlayer(EntityPlayer player)
	{
		ItemStack scroll = getExtendedInventory(player)[3];
		if(scroll!=null)
		{
			if(scroll.hasTagCompound() && scroll.getTagCompound().hasKey("title"))
				return scroll.getTagCompound().getString("title");
			if(scroll.hasDisplayName())
				return scroll.getDisplayName();
		}
		return null;
	}


}