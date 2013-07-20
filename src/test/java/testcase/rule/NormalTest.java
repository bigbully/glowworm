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
        int index = 0;
        assertEquals(1, result[index++]);//默认写类名
        byte[] bytesString = (Boolean.class.getName().getBytes("UTF-8"));
        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
        for (int i = 0; i < bytesStringLength.length; i++) {
            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[index++]));
        }
        //循环判断str内容转化成的byte[]
        for (int i = 0; i < bytesString.length; i++) {
            assertEquals(bytesString[i], result[index++]);
        }
        assertEquals(flag ? 1 : 0, result[index++]);
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

        int index = 0;
        assertEquals(1, result[index++]);//默认写类名
        byte[] bytesString = (Boolean.class.getName().getBytes("UTF-8"));
        Byte[] bytesStringLength = writeRawVarint32(bytesString.length);
        for (int i = 0; i < bytesStringLength.length; i++) {
            assertEquals(bytesStringLength[i].toString(), String.valueOf(result[index++]));
        }
        //循环判断str内容转化成的byte[]
        for (int i = 0; i < bytesString.length; i++) {
            assertEquals(bytesString[i], result[index++]);
        }
        //检查Boolean中的值
        assertEquals(flag ? 1 : 0, result[index++]);
    }


}
