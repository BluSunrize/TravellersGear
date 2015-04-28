package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageTileUpdate implements IMessage
{
	int worldId;
	int x;
	int y;
	int z;
	NBTTagCompound tag;

	public MessageTileUpdate(){}
	public MessageTileUpdate(TileEntity te)
	{
		try{
			this.worldId = te.getWorldObj().provider.dimensionId;
			this.x = te.xCoord;
			this.y = te.yCoord;
			this.z = te.zCoord;
			this.tag = new NBTTagCompound();
			te.writeToNBT(this.tag);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(worldId);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeTag(buf, tag);
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.worldId = buf.readInt();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.tag = ByteBufUtils.readTag(buf);
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
	public static class HandlerServer implements IMessageHandler<MessageTileUpdate, IMessage>
	{
		@Override
		public IMessage onMessage(MessageTileUpdate message, MessageContext ctx)
		{
			World world = DimensionManager.getWorld(message.worldId);
			if (world != null)
				if(world.getTileEntity(message.x, message.y, message.z)!=null)
					world.getTileEntity(message.x, message.y, message.z).readFromNBT(message.tag);
			return null;
		}
	}
}