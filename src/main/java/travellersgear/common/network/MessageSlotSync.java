package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.common.CommonProxy;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageSlotSync implements IMessage
{
	int dim;
	int playerid;
	boolean[] hidden;
	public MessageSlotSync(){}
	public MessageSlotSync(EntityPlayer player, boolean... hide)
	{
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
		this.hidden = hide;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(dim);
		buf.writeInt(playerid);
		buf.writeInt(hidden.length);
		for(boolean b : hidden)
			buf.writeBoolean(b);
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.dim = buf.readInt();
		this.playerid = buf.readInt();
		int l = buf.readInt();
		hidden = new boolean[l];
		for(int b=0;b<l;b++)
			hidden[b] = buf.readBoolean();
	}

	//	public static class HandlerClient implements IMessageHandler<MessageRequestNBTSync, IMessage>
	//	{
	//		@Override
	//		public IMessage onMessage(MessageRequestNBTSync message, MessageContext ctx)
	//		{
	//			World world = TravellersGear.proxy.getClientWorld();
	//			if (world == null)
	//				return null;
	//			Entity player = world.getEntityByID(message.playerid);
	//			if(!(player instanceof EntityPlayer))
	//				return null;
	//			
	//			for(int i=0; i<message.targetedSlots.length; i++)
	//				((EntityPlayer)player).inventory.mainInventory[message.targetedSlots[i]] = message.items[i];
	//			return null;
	//		}
	//	}
	public static class HandlerServer implements IMessageHandler<MessageSlotSync, IMessage>
	{
		@Override
		public IMessage onMessage(MessageSlotSync message, MessageContext ctx)
		{
			World world = DimensionManager.getWorld(message.dim);
			if (world == null)
				return null;
			Entity player = world.getEntityByID(message.playerid);
			if(player instanceof EntityPlayer)
				CommonProxy.hiddenSlots.put(player.getCommandSenderName(), message.hidden);
			return null;
		}
	}
}