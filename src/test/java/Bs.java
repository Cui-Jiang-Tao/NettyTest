import java.nio.ByteBuffer;

public class Bs {

    public static void main(String[] args) {
        ByteBuffer buffer1 = ByteBuffer.allocate(10);
        ByteBuffer buffer2 = ByteBuffer.allocate(10);

        buffer2.putInt(12);
        buffer2.putInt(12);

        buffer2.flip();
        buffer1.put(buffer2);

        buffer1.flip();

        byte[] b = new byte[buffer1.limit() - buffer1.position()];
        buffer1.get(b);

        System.out.println(b.length);

        /*int a = buffer1.getInt();
        int b = buffer1.getInt();

        System.out.println(a + "    "  + b);*/


    }
}
