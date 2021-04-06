package cn;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.Random;

@ChannelHandler.Sharable                                //1
public class EchoClientHandler2 extends
        SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        Test.Person.Builder person = Test.Person.newBuilder();
        person.setId(1234);
        person.setEmail(getRandString());

        Test.Person p = person.build();

        byte[] b = p.toByteArray();
        System.out.println("length: " + b.length);

        channel.write(Unpooled.copyInt(b.length));
        channel.writeAndFlush(Unpooled.copiedBuffer(b));

        Test.People.Builder people = Test.People.newBuilder();
        people.setCount(990);
        people.setEmail(getRandString());
        people.setName(getRandString());

        byte[] pbytes = people.build().toByteArray();

        channel.write(Unpooled.copyInt(pbytes.length));
        channel.writeAndFlush(Unpooled.copiedBuffer(pbytes));
    }


    public void WritePBMsg()
    {

    }

    @Override       //acceptInboundMessage
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));    //3
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {                    //4
        cause.printStackTrace();
        ctx.close();
    }

    private String getRandString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 1024 * 10; i ++)
        {
            sb.append("asdasasdfasdfsd" +  new Random().nextInt())  ;
        }

        return sb.toString();
    }
}
