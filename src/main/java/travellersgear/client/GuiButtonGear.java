package travellersgear.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiButtonGear extends GuiButton
{
	public GuiButtonGear(int id, int x, int y)
	{
		super(id, x,y, 10,10, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mX, int mY)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_146123_n = mX >= this.xPosition && mY >= this.yPosition && mX < this.xPosition + this.width && mY < this.yPosition + this.height;
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModelRectFromIcon(xPosition,yPosition, Items.book.getIconFromDamage(0), this.width,this.height);
			this.mouseDragged(mc, mX, mY);
			if (this.field_146123_n)
				this.drawCenteredString(fontrenderer, StatCollector.translateToLocal("TG.guitext.equipment"), this.xPosition+25, this.yPosition+height, 16777120);
		}
	}
}