package testcase.rule;

import org.junit.Test;
import testcase.TestBase;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class NormalTest extends TestBase {

    //所有基础类型无论是否是包装类都会走同一个序列化器
    //测试Boolean类型 TURE
    @Test
    public void testBoolean1() throws Exception {
        Boolean flag = true;
        byte[] result = executeSerialization(flag);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(0, existArray.length);

        assertEquals(1, typeHeadArray.length);
        assertEquals(0, typeHeadArray[0]);
        assertEquals(1, typeArray.length);
        assertEquals(32, typeArray[0]);

        //检查Boolean中的值
        assertEquals(flag ? 1 : 0, result[0]);
    }

    //测试Boolean类型 FALSE
    @Test
    public void testBoolean() throws Exception {
        boolean flag = false;
        byte[] result = executeSerialization(flag);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(0, existArray.length);

        assertEquals(1, typeHeadArray.length);
        assertEquals(0, typeHeadArray[0]);
        assertEquals(1, typeArray.length);
        assertEquals(32, typeArray[0]);

        //检查Boolean中的值
        assertEquals(flag ? 1 : 0, result[0]);
    }


//    //测试Byte类型 MAX_VALUE
//    @Test
//    public void testByte() throws Exception {
//        Byte b = Byte.MAX_VALUE;
//        byte[] result = executeSerialization(b);
//        //检查Byte类型是否转换正确
//        assertTrue(isTypeRight(b, result));
//
//        int index = getIndexAfterType(result);
//        //检查Byte在Type中的类型
//        assertEquals(Type.BYTE, result[++index]);
//        //检查Byte中的值
//        assertEquals((Object) b, result[++index]);
//    }
//
//    //测试Character类型
//    @Test
//    public void testCharacter() throws Exception {
//        Character c = 'A';
//        byte[] result = executeSerialization(c);
//        //检查Character类型是否转换正确
//        assertTrue(isTypeRight(c, result));
//
//        int index = getIndexAfterType(result);
//        //检查Character在Type中的类型
//        assertEquals(Type.CHAR, result[++index]);
//        //检查Character中的值，由于Character的值会转成String，
//
//        //循环判断c.toString().length()转化成byte[]
//        byte[] bytesString = c.toString().getBytes("UTF-8");
//        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
//        for (int i = 0; i < bytesStringLength.length; i++) {
//            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[++index]));
//        }
//        //循环判断c.toString内容转化成的byte[]
//        for (int i = 0; i < bytesString.length; i++) {
//            assertEquals(bytesString[i], result[++index]);
//        }
//    }
//
//
//    //测试Double类型
//    @Test
//    public void testDouble1() throws Exception {
//        Double d = Double.MAX_VALUE;
//        byte[] result = executeSerialization(d);
//        //检查Double类型是否转换正确
//        assertTrue(isTypeRight(d, result));
//
//        int index = getIndexAfterType(result);
//        //检查Double在Type中的类型
//        assertEquals(Type.DOUBLE, result[++index]);
//        //检查Double的值，Double会通过特殊的算法(如下)转为byte[8],直接记录byte[8]的内容
//        byte[] bytesDouble = getBytesFromLong(Double.doubleToLongBits(d));
//
//        for (int i = 0; i < bytesDouble.length; i++) {
//            assertEquals(bytesDouble[i], result[++index]);
//        }
//    }
//
//    //测试Double类型
//    @Test
//    public void testDouble2() throws Exception {
//        Double d = 12D;
//        byte[] result = executeSerialization(d);
//        //检查Double类型是否转换正确
//        assertTrue(isTypeRight(d, result));
//
//        int index = getIndexAfterType(result);
//        //检查Double在Type中的类型
//        assertEquals(Type.DOUBLE, result[++index]);
//        //检查Double的值，Double会通过特殊的算法(如下)转为byte[8]，8位16进制,直接记录byte[8]的内容
//        byte[] bytesDouble = getBytesFromLong(Double.doubleToLongBits(d));
//        for (int i = 0; i < bytesDouble.length; i++) {
//            assertEquals(bytesDouble[i], result[++index]);
//        }
//    }
//
////    //测试Double类型
////    @Test
////    public void testDouble3() throws Exception {
////        Double d = -12D;
////        byte[] result = executeSerialization(d);
////        //检查Double类型是否转换正确
////        assertTrue(isTypeRight(d, result));
////
////        int index = getIndexAfterType(result);
////        //检查Double在Type中的类型
////        assertEquals(Type.DOUBLE, result[++index]);
////        //检查Double的值，Double会通过特殊的算法(如下)转为byte[8]，8位16进制,直接记录byte[8]的内容
////        byte[] bytesDouble = CodedOutputStream.getBytesFromLong(Double.doubleToLongBits(d));
////        for (int i = 0; i < bytesDouble.length; i++) {
////            assertEquals(bytesDouble[i], result[++index]);
////        }
////    }
//
//    //测试Float类型
//    @Test
//    public void testFloat1() throws Exception {
//        Float f = Float.MAX_VALUE;
//        byte[] result = executeSerialization(f);
//        //检查Float类型是否转换正确
//        assertTrue(isTypeRight(f, result));
//
//        int index = getIndexAfterType(result);
//        //检查Float在Type中的类型
//        assertEquals(Type.FLOAT, result[++index]);
//        //Float类型会转化成4位byte
//        byte[] bytesFloat = writeRawFloat(f);
//        for (int i = 0; i < bytesFloat.length; i++) {
//            assertEquals(bytesFloat[i], result[++index]);
//        }
//    }
//
//    //测试Float类型
//    @Test
//    public void testFloat2() throws Exception {
//        Float f = 3.1415926F;
//        byte[] result = executeSerialization(f);
//        //检查Float类型是否转换正确
//        assertTrue(isTypeRight(f, result));
//
//        int index = getIndexAfterType(result);
//        //检查Float在Type中的类型
//        assertEquals(Type.FLOAT, result[++index]);
//        //Float类型会转化成4位byte
//        byte[] bytesFloat = writeRawFloat(f);
//        for (int i = 0; i < bytesFloat.length; i++) {
//            assertEquals(bytesFloat[i], result[++index]);
//        }
//    }
//
//
//    //测试Integer类型 正数
//    @Test
//    public void testInteger1() throws Exception {
//        Integer integer = 100;
//        byte[] result = executeSerialization(integer);
//        //检查Integer类型是否转换正确
//        assertTrue(isTypeRight(integer, result));
//
//        int index = getIndexAfterType(result);
//        //检查Integer在Type中的类型
//        assertEquals(Type.INT, result[++index]);
//        //检查Integer转化成byte字符
//        Byte[] bytesInteger = writeRawVarint32(encodeZigZag32(integer));
//        for (int i = 0; i < bytesInteger.length; i++) {
//            assertEquals(bytesInteger[i].toString(), String.valueOf(result[++index]));
//        }
//    }
//
//
//    //测试Integer类型 负数
//    @Test
//    public void testInteger2() throws Exception {
//        Integer integer = Integer.MIN_VALUE;
//        byte[] result = executeSerialization(integer);
//        //检查Integer类型是否转换正确
//        assertTrue(isTypeRight(integer, result));
//
//        int index = getIndexAfterType(result);
//        //检查Integer在Type中的类型
//        assertEquals(Type.INT, result[++index]);
//        //检查Integer转化成byte字符
//        Byte[] bytesInteger = writeRawVarint32(encodeZigZag32(integer));
//        for (int i = 0; i < bytesInteger.length; i++) {
//            assertEquals(bytesInteger[i].toString(), String.valueOf(result[++index]));
//        }
//    }
//
//    //BigDecimal
//    @Test
//    public void testBigDecimal() throws Exception {
//        BigDecimal bd = new BigDecimal(10001.002);
//        byte[] result = executeSerialization(bd);
//        //检查BigDecimal类型是否转换正确
//        assertTrue(isTypeRight(bd, result));
//
//        int index = getIndexAfterType(result);
//        //检查BigDecimal在Type中的类型
//        assertEquals(Type.BIGDECIMAL, result[++index]);
//
//        byte[] bytesString = (bd.toString().getBytes("UTF-8"));
//        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
//        for (int i = 0; i < bytesStringLength.length; i++) {
//            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[++index]));
//        }
//
//        for (int i = 0; i < bytesString.length; i++) {
//            assertEquals(bytesString[i], result[++index]);
//        }
//    }
//
//    //BigInteger
//    @Test
//    public void testBigInteger() throws Exception {
//        BigInteger bigInteger = new BigInteger(String.valueOf(Integer.MAX_VALUE));
//        byte[] result = executeSerialization(bigInteger);
//
//        assertTrue(isTypeRight(bigInteger, result));
//
//        int index = getIndexAfterType(result);
//
//        assertEquals(Type.BIGINTEGER, result[++index]);
//
//        byte[] bytesString = (bigInteger.toString().getBytes("UTF-8"));
//        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
//        for (int i = 0; i < bytesStringLength.length; i++) {
//            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[++index]));
//        }
//
//        for (int i = 0; i < bytesString.length; i++) {
//            assertEquals(bytesString[i], result[++index]);
//        }
//    }
//
//    //测试Long类型 正数
//    @Test
//    public void testLong1() throws Exception {
//        Long l = Long.MAX_VALUE;
//        byte[] result = executeSerialization(l);
//        //检查Long类型是否转换正确
//        assertTrue(isTypeRight(l, result));
//
//        int index = getIndexAfterType(result);
//        //检查Long在Type中的类型
//        assertEquals(Type.LONG, result[++index]);
//        //检查Long转化成byte字符
//        Byte[] bytesLong = writeRawVarint64(encodeZigZag64(l));
//        for (int i = 0; i < bytesLong.length; i++) {
//            assertEquals(bytesLong[i].toString(), String.valueOf(result[++index]));
//        }
//    }
//
//    //测试Long类型 负数
//    @Test
//    public void testLong2() throws Exception {
//        Long l = Long.MIN_VALUE;
//        byte[] result = executeSerialization(l);
//        //检查Long类型是否转换正确
//        assertTrue(isTypeRight(l, result));
//
//        int index = getIndexAfterType(result);
//        //检查Long在Type中的类型
//        assertEquals(Type.LONG, result[++index]);
//        //检查Long转化成byte字符
//        Byte[] bytesLong = writeRawVarint64(encodeZigZag64(l));
//        for (int i = 0; i < bytesLong.length; i++) {
//            assertEquals(bytesLong[i].toString(), String.valueOf(result[++index]));
//        }
//    }
//
//    //测试Short类型 正数
//    @Test
//    public void testShort1() throws Exception {
//        Short s = Short.MAX_VALUE;
//        byte[] result = executeSerialization(s);
//        //检查Short类型是否转换正确
//        assertTrue(isTypeRight(s, result));
//
//        int index = getIndexAfterType(result);
//        //检查Short在Type中的类型
//        assertEquals(Type.SHORT, result[++index]);
//        //检查Short转化成byte字符
//        Byte[] bytesShort = writeRawVarint32(encodeZigZag32(s));
//        for (int i = 0; i < bytesShort.length; i++) {
//            assertEquals(bytesShort[i].toString(), String.valueOf(result[++index]));
//        }
//    }
//
//    //测试Short类型 负数
//    @Test
//    public void testShort2() throws Exception {
//        Short s = Short.MIN_VALUE;
//        byte[] result = executeSerialization(s);
//        //检查Integer类型是否转换正确
//        assertTrue(isTypeRight(s, result));
//
//        int index = getIndexAfterType(result);
//        //检查Integer在Type中的类型
//        assertEquals(Type.SHORT, result[++index]);
//        //检查Integer转化成byte字符
//        Byte[] bytesShort = writeRawVarint32(encodeZigZag32(s));
//        for (int i = 0; i < bytesShort.length; i++) {
//            assertEquals(bytesShort[i].toString(), String.valueOf(result[++index]));
//        }
//    }
//
//    @Test
//    public void testAtomicInteger() throws Exception {
//        AtomicInteger ai = new AtomicInteger(Integer.MAX_VALUE);
//        byte[] result = executeSerialization(ai);
//
//        assertTrue(isTypeRight(ai, result));
//
//        int index = getIndexAfterType(result);
//
//        assertEquals(Type.ATOMIC_INT, result[++index]);
//
//        Byte[] bytesInteger = writeRawVarint32(encodeZigZag32(ai.intValue()));
//        for (int i = 0; i < bytesInteger.length; i++) {
//            assertEquals(bytesInteger[i].toString(), String.valueOf(result[++index]));
//        }
//    }
//
//    @Test
//    public void testAtomicLong() throws Exception {
//        AtomicLong al = new AtomicLong(Long.MIN_VALUE);
//        byte[] result = executeSerialization(al);
//
//        assertTrue(isTypeRight(al, result));
//
//        int index = getIndexAfterType(result);
//
//        assertEquals(Type.ATOMIC_LONG, result[++index]);
//
//        Byte[] bytesLong = writeRawVarint64(encodeZigZag64(al.longValue()));
//        for (int i = 0; i < bytesLong.length; i++) {
//            assertEquals(bytesLong[i].toString(), String.valueOf(result[++index]));
//        }
//    }
//
//    @Test
//    public void testAtomicBoolean() throws Exception {
//        AtomicBoolean ab = new AtomicBoolean(true);
//        byte[] result = executeSerialization(ab);
//
//        assertTrue(isTypeRight(ab, result));
//
//        int index = getIndexAfterType(result);
//
//        assertEquals(Type.ATOMIC_BOOL, result[++index]);
//
//        assertEquals(1, result[++index]);
//    }
//
//
//    //测试String类型
//    @Test
//    public void testString() throws Exception {
//        String str = "360buy.com";
//        byte[] result = executeSerialization(str);
//        //检查Integer类型是否转换正确
//        assertTrue(isTypeRight(str, result));
//
//        int index = getIndexAfterType(result);
//        //检查Integer在Type中的类型
//        assertEquals(Type.STRING, result[++index]);
//
//        //循环判断str.length()转化成byte[]
//        byte[] bytesString = (str.getBytes("UTF-8"));
//        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
//        for (int i = 0; i < bytesStringLength.length; i++) {
//            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[++index]));
//        }
//        //循环判断str内容转化成的byte[]
//        for (int i = 0; i < bytesString.length; i++) {
//            assertEquals(bytesString[i], result[++index]);
//        }
//    }
//
//    @Test
//    public void testInetAddress() throws Exception {
//        InetAddress ia = InetAddress.getByName("127.0.0.1");
//        byte[] result = executeSerialization(ia);
//
//        assertTrue(isTypeRight(ia, result));
//
//        int index = getIndexAfterType(result);
//        assertEquals(Type.INETADDRESS, result[++index]);
//
//        //循环判断str.length()转化成byte[]
//        byte[] bytesString = (ia.getHostAddress().getBytes("UTF-8"));
//        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
//        for (int i = 0; i < bytesStringLength.length; i++) {
//            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[++index]));
//        }
//        //循环判断str内容转化成的byte[]
//        for (int i = 0; i < bytesString.length; i++) {
//            assertEquals(bytesString[i], result[++index]);
//        }
//    }
//
//    @Test
//    public void testDate() throws Exception {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        ParsePosition pos = new ParsePosition(0);
//        Date date = formatter.parse("2012-12-21 23:59:59", pos);
//        byte[] result = executeSerialization(date);
//
//        assertTrue(isTypeRight(date, result));
//
//        int index = getIndexAfterType(result);
//
//        assertEquals(Type.DATE, result[++index]);
//
//        Byte[] bytesLong = writeRawVarint64(encodeZigZag64(date.getTime()));
//        for (int i = 0; i < bytesLong.length; i++) {
//            assertEquals(bytesLong[i].toString(), String.valueOf(result[++index]));
//        }
//    }
//
//    @Test
//    public void testTimeStamp() throws Exception {
//        long millis = (System.currentTimeMillis() / 1000) * 1000;
//        Timestamp timestamp = new Timestamp(millis);
//        byte[] result = executeSerialization(timestamp);
//
//        assertTrue(isTypeRight(timestamp, result));
//
//        int index = getIndexAfterType(result);
//
//        assertEquals(Type.TIMESTAMP, result[++index]);
//
//        Byte[] bytesLong = writeRawVarint64(encodeZigZag64(timestamp.getTime()));
//        for (int i = 0; i < bytesLong.length; i++) {
//            assertEquals(bytesLong[i].toString(), String.valueOf(result[++index]));
//        }
//    }


}
