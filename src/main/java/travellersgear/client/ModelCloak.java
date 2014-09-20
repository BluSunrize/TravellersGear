package travellersgear.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class ModelCloak extends ModelBiped
{
	private static double[] circPos = {
		0.5,
		0.49039,
		0.46194,
		0.41573,
		0.35355,
		0.27779,
		0.19134,
		0.09755,
		0.0,
		-0.09755,
		-0.19134,
		-0.27779,
		-0.35355,
		-0.41573,
		-0.46194,
		-0.49039,
		-0.5,
		-0.49039,
		-0.46194,
		-0.41573,
		-0.35355,
		-0.27779,
		-0.19134,
		-0.09755,
		0.0,
		0.09755,
		0.19134,
		0.27779,
		0.35355,
		0.41573,
		0.46194,
		0.49039
	};

	public static boolean doAnimation = true;
	int colour;

	public ModelCloak(int colour)
	{
		this.bipedHead.showModel = false;
		this.bipedHeadwear.showModel = false;
		this.bipedBody.showModel = false;
		this.bipedLeftArm.showModel = false;
		this.bipedRightArm.showModel = false;
		this.bipedLeftLeg.showModel = false;
		this.bipedRightLeg.showModel = false;
		this.colour = colour;
	}

	@Override
	public void render(Entity ent, float f1, float f2, float f3, float f4, float f5, float f6)
	{
		if(!(ent instanceof EntityLivingBase))
			return;
		EntityLivingBase living = (EntityLivingBase) ent;

		ModelBiped playerModel = null;
		try
		{
			AbstractClientPlayer player = (AbstractClientPlayer) living;
			Object model = ObfuscationReflectionHelper.getPrivateValue(RenderPlayer.class, (RenderPlayer)RenderManager.instance.getEntityRenderObject(player), 1);
			if(model instanceof ModelBiped)
				playerModel = (ModelBiped) model;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		if(playerModel == null)
			return;

		this.setRotationAngles(f1, f2, f3, f4, f5, f6, ent);
		//int colour = 0xffffff;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		boolean drawHood = living.getEquipmentInSlot(4)==null;
		String unl = living.getEquipmentInSlot(4)!=null?living.getEquipmentInSlot(4).getUnlocalizedName().toLowerCase():"";
		drawHood |= unl.contains("goggle") || (unl.contains("glasses")&&!unl.contains("sonicglasses")) || unl.contains("monocle");
		
		Tessellator tessellator = Tessellator.instance;

		GL11.glTranslatef(0, living.getEquipmentInSlot(3)==null?1.4375f:1.375f, 0);
		GL11.glScalef(1.125f, -1, 1.1f);
		GL11.glTranslatef(0, 0,-.1f);

		circPos = new double[] {
				0.5,
				0.49039,
				0.46194,
				0.41573,
				0.35355,
				0.27779,
				0.19134,
				0.09755,
				0.0,
				-0.09755,
				-0.19134,
				-0.27779,
				-0.35355,
				-0.41573,
				-0.46194,
				-0.49039,
				-0.5,
				-0.49039,
				-0.46194,
				-0.41573,
				-0.35355,
				-0.27779,
				-0.19134,
				-0.09755,
				0.0,
				0.09755,
				0.19134,
				0.27779,
				0.35355,
				0.41573,
				0.46194,
				0.49039
		};

		double d0_1 = circPos[0]*1;
		double d1_1 = circPos[1]*1;
		double d2_1 = circPos[24]*1;
		double d3_1 = circPos[25]*1;

		for(int i=0;i<8;i++)
		{
			int it0 = i;
			int it1 = it0+1;
			if(it1 > 31)it1-=31;
			int it2 = i+24;
			if(it2 > 31)it2-=31;
			int it3 = it2+1;
			if(it3 > 31)it3-=31;


			for(int j=0; j < 8;j++)
			{
				int jt0 = j;
				int jt1 = jt0+1;
				double h0 = (circPos[jt0]*circPos[jt0])*7;
				double h1 = (circPos[jt1]*circPos[jt1])*7;
				double dividerA[] = {0.3,0.725,0.75,0.8,0.825,0.9,1.0,1.1};
				double divider = dividerA[j];

				double d0 = circPos[it0]*1.5*divider;
				double d1 = circPos[it1]*1.5*divider;
				double d2 = circPos[it2]*1.5*divider;
				double d3 = circPos[it3]*1.5*divider;


				double minU = i*0.0625;//icon.getMinU();
				double maxU = (i+1)*0.0625;//icon.getMaxU();
				double minV = j * 0.125;//1 - (j+1)*0.125;//icon.getMinV();
				double maxV = (j+1)*0.125;//1 - j*0.125;//icon.getMaxV();

				if(j==2)h0*=0.975;
				if(j==1)
				{
					h1*=0.975;
					h0*=0.9;
				}
				if(j==0)
				{
					d0 *=0.0;
					d0_1 *=0.0;
					d1 *=0.0;
					d1_1 *=0.0;
					d2 *=0.0;
					d2_1 *=0.0;
					d3 *=0.0;
					d3_1 *=0.0;
					h1*=0.9;
					h0*=0.9;
				}

				if(doAnimation)
				{
					//					double offsettingAngle = Math.max(this.bipedLeftLeg.rotateAngleX * (180F / (float)Math.PI), this.bipedRightLeg.rotateAngleX * (180F / (float)Math.PI));
					double offsettingAngle = living.limbSwingAmount * (180F / (float)Math.PI);
					if(offsettingAngle > 1)
					{
						double stretch = 0.75 *  (offsettingAngle / 90.0);
						stretch += 1.0;
						d2*=stretch;
						d3*=stretch;
					}
				}

				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, 1.0F, 0.0F);
				tessellator.setColorOpaque_I(colour);
				tessellator.addVertexWithUV(d0_1, h0, d2_1, minU, minV);
				tessellator.addVertexWithUV(d0  , h1, d2  , minU, maxV);
				tessellator.addVertexWithUV(d1  , h1, d3  , maxU, maxV);
				tessellator.addVertexWithUV(d1_1, h0, d3_1, maxU, minV);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, 1.0F, 0.0F);
				tessellator.setColorOpaque_I(colour);
				tessellator.addVertexWithUV(-d0_1, h0, d2_1, minU, minV);
				tessellator.addVertexWithUV(-d0  , h1, d2  , minU, maxV);
				tessellator.addVertexWithUV(-d1  , h1, d3  , maxU, maxV);
				tessellator.addVertexWithUV(-d1_1, h0, d3_1, maxU, minV);
				tessellator.draw();
				d0_1 = d0;
				d1_1 = d1;
				d2_1 = d2;
				d3_1 = d3;
			}
		}


		if(drawHood)
		{
			//			GL11.glRotatef(living.rotationYawHead-living.renderYawOffset, 0F, 1F, 0F);
			GL11.glTranslatef(0, living.getEquipmentInSlot(3)==null?1.375f:1.3f, 0);

			GL11.glTranslated(0, 0,-.02f);
			GL11.glScaled(.9375, 1.1, 1.5);



			for(int i=0;i<8;i++)
			{
				int it0 = i;
				int it1 = it0+1;
				if(it1 > 31)it1-=31;
				int it2 = i+24;
				if(it2 > 31)it2-=31;
				int it3 = it2+1;
				if(it3 > 31)it3-=31;

				int it0_1 = i+1;
				int it1_1 = it0_1+1;
				if(it1_1 > 31)it1_1-=31;
				int it2_1 = i+24;
				if(it2_1 > 31)it2_1-=31;
				int it3_1 = it2_1+1;
				if(it3_1 > 31)it3_1-=31;

				for(int j=0; j < 7;j++)
				{

					int jt0 = j;
					int jt1 = jt0+1;
					double h0 = (circPos[jt0]*circPos[jt0])*2.75;
					double h1 = (circPos[jt1]*circPos[jt1])*2.75;
					double dividerA[] = {0,0.65,0.675,0.7,0.725,0.775,0.825,0.9};
					double divider = dividerA[j];
					double d0 = circPos[it0]*0.9*divider;
					double d1 = circPos[it1]*0.9*divider;
					double d2 = circPos[it2]*0.9*divider;
					double d3 = circPos[it3]*0.9*divider;

					double minU = i*0.0625;
					double maxU = (i+1)*0.0625;
					double minV = j * 0.125;
					double maxV = (j+1)*0.125;


					if(j==2)h0*=0.975;
					if(j==1)
					{
						h1*=0.975;
						h0*=0.9;
					}
					if(j==2||j==3||j==4)
					{
						d2 *= 1.25;
						d3 *= 1.25;
					}
					if(j!=0 )
					{
						tessellator.startDrawingQuads();
						tessellator.setColorOpaque_I(this.colour);
						tessellator.setNormal(0.0F, 1.0F, 0.0F);
						tessellator.addVertexWithUV(d0_1, h0, d2_1, minU, minV);
						tessellator.addVertexWithUV(d0  , h1, d2  , minU, maxV);
						tessellator.addVertexWithUV(d1  , h1, d3  , maxU, maxV);
						tessellator.addVertexWithUV(d1_1, h0, d3_1, maxU, minV);
						tessellator.draw();
						tessellator.startDrawingQuads();
						tessellator.setColorOpaque_I(this.colour);
						tessellator.setNormal(0.0F, 1.0F, 0.0F);
						tessellator.addVertexWithUV(-d0_1, h0, d2_1, minU, minV);
						tessellator.addVertexWithUV(-d0  , h1, d2  , minU, maxV);
						tessellator.addVertexWithUV(-d1  , h1, d3  , maxU, maxV);
						tessellator.addVertexWithUV(-d1_1, h0, d3_1, maxU, minV);
						tessellator.draw();
					}
					d0_1 = d0;
					d1_1 = d1;
					d2_1 = d2;
					d3_1 = d3;
				}
			}
		}

		GL11.glDisable(3042);
		GL11.glPopMatrix();
	}
}