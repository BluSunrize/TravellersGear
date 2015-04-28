package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
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
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageNBTSync implements IMessage
{
	int dim;
	int playerid;
	NBTTagCompound tag;
	NBTTagCompound[] toolDisplay;
	public MessageNBTSync(){}
	public MessageNBTSync(EntityPlayer player)
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
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(dim);
		buf.writeInt(playerid);
		ByteBufUtils.writeTag(buf, tag);
	}
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.dim = buf.readInt();
		this.playerid = buf.readInt();
		this.tag = ByteBufUtils.readTag(buf);
	}
	
	public static class HandlerClient implements IMessageHandler<MessageNBTSync, IMessage>
	{
		@Override
		public IMessage onMessage(MessageNBTSync message, MessageContext ctx)
		{
			World world = TravellersGear.proxy.getClientWorld();
			if (world == null)
				return null;
			Entity player = world.getEntityByID(message.playerid);
			if(!(player instanceof EntityPlayer))
				return null;
			if(message.tag == null)
			throw new RuntimeException("HEYO!");
			TGSaveData.setPlayerData((EntityPlayer) player, message.tag);
			TGSaveData.setDirty();
			
			ClientProxy.equipmentMap.put(player.getCommandSenderName(), TravellersGearAPI.getExtendedInventory((EntityPlayer) player));
			if(message.tag.hasKey("toolDisplay"))
			{
				NBTTagList list = message.tag.getTagList("toolDisplay", 10);
				ToolDisplayInfo[] tdi = new ToolDisplayInfo[list.tagCount()];
				for(int i=0; i<list.tagCount(); i++)
					tdi[i] = ToolDisplayInfo.readFromNBT(list.getCompoundTagAt(i));
				ClientProxy.toolDisplayMap.put(player.getCommandSenderName(), tdi);
			}
			return null;
		}
	}
	public static class HandlerServer implements IMessageHandler<MessageNBTSync, IMessage>
	{
		@Override
		public IMessage onMessage(MessageNBTSync message, MessageContext ctx)
		{
			World world = DimensionManager.getWorld(message.dim);
			if (world == null)
				return null;
			Entity player = world.getEntityByID(message.playerid);
			if(!(player instanceof EntityPlayer))
				return null;
//			((EntityPlayer)player).getEntityData().setTag("TravellersRPG", this.tag);
			TGSaveData.setPlayerData((EntityPlayer) player, message.tag);
			TGSaveData.setDirty();
//			PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync((EntityPlayer) player));
			TravellersGear.packetHandler.sendToAll(new MessageNBTSync((EntityPlayer) player));
			return null;
		}
	}
}