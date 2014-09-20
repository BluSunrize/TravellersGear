package travellersgear.client;

import net.minecraft.client.settings.KeyBinding;
import travellersgear.TravellersGear;
import travellersgear.common.network.PacketOpenGearGui;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class KeyHandler
{
	public static KeyBinding openInventory = new KeyBinding("TG.keybind.openInv", 71, "key.categories.inventory");
	public boolean[] keyDown = {false};
	
	public KeyHandler()
	{
		ClientRegistry.registerKeyBinding(openInventory);
	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event)
	{
		if(event.side!=Side.SERVER && event.phase==TickEvent.Phase.START && FMLClientHandler.instance().getClient().inGameHasFocus)
		{
			if(openInventory.getIsKeyPressed() && !keyDown[0])
			{
				TravellersGear.instance.packetPipeline.sendToServer(new PacketOpenGearGui(event.player));
				keyDown[0] = true;
			}
			else if(keyDown[0])
				keyDown[0] = false;
		}
	}
}
