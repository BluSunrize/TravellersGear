package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import travellersgear.TravellersGear;
import travellersgear.api.TravellersGearAPI;
import travellersgear.client.ToolDisplayInfo;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessagePlayerInventorySync implements IMessage
{
	int playerid;
	int[] targetedSlots;
	ItemStack[] items;

	public MessagePlayerInventorySync(){}
	public MessagePlayerInventorySync(EntityPlayer player)
	{
		playerid = player.getEntityId();
		NBTTagList list = TravellersGearAPI.getDisplayTools(player);
		
		targetedSlots = new int[list.tagCount()];
		for(int i=0;i<list.tagCount();i++)
			targetedSlots[i] = ToolDisplayInfo.readFromNBT(list.getCompoundTagAt(i)).slot;
		items = new ItemStack[targetedSlots.length];
		for(int i=0; i<items.length; i++)
			items[i] = player.inventory.mainInventory[targetedSlots[i]];
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(playerid);
		buf.writeInt(targetedSlots.length);
		for(int i=0; i<targetedSlots.length; i++)
			buf.writeInt(targetedSlots[i]);
		for(int i=0; i<targetedSlots.length; i++)
			ByteBufUtils.writeItemStack(buf, items[i]);
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.playerid = buf.readInt();
		int l = buf.readInt();
		this.targetedSlots = new int[l];
		for(int i=0; i<l; i++)
			targetedSlots[i] = buf.readInt();
		items = new ItemStack[l];
		for(int i=0; i<l; i++)
			items[i] = ByteBufUtils.readItemStack(buf);
	}
	
	public static class HandlerClient implements IMessageHandler<MessagePlayerInventorySync, IMessage>
	{
		@Override
		public IMessage onMessage(MessagePlayerInventorySync message, MessageContext ctx)
		{
			World world = TravellersGear.proxy.getClientWorld();
			if (world == null)
				return null;
			Entity player = world.getEntityByID(message.playerid);
			if(!(player instanceof EntityPlayer))
				return null;
			
			for(int i=0; i<message.targetedSlots.length; i++)
				((EntityPlayer)player).inventory.mainInventory[message.targetedSlots[i]] = message.items[i];
			return null;
		}
	}
//	public static class HandlerServer implements IMessageHandler<MessagePlayerInventorySync, IMessage>
//	{
//		@Override
//		public IMessage onMessage(MessagePlayerInventorySync message, MessageContext ctx)
//		{
//			World world = DimensionManager.getWorld(message.dim);
//			if (world == null)
//				return null;
//			Entity ent = world.getEntityByID(message.playerid);
//			if(!(ent instanceof EntityPlayer))
//				return null;
//			EntityPlayer player = (EntityPlayer) ent;
//			PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync(player));
//			PacketPipeline.INSTANCE.sendTo(new PacketOpenGui(player,message.guiid), (EntityPlayerMP) player);
//			player.openGui(TravellersGear.instance, message.guiid, player.worldObj, MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
//			return null;
//		}
//	}
}