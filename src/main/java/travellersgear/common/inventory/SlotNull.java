package travellersgear.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlotNull extends Slot
{
	public SlotNull(IInventory inv, int id, int x, int y)
	{
		super(inv, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}

	@Override
	public int getSlotStackLimit()
	{
		return 0;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_111238_b()
	{
		return false;
	}

	@Override
	public ItemStack getStack()
	{
		return null;
	}


	@Override
	public boolean getHasStack()
	{
		return false;
	}


	@Override
	public void putStack(ItemStack p_75215_1_)
	{
	}


	@Override
	public void onSlotChanged()
	{
	}

	@Override
	public ItemStack decrStackSize(int p_75209_1_)
	{
		return null;
	}


	@Override
	public boolean isSlotInInventory(IInventory p_75217_1_, int p_75217_2_)
	{
		return false;
	}

}