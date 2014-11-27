package travellersgear.client;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import travellersgear.TravellersGear;
import travellersgear.api.RenderTravellersGearEvent;
import travellersgear.api.TravellersGearAPI;
import travellersgear.common.CommonProxy;
import travellersgear.common.blocks.TileEntityArmorStand;
import travellersgear.common.inventory.SlotRestricted;
import travellersgear.common.network.PacketOpenGui;
import travellersgear.common.network.PacketRequestNBTSync;
import travellersgear.common.network.PacketSlotSync;
import travellersgear.common.util.ModCompatability;
import travellersgear.common.util.TGClientCommand;
import travellersgear.common.util.Utils;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy
{
	public static HashMap<String, ItemStack[]> equipmentMap = new HashMap();
	public File configDir;
	public static List<GuiButtonMoveableElement> moveableInvElements = null;
	public static int elementsNonSlotStart;
	public Configuration invConfig;
	public static ResourceLocation[] invTextures = {new ResourceLocation("travellersgear","textures/gui/inventory_book.png"),new ResourceLocation("travellersgear","textures/gui/inventory_digital.png"),new ResourceLocation("travellersgear","textures/gui/inventory_epic.png")};
	public static ResourceLocation invTexture = invTextures[0];
	public static int[] equipmentButtonPos;
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		configDir = event.getModConfigurationDirectory();
		invConfig = new Configuration(new File(configDir,"TravellersGear_inv.cfg"));
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		cfg.load();
		equipmentButtonPos = cfg.get("Options", "Button Position", new int[]{27,9}, "The position of the Equipment Button in the Inventory").getIntList();
		cfg.save();
	}

	@SubscribeEvent
	public void loadTextures(TextureStitchEvent event)
	{
		List<ResourceLocation> txts = new ArrayList();
		txts.add(new ResourceLocation("travellersgear","textures/gui/inventory_book.png"));
		txts.add(new ResourceLocation("travellersgear","textures/gui/inventory_digital.png"));
		txts.add(new ResourceLocation("travellersgear","textures/gui/inventory_epic.png"));
		int c=0;
		ResourceLocation customN = new ResourceLocation("travellersgear","textures/gui/inventory_custom"+c+".png");
		while(resourceExists(customN) && c<32)
		{
			txts.add(customN);
			c++;
			customN = new ResourceLocation("travellersgear","textures/gui/inventory_custom"+c+".png");
		}
		invTextures = txts.toArray(new ResourceLocation[0]);
	}
	boolean resourceExists(ResourceLocation rl)
	{
		IResource r = null;
		try{
			r = Minecraft.getMinecraft().getResourceManager().getResource(rl);
		}catch(Exception e)
		{
			return false;
		}
		return r!=null;
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
		case 2:
			return new GuiTravellersInvCustomization(player);
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
			event.buttonList.add(new GuiButtonGear(106, guiLeft + equipmentButtonPos[0], guiTop + equipmentButtonPos[1]));
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
				bList.add(new GuiButtonGear(106, guiLeft + equipmentButtonPos[0], guiTop + equipmentButtonPos[1]));
			}
	}

	@SubscribeEvent
	public void guiPostAction(GuiScreenEvent.ActionPerformedEvent.Post event)
	{
		if(event.gui instanceof GuiInventory && event.button.getClass().equals(GuiButtonGear.class))
		{
			boolean[] hidden = new boolean[ClientProxy.moveableInvElements.size()];
			for(int bme=0;bme<hidden.length;bme++)
				hidden[bme] = ClientProxy.moveableInvElements.get(bme).hideElement;
			TravellersGear.instance.packetPipeline.sendToServer(new PacketSlotSync(event.gui.mc.thePlayer,hidden));
			TravellersGear.instance.packetPipeline.sendToServer(new PacketOpenGui(event.gui.mc.thePlayer, 0));
		}
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
					 renderTravellersItem(eq, i, event.entityPlayer, event.renderer, event.partialRenderTick);
				}
			}
		}
		else if(event.entityPlayer.getPlayerCoordinates()!=null)
			TravellersGear.instance.packetPipeline.sendToServer(new PacketRequestNBTSync(event.entityPlayer,Minecraft.getMinecraft().thePlayer));
	}
	public static float interpolateRotation(float par1, float par2, float par3)
	{
		float f3;
		for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F)
			;
		while (f3 >= 180.0F)
			f3 -= 360.0F;
		return par1 + par3 * f3;
	}

	public static void renderTravellersItem(ItemStack stack, int slot, EntityPlayer player, RenderPlayer renderer, float partialRenderTick)
	{
		RenderTravellersGearEvent renderEvent = new RenderTravellersGearEvent(player, renderer, stack, partialRenderTick);
		MinecraftForge.EVENT_BUS.post(renderEvent);
		if(!renderEvent.shouldRender)
			return;
		
		ModelBiped m = stack.getItem().getArmorModel(player, stack, 4+slot);
		if(m==null)
			return;
		String tex = stack.getItem().getArmorTexture(stack, player, 4+slot, null);
		if(tex!=null && !tex.isEmpty())
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(stack.getItem().getArmorTexture(stack, player, 4+slot, null)));

		m.aimedBow = renderer.modelBipedMain.aimedBow;
		m.heldItemRight = renderer.modelBipedMain.heldItemRight;
		m.heldItemLeft = renderer.modelBipedMain.heldItemLeft;
		m.onGround = renderer.modelBipedMain.onGround;
		m.isRiding = renderer.modelBipedMain.isRiding;
		m.isChild = renderer.modelBipedMain.isChild;
		m.isSneak = renderer.modelBipedMain.isSneak;
		float f2 = interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, partialRenderTick);
		float f3 = interpolateRotation(player.prevRotationYawHead, player.rotationYawHead, partialRenderTick);
		float f4;
		if (player.isRiding() && player.ridingEntity instanceof EntityLivingBase)
		{
			EntityLivingBase entitylivingbase1 = (EntityLivingBase)player.ridingEntity;
			f2 = interpolateRotation(entitylivingbase1.prevRenderYawOffset, entitylivingbase1.renderYawOffset, partialRenderTick);
			f4 = Math.min(85.0F, Math.max(-85.0F, MathHelper.wrapAngleTo180_float(f3 - f2)));
			f2 = f3 - f4;
			if (f4 * f4 > 2500.0F)
				f2 += f4 * 0.2F;
		}
		float f13 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialRenderTick;
		f4 = player.ticksExisted+partialRenderTick;
		float f5 = 0.0625F;
		float f6 = Math.min(1, player.prevLimbSwingAmount + (player.limbSwingAmount - player.prevLimbSwingAmount) * partialRenderTick);
		float f7 = (player.isChild()?3:1) *(player.limbSwing - player.limbSwingAmount * (1.0F - partialRenderTick));
		m.setLivingAnimations(player, f7, f6, partialRenderTick);
		m.render(player, f7, f6, f4, f3 - f2, f13, f5);
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onTooltip(ItemTooltipEvent event)
	{
		if(Minecraft.getMinecraft().currentScreen!=null && Minecraft.getMinecraft().currentScreen.getClass().equals(GuiTravellersInv.class))
		{
			if(event.itemStack.getItem().getClass().getName().endsWith("Knapsack"))
			{
				GuiTravellersInv guiContainer = (GuiTravellersInv)Minecraft.getMinecraft().currentScreen;
				int mX = Mouse.getEventX() * guiContainer.width / guiContainer.mc.displayWidth;
				int mY = guiContainer.height - Mouse.getEventY() * guiContainer.height / guiContainer.mc.displayHeight - 1;
				mX -= ((guiContainer.width - 218) / 2);
				mY -= ((guiContainer.height - 200) / 2);
				if(Utils.getSlotAtPosition(guiContainer, mX, mY) instanceof SlotRestricted && ((SlotRestricted)Utils.getSlotAtPosition(guiContainer, mX, mY)).type==SlotRestricted.SlotType.TINKERS_BAG)
					event.toolTip.add(1,EnumChatFormatting.AQUA+StatCollector.translateToLocal("TG.guitext.rightclickToOpen"));
			}
			event.toolTip.add(EnumChatFormatting.LIGHT_PURPLE+StatCollector.translateToLocal("TG.guitext.linkInChat"));
		}
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

	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(new KeyHandler());
		RenderingRegistry.registerBlockHandler(new BlockRenderArmorStand());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmorStand.class, new TileRenderArmorStand());
		ClientCommandHandler.instance.registerCommand(new TGClientCommand());

		invConfig.load();
		moveableInvElements = new ArrayList();
		//ARMOR
		addElementWithConfig(moveableInvElements, invConfig, 0, 18,18, "Helmet", false, 25,30);
		addElementWithConfig(moveableInvElements, invConfig, 1, 18,18, "Chestplate", false, 25,48);
		addElementWithConfig(moveableInvElements, invConfig, 2, 18,18, "Leggings", false, 25,66);
		addElementWithConfig(moveableInvElements, invConfig, 3, 18,18, "Boots", false, 25,84);
		//TRAVELLERS GEAR
		addElementWithConfig(moveableInvElements, invConfig, 4, 18,18, "Cloak", false, 61,12);
		addElementWithConfig(moveableInvElements, invConfig, 5, 18,18, "Pauldrons", false, 97,30);
		addElementWithConfig(moveableInvElements, invConfig, 6, 18,18, "Vambraces", false, 97,66);
		addElementWithConfig(moveableInvElements, invConfig, 7, 18,18, "Title", false, 25,102);
		int slots = 8;
		if(TravellersGear.BAUBLES)
		{
			addElementWithConfig(moveableInvElements, invConfig, slots+0, 18,18, "Amulet", false, 43,12);
			addElementWithConfig(moveableInvElements, invConfig, slots+1, 18,18, "Ring 1", false, 43,102);
			addElementWithConfig(moveableInvElements, invConfig, slots+2, 18,18, "Ring 2", false, 61,102);
			addElementWithConfig(moveableInvElements, invConfig, slots+3, 18,18, "Belt", false, 97,48);
			slots+=4;
		}
		if(TravellersGear.MARI)
		{
			addElementWithConfig(moveableInvElements, invConfig, slots+0, 18,18, "Mariculture Ring", false, 79,102);
			addElementWithConfig(moveableInvElements, invConfig, slots+1, 18,18, "Mariculture Bracelet", false, 97,84);
			addElementWithConfig(moveableInvElements, invConfig, slots+2, 18,18, "Mariculture Necklace", false, 79,12);
			slots+=3;
		}
		if(TravellersGear.TCON)
		{
			addElementWithConfig(moveableInvElements, invConfig, slots+0, 18,18, "Tinkers Glove", false, 97,102);
			addElementWithConfig(moveableInvElements, invConfig, slots+1, 18,18, "Tinkers Knapsack", false, 97,12);
			addElementWithConfig(moveableInvElements, invConfig, slots+2, 18,18, "Tinkers Heart Red", false, 190,30);
			addElementWithConfig(moveableInvElements, invConfig, slots+3, 18,18, "Tinkers Heart Yellow", false, 190,48);
			addElementWithConfig(moveableInvElements, invConfig, slots+4, 18,18, "Tinkers Heart Green", false, 190,66);
			slots+=5;
		}

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addElementWithConfig(moveableInvElements, invConfig, slots+j+(i+1)*9, 18,18, "Inventory "+(j+(i*9)), false, 25+j*18+(j>4?6:0),120+i*18);
		slots+=27;
		for (int i = 0; i < 9; ++i)
			addElementWithConfig(moveableInvElements, invConfig, slots+i, 18,18, "Hotbar "+i, false, 25+i*18+(i>4?6:0),174);
		slots+=9;

		elementsNonSlotStart=slots;
		addElementWithConfig(moveableInvElements, invConfig, slots+0, 54,72, "Player", true, 43,30);
		addElementWithConfig(moveableInvElements, invConfig, slots+1, 80,10, "Name", true, 128,12);
		addElementWithConfig(moveableInvElements, invConfig, slots+2, 76, 8, "Tile", true, 130,22);
		addElementWithConfig(moveableInvElements, invConfig, slots+3, 70,20, "Experience", true, 133,29);
		addElementWithConfig(moveableInvElements, invConfig, slots+4, 64,10, "Health", true, 136,49);
		addElementWithConfig(moveableInvElements, invConfig, slots+5, 64,10, "Armor", true, 136,59);
		addElementWithConfig(moveableInvElements, invConfig, slots+6, 64,10, "Speed", true, 136,69);
		addElementWithConfig(moveableInvElements, invConfig, slots+7, 64,10, "Attack Strength", true, 136,79);
		addElementWithConfig(moveableInvElements, invConfig, slots+8, 18,162,"Potion Effects", true, 0, 22);
		if(TravellersGear.THAUM)
			addElementWithConfig(moveableInvElements, invConfig, slots+9, 64,30, "Vis Discounts", true, 136,89);
		invTexture = new ResourceLocation(invConfig.get("InvConfig", "TEXTURE", "travellersgear:textures/gui/inventory_book.png").getString());
		invConfig.save();
		createPresets();
	}

	GuiButtonMoveableElement addElementWithConfig(List addToList, Configuration invConfig, int id, int w, int h, String name, boolean b, int... def)
	{
		int[] xy = invConfig.get("InvConfig", name, def).getIntList();
		boolean hidden = invConfig.get("InvConfig", name+"_isHidden", false).getBoolean();
		GuiButtonMoveableElement bme = new GuiButtonMoveableElement(id, xy[0],xy[1], w,h, name,true);
		bme.hideElement = hidden;
		if(addToList!=null)
			addToList.add(bme);
		return bme;
	}
	GuiButtonMoveableElement addElement(List addToList, Configuration invConfig, int id, int w, int h, String name, boolean b, int... def)
	{
		GuiButtonMoveableElement bme = new GuiButtonMoveableElement(id, def[0],def[1], w,h, name,true);
		if(addToList!=null)
			addToList.add(bme);
		return bme;
	}

	public void writeElementsToConfig(Configuration invConfig)
	{
		invConfig.load();
		for(GuiButtonMoveableElement bme : moveableInvElements)
		{
			invConfig.get("InvConfig", bme.displayString, new int[]{bme.elementX,bme.elementY}).set(new int[]{bme.elementX,bme.elementY});
			invConfig.get("InvConfig", bme.displayString+"_isHidden", bme.hideElement).set(bme.hideElement);
		}
		invConfig.get("InvConfig", "TEXTURE", invTexture.getResourceDomain()+":"+invTexture.getResourcePath()).set(invTexture.getResourceDomain()+":"+invTexture.getResourcePath());
		invConfig.save();
	}

	static HashMap<String,InvPreset> presets = new HashMap();
	void createPresets()
	{
		List presetList = new ArrayList();
		//ARMOR
		addElement(presetList, invConfig, 0, 18,18, "Helmet", false, 25,30);
		addElement(presetList, invConfig, 1, 18,18, "Chestplate", false, 25,48);
		addElement(presetList, invConfig, 2, 18,18, "Leggings", false, 25,66);
		addElement(presetList, invConfig, 3, 18,18, "Boots", false, 25,84);
		//TRAVELLERS GEAR
		addElement(presetList, invConfig, 4, 18,18, "Cloak", false, 61,12);
		addElement(presetList, invConfig, 5, 18,18, "Pauldrons", false, 97,30);
		addElement(presetList, invConfig, 6, 18,18, "Vambraces", false, 97,66);
		addElement(presetList, invConfig, 7, 18,18, "Title", false, 25,102);
		int slots = 8;
		if(TravellersGear.BAUBLES)
		{
			addElement(presetList, invConfig, slots+0, 18,18, "Amulet", false, 43,12);
			addElement(presetList, invConfig, slots+1, 18,18, "Ring 1", false, 43,102);
			addElement(presetList, invConfig, slots+2, 18,18, "Ring 2", false, 61,102);
			addElement(presetList, invConfig, slots+3, 18,18, "Belt", false, 97,48);
			slots+=4;
		}
		if(TravellersGear.MARI)
		{
			addElement(presetList, invConfig, slots+0, 18,18, "Mariculture Ring", false, 79,102);
			addElement(presetList, invConfig, slots+1, 18,18, "Mariculture Bracelet", false, 97,84);
			addElement(presetList, invConfig, slots+2, 18,18, "Mariculture Necklace", false, 79,12);
			slots+=3;
		}
		if(TravellersGear.TCON)
		{
			addElement(presetList, invConfig, slots+0, 18,18, "Tinkers Glove", false, 97,102);
			addElement(presetList, invConfig, slots+1, 18,18, "Tinkers Knapsack", false, 97,12);
			addElement(presetList, invConfig, slots+2, 18,18, "Tinkers Heart Red", false, 190,30).hideElement=true;
			addElement(presetList, invConfig, slots+3, 18,18, "Tinkers Heart Yellow", false, 190,48).hideElement=true;
			addElement(presetList, invConfig, slots+4, 18,18, "Tinkers Heart Green", false, 190,66).hideElement=true;
			slots+=5;
		}

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addElement(presetList, invConfig, slots+j+(i+1)*9, 18,18, "Inventory "+(j+(i*9)), false, 25+j*18+(j>4?6:0),120+i*18);
		slots+=27;
		for (int i = 0; i < 9; ++i)
			addElement(presetList, invConfig, slots+i, 18,18, "Hotbar "+i, false, 25+i*18+(i>4?6:0),174);
		slots+=9;

		elementsNonSlotStart=slots;
		addElement(presetList, invConfig, slots+0, 54,72, "Player", true, 43,30);
		addElement(presetList, invConfig, slots+1, 80,10, "Name", true, 128,12);
		addElement(presetList, invConfig, slots+2, 76, 8, "Tile", true, 130,22);
		addElement(presetList, invConfig, slots+3, 70,20, "Experience", true, 133,29);
		addElement(presetList, invConfig, slots+4, 64,10, "Health", true, 136,49);
		addElement(presetList, invConfig, slots+5, 64,10, "Armor", true, 136,59);
		addElement(presetList, invConfig, slots+6, 64,10, "Speed", true, 136,69);
		addElement(presetList, invConfig, slots+7, 64,10, "Attack Strength", true, 136,79);
		addElement(presetList, invConfig, slots+8, 18,162,"Potion Effects", true, 0, 22);
		if(TravellersGear.THAUM)
			addElement(presetList, invConfig, slots+9, 64,30, "Vis Discounts", true, 136,89);
		presets.put("Book", new InvPreset(invTextures[0],presetList));


		presetList = new ArrayList();
		//ARMOR
		addElement(presetList, invConfig, 0, 18,18, "Helmet", false, 64,22);
		addElement(presetList, invConfig, 1, 18,18, "Chestplate", false, 64,42);
		addElement(presetList, invConfig, 2, 18,18, "Leggings", false, 64,62);
		addElement(presetList, invConfig, 3, 18,18, "Boots", false, 64,82);
		//TRAVELLERS GEAR
		addElement(presetList, invConfig, 4, 18,18, "Cloak", false, 136,42);
		addElement(presetList, invConfig, 5, 18,18, "Pauldrons", false, 136,22);
		addElement(presetList, invConfig, 6, 18,18, "Vambraces", false, 136,62);
		addElement(presetList, invConfig, 7, 18,18, "Title", false, 44,22);
		slots = 8;
		if(TravellersGear.BAUBLES)
		{
			addElement(presetList, invConfig, slots+0, 18,18, "Amulet", false, 156,22);
			addElement(presetList, invConfig, slots+1, 18,18, "Ring 1", false, 156,42);
			addElement(presetList, invConfig, slots+2, 18,18, "Ring 2", false, 156,62);
			addElement(presetList, invConfig, slots+3, 18,18, "Belt", false, 156,82);
			slots+=4;
		}
		if(TravellersGear.MARI)
		{
			addElement(presetList, invConfig, slots+0, 18,18, "Mariculture Ring", false, 44,82);
			addElement(presetList, invConfig, slots+1, 18,18, "Mariculture Bracelet", false, 44,62);
			addElement(presetList, invConfig, slots+2, 18,18, "Mariculture Necklace", false, 44,42);
			slots+=3;
		}
		if(TravellersGear.TCON)
		{
			addElement(presetList, invConfig, slots+0, 18,18, "Tinkers Glove", false, 136,82);
			addElement(presetList, invConfig, slots+1, 18,18, "Tinkers Knapsack", false, 176,22).hideElement=true;
			addElement(presetList, invConfig, slots+2, 18,18, "Tinkers Heart Red", false, 176,42).hideElement=true;
			addElement(presetList, invConfig, slots+3, 18,18, "Tinkers Heart Yellow", false, 176,62).hideElement=true;
			addElement(presetList, invConfig, slots+4, 18,18, "Tinkers Heart Green", false, 176,82).hideElement=true;
			slots+=5;
		}

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addElement(presetList, invConfig, slots+j+(i+1)*9, 18,18, "Inventory "+(j+(i*9)), false, 24+j*18+(j>7?8:j>6?6:j>1?4:j>0?2:0),132+i*18);
		slots+=27;
		for (int i = 0; i < 9; ++i)
			addElement(presetList, invConfig, slots+i, 18,18, "Hotbar "+i, false, 4,22+i*18+(i/3));
		slots+=9;

		elementsNonSlotStart=slots;
		addElement(presetList, invConfig, slots+0, 54,72, "Player", true, 82,28);
		addElement(presetList, invConfig, slots+1, 80,10, "Name", true, 69,10);
		addElement(presetList, invConfig, slots+2, 76, 8, "Tile", true, 71,20);
		addElement(presetList, invConfig, slots+3, 70,20, "Experience", true, 74,100);
		addElement(presetList, invConfig, slots+4, 64,10, "Health", true, 82,110).hideElement=true;
		addElement(presetList, invConfig, slots+5, 64,10, "Armor", true, 109,110).hideElement=true;
		addElement(presetList, invConfig, slots+6, 64,10, "Speed", true, 82,120).hideElement=true;
		addElement(presetList, invConfig, slots+7, 64,10, "Attack Strength", true, 109,120).hideElement=true;
		addElement(presetList, invConfig, slots+8, 18,162,"Potion Effects", true, 196, 23);
		if(TravellersGear.THAUM)
			addElement(presetList, invConfig, slots+9, 64,30, "Vis Discounts", true, 130,101).hideElement=true;
		presets.put("Digital", new InvPreset(invTextures[1],presetList));


		presetList = new ArrayList();
		//ARMOR
		addElement(presetList, invConfig, 0, 18,18, "Helmet", false, 8,49);
		addElement(presetList, invConfig, 1, 18,18, "Chestplate", false, 8,67);
		addElement(presetList, invConfig, 2, 18,18, "Leggings", false, 8,85);
		addElement(presetList, invConfig, 3, 18,18, "Boots", false, 8,103);
		//TRAVELLERS GEAR
		addElement(presetList, invConfig, 4, 18,18, "Cloak", false, 44,31);
		addElement(presetList, invConfig, 5, 18,18, "Pauldrons", false, 80,49);
		addElement(presetList, invConfig, 6, 18,18, "Vambraces", false, 80,85);
		addElement(presetList, invConfig, 7, 18,18, "Title", false, 8,121);
		slots = 8;
		if(TravellersGear.BAUBLES)
		{
			addElement(presetList, invConfig, slots+0, 18,18, "Amulet", false, 26,31);
			addElement(presetList, invConfig, slots+1, 18,18, "Ring 1", false, 26,121);
			addElement(presetList, invConfig, slots+2, 18,18, "Ring 2", false, 44,121);
			addElement(presetList, invConfig, slots+3, 18,18, "Belt", false, 80,67);
			slots+=4;
		}
		if(TravellersGear.MARI)
		{
			addElement(presetList, invConfig, slots+0, 18,18, "Mariculture Ring", false, 62,121);
			addElement(presetList, invConfig, slots+1, 18,18, "Mariculture Bracelet", false, 80,103);
			addElement(presetList, invConfig, slots+2, 18,18, "Mariculture Necklace", false, 62,31);
			slots+=3;
		}
		if(TravellersGear.TCON)
		{
			addElement(presetList, invConfig, slots+0, 18,18, "Tinkers Glove", false, 80,121);
			addElement(presetList, invConfig, slots+1, 18,18, "Tinkers Knapsack", false, 80,31);
			addElement(presetList, invConfig, slots+2, 18,18, "Tinkers Heart Red", false, 23,180).hideElement=true;
			addElement(presetList, invConfig, slots+3, 18,18, "Tinkers Heart Yellow", false, 44,180).hideElement=true;
			addElement(presetList, invConfig, slots+4, 18,18, "Tinkers Heart Green", false, 65,180).hideElement=true;
			slots+=5;
		}

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addElement(presetList, invConfig, slots+j+(i+1)*9, 18,18, "Inventory "+(j+(i*9)), false, 192-i*18,13+j*18);
		slots+=27;
		for (int i = 0; i < 9; ++i)
			addElement(presetList, invConfig, slots+i, 18,18, "Hotbar "+i, false, 138,13+i*18);
		slots+=9;

		elementsNonSlotStart=slots;
		addElement(presetList, invConfig, slots+0, 54,72, "Player", true, 26,49);
		addElement(presetList, invConfig, slots+1, 80,10, "Name", true, 13,13);
		addElement(presetList, invConfig, slots+2, 76, 8, "Tile", true, 13,23);
		addElement(presetList, invConfig, slots+3, 70,20, "Experience", true, 18,139);
		addElement(presetList, invConfig, slots+4, 64,10, "Health", true, 21,159);
		addElement(presetList, invConfig, slots+5, 64,10, "Armor", true, 21,169);
		addElement(presetList, invConfig, slots+6, 64,10, "Speed", true, 21,179).hideElement=true;
		addElement(presetList, invConfig, slots+7, 64,10, "Attack Strength", true, 21,189).hideElement=true;
		addElement(presetList, invConfig, slots+8, 18,162,"Potion Effects", true, 109, 13);
		if(TravellersGear.THAUM)
			addElement(presetList, invConfig, slots+9, 64,30, "Vis Discounts", true, 86,175).hideElement=true;
		presets.put("Epic Quest", new InvPreset(invTextures[2],presetList));
	}

	public class InvPreset
	{
		final ResourceLocation texture;
		final List<GuiButtonMoveableElement> elements;
		public InvPreset(ResourceLocation tex, List<GuiButtonMoveableElement> e)
		{
			this.texture = tex;
			this.elements = e;
		}

	}
}