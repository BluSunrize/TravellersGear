package travellersgear.client.gui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import travellersgear.api.TravellersGearAPI;
import travellersgear.client.ClientProxy;
import travellersgear.client.ToolDisplayInfo;
import travellersgear.common.network.PacketNBTSync;
import travellersgear.common.network.PacketPipeline;

public class GuiConfigDisplayItems extends GuiScreen
{
	float playerRotationH;
	float playerRotationV;
	EntityPlayer player;
	ToolDisplayInfo[] tools;
	int sel=-1;

	public GuiConfigDisplayItems(EntityPlayer player)
	{
		this.player = player;

		NBTTagCompound tgTag = TravellersGearAPI.getTravellersNBTData(player);
		tools = new ToolDisplayInfo[0];
		if(tgTag.hasKey("toolDisplay"))
		{
			NBTTagList list = tgTag.getTagList("toolDisplay", 10);
			tools = new ToolDisplayInfo[list.tagCount()];
			for(int i=0; i<list.tagCount(); i++)
				tools[i] = ToolDisplayInfo.readFromNBT(list.getCompoundTagAt(i));
		}
	}

	@Override
	public void initGui()
	{
		this.buttonList.clear();
		if(tools.length<6)
			this.buttonList.add(new GuiButton(0, 20,10, 40,20, StatCollector.translateToLocal("TG.guitext.add")));

		if(sel>=0 && tools.length>sel && tools[sel]!=null)
		{
			this.buttonList.add(new GuiButton(1, 60,10, 40,20, StatCollector.translateToLocal("TG.guitext.rem")));

			float[] trn = tools[sel].translation;
			this.buttonList.add( new GuiButtonSlider(this, 2, width-100,height-170, 80,10, (trn[0]+1)/2f, -1) );
			this.buttonList.add( new GuiButtonSlider(this, 3, width-100,height-160, 80,10, (trn[1]+1)/2f, -1) );
			this.buttonList.add( new GuiButtonSlider(this, 4, width-100,height-150, 80,10, (trn[2]+1)/2f, -1) );

			float[] rot = tools[sel].rotation;
			this.buttonList.add( new GuiButtonSlider(this, 5, width-100,height-120, 80,10, (rot[0]+180)/360f, -1) );
			this.buttonList.add( new GuiButtonSlider(this, 6, width-100,height-110, 80,10, (rot[1]+180)/360f, -1) );
			this.buttonList.add( new GuiButtonSlider(this, 7, width-100,height-100, 80,10, (rot[2]+180)/360f, -1) );

			GuiButtonSlider sl =  new GuiButtonSlider(this, 8, width-100,height-70, 80,10, (tools[sel].scale[0]+2)/4f, -1);
			sl.incrementStep = .015625f;
			this.buttonList.add(sl);
			sl = new GuiButtonSlider(this, 9, width-100,height-60, 80,10, (tools[sel].scale[1]+2)/4f, -1);
			sl.incrementStep = .015625f;
			this.buttonList.add(sl);
			sl = new GuiButtonSlider(this,10, width-100,height-50, 80,10, (tools[sel].scale[2]+2)/4f, -1);
			sl.incrementStep = .015625f;
			this.buttonList.add(sl);

			this.buttonList.add( new GuiButton(11, width-100,height-30, 40,20, tools[sel].hideWhenEquipped?EnumChatFormatting.DARK_GREEN+"\u2714":EnumChatFormatting.DARK_RED+"\u2716") );
			this.buttonList.add( new GuiButton(12, width- 60,height-30, 40,20, tools[sel].rotateWithHead?EnumChatFormatting.DARK_GREEN+"\u2714":EnumChatFormatting.DARK_RED+"\u2716") );

			for(int i=0; i<player.inventory.mainInventory.length; i++)
			{
				int bx = width-105 + i%9*10;
				int by = i<9?height-200: height-245+ i/9*10;
				GuiButtonToggleableOutline but = new GuiButtonToggleableOutline(1000+i, bx,by, 10,10, tools[sel].slot==i?0x00aa00:0xaaaaaa);
				but.stack = player.inventory.getStackInSlot(i);
				this.buttonList.add(but);
			}
		}

		for(int i=0; i<tools.length; i++)
			this.buttonList.add(new GuiButtonItemDisplay(100+i, 20,40+i*30, 80,20, player.inventory.getStackInSlot(tools[i].slot)));
	}
	@Override
	public void onGuiClosed()
	{
		ClientProxy.toolDisplayMap.put(player.getCommandSenderName(), tools);
		NBTTagList list = new NBTTagList();
		for(int i=0; i<tools.length; i++)
			list.appendTag(tools[i].writeToNBT());
		TravellersGearAPI.getTravellersNBTData(player).setTag("toolDisplay", list);
		PacketPipeline.INSTANCE.sendToServer(new PacketNBTSync(player));
	}

