package testcase.function;

import com.jd.dd.glowworm.util.Parameters;
import org.junit.Test;
import testcase.TestBase;
import userJavabean.LoopPerson1;
import userJavabean.Person1;
import userJavabean.Person2;
import userJavabean.User1;

import java.util.*;

import static org.junit.Assert.*;

public class MultiTest extends TestBase {

    @Test
    public void testBooleanArrayPrimary() throws Exception {
        boolean[] booleans = new boolean[]{true, false, true};
        boolean[] result = executeBackAndForth(booleans, boolean[].class);
        assertEquals(true, result[0]);
        assertEquals(false, result[1]);
        assertEquals(true, result[2]);
    }

    @Test
    public void testBooleanArray() throws Exception {
        Boolean[] booleans = new Boolean[]{true, false, null, true};
        Boolean[] result = executeBackAndForth(booleans, Boolean[].class);
        assertEquals(true, result[0]);
        assertEquals(false, result[1]);
        assertEquals(null, result[2]);
        assertEquals(true, result[3]);
    }

    //Byte[] = {new Byte("1"), new Byte("2")}
    @Test
    public void testByteArray1() throws Exception {
        Byte[] testData = {new Byte("1"), new Byte("2"), null};
        Byte[] result = executeBackAndForth(testData, Byte[].class);
        assertNotNull(result);
        assertEquals(Byte[].class, result.getClass());
        assertEquals("1", result[0].toString());
        assertEquals("2", result[1].toString());
        assertNull(result[2]);
    }

    //byte[] = {new Byte("1"), new Byte("2")}
    @Test
    public void testByteArray2() throws Exception {
        byte[] testData = {new Byte("1"), new Byte("2")};
        byte[] bytes = executeBackAndForth(testData, byte[].class);
        assertNotNull(bytes);
        assertEquals(byte[].class, bytes.getClass());
        assertEquals("1", String.valueOf(bytes[0]));
        assertEquals("2", String.valueOf(bytes[1]));
    }

    //Character[] = {Character.MAX_VALUE, Character.MIN_VALUE}
    @Test
    public void testCharacterArray1() throws Exception {
        Character[] testData = {Character.MAX_VALUE, Character.MIN_VALUE, null};
        Character[] result = executeBackAndForth(testData, Character[].class);
        assertNotNull(result);
        assertEquals(Character[].class, result.getClass());
        assertEquals(String.valueOf(Character.MAX_VALUE), result[0].toString());
        assertEquals(String.valueOf(Character.MIN_VALUE), result[1].toString());
        assertNull(result[2]);
    }

    //character[] = {Character.MAX_VALUE, Character.MIN_VALUE}
    //这个转化方式不一样
    @Test
    public void testCharacterArray2() throws Exception {
        char[] testData = {Character.MAX_VALUE, Character.MIN_VALUE};
        char[] characters = executeBackAndForth(testData, char[].class);
        assertNotNull(characters);
        assertEquals(char[].class, characters.getClass());
        assertEquals(String.valueOf(Character.MAX_VALUE), String.valueOf(characters[0]));
        assertEquals(String.valueOf(Character.MIN_VALUE), String.valueOf(characters[1]));
    }

    //Double[] = {Double.MAX_VALUE, Double.MIN_VALUE}
    @Test
    public void testDoubleArray1() throws Exception {
        Double[] testData = {Double.MAX_VALUE, Double.MIN_VALUE, null};
        Double[] result = executeBackAndForth(testData, Double[].class);
        assertNotNull(result);
        assertEquals(Double[].class, result.getClass());
        assertEquals(String.valueOf(Double.MAX_VALUE), result[0].toString());
        assertEquals(String.valueOf(Double.MIN_VALUE), result[1].toString());
        assertNull(result[2]);
    }

    //double[] = {Double.MAX_VALUE, Double.MIN_VALUE}
    @Test
    public void testDoubleArray2() throws Exception {
        double[] testData = {Double.MAX_VALUE, Double.MIN_VALUE};
        double[] doubles = executeBackAndForth(testData, double[].class);
        assertNotNull(doubles);
        assertEquals(double[].class, doubles.getClass());
        assertEquals(String.valueOf(Double.MAX_VALUE), String.valueOf(doubles[0]));
        assertEquals(String.valueOf(Double.MIN_VALUE), String.valueOf(doubles[1]));
    }

