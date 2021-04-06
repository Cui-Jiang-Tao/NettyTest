package cn;

import com.google.protobuf.CodedOutputStream;
import com.google.rpc.Code;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

@ChannelHandler.Sharable                                //1
public class EchoClientHandler extends
        SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        Test.Person.Builder person = Test.Person.newBuilder();
        person.setId(1234);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 1024 * 10; i ++)
        {
            sb.append("asdasasdfasdfsd");
        }
        person.setEmail(sb.toString());
        Test.Person p = person.build();

        byte[] b = p.toByteArray();
        System.out.println("length: " + b.length);
        int length = b.length;

        channel.write(Unpooled.copyInt(b.length));
        channel.writeAndFlush(Unpooled.copiedBuffer(b));


    }

    @Override       //acceptInboundMessage
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        System.out.println("Client received11111: " + in.toString(CharsetUtil.UTF_8));    //3
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {                    //4
        cause.printStackTrace();
        ctx.close();
    }
}
