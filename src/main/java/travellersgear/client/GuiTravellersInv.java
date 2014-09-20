package travellersgear.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeHooks;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import travellersgear.TravellersGear;
import travellersgear.api.TravellersGearAPI;
import travellersgear.common.inventory.ContainerTravellersInv;
import travellersgear.common.inventory.SlotRestricted;
import travellersgear.common.network.PacketItemShoutout;
import travellersgear.common.util.ModCompatability;

public class GuiTravellersInv extends GuiContainer
{
	static ResourceLocation texture = new ResourceLocation("travellersgear","textures/gui/inventory.png");
	private float playerRotation = 0;
	EntityPlayer player;
	static List<int[]> slotOverlays = null;

	public GuiTravellersInv(EntityPlayer player)
	{
		super(new ContainerTravellersInv(player.inventory));
		this.xSize = 200;
		this.ySize = 197;
		this.player = player;
		if(slotOverlays==null)
		{
			slotOverlays = new ArrayList();
			//ARMOR
			slotOverlays.add(new int[]{ 6,26, 203, 32});//HELM
			slotOverlays.add(new int[]{ 6,44, 203, 50});//CHEST
			slotOverlays.add(new int[]{ 6,62, 221, 32});//LEGS
			slotOverlays.add(new int[]{ 6,80, 221, 50});//BOOTS
			//TRAVELLERS GEAR
			slotOverlays.add(new int[]{42, 8, 221, 86});//CLOAK
			slotOverlays.add(new int[]{78,26, 221, 68});//PAULDRON
			slotOverlays.add(new int[]{78,62, 221,104});//VAMBRACES
			slotOverlays.add(new int[]{ 6,98, 221,122});//TITLE
			if(TravellersGear.BAUBLES)
			{
				slotOverlays.add(new int[]{24, 8, 203, 86});//AMULET
				slotOverlays.add(new int[]{24,98, 203, 68});//RING 1
				slotOverlays.add(new int[]{42,98, 203, 68});//RING 2
				slotOverlays.add(new int[]{78,44, 203,104});//BELT
			}
			if(TravellersGear.MARI)
			{
				slotOverlays.add(new int[]{60,98, 203,140});//RING
				slotOverlays.add(new int[]{78,80, 221,140});//BRACELET
				slotOverlays.add(new int[]{60, 8, 239,140});//NECKLACE
			}
			if(TravellersGear.TCON)
			{
				slotOverlays.add(new int[]{78,98, 203,122});//GLOVE
				slotOverlays.add(new int[]{78, 8, 239,122});//KNAPSACK
			}
		}
	}

	@Override
	public void initGui()
	{
		super.initGui();
	}