    //Float[] = {Float.MAX_VALUE, Float.MIN_VALUE}
    @Test
    public void testFloatArray1() throws Exception {
        Float[] testData = {Float.MAX_VALUE, Float.MIN_VALUE, null};
        Float[] result = executeBackAndForth(testData, Float[].class);
        assertNotNull(result);
        assertEquals(Float[].class, result.getClass());
        assertEquals(String.valueOf(Float.MAX_VALUE), String.valueOf(result[0]));
        assertEquals(String.valueOf(Float.MIN_VALUE), String.valueOf(result[1]));
        assertNull(result[2]);
    }

    //float[] = {Float.MAX_VALUE, Float.MIN_VALUE}
    @Test
    public void testFloatArray2() throws Exception {
        float[] testData = {Float.MAX_VALUE, Float.MIN_VALUE};
        float[] floats = executeBackAndForth(testData, float[].class);
        assertNotNull(floats);
        assertEquals(float[].class, floats.getClass());
        assertEquals(String.valueOf(Float.MAX_VALUE), String.valueOf(floats[0]));
        assertEquals(String.valueOf(Float.MIN_VALUE), String.valueOf(floats[1]));
    }

    //Short[] = {Short.MAX_VALUE, Short.MIN_VALUE}
    @Test
    public void testShortArray1() throws Exception {
        Short[] testData = {Short.MAX_VALUE, Short.MIN_VALUE, null};
        Short[] result = executeBackAndForth(testData, Short[].class);
        assertNotNull(result);
        assertEquals(Short[].class, result.getClass());
        assertEquals(String.valueOf(Short.MAX_VALUE), String.valueOf(result[0]));
        assertEquals(String.valueOf(Short.MIN_VALUE), String.valueOf(result[1]));
        assertNull(result[2]);
    }

    //short[] = {Short.MAX_VALUE, Short.MIN_VALUE}
    @Test
    public void testShortArray2() throws Exception {
        short[] testData = {Short.MAX_VALUE, Short.MIN_VALUE};
        short[] shorts = executeBackAndForth(testData, short[].class);
        assertNotNull(shorts);
        assertEquals(short[].class, shorts.getClass());
        assertEquals(String.valueOf(Short.MAX_VALUE), String.valueOf(shorts[0]));
        assertEquals(String.valueOf(Short.MIN_VALUE), String.valueOf(shorts[1]));
    }

    //Integer[] = {Integer.MAX_VALUE, Integer.MIN_VALUE}
    @Test
    public void testIntegerArray1() throws Exception {
        Integer[] testData = {Integer.MAX_VALUE, Integer.MIN_VALUE, null};
        Integer[] result = executeBackAndForth(testData, Integer[].class);
        assertNotNull(result);
        assertEquals(Integer[].class, result.getClass());
        assertEquals(String.valueOf(Integer.MAX_VALUE), String.valueOf(result[0]));
        assertEquals(String.valueOf(Integer.MIN_VALUE), String.valueOf(result[1]));
        assertNull(result[2]);
    }

    //int[] = {Integer.MAX_VALUE, Integer.MIN_VALUE}
    @Test
    public void testIntegerArray2() throws Exception {
        int[] testData = {Integer.MAX_VALUE, Integer.MIN_VALUE};
        int[] ints = executeBackAndForth(testData, int[].class);
        assertNotNull(ints);
        assertEquals(int[].class, ints.getClass());
        assertEquals(String.valueOf(Integer.MAX_VALUE), String.valueOf(ints[0]));
        assertEquals(String.valueOf(Integer.MIN_VALUE), String.valueOf(ints[1]));
    }

    //Long[] = {Long.MAX_VALUE, Long.MIN_VALUE}
    @Test
    public void testLongArray1() throws Exception {
        Long[] testData = {Long.MAX_VALUE, Long.MIN_VALUE, null};
        Long[] result = executeBackAndForth(testData, Long[].class);
        assertNotNull(result);
        assertEquals(Long[].class, result.getClass());
        assertEquals(String.valueOf(Long.MAX_VALUE), String.valueOf(result[0]));
        assertEquals(String.valueOf(Long.MIN_VALUE), String.valueOf(result[1]));
        assertNull(result[2]);
    }

