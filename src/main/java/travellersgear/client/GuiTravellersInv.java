package travellersgear.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeHooks;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import travellersgear.TravellersGear;
import travellersgear.api.TravellersGearAPI;
import travellersgear.common.inventory.ContainerTravellersInv;
import travellersgear.common.inventory.SlotNull;
import travellersgear.common.inventory.SlotRestricted;
import travellersgear.common.network.PacketItemShoutout;
import travellersgear.common.util.ModCompatability;

public class GuiTravellersInv extends GuiContainer
{
	private float playerRotation = 0;
	EntityPlayer player;
	static List<int[]> slotOverlays = null;
	static int playerSlotStart=-1;

	public GuiTravellersInv(EntityPlayer player)
	{
		super(new ContainerTravellersInv(player.inventory));
		this.xSize = 218;
		this.ySize = 200;
		this.player = player;
		if(playerSlotStart<=0)
			playerSlotStart= 4+4+(TravellersGear.BAUBLES?4:0)+(TravellersGear.MARI?3:0)+(TravellersGear.TCON?2:0);
		if(slotOverlays==null)
		{
			slotOverlays = new ArrayList();
			//ARMOR
			slotOverlays.add(new int[]{ 6,26, 221, 19});//HELM
			slotOverlays.add(new int[]{ 6,44, 221, 37});//CHEST
			slotOverlays.add(new int[]{ 6,62, 239, 18});//LEGS
			slotOverlays.add(new int[]{ 6,80, 239, 37});//BOOTS
			//TRAVELLERS GEAR
			slotOverlays.add(new int[]{42, 8, 239, 55});//CLOAK
			slotOverlays.add(new int[]{78,26, 239, 73});//PAULDRON
			slotOverlays.add(new int[]{78,62, 239, 91});//VAMBRACES
			slotOverlays.add(new int[]{ 6,98, 239,109});//TITLE
			if(TravellersGear.BAUBLES)
			{
				slotOverlays.add(new int[]{24, 8, 221, 55});//AMULET
				slotOverlays.add(new int[]{24,98, 221, 73});//RING 1
				slotOverlays.add(new int[]{42,98, 221, 73});//RING 2
				slotOverlays.add(new int[]{78,44, 221,91});//BELT
			}
			if(TravellersGear.MARI)
			{
				slotOverlays.add(new int[]{60,98, 221,109});//RING
				slotOverlays.add(new int[]{78,80, 221,127});//BRACELET
				slotOverlays.add(new int[]{60, 8, 239,127});//NECKLACE
			}
			if(TravellersGear.TCON)
			{
				slotOverlays.add(new int[]{78,98, 221,145});//GLOVE
				slotOverlays.add(new int[]{78, 8, 239,145});//KNAPSACK
			}
		}
	}

	@Override
	public void initGui()
	{
		super.initGui();
		this.buttonList.clear();

		for(GuiButtonMoveableElement but : ClientProxy.moveableInvElements)
		{
			but.xPosition = guiLeft+but.elementX;
			but.yPosition = guiTop+but.elementY;
		}

		int start = ClientProxy.elementsNonSlotStart;
		drawPlayer = ClientProxy.moveableInvElements.get(start+0);
		drawName = ClientProxy.moveableInvElements.get(start+1);
		drawTitle = ClientProxy.moveableInvElements.get(start+2);
		drawXP = ClientProxy.moveableInvElements.get(start+3);
		drawHealth = ClientProxy.moveableInvElements.get(start+4);
		drawArmor = ClientProxy.moveableInvElements.get(start+5);
		drawSpeed = ClientProxy.moveableInvElements.get(start+6);
		drawDamage = ClientProxy.moveableInvElements.get(start+7);
		drawPotionEffects = ClientProxy.moveableInvElements.get(start+8);
		if(TravellersGear.THAUM)
			drawVisDiscounts = ClientProxy.moveableInvElements.get(start+9);
	}

