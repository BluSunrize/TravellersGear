package travellersgear.common.network.old;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.common.CommonProxy;

public class PacketSlotSync extends AbstractPacket
{
	int dim;
	int playerid;
	boolean[] hidden;
	public PacketSlotSync(){}
	public PacketSlotSync(EntityPlayer player, boolean... hide)
	{
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
		this.hidden = hide;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(dim);
		buffer.writeInt(playerid);
		buffer.writeInt(hidden.length);
		for(boolean b : hidden)
			buffer.writeBoolean(b);
	}
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		this.dim = buffer.readInt();
		this.playerid = buffer.readInt();
		int l = buffer.readInt();
		hidden = new boolean[l];
		for(int b=0;b<l;b++)
			hidden[b] = buffer.readBoolean();
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
		if(player instanceof EntityPlayer)
			CommonProxy.hiddenSlots.put(player.getCommandSenderName(), hidden);
	}
}