package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.TravellersGear;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestNBTSync implements IMessage
{
	int worldId;
	int playerId;
	int requestWorldId;
	int requestPlayerId;
	public MessageRequestNBTSync(){}
	public MessageRequestNBTSync(EntityPlayer player, EntityPlayer requester)
	{
		worldId = player.dimension;
		playerId = player.getEntityId();
		requestWorldId = requester.dimension;
		requestPlayerId = requester.getEntityId();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(worldId);
		buf.writeInt(playerId);
		buf.writeInt(requestWorldId);
		buf.writeInt(requestPlayerId);
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.worldId = buf.readInt();
		this.playerId = buf.readInt();
		this.requestWorldId = buf.readInt();
		this.requestPlayerId = buf.readInt();
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
	public static class HandlerServer implements IMessageHandler<MessageRequestNBTSync, IMessage>
	{
		@Override
		public IMessage onMessage(MessageRequestNBTSync message, MessageContext ctx)
		{
			World world = DimensionManager.getWorld(message.worldId);
			if(world == null)
				return null;
			Entity player = world.getEntityByID(message.playerId);
			if(player==null || !(player instanceof EntityPlayer))
				return null;

			World worldR = DimensionManager.getWorld(message.requestWorldId);
			if(worldR == null)
				return null;
			Entity playerR = worldR.getEntityByID(message.requestPlayerId);
			if(playerR==null || !(playerR instanceof EntityPlayerMP))
				return null;
//			if ( player!=null&&player instanceof EntityPlayer && playerR!=null &&playerR instanceof EntityPlayerMP)
//				PacketPipeline.INSTANCE.sendTo(new PacketNBTSync((EntityPlayer)player), (EntityPlayerMP) playerR);
			TravellersGear.packetHandler.sendTo(new MessageNBTSync((EntityPlayer)player), (EntityPlayerMP) playerR);
//			return new MessageNBTSync((EntityPlayer)player);
			return null;
		}
	}
}