package travellersgear.client.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import travellersgear.client.handlers.CustomizeableGuiHandler;

public class GuiTravellersInvCustomization extends GuiScreen
{
	EntityPlayer player;
	int xSize;
	int ySize;
	int guiLeft;
	int guiTop;
	GuiDropdownMenu presetMenu;
	GuiDropdownMenu textureMenu;

	Set<GuiButtonMoveableElement> multiSelect = new HashSet<GuiButtonMoveableElement>();

	public GuiTravellersInvCustomization(EntityPlayer player)
	{
		super();
		this.xSize = 218;
		this.ySize = 200;
		this.player = player;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		this.guiLeft = (this.width-this.xSize)/2;
		this.guiTop = (this.height-this.ySize)/2;
		this.buttonList.clear();
		for(GuiButtonMoveableElement but : CustomizeableGuiHandler.moveableInvElements)
		{
			but.xPosition = guiLeft+but.elementX;
			but.yPosition = guiTop+but.elementY;
			but.implementedGui = this;
			this.buttonList.add(but);
		}
		int start= presetMenu!=null?presetMenu.selectedOption:0;
		presetMenu = new GuiDropdownMenu(this.buttonList.size(), guiLeft+218,guiTop+20, 64,10, 100, CustomizeableGuiHandler.presets.keySet().toArray(new String[0]));
		presetMenu.selectedOption = start;
		this.buttonList.add(presetMenu);

		start= textureMenu!=null?textureMenu.selectedOption:0;
		String[] txts = new String[CustomizeableGuiHandler.invTextures.length];
		for(int t=0;t<txts.length;t++)
			txts[t] = CustomizeableGuiHandler.invTextures[t].getResourcePath().substring(CustomizeableGuiHandler.invTextures[t].getResourcePath().lastIndexOf("/")).replace("/inventory_", "");
		textureMenu = new GuiDropdownMenu(this.buttonList.size(), guiLeft+218,guiTop+120, 64,10, 100, txts);
		textureMenu.selectedOption = start;
		this.buttonList.add(textureMenu);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.equals(presetMenu))
		{
			CustomizeableGuiHandler.InvPreset ps = CustomizeableGuiHandler.presets.values().toArray(new CustomizeableGuiHandler.InvPreset[0])[presetMenu.selectedOption];
			CustomizeableGuiHandler.moveableInvElements = new ArrayList<GuiButtonMoveableElement>();
			for(GuiButtonMoveableElement bme : ps.elements)
				CustomizeableGuiHandler.moveableInvElements.add(bme.copy());
			CustomizeableGuiHandler.invTexture = ps.texture;
			for(int irl=0;irl<CustomizeableGuiHandler.invTextures.length;irl++)
				if(CustomizeableGuiHandler.invTextures[irl].equals(ps.texture))
					textureMenu.selectedOption = irl;
			this.initGui();
		}
		if(button.equals(textureMenu))
		{
			CustomizeableGuiHandler.invTexture = CustomizeableGuiHandler.invTextures[textureMenu.selectedOption];
			this.initGui();
		}
	}

	@Override
	public void drawScreen(int mX, int mY, float f)
	{
		GL11.glColor3f(1, 1, 1);
		this.drawWorldBackground(0);
		this.mc.getTextureManager().bindTexture(CustomizeableGuiHandler.invTexture);
		this.drawTexturedModalRect(guiLeft,guiTop, 0,0, xSize,ySize);
		GL11.glEnable(3042);
		super.drawScreen(mX, mY, f);
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("TG.guitext.preset")+":", guiLeft+218+32, guiTop+10, 0xffffff);
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("TG.guitext.texture")+":", guiLeft+218+32, guiTop+110, 0xffffff);
		GL11.glColor3f(1, 1, 1);
		if(rightClickPos!=null)
		{
			mc.getTextureManager().bindTexture(new ResourceLocation("travellersgear:textures/models/cloak.png"));
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glLineWidth(1);
			Tessellator tes = Tessellator.instance;
			tes.setColorOpaque_I(0xffffff);
			tes.startDrawing(2);
			tes.addVertex(rightClickPos[0]	, rightClickPos[1]	, 0);
			tes.addVertex(mX				, rightClickPos[1]	, 0);
			tes.addVertex(mX				, mY				, 0);
			tes.addVertex(rightClickPos[0]	, mY				, 0);
			tes.draw();
		}
	}

	@Override
	protected void mouseClickMove(int mX, int mY, int button, long timer)
	{
		super.mouseClickMove(mX, mY, button, timer);
		if(button==1 && rightClickPos!=null)
		{
		}
	}
	int[] rightClickPos;
	@Override
	protected void mouseClicked(int mX, int mY, int button)
	{
		super.mouseClicked(mX, mY, button);
		if(button==1)
			rightClickPos=new int[]{mX,mY};
	}
	@Override
	protected void mouseMovedOrUp(int mX, int mY, int button)
	{
		super.mouseMovedOrUp(mX, mY, button);
		if(button==1 && rightClickPos!=null)
		{
			this.multiSelect.clear();
			for(Object but : this.buttonList)
				if(but instanceof GuiButtonMoveableElement)
				{
					GuiButtonMoveableElement bme = (GuiButtonMoveableElement) but;
					if(bme.xPosition>=rightClickPos[0] && bme.xPosition+bme.width<=mX)
						if(bme.yPosition>=rightClickPos[1] && bme.yPosition+bme.height<=mY)
							this.multiSelect.add(bme);
				}
			rightClickPos=null;
		}
	}

	@Override
	public void onGuiClosed()
	{
		if(player.worldObj.isRemote)
		{
			CustomizeableGuiHandler.instance.writeElementsToConfig(CustomizeableGuiHandler.instance.invConfig);
			for(GuiButtonMoveableElement but : CustomizeableGuiHandler.moveableInvElements)
			{
				but.implementedGui = null;
			}
		}
	}
}