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
import net.minecraftforge.client.ForgeHooksClient;
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
		this.modelStand = new ModelArmorStand();
		this.modelArmor.isChild=false;
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
			this.modelStand.renderAll(tile.renderArmor[0], tile.renderArmor[1], tile.renderArmor[2], tile.renderArmor[3], tile.renderFloor, tile.renderTable);

			GL11.glTranslated(0, 1.61, 0);
			GL11.glTranslatef(0,0,-.25f);
			for(int armor=0;armor<4;armor++)
			{
				if(tile.renderArmor[armor])
					this.fakepl.inventory.armorInventory[3-armor] = tile.getStackInSlot(armor);
				else
					this.fakepl.inventory.armorInventory[3-armor] = null;
			}
			GL11.glDisable(GL11.GL_CULL_FACE);
			boolean[] renderPlayer = {false,false,false,false};
			for(int armor=0;armor<4;armor++)
				if( (armor==0&&tile.renderArmor[0])||(armor==1&&tile.renderArmor[1])||(armor==2&&tile.renderArmor[2])||(armor==3&&tile.renderArmor[3]) )
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

									if(armor==1)
										GL11.glScaled(1.1,1.1,1.1);
									if(armor==3)
										GL11.glScaled(1.1,1,1.1);
									if(armor==2)
										GL11.glTranslated(0, -.125, 0);

									GL11.glPushMatrix();
									this.renderDefaultArmor(armor);
									GL11.glPopMatrix();

									if(isEnchanted)
									{
										float tick = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 48.0F;
										this.bindTexture(new ResourceLocation("textures/misc/enchanted_item_glint.png"));
										GL11.glEnable(GL11.GL_BLEND);
										float f2 = 0.5F;
										GL11.glColor4f(f2, f2, f2, 1.0F);
										GL11.glDepthFunc(GL11.GL_EQUAL);
										GL11.glDepthMask(false);
										for (int var21 = 0; var21 < 2; var21++)
										{
											GL11.glDisable(GL11.GL_LIGHTING);
											float var22 = 0.76F;
											GL11.glColor4f(0.5F * var22, 0.25F * var22, 0.8F * var22, 1.0F);
											GL11.glBlendFunc(768, 1);
											GL11.glMatrixMode(GL11.GL_TEXTURE);
											GL11.glLoadIdentity();
											float var23 = tick * (0.001F + var21 * 0.003F) * 20.0F;
											float var24 = 0.3333333F;
											GL11.glScalef(var24, var24, var24);
											GL11.glRotatef(30.0F - var21 * 60.0F, 0.0F, 0.0F, 1.0F);
											GL11.glTranslatef(0.0F, var23, 0.0F);
											GL11.glMatrixMode(GL11.GL_MODELVIEW);
											this.renderDefaultArmor(armor);
										}
										GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
										GL11.glMatrixMode(GL11.GL_TEXTURE);
										GL11.glDepthMask(true);
										GL11.glLoadIdentity();
										GL11.glMatrixMode(GL11.GL_MODELVIEW);
										GL11.glEnable(GL11.GL_LIGHTING);
										GL11.glDisable(GL11.GL_BLEND);
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
								TileEntitySkullRenderer.field_147536_b.func_152674_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, armorStack.getItemDamage(), gp);

								GL11.glPopMatrix();
							}
						}

					}

			//Traveller's Gear
			GL11.glTranslatef(0,-.05f,0);
			GL11.glRotatef(180, 1, 0, 0);
			for(int ieq=0;ieq<4;ieq++)
				if(tile.getStackInSlot(4+ieq)!=null && tile.renderBaubles[ieq])
					ClientProxy.renderTravellersItem(tile.getStackInSlot(4+ieq), ieq, this.fakepl, this.renderPlayer, f);
			for(int ieq=0;ieq<3;ieq++)
				if(tile.getStackInSlot(8+ieq)!=null && tile.renderTravellersGear[ieq])
					ClientProxy.renderTravellersItem(tile.getStackInSlot(8+ieq), ieq, this.fakepl, this.renderPlayer, f);
			GL11.glRotatef(180, 1, 0, 0);
			GL11.glTranslatef(0,.05f,0);

			float playerScale = 1.03125f;
			GL11.glScalef(playerScale,playerScale,playerScale);
			GL11.glTranslatef(0,.2f,0);
			if(renderPlayer[0]||renderPlayer[1]||renderPlayer[2]||renderPlayer[3])
			{
				for(int its=0;its<4;its++)
					if(!renderPlayer[its])
						fakepl.inventory.armorInventory[3-its]=null;
				RenderManager.instance.renderEntitySimple(fakepl, 0);
			}
			GL11.glScalef(1/playerScale,1/playerScale,1/playerScale);

			this.bindTexture(TextureMap.locationItemsTexture);
			GL11.glRotatef(180, 1, 0, 0);
			GL11.glRotatef(180, 0, 0, 1);
			GL11.glRotated(-.3f*(180/Math.PI), 1, 0, 0);
			GL11.glScaled(.25,1,.25);
			GL11.glTranslated(-.5,-.6625,-2.6);


			GL11.glRotated(90, 1,0,0);

			//0-3 baubles
			//4-6 TG
			//7-9 Mari
			//10 Glove
			float zOffset = .15f;
			float[] hscale =
				{
					.625f,
					.5f,
					.5f,
					1,
					.5f,
					1,
					1,
					.5f,
					.5f,
					.5f,
					.75f
				};

			float[] rotation = new float[11];
			rotation[6]= -45;

			float[] scaleForType = {1,.5f,1.25f};
			float[][] posNormal = {{1.5f,-.3f}, {-1.5f,-.3f}, {-.15f,-1.2f}, {.8f,-.6f}};
			int usedNormal = 0;
			float[][] posTiny = {{-1.5f,-1.5f}, {-1f,-1.5f}, {-.5f,-1.5f}, {-1.45f,-1f}, {-.9f,-.8f}};
			int usedTiny = 0;
			float[][] posBig = {{1.2f,-1.6f}, {-.3f,-.8f}};
			int usedBig = 0;

			int occupiedTiny=0;
			for(int dis=0;dis<11;dis++)
				if(tile.getStackInSlot(dis+4)!=null)
				{
					int type = dis==1||dis==2||dis==7||dis==8||dis==9?1: dis==3||dis==4?2: 0;
					if(type==1)
						occupiedTiny++;
				}


			GL11.glColor4f(1, 1, 1, 1);
			if(tile.renderTable)
				for(int dis=0;dis<11;dis++)
				{
					int type = dis==1||dis==2||dis==7||dis==8||dis==9?1: dis==3||dis==4?2: 0;
					//0=normal, 1=tiny, 2=big, 3=small
					if(dis==10 && usedBig<posBig.length)
						type = 2;

					if(dis<4 && !tile.displayBaubles[dis])
						continue;
					if(dis>=4 && dis<7 && !tile.displayTravellersGear[dis-4])
						continue;
					if(dis>=7 && dis<10 && !tile.renderMari[dis-7])
						continue;

					int slot = dis+4;
					if(tile.getStackInSlot(slot)!=null)
					{
						float scale = scaleForType[type];
						double oX = type==2?posBig[usedBig][0]: type==1?posTiny[usedTiny][0]: posNormal[usedNormal][0];
						double oY = type==2?posBig[usedBig][1]: type==1?posTiny[usedTiny][1]: posNormal[usedNormal][1];
						if(dis==5 && occupiedTiny<4)
						{
							scale = 1.25f;
							oY -= .375;
						}

						GL11.glTranslated(oX,oY,zOffset);
						GL11.glScaled(scale,scale,hscale[dis]);
						GL11.glRotatef(rotation[dis], 0, 0, 1);

						if(!ForgeHooksClient.renderInventoryItem(RenderBlocks.getInstance(), Minecraft.getMinecraft().renderEngine, tile.getStackInSlot(slot), true, 0, 0, 0))
							this.renderStackOnDisplay(tile.getStackInSlot(slot));

						GL11.glRotatef(-rotation[dis], 0, 0, 1);
						GL11.glScaled(1/scale,1/scale,1/hscale[dis]);
						GL11.glTranslated(-oX,-oY,-zOffset);

						if(type==2 && usedBig<posBig.length)
							usedBig++;
						if(type==1 && usedTiny<posTiny.length)
							usedTiny++;
						if(type==0 && usedNormal<posNormal.length)
							usedNormal++;
					}
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
		switch(slot)
		{
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

	void renderStackOnDisplay(ItemStack stack)
	{
		for(int pass=0;pass<stack.getItem().getRenderPasses(stack.getItemDamage());pass++)
		{
			int col = stack.getItem().getColorFromItemStack(stack, pass);
			GL11.glColor3f((col>>16&255)/255f,(col>>8&255)/255f,(col&255)/255f);
			IIcon iicon = stack.getItem().getIcon(stack, pass);
			ItemRenderer.renderItemIn2D(Tessellator.instance,  iicon.getMaxU(), iicon.getMinV(), iicon.getMinU(), iicon.getMaxV(), iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);
			GL11.glColor3f(1,1,1);
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