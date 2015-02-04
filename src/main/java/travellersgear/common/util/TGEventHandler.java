package travellersgear.common.util;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import travellersgear.TravellersGear;
import travellersgear.api.IEventGear;
import travellersgear.api.TGSaveData;
import travellersgear.api.TravellersGearAPI;
import travellersgear.client.ToolDisplayInfo;
import travellersgear.common.network.PacketNBTSync;
import travellersgear.common.network.PacketPipeline;
import travellersgear.common.network.PacketPlayerInventorySync;
import baubles.api.BaublesApi;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TGEventHandler
{
	TGSaveData worldData;

	@SubscribeEvent
	public void onLoad(WorldEvent.Load event)
	{
		if(event.world.provider.dimensionId==0)
			if(!event.world.isRemote)
			{
				worldData = (TGSaveData) event.world.loadItemData(TGSaveData.class, TGSaveData.dataName);
				if(worldData==null)
				{
					worldData = new TGSaveData(TGSaveData.dataName);
					event.world.setItemData(TGSaveData.dataName, worldData);
				}
				TGSaveData.setInstance(worldData);
			}
	}

	static HashMap<String, ItemStack[]> previousInv = new HashMap();
	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event)
	{
		if(event.type!=TickEvent.Type.PLAYER)
			return;

		if(event.phase.equals(TickEvent.Phase.START) && event.player!=null)
		{
			ItemStack[] prev = previousInv.get(event.player.getCommandSenderName());
//			NBTTagList list = TravellersGearAPI.getTravellersNBTData(event.player).getTagList("toolDisplay", 10);
			NBTTagList list = TravellersGearAPI.getDisplayTools(event.player);
			
			int[] targetedSlots = new int[list.tagCount()];
			for(int i=0;i<list.tagCount();i++)
				targetedSlots[i] = ToolDisplayInfo.readFromNBT(list.getCompoundTagAt(i)).slot;

			if(list!=null && targetedSlots!=null && targetedSlots.length>0)
				if(prev==null || prev.length!=targetedSlots.length)
					PacketPipeline.INSTANCE.sendToAll(new PacketPlayerInventorySync(event.player));
				else
				{
					boolean packet = false;
					for(int i=0; i<prev.length; i++)
						if(!ItemStack.areItemStacksEqual(prev[i], event.player.inventory.mainInventory[targetedSlots[i]]))
							packet=true;
					if(packet)
						PacketPipeline.INSTANCE.sendToAll(new PacketPlayerInventorySync(event.player));
				}


			for(ItemStack stack : TravellersGearAPI.getExtendedInventory(event.player))
				if(stack!=null && ModCompatability.getTravellersGearSlot(stack)!=-1)
					Utils.tickTravGear(event.player, stack);
		}
		if(event.phase.equals(TickEvent.Phase.END) && event.player!=null)
		{
//			NBTTagList list = TravellersGearAPI.getTravellersNBTData(event.player).getTagList("toolDisplay", 10);
			NBTTagList list = TravellersGearAPI.getDisplayTools(event.player);
			int[] targetedSlots = new int[list.tagCount()];
			for(int i=0;i<list.tagCount();i++)
				targetedSlots[i] = ToolDisplayInfo.readFromNBT(list.getCompoundTagAt(i)).slot;

			ItemStack[] newPrev = new ItemStack[targetedSlots.length];
			for(int i=0; i<newPrev.length; i++)
				newPrev[i] = event.player.inventory.mainInventory[targetedSlots[i]];
			previousInv.put(event.player.getCommandSenderName(), newPrev);
		}
	}

	@SubscribeEvent
	public void playerDrops(PlayerDropsEvent event)
	{
		if(!event.entityPlayer.worldObj.isRemote && !event.entityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
		{
			ItemStack[] tg = TravellersGearAPI.getExtendedInventory(event.entityPlayer);
			for(int i=0; i<tg.length; i++)
				if(tg[i]!=null)
				{
					EntityItem ei = new EntityItem(event.entityPlayer.worldObj, event.entityPlayer.posX,event.entityPlayer.posY,event.entityPlayer.posZ, tg[i]);
					ei.delayBeforeCanPickup = 40;
					float f1 = event.entityPlayer.worldObj.rand.nextFloat() * 0.5F;
					float f2 = event.entityPlayer.worldObj.rand.nextFloat() * 3.141593F * 2.0F;
					ei.motionX = (-MathHelper.sin(f2) * f1);
					ei.motionZ = (MathHelper.cos(f2) * f1);
					ei.motionY = 0.2000000029802322D;
					event.drops.add(ei);
					tg[i] = null;
				}
			TravellersGearAPI.setExtendedInventory(event.entityPlayer, tg);
			PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync(event.entityPlayer));
		}
	}

	@SubscribeEvent
	public void onLogin(PlayerLoggedInEvent event)
	{
		if(!event.player.worldObj.isRemote)
		{
			PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync(event.player));
		}
	}
	@SubscribeEvent
	public void onDimensionChange(PlayerChangedDimensionEvent event)
	{
		if(!event.player.worldObj.isRemote)
		{
			PacketPipeline.INSTANCE.sendToAll(new PacketNBTSync(event.player));
		}
	}

	@SubscribeEvent
	public void onPlayerDamaged(LivingHurtEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer)
			for(ItemStack stack : buildEventGearList((EntityPlayer) event.entityLiving))
				((IEventGear)stack.getItem()).onUserDamaged(event, stack);
	}
	@SubscribeEvent
	public void onPlayerAttacking(AttackEntityEvent event)
	{
		for(ItemStack stack : buildEventGearList(event.entityPlayer))
			((IEventGear)stack.getItem()).onUserAttacking(event, stack);
	}
	@SubscribeEvent
	public void onPlayerJump(LivingJumpEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer)
			for(ItemStack stack : buildEventGearList((EntityPlayer) event.entityLiving))
				((IEventGear)stack.getItem()).onUserJump(event, stack);
	}
	@SubscribeEvent
	public void onPlayerFall(LivingFallEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer)
			for(ItemStack stack : buildEventGearList((EntityPlayer) event.entityLiving))
				((IEventGear)stack.getItem()).onUserFall(event, stack);
	}
	@SubscribeEvent
	public void onPlayerTargeted(LivingSetAttackTargetEvent event)
	{
		if(event.target instanceof EntityPlayer)
			for(ItemStack stack : buildEventGearList((EntityPlayer) event.target))
				((IEventGear)stack.getItem()).onUserTargeted(event, stack);
	}

	public ItemStack[] buildEventGearList(EntityPlayer player)
	{
		ArrayList<ItemStack> list = new ArrayList();

		ItemStack[] is = player.inventory.armorInventory;
		for(int armor=0; armor<is.length; armor++)
			if(is[armor]!=null && is[armor].getItem() instanceof IEventGear)
				list.add(is[armor]);

		if(TravellersGear.BAUBLES)
		{
			IInventory inv = BaublesApi.getBaubles(player);
			for(int i=0; i<inv.getSizeInventory(); i++)
				if(inv.getStackInSlot(i)!=null && inv.getStackInSlot(i).getItem() instanceof IEventGear)
					list.add(inv.getStackInSlot(i));
		}

		is = TravellersGearAPI.getExtendedInventory(player);
		for(int tg=0; tg<is.length; tg++)
			if(is[tg]!=null && is[tg].getItem() instanceof IEventGear)
				list.add(is[tg]);

		if(TravellersGear.MARI)
		{
			IInventory inv = ModCompatability.getMariInventory(player);
			for(int i=0; i<inv.getSizeInventory(); i++)
				if(inv.getStackInSlot(i)!=null && inv.getStackInSlot(i).getItem() instanceof IEventGear)
					list.add(inv.getStackInSlot(i));
		}
		if(TravellersGear.TCON)
		{
			IInventory inv = ModCompatability.getTConArmorInv(player);
			for(int i=1; i<3; i++)
				if(inv.getStackInSlot(i)!=null && inv.getStackInSlot(i).getItem() instanceof IEventGear)
					list.add(inv.getStackInSlot(i));
		}
		if(player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() instanceof IEventGear)
			list.add(list.size()/2, player.getCurrentEquippedItem());

		return list.toArray(new ItemStack[0]);
	}
}