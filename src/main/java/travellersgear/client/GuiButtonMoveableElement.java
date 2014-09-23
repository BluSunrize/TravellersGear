package travellersgear.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiButtonMoveableElement extends GuiButton
{
	public int elementX;
	public int elementY;
	boolean dragging = false;
	final boolean hideable;
	public boolean hideElement = false;

	public GuiButtonMoveableElement(int id, int x, int y, int w, int h, String name)
	{
		super(id, x, y, w, h, name);
		this.elementX = x;
		this.elementY = y;
		hideable = false;
	}
	public GuiButtonMoveableElement(int id, int x, int y, int w, int h, String name, boolean hideable)
	{
		super(id, x, y, w, h, name);
		this.elementX = x;
		this.elementY = y;
		this.hideable = hideable;
	}

	public void drawButton(Minecraft mc, int mX, int mY)
	{
		if (this.visible)
		{
			this.field_146123_n = mX >= this.xPosition && mY >= this.yPosition && mX < this.xPosition + this.width && mY < this.yPosition + this.height;
			mc.getTextureManager().bindTexture(new ResourceLocation("travellersgear:textures/models/cloak.png"));

			GL11.glEnable(GL11.GL_BLEND);
			this.mouseDragged(mc, mX, mY);

			FontRenderer fontrenderer = mc.fontRenderer;
			GL11.glLineWidth(2);
			Tessellator tes = Tessellator.instance;
			tes.startDrawing(2);
			tes.setColorOpaque_I(this.hideElement?0x664400:0xffcc00);
			tes.addVertex(xPosition+.5, yPosition+.5, 0);
			tes.addVertex(xPosition+width-.5, yPosition+.5, 0);
			tes.setColorOpaque_I(this.hideElement?0x664400:0xcc8800);
			tes.addVertex(xPosition+width-.5, yPosition+height-.5, 0);
			tes.addVertex(xPosition+.5, yPosition+height-.5, 0);
			tes.draw();
			if(this.hideable)
			{
				tes.startDrawing(2);
				tes.setColorOpaque_I(0xcccccc);
				tes.addVertex(xPosition+width-5.5, yPosition+.5, 0);
				tes.addVertex(xPosition+width-.5, yPosition+.5, 0);
				tes.addVertex(xPosition+width-.5, yPosition+5.5, 0);
				tes.addVertex(xPosition+width-5.5, yPosition+5.5, 0);
				tes.draw();
				tes.startDrawing(1);
				tes.setColorOpaque_I(0xcccccc);
				tes.addVertex(xPosition+width-5.5, yPosition+.5, 0);
				tes.addVertex(xPosition+width-.5, yPosition+5.5, 0);
				tes.draw();
				tes.startDrawing(1);
				tes.setColorOpaque_I(0xcccccc);
				tes.addVertex(xPosition+width-.5, yPosition+.5, 0);
				tes.addVertex(xPosition+width-5.5, yPosition+5.5, 0);
				tes.draw();
			}
			GL11.glTranslated(0, 0, 200);
			if(field_146123_n)
				if(this.hideable && mX>=xPosition+width-6 && mX<=xPosition+width-1 && mY>=yPosition+1 && mY<=yPosition+6)
					this.drawCenteredString(fontrenderer, "Hide", this.xPosition + this.width-3, this.yPosition + 3 / 2, 0xffffff);
				else
					this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, 0xffffff);
			GL11.glTranslated(0, 0,-200);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mX, int mY)
	{
		boolean f = super.mousePressed(mc, mX, mY);
		if(f)
		{
			if(this.hideable && mX>=xPosition+width-6 && mX<=xPosition+width-1 && mY>=yPosition+1 && mY<=yPosition+6)
				this.hideElement = !this.hideElement;
			else
				this.dragging = true;
		}
		return f;
	}
	@Override
	protected void mouseDragged(Minecraft mc, int mX, int mY)
	{
		if(dragging)
		{
			this.xPosition = mX-(width/2);
			this.yPosition = mY-(height/2);
			if(mc.currentScreen instanceof GuiTravellersInvCustomization)
			{
				this.elementX = xPosition-((GuiTravellersInvCustomization)mc.currentScreen).guiLeft;
				this.elementY = yPosition-((GuiTravellersInvCustomization)mc.currentScreen).guiTop;
			}
		}
	}

	@Override
	public void mouseReleased(int mX, int mY)
	{
		dragging = false;
	}


}
