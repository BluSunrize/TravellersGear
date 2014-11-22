package travellersgear.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import travellersgear.TravellersGear;
import travellersgear.common.blocks.TileEntityArmorStand;
import travellersgear.common.inventory.ContainerArmorStand;
import travellersgear.common.network.PacketTileUpdate;

public class GuiArmorStand extends GuiContainer
{
	TileEntityArmorStand tile;
	static ResourceLocation texture = new ResourceLocation("travellersgear:textures/gui/armorStand.png");
	static List<int[]> slotOverlays = null;
	boolean styleMenu = false;

	public GuiArmorStand(InventoryPlayer inventoryPlayer, TileEntityArmorStand tile)
	{
		super(new ContainerArmorStand(inventoryPlayer, tile));
		this.ySize=199;
		this.tile = tile;

		if(slotOverlays==null)
		{
			slotOverlays = new ArrayList();
			//ARMOR
			slotOverlays.add(new int[]{ 4,22, 203,116});//HELM
			slotOverlays.add(new int[]{ 4,40, 203,134});//CHEST
			slotOverlays.add(new int[]{ 4,58, 221,116});//LEGS
			slotOverlays.add(new int[]{ 4,76, 221,134});//BOOTS
			if(TravellersGear.BAUBLES)
			{
				slotOverlays.add(new int[]{22, 4, 203,170});//AMULET
				slotOverlays.add(new int[]{22,94, 203,152});//RING 1
				slotOverlays.add(new int[]{40,94, 203,152});//RING 2
				slotOverlays.add(new int[]{76,40, 203,188});//BELT
			}
			//TRAVELLERS GEAR
			slotOverlays.add(new int[]{40, 4, 221,170});//CLOAK
			slotOverlays.add(new int[]{76,22, 221,152});//PAULDRON
			slotOverlays.add(new int[]{76,58, 221,188});//VAMBRACES
			if(TravellersGear.MARI)
			{
				slotOverlays.add(new int[]{58,94, 203,224});//RING
				slotOverlays.add(new int[]{76,76, 221,224});//BRACELET
				slotOverlays.add(new int[]{58, 4, 239,224});//NECKLACE
			}
			if(TravellersGear.TCON)
				slotOverlays.add(new int[]{76,94, 203,206});//GLOVE
			/** ARMOR STAND INV */
			//ARMOR
			slotOverlays.add(new int[]{120,22, 203,116});//HELM
			slotOverlays.add(new int[]{120,40, 203,134});//CHEST
			slotOverlays.add(new int[]{120,58, 221,116});//LEGS
			slotOverlays.add(new int[]{120,76, 221,134});//BOOTS
			if(TravellersGear.BAUBLES)
			{
				slotOverlays.add(new int[]{138,22, 203,170});//AMULET
				slotOverlays.add(new int[]{138,40, 203,152});//RING 1
				slotOverlays.add(new int[]{138,58, 203,152});//RING 2
				slotOverlays.add(new int[]{138,76, 203,188});//BELT
			}
			//TRAVELLERS GEAR
			slotOverlays.add(new int[]{156,22, 221,170});//CLOAK
			slotOverlays.add(new int[]{156,40, 221,152});//PAULDRON
			slotOverlays.add(new int[]{156,58, 221,188});//VAMBRACES
			if(TravellersGear.MARI)
			{
				slotOverlays.add(new int[]{120, 4, 203,224});//RING
				slotOverlays.add(new int[]{138, 4, 221,224});//BRACELET
				slotOverlays.add(new int[]{156, 4, 239,224});//NECKLACE
			}
			if(TravellersGear.TCON)
				slotOverlays.add(new int[]{156,76, 203,206});//GLOVE
		}
	}

	@Override
	public void initGui()
	{
		if(styleMenu)
			this.xSize = 256;
		else
			this.xSize = 176;
		super.initGui();
	}

