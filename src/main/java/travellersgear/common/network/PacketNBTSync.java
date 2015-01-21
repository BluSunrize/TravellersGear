package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.TravellersGear;
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
	public void handleClientSide(EntityPlayer p)
	{
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("HEYOOOOOO");
		System.out.println();
		System.out.println();
		System.out.println();
		World world = TravellersGear.proxy.getClientWorld();
		if (world == null)
		{
			System.out.println("No World");
			return;
		}
		Entity player = world.getEntityByID(this.playerid);
		if(!(player instanceof EntityPlayer))
			return;
		System.out.println("BEFORE");
		for(ItemStack ss : TravellersGearAPI.getExtendedInventory((EntityPlayer) player))
			System.out.println(ss);
		System.out.println("AFTER");
		
		
//		System.out.println("Handling NBTSYNC!"+((EntityPlayer)player).getCommandSenderName());
		((EntityPlayer)player).getEntityData().setTag("TravellersRPG", this.tag);

		for(ItemStack ss : TravellersGearAPI.getExtendedInventory((EntityPlayer) player))
			System.out.println(ss);
		ClientProxy.equipmentMap.put(player.getCommandSenderName(), TravellersGearAPI.getExtendedInventory((EntityPlayer) player));
		if(this.tag.hasKey("toolDisplay"))
		{
			//System.out.println("updating displayTools for "+((EntityPlayer)player).getCommandSenderName());
			NBTTagList list = this.tag.getTagList("toolDisplay", 10);
			ToolDisplayInfo[] tdi = new ToolDisplayInfo[list.tagCount()];
			for(int i=0; i<list.tagCount(); i++)
			{
				tdi[i] = ToolDisplayInfo.readFromNBT(list.getCompoundTagAt(i));
				//System.out.println(tdi[i]+", "+tdi[i].slot);
			}
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
		((EntityPlayer)player).getEntityData().setTag("TravellersRPG", this.tag);
		PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync((EntityPlayer) player));
	}
}