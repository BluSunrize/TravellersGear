package travellersgear.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import travellersgear.common.util.ModCompatability;
import baubles.api.BaubleType;
import baubles.api.IBauble;

public class SlotRestricted extends Slot
{
	public int slotLimit = 1;
	public SlotType type;
	EntityPlayer player;

	public SlotRestricted(IInventory inv, int id, int x, int y, EntityPlayer player, SlotType type)
	{
		super(inv, id, x, y);
		this.player = player;
		this.type = type;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		if(stack==null || stack.getItem()==null)
			return false;
		switch(this.type)
		{
		case VANILLA_HELM:
		case VANILLA_CHEST:
		case VANILLA_LEGS:
		case VANILLA_BOOTS:
			return stack.getItem().isValidArmor(stack, this.type.ordinal(), player);
		case TRAVEL_CLOAK:
		case TRAVEL_SHOULDER:
		case TRAVEL_VAMBRACE:
		case TRAVEL_TITLE:
			return ModCompatability.getTravellersGearSlot(stack) == this.type.ordinal()-4;
		case BAUBLE_BELT:
			return stack.getItem() instanceof IBauble && ((IBauble)stack.getItem()).getBaubleType(stack) == BaubleType.BELT && ((IBauble)stack.getItem()).canEquip(stack, this.player);
		case BAUBLE_NECK:
			return stack.getItem() instanceof IBauble && ((IBauble)stack.getItem()).getBaubleType(stack) == BaubleType.AMULET && ((IBauble)stack.getItem()).canEquip(stack, this.player);
		case BAUBLE_RING:
			return stack.getItem() instanceof IBauble && ((IBauble)stack.getItem()).getBaubleType(stack) == BaubleType.RING && ((IBauble)stack.getItem()).canEquip(stack, this.player);
		case MARI_BRACELET:
		case MARI_NECKLACE:
		case MARI_RING:
			String jewelType = ModCompatability.getMariJeweleryType(stack);
			return jewelType!=null && jewelType.equalsIgnoreCase(this.type.name().substring(5));
		case TINKERS_GLOVE:
			return ModCompatability.canEquipTConAccessory(stack, 1);
		case TINKERS_BAG:
			return ModCompatability.canEquipTConAccessory(stack, 2);
		case TINKERS_BELT:
			return ModCompatability.canEquipTConAccessory(stack, 3);
		case TINKERS_MASK:
			return ModCompatability.canEquipTConAccessory(stack, 0);
		case TINKERS_HEART_R:
			return ModCompatability.canEquipTConAccessory(stack, 6);
		case TINKERS_HEART_Y:
			return ModCompatability.canEquipTConAccessory(stack, 5);
		case TINKERS_HEART_G:
			return ModCompatability.canEquipTConAccessory(stack, 4);
		default:
			return false;
		}
	}

	public boolean isBaubleSlot()
	{
		return this.type==SlotType.BAUBLE_NECK || this.type==SlotType.BAUBLE_RING || this.type==SlotType.BAUBLE_BELT;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		if(getStack()==null)
			return false;
		switch(this.type)
		{
		case BAUBLE_BELT:
			return getStack().getItem() instanceof IBauble && ((IBauble)getStack().getItem()).getBaubleType(getStack()) == BaubleType.BELT && ((IBauble)getStack().getItem()).canUnequip(getStack(), this.player);
		case BAUBLE_NECK:
			return getStack().getItem() instanceof IBauble && ((IBauble)getStack().getItem()).getBaubleType(getStack()) == BaubleType.AMULET && ((IBauble)getStack().getItem()).canUnequip(getStack(), this.player);
		case BAUBLE_RING:
			return getStack().getItem() instanceof IBauble && ((IBauble)getStack().getItem()).getBaubleType(getStack()) == BaubleType.RING && ((IBauble)getStack().getItem()).canUnequip(getStack(), this.player);
		default:
			return true;
		}
	}

	@Override
	public int getSlotStackLimit()
	{
		return slotLimit;
	}

	public enum SlotType
	{
		VANILLA_HELM,
		VANILLA_CHEST,
		VANILLA_LEGS,
		VANILLA_BOOTS,
		TRAVEL_CLOAK,
		TRAVEL_SHOULDER,
		TRAVEL_VAMBRACE,
		TRAVEL_TITLE,
		BAUBLE_NECK,
		BAUBLE_RING,
		BAUBLE_BELT,
		MARI_NECKLACE,
		MARI_BRACELET,
		MARI_RING,
		TINKERS_GLOVE,
		TINKERS_BAG,
		TINKERS_BELT,
		TINKERS_MASK,
		TINKERS_HEART_R,
		TINKERS_HEART_Y,
		TINKERS_HEART_G;
	}
}