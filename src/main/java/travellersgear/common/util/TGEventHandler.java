package travellersgear.common.util;

import net.minecraft.item.ItemStack;
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
	public void onLogin(PlayerLoggedInEvent event)
	{
		TravellersGear.instance.packetPipeline.sendToAll(new PacketNBTSync(event.player));
		TravellersGear.BAUBLES &= ModCompatability.getNewBaublesInv(event.player)!=null;
		TravellersGear.MARI &= ModCompatability.getMariInventory(event.player)!=null;
		TravellersGear.TCON &= ModCompatability.getTConArmorInv(event.player)!=null;
	}

}