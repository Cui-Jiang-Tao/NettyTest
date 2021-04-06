package cn;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable                                        //1
public class EchoServerHandler extends
        ChannelInboundHandlerAdapter {

    static int length = 0;
    static byte tmp;
    static byte[] b = null;
    @Override
    public void channelRead(ChannelHandlerContext ctx,
                            Object msg) throws InvalidProtocolBufferException {
        ByteBuf in = (ByteBuf) msg;

        if(length == 0)
        {
            if(in.readableBytes() < 4)
            {
                if(b == null)
                {
                    b = new byte[in.readableBytes()];
                    in.readBytes(b);
                }
                else
                {
                    byte[] tmp = new byte[in.readableBytes()];

                }


            }
            length = in.readInt();

            if(in.readableBytes() > 0)
            {
                byte[] readByte = new byte[in.readableBytes()];
                in.readBytes(readByte);
                b = readByte;
            }
        }
        else
        {
            byte[] readByte = new byte[in.readableBytes()];
            in.readBytes(readByte);

            int bLength = 0;
            if(b != null)
            {
                bLength = b.length;
            }
            byte[] tmp = new byte[bLength + readByte.length];
            if(bLength > 0)
            {
                System.arraycopy(b, 0, tmp, 0, b.length);
            }

            System.arraycopy(readByte, 0, tmp, bLength, readByte.length);

            b = tmp;
            if(b.length >= length)
            {
                System.out.println("length: " + b.length);

                byte[] data = new byte[length];
                System.arraycopy(b, 0, data, 0, length);
                Test.Person p = Test.Person.parseFrom(data);
                System.out.println("Server received: " + p.getEmail());
                System.out.println("Server received id: " + p.getId());    //2


            }
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)//4
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();                //5
        ctx.close();                            //6
    }
}
