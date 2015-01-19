package travellersgear.common.util;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import travellersgear.common.network.PacketOpenGui;
import travellersgear.common.network.PacketPipeline;

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
		if(sender instanceof EntityPlayer && args.length>=1 && args[0].equalsIgnoreCase("gui") && ((EntityPlayer)sender).worldObj.isRemote)
		{
			PacketPipeline.INSTANCE.sendToServer(new PacketOpenGui((EntityPlayer) sender,2));
		}
		if(sender instanceof EntityPlayer && args.length>=1 && args[0].equalsIgnoreCase("toolDisplay") && ((EntityPlayer)sender).worldObj.isRemote)
		{
			PacketPipeline.INSTANCE.sendToServer(new PacketOpenGui((EntityPlayer) sender,3));
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if(args==null || (args.length==1&&args[0].isEmpty()))
			return Arrays.asList(new String[]{"gui","toolDisplay"});
		return null;
	}

}