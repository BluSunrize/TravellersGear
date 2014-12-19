package travellersgear.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import travellersgear.api.TravellersGearAPI;
import travellersgear.client.ToolDisplayInfo;
import cpw.mods.fml.common.network.ByteBufUtils;

public class PacketPlayerInventorySync extends AbstractPacket
{
	int dim;
	int playerid;
	int[] targetedSlots;
	ItemStack[] items;
	public PacketPlayerInventorySync()
	{
	}
	public PacketPlayerInventorySync(EntityPlayer player)
	{
		dim = player.worldObj.provider.dimensionId;
		playerid = player.getEntityId();
		NBTTagList list = TravellersGearAPI.getTravellersNBTData(player).getTagList("toolDisplay", 10);
		targetedSlots = new int[list.tagCount()];
		for(int i=0;i<list.tagCount();i++)
			targetedSlots[i] = ToolDisplayInfo.readFromNBT(list.getCompoundTagAt(i)).slot;
		items = new ItemStack[targetedSlots.length];
		for(int i=0; i<items.length; i++)
			items[i] = player.inventory.mainInventory[targetedSlots[i]];
	}

	@Override
	public void encodeInto(ChannelHandlerContext context, ByteBuf buffer)
	{
		buffer.writeInt(dim);
		buffer.writeInt(playerid);
		buffer.writeInt(targetedSlots.length);
		for(int i=0; i<targetedSlots.length; i++)
			buffer.writeInt(targetedSlots[i]);
		for(int i=0; i<targetedSlots.length; i++)
			ByteBufUtils.writeItemStack(buffer, items[i]);
	}

	@Override
	public void decodeInto(ChannelHandlerContext context, ByteBuf buffer)
	{
		this.dim = buffer.readInt();
		this.playerid = buffer.readInt();
		int l = buffer.readInt();
		this.targetedSlots = new int[l];
		for(int i=0; i<l; i++)
			targetedSlots[i] = buffer.readInt();
		items = new ItemStack[l];
		for(int i=0; i<l; i++)
			items[i] = ByteBufUtils.readItemStack(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer)
	{
		World world = DimensionManager.getWorld(this.dim);
		world = clientPlayer.worldObj;
		if (world == null)
			return;
		Entity player = world.getEntityByID(this.playerid);
		if(!(player instanceof EntityPlayer))
			return;
		
		for(int i=0; i<targetedSlots.length; i++)
			((EntityPlayer)player).inventory.mainInventory[targetedSlots[i]] = items[i];
	}

	@Override
	public void handleServerSide(EntityPlayer p2)
	{
	}

}
