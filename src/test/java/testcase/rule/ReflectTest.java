package testcase.rule;

import org.junit.Test;
import testcase.TestBase;
import userJavabean.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReflectTest extends TestBase {

    //只有一个属性boolean的javabean
    @Test
    public void testUserJavaBean1() throws Exception {
        User7 u7 = new User7();
        u7.putInnerBoolean(true);
        byte[] result = executeSerialization(u7);

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

        assertEquals(1, result[0]);
    }

    //只有一个属性Boolean的javabean
    @Test
    public void testUserJavaBean2() throws Exception {
        User8 u = new User8();
        u.putInnerBoolean(true);
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

        assertEquals(1, result[0]);
    }

    //测试User9的inner中的属性是User1的情况，user1走asm
    @Test
    public void testUserJavaBean3() throws Exception {
        User1 u1 = new User1();
        u1.setB(true);

        User9 u9 = new User9();
        u9.putInnerBean(u1);
        byte[] result = executeSerialization(u9);

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

        assertEquals(1, result[0]);
    }

    //测试User10中inner类中的属性是User1数组的情况，都走user1走asm
    @Test
    public void testUserJavaBean4() throws Exception {
        User10 u10 = new User10();

        User1[] user1s = new User1[3];
        User1 u11 = new User1();
        u11.setB(true);

        User1 u13 = new User1();
        u13.setB(false);

        user1s[0] = u11;
        user1s[1] = null;
        user1s[2] = u13;

        u10.putInnerArray(user1s);
        byte[] result = executeSerialization(u10);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(1, existArray.length);
        assertEquals(8, existArray[0]);

        assertEquals(0, typeHeadArray.length);
        assertEquals(0, typeArray.length);

        int index = 0;
        assertEquals(3, result[index++]);
        assertEquals(1, result[index++]);
        assertEquals(0, result[index++]);
    }


    //测试User11的inner类中的属性是Object数组的情况，数组中user1走asm
    @Test
    public void testUserJavaBean5() throws Exception {
        User11 u_11 = new User11();
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

        u_11.putInnerArray(user1s);
        byte[] result = executeSerialization(u_11);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(2, existArray.length);
        assertEquals(2, existArray[0]);
        assertEquals(0, existArray[1]);

        assertEquals(1, typeHeadArray.length);
        assertEquals(0, typeHeadArray[0]);
        assertEquals(2, typeArray.length);
        assertEquals(0, typeArray[0]);
        assertEquals(0, typeArray[1]);

        int index = 0;
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
