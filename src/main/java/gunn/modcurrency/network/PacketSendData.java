package gunn.modcurrency.network;

import gunn.modcurrency.blocks.BlockVendor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-11-06.
 */
public class PacketSendData implements IMessage{
    private BlockPos blockPos;
    private int data,mode;
    
    public PacketSendData(){}
    
    public void setData(int data, BlockPos pos, int mode) {
        this.blockPos = pos;
        this.data = data;
        this.mode = mode;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        data = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(data);
        buf.writeInt(mode);
    }
    
    public static class Handler implements IMessageHandler<PacketSendData, IMessage>{

        @Override
        public IMessage onMessage(final PacketSendData message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }
        
        private void handle(PacketSendData message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            World world = playerEntity.worldObj;
            
            switch(message.mode){
                case 0:     //BlockVendor set Lock
                    BlockVendor block = (BlockVendor)world.getBlockState(message.blockPos).getBlock();
                    block.getTile(world,message.blockPos).setField(1,message.data);
            }
        }
    }
}
