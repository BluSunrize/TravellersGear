package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.TravellersGear;

public class PacketOpenGearGui extends AbstractPacket
{
	int dim;
	int playerid;

	public PacketOpenGearGui(){}

	public PacketOpenGearGui(EntityPlayer player)
	{
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		this.dim = buffer.readInt();
		this.playerid = buffer.readInt();
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(this.dim);
		buffer.writeInt(this.playerid);
	}

	@Override
	public void handleClientSide(EntityPlayer player)
	{
	}

	@Override
	public void handleServerSide(EntityPlayer player2)
	{
		World world = DimensionManager.getWorld(this.dim);
		if (world == null)
			return;
		Entity ent = world.getEntityByID(this.playerid);
		if(!(ent instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) ent;
		TravellersGear.instance.packetPipeline.sendToAll(new PacketNBTSync(player));
		player.openGui(TravellersGear.instance, 0, world, MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
	}

}