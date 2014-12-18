package travellersgear.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import travellersgear.api.TravellersGearAPI;
import travellersgear.common.util.ModCompatability;
import travellersgear.common.util.Utils;

public class InventoryTG implements IInventory
{
	private Container container;
	public ItemStack[] stackList;
	public EntityPlayer player;
	boolean allowEvents = true;

	public InventoryTG(Container par1Container, EntityPlayer p)
	{
		this.container = par1Container;
		this.player = p;
		this.stackList = new ItemStack[4];
	}


	@Override
	public int getSizeInventory()
	{
		return this.stackList.length;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		if(i >= this.getSizeInventory())return null;
		return this.stackList[i];
	}	

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		if (this.stackList[i] != null)
		{
			ItemStack itemstack = this.stackList[i];
			this.stackList[i] = null;
			return itemstack;
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if(this.stackList[i] != null)
		{
			ItemStack itemstack;

			if(this.stackList[i].stackSize <= j)
			{
				itemstack = this.stackList[i];
				this.stackList[i] = null;
				if(itemstack != null && ModCompatability.getTravellersGearSlot(itemstack)>=0)
					Utils.unequipTravGear(player, itemstack);

				this.markDirty();
				return itemstack;
			}
			itemstack = this.stackList[i].splitStack(j);

			if(this.stackList[i].stackSize == 0)
			{
				this.stackList[i] = null;
			}
			if(itemstack != null && ModCompatability.getTravellersGearSlot(itemstack)>=0)
				Utils.unequipTravGear(player, itemstack);

			this.container.onCraftMatrixChanged(this);
			//this.onInventoryChanged();
			return itemstack;
		}
		return null;
	}


	@Override
	public void setInventorySlotContents(int i, ItemStack stack)
	{
		if(allowEvents && this.stackList[i] != null && ModCompatability.getTravellersGearSlot(this.stackList[i])>=0)
			Utils.unequipTravGear(player, this.stackList[i]);

		this.stackList[i] = stack;
		if(stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}
		if(allowEvents && stack != null && ModCompatability.getTravellersGearSlot(stack)>=0)
			Utils.equipTravGear(player, stack);

		this.container.onCraftMatrixChanged(this);
	}

	@Override
	public String getInventoryName() {
		return "container.TravelersRPG.Skills";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty()
	{
		if(this.player.worldObj.isRemote)
			TravellersGearAPI.setExtendedInventory(this.player, this.stackList);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

}