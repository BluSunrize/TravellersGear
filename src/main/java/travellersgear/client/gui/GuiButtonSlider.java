package travellersgear.client.gui;

import java.text.DecimalFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

public class GuiButtonSlider extends GuiButton
{
	GuiConfigDisplayItems gui;
	float valueH=0;
	boolean moveH=true;
	float valueV=0;
	boolean moveV=true;

	public float incrementStep = .03125f;
	//double steppedMove;
	boolean dragging;
	String name;
	DecimalFormat displayFormat= new DecimalFormat("0.00");

	public GuiButtonSlider(GuiConfigDisplayItems gui, int id, int x, int y, int width, int height, float startH, float startV)
	{
		super(id, x, y, width, height, "");
		this.gui=gui;
		this.valueH=startH;
		this.valueV=startV;
		if(valueH<0)
			moveH=false;
		if(valueV<0)
			moveV=false;
	}
	public void setDisplayFormat(String df)
	{
		this.displayFormat = new DecimalFormat(df);
	}
	public void setName(String name)
	{
		this.name = name;
	}



	@Override
	public int getHoverState(boolean par1)
	{
		return 0;
	}
	@Override
	protected void mouseDragged(Minecraft mc, int x, int y)
	{
		if (this.visible)
		{
			if (this.dragging)
			{
				if(moveH)
				{
					float newValue = (x-(this.xPosition + 4)) / (float)(this.width - 8);
					if(GuiScreen.isShiftKeyDown())
						newValue -= newValue%incrementStep;
					this.valueH = newValue;
					if (this.valueH < 0)
						this.valueH = 0;
					if (this.valueH > 1)
						this.valueH = 1;
				}
				if(moveV)
				{
					float newValue = (y-(this.yPosition + 4)) / (float)(this.height - 8);
					if(GuiScreen.isShiftKeyDown())
						newValue -= newValue%incrementStep;
					this.valueV = newValue;
					if (this.valueV < 0)
						this.valueV = 0;
					if (this.valueV > 1)
						this.valueV = 1;
				}
				//this.displayString = par1Minecraft.gameSettings.getKeyBinding(this.idFloat);
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			int bWidth = moveH?8:this.width;
			int bHeight = moveV?8:this.height;
			int bX = xPosition+ (!moveH?width/2: (int)(valueH*(width-8)+bWidth/2));
			int bY = yPosition+ (!moveV?height/2: (int)(valueV*(height-8)+bHeight/2));
			this.drawTexturedModalRect(bX-bWidth/2, bY-bHeight/2, 0,66, bWidth/2,bHeight/2);
			this.drawTexturedModalRect(bX-bWidth/2, bY, 0,86-bHeight/2, bWidth/2,bHeight/2);
			this.drawTexturedModalRect(bX, bY-bHeight/2, 200-bWidth/2,66, bWidth/2,bHeight/2);
			this.drawTexturedModalRect(bX, bY, 200-bWidth/2,86-bHeight/2, bWidth/2,bHeight/2);
		}	
	}
	public boolean mouseOnButton(Minecraft par1Minecraft, int par2, int par3)
	{
		return par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
	}
	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = par1Minecraft.fontRenderer;
			par1Minecraft.getTextureManager().bindTexture(buttonTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
			int k = this.getHoverState(this.field_146123_n);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + k * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
			this.mouseDragged(par1Minecraft, par2, par3);
			int l = 14737632;

//			if (!this.enabled)
//			{
//				l = -6250336;
//			}
//			else if (this.field_146123_n)
//			{
//				l = 16777120;
//			}

			//String toWrite = this.name +": "+displayFormat.format(invert && this.getValueScaled()!=0 ? this.getValueScaled()*(-1) : this.getValueScaled());

			//			int charPerLine = this.width/6;
			//			int iSMax = Math.max(1, toWrite.length()/charPerLine + (toWrite.length()%charPerLine != 0? 1 : 0));
			//			if(iSMax>1)
			//			{
			//				this.drawCenteredString(fontrenderer, toWrite.substring(0, toWrite.indexOf(":")), this.xPosition + this.width / 2, this.yPosition + (this.height - 8)/2  - ((iSMax-1)*4), l);
			//				this.drawCenteredString(fontrenderer, toWrite.substring(toWrite.indexOf(":")+1, toWrite.length()), this.xPosition + this.width / 2, this.yPosition + (this.height - 8)/2  - ((iSMax-1)*4) + 8, l);
			//			}
			//			else
			//				this.drawCenteredString(fontrenderer, toWrite, this.xPosition + this.width / 2, this.yPosition + (this.height - 8)/2 , l);
			this.drawCenteredString(fontrenderer, displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
		}
	}
	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int x, int y)
	{
		if (super.mousePressed(par1Minecraft, x, y))
		{
			if(moveH)
			{
				float newValue = (x-(this.xPosition + 4)) / (float)(this.width - 8);
				if(GuiScreen.isShiftKeyDown())
					newValue -= newValue%incrementStep;
				this.valueH = newValue;
				if (this.valueH < 0)
					this.valueH = 0;
				if (this.valueH > 1)
					this.valueH = 1;
			}
			if(moveV)
			{
				float newValue = (y-(this.yPosition + 4)) / (float)(this.height - 8);
				if(GuiScreen.isShiftKeyDown())
					newValue -= newValue%incrementStep;
				this.valueV = newValue;
				if (this.valueV < 0)
					this.valueV = 0;
				if (this.valueV > 1)
					this.valueV = 1;
			}
			//par1Minecraft.gameSettings.setOptionFloatValue(this.idFloat, this.sliderValue);
			//this.displayString = par1Minecraft.gameSettings.getKeyBinding(this.idFloat);
			this.dragging = true;
			return true;
		}
		else
		{
			return false;
		}
	}
	@Override
	public void mouseReleased(int par1, int par2)
	{
		this.dragging = false;
		this.gui.actionPerformed(this);
	}
}