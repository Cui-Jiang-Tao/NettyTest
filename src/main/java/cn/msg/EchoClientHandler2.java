package cn.msg;

import com.google.protobuf.AbstractMessage;
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

    public void sendMsg(Channel channel,  int type, AbstractMessage msg) {
        byte[] b = msg.toByteArray();

        System.out.println(b.length);
        channel.write(Unpooled.copyInt(b.length + 4));
        channel.write(Unpooled.copyInt(type));
        channel.writeAndFlush(Unpooled.copiedBuffer(b));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        Test.Person.Builder person = Test.Person.newBuilder();
        person.setId(1234);
        person.setEmail(getRandString());
        sendMsg(channel, Test.MsgType.MSG_TYPE_PERSON.ordinal(), person.build());

        Test.People.Builder people = Test.People.newBuilder();
        people.setCount(990);
        people.setEmail(getRandString());
        people.setName(getRandString());
        sendMsg(channel, Test.MsgType.MSG_TYPE_PEOPLE.ordinal(), people.build());
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
