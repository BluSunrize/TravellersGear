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
import net.minecraftforge.common.ChestGenHooks;
import travellersgear.TravellersGear;
import travellersgear.api.ITravellersGear;
import travellersgear.client.ModelCloak;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTravellersGear extends Item implements IBauble, ITravellersGear
{
	public static String[] subNames = {"cloak","belt","ringGold","ringSilver","pauldrons","vambraces", "title"};
	IIcon[] icons = new IIcon[subNames.length];
	static String[] titles = {"treepuncher","titan","librarian","bursar","archchancellor","justicar","explorer","defender","seeker","boxFox","freshPrince"};

	public ItemTravellersGear()
	{
		this.setHasSubtypes(true);
		this.setCreativeTab(TravellersGear.creativeTab);
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
		if(meta>=0 && meta<icons.length)
			return this.icons[meta];
		return icons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for(int i=0;i<subNames.length;i++)
			list.add(new ItemStack(this,1,i));

		for(String tit : titles)
		{
			ItemStack scr = new ItemStack(this,1,6);
			scr.setTagCompound(new NBTTagCompound());
			scr.getTagCompound().setString("title", "TG.personaltitle."+tit);
			list.add(scr);
		}
	}
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		String subName = stack.getItemDamage()<subNames.length?subNames[stack.getItemDamage()]:"";
		return this.getUnlocalizedName()+"."+subName;
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
	{
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("title"))
			list.add(StatCollector.translateToLocal(stack.getTagCompound().getString("title")));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	{
		String subName = stack.getItemDamage()<subNames.length?subNames[stack.getItemDamage()]:"";
		if( !subName.startsWith("cloak") )
			return null;
		return "travellersgear:textures/models/cloak.png";
	}
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot)
	{
		String subName = stack.getItemDamage()<subNames.length?subNames[stack.getItemDamage()]:"";
		if( !subName.startsWith("cloak") )
			return null;
		return new ModelCloak(getColorFromItemStack(stack, 0));
	}

	@Override
	public int getSlot(ItemStack stack)
	{
		String subName = stack.getItemDamage()<subNames.length?subNames[stack.getItemDamage()]:"";
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
		String subName = stack.getItemDamage()<subNames.length?subNames[stack.getItemDamage()]:"";
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
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase living)
	{
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
		if(baubles!=null)
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
	}

	@Override
	public void onTravelGearUnequip(EntityPlayer player, ItemStack stack)
	{
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int pass)
	{
		String subName = stack.getItemDamage()<subNames.length?subNames[stack.getItemDamage()]:"";
		if( !subName.startsWith("cloak") || !stack.hasTagCompound())
			return 0xffffff;
		else
		{
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("display");
			if(tag == null)
				return 0xffffff;
			return tag.hasKey("colour")?tag.getInteger("colour") : 0xffffff;
		}
	}
	public void setColorForItemStack(ItemStack stack, int colour)
	{
		String subName = stack.getItemDamage()<subNames.length?subNames[stack.getItemDamage()]:"";
		if( !subName.startsWith("cloak") )
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

	@Override
	public WeightedRandomChestContent getChestGenBase(ChestGenHooks chest, Random random, WeightedRandomChestContent original)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("title", "TG.personaltitle."+titles[random.nextInt(titles.length)]);
		original.theItemId.setTagCompound(tag);

		return original;
	}
}