	public int[] getGuiPos()
	{
		return new int[]{guiLeft,guiTop};
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mX, int mZ)
	{
		this.mc.getTextureManager().bindTexture(ClientProxy.invTexture);
		this.drawTexturedModalRect(guiLeft,guiTop, 0,0, xSize,ySize);
		GL11.glEnable(3042);
		//this.drawTexturedModalRect(guiLeft+23,guiTop+25, 202,175, 54,72);
		for(int slot=0; slot<ClientProxy.elementsNonSlotStart; slot++)
			if(slot<this.inventorySlots.inventorySlots.size() && !(this.inventorySlots.inventorySlots.get(slot) instanceof SlotNull))
			{
				GuiButtonMoveableElement bme = ClientProxy.moveableInvElements.get(slot);
				this.drawTexturedModalRect(bme.xPosition, bme.yPosition, 220,0, 18,18);
				if(slot<playerSlotStart && !((Slot)this.inventorySlots.inventorySlots.get(slot)).getHasStack())
				{
					int[] xyuv = slotOverlays.get(slot);
					this.drawTexturedModalRect(bme.xPosition+1, bme.yPosition+1, xyuv[2],xyuv[3], 16,16);
				}

			}

		//		if(!slotOverlays.isEmpty())
		//			for(int slot=0;slot<slotOverlays.size();slot++)
		//			{
		//				int[] xyuv = slotOverlays.get(slot);
		//				this.drawTexturedModalRect(guiLeft+xyuv[0]-1, guiTop+xyuv[1]-1, 202,13, 18,18);
		//				if( !((Slot)this.inventorySlots.inventorySlots.get(slot)).getHasStack() )
		//					this.drawTexturedModalRect(guiLeft+xyuv[0], guiTop+xyuv[1], xyuv[2],xyuv[3], 16,16);
		//			}
		if(!drawPlayer.hideElement)
		{
			this.drawTexturedModalRect(drawPlayer.xPosition, drawPlayer.yPosition+drawPlayer.height-9, 202,247, 54,9);
			renderLiving(drawPlayer.xPosition+drawPlayer.width/2, drawPlayer.yPosition+drawPlayer.height-7, 30, playerRotation, this.mc.thePlayer);
		}
	}

