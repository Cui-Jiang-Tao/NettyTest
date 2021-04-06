package cn;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable                                //1
public class EchoClientHandlerA extends
        ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();

        channel.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", //2
                CharsetUtil.UTF_8));

        ctx.fireChannelActive();
    }

    @Override       //acceptInboundMessage
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf)msg;

        System.out.println(this.getClass().getName() + in.toString(CharsetUtil.UTF_8) + "A解码");    //3
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {                    //4
        cause.printStackTrace();
        ctx.close();
    }
}

@ChannelHandler.Sharable                                //1
class EchoClientHandlerB extends
        ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();

        channel.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", //2
                CharsetUtil.UTF_8));

        ctx.fireChannelActive();
    }

    @Override       //acceptInboundMessage
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf)msg;

        System.out.println(this.getClass().getName() + in.toString(CharsetUtil.UTF_8) + "A解码");    //3
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {                    //4
        cause.printStackTrace();
        ctx.close();
    }
}

