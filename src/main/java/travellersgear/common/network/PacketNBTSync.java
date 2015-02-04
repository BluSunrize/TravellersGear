package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.TravellersGear;
import travellersgear.api.TGSaveData;
import travellersgear.api.TravellersGearAPI;
import travellersgear.client.ClientProxy;
import travellersgear.client.ToolDisplayInfo;
import cpw.mods.fml.common.network.ByteBufUtils;

public class PacketNBTSync extends AbstractPacket
{
	int dim;
	int playerid;
	NBTTagCompound tag;
	NBTTagCompound[] toolDisplay;
	public PacketNBTSync(){}
	public PacketNBTSync(EntityPlayer player)
	{
		this.dim = player.worldObj.provider.dimensionId;
		this.playerid = player.getEntityId();
		
		this.tag = TGSaveData.getPlayerData(player);
		if(this.tag==null)
		{
			tag = new NBTTagCompound();
			if(player.getEntityData().getCompoundTag("TravellersRPG")!=null)
			{
				tag = player.getEntityData().getCompoundTag("TravellersRPG");
				player.getEntityData().removeTag("TravellersRPG");
			}
			tag.setLong("UUIDMost", player.getPersistentID().getMostSignificantBits());
			tag.setLong("UUIDLeast", player.getPersistentID().getLeastSignificantBits());
		}
			
//		this.tag = TravellersGearAPI.getTravellersNBTData(player);
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
	public void handleClientSide(EntityPlayer p)
	{
		World world = TravellersGear.proxy.getClientWorld();
		if (world == null)
			return;
		Entity player = world.getEntityByID(this.playerid);
		if(!(player instanceof EntityPlayer))
			return;
		if(tag == null)
		throw new RuntimeException("HEYO!");
//		((EntityPlayer)player).getEntityData().setTag("TravellersRPG", this.tag);
//		this.tag = TGSaveData.INSTANCE.playerData.get(player.getPersistentID());
		TGSaveData.setPlayerData((EntityPlayer) player, this.tag);
		TGSaveData.setDirty();
		
		ClientProxy.equipmentMap.put(player.getCommandSenderName(), TravellersGearAPI.getExtendedInventory((EntityPlayer) player));
		if(this.tag.hasKey("toolDisplay"))
		{
			NBTTagList list = this.tag.getTagList("toolDisplay", 10);
			ToolDisplayInfo[] tdi = new ToolDisplayInfo[list.tagCount()];
			for(int i=0; i<list.tagCount(); i++)
				tdi[i] = ToolDisplayInfo.readFromNBT(list.getCompoundTagAt(i));
			ClientProxy.toolDisplayMap.put(player.getCommandSenderName(), tdi);
		}
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
//		((EntityPlayer)player).getEntityData().setTag("TravellersRPG", this.tag);
		TGSaveData.setPlayerData((EntityPlayer) player, this.tag);
		TGSaveData.setDirty();
		PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync((EntityPlayer) player));
	}
}