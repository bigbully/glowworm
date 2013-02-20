package testcase.rule;

import com.jd.dd.glowworm.util.ExistInputStream;
import com.jd.dd.glowworm.util.ExistOutputStream;
import com.jd.dd.glowworm.util.TypeInputStream;
import com.jd.dd.glowworm.util.TypeOutputStream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BitTest {

    @Test
    public void testExistStreamInput() {
        ExistInputStream stream = new ExistInputStream(new byte[]{-12, 23}, 0, 2);
        assertEquals(1, stream.readRawByte());
        assertEquals(1, stream.readRawByte());
        assertEquals(1, stream.readRawByte());
        assertEquals(1, stream.readRawByte());
        assertEquals(0, stream.readRawByte());
        assertEquals(1, stream.readRawByte());
        assertEquals(0, stream.readRawByte());
        assertEquals(0, stream.readRawByte());

        assertEquals(0, stream.readRawByte());
        assertEquals(0, stream.readRawByte());
        assertEquals(0, stream.readRawByte());
        assertEquals(1, stream.readRawByte());
        assertEquals(0, stream.readRawByte());
        assertEquals(1, stream.readRawByte());
        assertEquals(1, stream.readRawByte());
        assertEquals(1, stream.readRawByte());
    }

    @Test
    public void testTypeStreamInput1() {
        byte[] bytes = new byte[]{1, 0, 3, -85, -94, 31};  //10101011 10100010 00011111
        TypeInputStream stream = new TypeInputStream(bytes, 1, 1);
        stream.reset(3, 3);
        assertEquals(5, stream.readInt());
        assertEquals(2, stream.readInt());
        assertEquals(7, stream.readInt());
        assertEquals(2, stream.readInt());
        assertEquals(1, stream.readInt());
        assertEquals(0, stream.readInt());
        assertEquals(3, stream.readInt());
        assertEquals(7, stream.readInt());
    }

    @Test
    public void testTypeStreamInput2() {
        //11111111 11000000
        //        10101101 10100011 10111011 11110010 00010010 11111001 11011010 00010001
        byte[] bytes = new byte[]{2, -1, -64, 8, -83, -93, -69, -14, 18, -7, -38, 17};
        TypeInputStream stream = new TypeInputStream(bytes, 1, 2);
        stream.reset(4, 8);

        assertEquals(43, stream.readInt());
        assertEquals(26, stream.readInt());
        assertEquals(14, stream.readInt());
        assertEquals(59, stream.readInt());
        assertEquals(60, stream.readInt());
        assertEquals(33, stream.readInt());
        assertEquals(11, stream.readInt());
        assertEquals(57, stream.readInt());
        assertEquals(54, stream.readInt());
        assertEquals(33, stream.readInt());
    }

    //测试typeStream 常用类型与非常用类型混合 写10次type
    @Test
    public void testTypeStream3() {
        TypeOutputStream typeStream = new TypeOutputStream(100);
        typeStream.write(16);//01000000    16-->010000
        assertEquals(64, typeStream.getBytes()[0]);
        typeStream.write(25);//01000001 10010000    25-->011001
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-112, typeStream.getBytes()[1]);
        typeStream.write(3);//01000001 10010110    3-->011
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-106, typeStream.getBytes()[1]);
        typeStream.write(24);//01000001 10010110 11000000    24-->011000
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-106, typeStream.getBytes()[1]);
        assertEquals(-64, typeStream.getBytes()[2]);
        typeStream.write(2);//01000001 10010110 11000010    2-->010
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-106, typeStream.getBytes()[1]);
        assertEquals(-62, typeStream.getBytes()[2]);
        typeStream.write(2);//01000001 10010110 11000010 01000000    2-->010
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-106, typeStream.getBytes()[1]);
        assertEquals(-62, typeStream.getBytes()[2]);
        assertEquals(64, typeStream.getBytes()[3]);

        typeStream.write(44);//01000001 10010110 11000010 01010110 00000000   44-->101100
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-106, typeStream.getBytes()[1]);
        assertEquals(-62, typeStream.getBytes()[2]);
        assertEquals(86, typeStream.getBytes()[3]);
        assertEquals(0, typeStream.getBytes()[4]);

        typeStream.write(23);//01000001 10010110 11000010 01010110 00101110   23-->010111
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-106, typeStream.getBytes()[1]);
        assertEquals(-62, typeStream.getBytes()[2]);
        assertEquals(86, typeStream.getBytes()[3]);
        assertEquals(46, typeStream.getBytes()[4]);

        typeStream.write(7);//01000001 10010110 11000010 01010110 00101111 11000000   7-->111
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-106, typeStream.getBytes()[1]);
        assertEquals(-62, typeStream.getBytes()[2]);
        assertEquals(86, typeStream.getBytes()[3]);
        assertEquals(47, typeStream.getBytes()[4]);
        assertEquals(-64, typeStream.getBytes()[5]);

        typeStream.write(36);//01000001 10010110 11000010 01010110 00101111 11100100   36-->100100
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-106, typeStream.getBytes()[1]);
        assertEquals(-62, typeStream.getBytes()[2]);
        assertEquals(86, typeStream.getBytes()[3]);
        assertEquals(47, typeStream.getBytes()[4]);
        assertEquals(-28, typeStream.getBytes()[5]);

        //验证头信息
        assertEquals(-45, typeStream.getHeadBytes()[0]);
        assertEquals(64, typeStream.getHeadBytes()[1]);
    }


    //测试typeStream 非常用类型 i>7
    @Test
    public void testTypeStream1() {
        TypeOutputStream typeStream = new TypeOutputStream(100);
        typeStream.write(16);//01000000    16-->010000
        assertEquals(64, typeStream.getBytes()[0]);
        typeStream.write(25);//01000001 10010000    25-->011001
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-112, typeStream.getBytes()[1]);
        typeStream.write(34);//01000001 10011000 10000000    34-->100010
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-104, typeStream.getBytes()[1]);
        assertEquals(-128, typeStream.getBytes()[2]);
        typeStream.write(44);//01000001 10011000 10101100    44-->101100
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-104, typeStream.getBytes()[1]);
        assertEquals(-84, typeStream.getBytes()[2]);
        typeStream.write(44);//01000001 10011000 10101100 10110000    44-->101100
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-104, typeStream.getBytes()[1]);
        assertEquals(-84, typeStream.getBytes()[2]);
        assertEquals(-80, typeStream.getBytes()[3]);

        typeStream.write(13);//01000001 10011000 10101100 10110000 11010000    13-->001101
        assertEquals(65, typeStream.getBytes()[0]);
        assertEquals(-104, typeStream.getBytes()[1]);
        assertEquals(-84, typeStream.getBytes()[2]);
        assertEquals(-80, typeStream.getBytes()[3]);
        assertEquals(-48, typeStream.getBytes()[4]);

        assertEquals(-4, typeStream.getHeadBytes()[0]); //验证头信息

    }

    //测试typeStream 常用类型
    @Test
    public void testTypeStream2() {
        TypeOutputStream typeStream = new TypeOutputStream(100);
        typeStream.write(7);
        assertEquals(-32, typeStream.getBytes()[0]);//11100000
        typeStream.write(5);
        assertEquals(-12, typeStream.getBytes()[0]);//11110100
        typeStream.write(5);
        assertEquals(-10, typeStream.getBytes()[0]);//11110110 10000000
        assertEquals(-128, typeStream.getBytes()[1]);
        typeStream.write(3);
        assertEquals(-10, typeStream.getBytes()[0]);//11110110 10110000
        assertEquals(-80, typeStream.getBytes()[1]);
        typeStream.write(1);
        assertEquals(-10, typeStream.getBytes()[0]);//11110110 10110010
        assertEquals(-78, typeStream.getBytes()[1]);
        typeStream.write(2);
        assertEquals(-10, typeStream.getBytes()[0]);//11110110 10110010 10000000
        assertEquals(-78, typeStream.getBytes()[1]);
        assertEquals(-128, typeStream.getBytes()[2]);
        typeStream.write(2);
        assertEquals(-10, typeStream.getBytes()[0]);//11110110 10110010 10010000
        assertEquals(-78, typeStream.getBytes()[1]);
        assertEquals(-112, typeStream.getBytes()[2]);
        typeStream.write(2);
        assertEquals(-10, typeStream.getBytes()[0]);//11110110 10110010 10010010
        assertEquals(-78, typeStream.getBytes()[1]);
        assertEquals(-110, typeStream.getBytes()[2]);
        typeStream.write(2);
        assertEquals(-10, typeStream.getBytes()[0]);//11110110 10110010 10010010 01000000
        assertEquals(-78, typeStream.getBytes()[1]);
        assertEquals(-110, typeStream.getBytes()[2]);
        assertEquals(64, typeStream.getBytes()[3]);

        assertEquals(0, typeStream.getHeadBytes()[0]); //验证头信息
    }


    //测试写入existCode
    @Test
    public void testBitOutputStream() {
        ExistOutputStream existStream = new ExistOutputStream(10);
        existStream.write(false);//0
        assertEquals(-128, existStream.getBytes()[0]);//10000000
        existStream.write(true);//1
        assertEquals(-128, existStream.getBytes()[0]);//10000000
        existStream.write(false);//2
        assertEquals(-96, existStream.getBytes()[0]);//10100000
        existStream.write(true);//3
        assertEquals(-96, existStream.getBytes()[0]);//10100000
        existStream.write(false);//4
        assertEquals(-88, existStream.getBytes()[0]);//10101000
        existStream.write(true);//5
        assertEquals(-88, existStream.getBytes()[0]);//10101000
        existStream.write(false);//6
        assertEquals(-86, existStream.getBytes()[0]);//10101010
        existStream.write(false);//7
        assertEquals(-85, existStream.getBytes()[0]);//10101011
        existStream.write(false);//8
        assertEquals(-85, existStream.getBytes()[0]);//10101011 10000000
        assertEquals(-128, existStream.getBytes()[1]);
        existStream.write(false);//9
        assertEquals(-85, existStream.getBytes()[0]);//10101011 11000000
        assertEquals(-64, existStream.getBytes()[1]);

    }

    public static void main(String[] args) {
//        String str = "10110100";
//        byte b = 0;
//        for (int i = 0; i < 8; i++) {
//            b  = (byte)((b << 1) | Integer.parseInt(str.substring(i, i + 1)));
//        }
//        System.out.println(b);


//        String str = "";
//        byte b = 40;
//        for (int i = 0; i < 8; i++) {
//            str += (b >>> (7 - i) & 1);
//        }
//        System.out.println(str);
        System.out.println(Integer.MAX_VALUE / 1000 / 1000);
    }
}
