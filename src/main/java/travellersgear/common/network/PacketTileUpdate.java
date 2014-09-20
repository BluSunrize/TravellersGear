package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.ByteBufUtils;

public class PacketTileUpdate extends AbstractPacket
{
	int worldId;
	int x;
	int y;
	int z;
	NBTTagCompound tag;

	public PacketTileUpdate(){}
	public PacketTileUpdate(TileEntity te)
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
	public void encodeInto(ChannelHandlerContext context, ByteBuf buffer)
	{
		buffer.writeInt(worldId);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		ByteBufUtils.writeTag(buffer, tag);
	}

	@Override
	public void decodeInto(ChannelHandlerContext context, ByteBuf buffer)
	{
		this.worldId = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.tag = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer)
	{

	}

	@Override
	public void handleServerSide(EntityPlayer p2)
	{
		World world = DimensionManager.getWorld(this.worldId);
		if (world == null)
			return;
		if(world.getTileEntity(x, y, z)!=null)
			world.getTileEntity(x, y, z).readFromNBT(tag);
	}

}
