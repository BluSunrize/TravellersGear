package travellersgear.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import travellersgear.TravellersGear;
import travellersgear.api.TravellersGearAPI;
import travellersgear.client.ClientProxy;
import travellersgear.common.blocks.TileEntityArmorStand;
import travellersgear.common.network.PacketNBTSync;
import travellersgear.common.util.ModCompatability;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;

public class ContainerArmorStand_old extends Container
{
	protected TileEntityArmorStand tileEntity;
	public IInventory invBaubles;
	public InventoryTG invTG;
	public IInventory invMari;
	public IInventory invTConArmor;
	private final EntityPlayer player;
	int playerSlots;
	int baubleSlotStart;
	int tgSlotStart;
	int mariSlotStart;
	int tconSlotStart;

	public ContainerArmorStand_old(InventoryPlayer inventoryPlayer, TileEntityArmorStand te)
	{
		this.player = inventoryPlayer.player;
		this.invTG = new InventoryTG(this, player);
		if(!player.worldObj.isRemote)
			this.invTG.stackList = TravellersGearAPI.getExtendedInventory(player);

		this.tileEntity = te;
		for (int i=0; i<4; i++)
		{
			final int k = i;
			addSlotToContainer(new Slot(inventoryPlayer, inventoryPlayer.getSizeInventory() - 1 - i, 4, 22 + i * 18)
			{
				@Override
				public int getSlotStackLimit()
				{
					return 1;
				}
				@Override
				public boolean isItemValid(ItemStack par1ItemStack)
				{
					if (par1ItemStack == null)
						return false;
					return par1ItemStack.getItem().isValidArmor(par1ItemStack, k, ContainerArmorStand_old.this.player);
				}
			});
		}
		playerSlots = 4;
		if(TravellersGear.BAUBLES)
		{
			baubleSlotStart = playerSlots;
			this.invBaubles = ModCompatability.getNewBaublesInv(player);
			ModCompatability.setBaubleContainer(invBaubles, this);
			if(!player.worldObj.isRemote)
				ModCompatability.setBaubleInvStacklist(invBaubles, BaublesApi.getBaubles(player));
			addSlotToContainer(new SlotRestricted(this.invBaubles, 0, 22,  4, player, SlotRestricted.SlotType.BAUBLE_NECK));
			addSlotToContainer(new SlotRestricted(this.invBaubles, 1, 22, 94, player, SlotRestricted.SlotType.BAUBLE_RING));
			addSlotToContainer(new SlotRestricted(this.invBaubles, 2, 40, 94, player, SlotRestricted.SlotType.BAUBLE_RING));
			addSlotToContainer(new SlotRestricted(this.invBaubles, 3, 76, 40, player, SlotRestricted.SlotType.BAUBLE_BELT));
			playerSlots += 4;
		}
		tgSlotStart = playerSlots;
		addSlotToContainer(new SlotRestricted(this.invTG, 0, 40,  4, player, SlotRestricted.SlotType.TRAVEL_CLOAK));
		addSlotToContainer(new SlotRestricted(this.invTG, 1, 76, 22, player, SlotRestricted.SlotType.TRAVEL_SHOULDER));
		addSlotToContainer(new SlotRestricted(this.invTG, 2, 76, 58, player, SlotRestricted.SlotType.TRAVEL_VAMBRACE));
		playerSlots += 3;
		if(TravellersGear.MARI)
		{
			mariSlotStart = playerSlots;
			this.invMari = ModCompatability.getMariInventory(player);
			addSlotToContainer(new SlotRestricted(this.invMari, 0, 58, 94, player, SlotRestricted.SlotType.MARI_RING));
			addSlotToContainer(new SlotRestricted(this.invMari, 1, 76, 76, player, SlotRestricted.SlotType.MARI_BRACELET));
			addSlotToContainer(new SlotRestricted(this.invMari, 2, 58,  4, player, SlotRestricted.SlotType.MARI_NECKLACE));
			playerSlots += 3;
		}
		if(TravellersGear.TCON)
		{
			tconSlotStart = playerSlots;
			this.invTConArmor = ModCompatability.getTConArmorInv(player);
			addSlotToContainer(new SlotRestricted(this.invTConArmor, 1, 76, 94, player, SlotRestricted.SlotType.TINKERS_GLOVE));
			playerSlots += 1;
		}
		/**
		 * 
		 * ARMORSTAND
		 * 
		 */
		for (int i=0; i<4; i++)
		{
			final int k = i;
			addSlotToContainer(new Slot(this.tileEntity, i, 120,22+i*18)
			{
				@Override
				public int getSlotStackLimit()
				{
					return 1;
				}
				@Override
				public boolean isItemValid(ItemStack par1ItemStack)
				{
					if (par1ItemStack == null)
						return false;
					return par1ItemStack.getItem().isValidArmor(par1ItemStack, k, ContainerArmorStand_old.this.player);
				}
			});
		}
		if(TravellersGear.BAUBLES)
			for (int i=0; i<4; i++)
			{
				SlotRestricted.SlotType slotType = i==0?SlotRestricted.SlotType.BAUBLE_NECK: i==3?SlotRestricted.SlotType.BAUBLE_BELT: SlotRestricted.SlotType.BAUBLE_RING;
				addSlotToContainer(new SlotRestricted(this.tileEntity, 4+i, 138,22+i*18, player,slotType)
				{
					@Override
					public boolean canTakeStack(EntityPlayer player)
					{
						return true;
					}
				});
			}
		for (int i=0; i<3; i++)
		{
			SlotRestricted.SlotType slotType = i==0?SlotRestricted.SlotType.TRAVEL_CLOAK: i==1?SlotRestricted.SlotType.TRAVEL_SHOULDER: SlotRestricted.SlotType.TRAVEL_VAMBRACE;
			addSlotToContainer(new SlotRestricted(this.tileEntity, 8+i, 156,22+i*18, player,slotType));
		}
		if(TravellersGear.MARI)
			for (int i=0; i<3; i++)
			{
				SlotRestricted.SlotType slotType = i==0?SlotRestricted.SlotType.MARI_RING: i==1?SlotRestricted.SlotType.MARI_BRACELET: SlotRestricted.SlotType.MARI_NECKLACE;
				addSlotToContainer(new SlotRestricted(this.tileEntity, 11+i, 120+i*18,4, player,slotType));
			}
		if(TravellersGear.TCON)
			addSlotToContainer(new SlotRestricted(this.tileEntity, 14, 156,76, player,SlotRestricted.SlotType.TINKERS_GLOVE));

		this.bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}
	
