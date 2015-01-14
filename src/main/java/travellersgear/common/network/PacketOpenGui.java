package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.TravellersGear;

public class PacketOpenGui extends AbstractPacket
{
	int dim;
	int playerid;
	int guiid;

	public PacketOpenGui(){}

	public PacketOpenGui(EntityPlayer player, int guiid)
	{
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
		this.guiid = guiid;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(this.dim);
		buffer.writeInt(this.playerid);
		buffer.writeInt(this.guiid);
	}
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		this.dim = buffer.readInt();
		this.playerid = buffer.readInt();
		this.guiid = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer p)
	{
		Minecraft.getMinecraft().thePlayer.openGui(TravellersGear.instance, guiid, Minecraft.getMinecraft().thePlayer.worldObj, MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posX), MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posY), MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posZ));
	}
	@Override
	public void handleServerSide(EntityPlayer p)
	{
		World world = DimensionManager.getWorld(this.dim);
		if (world == null)
			return;
		Entity ent = world.getEntityByID(this.playerid);
		if(!(ent instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) ent;
		PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync(player));
		PacketPipeline.INSTANCE.sendTo(new PacketOpenGui(player,guiid), (EntityPlayerMP) player);
		player.openGui(TravellersGear.instance, guiid, player.worldObj, MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
	}
}