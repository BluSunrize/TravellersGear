package travellersgear.common.network.old;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.api.IActiveAbility;
import travellersgear.api.TravellersGearAPI;
import travellersgear.common.util.ModCompatability;
import baubles.api.BaublesApi;

public class PacketActiveAbility extends AbstractPacket
{
	int dim;
	int playerid;
	int slot;
	public PacketActiveAbility(){}
	public PacketActiveAbility(EntityPlayer player, int slot)
	{
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
		this.slot = slot;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(dim);
		buffer.writeInt(playerid);
		buffer.writeInt(slot);
	}
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		this.dim = buffer.readInt();
		this.playerid = buffer.readInt();
		this.slot = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer p)
	{
	}
	@Override
	public void handleServerSide(EntityPlayer p)
	{
		World world = DimensionManager.getWorld(this.dim);
		if (world == null)
			return;
		Entity ent = world.getEntityByID(this.playerid);
		if(!(ent instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) ent;
		performAbility(player,slot);
		//FIXME
//		PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync(player));
	}
	
	
	public static void performAbility(EntityPlayer player, int slot)
	{
		switch(slot)
		{
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
			ItemStack[] mainInv = player.inventory.mainInventory;
			activateItem(mainInv[slot], player, true);
			break;
		case 9:
		case 10:
		case 11:
		case 12: // ARMOR
			ItemStack[] armorInv = player.inventory.armorInventory;
//			if(!Utils.itemsMatch(armorInv[slot-9], item, true, true))
//				armorInv[slot-9]=item;
//			player.inventory.armorInventory=armorInv;
			activateItem(armorInv[slot-9], player, false);
			break;
		case 13:
		case 14:
		case 15:
		case 16: // BAUBLES
			IInventory baubInv = BaublesApi.getBaubles(player);
//			if(baubInv!=null)
//				if(!Utils.itemsMatch(baubInv.getStackInSlot(slot-4-9), item, true, true))
//					baubInv.setInventorySlotContents(slot-4-9, item);
//			ModCompatability.setPlayerBaubles(player, baubInv);
			activateItem(baubInv.getStackInSlot(slot-4-9), player, false);
			break;
		case 17:
		case 18:
		case 19:
		case 20: // TRAVELLER'S GEAR
			ItemStack[] tgInv = TravellersGearAPI.getExtendedInventory(player);
//			if(tgInv!=null)
//				tgInv[slot-8-9]= item;
//			TravellersGearAPI.setExtendedInventory(player, tgInv);
			activateItem(tgInv[slot-8-9], player, false);
			TravellersGearAPI.setExtendedInventory(player, tgInv);
			//			PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync(player));
			break;
		case 21:
		case 22:
		case 23: // MARICULTURE
			IInventory mariInv = ModCompatability.getMariInventory(player);
//			if(mariInv!=null)
//				if(!Utils.itemsMatch(mariInv.getStackInSlot(slot-12-9), item, true, true))
//					mariInv.setInventorySlotContents(slot-12-9, item);
			activateItem(mariInv.getStackInSlot(slot-12-9), player, false);
			break;
		case 24:
		case 25: // TCON
			IInventory tconInv = ModCompatability.getTConArmorInv(player);
//			if(tconInv!=null)
//				if(!Utils.itemsMatch(tconInv.getStackInSlot(slot-15-9), item, true, true))
//					tconInv.setInventorySlotContents(slot-15-9, item);
			activateItem(tconInv.getStackInSlot(slot-15-9), player, false);
			break;
		default:
			break;
		}
	}
//	public static void updateInventories(EntityPlayer player, int slot, ItemStack item)
//	{
//		switch(slot)
//		{
//		case 0:
//		case 1:
//		case 2:
//		case 3:
//		case 4:
//		case 5:
//		case 6:
//		case 7:
//		case 8:
//			ItemStack[] mainInv = player.inventory.mainInventory;
//			if(!Utils.itemsMatch(mainInv[slot], item, true, true))
//				mainInv[slot]=item;
//			player.inventory.mainInventory=mainInv;
//			break;
//		case 9:
//		case 10:
//		case 11:
//		case 12: // ARMOR
//			ItemStack[] armorInv = player.inventory.armorInventory;
//			if(!Utils.itemsMatch(armorInv[slot-9], item, true, true))
//				armorInv[slot-9]=item;
//			player.inventory.armorInventory=armorInv;
//			break;
//		case 13:
//		case 14:
//		case 15:
//		case 16: // BAUBLES
//			IInventory baubInv = BaublesApi.getBaubles(player);
//			if(baubInv!=null)
//				if(!Utils.itemsMatch(baubInv.getStackInSlot(slot-4-9), item, true, true))
//					baubInv.setInventorySlotContents(slot-4-9, item);
//			ModCompatability.setPlayerBaubles(player, baubInv);
//			break;
//		case 17:
//		case 18:
//		case 19:
//		case 20: // TRAVELLER'S GEAR
//			ItemStack[] tgInv = TravellersGearAPI.getExtendedInventory(player);
//			if(tgInv!=null)
//				tgInv[slot-8-9]= item;
//			TravellersGearAPI.setExtendedInventory(player, tgInv);
//			//			PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync(player));
//			break;
//		case 21:
//		case 22:
//		case 23: // MARICULTURE
//			IInventory mariInv = ModCompatability.getMariInventory(player);
//			if(mariInv!=null)
//				if(!Utils.itemsMatch(mariInv.getStackInSlot(slot-12-9), item, true, true))
//					mariInv.setInventorySlotContents(slot-12-9, item);
//			break;
//		case 24:
//		case 25: // TCON
//			IInventory tconInv = ModCompatability.getTConArmorInv(player);
//			if(tconInv!=null)
//				if(!Utils.itemsMatch(tconInv.getStackInSlot(slot-15-9), item, true, true))
//					tconInv.setInventorySlotContents(slot-15-9, item);
//			break;
//		default:
//			break;
//		}
//	}
	static void activateItem(ItemStack stack, EntityPlayer player, boolean e)
	{
		if(stack!=null && stack.getItem() instanceof IActiveAbility)
			if(((IActiveAbility)stack.getItem()).canActivate(player, stack, e))
				((IActiveAbility)stack.getItem()).activate(player, stack);
	}
}
