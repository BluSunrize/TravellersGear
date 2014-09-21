package travellersgear.common.items;

import java.util.List;
import java.util.Random;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import travellersgear.TravellersGear;
import travellersgear.api.ITravellersGear;
import travellersgear.client.ModelCloak;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;

public class ItemTravellersGear extends Item implements IBauble, ITravellersGear
{
	public static String[] subNames = {"cloak","belt","ringGold","ringSilver","pauldrons","vambraces", "title"};
	IIcon[] icons = new IIcon[subNames.length];

	public ItemTravellersGear()
	{
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.setMaxStackSize(1);
	}

	@Override
	public void registerIcons(IIconRegister ir)
	{
		for(int i=0;i<icons.length;i++)
			this.icons[i] = ir.registerIcon("travellersgear:simplegear_"+subNames[i]);
	}
	@Override
	public IIcon getIconFromDamage(int meta)
	{
		//		System.out.println("Getting Icon for Damage: "+stack.getItemDamage());
		return this.icons[meta];
	}
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for(int i=0;i<subNames.length;i++)
			list.add(new ItemStack(this,1,i));
	}
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return this.getUnlocalizedName()+"."+subNames[stack.getItemDamage()];
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
	{
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("title"))
			list.add(StatCollector.translateToLocal(stack.getTagCompound().getString("title")));
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	{
		if( !subNames[stack.getItemDamage()].startsWith("cloak") )
			return null;
		return "travellersgear:textures/models/cloak.png";
	}
	@Override
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot)
	{
		if( !subNames[stack.getItemDamage()].startsWith("cloak") )
			return null;
		return new ModelCloak(getColorFromItemStack(stack, 0));
	}

	@Override
	public int getSlot(ItemStack stack)
	{
		String subName = subNames[stack.getItemDamage()];
		if(subName.startsWith("cloak"))
			return 0;
		else if(subName.startsWith("pauldrons"))
			return 1;
		else if(subName.startsWith("vambraces"))
			return 2;
		else if(subName.startsWith("title"))
			return 3;
		return -1;
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityLivingBase living)
	{
		return true;
	}

	@Override
	public boolean canUnequip(ItemStack stack, EntityLivingBase living)
	{
		return true;
	}

	@Override
	public BaubleType getBaubleType(ItemStack stack)
	{
		String subName = subNames[stack.getItemDamage()];
		if(subName.startsWith("ring"))
			return BaubleType.RING;
		else if(subName.startsWith("belt"))
			return BaubleType.BELT;
		else if(subName.startsWith("necklace"))
			return BaubleType.AMULET;
		return null;
	}

	@Override
	public void onEquipped(ItemStack stack, EntityLivingBase living)
	{
		onEquippedOrLoaded(stack,living);
		//System.out.println("Bauble Equipped ("+stack+") on "+(living.worldObj.isRemote?"Client":"Server")+"World");
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase living)
	{
		//System.out.println("Bauble Unequipped ("+stack+") on "+(living.worldObj.isRemote?"Client":"Server")+"World");
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase living)
	{
		if(living.ticksExisted == 1)
			onEquippedOrLoaded(stack, living);
	}

	public void onEquippedOrLoaded(ItemStack stack, EntityLivingBase living)
	{
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		IInventory baubles = BaublesApi.getBaubles(player);
		for(int i = 0; i < baubles.getSizeInventory(); i++)
			if(baubles.getStackInSlot(i) == null && baubles.isItemValidForSlot(i, stack)) {
				if(!world.isRemote)
				{
					baubles.setInventorySlotContents(i, stack.copy());
					if(!player.capabilities.isCreativeMode)
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				}
				onEquipped(stack, player);
				break;
			}

		return stack;
	}


	@Override
	public void onTravelGearTick(EntityPlayer player, ItemStack stack)
	{
	}
	@Override
	public void onTravelGearEquip(EntityPlayer player, ItemStack stack)
	{
		//System.out.println("TrvlGear Equipped ("+stack+") on "+(player.worldObj.isRemote?"Client":"Server")+"World");
	}

	@Override
	public void onTravelGearUnequip(EntityPlayer player, ItemStack stack)
	{
		//System.out.println("TrvlGear Unequipped ("+stack+") on "+(player.worldObj.isRemote?"Client":"Server")+"World");
	}

	//	@Override
	//	public boolean requiresMultipleRenderPasses()
	//	{
	//		return true;
	//	}
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass)
	{
		if( !subNames[stack.getItemDamage()].startsWith("cloak") || !stack.hasTagCompound())
			return 0xffffff;
		else
		{
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("display");
			if(tag == null)
				return 0xffffff;
			//			System.out.println("gettign colour for cloak");
			return tag.hasKey("colour")?tag.getInteger("colour") : 0xffffff;
		}
	}
	public void setColorForItemStack(ItemStack stack, int colour)
	{
		if( !subNames[stack.getItemDamage()].startsWith("cloak") )
			return;
		else
		{
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("display");
			tag.setInteger("colour",colour);
			stack.getTagCompound().setTag("display",tag);
		}
	}

	public static class WeightedRandomTitleScroll extends WeightedRandomChestContent
	{
		public WeightedRandomTitleScroll()
		{
			super(new ItemStack(TravellersGear.simpleGear,1,6), 1,1, 8);
		}

		@Override
		protected ItemStack[] generateChestContent(Random random, IInventory newInventory)
		{
			ItemStack s = theItemId.copy();
			s.setTagCompound(new NBTTagCompound());
			s.getTagCompound().setString("title", "TG.personaltitle."+titles[random.nextInt(titles.length)]);
			return new ItemStack[]{s};
		}
		static String[] titles = {"treepuncher","titan","librarian","bursar","archchancellor","justicar","explorer","defender","seeker"};
	}
}