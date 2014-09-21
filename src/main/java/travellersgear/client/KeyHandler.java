package travellersgear.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ScreenShotHelper;
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
		if(Minecraft.getMinecraft().currentScreen!=null && Keyboard.isKeyDown(Keyboard.KEY_F2))
			/*Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(*/ScreenShotHelper.saveScreenshot(Minecraft.getMinecraft().mcDataDir, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, Minecraft.getMinecraft().getFramebuffer());/*);*/
        
		
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
