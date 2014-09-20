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
		//System.out.println("Fireing packet!");
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
		this.tag = TravellersGearAPI.getTravellersNBTData(player);
		this.tag.setDouble("info_playerDamage", player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		//System.out.println("Encoding");
		buffer.writeInt(dim);
		buffer.writeInt(playerid);
		ByteBufUtils.writeTag(buffer, tag);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		//System.out.println("Decoding");
		this.dim = buffer.readInt();
		this.playerid = buffer.readInt();
		this.tag = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer p2)
	{
		//System.out.println("Client Handling");
		World world = DimensionManager.getWorld(this.dim);
		world = p2.worldObj;
		if (world == null)
		{
			//System.out.println("No World for dimension "+dim+"! D:");
			return;
		}
		//System.out.println("Has World");
		Entity player = world.getEntityByID(this.playerid);
		if(!(player instanceof EntityPlayer))
		{
			//System.out.println("No Player for id "+playerid+"! D:");
			return;
		}
		//System.out.println("Has Player");
		//74
		((EntityPlayer)player).getEntityData().setTag("TravellersRPG", this.tag);
		//System.out.println("performing NBTSync for "+player.getCommandSenderName()+" on "+(p2.worldObj.isRemote?"Client":"Server")+" world of "+p2.getCommandSenderName());
		//for(ItemStack stack : TravellersRPGAPI.getExtendedInventory((EntityPlayer) player))
		//	System.out.println(player.getCommandSenderName()+" is wearing: "+(stack!=null?stack.getDisplayName():"null"));
		ClientProxy.equipmentMap.put(player.getCommandSenderName(), TravellersGearAPI.getExtendedInventory((EntityPlayer) player));
		//		world.getPlayerEntityByName(player.getCommandSenderName()).getEntityData().setTag("TravellersRPG", this.tag);
		//		p2.getEntityData().setTag("TravellersRPG", this.tag);
	}

	@Override
	public void handleServerSide(EntityPlayer p2)
	{
		World world = DimensionManager.getWorld(this.dim);
		if (world == null) return;
		Entity player = world.getEntityByID(this.playerid);
		if(!(player instanceof EntityPlayer)) return;
		//		System.out.println("hargl");
		//		TravellersRPGAPI.setActiveTitleForPlayer((EntityPlayer)player, selected);
		//		for(String s : TravellersRPGAPI.getGivenTitlesForPlayer((EntityPlayer) player))
		//			System.out.println(s);
		//		TravellersRPG.packetPipeline.sendToAll(new PacketTitleBroadcast((EntityPlayer) player,selected,TravellersRPGAPI.getGivenTitlesForPlayer((EntityPlayer) player).toArray(new String[0])));

	}

}
