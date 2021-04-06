package cn.msg;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.nio.ByteBuffer;

public class ParseMsg {
    static CircleByteBuffer buf = new CircleByteBuffer();
    static byte[] completeMsg = null;
    static int headLenght = -1;

    private static void getBytes(ByteBuf in) {
        buf.putBytes(in);
    }

    private static boolean parseByteBuffer() throws InvalidProtocolBufferException {
        if (headLenght == -1) {
            if(buf.canReadSize() >= 4) {
                ByteBuffer buffer = buf.getByteBuffer(4);
                buffer.flip();
                headLenght = buffer.getInt();
            }
            else {
                return false;
            }
        }

        if(buf.canReadSize() >= headLenght) {
            completeMsg = buf.getBytes(headLenght);
            headLenght = -1;
            dealMsg();
        }
        else {
            return false;
        }

        return true;
    }

    private static void dealMsg() throws InvalidProtocolBufferException {
        System.out.println(completeMsg.length);
        Test.MSG msg = Test.MSG.parseFrom(completeMsg);

        if(msg.getMsgType() == Test.MsgType.MSG_TYPE_PERSON) {
            Test.Person p = msg.getPerson();
            System.out.println("Server received: " + p.getEmail());
            System.out.println("Server received id: " + p.getId());

        }
        else if(msg.getMsgType() == Test.MsgType.MSG_TYPE_PEOPLE) {
            Test.People p = msg.getPeople();
            System.out.println("Server received count: " + p.getCount());
            System.out.println("Server received email: " + p.getEmail());
            System.out.println("Server received name: " + p.getName());
        }

        completeMsg = null;
    }

    public static void parseToMsg(ChannelHandlerContext ctx, Object msg) throws InvalidProtocolBufferException {
        getBytes((ByteBuf) msg);
        while(parseByteBuffer());
    }
}

