package travellersgear.client;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import travellersgear.TravellersGear;
import travellersgear.api.TravellersGearAPI;
import travellersgear.common.CommonProxy;
import travellersgear.common.blocks.TileEntityArmorStand;
import travellersgear.common.network.PacketOpenGearGui;
import travellersgear.common.network.PacketRequestNBTSync;
import travellersgear.common.util.ModCompatability;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy
{
	public static HashMap<String, ItemStack[]> equipmentMap = new HashMap();

	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(this);
		RenderingRegistry.registerBlockHandler(new BlockRenderArmorStand());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmorStand.class, new TileRenderArmorStand());
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch(ID)
		{
		case 0:
			return new GuiTravellersInv(player);
		case 1:
			return new GuiArmorStand(player.inventory, (TileEntityArmorStand) world.getTileEntity(x, y, z));
		}
		return null;
	}

	@SubscribeEvent
	public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event)
	{
		if(event.gui instanceof GuiInventory)
		{
			int xSize = 176;
			int ySize = 166;

			int guiLeft = (event.gui.width - xSize) / 2;
			int guiTop = (event.gui.height - ySize) / 2;
			if( !event.gui.mc.thePlayer.getActivePotionEffects().isEmpty() && ModCompatability.isNeiHidden())
				guiLeft = 160 + (event.gui.width - xSize - 200) / 2;
			event.buttonList.add(new GuiButtonGear(106, guiLeft + 27, guiTop + 9));
		}
	}

	@SubscribeEvent
	public void guiDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event)
	{
		if(Loader.isModLoaded("GalacticraftCore"))
			if(event.gui instanceof GuiInventory)
			{
				List bList = null;
				try{
					bList = (List) GuiScreen.class.getDeclaredFields()[4].get(event.gui);
				}
				catch(Exception e){e.printStackTrace();}
				if(bList!=null)
					for(Object o : bList)
						if(o instanceof GuiButtonGear)
							return;
				int xSize = 176;
				int ySize = 166;
				int guiLeft = (event.gui.width - xSize) / 2;
				int guiTop = (event.gui.height - ySize) / 2;
				if( !event.gui.mc.thePlayer.getActivePotionEffects().isEmpty() && ModCompatability.isNeiHidden())
					guiLeft = 160 + (event.gui.width - xSize - 200) / 2;
				bList.add(new GuiButtonGear(106, guiLeft + 27, guiTop + 9));
			}
	}

	@SubscribeEvent
	public void guiPostAction(GuiScreenEvent.ActionPerformedEvent.Post event)
	{
		if(event.gui instanceof GuiInventory && event.button.getClass().equals(GuiButtonGear.class))
			TravellersGear.instance.packetPipeline.sendToServer(new PacketOpenGearGui(event.gui.mc.thePlayer));
	}

	@SubscribeEvent
	public void renderPlayerSpecialPre(RenderPlayerEvent.Specials.Pre event)
	{
		if(equipmentMap.containsKey(event.entityPlayer.getCommandSenderName()))
		{
			for(int i=0;i<equipmentMap.get(event.entityPlayer.getCommandSenderName()).length;i++)
			{
				ItemStack eq = equipmentMap.get(event.entityPlayer.getCommandSenderName())[i];
				if(eq!=null && eq.getItem().getArmorModel(event.entityPlayer, eq, 4+i)!=null)
				{
					if(i==0)
						event.renderCape = false;
					ModelBiped m = eq.getItem().getArmorModel(event.entityPlayer, eq, 4+i);
					if(eq.getItem().getArmorTexture(eq, event.entityPlayer, 4+i, null)!=null)
						Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(eq.getItem().getArmorTexture(eq, event.entityPlayer, 4+i, null)));
					m.render(event.entityPlayer, 0, 0, 0, 0, 0, .0625f);
				}
			}
		}
		else if(event.entityPlayer.getPlayerCoordinates()!=null)
			TravellersGear.instance.packetPipeline.sendToServer(new PacketRequestNBTSync(event.entityPlayer,Minecraft.getMinecraft().thePlayer));
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onTooltip(ItemTooltipEvent event)
	{
		if(event.itemStack.getItem().getClass().getName().endsWith("Knapsack") && Minecraft.getMinecraft().currentScreen!=null && Minecraft.getMinecraft().currentScreen.getClass().equals(GuiTravellersInv.class))
		{
			final ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
			int mX = Mouse.getX() * scaledresolution.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
			int mY = scaledresolution.getScaledHeight() - Mouse.getY() * scaledresolution.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
			int guiLeft = ((GuiTravellersInv)Minecraft.getMinecraft().currentScreen).getGuiPos()[0];
			int guiTop = ((GuiTravellersInv)Minecraft.getMinecraft().currentScreen).getGuiPos()[1];
			mX-=guiLeft;
			mY-=guiTop;
			if(mX>77&&mX<95 && mY>7&&mY<25)
			event.toolTip.add(1,EnumChatFormatting.AQUA+StatCollector.translateToLocal("TG.guitext.rightclickToOpen"));
		}
		for(int oid:OreDictionary.getOreIDs(event.itemStack))
			event.toolTip.add(OreDictionary.getOreName(oid));
	}

	@SubscribeEvent
	public void renderPlayerPre(RenderPlayerEvent.Pre event)
	{
		if(TravellersGearAPI.getTitleForPlayer(event.entityPlayer)==null || TravellersGearAPI.getTitleForPlayer(event.entityPlayer).isEmpty())
			return;
		String title=StatCollector.translateToLocal(TravellersGearAPI.getTitleForPlayer(event.entityPlayer));
		if(!RenderManager.instance.livingPlayer.equals(event.entityPlayer) && title!=null && !title.isEmpty() && !event.entityPlayer.isSneaking() && !Minecraft.getMinecraft().gameSettings.hideGUI)
		{	
			double p_147906_3_=event.entityPlayer.lastTickPosX-RenderManager.renderPosX;
			double p_147906_5_=event.entityPlayer.lastTickPosY-RenderManager.renderPosY;
			double p_147906_7_=event.entityPlayer.lastTickPosZ-RenderManager.renderPosZ;

			int p_147906_9_=64;
			double d3 = event.entityPlayer.getDistanceSqToEntity(RenderManager.instance.livingPlayer);

			if (d3 <= (double)(p_147906_9_ * p_147906_9_))
			{
				FontRenderer fontrenderer = RenderManager.instance.getFontRenderer();
				float f = 0.75F;
				float f1 = 0.016666668F * f;
				GL11.glPushMatrix();
				GL11.glTranslatef((float)p_147906_3_ + 0.0F, (float)p_147906_5_ + event.entityPlayer.height + 0.275F, (float)p_147906_7_);
				GL11.glNormal3f(0.0F, 1.0F, 0.0F);
				GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
				GL11.glScalef(-f1, -f1, f1);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDepthMask(false);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				Tessellator tessellator = Tessellator.instance;
				byte b0 = 0;
				if (event.entityPlayer.getCommandSenderName().equalsIgnoreCase("deadmau5"))
					b0 = -10;
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				tessellator.startDrawingQuads();
				int j = fontrenderer.getStringWidth(title) / 2;
				tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
				tessellator.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
				tessellator.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
				tessellator.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
				tessellator.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
				tessellator.draw();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				fontrenderer.drawString(title, -fontrenderer.getStringWidth(title) / 2, b0, 553648127);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(true);
				fontrenderer.drawString(title, -fontrenderer.getStringWidth(title) / 2, b0, -1);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glPopMatrix();
			}
		}
	}
}