package travellersgear.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import baubles.api.BaubleType;
import baubles.api.IBauble;

public class TileEntityArmorStand extends TileEntity implements IInventory
{
	public ItemStack[] Inv;
	public int facing = 4;
	public boolean renderFloor = true;
	public boolean renderTable = true;
	public boolean renderArmor[] = {true,true,true,true};
	public boolean displayBaubles[] = {true,true,true,true};
	public boolean renderBaubles[] = {true,true,true,true};
	public boolean displayTravellersGear[] = {true,true,true};
	public boolean renderTravellersGear[] = {true,true,true};
	public boolean renderMari[] = {true,true,true};
	public boolean renderGlove = true;

	public TileEntityArmorStand()
	{
		Inv = new ItemStack[4+4+3+3+1];
	}

	@Override
	public boolean canUpdate()
	{
		return false;
	}


	@Override
	public void readFromNBT(NBTTagCompound tags)
	{
		super.readFromNBT(tags);
		readCustomNBT(tags);
	}

	@Override
	public void writeToNBT(NBTTagCompound tags)
	{
		super.writeToNBT(tags);
		writeCustomNBT(tags);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 3, nbttagcompound);
	}
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		this.readFromNBT(pkt.func_148857_g());
	}

	public void readCustomNBT(NBTTagCompound tags)
	{
		NBTTagList tagList = tags.getTagList("Inv",10);
		for (int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < Inv.length)
				Inv[slot] = ItemStack.loadItemStackFromNBT(tag);
		}
		facing = tags.getInteger("facing");

		renderFloor = tags.getBoolean("renderFloor");
		renderArmor= new boolean[]{tags.getBoolean("renderArmor0"),tags.getBoolean("renderArmor1"),tags.getBoolean("renderArmor2"),tags.getBoolean("renderArmor3")};
		displayBaubles = new boolean[]{tags.getBoolean("displayBaubles0"),tags.getBoolean("displayBaubles1"),tags.getBoolean("displayBaubles2"),tags.getBoolean("displayBaubles3")};
		displayTravellersGear = new boolean[]{tags.getBoolean("displayTG0"),tags.getBoolean("displayTG1"),tags.getBoolean("displayTG2")};
		renderBaubles = new boolean[]{tags.getBoolean("renderBaubles0"),tags.getBoolean("renderBaubles1"),tags.getBoolean("renderBaubles2"),tags.getBoolean("renderBaubles3")};
		renderTravellersGear = new boolean[]{tags.getBoolean("renderTG0"),tags.getBoolean("renderTG1"),tags.getBoolean("renderTG2")};
		renderMari = new boolean[]{tags.getBoolean("renderMari0"),tags.getBoolean("renderMari1"),tags.getBoolean("renderMari2")};
		renderGlove = tags.getBoolean("renderGlove");
	}

	public void writeCustomNBT(NBTTagCompound tags)
	{
		if(Inv!=null){
			NBTTagList itemList = new NBTTagList();
			for (int i = 0; i < Inv.length; i++){
				ItemStack stack = Inv[i];
				if (stack != null){
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("Slot", (byte) i);
					stack.writeToNBT(tag);
					itemList.appendTag(tag);
				}}tags.setTag("Inv", itemList);}

		tags.setInteger("facing", facing);
		tags.setBoolean("renderFloor", renderFloor);
		for(int i=0;i<4;i++)
		{
			tags.setBoolean("renderArmor"+i, renderArmor[i]);
			tags.setBoolean("displayBaubles"+i, displayBaubles[i]);
			tags.setBoolean("renderBaubles"+i, renderBaubles[i]);
			if(i<3)
			{
				tags.setBoolean("displayTG"+i, displayTravellersGear[i]);
				tags.setBoolean("renderTG"+i, renderTravellersGear[i]);
				tags.setBoolean("renderMari"+i, renderMari[i]);
			}
		}
		tags.setBoolean("renderGlove", renderGlove);
	}

	@Override
	public int getSizeInventory()
	{
		return Inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		return Inv[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt)
	{
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		Inv[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}              
	}

	@Override
	public String getInventoryName()
	{
		return "ArmorStand";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return true;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this &&
				player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		if(itemstack==null || itemstack.getItem()==null)
			return false;
		if(i<4)
			return itemstack.getItem() instanceof ItemArmor && ((ItemArmor)itemstack.getItem()).armorType==i;
		return itemstack.getItem() instanceof IBauble && ((IBauble)itemstack.getItem()).getBaubleType(itemstack).equals(i==4?BaubleType.AMULET:i==5||i==6?BaubleType.AMULET:BaubleType.BELT);
	}

}