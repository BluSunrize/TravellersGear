package travellersgear.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ScreenShotHelper;
import travellersgear.TravellersGear;
import travellersgear.common.network.PacketOpenGui;
import travellersgear.common.network.PacketSlotSync;
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
				boolean[] hidden = new boolean[ClientProxy.moveableInvElements.size()];
				for(int bme=0;bme<hidden.length;bme++)
					hidden[bme] = ClientProxy.moveableInvElements.get(bme).hideElement;
				TravellersGear.instance.packetPipeline.sendToServer(new PacketSlotSync(event.player,hidden));
				TravellersGear.instance.packetPipeline.sendToServer(new PacketOpenGui(event.player,0));
				keyDown[0] = true;
			}
			else if(keyDown[0])
				keyDown[0] = false;
		}
	}
}