	int foldOut = 0;
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		if(styleMenu && foldOut<80)
			foldOut++;
		if(!styleMenu && foldOut>0)
			foldOut--;
		if(foldOut>0)
			this.drawTexturedModalRect(x+174, y, 176, 0, foldOut, 114);

		this.drawTexturedModalRect(x, y, 0, 0, 176, ySize);
		GL11.glEnable(3042);
		if(!slotOverlays.isEmpty())
			for(int slot=0;slot<slotOverlays.size();slot++)
				if(this.inventorySlots.inventorySlots.get(slot) != null)
				{
					int[] xyuv = slotOverlays.get(slot);
					this.drawTexturedModalRect(guiLeft+xyuv[0]-1, guiTop+xyuv[1]-1, 184,115, 18,18);
					if( !((Slot)this.inventorySlots.inventorySlots.get(slot)).getHasStack() )
						this.drawTexturedModalRect(guiLeft+xyuv[0], guiTop+xyuv[1], xyuv[2],xyuv[3], 16,16);
				}

		GuiInventory.func_147046_a(x + 48, y + 85, 30, (float)(x + 51) - mouseX, (float)(y + 75 - 50) - mouseY, this.mc.thePlayer);

		if(styleMenu && foldOut>=80)
		{
			GL11.glPushMatrix();
			try{
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glTranslatef(x+178,y+40,1);
				GL11.glRotatef(-20, 1, 0, 0);
				GL11.glRotatef(45, 0, 1, 0);
				GL11.glScaled(15,-15,15);
				TileEntityRendererDispatcher.instance.renderTileEntityAt(getModifiedTilecopy(tile,true,true), 0.0D, 0.0D, 0.0D, 0.0F);
				GL11.glTranslatef(2.25f,0,2.25f);
				TileEntityRendererDispatcher.instance.renderTileEntityAt(getModifiedTilecopy(tile,false,true), 0.0D, 0.0D, 0.0D, 0.0F);
				GL11.glTranslatef(-2.25f,0,-2.25f);
				GL11.glTranslatef(-1f,-3.5f,1f);
				TileEntityRendererDispatcher.instance.renderTileEntityAt(getModifiedTilecopy(tile,true,false), 0.0D, 0.0D, 0.0D, 0.0F);
				GL11.glTranslatef(2.25f,0.0625f,2.25f);
				TileEntityRendererDispatcher.instance.renderTileEntityAt(getModifiedTilecopy(tile,false,false), 0.0D, 0.0D, 0.0D, 0.0F);
			}catch(Exception e)
			{
				e.printStackTrace();
				GL11.glPopMatrix();
			}
			GL11.glEnable(32826);
			GL11.glPopMatrix();
		}
	}

	static TileEntityArmorStand getModifiedTilecopy(TileEntityArmorStand base, boolean baub, boolean trav)
	{
		TileEntityArmorStand copy = new TileEntityArmorStand();
		copy.Inv = base.Inv;
		copy.renderBaubles = baub;
		copy.renderTravellersGear = trav;
		return copy;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		List<String> l = new ArrayList();
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		if(mouseX>x+102&&mouseX<x+110 && mouseY>y+26&&mouseY<y+34)
			l.add(StatCollector.translateToLocal("TG.guitext.showHelmet"));
		if(tile.renderHelmet)
			this.drawString(fontRendererObj, "\u2714", 103, 25, 0xffffff);

		if(mouseX>x+102&&mouseX<x+110 && mouseY>y+44&&mouseY<y+52)
			l.add(StatCollector.translateToLocal("TG.guitext.showChest"));
		if(tile.renderChest)
			this.drawString(fontRendererObj, "\u2714", 103, 43, 0xffffff);

		if(mouseX>x+102&&mouseX<x+110 && mouseY>y+62&&mouseY<y+70)
			l.add(StatCollector.translateToLocal("TG.guitext.showLegs"));
		if(tile.renderLegs)
			this.drawString(fontRendererObj, "\u2714", 103, 61, 0xffffff);

		if(mouseX>x+102&&mouseX<x+110 && mouseY>y+80&&mouseY<y+88)
			l.add(StatCollector.translateToLocal("TG.guitext.showBoots"));
		if(tile.renderBoots)
			this.drawString(fontRendererObj, "\u2714", 103, 79, 0xffffff);

		if(mouseX>x+102&&mouseX<x+110 && mouseY>y+98&&mouseY<y+106)
			l.add(StatCollector.translateToLocal("TG.guitext.showPlate"));
		if(tile.renderFloor)
			this.drawString(fontRendererObj, "\u2714", 103, 97, 0xffffff);

		if(styleMenu&&foldOut>=80)
		{
			if(mouseX>x+176&&mouseX<x+204 && mouseY>y+4&&mouseY<y+44)
			{
				String s = StatCollector.translateToLocal("TG.guitext.renderBaubTG");
				for(String s2:s.split(" "))
					l.add(s2);
			}
			if(mouseX>x+222&&mouseX<x+250 && mouseY>y+8&&mouseY<y+44)
			{
				String s = StatCollector.translateToLocal("TG.guitext.renderTG");
				for(String s2:s.split(" "))
					l.add(s2);
			}
			if(mouseX>x+176&&mouseX<x+204 && mouseY>y+64&&mouseY<y+102)
			{
				String s = StatCollector.translateToLocal("TG.guitext.renderBaub");
				for(String s2:s.split(" "))
					l.add(s2);
			}
			if(mouseX>x+222&&mouseX<x+250 && mouseY>y+64&&mouseY<y+102)
			{
				String s = StatCollector.translateToLocal("TG.guitext.renderNoBaubTG");
				for(String s2:s.split(" "))
					l.add(s2);
			}
		}
		this.drawCenteredString(fontRendererObj, StatCollector.translateToLocal("TG.guitext.style"), 147, 98, 0xffffff);


		if(!l.isEmpty())
		{
			this.drawHoveringText(l, mouseX-x, mouseY-y, this.fontRendererObj);
			RenderHelper.enableGUIStandardItemLighting();
		}
	}

	@Override
	protected void mouseClicked(int mX, int mY, int button)
	{
		super.mouseClicked(mX, mY, button);
		mX-= (width - xSize)/2;
		mY-= (height - ySize)/2;

		if(mX>=130&&mX<164 && mY>=93&&mY<111)
		{
			this.styleMenu = !this.styleMenu;
			this.initGui();
		}

		if(mX>102&&mX<110 && mY>26&&mY<34)
			tile.renderHelmet = !tile.renderHelmet;
		if(mX>102&&mX<110 && mY>44&&mY<52)
			tile.renderChest = !tile.renderChest;
		if(mX>102&&mX<110 && mY>62&&mY<70)
			tile.renderLegs = !tile.renderLegs;
		if(mX>102&&mX<110 && mY>80&&mY<88)
			tile.renderBoots = !tile.renderBoots;
		if(mX>102&&mX<110 && mY>98&&mY<106)
			tile.renderFloor = !tile.renderFloor;
		if(styleMenu&&foldOut>=80)
		{
			if(mX>176&&mX<204 && mY>4&&mY<44)
			{
				tile.renderBaubles = true;
				tile.renderTravellersGear = true;
			}
			if(mX>222&&mX<250 && mY>8&&mY<44)
			{
				tile.renderBaubles = false;
				tile.renderTravellersGear = true;
			}
			if(mX>176&&mX<204 && mY>64&&mY<102)
			{
				tile.renderBaubles = true;
				tile.renderTravellersGear = false;
			}
			if(mX>222&&mX<250 && mY>64&&mY<102)
			{
				tile.renderBaubles = false;
				tile.renderTravellersGear = false;
			}
		}
	}

	@Override
	public void onGuiClosed()
	{
		TravellersGear.instance.packetPipeline.sendToServer(new PacketTileUpdate(tile));
	}

}