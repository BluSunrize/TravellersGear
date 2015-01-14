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
import travellersgear.common.network.PacketPipeline;
import travellersgear.common.util.ModCompatability;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;

public class ContainerArmorStand extends Container
{
	protected TileEntityArmorStand tileEntity;
	public IInventory invBaubles;
	public InventoryTG invTG;
	public IInventory invMari;
	public IInventory invTConArmor;
	private final EntityPlayer player;

	int playerSlots;

	int baubleStart;
	int tgStart;
	int mariStart;
	int tconStart;

	public ContainerArmorStand(InventoryPlayer inventoryPlayer, TileEntityArmorStand te)
	{
		this.player = inventoryPlayer.player;
		this.tileEntity = te;

		this.invBaubles = BaublesApi.getBaubles(player);
		//		this.invBaubles = ModCompatability.getNewBaublesInv(player);
		//		ModCompatability.setBaubleContainer(invBaubles, this);
		//		if(!player.worldObj.isRemote)
		//			ModCompatability.setBaubleInvStacklist(invBaubles, BaublesApi.getBaubles(player));

		this.invTG = new InventoryTG(this, player);
		if(!player.worldObj.isRemote)
			this.invTG.stackList = TravellersGearAPI.getExtendedInventory(player);

		this.invMari = ModCompatability.getMariInventory(player);

		this.invTConArmor = ModCompatability.getTConArmorInv(player);


		this.tileEntity = te;

		for(int i=0; i<4; i++)
		{
			SlotRestricted.SlotType type = i==0?SlotRestricted.SlotType.VANILLA_HELM: i==1?SlotRestricted.SlotType.VANILLA_CHEST: i==2?SlotRestricted.SlotType.VANILLA_LEGS: SlotRestricted.SlotType.VANILLA_BOOTS;
			addSlotToContainer(new SlotRestricted(inventoryPlayer, inventoryPlayer.getSizeInventory()-1-i, 4,22+i*18, player, type));
		}
		playerSlots=4;
		if(invBaubles!=null)
		{
			baubleStart = playerSlots;
			addSlotToContainer(new SlotRestricted(invBaubles, 0, 22, 4, player, SlotRestricted.SlotType.BAUBLE_NECK));
			addSlotToContainer(new SlotRestricted(invBaubles, 1, 22,94, player, SlotRestricted.SlotType.BAUBLE_RING));
			addSlotToContainer(new SlotRestricted(invBaubles, 2, 40,94, player, SlotRestricted.SlotType.BAUBLE_RING));
			addSlotToContainer(new SlotRestricted(invBaubles, 3, 76,40, player, SlotRestricted.SlotType.BAUBLE_BELT));
			playerSlots+=4;
		}
		if(invTG!=null)
		{
			tgStart = playerSlots;
			addSlotToContainer(new SlotRestricted(invTG, 0, 40, 4, player, SlotRestricted.SlotType.TRAVEL_CLOAK));
			addSlotToContainer(new SlotRestricted(invTG, 1, 76,22, player, SlotRestricted.SlotType.TRAVEL_SHOULDER));
			addSlotToContainer(new SlotRestricted(invTG, 2, 76,58, player, SlotRestricted.SlotType.TRAVEL_VAMBRACE));
			playerSlots+=3;
		}
		if(invMari!=null)
		{
			mariStart = playerSlots;
			addSlotToContainer(new SlotRestricted(invMari, 0, 58,94, player, SlotRestricted.SlotType.MARI_RING));
			addSlotToContainer(new SlotRestricted(invMari, 1, 76,76, player, SlotRestricted.SlotType.MARI_BRACELET));
			addSlotToContainer(new SlotRestricted(invMari, 2, 58, 4, player, SlotRestricted.SlotType.MARI_NECKLACE));
			playerSlots+=3;
		}
		if(invTConArmor!=null)
		{
			tconStart = playerSlots;
			addSlotToContainer(new SlotRestricted(invTConArmor, 1, 76,94, player, SlotRestricted.SlotType.TINKERS_GLOVE));
			playerSlots+=1;
		}


		for(int i=0; i<4; i++)
		{
			SlotRestricted.SlotType type = i==0?SlotRestricted.SlotType.VANILLA_HELM: i==1?SlotRestricted.SlotType.VANILLA_CHEST: i==2?SlotRestricted.SlotType.VANILLA_LEGS: SlotRestricted.SlotType.VANILLA_BOOTS;
			addSlotToContainer(new SlotRestricted(tileEntity, i, 120,22+i*18, player, type));
		}
		if(invBaubles!=null)
		{
			addSlotToContainer(new SlotRestricted(tileEntity, 4,138,22, player, SlotRestricted.SlotType.BAUBLE_NECK));
			addSlotToContainer(new SlotRestricted(tileEntity, 5,138,40, player, SlotRestricted.SlotType.BAUBLE_RING));
			addSlotToContainer(new SlotRestricted(tileEntity, 6,138,58, player, SlotRestricted.SlotType.BAUBLE_RING));
			addSlotToContainer(new SlotRestricted(tileEntity, 7,138,76, player, SlotRestricted.SlotType.BAUBLE_BELT));
		}
		if(invTG!=null)
		{
			addSlotToContainer(new SlotRestricted(tileEntity, 8,156,22, player, SlotRestricted.SlotType.TRAVEL_CLOAK));
			addSlotToContainer(new SlotRestricted(tileEntity, 9,156,40, player, SlotRestricted.SlotType.TRAVEL_SHOULDER));
			addSlotToContainer(new SlotRestricted(tileEntity,10,156,58, player, SlotRestricted.SlotType.TRAVEL_VAMBRACE));
		}
		if(invMari!=null)
		{
			addSlotToContainer(new SlotRestricted(tileEntity,11,120, 4, player, SlotRestricted.SlotType.MARI_RING));
			addSlotToContainer(new SlotRestricted(tileEntity,12,138, 4, player, SlotRestricted.SlotType.MARI_BRACELET));
			addSlotToContainer(new SlotRestricted(tileEntity,13,156, 4, player, SlotRestricted.SlotType.MARI_NECKLACE));
		}
		if(invTConArmor!=null)
			addSlotToContainer(new SlotRestricted(tileEntity,14,156,76, player, SlotRestricted.SlotType.TINKERS_GLOVE));


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
			PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync(player));
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
			if(iSlot<playerSlots || (iSlot>=playerSlots&&iSlot<playerSlots*2))
			{
				int armSlot = iSlot%playerSlots;
				ItemStack tempAS = ((Slot)this.inventorySlots.get(playerSlots+armSlot)).getStack();
				ItemStack tempP = ((Slot)this.inventorySlots.get(0+armSlot)).getStack();
				((Slot)this.inventorySlots.get(playerSlots+armSlot)).putStack(tempP);
				((Slot)this.inventorySlots.get(playerSlots+armSlot)).onSlotChanged();
				((Slot)this.inventorySlots.get(0+armSlot)).putStack(tempAS);
				((Slot)this.inventorySlots.get(0+armSlot)).onSlotChanged();


				//				if(player.worldObj.isRemote && iSlot>=tgStart&&iSlot<mariStart)
				//					ClientProxy.equipmentMap.put(player.getCommandSenderName(), this.invTG.stackList);
				return null;
			}
			else
			{
				Item stackItem = stack.getItem();
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
						if(!mergeItemStack(stackInSlot, baubleStart+min, baubleStart+min+1, true))
							return null;
					}
					else if(invBaubles.getStackInSlot(max)==null)
					{
						if(!mergeItemStack(stackInSlot, baubleStart+max, baubleStart+max+1, true))
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
						if(!mergeItemStack(stackInSlot, tgStart+type, tgStart+type+1, true))
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
						if(!mergeItemStack(stackInSlot, mariStart+type, mariStart+type+1, true))
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
						if(!mergeItemStack(stackInSlot, tconStart, tconStart+1, true))
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