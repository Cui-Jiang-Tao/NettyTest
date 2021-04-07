package cn.msg;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.nio.ByteBuffer;
import java.util.Arrays;

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
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put(completeMsg, 0, 4);
        buffer.flip();
        int type = buffer.getInt();

        completeMsg = Arrays.copyOfRange(completeMsg, 4, completeMsg.length);

        if(type == Test.MsgType.MSG_TYPE_PERSON.ordinal()) {
            Test.Person p = Test.Person.parseFrom(completeMsg);
            System.out.println("Server received: " + p.getEmail());
            System.out.println("Server received id: " + p.getId());
        }
        else if(type == Test.MsgType.MSG_TYPE_PEOPLE.ordinal()) {
            Test.People p = Test.People.parseFrom(completeMsg);
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

