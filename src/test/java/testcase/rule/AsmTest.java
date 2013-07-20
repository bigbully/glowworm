package testcase.rule;

import org.junit.Test;
import testcase.TestBase;
import userJavabean.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AsmTest extends TestBase {

    //只有一个属性boolean的javabean
    @Test
    public void testUserJavaBean1() throws Exception {
        User1 u = new User1();
        u.setB(true);
        byte[] result = executeSerialization(u);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(1, existArray.length);
        assertEquals(0, existArray[0]);

        assertEquals(0, typeHeadArray.length);
        assertEquals(0, typeArray.length);

        int index = 0;
        assertEquals(1, result[index++]);//默认写类名
        byte[] bytesString = (User1.class.getName().getBytes("UTF-8"));
        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
        for (int i = 0; i < bytesStringLength.length; i++) {
            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[index++]));
        }
        //循环判断str内容转化成的byte[]
        for (int i = 0; i < bytesString.length; i++) {
            assertEquals(bytesString[i], result[index++]);
        }
        assertEquals(1, result[index++]);
    }

    //只有一个属性Boolean的javabean
    @Test
    public void testUserJavaBean2() throws Exception {
        User2 u = new User2();
        u.setB(true);
        byte[] result = executeSerialization(u);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(1, existArray.length);
        assertEquals(0, existArray[0]);

        assertEquals(0, typeHeadArray.length);
        assertEquals(0, typeArray.length);

        int index = 0;
        assertEquals(1, result[index++]);//默认写类名
        byte[] bytesString = (User2.class.getName().getBytes("UTF-8"));
        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
        for (int i = 0; i < bytesStringLength.length; i++) {
            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[index++]));
        }
        //循环判断str内容转化成的byte[]
        for (int i = 0; i < bytesString.length; i++) {
            assertEquals(bytesString[i], result[index++]);
        }
        assertEquals(1, result[index++]);
    }

    //测试User3中的属性是User1的情况，都走asm
    @Test
    public void testUserJavaBean3() throws Exception {
        User1 u1 = new User1();
        u1.setB(true);

        User3 u3 = new User3();
        u3.setUser1(u1);
        byte[] result = executeSerialization(u3);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(1, existArray.length);
        assertEquals(0, existArray[0]);

        assertEquals(0, typeHeadArray.length);
        assertEquals(0, typeArray.length);

        int index = 0;
        assertEquals(1, result[index++]);//默认写类名
        byte[] bytesString = (User3.class.getName().getBytes("UTF-8"));
        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
        for (int i = 0; i < bytesStringLength.length; i++) {
            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[index++]));
        }
        //循环判断str内容转化成的byte[]
        for (int i = 0; i < bytesString.length; i++) {
            assertEquals(bytesString[i], result[index++]);
        }
        assertEquals(1, result[index++]);
    }

    //测试User4中的属性是User1数组的情况，都走asm
    @Test
    public void testUserJavaBean4() throws Exception {
        User4 u4 = new User4();
        User1[] user1s = new User1[3];
        User1 u11 = new User1();
        u11.setB(true);

        User1 u13 = new User1();
        u13.setB(false);

        user1s[0] = u11;
        user1s[1] = null;
        user1s[2] = u13;

        u4.setUser1s(user1s);
        byte[] result = executeSerialization(u4);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(1, existArray.length);
        assertEquals(16, existArray[0]);

        assertEquals(0, typeHeadArray.length);
        assertEquals(0, typeArray.length);

        int index = 0;
        assertEquals(1, result[index++]);//默认写类名
        byte[] bytesString = (User4.class.getName().getBytes("UTF-8"));
        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
        for (int i = 0; i < bytesStringLength.length; i++) {
            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[index++]));
        }
        //循环判断str内容转化成的byte[]
        for (int i = 0; i < bytesString.length; i++) {
            assertEquals(bytesString[i], result[index++]);
        }
        assertEquals(3, result[index++]);
        assertEquals(1, result[index++]);
        assertEquals(0, result[index++]);
    }


    //测试User4中的属性是Object数组的情况，都走asm
    @Test
    public void testUserJavaBean5() throws Exception {
        User5 u5 = new User5();
        Object[] user1s = new Object[4];
        User1 u11 = new User1();
        u11.setB(true);

        User1 u13 = new User1();
        u13.setB(false);

        User2 u22 = new User2();
        u22.setB(true);

        user1s[0] = u11;
        user1s[1] = u13;
        user1s[2] = null;
        user1s[3] = u22;

        u5.setObjects(user1s);
        byte[] result = executeSerialization(u5);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(1, existArray.length);
        assertEquals(4, existArray[0]);

        assertEquals(1, typeHeadArray.length);
        assertEquals(0, typeHeadArray[0]);
        assertEquals(2, typeArray.length);
        assertEquals(0, typeArray[0]);
        assertEquals(0, typeArray[1]);

        int index = 0;
        assertEquals(1, result[index++]);//默认写类名
        byte[] bytesString0 = (User5.class.getName().getBytes("UTF-8"));
        Byte[] bytesStringLength0 = writeRawVarint32(bytesString0.length);
        for (int i = 0; i < bytesStringLength0.length; i++) {
            assertEquals(bytesStringLength0[i].toString(), String.valueOf(result[index++]));
        }
        //循环判断str内容转化成的byte[]
        for (int i = 0; i < bytesString0.length; i++) {
            assertEquals(bytesString0[i], result[index++]);
        }


        assertEquals(4, result[index++]);

        //第一个元素，写入type，类名
        byte[] bytesString = (User1.class.getName().getBytes("UTF-8"));
        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
        for (int i = 0; i < bytesStringLength.length; i++) {
            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[index++]));
        }
        //循环判断str内容转化成的byte[]
        for (int i = 0; i < bytesString.length; i++) {
            assertEquals(bytesString[i], result[index++]);
        }
        //第一个元素的内容
        assertEquals(1, result[index++]);

        //第二个元素，写type，不写类名
        assertEquals(0, result[index++]);
        //第二个元素的内容
        assertEquals(0, result[index++]);

        //第三个元素，写type，写类名
        bytesString = (User2.class.getName().getBytes("UTF-8"));
        bytesStringLength = writeRawVarint32(bytesString.length);
        for (int i = 0; i < bytesStringLength.length; i++) {
            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[index++]));
        }
        //循环判断str内容转化成的byte[]
        for (int i = 0; i < bytesString.length; i++) {
            assertEquals(bytesString[i], result[index++]);
        }
        //第三个元素的内容
        assertEquals(1, result[index++]);
    }


}