    //long[] = {Long.MAX_VALUE, Long.MIN_VALUE}
    @Test
    public void testLongArray2() throws Exception {
        long[] testData = {Long.MAX_VALUE, Long.MIN_VALUE};
        long[] longs = executeBackAndForth(testData, long[].class);
        assertNotNull(longs);
        assertEquals(long[].class, longs.getClass());
        assertEquals(String.valueOf(Long.MAX_VALUE), String.valueOf(longs[0]));
        assertEquals(String.valueOf(Long.MIN_VALUE), String.valueOf(longs[1]));
    }

    //String[] = {"1", "A"}
    @Test
    public void testStringArray() throws Exception {
        String[] testData = {"1", "A", null};
        String[] result = executeBackAndForth(testData, String[].class);
        assertNotNull(result);
        assertEquals(String[].class, result.getClass());
        assertEquals("1", String.valueOf(result[0]));
        assertEquals("A", String.valueOf(result[1]));
        assertNull(result[2]);
    }

    //序列化一个Object数组，测试不传多余类名
    @Test
    public void testObjectArray1() throws Exception {
        Object[] booleans = new Object[]{true, false, null, true};
        Object[] result = executeBackAndForth(booleans, Object[].class);
        assertEquals(true, result[0]);
        assertEquals(false, result[1]);
        assertEquals(null, result[2]);
        assertEquals(true, result[3]);
    }

    //序列化一个LoopPerson1的数组，测试不传类名
    @Test
    public void testCollection6() throws Exception{
        LoopPerson1[] array = new LoopPerson1[3];
        LoopPerson1 p1 = new LoopPerson1();
        p1.setB(new Byte("1"));
        LoopPerson1 p2 = new LoopPerson1();
        p2.setB(new Byte("2"));

//        Person1 p3 = new Person1();
//        p3.setId(3L);

        array[0] = p1;
        array[1] = p2;

        LoopPerson1[] result = executeBackAndForth(array, LoopPerson1[].class);
        assertEquals("1", String.valueOf(((LoopPerson1)result[0]).getB()));
        assertEquals("2", String.valueOf(((LoopPerson1)result[1]).getB()));
    }

    //Object数组
    //Object[] = {true, Integer.MAX_VALUE, Long.MAX_VALUE, null, {true, false, null}}
    @Test
    public void testObjectArray2() throws Exception {
        Object[] testData = {true, Integer.MAX_VALUE, Long.MAX_VALUE, null, new Object[]{true, false, null}};
        Object[] objects = executeBackAndForth(testData, Object[].class);
        assertNotNull(objects);
        assertEquals(Object[].class, objects.getClass());
        assertEquals(true, objects[0]);
        assertEquals(String.valueOf(Integer.MAX_VALUE), String.valueOf(objects[1]));
        assertEquals(String.valueOf(Long.MAX_VALUE), String.valueOf(objects[2]));
        assertNull(objects[3]);
        assertEquals(true, ((Object[]) objects[4])[0]);
        assertEquals(false, ((Object[]) objects[4])[1]);
        assertNull(((Object[]) objects[4])[2]);
    }

    //测试HashMap {"1":true, "2":false, "3":null}
    //泛型无用，所以每个元素存type
    @Test
    public void testString_BooleanMap1() throws Exception {
        HashMap<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("1", true);
        map.put("2", false);
        map.put("3", null);
        HashMap result  = executeBackAndForth(map, HashMap.class);
        assertEquals(true, result.get("1"));
        assertEquals(false, result.get("2"));
        assertNull(result.get("3"));
    }

    //测试HashMap {"1":u1, "2":u2, "3":null, "4":u3}
    //泛型无用，所以每个元素存type
    @Test
    public void testString_User1Map1() throws Exception {
        HashMap<String, User1> map = new HashMap<String, User1>();
        User1 u1 = new User1();
        u1.setB(true);
        User1 u2 = new User1();
        u2.setB(false);
        User1 u3 = new User1();
        u3.setB(true);

        map.put("1", u1);
        map.put("2", u2);
        map.put("3", null);
        map.put("4", u3);

        HashMap result  = executeBackAndForth(map, HashMap.class);
        assertEquals(true, ((User1)result.get("1")).getB());
        assertEquals(false, ((User1)result.get("2")).getB());
        assertNull(result.get("3"));
        assertEquals(true, ((User1)result.get("4")).getB());
    }

