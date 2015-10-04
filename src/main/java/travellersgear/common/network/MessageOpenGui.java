package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.TravellersGear;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenGui implements IMessage
{
	int dim;
	int playerid;
	int guiid;

	public MessageOpenGui(){}
	public MessageOpenGui(EntityPlayer player, int guiid)
	{
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
		this.guiid = guiid;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(dim);
		buf.writeInt(playerid);
		buf.writeInt(guiid);
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.dim = buf.readInt();
		this.playerid = buf.readInt();
		this.guiid = buf.readInt();
	}
	
	public static class HandlerClient implements IMessageHandler<MessageOpenGui, IMessage>
	{
		@Override
		public IMessage onMessage(MessageOpenGui message, MessageContext ctx)
		{
			Minecraft.getMinecraft().thePlayer.openGui(TravellersGear.instance, message.guiid, Minecraft.getMinecraft().thePlayer.worldObj, MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posX), MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posY), MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posZ));
			return null;
		}
	}
	public static class HandlerServer implements IMessageHandler<MessageOpenGui, IMessage>
	{
		@Override
		public IMessage onMessage(MessageOpenGui message, MessageContext ctx)
		{
			World world = DimensionManager.getWorld(message.dim);
			if (world == null)
				return null;
			Entity ent = ctx.getServerHandler().playerEntity;//world.getEntityByID(message.playerid);
			if(!(ent instanceof EntityPlayer))
				return null;
			EntityPlayer player = (EntityPlayer) ent;
//			PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync(player));
			TravellersGear.packetHandler.sendToAll(new MessageNBTSync(player));
//			PacketPipeline.INSTANCE.sendTo(new PacketOpenGui(player,message.guiid), (EntityPlayerMP) player);
			boolean hasServerGui = TravellersGear.proxy.getServerGuiElement(message.guiid, player, world, (int)player.posX, (int)player.posY, (int)player.posZ)!=null;
			player.openGui(TravellersGear.instance, message.guiid, player.worldObj, MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
			return hasServerGui?null: new MessageOpenGui(player,message.guiid);
		}
	}
}