	GuiButtonMoveableElement drawPlayer;
	GuiButtonMoveableElement drawName;
	GuiButtonMoveableElement drawTitle;
	GuiButtonMoveableElement drawHealth;
	GuiButtonMoveableElement drawArmor;
	GuiButtonMoveableElement drawXP;
	GuiButtonMoveableElement drawSpeed;
	GuiButtonMoveableElement drawDamage;
	GuiButtonMoveableElement drawPotionEffects;
	GuiButtonMoveableElement drawVisDiscounts;
	@Override
	protected void drawGuiContainerForegroundLayer(int mX, int mY)
	{
		float scale;

		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glColor3f(1, 1, 1);
		/**DRAW ICONS*/
		this.mc.getTextureManager().bindTexture(ClientProxy.invTexture);
		//HEALTH
		if(!drawHealth.hideElement)
		{
			this.drawTexturedModalRect(drawHealth.elementX,drawHealth.elementY, 220,162, 9,9);
		}
		//ARMOR
		if(!drawArmor.hideElement)
			this.drawTexturedModalRect(drawArmor.elementX,drawArmor.elementY, 229,162, 9,9);
		//XP
		if(!drawXP.hideElement)
		{
			int xpCap = this.player.xpBarCap();
			if (xpCap > 0)
			{
				this.drawTexturedModalRect(drawXP.elementX,drawXP.elementY+6, 186,200, 70,5);
				int filled = (int)(this.player.experience * (float)(70+1));
				if (filled > 0)
					this.drawTexturedModalRect(drawXP.elementX,drawXP.elementY+6, 186,205, 70,5);
			}
		}
		//STATS
		if(!drawSpeed.hideElement)
			this.drawTexturedModalRect(drawSpeed.elementX,drawSpeed.elementY, 238,162, 9,9);
		if(!drawDamage.hideElement)
			this.drawTexturedModalRect(drawDamage.elementX,drawDamage.elementY, 247,162, 9,9);
		if(!drawPotionEffects.hideElement && !this.player.getActivePotionEffects().isEmpty())
		{
			mX -= guiLeft;
			mY -= guiTop;
			Collection col = this.player.getActivePotionEffects();

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			int k = 18;

			int j=drawPotionEffects.elementY;
			int i=drawPotionEffects.elementX;
			j=0;
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
					GL11.glColor3f(1, 1, 1);
				}
				j+=k;
			}
		}
		//ASPECTS
		if(TravellersGear.THAUM && !drawVisDiscounts.hideElement)
		{
			scale = .5f;
			GL11.glScalef(scale,scale,scale);
			ModCompatability.drawTCAspect((int)((drawVisDiscounts.elementX+00)/scale), (int)((drawVisDiscounts.elementY+10)/scale), "aer");
			ModCompatability.drawTCAspect((int)((drawVisDiscounts.elementX+00)/scale), (int)((drawVisDiscounts.elementY+20)/scale), "ignis");
			ModCompatability.drawTCAspect((int)((drawVisDiscounts.elementX+20)/scale), (int)((drawVisDiscounts.elementY+10)/scale), "terra");
			ModCompatability.drawTCAspect((int)((drawVisDiscounts.elementX+20)/scale), (int)((drawVisDiscounts.elementY+20)/scale), "aqua");
			ModCompatability.drawTCAspect((int)((drawVisDiscounts.elementX+40)/scale), (int)((drawVisDiscounts.elementY+10)/scale), "ordo");
			ModCompatability.drawTCAspect((int)((drawVisDiscounts.elementX+40)/scale), (int)((drawVisDiscounts.elementY+20)/scale), "perditio");
			GL11.glScalef(1/scale,1/scale,1/scale);
		}


		/**TEXT */
        RenderHelper.disableStandardItemLighting();
		//NAME
		if(!drawName.hideElement)
			fontRendererObj.drawString(this.player.getCommandSenderName(), drawName.elementX+(drawName.width/2)-fontRendererObj.getStringWidth(this.player.getCommandSenderName())/2, drawName.elementY, 0x777777);
		if(!drawTitle.hideElement)
			if(TravellersGearAPI.getTitleForPlayer(this.player)!=null && !TravellersGearAPI.getTitleForPlayer(this.player).isEmpty())
			{
				String s = StatCollector.translateToLocal(TravellersGearAPI.getTitleForPlayer(this.player));
				scale = .5f;
				GL11.glScaled(scale,scale,scale);
				fontRendererObj.drawString(s, (int)((drawTitle.elementX+drawTitle.width/2)/scale - fontRendererObj.getStringWidth(s)*scale), (int)(drawTitle.elementY/scale), 0x777777);
				//			fontRendererObj.drawString(s, (int)((150/scale-fontRendererObj.getStringWidth(s)/scale)), (int)(12/scale), 0x777777);
				GL11.glScaled(1/scale,1/scale,1/scale);
			}

		//HEALTH
		if(!drawHealth.hideElement)
			fontRendererObj.drawString("x"+this.player.getMaxHealth()/2, drawHealth.elementX+10, drawHealth.elementY, 0x777777);
		//ARMOR
		if(!drawArmor.hideElement)
			fontRendererObj.drawString("x"+ForgeHooks.getTotalArmorValue(this.player), drawArmor.elementX+10, drawArmor.elementY, 0x777777);
		//EXPERIENCE
		if(!drawXP.hideElement)
		{
			int xpCap = this.player.xpBarCap();
			if (xpCap > 0)
			{
				scale = .5f;
				GL11.glScalef(scale,scale,scale);
				String exp =  StatCollector.translateToLocal("TG.guitext.lvl")+": "+this.player.experienceLevel;
				fontRendererObj.drawString(exp, (int)Math.floor((drawXP.elementX+drawXP.width/2)/scale)-fontRendererObj.getStringWidth(exp)/2, (int)Math.floor(drawXP.elementY/scale), 0x33aa66);
				exp = (int)Math.floor(this.player.experience*this.player.xpBarCap())+"/"+this.player.xpBarCap();
				fontRendererObj.drawString(exp, (int)Math.floor((drawXP.elementX+drawXP.width/2)/scale)-fontRendererObj.getStringWidth(exp)/2, (int)Math.floor((drawXP.elementY+12)/scale), 0x777777);
				GL11.glScalef(1/scale,1/scale,1/scale);
			}
		}
		//STATS
		scale = 1;
		if(!drawSpeed.hideElement)
		{
			ModifiableAttributeInstance attr = ((ModifiableAttributeInstance)this.player.getEntityAttribute(SharedMonsterAttributes.movementSpeed));
			fontRendererObj.drawString( (int) (attr.getAttributeValue()*1000)+"%", (int)(drawSpeed.elementX+10/scale),(int)((drawSpeed.elementY)/scale), 0x777777);
		}
		if(!drawDamage.hideElement)
			fontRendererObj.drawString( Math.round(TravellersGearAPI.getTravellersNBTData(player).getDouble("info_playerDamage")*100)+"%", (int)(drawDamage.elementX+10/scale),(int)((drawDamage.elementY)/scale), 0x777777);
		//ASPECTS
		if(TravellersGear.THAUM && !drawVisDiscounts.hideElement)
		{
			fontRendererObj.drawString( StatCollector.translateToLocal("TG.guitext.visDiscount")+":", (int)(drawVisDiscounts.elementX/scale),(int)((drawVisDiscounts.elementY)/scale), 0x777777);
			scale = .5f;
			GL11.glScalef(scale,scale,scale);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "aer")*100)+"%", (int)Math.floor((drawVisDiscounts.elementX+10)/scale), (int)Math.floor((drawVisDiscounts.elementY+12)/scale), 0x777777);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "ignis")*100)+"%", (int)Math.floor((drawVisDiscounts.elementX+10)/scale), (int)Math.floor((drawVisDiscounts.elementY+22)/scale), 0x777777);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "terra")*100)+"%", (int)Math.floor((drawVisDiscounts.elementX+30)/scale), (int)Math.floor((drawVisDiscounts.elementY+12)/scale), 0x777777);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "aqua")*100)+"%", (int)Math.floor((drawVisDiscounts.elementX+30)/scale), (int)Math.floor((drawVisDiscounts.elementY+22)/scale), 0x777777);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "ordo")*100)+"%", (int)Math.floor((drawVisDiscounts.elementX+50)/scale), (int)Math.floor((drawVisDiscounts.elementY+12)/scale), 0x777777);
			fontRendererObj.drawString((int)(ModCompatability.getTCVisDiscount(this.player, "perditio")*100)+"%", (int)Math.floor((drawVisDiscounts.elementX+50)/scale), (int)Math.floor((drawVisDiscounts.elementY+22)/scale), 0x777777);
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
		if(!drawPlayer.hideElement && mX>=drawPlayer.xPosition&&mX<=drawPlayer.xPosition+drawPlayer.width && mY>=drawPlayer.yPosition+drawPlayer.height-9&&mY<=drawPlayer.yPosition+drawPlayer.height )
		{
			mX-=drawPlayer.xPosition;
			mY-=drawPlayer.yPosition;
			if(mX>=0&&mX<=16)
				this.playerRotation += 22.5f;
			if(mX>=(drawPlayer.width-16)&&mX<=(drawPlayer.width))
				this.playerRotation -= 22.5f;
		}
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