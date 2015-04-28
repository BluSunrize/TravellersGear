package travellersgear.common.network.old;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

import org.apache.logging.log4j.Level;

import travellersgear.TravellersGear;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * fully stolen from TinkersConstruct
 * 
 * love you guys! =)
 * @author sirgingalot
 */
@ChannelHandler.Sharable
public class PacketPipeline extends MessageToMessageCodec<FMLProxyPacket, AbstractPacket>
{
	private EnumMap<Side, FMLEmbeddedChannel> channels;
	private LinkedList<Class<? extends AbstractPacket>> packets = new LinkedList<Class<? extends AbstractPacket>>();
	private boolean isPostInitialised = false;
//	public static PacketPipeline INSTANCE = new PacketPipeline();
	
	public boolean registerPacket (Class<? extends AbstractPacket> clazz)
	{
		if (this.packets.size() > 256)
		{
			TravellersGear.logger.log(Level.ERROR, "Too many packets registered!");
			return false;
		}
		if (this.packets.contains(clazz))
		{
			TravellersGear.logger.log(Level.ERROR, "Packet already registered! "+clazz);
			return false;
		}
		if (this.isPostInitialised)
		{
			TravellersGear.logger.log(Level.ERROR, "PacketHandler is past its init!");
			return false;
		}
		this.packets.add(clazz);
		return true;
	}

	@Override
	protected void encode (ChannelHandlerContext ctx, AbstractPacket msg, List<Object> out) throws Exception
	{
		ByteBuf buffer = Unpooled.buffer();
		Class<? extends AbstractPacket> clazz = msg.getClass();
		if (!this.packets.contains(msg.getClass()))
		{
			throw new NullPointerException("No Packet Registered for: " + msg.getClass().getCanonicalName());
		}
		byte discriminator = (byte) this.packets.indexOf(clazz);
		buffer.writeByte(discriminator);
		msg.encodeInto(ctx, buffer);
		FMLProxyPacket proxyPacket = new FMLProxyPacket(buffer.copy(), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
		out.add(proxyPacket);
	}
	// In line decoding and handling of the packet
	@Override
	protected void decode (ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception
	{
		ByteBuf payload = msg.payload();
		byte discriminator = payload.readByte();
		Class<? extends AbstractPacket> clazz = this.packets.get(discriminator);
		if (clazz == null)
		{
			throw new NullPointerException("No packet registered for discriminator: " + discriminator);
		}
		AbstractPacket pkt = clazz.newInstance();
		pkt.decodeInto(ctx, payload.slice());
		EntityPlayer player;
		switch (FMLCommonHandler.instance().getEffectiveSide())
		{
		case CLIENT:
			player = this.getClientPlayer();
			pkt.handleClientSide(player);
			break;
		case SERVER:
			INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
			player = ((NetHandlerPlayServer) netHandler).playerEntity;
			pkt.handleServerSide(player);
			break;
		default:
		}
	}
	// Method to call from FMLInitializationEvent
	public void initalise ()
	{
		this.channels = NetworkRegistry.INSTANCE.newChannel(TravellersGear.MODID, this);
		registerPackets();
	}
	public void registerPackets ()
	{
		registerPacket(PacketActiveAbility.class);
		registerPacket(PacketItemShoutout.class);
		registerPacket(PacketNBTSync.class);
		registerPacket(PacketOpenGui.class);
		registerPacket(PacketPlayerInventorySync.class);
		registerPacket(PacketRequestNBTSync.class);
		registerPacket(PacketSlotSync.class);
		registerPacket(PacketTileUpdate.class);
	}

	public void postInitialise()
	{
		if (this.isPostInitialised)
			return;
		this.isPostInitialised = true;
		Collections.sort(this.packets, new Comparator<Class<? extends AbstractPacket>>()
				{
			@Override
			public int compare (Class<? extends AbstractPacket> clazz1, Class<? extends AbstractPacket> clazz2)
			{
				int com = String.CASE_INSENSITIVE_ORDER.compare(clazz1.getCanonicalName(), clazz2.getCanonicalName());
				if (com == 0)
				{
					com = clazz1.getCanonicalName().compareTo(clazz2.getCanonicalName());
				}
				return com;
			}
				});
	}
	@SideOnly(Side.CLIENT)
	private EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().thePlayer;
	}

	public void sendToAll(AbstractPacket message)
	{
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		this.channels.get(Side.SERVER).writeAndFlush(message);
	}
	public void sendTo(AbstractPacket message, EntityPlayerMP player)
	{
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		this.channels.get(Side.SERVER).writeAndFlush(message);
	}
	public void sendToAllAround(AbstractPacket message, NetworkRegistry.TargetPoint point)
	{
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
		this.channels.get(Side.SERVER).writeAndFlush(message);
	}
	public void sendToDimension(AbstractPacket message, int dimensionId)
	{
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
		this.channels.get(Side.SERVER).writeAndFlush(message);
	}
	public void sendToServer(AbstractPacket message)
	{
		this.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		this.channels.get(Side.CLIENT).writeAndFlush(message);
	}
}