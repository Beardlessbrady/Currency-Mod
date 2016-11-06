package gunn.modcurrency.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency Mod for Minecraft.
 *
 * The Currency Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-11-06.
 */
public class PacketSendItem implements IMessage{
    private String word;
    
    public PacketSendItem(String word){
        this.word = word;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        word = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf,word);

    }
    
    public static class Handler implements IMessageHandler<PacketSendItem, IMessage>{

        @Override
        public IMessage onMessage(final PacketSendItem message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }
        
        private void handle(PacketSendItem message, MessageContext ctx){
            System.out.println("Test Word:" + message.word);
        }
    }
}
