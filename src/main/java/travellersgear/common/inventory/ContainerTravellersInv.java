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
import travellersgear.client.ClientProxy;
import travellersgear.client.GuiButtonMoveableElement;
import travellersgear.common.CommonProxy;
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
	public int playerInventorySlots;
	public int playerHotbarSlots;
	int[] vanillaArmor={-1,-1,-1,-1};
	int[] baubles={-1,-1,-1,-1};
	int[] travGear={-1,-1,-1,-1};
	int[] mari={-1,-1,-1};
	int[] tcon={-1,-1, -1,-1,-1};

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

		vanillaArmor[0] = addSlot(new SlotRestricted(invPlayer, invPlayer.getSizeInventory()-1-0,  6, 26, player, SlotRestricted.SlotType.VANILLA_HELM));
		vanillaArmor[1] = addSlot(new SlotRestricted(invPlayer, invPlayer.getSizeInventory()-1-1,  6, 44, player, SlotRestricted.SlotType.VANILLA_CHEST));
		vanillaArmor[2] = addSlot(new SlotRestricted(invPlayer, invPlayer.getSizeInventory()-1-2,  6, 62, player, SlotRestricted.SlotType.VANILLA_LEGS));
		vanillaArmor[3] = addSlot(new SlotRestricted(invPlayer, invPlayer.getSizeInventory()-1-3,  6, 80, player, SlotRestricted.SlotType.VANILLA_BOOTS));
		nonInventorySlots=0+(vanillaArmor[0]>=0?1:0)+(vanillaArmor[1]>=0?1:0)+(vanillaArmor[2]>=0?1:0)+(vanillaArmor[3]>=0?1:0);

		travGear[0]=addSlot(new SlotRestricted(this.invTG, 0, 42,  8, player, SlotRestricted.SlotType.TRAVEL_CLOAK));
		travGear[1]=addSlot(new SlotRestricted(this.invTG, 1, 78, 26, player, SlotRestricted.SlotType.TRAVEL_SHOULDER));
		travGear[2]=addSlot(new SlotRestricted(this.invTG, 2, 78, 62, player, SlotRestricted.SlotType.TRAVEL_VAMBRACE));
		travGear[3]=addSlot(new SlotRestricted(this.invTG, 3,  6, 98, player, SlotRestricted.SlotType.TRAVEL_TITLE));
		nonInventorySlots+=(travGear[0]>=0?1:0)+(travGear[1]>=0?1:0)+(travGear[2]>=0?1:0)+(travGear[3]>=0?1:0);

		if(TravellersGear.BAUBLES)
		{
			baubles[0]=addSlot(new SlotRestricted(this.invBaubles, 0, 24,  8, player, SlotRestricted.SlotType.BAUBLE_NECK));
			baubles[1]=addSlot(new SlotRestricted(this.invBaubles, 1, 24, 98, player, SlotRestricted.SlotType.BAUBLE_RING));
			baubles[2]=addSlot(new SlotRestricted(this.invBaubles, 2, 42, 98, player, SlotRestricted.SlotType.BAUBLE_RING));
			baubles[3]=addSlot(new SlotRestricted(this.invBaubles, 3, 78, 44, player, SlotRestricted.SlotType.BAUBLE_BELT));
			nonInventorySlots+=(baubles[0]>=0?1:0)+(baubles[1]>=0?1:0)+(baubles[2]>=0?1:0)+(baubles[3]>=0?1:0);
		}

		if(TravellersGear.MARI)
		{
			this.invMari = ModCompatability.getMariInventory(player);
			mari[0]=addSlot(new SlotRestricted(this.invMari, 0, 60, 98, player, SlotRestricted.SlotType.MARI_RING));
			mari[1]=addSlot(new SlotRestricted(this.invMari, 1, 78, 80, player, SlotRestricted.SlotType.MARI_BRACELET));
			mari[2]=addSlot(new SlotRestricted(this.invMari, 2, 60,  8, player, SlotRestricted.SlotType.MARI_NECKLACE));
			nonInventorySlots+=(mari[0]>=0?1:0)+(mari[1]>=0?1:0)+(mari[2]>=0?1:0);
		}

		if(TravellersGear.TCON)
		{
			this.invTConArmor = ModCompatability.getTConArmorInv(player);
			tcon[0]=addSlot(new SlotRestricted(this.invTConArmor, 1, 78, 98, player, SlotRestricted.SlotType.TINKERS_GLOVE));
			tcon[1]=addSlot(new SlotRestricted(this.invTConArmor, 2, 78, 8, player, SlotRestricted.SlotType.TINKERS_BAG));
			tcon[2]=addSlot(new SlotRestricted(this.invTConArmor, 4, 191, 31, player, SlotRestricted.SlotType.TINKERS_HEART_R));
			tcon[3]=addSlot(new SlotRestricted(this.invTConArmor, 5, 191, 49, player, SlotRestricted.SlotType.TINKERS_HEART_Y));
			tcon[4]=addSlot(new SlotRestricted(this.invTConArmor, 6, 191, 67, player, SlotRestricted.SlotType.TINKERS_HEART_G));
			nonInventorySlots+=(tcon[0]>=0?1:0)+(tcon[1]>=0?1:0)+(tcon[2]>=0?1:0)+(tcon[3]>=0?1:0)+(tcon[4]>=0?1:0);
		}
		//PLAYER INVENTORY
		int i;
		int j;
		playerInventorySlots=0;
		playerHotbarSlots=0;
		for (i = 0; i < 3; ++i)
			for (j = 0; j < 9; ++j)
				if(this.addSlot(new Slot(invPlayer, j + (i + 1) * 9, 6 + j*18 +(j>4?10:0), 119 + i*18))>=0)
					playerInventorySlots++;
		for (i = 0; i < 9; ++i)
			if(this.addSlot(new Slot(invPlayer, i, 6 + i*18 +(i>4?10:0), 173))>=0)
				playerHotbarSlots++;
	}

	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		if (!player.worldObj.isRemote)
		{
			ModCompatability.setPlayerBaubles(player, invBaubles);
			TravellersGearAPI.setExtendedInventory(player, this.invTG.stackList);
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
			if (par2 < nonInventorySlots)
			{
				if (!mergeItemStack(itemstack1, nonInventorySlots, nonInventorySlots+playerInventorySlots+playerHotbarSlots, false))
					return null;
				slot.onSlotChange(itemstack1, itemstack);
			}
			else if(ModCompatability.getTravellersGearSlot(itemstack)>=0)
			{
				int targetSlot = ModCompatability.getTravellersGearSlot(itemstack);
				if(travGear[targetSlot]!=-1 && targetSlot>=0 && targetSlot<=3)
				{
					if (!mergeItemStack(itemstack1, travGear[0]+targetSlot, travGear[0]+targetSlot+1, false))
						return null;
				}
			}
			else if(TravellersGear.BAUBLES && itemstack.getItem() instanceof IBauble && ((IBauble)itemstack.getItem()).getBaubleType(itemstack)!=null)
			{
				IBauble baubleItem = (IBauble)itemstack.getItem();
				if( baubleItem.getBaubleType(itemstack)==BaubleType.AMULET && baubleItem.canEquip(itemstack, this.player) && !((Slot)this.inventorySlots.get(baubles[0])).getHasStack() )
				{
					if (!mergeItemStack(itemstack1, baubles[0], baubles[0] + 1, false))
						return null;
				}
				else if( baubleItem.getBaubleType(itemstack)==BaubleType.RING && baubleItem.canEquip(itemstack, this.player) && !((Slot)this.inventorySlots.get(baubles[1])).getHasStack() )
				{
					if (!mergeItemStack(itemstack1, baubles[1], baubles[1] + 1, false))
						return null;
				}
				else if( baubleItem.getBaubleType(itemstack)==BaubleType.RING && baubleItem.canEquip(itemstack, this.player) && !((Slot)this.inventorySlots.get(baubles[2])).getHasStack() )
				{
					if (!mergeItemStack(itemstack1, baubles[2], baubles[2] + 1, false))
						return null;
				}
				else if( baubleItem.getBaubleType(itemstack)==BaubleType.BELT && baubleItem.canEquip(itemstack, this.player) && !((Slot)this.inventorySlots.get(baubles[3])).getHasStack() )
				{
					if (!mergeItemStack(itemstack1, baubles[3], baubles[3] + 1, false))
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
				if (!mergeItemStack(itemstack1, mari[0]+valSlot, mari[0]+valSlot+1, false))
					return null;
			}
			else if(TravellersGear.TCON && ModCompatability.canEquipTConAccessory(itemstack1, 1))
			{
				if (!mergeItemStack(itemstack1, tcon[0], tcon[0]+1, false))
					return null;
			}
			else if(TravellersGear.TCON && ModCompatability.canEquipTConAccessory(itemstack1, 2))
			{
				if (!mergeItemStack(itemstack1, tcon[1], tcon[1]+1, false))
					return null;
			}
			else if(TravellersGear.TCON && ModCompatability.canEquipTConAccessory(itemstack1, 4))
			{
				if (!mergeItemStack(itemstack1, tcon[2], tcon[2]+1, false))
					return null;
			}
			else if(TravellersGear.TCON && ModCompatability.canEquipTConAccessory(itemstack1, 5))
			{
				if (!mergeItemStack(itemstack1, tcon[3], tcon[3]+1, false))
					return null;
			}
			else if(TravellersGear.TCON && ModCompatability.canEquipTConAccessory(itemstack1, 6))
			{
				if (!mergeItemStack(itemstack1, tcon[4], tcon[4]+1, false))
					return null;
			}
			else if((par2 >= nonInventorySlots) && (par2 < nonInventorySlots+playerInventorySlots))
			{
				if (!mergeItemStack(itemstack1, nonInventorySlots+playerInventorySlots, nonInventorySlots+playerInventorySlots+playerHotbarSlots, false))
					return null;
			}
			else if ((par2 >= nonInventorySlots+playerInventorySlots) && (par2 < nonInventorySlots+playerInventorySlots+playerHotbarSlots))
			{
				if (!mergeItemStack(itemstack1, nonInventorySlots, nonInventorySlots+playerInventorySlots, false))
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
	
	@Override
	public ItemStack slotClick(int slotNumber, int p_75144_2_, int p_75144_3_, EntityPlayer player)
	{
		ItemStack stack =  super.slotClick(slotNumber, p_75144_2_, p_75144_3_, player);
		if(player.worldObj.isRemote && slotNumber < nonInventorySlots && stack!=null)
			ClientProxy.equipmentMap.put(player.getCommandSenderName(), this.invTG.stackList);
		return stack;
	}
	
	@Override
	protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean inverse)
	{
		boolean flag1 = false;
		int k = start;

		if (inverse)
			k = end - 1;

		Slot slot;
		ItemStack itemstack1;

		if(stack.isStackable())
		{
			while(stack.stackSize > 0 && (!inverse && k < end || inverse && k >= start))
			{
				slot = (Slot)this.inventorySlots.get(k);
				if(slot.isItemValid(stack))
				{
					itemstack1 = slot.getStack();

					if(itemstack1 != null && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, itemstack1))
					{
						int l = itemstack1.stackSize + stack.stackSize;

						if(l <= stack.getMaxStackSize())
						{
							stack.stackSize = 0;
							itemstack1.stackSize = l;
							slot.onSlotChanged();
							flag1 = true;
						}
						else if(itemstack1.stackSize < stack.getMaxStackSize())
						{
							stack.stackSize -= stack.getMaxStackSize() - itemstack1.stackSize;
							itemstack1.stackSize = stack.getMaxStackSize();
							slot.onSlotChanged();
							flag1 = true;
						}
					}
				}

				if(inverse)
					--k;
				else
					++k;
			}
		}

		if(stack.stackSize > 0)
		{
			if(inverse)
				k = end - 1;
			else
				k = start;

			while(!inverse && k < end || inverse && k >= start)
			{
				slot = (Slot)this.inventorySlots.get(k);
				if(slot.isItemValid(stack))
				{
					itemstack1 = slot.getStack();
					if(itemstack1 == null)
					{
						slot.putStack(stack.copy());
						slot.onSlotChanged();
						stack.stackSize = 0;
						flag1 = true;
						break;
					}
				}
				if(inverse)
					--k;
				else
					++k;
			}
		}

		return flag1;
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

	int addSlot(Slot slot)
	{
		return addSlotToContainer(slot)/* instanceof SlotNull?-1:slot*/.slotNumber;
	}
	@Override
	protected Slot addSlotToContainer(Slot slot)
	{
		if(player.worldObj.isRemote && ClientProxy.moveableInvElements!=null && ClientProxy.moveableInvElements.size()>this.inventorySlots.size())
		{
			GuiButtonMoveableElement bme = ClientProxy.moveableInvElements.get(this.inventorySlots.size());
			if(bme!=null)
			{
				if(bme.hideElement)
				{
					int indx = slot.getSlotIndex();
					int x = slot.xDisplayPosition;
					int y = slot.yDisplayPosition;
					slot = new SlotNull(indx,x,y);
				}
				slot.xDisplayPosition = bme.elementX+1;
				slot.yDisplayPosition = bme.elementY+1;
			}
		}else if(CommonProxy.hiddenSlots.containsKey(player.getCommandSenderName()))
		{
			boolean[] hidden = CommonProxy.hiddenSlots.get(player.getCommandSenderName());
			if(hidden!=null && hidden.length>this.inventorySlots.size() && hidden[this.inventorySlots.size()])
			{
				int indx = slot.getSlotIndex();
				int x = slot.xDisplayPosition;
				int y = slot.yDisplayPosition;
				slot = new SlotNull(indx,x,y);
			}
		}
		//System.out.print(this.inventorySlots.size()+":"+(slot instanceof SlotNull)+", ");
		slot.slotNumber = this.inventorySlots.size();
		this.inventorySlots.add(slot);
		this.inventoryItemStacks.add((Object)null);
		return slot;
	}

}