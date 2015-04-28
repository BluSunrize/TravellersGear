package travellersgear.common.network.old;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

/**
 * fully stolen from TinkersConstruct
 * 
 * love you guys! =)
 * @author sirgingalot
 */
public abstract class AbstractPacket
{
	public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer);
	public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer);

	public abstract void handleClientSide(EntityPlayer player);
	public abstract void handleServerSide(EntityPlayer player);
}