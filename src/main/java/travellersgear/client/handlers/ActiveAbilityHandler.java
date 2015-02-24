package travellersgear.client.handlers;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import travellersgear.TravellersGear;
import travellersgear.api.IActiveAbility;
import travellersgear.api.TravellersGearAPI;
import travellersgear.client.KeyHandler;
import travellersgear.common.network.PacketActiveAbility;
import travellersgear.common.network.PacketPipeline;
import travellersgear.common.util.ModCompatability;
import baubles.api.BaublesApi;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ActiveAbilityHandler
{
	public static ActiveAbilityHandler instance = new ActiveAbilityHandler();

	public static boolean inActiveAbilityRadial=false;
	static ResourceLocation whiteTexture=new ResourceLocation("travellersgear:textures/gui/white.png");

	public Object[][] buildActiveAbilityList(EntityPlayer player)
	{
		ArrayList<Object[]> list = new ArrayList<Object[]>();

		ItemStack[] is = player.inventory.armorInventory;
		for(int armor=0; armor<is.length; armor++)
			if(is[armor]!=null && is[armor].getItem() instanceof IActiveAbility && ((IActiveAbility)is[armor].getItem()).canActivate(player, is[armor], false) )
				list.add( new Object[]{is[armor],9+armor});

		if(TravellersGear.BAUBLES)
		{
			IInventory inv = BaublesApi.getBaubles(player);
			if(inv!=null)
				for(int i=0; i<inv.getSizeInventory(); i++)
					if(inv.getStackInSlot(i)!=null && inv.getStackInSlot(i).getItem() instanceof IActiveAbility && ((IActiveAbility)inv.getStackInSlot(i).getItem()).canActivate(player, inv.getStackInSlot(i), false) )
						list.add(new Object[]{inv.getStackInSlot(i),9+4+i});
		}

		is = TravellersGearAPI.getExtendedInventory(player);
		for(int tg=0; tg<is.length; tg++)
			if(is[tg]!=null && is[tg].getItem() instanceof IActiveAbility && ((IActiveAbility)is[tg].getItem()).canActivate(player, is[tg], false) )
				list.add( new Object[]{is[tg],9+8+tg});

		if(TravellersGear.MARI)
		{
			IInventory inv = ModCompatability.getMariInventory(player);
			if(inv!=null)
				for(int i=0; i<inv.getSizeInventory(); i++)
					if(inv.getStackInSlot(i)!=null && inv.getStackInSlot(i).getItem() instanceof IActiveAbility && ((IActiveAbility)inv.getStackInSlot(i).getItem()).canActivate(player, inv.getStackInSlot(i), false) )
						list.add(new Object[]{inv.getStackInSlot(i),9+12+i});
		}
		if(TravellersGear.TCON)
		{
			IInventory inv = ModCompatability.getTConArmorInv(player);
			if(inv!=null)
				for(int i=1; i<3; i++)
					if(inv.getStackInSlot(i)!=null && inv.getStackInSlot(i).getItem() instanceof IActiveAbility && ((IActiveAbility)inv.getStackInSlot(i).getItem()).canActivate(player, inv.getStackInSlot(i), false) )
						list.add(new Object[]{inv.getStackInSlot(i),9+15+i});
		}
		if(player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() instanceof IActiveAbility&& ((IActiveAbility)player.getCurrentEquippedItem().getItem()).canActivate(player, player.getCurrentEquippedItem(), true) )
			list.add(list.size()/2, new Object[]{player.getCurrentEquippedItem(),player.inventory.currentItem});

		return list.toArray(new Object[0][]);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleMouse(MouseEvent event)
	{
		if(event.button==0 && inActiveAbilityRadial)
		{
			inActiveAbilityRadial=false;
			KeyHandler.abilityLock=false;
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer player = mc.thePlayer;
			mc.setIngameFocus();

			ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			int w = scaledresolution.getScaledWidth();
			int h = scaledresolution.getScaledHeight();

			Object[][] gear = buildActiveAbilityList(player);

			double rad = 62.5*KeyHandler.abilityRadial;
			double radInternal = rad*.6875;
			float segmentAngle = 360f / (gear.length+1);
			int mx = (Mouse.getX()-mc.displayWidth/2) * w / mc.displayWidth;
			int my = -(Mouse.getY()-mc.displayHeight/2) * h / mc.displayHeight - 1;
			double mRadius = Math.sqrt(mx*mx + my*my);
			int mouseSegment = mRadius<radInternal||mRadius>rad?-1: 0;
			if(mouseSegment==0)
			{
				double mAngleX= Math.toDegrees( Math.asin(mx/mRadius) );
				double mAngleY= Math.toDegrees( Math.acos(my/mRadius) );
				if(mAngleX<0)
					mAngleY= 360-mAngleY;
				mAngleY= (mAngleY+segmentAngle/2)%360;
				mouseSegment=(int) (mAngleY/segmentAngle);
			}
			int sel = mouseSegment-1;

			if(sel>=0 && sel<gear.length && gear[sel][0]!=null)
			{
				PacketPipeline.INSTANCE.sendToServer(new PacketActiveAbility(player, (Integer) gear[sel][1]));
				PacketActiveAbility.performAbility(player, (Integer) gear[sel][1]);
			}
		}
	}
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderGameOverlay(RenderGameOverlayEvent.Pre event)
	{
		if(event.type == RenderGameOverlayEvent.ElementType.TEXT && KeyHandler.abilityRadial>0)
		{
			Minecraft mc = Minecraft.getMinecraft();
			RenderItem ri = RenderItem.getInstance();
			EntityPlayer player = mc.thePlayer;
			Tessellator tessellator = Tessellator.instance;
			ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			int w = scaledresolution.getScaledWidth();
			int h = scaledresolution.getScaledHeight();

			Object[][] gear = buildActiveAbilityList(player);
			if(gear.length<1)
				return;

			float n = gear.length+1;
			float segmentAngle = 360f / n;
			int x = event.resolution.getScaledWidth()/2;
			int y = event.resolution.getScaledHeight()/2;

			GL11.glTranslatef(x, y, 0);

			GL11.glEnable(3042);
			double rad = 62.5*KeyHandler.abilityRadial;
			double radInternal = rad*.6875;
			double radItem = rad*.95;
			GL11.glRotatef(180+180*KeyHandler.abilityRadial, 0, 0, 1);

			int mx = (Mouse.getX()-mc.displayWidth/2) * w / mc.displayWidth;
			int my = -(Mouse.getY()-mc.displayHeight/2) * h / mc.displayHeight - 1;
			double mRadius = Math.sqrt(mx*mx + my*my);
			int mouseSegment = mRadius<radInternal||mRadius>rad?-1: 0;
			if(mouseSegment==0)
			{
				double mAngleX= Math.toDegrees( Math.asin(mx/mRadius) );
				double mAngleY= Math.toDegrees( Math.acos(my/mRadius) );
				if(mAngleX<0)
					mAngleY= 360-mAngleY;
				mAngleY= (mAngleY+segmentAngle/2)%360;
				mouseSegment=(int) (mAngleY/segmentAngle);
			}

			mc.getTextureManager().bindTexture(whiteTexture);
			for(int i=0; i<=gear.length; i++)
			{
				GL11.glRotatef(i*segmentAngle, 0, 0,-1);

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glShadeModel(GL11.GL_SMOOTH);

				int limit = 16/gear.length;
				float subSegment=segmentAngle/limit;
				for(int pass=1;pass>=0;pass--)
				{
					if(pass==1)
						for(int j=0; j<limit; j++)
						{
							double cx0 = Math.sin(Math.toRadians(-(segmentAngle*.5)+(subSegment*j)));
							double cy0 = Math.cos(Math.toRadians(-(segmentAngle*.5)+(subSegment*j)));
							double cx1 = Math.sin(Math.toRadians(-(segmentAngle*.5)+(subSegment*(j+1))));
							double cy1 = Math.cos(Math.toRadians(-(segmentAngle*.5)+(subSegment*(j+1))));
							tessellator.startDrawingQuads();
							tessellator.setColorOpaque_I(i!=mouseSegment?0x4d2412:0xf9eed2);
							tessellator.addVertex(cx0*(rad), cy0*(rad), 0.0D);
							tessellator.addVertex(cx1*(rad), cy1*(rad), 0.0D);
							tessellator.setColorOpaque_I(i!=mouseSegment?0x803b26:0xfcf7e9);
							tessellator.addVertex(cx1*(radInternal), cy1*(radInternal), 0.0D);
							tessellator.addVertex(cx0*(radInternal), cy0*(radInternal), 0.0D);
							tessellator.draw();
						}
					else
					{
						GL11.glLineWidth(2);
						tessellator.startDrawing(2);
						tessellator.setColorOpaque_I(i!=mouseSegment?0x777777:0xffffff);
						for(int j=0; j<=limit; j++)
						{
							double cx = Math.sin(Math.toRadians(-(segmentAngle*.5)+(subSegment*j)));
							double cy = Math.cos(Math.toRadians(-(segmentAngle*.5)+(subSegment*j)));
							tessellator.addVertex(cx*(rad)-pass/2, cy*(rad)-pass/2, 0.0D);
						}
						tessellator.setColorOpaque_I(i!=mouseSegment?0x999999:0xfcf7e9);
						for(int j=0; j<=limit; j++)
						{
							double cx = Math.sin(Math.toRadians((segmentAngle*.5)-(subSegment*j)));
							double cy = Math.cos(Math.toRadians((segmentAngle*.5)-(subSegment*j)));
							tessellator.addVertex(cx*(radInternal)+pass*.5, cy*(radInternal)+pass*.5, 0.0D);
						}
						tessellator.draw();
					}
				}

				GL11.glShadeModel(GL11.GL_FLAT);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glRotatef(i*segmentAngle, 0, 0, 1);
			}


			for(int i=0; i<=gear.length; i++)
			{
				double angle = segmentAngle*i;
				double cx = Math.sin(Math.toRadians(angle));
				double cy = Math.cos(Math.toRadians(angle));

				GL11.glTranslated(radItem*.875*cx, radItem*.875*cy, 0);
				GL11.glPushMatrix();
				if(i>0)
					if(gear[i-1][0]!=null)
						ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), (ItemStack) gear[i-1][0], -8,-8);
				GL11.glPopMatrix();
				GL11.glTranslated(-radItem*.875*cx,-radItem*.875*cy,0);
			}


			GL11.glRotatef(180+180*KeyHandler.abilityRadial, 0, 0,-1);
			GL11.glTranslatef(-x,-y,0);
			GL11.glDisable(GL11.GL_LIGHTING);

			if(KeyHandler.abilityLock)
			{
				if((!inActiveAbilityRadial || mc.inGameHasFocus))
				{
					inActiveAbilityRadial=true;
					mc.setIngameFocus();
					mc.setIngameNotInFocus();
				}
			}
			else if(inActiveAbilityRadial)
			{
				inActiveAbilityRadial=false;
				mc.setIngameFocus();
			}

		}
	}
}