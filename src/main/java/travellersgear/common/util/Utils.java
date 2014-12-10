package travellersgear.common.util;

import java.lang.reflect.Method;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import travellersgear.api.ITravellersGear;
import travellersgear.client.ClientProxy;

public class Utils
{

	public static void tickTravGear(EntityPlayer player, ItemStack stack)
	{
		if(stack.getItem() instanceof ITravellersGear)
			((ITravellersGear)stack.getItem()).onTravelGearTick(player, stack);
		else if(ModCompatability.getPseudoTravellersGearData(stack)!=null && ModCompatability.getPseudoTravellersGearData(stack).length>=4)
			if(ModCompatability.getPseudoTravellersGearData(stack)[1]!=null)
				try{
					((Method)ModCompatability.getPseudoTravellersGearData(stack)[1]).invoke(stack.getItem(), player,stack);
				}catch(Exception e)
				{}
	}
	public static void equipTravGear(EntityPlayer player, ItemStack stack)
	{
		if(stack!=null)
			if(stack.getItem() instanceof ITravellersGear)
			{
				((ITravellersGear)stack.getItem()).onTravelGearEquip(player, stack);
				if(player.worldObj.isRemote)
					ClientProxy.equipmentMap.get(player.getCommandSenderName())[ModCompatability.getTravellersGearSlot(stack)] = stack;
			}
			else if(ModCompatability.getPseudoTravellersGearData(stack)!=null && ModCompatability.getPseudoTravellersGearData(stack).length>=4)
			{
				if(ModCompatability.getPseudoTravellersGearData(stack)[2]!=null)
					try{
						((Method)ModCompatability.getPseudoTravellersGearData(stack)[2]).invoke(stack.getItem(), player,stack);
					}catch(Exception e)
					{}
				if(player.worldObj.isRemote)
					ClientProxy.equipmentMap.get(player.getCommandSenderName())[ModCompatability.getTravellersGearSlot(stack)] = stack;
			}
	}
	public static void unequipTravGear(EntityPlayer player, ItemStack stack)
	{
		if(stack!=null)
			if(stack.getItem() instanceof ITravellersGear)
			{
				((ITravellersGear)stack.getItem()).onTravelGearUnequip(player, stack);
				if(player.worldObj.isRemote)
					ClientProxy.equipmentMap.get(player.getCommandSenderName())[ModCompatability.getTravellersGearSlot(stack)] = null;
			}
			else if(ModCompatability.getPseudoTravellersGearData(stack)!=null && ModCompatability.getPseudoTravellersGearData(stack).length>=4)
			{
				if(ModCompatability.getPseudoTravellersGearData(stack)[3]!=null)
					try{
						((Method)ModCompatability.getPseudoTravellersGearData(stack)[3]).invoke(stack.getItem(), player,stack);
					}catch(Exception e)
					{}
				if(player.worldObj.isRemote)
					ClientProxy.equipmentMap.get(player.getCommandSenderName())[ModCompatability.getTravellersGearSlot(stack)] = null;
			}
	}

	public static Slot getSlotAtPosition(GuiContainer gui, int x, int y)
	{
		for (int k = 0; k < gui.inventorySlots.inventorySlots.size(); ++k)
		{
			Slot slot = (Slot)gui.inventorySlots.inventorySlots.get(k);
			if(x >= slot.xDisplayPosition - 1 && x < slot.xDisplayPosition+16 + 1 && y >= slot.yDisplayPosition - 1 && y < slot.yDisplayPosition+16 + 1)
				return slot;
		}
		return null;
	}
	
	public static boolean compareToOreName(ItemStack item, String oreName)
	{
		for(int oid : OreDictionary.getOreIDs(item))
			if(OreDictionary.getOreName(oid).equalsIgnoreCase(oreName))
				return true;
		return false;
	}

	static String[] dyes = new String[]{"dyeBlack","dyeRed","dyeGreen","dyeBrown","dyeBlue","dyePurple","dyeCyan","dyeLightGray","dyeGray","dyePink","dyeLime","dyeYellow","dyeLightBlue","dyeMagenta","dyeOrange","dyeWhite"};
	public static boolean isDye(ItemStack item)
	{
		if(compareToOreName(item,"dye"))
			return true;
		for(String d : dyes)
			if(Utils.compareToOreName(item, d))
				return true;
		return false;
	}
	public static int getDamageForDye(ItemStack item)
	{
		for(int d=0;d<dyes.length;d++)
			if(Utils.compareToOreName(item, dyes[d]))
				return d;
		return -1;
	}
	public static ItemStack getColouredItem(ItemStack item, int col)
	{
		if(!item.hasTagCompound())
			item.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = item.getTagCompound().getCompoundTag("display");
		tag.setInteger("colour",col);
		item.getTagCompound().setTag("display",tag);
		return item;
	}
	
	public static boolean itemsMatch(ItemStack s0, ItemStack s1, boolean strict, boolean nbt)
	{
		boolean b0 = OreDictionary.itemMatches(s0, s1, strict);
		boolean b1 = nbt? ( (s0.hasTagCompound()&&s1.hasTagCompound())?s0.getTagCompound().equals(s1.getTagCompound()) : s0.hasTagCompound()==s1.hasTagCompound() ) : true;
		return b0&&b1;
	}
}