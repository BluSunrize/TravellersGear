package travellersgear.common.util;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import scala.actors.threadpool.Arrays;
import travellersgear.TravellersGear;
import travellersgear.common.network.PacketOpenGui;

public class TGClientCommand extends CommandBase
{

	@Override
	public int getRequiredPermissionLevel()
	{
		return 4;
	}

	@Override
	public String getCommandName() 
	{
		return "travellersgear";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(sender instanceof EntityPlayer && args.length>=1 && args[0].equalsIgnoreCase("gui"))
		{
			TravellersGear.instance.packetPipeline.sendToServer(new PacketOpenGui((EntityPlayer) sender,2));
			//			System.out.println(sender.getEntityWorld().isRemote);
//			Minecraft.getMinecraft().displayGuiScreen(new GuiTravellersInvCustomization((EntityPlayer) sender));
//			((EntityPlayer)sender).openGui(TravellersGear.instance, 2, sender.getEntityWorld(), sender.getPlayerCoordinates().posX, sender.getPlayerCoordinates().posY, sender.getPlayerCoordinates().posZ);
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if(args==null || (args.length==1&&args[0].isEmpty()))
			return Arrays.asList(new String[]{"gui"});
		return null;
	}

}