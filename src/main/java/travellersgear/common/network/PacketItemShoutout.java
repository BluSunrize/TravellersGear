package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.ByteBufUtils;

public class PacketItemShoutout extends AbstractPacket
{
	int dim;
	int playerid;
	ItemStack item;
	public PacketItemShoutout(){}
	public PacketItemShoutout(EntityPlayer player, ItemStack stack)
	{
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
		this.item = stack;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(dim);
		buffer.writeInt(playerid);
		ByteBufUtils.writeItemStack(buffer, item);
	}
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		this.dim = buffer.readInt();
		this.playerid = buffer.readInt();
		this.item=ByteBufUtils.readItemStack(buffer);
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
		Entity player = world.getEntityByID(this.playerid);
		if(!(player instanceof EntityPlayer))
			return;
		for(EntityPlayer onlineP : (List<EntityPlayer>)world.playerEntities)
			onlineP.addChatMessage(new ChatComponentTranslation("TG.chattext.showItem", ((EntityPlayer)player).getDisplayName(), item.func_151000_E()));
	}
}