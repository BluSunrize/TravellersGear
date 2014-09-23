package travellersgear.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiDropdownMenu extends GuiButton
{
	private String[] options;
	public boolean isDropped;
	int droppedHeight;
	public int selectedOption;
	public GuiDropdownMenu(int id, int x, int y, int width, int height, int droppedHeight, String[] options)
	{
		super(id,x,y,width,height,"");
		this.droppedHeight = options.length*10;
		isDropped = false;
		this.options = options;
	}

	@Override
	public void drawButton(Minecraft mc, int mX, int mY)
	{
		FontRenderer fontrenderer = mc.fontRenderer;
		mc.getTextureManager().bindTexture(buttonTextures);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//this.field_146123_n = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
		//int k = this.getHoverState(this.field_146123_n);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		drawSizeableButton(xPosition,yPosition,width,height,0,200,46,66);
		drawSizeableButton(xPosition+width-11,yPosition,10,height,0,200,66,86);
		if(isDropped)
		{
			drawSizeableButton(xPosition,yPosition+height,width,droppedHeight,0,200,46,66);
			for(int i=0;i<options.length;i++)
			{
				String s = (mX>=xPosition&&mX<=xPosition+width && mY>=yPosition+12+i*9&&mY<=yPosition+12+(i+1)*9)?StatCollector.translateToLocal(options[i]):fontrenderer.trimStringToWidth(StatCollector.translateToLocal(options[i]), width-4);
				fontrenderer.drawString(s, xPosition+2, yPosition+12+i*9, 0xffffff);
			}
		}

		fontrenderer.drawString( isDropped?"\u25B2":"\u25BC", xPosition+width-7, yPosition+1, 0xffffff);

		if(selectedOption>=0&&selectedOption<options.length)
			fontrenderer.drawString(fontrenderer.trimStringToWidth(StatCollector.translateToLocal(options[selectedOption]),width-11), xPosition+2, yPosition+1, 0xffffff);
	}

	void drawSizeableButton(int x,int y,int w,int h,int uMin,int uMax,int vMin,int vMax)
	{
		if(h>20)
		{
			int max=(h-2)/18;
			this.drawTexturedModalRect(x,y,uMin,vMin,w-1,1);
			for(int i=0;i<max;i++)
			{
				this.drawTexturedModalRect(x    ,y+1+18*i,uMin    ,vMin+1,w/2,18);
				this.drawTexturedModalRect(x+w/2,y+1+18*i,uMax-w/2,vMin+1,w/2,18);
			}
			int r=h-max*18;
			this.drawTexturedModalRect(x    ,y+h-r,uMin    ,vMin+1,w/2,r);
			this.drawTexturedModalRect(x+w/2,y+h-r,uMax-w/2,vMin+1,w/2,r);

			this.drawTexturedModalRect(x,y+h-1,uMin,vMax-1,w-1,1);
		}
		else
		{
			this.drawTexturedModalRect(x    , y    , uMin    , vMin,     w/2, h/2);
			this.drawTexturedModalRect(x    , y+h/2, uMin    , vMax-h/2, w/2, h/2);
			this.drawTexturedModalRect(x+w/2, y    , uMax-w/2, vMin,     w/2, h/2);
			this.drawTexturedModalRect(x+w/2, y+h/2, uMax-w/2, vMax-h/2, w/2, h/2);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mX, int mY)
	{
		if(this.options.length>0)
			if(mX>=xPosition&&mX<=xPosition+width && mY>=yPosition&&mY<=yPosition+height+(isDropped?droppedHeight:0))
			{
				if(mX>=xPosition+width-10&&mX<=xPosition+width && mY>=yPosition&&mY<=yPosition+height)
				{
					this.isDropped=!this.isDropped;
					return false;
				}
				if(isDropped)
					for(int i=0;i<options.length;i++)
						if(mY>=yPosition+12+i*9&&mY<=yPosition+12+(i+1)*9)
						{
							this.selectedOption = i;
							System.out.println(this.selectedOption);
							this.isDropped=false;
							return true;
						}
			}
		return super.mousePressed(mc, mX, mY);
	}

	public String[] getOptions()
	{
		return options;
	}
}