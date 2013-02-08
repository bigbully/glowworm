package testcase.rule;

import com.jd.dd.glowworm.asm.Type;
import org.junit.Test;
import testcase.TestBase;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultiTest extends TestBase {

    //测试boolean[4]{true, false, false, true}
    @Test
    public void testBooleanArray1() throws Exception {
        boolean[] booleans = new boolean[]{true, false, false, true};
        byte[] result = executeSerialization(booleans);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(0, existArray.length);

        assertEquals(1, typeHeadArray.length);
        assertEquals(-128, typeHeadArray[0]);
        assertEquals(1, typeArray.length);
        assertEquals(108, typeArray[0]);//ARRAY_BOOLEAN 11011000

        assertEquals(4, result[0]);
        assertEquals(1, result[1]);
        assertEquals(0, result[2]);
        assertEquals(0, result[3]);
        assertEquals(1, result[4]);
    }

    //测试Boolean[4]{true, false,null, false, true}
    @Test
    public void testArrayBoolean() throws Exception {
        Boolean[] booleans = new Boolean[]{true, false, null, false, true};
        byte[] result = executeSerialization(booleans);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(1, existArray.length);
        assertEquals(32, existArray[0]);

        assertEquals(1, typeHeadArray.length);
        assertEquals(-128, typeHeadArray[0]);
        assertEquals(1, typeArray.length);
        assertEquals(84, typeArray[0]);//ARRAY_BOOLEAN 01010100

        assertEquals(booleans.length, result[0]);
        assertEquals(1, result[1]);
        assertEquals(0, result[2]);
        assertEquals(0, result[3]);
        assertEquals(1, result[4]);
    }

    //测试Object[5]{true, false,null, false, true}
    @Test
    public void testObjectArray1() throws Exception {
        Object[] booleans = new Object[]{true, false, null, false, true};
        byte[] result = executeSerialization(booleans);

        List<byte[]> list = getHeadBytesArray(result);
        byte[] refArray = list.get(0);
        byte[] existArray = list.get(1);
        byte[] typeHeadArray = list.get(2);
        byte[] typeArray = list.get(3);
        result = list.get(4);

        assertEquals(0, refArray.length);
        assertEquals(1, existArray.length);
        assertEquals(32, existArray[0]);

        assertEquals(1, typeHeadArray.length);
        assertEquals(-128, typeHeadArray[0]);
        assertEquals(3, typeArray.length);
        assertEquals(84, typeArray[0]);//ARRAY_BOOLEAN 01010100 10010010 01000000
        assertEquals(-110, typeArray[1]);
        assertEquals(64, typeArray[2]);

        int index = 0;
        assertEquals(booleans.length, result[index++]);

        assertEquals(1, result[1]);
        assertEquals(0, result[2]);
        assertEquals(0, result[3]);
        assertEquals(1, result[4]);
    }


    //测试char[5]{'我', '知', '道'}
    @Test
    public void testCharArray2() throws Exception {
        //todo 对于gbk编码的char数组
        assertTrue(false);
    }



}
