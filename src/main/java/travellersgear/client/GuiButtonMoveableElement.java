package travellersgear.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiButtonMoveableElement extends GuiButton
{
	public int elementX;
	public int elementY;
	static GuiButtonMoveableElement currentDrag;
	GuiTravellersInvCustomization implementedGui;
	final boolean hideable;
	public boolean hideElement = false;
	int[] moveOffset={0,0};

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

			int priCol = (this.implementedGui!=null&&this.implementedGui.multiSelect.contains(this))?0x00ffcc:0xffcc00;
			int secCol = (this.implementedGui!=null&&this.implementedGui.multiSelect.contains(this))?0x00cc88:0xcc8800;

			FontRenderer fontrenderer = mc.fontRenderer;
			GL11.glLineWidth(2);
			Tessellator tes = Tessellator.instance;
			tes.startDrawing(2);
			tes.setColorOpaque_I(this.hideElement?0x664400:priCol);
			tes.addVertex(xPosition+.5, yPosition+.5, 0);
			tes.addVertex(xPosition+width-.5, yPosition+.5, 0);
			tes.setColorOpaque_I(this.hideElement?0x664400:secCol);
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
			else if(currentDrag==null)
			{
				currentDrag = this;
				moveOffset = new int[]{mX-xPosition , mY-yPosition};
				if(implementedGui!=null && !implementedGui.multiSelect.isEmpty())
					for(GuiButtonMoveableElement bme : implementedGui.multiSelect)
						bme.moveOffset = new int[]{mX-bme.xPosition , mY-bme.yPosition};
			}
		}
		return f;
	}
	@Override
	protected void mouseDragged(Minecraft mc, int mX, int mY)
	{
		if(currentDrag==this)
		{
			if(!GuiScreen.isCtrlKeyDown())
				this.xPosition = mX-this.moveOffset[0];
			if(!GuiScreen.isShiftKeyDown())
				this.yPosition = mY-this.moveOffset[1];
			if(implementedGui!=null)
			{
				if(!GuiScreen.isCtrlKeyDown())
					this.elementX = this.xPosition-this.implementedGui.guiLeft;
				if(!GuiScreen.isShiftKeyDown())
					this.elementY = this.yPosition-this.implementedGui.guiTop;
				if(!implementedGui.multiSelect.isEmpty())
					for(GuiButtonMoveableElement bme : implementedGui.multiSelect)
					{
						if(!GuiScreen.isCtrlKeyDown())
							bme.xPosition = mX-bme.moveOffset[0];
						if(!GuiScreen.isShiftKeyDown())
							bme.yPosition = mY-bme.moveOffset[1];
						if(bme.implementedGui!=null)
						{
							if(!GuiScreen.isCtrlKeyDown())
								bme.elementX = bme.xPosition-bme.implementedGui.guiLeft;
							if(!GuiScreen.isShiftKeyDown())
								bme.elementY = bme.yPosition-bme.implementedGui.guiTop;
						}
					}
			}
		}
	}

	public GuiButtonMoveableElement copy()
	{
		GuiButtonMoveableElement bme = new GuiButtonMoveableElement(this.id, this.xPosition, this.yPosition, this.width, this.height, this.displayString, this.hideable);
		bme.elementX = this.elementX;
		bme.elementY = this.elementY;
		bme.hideElement = this.hideElement;
		return bme;
	}

	@Override
	public void mouseReleased(int mX, int mY)
	{
		currentDrag=null;
	}
}
