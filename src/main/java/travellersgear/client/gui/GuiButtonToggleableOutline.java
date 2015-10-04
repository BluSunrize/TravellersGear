package travellersgear.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

public class GuiButtonToggleableOutline extends GuiButton
{
	public int colour = 0xaaaaaa;
	public ItemStack stack=null;
	static RenderItem itemRender = new RenderItem();
	public GuiButtonToggleableOutline(int id, int x, int y, int width, int height, int colour)
	{
		super(id, x, y, width, height, "");
		this.colour = colour;
	}

	@Override
	public void drawButton(Minecraft mc, int mX, int mY)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(buttonTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_146123_n = mX >= this.xPosition && mY >= this.yPosition && mX < this.xPosition + this.width && mY < this.yPosition + this.height;
			this.mouseDragged(mc, mX, mY);

			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			Tessellator tes = Tessellator.instance;
			tes.startDrawing(2);
			tes.setColorOpaque_I(colour);
			tes.addVertex(xPosition, yPosition, colour!=0xaaaaaa?20:0);
			tes.addVertex(xPosition+width, yPosition, colour!=0xaaaaaa?20:0);
			tes.addVertex(xPosition+width, yPosition+height, colour!=0xaaaaaa?20:0);
			tes.addVertex(xPosition, yPosition+height, colour!=0xaaaaaa?20:0);
			tes.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			if(stack!=null)
			{
				RenderHelper.enableGUIStandardItemLighting();
				float scale = (Math.min(width,height)-2)/16f;
				GL11.glScalef(scale,scale,scale);
				GL11.glTranslated((xPosition+1)/scale, (yPosition+1)/scale, 0);
				itemRender.renderItemAndEffectIntoGUI(fontrenderer, RenderManager.instance.renderEngine, stack, 0,0);
				GL11.glTranslated(-(xPosition+1)/scale,-(yPosition+1)/scale, 0);
				GL11.glScalef(1/scale,1/scale,1/scale);
				RenderHelper.disableStandardItemLighting();
			}
		}
	}
}