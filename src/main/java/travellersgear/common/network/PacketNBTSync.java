package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.api.TravellersGearAPI;
import travellersgear.client.ClientProxy;
import cpw.mods.fml.common.network.ByteBufUtils;

public class PacketNBTSync extends AbstractPacket
{
	int dim;
	int playerid;
	NBTTagCompound tag;
	public PacketNBTSync(){}
	public PacketNBTSync(EntityPlayer player)
	{
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
		this.tag = TravellersGearAPI.getTravellersNBTData(player);
		this.tag.setDouble("info_playerDamage", player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(dim);
		buffer.writeInt(playerid);
		ByteBufUtils.writeTag(buffer, tag);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		this.dim = buffer.readInt();
		this.playerid = buffer.readInt();
		this.tag = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer p2)
	{
		World world = DimensionManager.getWorld(this.dim);
		world = p2.worldObj;
		if (world == null)
		{
			return;
		}
		Entity player = world.getEntityByID(this.playerid);
		if(!(player instanceof EntityPlayer))
		{
			return;
		}
		((EntityPlayer)player).getEntityData().setTag("TravellersRPG", this.tag);
		ClientProxy.equipmentMap.put(player.getCommandSenderName(), TravellersGearAPI.getExtendedInventory((EntityPlayer) player));
	}

	@Override
	public void handleServerSide(EntityPlayer p2)
	{

	}

}
