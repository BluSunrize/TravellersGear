package travellersgear.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import travellersgear.TravellersGear;

public class GuiTravellersInvCustomization extends GuiScreen
{
	EntityPlayer player;
	int xSize;
	int ySize;
	int guiLeft;
	int guiTop;
	GuiDropdownMenu presetMenu;
	GuiDropdownMenu textureMenu;

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
		for(GuiButtonMoveableElement but : ClientProxy.moveableInvElements)
		{
			but.xPosition = guiLeft+but.elementX;
			but.yPosition = guiTop+but.elementY;
			this.buttonList.add(but);
		}
		int start= presetMenu!=null?presetMenu.selectedOption:0;
		presetMenu = new GuiDropdownMenu(this.buttonList.size(), guiLeft+218,guiTop+20, 64,10, 100, ClientProxy.presets.keySet().toArray(new String[0]));
		presetMenu.selectedOption = start;
		this.buttonList.add(presetMenu);

		start= textureMenu!=null?textureMenu.selectedOption:0;
		String[] txts = new String[ClientProxy.invTextures.length];
		for(int t=0;t<txts.length;t++)
			txts[t] = ClientProxy.invTextures[t].getResourcePath().substring(ClientProxy.invTextures[t].getResourcePath().lastIndexOf("/")).replace("/inventory_", "");
		textureMenu = new GuiDropdownMenu(this.buttonList.size(), guiLeft+218,guiTop+120, 64,10, 100, txts);
		textureMenu.selectedOption = start;
		this.buttonList.add(textureMenu);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.equals(presetMenu))
		{
			ClientProxy.InvPreset ps = ClientProxy.presets.values().toArray(new ClientProxy.InvPreset[0])[presetMenu.selectedOption];
			ClientProxy.moveableInvElements = ps.elements;
			ClientProxy.invTexture = ps.texture;
			for(int irl=0;irl<ClientProxy.invTextures.length;irl++)
				if(ClientProxy.invTextures[irl].equals(ps.texture))
					textureMenu.selectedOption = irl;
			this.initGui();
		}
		if(button.equals(textureMenu))
		{
			ClientProxy.invTexture = ClientProxy.invTextures[textureMenu.selectedOption];
			this.initGui();
		}
	}

	@Override
	public void drawScreen(int mX, int mY, float f)
	{
		this.drawWorldBackground(0);
		this.mc.getTextureManager().bindTexture(ClientProxy.invTexture);
		this.drawTexturedModalRect(guiLeft,guiTop, 0,0, xSize,ySize);
		GL11.glEnable(3042);
		super.drawScreen(mX, mY, f);
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("TG.guitext.preset")+":", guiLeft+218+32, guiTop+10, 0xffffff);
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("TG.guitext.texture")+":", guiLeft+218+32, guiTop+110, 0xffffff);
		GL11.glColor3f(1, 1, 1);
	}

	@Override
	public void onGuiClosed()
	{
		if(player.worldObj.isRemote)
			((ClientProxy)TravellersGear.proxy).writeElementsToConfig(((ClientProxy)TravellersGear.proxy).invConfig);
	}
}