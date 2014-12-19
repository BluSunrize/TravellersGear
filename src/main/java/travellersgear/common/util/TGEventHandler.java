package travellersgear.common.util;

import java.util.HashMap;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import travellersgear.TravellersGear;
import travellersgear.api.TravellersGearAPI;
import travellersgear.client.ToolDisplayInfo;
import travellersgear.common.network.PacketNBTSync;
import travellersgear.common.network.PacketPlayerInventorySync;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TGEventHandler
{
	static HashMap<String, ItemStack[]> previousInv = new HashMap();
	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event)
	{
		if(event.type!=TickEvent.Type.PLAYER)
			return;

		if(event.phase.equals(TickEvent.Phase.START) && event.player!=null)
		{
			ItemStack[] prev = previousInv.get(event.player.getCommandSenderName());
			NBTTagList list = TravellersGearAPI.getTravellersNBTData(event.player).getTagList("toolDisplay", 10);
			int[] targetedSlots = new int[list.tagCount()];
			for(int i=0;i<list.tagCount();i++)
				targetedSlots[i] = ToolDisplayInfo.readFromNBT(list.getCompoundTagAt(i)).slot;

			if(list!=null && targetedSlots!=null && targetedSlots.length>0)
				if(prev==null || prev.length!=targetedSlots.length)
					TravellersGear.instance.packetPipeline.sendToAll(new PacketPlayerInventorySync(event.player));
				else
				{
					boolean packet = false;
					for(int i=0; i<prev.length; i++)
						if(!ItemStack.areItemStacksEqual(prev[i], event.player.inventory.mainInventory[targetedSlots[i]]))
							packet=true;
					if(packet)
						TravellersGear.instance.packetPipeline.sendToAll(new PacketPlayerInventorySync(event.player));
				}


			for(ItemStack stack : TravellersGearAPI.getExtendedInventory(event.player))
				if(stack!=null && ModCompatability.getTravellersGearSlot(stack)!=-1)
					Utils.tickTravGear(event.player, stack);
		}
		if(event.phase.equals(TickEvent.Phase.END) && event.player!=null)
		{
			NBTTagList list = TravellersGearAPI.getTravellersNBTData(event.player).getTagList("toolDisplay", 10);
			int[] targetedSlots = new int[list.tagCount()];
			for(int i=0;i<list.tagCount();i++)
				targetedSlots[i] = ToolDisplayInfo.readFromNBT(list.getCompoundTagAt(i)).slot;

			ItemStack[] newPrev = new ItemStack[targetedSlots.length];
			for(int i=0; i<newPrev.length; i++)
				newPrev[i] = event.player.inventory.mainInventory[targetedSlots[i]];
			previousInv.put(event.player.getCommandSenderName(), newPrev);
		}
	}

	@SubscribeEvent
	public void playerDrops(PlayerDropsEvent event)
	{
		if(!event.entityPlayer.worldObj.isRemote && !event.entityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
		{
			ItemStack[] tg = TravellersGearAPI.getExtendedInventory(event.entityPlayer);
			for(int i=0; i<tg.length; i++)
				if(tg[i]!=null)
				{
					EntityItem ei = new EntityItem(event.entityPlayer.worldObj, event.entityPlayer.posX,event.entityPlayer.posY,event.entityPlayer.posZ, tg[i]);
					ei.delayBeforeCanPickup = 40;
					float f1 = event.entityPlayer.worldObj.rand.nextFloat() * 0.5F;
					float f2 = event.entityPlayer.worldObj.rand.nextFloat() * 3.141593F * 2.0F;
					ei.motionX = (-MathHelper.sin(f2) * f1);
					ei.motionZ = (MathHelper.cos(f2) * f1);
					ei.motionY = 0.2000000029802322D;
					event.drops.add(ei);
					tg[i] = null;
				}
			TravellersGearAPI.setExtendedInventory(event.entityPlayer, tg);
			TravellersGear.instance.packetPipeline.sendToAll(new PacketNBTSync(event.entityPlayer));
		}
	}

	@SubscribeEvent
	public void onLogin(PlayerLoggedInEvent event)
	{
		TravellersGear.instance.packetPipeline.sendToAll(new PacketNBTSync(event.player));
		TravellersGear.BAUBLES &= ModCompatability.getNewBaublesInv(event.player)!=null;
		TravellersGear.MARI &= ModCompatability.getMariInventory(event.player)!=null;
		TravellersGear.TCON &= ModCompatability.getTConArmorInv(event.player)!=null;
	}

}