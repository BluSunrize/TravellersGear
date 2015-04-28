package travellersgear.common.network;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageItemShoutout implements IMessage
{
	int dim;
	int playerid;
	ItemStack item;
	public MessageItemShoutout(){}
	public MessageItemShoutout(EntityPlayer player, ItemStack stack)
	{
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
		this.item = stack;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.dim = buf.readInt();
		this.playerid = buf.readInt();
		this.item=ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(dim);
		buf.writeInt(playerid);
		ByteBufUtils.writeItemStack(buf, item);
	}

	public static class Handler implements IMessageHandler<MessageItemShoutout, IMessage>
	{
		@Override
		public IMessage onMessage(MessageItemShoutout message, MessageContext ctx)
		{
			World world = DimensionManager.getWorld(message.dim);
			if (world == null)
				return null;
			Entity player = world.getEntityByID(message.playerid);
			if(!(player instanceof EntityPlayer))
				return null;
			for(EntityPlayer onlineP : (List<EntityPlayer>)world.playerEntities)
				onlineP.addChatMessage(new ChatComponentTranslation("TG.chattext.showItem", ((EntityPlayer)player).getDisplayName(), message.item.func_151000_E()));
			return null;
		}
	}
}