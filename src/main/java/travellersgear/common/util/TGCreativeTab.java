package travellersgear.common.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import travellersgear.TravellersGear;

public class TGCreativeTab extends CreativeTabs
{
	public TGCreativeTab()
	{
		super("travellersgear");
	}

	@Override
    @SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return null;
	}

	@Override
    @SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack()
	{
		return new ItemStack(TravellersGear.simpleGear,1,6);
	}

}
