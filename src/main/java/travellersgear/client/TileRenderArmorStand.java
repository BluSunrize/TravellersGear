package travellersgear.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import travellersgear.common.blocks.TileEntityArmorStand;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileRenderArmorStand extends TileEntitySpecialRenderer
{
	ModelArmorStand modelStand = new ModelArmorStand();
	ModelBiped modelArmor = new ModelBiped();
	FakeClientPlayer fakepl;
	RenderPlayer renderPlayer;
	
	static ResourceLocation texture = new ResourceLocation("travellersgear:textures/models/armorstand.png");
	
	public TileRenderArmorStand()
	{
		modelArmor.isChild = false;
		this.modelStand = new ModelArmorStand();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f)
	{
		try{
			if (this.fakepl == null)
			{
				this.fakepl = new FakeClientPlayer(Minecraft.getMinecraft().theWorld);
				this.fakepl.ticksExisted=0;
			}
			if(this.renderPlayer == null)
				this.renderPlayer = (RenderPlayer) RenderManager.instance.getEntityRenderObject(this.fakepl);
			
			RenderManager.renderPosX = fakepl.posX;
			RenderManager.renderPosY = fakepl.posY;
			RenderManager.renderPosZ = fakepl.posZ;
			
			GL11.glPushMatrix();
			TileEntityArmorStand tile = (TileEntityArmorStand) tileentity;

			GL11.glTranslatef((float)x+.5f, (float)y, (float)z+.5f);

			switch(tile.facing)
			{
			case 2:
				GL11.glRotatef(180, 0, 1, 0);
				break;
			case 3:
				break;
			case 4:
				GL11.glRotatef(270, 0, 1, 0);
				break;
			case 5:
				GL11.glRotatef(90, 0, 1, 0);
				break;
			}

			this.bindTexture(texture);
			this.modelStand.renderAll(tile.renderHelmet, tile.renderChest, tile.renderLegs, tile.renderBoots, tile.renderFloor, tile.renderBaubles);

			GL11.glTranslated(0, 1.61, 0);
			GL11.glTranslatef(0,0,-.25f);
			for(int armor=0;armor<4;armor++)
				if( (armor==0&&tile.renderHelmet)||(armor==1&&tile.renderChest)||(armor==2&&tile.renderLegs)||(armor==3&&tile.renderBoots) )
					this.fakepl.inventory.armorInventory[3-armor] = tile.getStackInSlot(armor)!=null? tile.getStackInSlot(armor).copy() : null;
					else
						this.fakepl.inventory.armorInventory[3-armor] = null;

			GL11.glDisable(GL11.GL_CULL_FACE);
			boolean[] renderPlayer = {false,false,false,false};
			for(int armor=0;armor<4;armor++)
				if( (armor==0&&tile.renderHelmet)||(armor==1&&tile.renderChest)||(armor==2&&tile.renderLegs)||(armor==3&&tile.renderBoots) )
					if(tile.getStackInSlot(armor)!=null)
					{
						ItemStack armorStack = tile.getStackInSlot(armor);

						if(armorStack.getItem() instanceof ItemArmor)
						{
							RenderPlayerEvent.SetArmorModel setArmorEvent = new RenderPlayerEvent.SetArmorModel(this.fakepl, this.renderPlayer, 3-armor, f, armorStack);
					        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(setArmorEvent);
					        if (setArmorEvent.result != -1)
					        	continue;
							
							ItemArmor armorItem = (ItemArmor)armorStack.getItem();
							ModelBiped customModel = armorItem.getArmorModel(this.fakepl, armorStack, armor);

							if(customModel!=null)
							{
								renderPlayer[armor]=true;
							}
							else
								for(int pass=0;pass<armorItem.getRenderPasses(armorStack.getItemDamage()); pass++)
								{
									boolean isEnchanted = armorItem.hasEffect(armorStack, pass);
									this.fakepl.inventory.armorInventory[3-armor] = null;

									ResourceLocation texture = RenderBiped.getArmorResource(this.fakepl, armorStack, armor, pass==0?null:"overlay");

									GL11.glPushMatrix();
									GL11.glColor3f(1,1,1);
									GL11.glRotated(180, 1,0,0);
									this.bindTexture(texture);


									int colour = armorItem.getColor(armorStack);
									if(colour != -1 && pass==0)
									{
										float f1 = (float)(colour >> 16 & 255) / 255.0F;
										float f2 = (float)(colour >> 8 & 255) / 255.0F;
										float f3 = (float)(colour & 255) / 255.0F;
										GL11.glColor3f(f1, f2, f3);
									}

									if(this.modelArmor.isChild)
										this.modelArmor.isChild=false;

									if(armor==1)
										GL11.glScaled(1.1,1.1,1.1);
									if(armor==3)
										GL11.glScaled(1.1,1,1.1);
									if(armor==2)
										GL11.glTranslated(0, -.125, 0);


									this.renderDefaultArmor(armor);

									if(isEnchanted)
									{
										GL11.glDepthFunc(GL11.GL_EQUAL);
										GL11.glDisable(GL11.GL_LIGHTING);
										this.bindTexture(new ResourceLocation("textures/misc/enchanted_item_glint.png"));
										GL11.glEnable(GL11.GL_BLEND);
										OpenGlHelper.glBlendFunc(768, 1, 1, 0);
										float f7 = 0.76F;
										GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
										GL11.glMatrixMode(GL11.GL_TEXTURE);
										GL11.glPushMatrix();
										float f8 = 0.125F;
										GL11.glScalef(f8, f8, f8);
										float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
										GL11.glTranslatef(f9, 0.0F, 0.0F);
										GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
										this.renderDefaultArmor(armor);
										GL11.glPopMatrix();

										GL11.glPushMatrix();
										GL11.glScalef(f8, f8, f8);
										f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
										GL11.glTranslatef(-f9, 0.0F, 0.0F);
										GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
										this.renderDefaultArmor(armor);
										GL11.glPopMatrix();
										GL11.glMatrixMode(GL11.GL_MODELVIEW);
										GL11.glDisable(GL11.GL_BLEND);
										GL11.glEnable(GL11.GL_LIGHTING);
										GL11.glDepthFunc(GL11.GL_LEQUAL);
									}
									GL11.glPopMatrix();
								}
						}
						else
						{
							if (armorStack.getItem() instanceof ItemBlock)
							{
								GL11.glPushMatrix();
								GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
								GL11.glScalef(.5f,.5f,.5f);
								GL11.glTranslatef(0.0F, .75F, 0.0F);
								net.minecraftforge.client.IItemRenderer customRenderer = net.minecraftforge.client.MinecraftForgeClient.getItemRenderer(armorStack, net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED);
								boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED, armorStack, net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D));

								if (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(armorStack.getItem()).getRenderType()))
								{
									GL11.glTranslatef(0.0F, -0.25F, 0.0F);
									GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
								}

								RenderManager.instance.itemRenderer.renderItem(fakepl, armorStack, 0);
								GL11.glPopMatrix();
							}
							else if(armorStack.getItem() instanceof ItemSkull)
							{
								GL11.glPushMatrix();

								TileEntitySkullRenderer skull = new TileEntitySkullRenderer();
								skull.func_147497_a(TileEntityRendererDispatcher.instance);
								GameProfile gp = null;
								if (armorStack.hasTagCompound())
								{
									NBTTagCompound nbttagcompound = armorStack.getTagCompound();
									if (nbttagcompound.hasKey("SkullOwner", 10))
										gp = NBTUtil.func_152459_a(nbttagcompound.getCompoundTag("SkullOwner"));
									else if (nbttagcompound.hasKey("SkullOwner", 8) && !StringUtils.isNullOrEmpty(nbttagcompound.getString("SkullOwner")))
										gp = new GameProfile((UUID)null, nbttagcompound.getString("SkullOwner"));
								}
								//						skull.func_152674_a(-.25f,-.1875f,-.5f, 0, 0, type, gp);
								TileEntitySkullRenderer.field_147536_b.func_152674_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, armorStack.getItemDamage(), gp);

								GL11.glPopMatrix();
							}
						}

					}

			if(tile.renderTravellersGear)
			{
				GL11.glTranslatef(0,-.1f,0);
				GL11.glRotatef(180, 1, 0, 0);
				for(int ieq=0;ieq<=1;ieq++)
					if(tile.getStackInSlot(8+ieq)!=null)
						ClientProxy.renderTravellersItem(tile.getStackInSlot(8+ieq), ieq, this.fakepl, this.renderPlayer, f);
				GL11.glRotatef(180, 1, 0, 0);
				GL11.glTranslatef(0,.1f,0);
			}

			GL11.glScalef(1.0625f,1.0625f,1.0625f);
			GL11.glTranslatef(0,.1f,0);
			
			if(renderPlayer[0]||renderPlayer[1]||renderPlayer[2]||renderPlayer[3])
			{
				for(int its=0;its<4;its++)
					if(!renderPlayer[its])
						fakepl.inventory.armorInventory[3-its]=null;
				RenderManager.instance.renderEntitySimple(fakepl, 0);
			}

			this.bindTexture(TextureMap.locationItemsTexture);
			GL11.glRotatef(180, 1, 0, 0);
			GL11.glRotatef(180, 0, 0, 1);
			GL11.glRotated(-.3f*(180/Math.PI), 1, 0, 0);
			GL11.glScaled(.25,1,.25);
			GL11.glTranslated(-.5,-.6625,-2.6);


			GL11.glRotated(90, 1,0,0);

			boolean hasBaub = tile.renderBaubles && (tile.getStackInSlot(4+0)!=null || tile.getStackInSlot(4+1)!=null || tile.getStackInSlot(4+2)!=null || tile.getStackInSlot(4+3)!=null);
			boolean hasMari = tile.renderBaubles && (tile.getStackInSlot(11+0)!=null || tile.getStackInSlot(11+1)!=null || tile.getStackInSlot(11+2)!=null);
			boolean hasVam = tile.renderBaubles && tile.renderTravellersGear && tile.getStackInSlot(8+2)!=null;
			boolean hasGlove = tile.renderBaubles && tile.getStackInSlot(14)!=null;

			int style = hasBaub&&hasMari&&hasVam&&hasGlove?0: hasBaub&&!hasMari&&!hasVam&&!hasGlove?1: hasBaub&&hasMari&&!hasVam&&!hasGlove?2: hasBaub&&hasMari&&hasVam&&!hasGlove?3: hasBaub&&hasMari&&!hasVam&&hasGlove?4: 5;

			float[] xOffset = new float[9];
			xOffset[0]= style==0?.1f: style==1?-.75f: style==2?-.5f: style==3?-.5f: style==4?-.5f: -1.375f;
			xOffset[1]= style==0?-.9f: style==1?-1.35f: style==2?-.9f: style==3?-.9f: style==4?-.9f: -.875f;
			xOffset[2]= -1.35f;
			xOffset[3]= style==0?1f: style==1?.375f: style==2?.375f: style==3?1.125f: style==4?1.125f: .9375f;
			xOffset[4]= style==0?-.5f: style==2?-1.125f: style==3?-1.125f: -1.125f;
			xOffset[5]= style==0?-1.375f: -1.3125f;
			xOffset[6]= style==0?-.125f: -.625f;
			xOffset[7]= style==0?.5f: style==3?.125f: -.5625f;
			xOffset[8]= style==0?-1.125f: style==4?.25f: -.375f;

			float[] yOffset = new float[9];
			yOffset[0]= style==0?-.275f: style==1?-1.25f: style==2?-1.25f: style==3?-1.25f: style==4?-1.25f: -.5f;
			yOffset[1]= style==0?-1.25f: style==1?-.5f: -1.25f;
			yOffset[2]= -1.25f;
			yOffset[3]= -1.25f;
			yOffset[4]= style==0?-1.25f: -.75f;
			yOffset[5]= style==0?-1f: -.3f;
			yOffset[6]= style==0?-1.3f: -.25f;
			yOffset[7]= style==0?.35f: style==3?.375f: .375f;		
			yOffset[8]= style==0?1f: style==4?1f: -1.375f;

			float[] scale = new float[9];
			scale[0]= style==0?1f: style==1?1.25f: style==2?1f: style==3?1f: style==4?1f: 1.25f;
			scale[1]= style==0?.375f: style==1?.75f: .5f;
			scale[2]= style==0?.375f: style==1?.75f: .5f;
			scale[3]= style==0?1.125f: style==1?2f: style==2?2f: style==3?1.25f: style==4?1.25f: 1.5f;
			scale[4]= style==0?.375f: .5f;
			scale[5]= .75f;
			scale[6]= 1f;
			scale[7]= 1.5f;
			scale[8]= 1.25f;

			float[] hscale = new float[9];
			hscale[0]= .75f;
			hscale[1]= .5f;
			hscale[2]= .5f;
			hscale[3]= 1f;
			hscale[4]= .5f;
			hscale[5]= .75f;
			hscale[6]= 1f;
			hscale[7]= 1f;		
			hscale[8]= .75f;

			float[] rotation = new float[9];
			rotation[7]= -45;
			rotation[8]= style==0||style==4?-90 : 0;

			if(tile.renderBaubles)
				for(int dis=0;dis<9;dis++)
				{
					if(dis>=4 && dis<7 && (style==1 || style==5))
						continue;
					if(dis>=7 && dis<8 && (!tile.renderTravellersGear || style==1 || style==2 || style==4))
						continue;
					if(dis>=8 && (style==1 || style==2 || style==3))
						continue;

					int slot = dis<4?4+dis: dis<7?7+dis: dis<8?10 : 14;
					if(tile.getStackInSlot(slot)!=null)
						this.renderStackOnDisplay(tile.getStackInSlot(slot), xOffset[dis], yOffset[dis], .0375f, scale[dis], hscale[dis], rotation[dis]);
				}

			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);

			GL11.glPopMatrix();

		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	void renderDefaultArmor(int slot)
	{
		switch(slot){
		case 0:
			this.modelArmor.bipedHead.render(.0625f);
			this.modelArmor.bipedHeadwear.render(.0625f);
			break;
		case 1:
			this.modelArmor.bipedBody.render(.0625f);
			this.modelArmor.bipedRightArm.render(.0625f);
			this.modelArmor.bipedLeftArm.render(.0625f);
			break;
		case 2:
			this.modelArmor.bipedRightLeg.render(.0625f);
			this.modelArmor.bipedLeftLeg.render(.0625f);
			break;
		case 3:
			this.modelArmor.bipedRightLeg.render(.0625f);
			this.modelArmor.bipedLeftLeg.render(.0625f);
			break;
		}
	}

	void renderStackOnDisplay(ItemStack stack, float x, float y, float z, float scaleXY, float scaleZ, float rotateY)
	{
		GL11.glTranslated(x,y,z);
		GL11.glScaled(scaleXY,scaleXY,scaleZ);
		GL11.glRotatef(rotateY, 0, 0, 1);
		for(int pass=0;pass<stack.getItem().getRenderPasses(stack.getItemDamage());pass++)
		{
			IIcon iicon = stack.getItem().getIcon(stack, pass);
			ItemRenderer.renderItemIn2D(Tessellator.instance,  iicon.getMaxU(), iicon.getMinV(), iicon.getMinU(), iicon.getMaxV(), iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);
			if (stack.hasEffect(pass))
			{
				GL11.glPushMatrix();
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDisable(GL11.GL_LIGHTING);
				this.bindTexture(new ResourceLocation("textures/misc/enchanted_item_glint.png"));
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(768, 1, 1, 0);
				float f7 = 0.76F;
				GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glPushMatrix();
				float f8 = 0.125F;
				GL11.glScalef(f8, f8, f8);
				float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
				GL11.glTranslatef(f9, 0.0F, 0.0F);
				GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
				ItemRenderer.renderItemIn2D(Tessellator.instance, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glScalef(f8, f8, f8);
				f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
				GL11.glTranslatef(-f9, 0.0F, 0.0F);
				GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
				ItemRenderer.renderItemIn2D(Tessellator.instance, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
				GL11.glPopMatrix();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
				GL11.glPopMatrix();
				GL11.glColor3f(1, 1, 1);
				this.bindTexture(TextureMap.locationItemsTexture);
			}
		}
		GL11.glRotatef(-rotateY, 0, 0, 1);
		GL11.glScaled(1/scaleXY,1/scaleXY,1/scaleZ);
		GL11.glTranslated(-x,-y,-z);
	}

	public static class ModelArmorStand extends ModelBase
	{
		ModelRenderer floor;
		ModelRenderer head;
		ModelRenderer spine;
		List<ModelRenderer> corpus = new ArrayList();

		List<ModelRenderer> table = new ArrayList();

		public ModelArmorStand()
		{
			this.textureHeight = 32;
			this.textureWidth = 96;

			//PLATE
			floor = new ModelRenderer(this, 0, 12);
			floor.addBox(0F, 0F, 0F, 16, 2, 16);
			floor.setRotationPoint(-8.0F, 0.0F, -8.0F);
			floor.setTextureSize(96, 32);
			floor.mirror = true;
			//HEAD
			head = new ModelRenderer(this, 68,0);
			head.addBox(0F, 0F, 0F, 7, 7, 7);
			head.setRotationPoint(-3.5F, 26F, -7.5F);
			head.setTextureSize(96, 32);
			head.mirror = true;
			//SPINE
			spine = new ModelRenderer(this, 56, 0);
			spine.addBox(0F, 0F, 0F, 3, 29, 3);
			spine.setRotationPoint(-1.5F, 0.0F, -5.5F);
			spine.setTextureSize(96, 32);
			spine.mirror = true;

			ModelRenderer temp;
			corpus.clear();
			//CORPUS
			temp = new ModelRenderer(this, 0,17);
			temp.addBox(0F, 0F, 0F, 4, 8, 4);
			temp.setRotationPoint(-2.0F, 16.0F, -6.0F);
			temp.setTextureSize(96, 32);
			temp.mirror = true;
			corpus.add(temp);
			//SHOULDER 1
			temp = new ModelRenderer(this, 68,14);
			temp.addBox(0F, 0F, 0F, 6, 2, 4);
			temp.setRotationPoint(-8.0F, 22.0F, -6.0F);
			temp.setTextureSize(96, 32);
			temp.mirror = true;
			corpus.add(temp);
			//SHOULDER 2
			temp = new ModelRenderer(this, 68,14);
			temp.addBox(0F, 0F, 0F, 6, 2, 4);
			temp.setRotationPoint(2.0F, 22.0F, -6.0F);
			temp.setTextureSize(96, 32);
			temp.mirror = true;
			corpus.add(temp);
			//STRUT 1
			temp = new ModelRenderer(this, 56,0);
			temp.addBox(-1F, 0F, -5F, 2, 7, 2);
			temp.setRotationPoint(1.0F, 18.0F, 0.0F);
			temp.setTextureSize(96, 32);
			setAngles(temp, 0.0F, 0.0F, -0.7853982F);
			temp.mirror = true;
			corpus.add(temp);
			//STRUT 2
			temp = new ModelRenderer(this, 56, 0);
			temp.addBox(-1F, 0F, -5F, 2, 7, 2);
			temp.setRotationPoint(-1.0F, 18.0F, 0.0F);
			temp.setTextureSize(96, 32);
			setAngles(temp, 0.0F, 0.0F, 0.7853982F);
			temp.mirror = true;
			corpus.add(temp);

			table.clear();
			//TOP
			temp = new ModelRenderer(this, 0, 0);
			temp.addBox(0.0F, 0.0F, 0.0F, 16, 2, 10);
			temp.setRotationPoint(-8.0F, 12.0F, -1.0F);
			temp.setTextureSize(96, 32);
			temp.mirror = true;
			temp.rotateAngleX = .3f;
			table.add(temp);
			//STOPPER
			temp = new ModelRenderer(this, 9, 9);
			temp.addBox(0.0F, 0.0F, 0.0F, 16, 1, 1);
			temp.setRotationPoint(-8.0F, 11.2F, 8.175F);
			temp.setTextureSize(96, 32);
			temp.mirror = true;
			temp.rotateAngleX = .3f;
			table.add(temp);
			//LEG 1
			temp = new ModelRenderer(this, 0, 14);
			temp.addBox(0.0F, 0.0F, 0.0F, 4, 10, 4);
			temp.setRotationPoint(3F, 1.875F, 2F);
			temp.setTextureSize(96, 32);
			temp.mirror = true;
			table.add(temp);
			//LEG 2
			temp = new ModelRenderer(this, 0, 14);
			temp.addBox(0.0F, 0.0F, 0.0F, 4, 10, 4);
			temp.setRotationPoint(-6.0F, 1.875F, 2F);
			temp.setTextureSize(96, 32);
			temp.mirror = true;
			table.add(temp);
		}

		public void renderAll(boolean renderHead, boolean renderChest, boolean renderLegs, boolean renderBoots, boolean renderFloor, boolean renderTable)
		{
			if(renderFloor)
				this.floor.render(.0625f);
			else
				GL11.glTranslatef(0,-.125f, 0);
			if(!renderTable)
				GL11.glTranslatef(0, 0, 0.25f);
			if(renderHead)
				this.head.render(.0625f);
			if(renderHead || renderChest || renderLegs)
				this.spine.render(.0625f);
			if(renderChest)
				for(ModelRenderer r : this.corpus)
					r.render(.0625f);
			if(renderTable)
				for(ModelRenderer r : this.table)
					r.render(.0625f);
		}

		void setAngles(ModelRenderer r, float x, float y, float z)
		{
			r.rotateAngleX = x;
			r.rotateAngleY = y;
			r.rotateAngleZ = z;
		}
	}

	public static class FakeClientPlayer extends AbstractClientPlayer
	{
		private static GameProfile gp = new GameProfile(null, "WG_FakeClientPlayer");

		public FakeClientPlayer(World world)
		{
			super(world, gp);
		}

		@Override
		public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_)
		{
			return false;
		}

		@Override
		public ChunkCoordinates getPlayerCoordinates()
		{
			return null;
		}

		@SideOnly(Side.CLIENT)
		public int getBrightnessForRender(float par1)
		{
			return 15728880;
		}

		@Override
		public boolean isInvisible()
		{
			return true;
		}

		@Override
		public void addChatMessage(IChatComponent p_145747_1_){}

	}
}