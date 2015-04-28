package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.TravellersGear;
import travellersgear.api.IActiveAbility;
import travellersgear.api.TravellersGearAPI;
import travellersgear.common.util.ModCompatability;
import baubles.api.BaublesApi;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageActiveAbility implements IMessage
{
	int dim;
	int playerid;
	int slot;
	public MessageActiveAbility(){}
	public MessageActiveAbility(EntityPlayer player, int slot)
	{
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
		this.slot = slot;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.dim = buf.readInt();
		this.playerid = buf.readInt();
		this.slot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(dim);
		buf.writeInt(playerid);
		buf.writeInt(slot);
	}

	public static class Handler implements IMessageHandler<MessageActiveAbility, IMessage>
	{
		@Override
		public IMessage onMessage(MessageActiveAbility message, MessageContext ctx)
		{
			World world = DimensionManager.getWorld(message.dim);
			if (world == null)
				return null;
			Entity ent = world.getEntityByID(message.playerid);
			if(!(ent instanceof EntityPlayer))
				return null;
			EntityPlayer player = (EntityPlayer) ent;
			performAbility(player,message.slot);
//			PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync(player));
			TravellersGear.packetHandler.sendToAll(new MessageNBTSync(player));
			return null;
		}
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
			activateItem(armorInv[slot-9], player, false);
			break;
		case 13:
		case 14:
		case 15:
		case 16: // BAUBLES
			IInventory baubInv = BaublesApi.getBaubles(player);
			activateItem(baubInv.getStackInSlot(slot-4-9), player, false);
			break;
		case 17:
		case 18:
		case 19:
		case 20: // TRAVELLER'S GEAR
			ItemStack[] tgInv = TravellersGearAPI.getExtendedInventory(player);
			activateItem(tgInv[slot-8-9], player, false);
			TravellersGearAPI.setExtendedInventory(player, tgInv);
			break;
		case 21:
		case 22:
		case 23: // MARICULTURE
			IInventory mariInv = ModCompatability.getMariInventory(player);
			activateItem(mariInv.getStackInSlot(slot-12-9), player, false);
			break;
		case 24:
		case 25: // TCON
			IInventory tconInv = ModCompatability.getTConArmorInv(player);
			activateItem(tconInv.getStackInSlot(slot-15-9), player, false);
			break;
		default:
			break;
		}
	}
	static void activateItem(ItemStack stack, EntityPlayer player, boolean e)
	{
		if(stack!=null && stack.getItem() instanceof IActiveAbility)
			if(((IActiveAbility)stack.getItem()).canActivate(player, stack, e))
				((IActiveAbility)stack.getItem()).activate(player, stack);
	}
}
