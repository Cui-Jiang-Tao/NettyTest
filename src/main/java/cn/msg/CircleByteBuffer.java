package cn.msg;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class CircleByteBuffer {
    private byte[] bytes = null;

    private int readIndex;
    private int writeIndex;
    private int capacity;

    private boolean isEmpty = true;
    private boolean isFull = false;

    public CircleByteBuffer() {
        //默认分配一个G
        bytes = new byte[1024 * 1024];
        readIndex = 0;
        writeIndex = 0;
        capacity = bytes.length;
    }

    public int canWriteSize() {
        if(isEmpty) {
            return capacity;
        }

        if(isFull) {
            return 0;
        }

        if(writeIndex > readIndex) {
            return capacity - writeIndex + readIndex;
        }
        else {
            return readIndex - writeIndex;
        }
    }

    public int canReadSize() {
        if(isEmpty) {
            return 0;
        }

        if(isFull) {
            return capacity;
        }

        if(readIndex < writeIndex) {
            return writeIndex - readIndex;
        }
        else {
            return capacity - readIndex + writeIndex;
        }
    }

    public ByteBuffer getByteBuffer(int length) {
        byte[] b = getBytes(length);
        ByteBuffer buffer = ByteBuffer.allocate(b.length);
        buffer.put(b, 0, buffer.capacity());

        return buffer;
    }

    public byte[] getBytes(int length) {
        byte[] b = _getBytes(length);

        if(readIndex == writeIndex) {
            isEmpty = true;
        }

        return b;
    }

    private byte[] _getBytes(int length) {
        if(canReadSize() >= length) {
            if(writeIndex > readIndex) {
                readIndex += length;

                return Arrays.copyOfRange(bytes, readIndex - length, readIndex);
            }
            else{
                if(capacity - readIndex >= length) {
                    readIndex += length;
                    return Arrays.copyOfRange(bytes, readIndex - length, readIndex);
                }
                else {
                    byte[] temp1 = Arrays.copyOfRange(bytes, readIndex, capacity);
                    byte[] temp2= Arrays.copyOfRange(bytes, 0, length - temp1.length);
                    byte[] a = new byte[temp1.length + temp2.length];
                    System.arraycopy(temp1, 0, a, 0, temp1.length);
                    System.arraycopy(temp2, 0, a, temp1.length, temp2.length);
                    readIndex = temp2.length;

                    return a;
                }
            }
        }
        else {
            return new byte[0];
        }
    }

    public void putBytes(byte[] b) {
        if(b == null || b.length == 0) {
            return;
        }

        int length = b.length;

        if(canWriteSize() > length) {
            if(writeIndex > readIndex) {
                if(capacity - writeIndex >= length) {
                    System.arraycopy(b, 0, bytes, writeIndex, length);
                    writeIndex += length;
                }
                else {
                    int right = capacity - writeIndex;
                    System.arraycopy(b, 0, bytes, writeIndex, right);
                    System.arraycopy(b, right, bytes, 0, length - right);
                    writeIndex = length - right;
                }
            }
            else {
                System.arraycopy(b, 0, bytes, writeIndex, length);
                writeIndex += length;
            }
            isEmpty = false;
        }

        if(writeIndex == readIndex) {
            isFull = true;
        }
    }

    public void putBytes(ByteBuf in) {
        int length = in.readableBytes();

        if(canWriteSize() > length) {
            if(writeIndex > readIndex) {
                if(capacity - writeIndex >= length) {
                    in.readBytes(bytes, writeIndex, length);
                    writeIndex += length;
                }
                else {
                    int right = capacity - writeIndex;
                    in.readBytes(bytes, writeIndex, right);
                    in.readBytes(bytes, 0, length - right);
                    writeIndex = length - right;
                }
            }
            else {
                in.readBytes(bytes, writeIndex, length);
                writeIndex += length;
            }
            isEmpty = false;
        }

        if(writeIndex == readIndex) {
            isFull = true;
        }
    }
}
