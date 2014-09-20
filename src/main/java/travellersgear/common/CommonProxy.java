package travellersgear.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import travellersgear.common.blocks.TileEntityArmorStand;
import travellersgear.common.inventory.ContainerArmorStand;
import travellersgear.common.inventory.ContainerTravellersInv;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{
	public void init()
	{

	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch(ID)
		{
		case 0:
			return new ContainerTravellersInv(player.inventory);
		case 1:
			return new ContainerArmorStand(player.inventory, (TileEntityArmorStand) world.getTileEntity(x, y, z));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
}