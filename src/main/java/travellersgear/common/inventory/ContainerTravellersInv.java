package travellersgear.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import travellersgear.TravellersGear;
import travellersgear.api.TravellersGearAPI;
import travellersgear.common.network.PacketNBTSync;
import travellersgear.common.util.ModCompatability;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;

public class ContainerTravellersInv extends Container
{
	//	public InventoryBaubles invBaubles;
	public IInventory invBaubles;
	public InventoryTG invTG;
	public IInventory invMari;
	public IInventory invTConArmor;
	EntityPlayer player = null;
	public int nonInventorySlots;
	int amulet;
	int ring0;
	int ring1;
	int belt;
	int travCloak;
	int travPauldrons;
	int travVambraces;
	int travTitle;
	int mariRing;
	int mariBracelet;
	int mariNecklace;
	int glove;
	int knapsack;

	public ContainerTravellersInv(InventoryPlayer invPlayer)
	{
		this.player = invPlayer.player;
		this.invTG = new InventoryTG(this, player);
		if(!player.worldObj.isRemote)
			this.invTG.stackList = TravellersGearAPI.getExtendedInventory(player);
		this.invBaubles = ModCompatability.getNewBaublesInv(player);
		ModCompatability.setBaubleContainer(invBaubles, this);
		if(!player.worldObj.isRemote)
			ModCompatability.setBaubleInvStacklist(invBaubles, BaublesApi.getBaubles(player));

		int i;
		for (i = 0; i < 4; i++)
		{
			final int k = i;
			int y = 26 + i*18;
			addSlotToContainer(new Slot(invPlayer, invPlayer.getSizeInventory() - 1 - i, 6, y)
			{
				public int getSlotStackLimit()
				{
					return 1;
				}

				public boolean isItemValid(ItemStack stack)
				{
					if (stack == null)
						return false;
					return stack.getItem().isValidArmor(stack, k, player);
				}
			});
		}
		//		addSlotToContainer(new SlotBauble(this.invBaubles, BaubleType.AMULET, 0, 42, 8));
		//		addSlotToContainer(new SlotBauble(this.invBaubles, BaubleType.RING, 1, 24, 8));
		//		addSlotToContainer(new SlotBauble(this.invBaubles, BaubleType.RING, 2, 60, 8));
		//		addSlotToContainer(new SlotBauble(this.invBaubles, BaubleType.BELT, 3, 78, 62));
		addSlotToContainer(new SlotRestricted(this.invTG, 0, 42,  8, player, SlotRestricted.SlotType.TRAVEL_CLOAK));
		addSlotToContainer(new SlotRestricted(this.invTG, 1, 78, 26, player, SlotRestricted.SlotType.TRAVEL_SHOULDER));
		addSlotToContainer(new SlotRestricted(this.invTG, 2, 78, 62, player, SlotRestricted.SlotType.TRAVEL_VAMBRACE));
		addSlotToContainer(new SlotRestricted(this.invTG, 3,  6, 98, player, SlotRestricted.SlotType.TRAVEL_TITLE));
		travCloak=4;
		travPauldrons=5;
		travVambraces=6;
		travTitle=7;
		nonInventorySlots=8;

		if(TravellersGear.BAUBLES)
		{
			addSlotToContainer(new SlotRestricted(this.invBaubles, 0, 24,  8, player, SlotRestricted.SlotType.BAUBLE_NECK));
			addSlotToContainer(new SlotRestricted(this.invBaubles, 1, 24, 98, player, SlotRestricted.SlotType.BAUBLE_RING));
			addSlotToContainer(new SlotRestricted(this.invBaubles, 2, 42, 98, player, SlotRestricted.SlotType.BAUBLE_RING));
			addSlotToContainer(new SlotRestricted(this.invBaubles, 3, 78, 44, player, SlotRestricted.SlotType.BAUBLE_BELT));
			amulet=nonInventorySlots;
			ring0=nonInventorySlots+1;
			ring1=nonInventorySlots+2;
			belt=nonInventorySlots+3;
			nonInventorySlots += 4;
		}

		if(TravellersGear.MARI)
		{
			this.invMari = ModCompatability.getMariInventory(player);
			addSlotToContainer(new SlotRestricted(this.invMari, 0, 60, 98, player, SlotRestricted.SlotType.MARI_RING));
			addSlotToContainer(new SlotRestricted(this.invMari, 1, 78, 80, player, SlotRestricted.SlotType.MARI_BRACELET));
			addSlotToContainer(new SlotRestricted(this.invMari, 2, 60,  8, player, SlotRestricted.SlotType.MARI_NECKLACE));
			mariRing = nonInventorySlots;
			mariBracelet = nonInventorySlots+1;
			mariNecklace = nonInventorySlots+2;
			nonInventorySlots += 3;
		}

		if(TravellersGear.TCON)
		{
			this.invTConArmor = ModCompatability.getTConArmorInv(player);
			addSlotToContainer(new SlotRestricted(this.invTConArmor, 1, 78, 98, player, SlotRestricted.SlotType.TINKERS_GLOVE));
			addSlotToContainer(new SlotRestricted(this.invTConArmor, 2, 78, 8, player, SlotRestricted.SlotType.TINKERS_BAG));
			glove = nonInventorySlots;
			knapsack = nonInventorySlots+1;
			nonInventorySlots += 2;
		}

		//PLAYER INVENTORY
		int j;
		for (i = 0; i < 3; ++i)
			for (j = 0; j < 9; ++j)
				this.addSlotToContainer(new Slot(invPlayer, j + (i + 1) * 9, 6 + j*18 +(j>4?10:0), 119 + i*18));
		for (i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(invPlayer, i, 6 + i*18 +(i>4?10:0), 173));
	}

	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		if (!player.worldObj.isRemote)
		{
			ModCompatability.setPlayerBaubles(player, invBaubles);
			TravellersGearAPI.setExtendedInventory(player, this.invTG.stackList);
			//			TravellersRPG.packetPipeline.sendTo(new PacketSkillsetInvUpdate(player), (EntityPlayerMP) player);
			TravellersGear.instance.packetPipeline.sendToAll(new PacketNBTSync(player));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(par2);
		if ((slot != null) && (slot.getHasStack()))
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (par2 <= nonInventorySlots)
			{
				if (!mergeItemStack(itemstack1, nonInventorySlots+1, nonInventorySlots+36, true))
					return null;
				slot.onSlotChange(itemstack1, itemstack);
			}
			else if(ModCompatability.getTravellersGearSlot(itemstack)>=0)
			{
				int targetSlot = ModCompatability.getTravellersGearSlot(itemstack);
				if(targetSlot>=0 && targetSlot<=3)
				{
					if (!mergeItemStack(itemstack1, travCloak+targetSlot, travCloak+targetSlot+1, false))
						return null;
				}
			}
			else if(TravellersGear.BAUBLES && itemstack.getItem() instanceof IBauble && ((IBauble)itemstack.getItem()).getBaubleType(itemstack)!=null)
			{
				IBauble baubleItem = (IBauble)itemstack.getItem();
				if( baubleItem.getBaubleType(itemstack)==BaubleType.AMULET && baubleItem.canEquip(itemstack, this.player) && !((Slot)this.inventorySlots.get(amulet)).getHasStack() )
				{
					if (!mergeItemStack(itemstack1, amulet, amulet + 1, false))
						return null;
				}
				else if( baubleItem.getBaubleType(itemstack)==BaubleType.RING && baubleItem.canEquip(itemstack, this.player) && !((Slot)this.inventorySlots.get(ring0)).getHasStack() )
				{
					if (!mergeItemStack(itemstack1, ring0, ring0 + 1, false))
						return null;
				}
				else if( baubleItem.getBaubleType(itemstack)==BaubleType.RING && baubleItem.canEquip(itemstack, this.player) && !((Slot)this.inventorySlots.get(ring1)).getHasStack() )
				{
					if (!mergeItemStack(itemstack1, ring1, ring1 + 1, false))
						return null;
				}
				else if( baubleItem.getBaubleType(itemstack)==BaubleType.BELT && baubleItem.canEquip(itemstack, this.player) && !((Slot)this.inventorySlots.get(belt)).getHasStack() )
				{
					if (!mergeItemStack(itemstack1, belt, belt + 1, false))
						return null;
				}
			}
			else if (((itemstack.getItem() instanceof ItemArmor)) && (!((Slot)this.inventorySlots.get(((ItemArmor)itemstack.getItem()).armorType)).getHasStack()))
			{
				int j = ((ItemArmor)itemstack.getItem()).armorType;
				if (!mergeItemStack(itemstack1, j, j + 1, false))
					return null;
			}
			else if(TravellersGear.MARI && ModCompatability.isMariJewelry(itemstack))
			{
				int valSlot = ModCompatability.getMariJeweleryType(itemstack).contains("BRACELET")?1 : ModCompatability.getMariJeweleryType(itemstack).contains("NECKLACE")?2 : 0;
				if (!mergeItemStack(itemstack1, mariRing+valSlot, mariRing+valSlot+1, false))
					return null;
			}
			else if(TravellersGear.TCON && ModCompatability.canEquipTConAccessory(itemstack1, 1))
			{
				if (!mergeItemStack(itemstack1, glove, glove+1, false))
					return null;
			}
			else if(TravellersGear.TCON && ModCompatability.canEquipTConAccessory(itemstack1, 2))
			{
				if (!mergeItemStack(itemstack1, knapsack, knapsack+1, false))
					return null;
			}
			else if((par2 >= nonInventorySlots) && (par2 < nonInventorySlots+27))
			{
				if (!mergeItemStack(itemstack1, nonInventorySlots+27, nonInventorySlots+36, false))
					return null;
			}
			else if ((par2 >= nonInventorySlots+27) && (par2 < nonInventorySlots+36))
			{
				if (!mergeItemStack(itemstack1, nonInventorySlots+1, nonInventorySlots+28, false))
					return null;
			}
			else if (!mergeItemStack(itemstack1, nonInventorySlots, nonInventorySlots+36, false, slot))
			{
				return null;
			}
			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack)null);
			} else {
				slot.onSlotChanged();
			}
			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}
			slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
		}
		return itemstack;
	}

	private void unequipBauble(ItemStack stack)
	{
		if ((stack.getItem() instanceof IBauble))
			((IBauble)stack.getItem()).onUnequipped(stack, this.player);
	}
	@Override
	public void putStacksInSlots(ItemStack[] stacks)
	{
		ModCompatability.baubleInvBlockEvents(invBaubles, true);
		this.invTG.allowEvents = false;
		super.putStacksInSlots(stacks);
	}

	protected boolean mergeItemStack(ItemStack par1ItemStack, int par2, int par3, boolean par4, Slot ss)
	{

		boolean flag1 = false;
		int k = par2;
		if (par4) {
			k = par3 - 1;
		}
		if (par1ItemStack.isStackable()) {
			while ((par1ItemStack.stackSize > 0) && (((!par4) && (k < par3)) || ((par4) && (k >= par2))))
			{
				Slot slot = (Slot)this.inventorySlots.get(k);
				ItemStack itemstack1 = slot.getStack();
				if ((itemstack1 != null) && (itemstack1.getItem() == par1ItemStack.getItem()) && ((!par1ItemStack.getHasSubtypes()) || (par1ItemStack.getItemDamage() == itemstack1.getItemDamage())) && (ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1)))
				{
					int l = itemstack1.stackSize + par1ItemStack.stackSize;
					if (l <= par1ItemStack.getMaxStackSize())
					{
						if ((ss instanceof SlotRestricted))
							if( ((SlotRestricted)ss).isBaubleSlot())
								unequipBauble(par1ItemStack);
						par1ItemStack.stackSize = 0;
						itemstack1.stackSize = l;
						slot.onSlotChanged();
						flag1 = true;
					}
					else if (itemstack1.stackSize < par1ItemStack.getMaxStackSize())
					{
						if ((ss instanceof SlotRestricted))
							if( ((SlotRestricted)ss).isBaubleSlot())
								unequipBauble(par1ItemStack);
						par1ItemStack.stackSize -= par1ItemStack.getMaxStackSize() - itemstack1.stackSize;
						itemstack1.stackSize = par1ItemStack.getMaxStackSize();
						slot.onSlotChanged();
						flag1 = true;
					}
				}
				if (par4) {
					k--;
				} else {
					k++;
				}
			}
		}
		if (par1ItemStack.stackSize > 0)
		{
			if (par4) {
				k = par3 - 1;
			} else {
				k = par2;
			}
			while (((!par4) && (k < par3)) || ((par4) && (k >= par2)))
			{
				Slot slot = (Slot)this.inventorySlots.get(k);
				ItemStack itemstack1 = slot.getStack();
				if (itemstack1 == null)
				{
					if ((ss instanceof SlotRestricted))
						if( ((SlotRestricted)ss).isBaubleSlot())
							unequipBauble(par1ItemStack);
					slot.putStack(par1ItemStack.copy());
					slot.onSlotChanged();
					par1ItemStack.stackSize = 0;
					flag1 = true;
					break;
				}
				if (par4) {
					k--;
				} else {
					k++;
				}
			}
		}
		return flag1;
	}

}