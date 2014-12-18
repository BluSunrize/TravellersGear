package travellersgear.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

public class GuiButtonItemDisplay extends GuiButton
{
	ItemStack stack;
	static RenderItem itemRender = new RenderItem();
	public GuiButtonItemDisplay(int id, int x, int y, int width, int height, ItemStack stack)
	{
		super(id, x, y, width, height,  "");
		this.stack=stack;
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
			int k = this.getHoverState(this.field_146123_n);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + k * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
			this.mouseDragged(mc, mX, mY);

			if(stack!=null)
			{
				int offset = Math.min(width,height)-16;
				String s = fontrenderer.trimStringToWidth(stack.getDisplayName(), width-offset-23);
				if(s.length()<stack.getDisplayName().length())
					s+="...";
				this.drawString(fontrenderer, s, xPosition+offset/2+17, yPosition+6, 0xffffff);
				RenderHelper.enableGUIStandardItemLighting();
				itemRender.renderItemAndEffectIntoGUI(fontrenderer, RenderManager.instance.renderEngine, stack, xPosition+offset/2,yPosition+offset/2);
				RenderHelper.disableStandardItemLighting();
			}
		}
	}

}