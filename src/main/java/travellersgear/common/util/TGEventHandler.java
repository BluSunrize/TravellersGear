package travellersgear.common.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import travellersgear.TravellersGear;
import travellersgear.api.TravellersGearAPI;
import travellersgear.common.network.PacketNBTSync;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TGEventHandler
{
	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event)
	{
		if(event.type!=TickEvent.Type.PLAYER)
			return;

		if(event.phase.equals(TickEvent.Phase.START) && event.player!=null)
		{
			for(ItemStack stack : TravellersGearAPI.getExtendedInventory(event.player))
				if(stack!=null && ModCompatability.getTravellersGearSlot(stack)!=-1)
					Utils.tickTravGear(event.player, stack);
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