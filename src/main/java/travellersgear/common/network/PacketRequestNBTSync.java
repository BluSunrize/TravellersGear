package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class PacketRequestNBTSync extends AbstractPacket
{
	int worldId;
	int playerId;
	int requestWorldId;
	int requestPlayerId;
	public PacketRequestNBTSync(){}
	public PacketRequestNBTSync(EntityPlayer player, EntityPlayer requester)
	{
		worldId = player.dimension;
		playerId = player.getEntityId();
		requestWorldId = requester.dimension;
		requestPlayerId = requester.getEntityId();
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(worldId);
		buffer.writeInt(playerId);
		buffer.writeInt(requestWorldId);
		buffer.writeInt(requestPlayerId);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		this.worldId = buffer.readInt();
		this.playerId = buffer.readInt();
		this.requestWorldId = buffer.readInt();
		this.requestPlayerId = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer p)
	{
	}
	@Override
	public void handleServerSide(EntityPlayer p)
	{
		World world = DimensionManager.getWorld(this.worldId);
		if(world == null)
			return;
		Entity player = world.getEntityByID(this.playerId);

		World worldR = DimensionManager.getWorld(this.requestWorldId);
		if(worldR == null)
			return;
		Entity playerR = worldR.getEntityByID(this.requestPlayerId);

		if ( player!=null&&player instanceof EntityPlayer && playerR!=null &&playerR instanceof EntityPlayerMP)
			PacketPipeline.INSTANCE.sendTo(new PacketNBTSync((EntityPlayer)player), (EntityPlayerMP) playerR);
	}
}