	@Override
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
		if(player.worldObj.isRemote && slotNumber<playerSlots)
			ClientProxy.equipmentMap.put(player.getCommandSenderName(), this.invTG.stackList);
		return stack;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer)
	{
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventoryPlayer, j+i*9+9, 8+j*18, 120+i*18));

		for (int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventoryPlayer, i, 8+i*18, 178));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int iSlot)
	{
		ItemStack stack = null;
		Slot slotObject = (Slot)this.inventorySlots.get(iSlot);
		if ((slotObject != null) && (slotObject.getHasStack()))
		{
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			Item stackItem = stack.getItem();
			if(iSlot<playerSlots || (iSlot>=playerSlots&&iSlot<playerSlots*2))
			{
				int armSlot = iSlot<playerSlots?iSlot : iSlot-playerSlots;
				ItemStack tempAS = ((Slot)this.inventorySlots.get(playerSlots+armSlot)).getStack();
				ItemStack tempP = ((Slot)this.inventorySlots.get(0+armSlot)).getStack();
				((Slot)this.inventorySlots.get(playerSlots+armSlot)).putStack(tempP);
				((Slot)this.inventorySlots.get(playerSlots+armSlot)).onSlotChanged();
				((Slot)this.inventorySlots.get(0+armSlot)).putStack(tempAS);
				((Slot)this.inventorySlots.get(0+armSlot)).onSlotChanged();

				if(player.worldObj.isRemote && armSlot>=tgSlotStart && armSlot<mariSlotStart)
					ClientProxy.equipmentMap.put(player.getCommandSenderName(), this.invTG.stackList);
				return null;
			}
			else
			{
				if(stackItem instanceof ItemArmor)
				{
					ItemArmor specificItem = (ItemArmor)stackItem;
					int type = specificItem.armorType;

					if(player.getCurrentArmor(3-type)==null)
					{
						if(!mergeItemStack(stackInSlot, 0+type, 0+type+1, true))
							return null;
					}
					else if(tileEntity.getStackInSlot(type)==null)
						if(!mergeItemStack(stackInSlot, playerSlots+type, playerSlots+type+1, true))
							return null;
				}
				else if(TravellersGear.BAUBLES && stackItem instanceof IBauble && ((IBauble)stackItem).getBaubleType(stackInSlot)!=null)
				{
					IBauble baubleItem = (IBauble)stackItem;
					BaubleType type = baubleItem.getBaubleType(stackInSlot);
					int min= type==BaubleType.AMULET?0 :type==BaubleType.RING?1 :3;
					int max= type==BaubleType.AMULET?0 :type==BaubleType.RING?2 :3;
					if(invBaubles.getStackInSlot(min)==null)
					{
						if(!mergeItemStack(stackInSlot, baubleSlotStart+min, baubleSlotStart+min+1, true))
							return null;
					}
					else if(invBaubles.getStackInSlot(max)==null)
					{
						if(!mergeItemStack(stackInSlot, baubleSlotStart+max, baubleSlotStart+max+1, true))
							return null;
					}
					else if(tileEntity.getStackInSlot(4+min)==null)
					{
						if(!mergeItemStack(stackInSlot, playerSlots+4+min, playerSlots+4+min+1, true))
							return null;
					}
					else if(tileEntity.getStackInSlot(4+max)==null)
					{
						if(!mergeItemStack(stackInSlot, playerSlots+4+max, playerSlots+4+max+1, true))
							return null;
					}
				}
				else if(ModCompatability.getTravellersGearSlot(stackInSlot)>=0)
				{
					int type = ModCompatability.getTravellersGearSlot(stackInSlot);
					if(invTG.getStackInSlot(type)==null)
					{
						if(!mergeItemStack(stackInSlot, tgSlotStart+type, tgSlotStart+type+1, true))
							return null;
					}
					else if(tileEntity.getStackInSlot(8+type)==null)
						if(!mergeItemStack(stackInSlot, playerSlots+8+type, playerSlots+8+type+1, true))
							return null;
					if(player.worldObj.isRemote)
						ClientProxy.equipmentMap.put(player.getCommandSenderName(), this.invTG.stackList);
				}
				else if(TravellersGear.MARI && ModCompatability.isMariJewelry(stackInSlot))
				{
					int type = ModCompatability.getMariJeweleryType(stackInSlot).contains("BRACELET")?1 : ModCompatability.getMariJeweleryType(stackInSlot).contains("NECKLACE")?2 : 0;
					if(invMari.getStackInSlot(type)==null)
					{
						if(!mergeItemStack(stackInSlot, mariSlotStart+type, mariSlotStart+type+1, true))
							return null;
					}
					else if(tileEntity.getStackInSlot(11+type)==null)
						if(!mergeItemStack(stackInSlot, playerSlots+11+type, playerSlots+11+type+1, true))
							return null;
				}
				else if(TravellersGear.TCON && ModCompatability.canEquipTConAccessory(stackInSlot, 1))
				{
					if(invTConArmor.getStackInSlot(1)==null)
					{
						if(!mergeItemStack(stackInSlot, tconSlotStart, tconSlotStart+1, true))
							return null;
					}
					else if(tileEntity.getStackInSlot(14)==null)
						if(!mergeItemStack(stackInSlot, playerSlots+14+1, playerSlots+14+1+1, true))
							return null;
				}

			}
			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}
			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
			return stack;
		}
		return null;
	}
}