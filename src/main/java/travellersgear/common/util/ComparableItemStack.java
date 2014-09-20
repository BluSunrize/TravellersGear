package travellersgear.common.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ComparableItemStack
{
	final ItemStack stack;
	public ComparableItemStack(ItemStack stack)
	{
		this.stack = stack;
	}
	
	public ItemStack getStack()
	{
		return this.stack;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof ComparableItemStack))
			return false;
		return OreDictionary.itemMatches(this.stack, ((ComparableItemStack)o).getStack(), true);
	}
}