	public int[] getGuiPos()
	{
		return new int[]{guiLeft,guiTop};
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mX, int mZ)
	{
		this.mc.getTextureManager().bindTexture(texture);
		this.drawTexturedModalRect(guiLeft,guiTop, 0,0, xSize,ySize);
		GL11.glEnable(3042);
		this.drawTexturedModalRect(guiLeft+23,guiTop+25, 202,175, 54,72);
		if(!slotOverlays.isEmpty())
			for(int slot=0;slot<slotOverlays.size();slot++)
			{
				int[] xyuv = slotOverlays.get(slot);
				this.drawTexturedModalRect(guiLeft+xyuv[0]-1, guiTop+xyuv[1]-1, 202,13, 18,18);
				if( !((Slot)this.inventorySlots.inventorySlots.get(slot)).getHasStack() )
					this.drawTexturedModalRect(guiLeft+xyuv[0], guiTop+xyuv[1], xyuv[2],xyuv[3], 16,16);
			}
		renderLiving(guiLeft + 50, guiTop + 86, 30, playerRotation, this.mc.thePlayer);
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mX, int mY)
	{
		float scale;

		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glColor3f(1, 1, 1);
		/**DRAW ICONS*/
		this.mc.getTextureManager().bindTexture(Gui.icons);
		//HEALTH
		this.drawTexturedModalRect(118,40, 34,0, 9,9);
		this.drawTexturedModalRect(118,40, 52,0, 9,9);
		//ARMOR
		this.drawTexturedModalRect(118,50, 43,9, 9,9);
		//XP
		int xpCap = this.player.xpBarCap();
		if (xpCap > 0)
		{
			Tessellator tes = Tessellator.instance;
			tes.startDrawingQuads();
			tes.addVertexWithUV(118+00,24+8, 0, 000/256f,69/256f);
			tes.addVertexWithUV(118+64,24+8, 0, 182/256f,69/256f);
			tes.addVertexWithUV(118+64,24+0, 0, 182/256f,64/256f);
			tes.addVertexWithUV(118+00,24+0, 0, 000/256f,64/256f);
			tes.draw();

			int filled = (int)(this.player.experience * (float)(182 + 1));
			if (filled > 0)
			{
				float f = 64/182f;
				tes.startDrawingQuads();
				tes.addVertexWithUV(118+00,24+8, 0, 000/256f,74/256f);
				tes.addVertexWithUV(118+filled*f,24+8, 0, filled/256f,74/256f);
				tes.addVertexWithUV(118+filled*f,24+0, 0, filled/256f,69/256f);
				tes.addVertexWithUV(118+00,24+0, 0, 000/256f,69/256f);
				tes.draw();
			}
		}
		//STATS
		this.mc.getTextureManager().bindTexture(texture);
		this.drawTexturedModalRect(118,60, 204,157, 9,9);
		this.drawTexturedModalRect(118,70, 213,157, 9,9);
		if(!this.player.getActivePotionEffects().isEmpty())
		{
			mX -= guiLeft;
			mY -= guiTop;
			this.mc.getTextureManager().bindTexture(texture);
			Collection col = this.player.getActivePotionEffects();

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			int k = 18;

			//			if (col.size() > 5)
			//			{
			//				k = 132 / (col.size() - 1);
			//			}

			int j=-16;
			int i=-18;
			this.drawTexturedModalRect(i, j, 0,197, 100,19);
			j=0;
			for(int rep=0;rep<col.size();rep++)
				this.drawTexturedModalRect(i, j+rep*k, 0,216, 18,18);
			this.drawTexturedModalRect(i, j+col.size()*k, 0,234, 18,9);
			Iterator iterator = col.iterator();
			List<String> textList = new ArrayList();
			while(iterator.hasNext())
			{
				PotionEffect potioneffect = (PotionEffect)iterator.next();
				Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.mc.getTextureManager().bindTexture(field_147001_a);

				if (potion.hasStatusIcon())
				{
					int l = potion.getStatusIconIndex();
					this.drawTexturedModalRect(i, j, 0 + l % 8 * 18, 198 + l / 8 * 18, 18, 18);
				}

				potion.renderInventoryEffect(i, j, potioneffect, mc);
				if (!potion.shouldRenderInvText(potioneffect)) continue;

				if(mX>i && mX<=i+18 && mY>j && mY<=j+18)
				{
					String s1 = I18n.format(potion.getName(), new Object[0]);
					if (potioneffect.getAmplifier() == 1)
					{
						s1 = s1 + " " + I18n.format("enchantment.level.2", new Object[0]);
					}
					else if (potioneffect.getAmplifier() == 2)
					{
						s1 = s1 + " " + I18n.format("enchantment.level.3", new Object[0]);
					}
					else if (potioneffect.getAmplifier() == 3)
					{
						s1 = s1 + " " + I18n.format("enchantment.level.4", new Object[0]);
					}
					textList.add(s1);
					String s = Potion.getDurationString(potioneffect);
					textList.add(s);
					this.drawHoveringText(textList, mX, mY, this.fontRendererObj);
				}
				j+=k;
			}
		}
		//ASPECTS
		if(TravellersGear.THAUM)
		{
			scale = .5f;
			GL11.glScalef(scale,scale,scale);
			ModCompatability.drawTCAspect((int)(118/scale), (int)(90/scale), "aer");
			ModCompatability.drawTCAspect((int)(118/scale), (int)(100/scale), "ignis");
			ModCompatability.drawTCAspect((int)(138/scale), (int)(90/scale), "terra");
			ModCompatability.drawTCAspect((int)(138/scale), (int)(100/scale), "aqua");
			ModCompatability.drawTCAspect((int)(158/scale), (int)(90/scale), "ordo");
			ModCompatability.drawTCAspect((int)(158/scale), (int)(100/scale), "perditio");
			GL11.glScalef(1/scale,1/scale,1/scale);
		}


		/**TEXT */
		//NAME
		fontRendererObj.drawString(this.player.getCommandSenderName(), 150-fontRendererObj.getStringWidth(this.player.getCommandSenderName())/2, 6, 0x777777);
		if(TravellersGearAPI.getTitleForPlayer(this.player)!=null && !TravellersGearAPI.getTitleForPlayer(this.player).isEmpty())
		{
			String s = StatCollector.translateToLocal(TravellersGearAPI.getTitleForPlayer(this.player));
			scale = .5f;
			GL11.glScaled(scale,scale,scale);
			fontRendererObj.drawString(s, (int)(150/scale - fontRendererObj.getStringWidth(s)*scale), (int)(14/scale), 0x777777);
			//			fontRendererObj.drawString(s, (int)((150/scale-fontRendererObj.getStringWidth(s)/scale)), (int)(12/scale), 0x777777);
			GL11.glScaled(1/scale,1/scale,1/scale);
		}

		//HEALTH
		fontRendererObj.drawString("x"+this.player.getMaxHealth()/2, 128, 41, 0x777777);
		//ARMOR
		GL11.glColor3f(1,1,1);
		fontRendererObj.drawString("x"+ForgeHooks.getTotalArmorValue(this.player), 128, 51, 0x777777);
		//EXPERIENCE
		if (xpCap > 0)
		{
			scale = .5f;
			GL11.glScalef(scale,scale,scale);
			String exp = "Lvl: "+this.player.experienceLevel;
			fontRendererObj.drawString(exp, (int)Math.floor(150/scale)-fontRendererObj.getStringWidth(exp)/2, (int)Math.floor(20/scale), 0x33aa66);
			exp = (int)Math.floor(this.player.experience*this.player.xpBarCap())+"/"+this.player.xpBarCap();
			fontRendererObj.drawString(exp, (int)Math.floor(150/scale)-fontRendererObj.getStringWidth(exp)/2, (int)Math.floor(33/scale), 0x777777);
			GL11.glScalef(1/scale,1/scale,1/scale);
		}
		//STATS
		scale = 1;
		//		GL11.glScalef(scale,scale,scale);
		int y = 0;
		ModifiableAttributeInstance attr = ((ModifiableAttributeInstance)this.player.getEntityAttribute(SharedMonsterAttributes.movementSpeed));
		fontRendererObj.drawString( (int) (attr.getAttributeValue()*1000)+"%", (int)(128/scale),(int)((61+y)/scale), 0x777777);
		y+=10;
		fontRendererObj.drawString( Math.round(TravellersGearAPI.getTravellersNBTData(player).getDouble("info_playerDamage")*100)+"%", (int)(128/scale),(int)((61+y)/scale), 0x777777);
		y+=10;
		//ASPECTS
		if(TravellersGear.THAUM)
		{
			fontRendererObj.drawString( StatCollector.translateToLocal("TG.guitext.visDiscount")+":", (int)(118/scale),(int)((61+y)/scale), 0x777777);
			scale = .5f;
			GL11.glScalef(scale,scale,scale);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "aer")*100)+"%", (int)Math.floor(128/scale), (int)Math.floor(92/scale), 0x777777);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "ignis")*100)+"%", (int)Math.floor(128/scale), (int)Math.floor(102/scale), 0x777777);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "terra")*100)+"%", (int)Math.floor(148/scale), (int)Math.floor(92/scale), 0x777777);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "aqua")*100)+"%", (int)Math.floor(148/scale), (int)Math.floor(102/scale), 0x777777);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "ordo")*100)+"%", (int)Math.floor(168/scale), (int)Math.floor(92/scale), 0x777777);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "perditio")*100)+"%", (int)Math.floor(168/scale), (int)Math.floor(102/scale), 0x777777);
			GL11.glScalef(1/scale,1/scale,1/scale);
		}
	}

	Slot findSlotForPosition(int x, int y)
	{
		for (int k = 0; k < this.inventorySlots.inventorySlots.size(); ++k)
		{
			Slot slot = (Slot)this.inventorySlots.inventorySlots.get(k);
			if (this.func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, x, y))
				return slot;
		}
		return null;
	}

	@Override
	protected void mouseClicked(int mX, int mY, int eventButton)
	{
		if(eventButton == 1)
		{
			Slot slot = this.findSlotForPosition(mX, mY);
			if(slot!=null && slot.getHasStack())
			{
				if(isCtrlKeyDown())
				{
					TravellersGear.instance.packetPipeline.sendToServer(new PacketItemShoutout(this.player,slot.getStack()));
					return;
				}
				else if(slot instanceof SlotRestricted && SlotRestricted.SlotType.TINKERS_BAG==((SlotRestricted)slot).type)
				{
					ModCompatability.openTConKnapsack();
					return;
				}
			}
		}
		super.mouseClicked(mX, mY, eventButton);
		mX-=this.guiLeft;
		mY-=this.guiTop;
		if( (mX>23&&mX<39) && (mY>88&&mY<97) )
			this.playerRotation += 22.5f;
		if( (mX>61&&mX<77) && (mY>88&&mY<97) )
			this.playerRotation -= 22.5f;
	}

	public static void renderLiving(int x, int y, float scale, float xRotation, EntityLivingBase living)
	{
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, 50.0F);
		GL11.glScalef(-scale, scale, scale);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float f2 = living.renderYawOffset;
		float f3 = living.rotationYaw;
		float f4 = living.rotationPitch;
		float f5 = living.prevRotationYawHead;
		float f6 = living.rotationYawHead;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		living.renderYawOffset = xRotation;//(float)Math.atan((double)(adjustedMouseX / 40.0F)) * 20.0F;
		living.rotationYaw = xRotation;//(float)Math.atan((double)(adjustedMouseX / 40.0F)) * 40.0F;
		living.rotationPitch = 0;//-((float)Math.atan((double)(adjustedMouseY / 40.0F))) * 20.0F;
		living.rotationYawHead = living.rotationYaw;
		living.prevRotationYawHead = living.rotationYaw;
		GL11.glTranslatef(0.0F, living.yOffset, 0.0F);
		RenderManager.instance.playerViewY = 180.0F;
		RenderManager.instance.renderEntityWithPosYaw(living, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		living.renderYawOffset = f2;
		living.rotationYaw = f3;
		living.rotationPitch = f4;
		living.prevRotationYawHead = f5;
		living.rotationYawHead = f6;
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
}