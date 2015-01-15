package travellersgear.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import travellersgear.TravellersGear;
import travellersgear.client.BlockRenderArmorStand;

public class BlockArmorStand extends BlockContainer
{
	public BlockArmorStand()
	{
		super(Material.wood);
		setCreativeTab(TravellersGear.creativeTab);
		setHardness(2.5F);
		setResistance(10.0F);
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon("");
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return BlockRenderArmorStand.renderID;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		if(world.getBlockMetadata(x, y, z)==3)
			return true;
		return super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List list)
	{
		list.add(new ItemStack(item, 1, 0));
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int idk, float what, float these, float are)
	{
		if(!player.isSneaking())
		{
			if(!world.isRemote)
				player.openGui(TravellersGear.instance, 1, world, x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess, int x, int y, int z)
	{
		if(iBlockAccess.getTileEntity(x, y, z) instanceof TileEntityArmorStand)
		{
			float yMax = ((TileEntityArmorStand)iBlockAccess.getTileEntity(x, y, z)).renderArmor[0]?2 :((TileEntityArmorStand)iBlockAccess.getTileEntity(x, y, z)).renderArmor[1]?1.8125f : 1;
			float depth = ((TileEntityArmorStand)iBlockAccess.getTileEntity(x, y, z)).renderTable? 0 :.25f;
			switch (((TileEntityArmorStand)iBlockAccess.getTileEntity(x, y, z)).facing)
			{ 
			case 2:
			default:
				this.setBlockBounds(0,0,0+depth,1,yMax,1-depth);
				break;
			case 3:
				this.setBlockBounds(0,0,0+depth,1,yMax,1-depth);
				break;
			case 4:
				this.setBlockBounds(0+depth,0,0,1-depth,yMax,1);
				break;
			case 5:
				this.setBlockBounds(0+depth,0,0,1-depth,yMax,1);
				break;
			}
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		this.setBlockBoundsBasedOnState(world,x,y,z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		this.setBlockBoundsBasedOnState(world,x,y,z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack stack)
	{
		int playerViewQuarter = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		int f = playerViewQuarter==0 ? 2:playerViewQuarter==1 ? 5:playerViewQuarter==2 ? 3: 4;
		((TileEntityArmorStand)world.getTileEntity(x,y,z)).facing = f;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityArmorStand();
	}

	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
	{
		if(world.getTileEntity(x,y,z) instanceof TileEntityArmorStand)
		{
			TileEntityArmorStand tile = (TileEntityArmorStand)world.getTileEntity(x,y,z);

			for(int i=0;i<tile.getSizeInventory();i++)
			{
				ItemStack stack = tile.getStackInSlot(i);
				if (stack != null)
				{
					float f = world.rand.nextFloat() * 0.8F + 0.1F;
					float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
					EntityItem entityitem;
					for (float f2 = world.rand.nextFloat() * 0.8F + 0.1F; stack.stackSize > 0; world.spawnEntityInWorld(entityitem))
					{
						int k1 = world.rand.nextInt(21) + 10;
						if (k1 > stack.stackSize)
							k1 = stack.stackSize;
						stack.stackSize -= k1;
						entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(stack.getItem(), k1, stack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (float)world.rand.nextGaussian() * f3;
						entityitem.motionY = (float)world.rand.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float)world.rand.nextGaussian() * f3;

						if (stack.hasTagCompound())
						{
							entityitem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
						}
					}
				}
			}
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}

}