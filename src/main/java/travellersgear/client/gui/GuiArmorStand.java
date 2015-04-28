package travellersgear.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import travellersgear.TravellersGear;
import travellersgear.common.blocks.TileEntityArmorStand;
import travellersgear.common.inventory.ContainerArmorStand;
import travellersgear.common.network.MessageTileUpdate;

public class GuiArmorStand extends GuiContainer
{
	TileEntityArmorStand tile;
	static ResourceLocation texture = new ResourceLocation("travellersgear:textures/gui/armorStand.png");
	static List<int[]> slotOverlays = null;
	boolean styleMenu = false;
	static final int foldOutSize = 73;

	public GuiArmorStand(InventoryPlayer inventoryPlayer, TileEntityArmorStand tile)
	{
		super(new ContainerArmorStand(inventoryPlayer, tile));
		this.ySize=199;
		this.tile = tile;

		if(slotOverlays==null)
		{
			slotOverlays = new ArrayList<int[]>();
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
		if(styleMenu && foldOut<foldOutSize)
			foldOut+=4;
		if(!styleMenu && foldOut>0)
			foldOut-=4;
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
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		List<String> l = new ArrayList<String>();
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;

		if(styleMenu&&foldOut>=foldOutSize)
		{
			if(mouseX>x+178&&mouseX<x+178+8 && mouseY>y+8&&mouseY<y+8+8)
				l.add(StatCollector.translateToLocal("TG.guitext.showPlate"));
			if(tile.renderFloor)
				this.drawString(fontRendererObj, "\u2714", 179, 7, 0xffffff);

			if(mouseX>x+191&&mouseX<x+191+8 && mouseY>y+8&&mouseY<y+8+8)
				l.add(StatCollector.translateToLocal("TG.guitext.showTable"));
			if(tile.renderTable)
				this.drawString(fontRendererObj, "\u2714", 192, 7, 0xffffff);

			for(int i=0; i<4; i++)
			{
				if(mouseX>x+178&&mouseX<x+178+8 && mouseY>y+26+18*i&&mouseY<y+26+8+18*i)
					l.add(StatCollector.translateToLocal("TG.guitext.showArmor"+i));
				if(tile.renderArmor[i])
					this.drawString(fontRendererObj, "\u2714", 179, 25+18*i, 0xffffff);
			}
			for(int i=0; i<4; i++)
			{
				if(mouseX>x+191&&mouseX<x+191+8 && mouseY>y+26+18*i&&mouseY<y+26+8+18*i)
					l.add(StatCollector.translateToLocal("TG.guitext.showBaubles"+i));
				if(tile.displayBaubles[i])
					this.drawString(fontRendererObj, "\u2714", 192, 25+18*i, 0xffffff);

				if(mouseX>x+200&&mouseX<x+200+8 && mouseY>y+26+18*i&&mouseY<y+26+8+18*i)
					l.add(StatCollector.translateToLocal("TG.guitext.renderBaubles"+i));
				if(tile.renderBaubles[i])
					this.drawString(fontRendererObj, "\u2714", 201, 25+18*i, 0xffffff);
			}
			for(int i=0; i<3; i++)
			{
				if(mouseX>x+211&&mouseX<x+211+8 && mouseY>y+26+18*i&&mouseY<y+26+8+18*i)
					l.add(StatCollector.translateToLocal("TG.guitext.showTG"+i));
				if(tile.displayTravellersGear[i])
					this.drawString(fontRendererObj, "\u2714", 212, 25+18*i, 0xffffff);

				if(mouseX>x+220&&mouseX<x+220+8 && mouseY>y+26+18*i&&mouseY<y+26+8+18*i)
					l.add(StatCollector.translateToLocal("TG.guitext.renderTG"+i));
				if(tile.renderTravellersGear[i])
					this.drawString(fontRendererObj, "\u2714", 221, 25+18*i, 0xffffff);
			}
			for(int i=0; i<3; i++)
			{
				if(mouseX>x+233&&mouseX<x+233+8 && mouseY>y+26+18*i&&mouseY<y+26+8+18*i)
					l.add(StatCollector.translateToLocal("TG.guitext.showMari"+i));
				if(tile.renderMari[i])
					this.drawString(fontRendererObj, "\u2714", 234, 25+18*i, 0xffffff);
			}
		}
		this.drawCenteredString(fontRendererObj, StatCollector.translateToLocal("TG.guitext.style"), 147, 98, 0xffffff);


		if(!l.isEmpty())
		{
			int k = 0;
			for(String ss : l)
				if(fontRendererObj.getStringWidth(ss)>k)
					k=fontRendererObj.getStringWidth(ss);
			this.drawHoveringText(l, mouseX-x-(k+18), mouseY-y, this.fontRendererObj);
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

		if(styleMenu&&foldOut>=foldOutSize)
		{
			if(mX>178&&mX<178+8 && mY>8&&mY<16)
				tile.renderFloor = !tile.renderFloor;
			if(mX>194&&mX<194+8 && mY>8&&mY<16)
				tile.renderTable = !tile.renderTable;

			for(int i=0; i<4; i++)
				if(mX>178&&mX<178+8 && mY>26+18*i&&mY<34+18*i)
					tile.renderArmor[i] = !tile.renderArmor[i];
			for(int i=0; i<4; i++)
			{
				if(mX>191&&mX<191+8 && mY>26+18*i&&mY<34+18*i)
					tile.displayBaubles[i] = !tile.displayBaubles[i];
				if(mX>200&&mX<200+8 && mY>26+18*i&&mY<34+18*i)
					tile.renderBaubles[i] = !tile.renderBaubles[i];
			}
			for(int i=0; i<3; i++)
			{
				if(mX>211&&mX<211+8 && mY>26+18*i&&mY<34+18*i)
					tile.displayTravellersGear[i] = !tile.displayTravellersGear[i];
				if(mX>220&&mX<220+8 && mY>26+18*i&&mY<34+18*i)
					tile.renderTravellersGear[i] = !tile.renderTravellersGear[i];
			}
			for(int i=0; i<3; i++)
				if(mX>233&&mX<233+8 && mY>26+18*i&&mY<34+18*i)
					tile.renderMari[i] = !tile.renderMari[i];
		}
	}

	@Override
	public void onGuiClosed()
	{
		try{
			TravellersGear.packetHandler.sendToServer(new MessageTileUpdate(tile));
//			PacketPipeline.INSTANCE.sendToServer(new PacketTileUpdate(tile));

		}catch(Throwable t)
		{
			t.printStackTrace();
		}
	}

}