	boolean usingButton = false;
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id==0)
		{
			ToolDisplayInfo[] newTools = new ToolDisplayInfo[tools.length+1];
			System.arraycopy(tools,0, newTools,0, tools.length);
			newTools[tools.length] = new ToolDisplayInfo(0, new float[]{0,0,0}, new float[]{0,0,0}, new float[]{1,1,1});
			sel = tools.length;
			tools = newTools;
			ClientProxy.toolDisplayMap.put(player.getCommandSenderName(), tools);
			this.initGui();
		}
		else if(button.id==1 && sel>=0 && tools[sel]!=null)
		{
			List<ToolDisplayInfo> newTools = new ArrayList(Arrays.asList(tools));
			newTools.remove(sel);
			tools = newTools.toArray(new ToolDisplayInfo[0]);
			sel = -1;
			ClientProxy.toolDisplayMap.put(player.getCommandSenderName(), tools);
			this.initGui();
		}
		else if(button.id>=1000)
		{
			if(sel>=0 && tools[sel]!=null)
				if(button.id-1000>=0 && button.id-1000 < player.inventory.mainInventory.length)
				{
					tools[sel].slot=button.id-1000;
					ClientProxy.toolDisplayMap.put(player.getCommandSenderName(), tools);
				}
			this.initGui();
		}
		else if(button.id>=100)
		{
			if(button.id-100<tools.length)
				sel = button.id-100;
			this.initGui();
		}
		else if(sel>=0 && tools[sel]!=null)
		{
			if(button instanceof GuiButtonSlider)
			{
				GuiButtonSlider slider = (GuiButtonSlider)button;
				if(button.id==2)
					tools[sel].translation[0] = -1f +slider.valueH*2;
				if(button.id==3)
					tools[sel].translation[1] = -1f +slider.valueH*2;
				if(button.id==4)
					tools[sel].translation[2] = -1f +slider.valueH*2;

				if(button.id==5)
					tools[sel].rotation[0] = -180f +slider.valueH*360;
				if(button.id==6)
					tools[sel].rotation[1] = -180f +slider.valueH*360;
				if(button.id==7)
					tools[sel].rotation[2] = -180f +slider.valueH*360;

				if(button.id==8)
					tools[sel].scale[0] = slider.valueH*4-2;
				if(button.id==9)
					tools[sel].scale[1] = slider.valueH*4-2;
				if(button.id==10)
					tools[sel].scale[2] = slider.valueH*4-2;

				usingButton = !usingButton;
			}
			else if(button.id==11)
			{
				tools[sel].hideWhenEquipped = !tools[sel].hideWhenEquipped;
				button.displayString = tools[sel].hideWhenEquipped?EnumChatFormatting.DARK_GREEN+"\u2714":EnumChatFormatting.DARK_RED+"\u2716";
			}
			else if(button.id==12)
			{
				tools[sel].rotateWithHead = !tools[sel].rotateWithHead;
				button.displayString = tools[sel].rotateWithHead?EnumChatFormatting.DARK_GREEN+"\u2714":EnumChatFormatting.DARK_RED+"\u2716";
			}

			ClientProxy.toolDisplayMap.put(player.getCommandSenderName(), tools);
		}

	}

	@Override
	public void drawScreen(int mX, int mY, float f)
	{
		this.drawDefaultBackground();
		super.drawScreen(mX, mY, f);

		this.drawPlayer(width/2, height/5*4, 80, 0, 0, player);

		if(sel>=0 && tools.length>sel && tools[sel]!=null)
		{
			DecimalFormat df = new DecimalFormat("0.000");


			this.drawString(fontRendererObj, StatCollector.translateToLocal("TG.guitext.translation"), width-90,height-180, 0xffffff);
			this.drawCenteredString(fontRendererObj, df.format(tools[sel].translation[0]), width-120,height-168, 0xffffff);
			this.drawCenteredString(fontRendererObj, df.format(tools[sel].translation[1]), width-120,height-158, 0xffffff);
			this.drawCenteredString(fontRendererObj, df.format(tools[sel].translation[2]), width-120,height-148, 0xffffff);

			this.drawString(fontRendererObj, StatCollector.translateToLocal("TG.guitext.rotation"), width-90,height-130, 0xffffff);
			this.drawCenteredString(fontRendererObj, df.format(tools[sel].rotation[0]), width-120,height-118, 0xffffff);
			this.drawCenteredString(fontRendererObj, df.format(tools[sel].rotation[1]), width-120,height-108, 0xffffff);
			this.drawCenteredString(fontRendererObj, df.format(tools[sel].rotation[2]), width-120,height- 98, 0xffffff);

			this.drawString(fontRendererObj, StatCollector.translateToLocal("TG.guitext.scale"), width-90,height- 80, 0xffffff);
			this.drawCenteredString(fontRendererObj, df.format(tools[sel].scale[0]), width-120,height- 68, 0xffffff);
			this.drawCenteredString(fontRendererObj, df.format(tools[sel].scale[1]), width-120,height- 58, 0xffffff);
			this.drawCenteredString(fontRendererObj, df.format(tools[sel].scale[2]), width-120,height- 48, 0xffffff);

			GuiButton but = (GuiButton)this.buttonList.get(11);
			if(mX>=but.xPosition && mY>=but.yPosition && mX<but.xPosition+but.width && mY<but.yPosition+but.height)
				this.drawHoveringText(Arrays.asList(StatCollector.translateToLocal("TG.guitext.hideWhenEquipped")), mX,mY, this.fontRendererObj);
			but = (GuiButton)this.buttonList.get(12);
			if(mX>=but.xPosition && mY>=but.yPosition && mX<but.xPosition+but.width && mY<but.yPosition+but.height)
				this.drawHoveringText(Arrays.asList(StatCollector.translateToLocal("TG.guitext.rotateWithHead")), mX,mY, this.fontRendererObj);
		}
	}

	int prevDragX;
	int prevDragY;
	@Override
	protected void mouseClickMove(int mX, int mY, int button, long time)
	{
		if(!usingButton)
		{
			if(button==0)
			{
				playerRotationH-=prevDragX-mX;
				prevDragX=mX;
			}
			if(button==1)
			{
				playerRotationV+=prevDragY-mY;
				prevDragY=mY;
			}
		}
	}
	@Override
	protected void mouseClicked(int mX, int mY, int button)
	{
		super.mouseClicked(mX, mY, button);
		prevDragX=mX;
		prevDragY=mY;
	}

	public void drawPlayer(int x, int y, int scale, float yaw, float pitch, EntityLivingBase player)
	{
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, 100.0F);
		GL11.glScalef((float)(-scale), (float)scale, (float)scale);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float f2 = player.renderYawOffset;
		float f3 = player.rotationYaw;
		float f4 = player.rotationPitch;
		float f5 = player.prevRotationYawHead;
		float f6 = player.rotationYawHead;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float)Math.atan((double)(pitch / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		player.renderYawOffset = (float)Math.atan((double)(yaw / 40.0F)) * 20.0F;
		player.rotationYaw = (float)Math.atan((double)(yaw / 40.0F)) * 40.0F;
		player.rotationPitch = -((float)Math.atan((double)(pitch / 40.0F))) * 20.0F;
		player.rotationYawHead = player.rotationYaw;
		player.prevRotationYawHead = player.rotationYaw;
		GL11.glTranslatef(0.0F, player.yOffset, 0.0F);
		RenderManager.instance.playerViewY = 180.0F;
		GL11.glTranslatef(0, -.5f, 0);
		GL11.glRotatef(playerRotationH, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(playerRotationV, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0, .5f, 0);
		RenderManager.instance.renderEntityWithPosYaw(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		player.renderYawOffset = f2;
		player.rotationYaw = f3;
		player.rotationPitch = f4;
		player.prevRotationYawHead = f5;
		player.rotationYawHead = f6;
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
}