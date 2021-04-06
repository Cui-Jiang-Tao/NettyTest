package cn.msg;

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

    public void sendMsg(Channel channel, Test.MSG.Builder msg) {
        byte[] b = msg.build().toByteArray();

        System.out.println(b.length);
        channel.write(Unpooled.copyInt(b.length));
        channel.writeAndFlush(Unpooled.copiedBuffer(b));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        Test.Person.Builder person = Test.Person.newBuilder();
        person.setId(1234);
        person.setEmail(getRandString());

        Test.MSG.Builder msg1 = Test.MSG.newBuilder();
        msg1.setMsgType(Test.MsgType.MSG_TYPE_PERSON);
        msg1.setPerson(person);

        sendMsg(channel, msg1);



        Test.People.Builder people = Test.People.newBuilder();
        people.setCount(990);
        people.setEmail(getRandString());
        people.setName(getRandString());

        Test.MSG.Builder msg2 = Test.MSG.newBuilder();
        msg2.setMsgType(Test.MsgType.MSG_TYPE_PEOPLE);
        msg2.setPeople(people);

        sendMsg(channel, msg2);

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
