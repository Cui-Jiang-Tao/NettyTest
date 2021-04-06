package cn;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;

public class ParseMsg {
    static LinkedList<ByteBuffer> bufs = new LinkedList();
    static byte[] completeMsg = null;
    static int headLenght = -1;

    private static void getBytes(ByteBuf in) {
        int capacity = in.readableBytes();
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        in.readBytes(buffer);

        bufs.add(buffer);
    }

    private static boolean parseByteBuffer() throws InvalidProtocolBufferException {
        if (headLenght == -1) {
            if(getBufsSize(bufs) >= 4) {
                LinkedList<ByteBuffer> bs = new LinkedList<>();
                while (getBufsSize(bs) < 4) {
                    bs.add(bufs.pollFirst());
                }

                ByteBuffer buffer = ByteBuffer.allocate(getBufsSize(bs));
                while(!bs.isEmpty()) {
                    ByteBuffer temp = bs.pollFirst();
                    temp.flip();
                    buffer.put(temp);
                }

                buffer.flip();
                headLenght = buffer.getInt();
                ByteBuffer temp = ByteBuffer.allocate(buffer.limit() - buffer.position());
                temp.put(buffer);
                bufs.addFirst(temp);
            }
            else {
                return false;
            }
        }

        if(getBufsSize(bufs) >= headLenght) {
            byte[] bytes = new byte[getBufsSize(bufs)];
            int position = 0;

            while (!bufs.isEmpty()) {
                ByteBuffer temp = bufs.pollFirst();
                temp.flip();
                temp.get(bytes, position, temp.capacity());
                position += temp.capacity();
            }

            if(bytes.length > headLenght) {
                ByteBuffer buffer = ByteBuffer.allocate(bytes.length - position);
                buffer.put(bytes, position, buffer.capacity());
                bufs.addFirst(buffer);
            }

            completeMsg = Arrays.copyOfRange(bytes, 0, headLenght);
            headLenght = -1;
            dealMsg();
        }
        else {
            return false;
        }

        return true;
    }

    private static int getBufsSize(LinkedList<ByteBuffer> bufs) {
        int sum = 0;

        for (ByteBuffer buffer : bufs) {
            sum += buffer.capacity();
        }

        return sum;
    }


    private static boolean flag = true;
    private static void dealMsg() throws InvalidProtocolBufferException {
        if(flag) {
            System.out.println(completeMsg.length);
            Test.Person p = Test.Person.parseFrom(completeMsg);
            System.out.println("Server received: " + p.getEmail());
            System.out.println("Server received id: " + p.getId());

        }
        else {
            Test.People p = Test.People.parseFrom(completeMsg);
            System.out.println("Server received count: " + p.getCount());
            System.out.println("Server received email: " + p.getEmail());
            System.out.println("Server received name: " + p.getName());
        }

        flag = !flag;
        completeMsg = null;
    }

    public static void parseToMsg(ChannelHandlerContext ctx, Object msg) throws InvalidProtocolBufferException {
        getBytes((ByteBuf) msg);
        while(parseByteBuffer());
    }
}
