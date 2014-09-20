package travellersgear.common.util;

import java.util.ArrayList;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import travellersgear.common.items.ItemTravellersGear;

public class CloakColourizationRecipe implements IRecipe
{
	@Override
	public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World)
	{
		ItemStack itemstack = null;
		ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>();

		for(int i = 0; i < par1InventoryCrafting.getSizeInventory(); i++)
		{
			ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(i);

			if(itemstack1 != null)
			{
				if(itemstack == null && itemstack1.getItem().getClass()==ItemTravellersGear.class && ItemTravellersGear.subNames[itemstack1.getItemDamage()].startsWith("cloak"))
				{
					itemstack = itemstack1;
				}
				else
				{
					if(!Utils.isDye(itemstack1))
						return false;

					arraylist.add(itemstack1);
				}
			}
		}

		return (itemstack != null) && (!arraylist.isEmpty());
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting)
	{
		ItemStack itemstack = null;
		int[] aint = new int[3];
		int i = 0;
		int j = 0;
		ItemTravellersGear itemClass = null;

		for(int k = 0; k < par1InventoryCrafting.getSizeInventory(); k++)
		{
			ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(k);

			if(itemstack1 != null)
			{
				if(itemstack == null && itemstack1.getItem().getClass()==ItemTravellersGear.class && ItemTravellersGear.subNames[itemstack1.getItemDamage()].startsWith("cloak"))
				{
					itemstack = itemstack1.copy();
					itemstack.stackSize = 1;
					itemClass = (ItemTravellersGear) itemstack.getItem();

					int l = itemClass.getColorFromItemStack(itemstack, 0);
					float f = (l >> 16 & 0xFF) / 255.0F;
					float f1 = (l >> 8 & 0xFF) / 255.0F;
					float f2 = (l & 0xFF) / 255.0F;
					i = (int)(i + Math.max(f, Math.max(f1, f2)) * 255.0F);
					aint[0] = ((int)(aint[0] + f * 255.0F));
					aint[1] = ((int)(aint[1] + f1 * 255.0F));
					aint[2] = ((int)(aint[2] + f2 * 255.0F));
					j++;
				}
				else
				{
					if(!Utils.isDye(itemstack1))
						return null;

					float[] afloat = net.minecraft.entity.passive.EntitySheep.fleeceColorTable[net.minecraft.block.BlockColored.func_150032_b(Utils.getDamageForDye(itemstack1))];
					int j1 = (int)(afloat[0] * 255.0F);
					int k1 = (int)(afloat[1] * 255.0F);
					int i1 = (int)(afloat[2] * 255.0F);
					i += Math.max(j1, Math.max(k1, i1));
					aint[0] += j1;
					aint[1] += k1;
					aint[2] += i1;
					j++;
				}
			}
		}

		if(itemClass == null)
		{
			return null;
		}

		int k = aint[0] / j;
		int l1 = aint[1] / j;
		int l = aint[2] / j;
		float f = i / j;
		float f1 = Math.max(k, Math.max(l1, l));
		k = (int)(k * f / f1);
		l1 = (int)(l1 * f / f1);
		l = (int)(l * f / f1);
		int i1 = (k << 8) + l1;
		i1 = (i1 << 8) + l;
		itemClass.setColorForItemStack(itemstack, i1);
		return itemstack;
	}

	@Override
	public int getRecipeSize()
	{
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return null;
	}
}