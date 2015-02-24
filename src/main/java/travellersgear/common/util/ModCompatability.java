package travellersgear.common.util;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import travellersgear.TravellersGear;
import travellersgear.api.ITravellersGear;
import cpw.mods.fml.common.Loader;

public class ModCompatability
{
	static Class<?> clazz_TPlayerStats;
	static Class<?> clazz_IAccessory;
	static Class<?> clazz_ArmorControls;
	static Method method_canEquipAccessory;
	static Method method_openKnapsackGui;
	public static IInventory getTConArmorInv(EntityPlayer player)
	{
		IInventory armorInv = null;
		if(TravellersGear.TCON)
			try{
				if(clazz_TPlayerStats==null)
					clazz_TPlayerStats = Class.forName("tconstruct.armor.player.TPlayerStats");
				Object tPStatsInstance = clazz_TPlayerStats.getMethod("get", EntityPlayer.class).invoke(null, player);
				armorInv = (IInventory) clazz_TPlayerStats.getField("armor").get(tPStatsInstance);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		return armorInv;
	}
	public static boolean canEquipTConAccessory(ItemStack stack, int slot)
	{
		if(!Loader.isModLoaded("TConstruct") || stack==null)
			return false;
		try{
			Item item = stack.getItem();
			if(clazz_IAccessory==null)
				clazz_IAccessory = Class.forName("tconstruct.library.accessory.IAccessory");
			boolean hasInterface = clazz_IAccessory.isAssignableFrom(item.getClass());
			if(!hasInterface)
				return false;

			return (Boolean) item.getClass().getMethod("canEquipAccessory", ItemStack.class, int.class).invoke(item, stack, slot);

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	public static void openTConKnapsack()
	{
		if(Loader.isModLoaded("TConstruct"))
			try{
				if(clazz_ArmorControls==null)
					try{clazz_ArmorControls = Class.forName("tconstruct.client.ArmorControls");}catch(Exception e){}
				if(clazz_ArmorControls==null)
					clazz_ArmorControls = Class.forName("tconstruct.client.TControls");
				if(method_canEquipAccessory==null)
					method_canEquipAccessory = clazz_ArmorControls.getMethod("openKnapsackGui", new Class[0]);
				method_canEquipAccessory.invoke(null);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	static Class<?> clazz_JewelryHandler;
	static Class<?> clazz_InventoryMirror;
	static Method method_getType;

	public static String getMariJeweleryType(ItemStack stack)
	{
		if(TravellersGear.MARI)
		{
			try{
				if(clazz_JewelryHandler==null)
					clazz_JewelryHandler = Class.forName("mariculture.magic.JewelryHandler");
				if(method_getType==null)
					method_getType = clazz_JewelryHandler.getMethod("getType", ItemStack.class);
				Object jType = method_getType.invoke(null, stack);
				return jType.toString();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	public static IInventory getMariInventory(EntityPlayer player)
	{
		if(TravellersGear.MARI)
		{
			try{
				if(clazz_InventoryMirror==null)
					clazz_InventoryMirror = Class.forName("mariculture.magic.InventoryMirror");
				return (IInventory) clazz_InventoryMirror.getConstructor(EntityPlayer.class).newInstance(player);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	public static boolean isMariJewelry(ItemStack stack)
	{
		String t = getMariJeweleryType(stack);
		return t!=null && !t.isEmpty() && !t.equalsIgnoreCase("NONE");
	}


	static Class<?> clazz_PlayerHandler;
	static Class<? extends IInventory> clazz_InventoryBaubles;
	static Method method_setPlayerBaubles;
	static Method method_setEventHandler;
	public static void setPlayerBaubles(EntityPlayer player, IInventory invBaubles)
	{
		if(TravellersGear.BAUBLES)
		{
			try{
				if(clazz_InventoryBaubles==null)
					clazz_InventoryBaubles = (Class<? extends IInventory>) Class.forName("baubles.common.container.InventoryBaubles");
				if(clazz_PlayerHandler==null)
					clazz_PlayerHandler = Class.forName("baubles.common.lib.PlayerHandler");
				if(method_setPlayerBaubles==null)
					method_setPlayerBaubles = clazz_PlayerHandler.getMethod("setPlayerBaubles", EntityPlayer.class,clazz_InventoryBaubles);
				method_setPlayerBaubles.invoke(null, player,invBaubles);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public static void setBaubleContainer(IInventory invBaubles, Container container)
	{
		if(TravellersGear.BAUBLES)
		{
			try{
				if(clazz_InventoryBaubles==null)
					clazz_InventoryBaubles = (Class<? extends IInventory>) Class.forName("baubles.common.container.InventoryBaubles");
				if(method_setEventHandler==null)
					method_setEventHandler = clazz_InventoryBaubles.getMethod("setEventHandler", Container.class);
				method_setEventHandler.invoke(invBaubles, container);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public static IInventory getNewBaublesInv(EntityPlayer player)
	{
		if(TravellersGear.BAUBLES)
		{
			try{
				if(clazz_InventoryBaubles==null)
					clazz_InventoryBaubles = (Class<? extends IInventory>) Class.forName("baubles.common.container.InventoryBaubles");
				return clazz_InventoryBaubles.getConstructor(EntityPlayer.class).newInstance(player);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	public static void setBaubleInvStacklist(IInventory invBaubles, IInventory otherInvBaubles)
	{
		if(TravellersGear.BAUBLES)
		{
			try{
				invBaubles.getClass().getField("stackList").set(invBaubles, otherInvBaubles.getClass().getField("stackList").get(otherInvBaubles));
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public static void baubleInvBlockEvents(IInventory invBaubles, boolean bool)
	{
		if(TravellersGear.BAUBLES)
		{
			try{
				invBaubles.getClass().getField("blockEvents").set(invBaubles, bool);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	static Class<?> clazz_NEIClientConfig;
	static Method method_isHidden;
	public static boolean isNeiHidden()
	{
		if(TravellersGear.NEI)
		{
			try
			{
				if(clazz_NEIClientConfig==null)
					clazz_NEIClientConfig = Class.forName("codechicken.nei.NEIClientConfig");
				if(method_isHidden==null)
					method_isHidden = clazz_NEIClientConfig.getMethod("isHidden", new Class[0]);
				return (Boolean) method_isHidden.invoke(null, new Object[0]);
			}
			catch (Exception ex){}
		}
		return true;
	}

	public static boolean isStackPseudoTravellersGear(ItemStack stack)
	{
		for(ComparableItemStack cis : TravellersGear.additionalTravelersGear.keySet())
			if(cis.equals(new ComparableItemStack(stack)))
				return true;
		return false;
	}
	public static Object[] getPseudoTravellersGearData(ItemStack stack)
	{
		for(ComparableItemStack cis : TravellersGear.additionalTravelersGear.keySet())
			if(cis.equals(new ComparableItemStack(stack)))
				return TravellersGear.additionalTravelersGear.get(cis);
		return new Object[0];
	}
	public static int getTravellersGearSlot(ItemStack stack)
	{
		if(stack.getItem() instanceof ITravellersGear)
			return ((ITravellersGear)stack.getItem()).getSlot(stack);
		Object[] data = getPseudoTravellersGearData(stack);
		if(data.length>0 && data[0] instanceof Integer)
			return (Integer) data[0];
		return -1;
	}

	static Class<?> clazz_Aspect;
	static Method method_getAspect;
	static Class<?> clazz_WandManager;
	static Method method_getTotalVisDiscount;
	public static float getTCVisDiscount(EntityPlayer player, String aspect)
	{
		if(TravellersGear.THAUM)
		{
			try{
				if(clazz_Aspect==null)
					clazz_Aspect = Class.forName("thaumcraft.api.aspects.Aspect");
				if(method_getAspect==null)
					method_getAspect = clazz_Aspect.getMethod("getAspect", String.class);
				if(clazz_WandManager==null)
					clazz_WandManager = Class.forName("thaumcraft.common.items.wands.WandManager");
				if(method_getTotalVisDiscount==null)
					method_getTotalVisDiscount = clazz_WandManager.getMethod("getTotalVisDiscount", EntityPlayer.class, clazz_Aspect);

				Object a = method_getAspect.invoke(null, aspect);
				return (Float) method_getTotalVisDiscount.invoke(null, player,a);
			}catch(Exception e){}
		}
		return 0;
	}
	static Class<?> clazz_UtilsFX;
	static Method method_drawTag;
	public static void drawTCAspect(int x, int y, String aspect)
	{
		if(TravellersGear.THAUM)
		{
			try{
				if(clazz_Aspect==null)
					clazz_Aspect = Class.forName("thaumcraft.api.aspects.Aspect");
				if(method_getAspect==null)
					method_getAspect = clazz_Aspect.getMethod("getAspect", String.class);
				if(clazz_UtilsFX==null)
					clazz_UtilsFX = Class.forName("thaumcraft.client.lib.UtilsFX");
				if(method_drawTag==null)
					method_drawTag = clazz_UtilsFX.getMethod("drawTag", int.class,int.class,clazz_Aspect,float.class,int.class,double.class);
				Object a = method_getAspect.invoke(null, aspect);
				method_drawTag.invoke(null, x,y,a,0,0,0);
			}catch(Exception e){}
		}
	}
}