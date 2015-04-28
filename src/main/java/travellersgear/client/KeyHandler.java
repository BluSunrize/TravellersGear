package travellersgear.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import travellersgear.TravellersGear;
import travellersgear.client.handlers.ActiveAbilityHandler;
import travellersgear.client.handlers.CustomizeableGuiHandler;
import travellersgear.common.network.MessageOpenGui;
import travellersgear.common.network.MessageSlotSync;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class KeyHandler
{
	public static KeyBinding openInventory = new KeyBinding("TG.keybind.openInv", 71, "key.categories.inventory");
	public static KeyBinding activeAbilitiesWheel = new KeyBinding("TG.keybind.activeaAbilities", 19, "key.categories.inventory");
	public boolean[] keyDown = {false,false};
	public static float abilityRadial;
	public static boolean abilityLock = false;

	public KeyHandler()
	{
		ClientRegistry.registerKeyBinding(openInventory);
		ClientRegistry.registerKeyBinding(activeAbilitiesWheel);
	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event)
	{
		if(Keyboard.isCreated() && event.side!=Side.SERVER && event.phase==TickEvent.Phase.START && FMLClientHandler.instance().getClient().inGameHasFocus)
		{
			EntityPlayer player = event.player;
			if(player==null)
				return;

			if(openInventory.getIsKeyPressed() && !keyDown[0])
			{
				boolean[] hidden = new boolean[CustomizeableGuiHandler.moveableInvElements.size()];
				for(int bme=0;bme<hidden.length;bme++)
					hidden[bme] = CustomizeableGuiHandler.moveableInvElements.get(bme).hideElement;
				TravellersGear.packetHandler.sendToServer(new MessageSlotSync(player,hidden));
				//				PacketPipeline.INSTANCE.sendToServer(new PacketSlotSync(player,hidden));
				TravellersGear.packetHandler.sendToServer(new MessageOpenGui(player,0));
				//				PacketPipeline.INSTANCE.sendToServer(new PacketOpenGui(player,0));
				keyDown[0] = true;
			}
			else if(keyDown[0])
				keyDown[0] = false;

			if(activeAbilitiesWheel!=null && activeAbilitiesWheel.getIsKeyPressed() && !keyDown[1] && ActiveAbilityHandler.instance.buildActiveAbilityList(player).length>0)
			{
				if(abilityLock)
				{
					abilityLock=false;
					keyDown[1] = true;
				}
				else if(FMLClientHandler.instance().getClient().inGameHasFocus)
				{
					if(abilityRadial<1)
						abilityRadial += ClientProxy.activeAbilityGuiSpeed;
					if(abilityRadial>1)
						abilityRadial=1f;
					if(abilityRadial>=1)	
					{
						abilityLock=true;
						keyDown[1] = true;
					}
				}
			}
			else
			{
				if(keyDown[1] && !activeAbilitiesWheel.getIsKeyPressed())
					keyDown[1]=false;
				if(!abilityLock)
				{
					if(abilityRadial>0)
						abilityRadial -= ClientProxy.activeAbilityGuiSpeed;
					if(abilityRadial<0)
						abilityRadial=0f;
				}
			}
		}
	}
}