    //测试HashMap {u1:"1", u2:"2", null:"3", u3:"4"}
    //泛型无用，所以每个元素存type
    @Test
    public void testUser1_StringMap1() throws Exception {
        HashMap<User1, String> map = new HashMap<User1, String>();
        User1 u1 = new User1();
        u1.setB(true);
        User1 u2 = new User1();
        u2.setB(false);
        User1 u3 = new User1();
        u3.setB(true);

        map.put(u1, "1");
        map.put(u2, "2");
        map.put(null, "3");
        map.put(u3, "4");

        HashMap result  = executeBackAndForth(map, HashMap.class);

        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;

        Iterator it = result.entrySet().iterator();
        for (int i = 0; i < result.size(); i++) {
            Map.Entry entry = (Map.Entry)it.next();
            if (entry.getKey() instanceof User1){
                if (((User1)entry.getKey()).getB()){
                    if (entry.getValue().equals("1")){
                        flag1 = true;
                    }else if (entry.getValue().equals("4")){
                        flag4 = true;
                    }
                }else if(!((User1)entry.getKey()).getB()){
                    flag2 = true;
                }
            }else if (entry.getKey() == null){
                if (entry.getValue().equals("3")){
                    flag3 = true;
                }
            }
        }
        assertTrue(flag1);
        assertTrue(flag2);
        assertTrue(flag3);
        assertTrue(flag4);
    }

    //HashMap 在hashmap中插入一些基本类型
    //LinkedHashMap，ConcurrentHashMap同理，只有类型和Type不同
    @Test
    public void testHashMap() throws Exception {
        Map<Object, Object> testData = new HashMap<Object, Object>();
        testData.put("1", 1L);
        testData.put(3.3D, 123);
        Map<Object, Object> map = executeBackAndForth(testData, HashMap.class);
        assertNotNull(map);
        assertEquals(HashMap.class, map.getClass());
        assertEquals(1L, map.get("1"));
        assertEquals(123, map.get(3.3D));
    }

    //测试list中装不同类型的元素时，是否可以针对不同类名选择性写入类名
    @Test
    public void testCollection3() throws Exception{
        ArrayList list = new ArrayList();
        Person1 p1 = new Person1();
        p1.setId(1L);

        Person1 p1_1 = new Person1();
        p1_1.setId(12L);
        Person2 p2 = new Person2();
        p2.setId(2L);

        list.add(p1);
        list.add(p1_1);
        list.add(p2);
        ArrayList result = executeBackAndForth(list, ArrayList.class);
        assertEquals("1", ((Person1)result.get(0)).getId().toString());
        assertEquals("12", ((Person1)result.get(1)).getId().toString());
        assertEquals("2", ((Person2)result.get(2)).getId().toString());
    }

    //List 直接序列化ArrayList
    @Test
    public void testList() throws Exception {
        List<Boolean> testData = new ArrayList<Boolean>();
        testData.add(true);
        testData.add(false);
        List<Boolean> list = executeBackAndForth(testData, ArrayList.class);
        assertNotNull(list);
        assertTrue(list.get(0));
        assertFalse(list.get(1));
    }

    //HashSet 序列化一个HashSet
    @Test
    public void testCollection1() throws Exception {
        Set<Boolean> testData = new HashSet<Boolean>();
        testData.add(true);
        testData.add(null);
        testData.add(false);
        Set<Boolean> set = executeBackAndForth(testData, HashSet.class);
        Iterator<Boolean> it = testData.iterator();
        Iterator<Boolean> itSet = set.iterator();
        while (it.hasNext() && itSet.hasNext()) {
            Boolean b1 = (Boolean) it.next();
            Boolean b2 = (Boolean) itSet.next();
            assertEquals(b1, b2);
        }
    }

    //测试char[5]{'我', '知', '道'},gbk编码
    @Test
    public void testCharArray2() throws Exception {
        char[] chars = new char[]{new String("金".getBytes("UTF-8"), "GBK").charAt(0), new String("三".getBytes("UTF-8"), "GBK").charAt(0), new String("胖".getBytes("UTF-8"), "GBK").charAt(0)};
        Parameters parameters = new Parameters();
        parameters.setCharset("GBK");
        char[] result = executeBackAndForth(chars, char[].class, parameters);
        assertEquals(chars[0], result[0]);
        assertEquals(chars[1], result[1]);
        assertEquals(chars[2], result[2]);
    }
}
