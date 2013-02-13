package testcase.function;

import com.jd.dd.glowworm.util.Parameters;
import javabean.Course;
import javabean.InnerBean;
import org.junit.Test;
import testcase.TestBase;
import userJavabean.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

public class AsmTest extends TestBase{

    //只有一个属性boolean的userJavaBean
    @Test
    public void testUserJavaBean1() throws Exception{
        User1 u = new User1();
        u.setB(true);
        User1 result = executeBackAndForth(u, User1.class);
        assertEquals(true, result.getB());
    }

    //只有一个属性Boolean的userJavaBean
    @Test
    public void testUserJavaBean2() throws Exception{
        User2 u = new User2();
        u.setB(true);
        User2 result = executeBackAndForth(u, User2.class);
        assertEquals(true, result.getB());
    }

    //只有一个属性Boolean的userJavaBean 但是属性为空
    @Test
    public void testUserJavaBean3() throws Exception{
        User2 u = new User2();
        User2 result = executeBackAndForth(u, User2.class);
        assertNull(result.getB());
    }


    @Test
    public void testUserJavaBean4() throws Exception{
        User1 u1 = new User1();
        u1.setB(true);

        User3 u3 = new User3();
        u3.setUser1(u1);

        User3 result = executeBackAndForth(u3, User3.class);
        assertEquals(true, result.getUser1().getB());

    }

    //测试User4中的属性是User1数组的情况，都走asm
    @Test
    public void testUserJavaBean5() throws Exception{
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
        User4 result = executeBackAndForth(u4, User4.class);
        assertEquals(3, result.getUser1s().length);
        assertEquals(true, result.getUser1s()[0].getB());
        assertNull(result.getUser1s()[1]);
        assertEquals(false, result.getUser1s()[2].getB());
    }

    //测试User4中的属性是Object数组的情况，都走asm
    @Test
    public void testUserJavaBean6() throws Exception{
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
        User5 result = executeBackAndForth(u5, User5.class);
        assertEquals(true, ((User1)result.getObjects()[0]).getB());
        assertEquals(false, ((User1)result.getObjects()[1]).getB());
        assertNull(result.getObjects()[2]);
        assertEquals(true, ((User2)result.getObjects()[3]).getB());
    }

    //测试asm中有个map属性，map中放各种类型的变量
    @Test
    public void testHashMap1() throws Exception {
        MapBean mapBean = new MapBean();
        mapBean.setI(1);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("123", 1);
        map.put("222", null);
        map.put(null, "1231123");
        mapBean.setMap(map);

        MapBean mp = executeBackAndForth(mapBean, MapBean.class);

        assertEquals("1", mp.getMap().get("123").toString());
        assertNull(mp.getMap().get("222"));
        assertEquals("1231123", mp.getMap().get(null));
    }

    //类里放各种类型的map，走asm
    @Test
    public void testUserJavaBean7() throws Exception{
        User13 u = new User13();
        Map<Integer, Object> map = new ConcurrentHashMap<Integer, Object>();
        User1 u1 = new User1();
        u1.setB(true);
        User1 u2 = new User1();
        u2.setB(false);

        map.put(1, u1);
        map.put(2, u2);
        map.put(3, "123");

        HashMap<Integer, Object> hashMap = new HashMap<Integer, Object>();
        hashMap.put(1, u1);
        hashMap.put(2, u2);
        hashMap.put(3, "123");

        Map<Integer, User1> map1 = new ConcurrentHashMap<Integer, User1>();
        User1 u1_1 = new User1();
        u1_1.setB(true);
        User1 u2_2 = new User1();
        u2_2.setB(false);

        map1.put(1, u1_1);
        map1.put(2, u2_2);

        u.setHashMap(hashMap);
        u.setMap(map);
        u.setGenericMap(map1);

        User13 result = executeBackAndForth(u, User13.class);
        assertEquals(true, ((User1)result.getMap().get(1)).getB());
        assertEquals(false, ((User1)result.getMap().get(2)).getB());
        assertEquals("123", result.getMap().get(3));
        assertEquals(true, ((User1)result.getHashMap().get(1)).getB());
        assertEquals(false, ((User1)result.getHashMap().get(2)).getB());
        assertEquals("123", result.getHashMap().get(3));
        assertEquals(true, (result.getGenericMap().get(1)).getB());
        assertEquals(false, (result.getGenericMap().get(2)).getB());

    }

    //Asm 中有属性是ConcurrentHashMap的情况 有循环引用
    @Test
    public void testSupplyMap1() throws Exception {
        Supply1 s1 = new Supply1();
        ConcurrentHashMap map1 = new ConcurrentHashMap();
        map1.put("c1_2", "c1_value");
        map1.put("c2_2", "c2_value");
        map1.put(1, s1);

        s1.setTheConHMap(map1);
        Supply1 result = executeBackAndForth(s1, Supply1.class);

        for (Object obj : s1.getTheConHMap().entrySet()) {
            if (((Map.Entry) obj).getValue() instanceof Supply1) {
                Supply1 sResult = (Supply1) ((Map.Entry) obj).getValue();
                assertNotNull(sResult.getTheConHMap());
                assertEquals(3, sResult.getTheConHMap().entrySet().size());
            } else {
                assertEquals(((Map.Entry) obj).getValue(), result.getTheConHMap().get(((Map.Entry) obj).getKey()));
            }
        }
    }

    //测试list中装不同类型的元素时，是否可以针对不同类名选择性写入类名
    @Test
    public void testCollection2() throws Exception{
        PersonCollection3 p = new PersonCollection3();
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
        p.setList(list);
        PersonCollection3 result = executeBackAndForth(p, PersonCollection3.class);
        assertEquals("1", ((Person1)result.getList().get(0)).getId().toString());
        assertEquals("12", ((Person1)result.getList().get(1)).getId().toString());
        assertEquals("2", ((Person2)result.getList().get(2)).getId().toString());
    }


    //测试Person1 {
    // private Long id; //1
    // }
    @Test
    public void testJavaBean1() throws Exception {
        Person1 testData = new Person1();
        testData.setId(1L);
        Person1 p = executeBackAndForth(testData, Person1.class);
        assertNotNull(p);
        assertEquals(Person1.class, p.getClass());
        assertEquals(String.valueOf(1L), p.getId().toString());
    }

    //测试Person1 {
    // private Long id; //null
    // }
    @Test
    public void testJavaBean2() throws Exception {
        Person1 testData = new Person1();
        Person1 p = executeBackAndForth(testData, Person1.class);
        assertNotNull(p);
        assertEquals(Person1.class, p.getClass());
        assertNull(p.getId());
    }

    //测试Person2 {
    // private Long id; //null
    // private String name;//null
    // }
    @Test
    public void testJavaBean3() throws Exception {
        Person2 testData = new Person2();
        Person2 p = executeBackAndForth(testData, Person2.class);
        assertNotNull(p);
        assertEquals(Person2.class, p.getClass());
        assertNull(p.getId());
        assertNull(p.getName());
    }


    //测试Person2 {
    // private Long id; //1
    // private String name;//null
    // }
    @Test
    public void testJavaBean4() throws Exception {
        Person2 testData = new Person2();
        testData.setId(1L);
        Person2 p = executeBackAndForth(testData, Person2.class);
        assertNotNull(p);
        assertEquals(Person2.class, p.getClass());
        assertEquals(String.valueOf(1L), p.getId().toString());
        assertNull(p.getName());
    }

    //测试Person2 {
    // private Long id; //1
    // private String name;//"360buy.com"
    // }
    @Test
    public void testJavaBean5() throws Exception {
        Person2 testData = new Person2();
        testData.setId(1L);
        testData.setName("360buy.com");
        Person2 p = executeBackAndForth(testData, Person2.class);
        assertNotNull(p);
        assertEquals(Person2.class, p.getClass());
        assertEquals(String.valueOf(1L), p.getId().toString());
        assertEquals("360buy.com", p.getName());
    }

    //javabean中放入所有基础类型
    @Test
    public void testJavaBean6() throws Exception {
        Person4 testData = new Person4();
        testData.setC(Character.MAX_VALUE);
        testData.setI(Integer.MAX_VALUE);
        testData.setB(Byte.MAX_VALUE);
        testData.setS(Short.MAX_VALUE);
        testData.setF(Float.MAX_VALUE);
        testData.setD(Double.MAX_VALUE);
        testData.setL(Long.MAX_VALUE);
        testData.setBool(Boolean.TRUE);
        Person4 p = executeBackAndForth(testData, Person4.class);
        assertNotNull(p);
        assertEquals(Person4.class, p.getClass());
        assertEquals(String.valueOf(Character.MAX_VALUE), String.valueOf(p.getC()));
        assertEquals(String.valueOf(Integer.MAX_VALUE), String.valueOf(p.getI()));
        assertEquals(String.valueOf(Byte.MAX_VALUE), String.valueOf(p.getB()));
        assertEquals(String.valueOf(Short.MAX_VALUE), String.valueOf(p.getS()));
        assertEquals(String.valueOf(Float.MAX_VALUE), String.valueOf(p.getF()));
        assertEquals(String.valueOf(Double.MAX_VALUE), String.valueOf(p.getD()));
        assertEquals(String.valueOf(Long.MAX_VALUE), String.valueOf(p.getL()));
        assertEquals(String.valueOf(Boolean.TRUE), String.valueOf(p.isBool()));
    }

    //javabean中放入基础类型的包装类
    @Test
    public void testJavaBean7() throws InterruptedException {
        Person1 testData = new Person1();
        testData.setId(Long.MAX_VALUE);
        Person1 p = executeBackAndForth(testData, Person1.class);
        assertNotNull(p);
        assertEquals(Person1.class, p.getClass());
        assertEquals(String.valueOf(Long.MAX_VALUE), String.valueOf(p.getId()));
    }


    //循环引用1
    //{"b":1,"brother":{"b":2,"brother":{"$ref":".."}}}
    @Test
    public void testLoopJavaBean1() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        lp1.setB(new Byte("1"));
        LoopPerson1 lp2 = new LoopPerson1();
        lp2.setB(new Byte("2"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);

        LoopPerson1 result = executeBackAndForth(lp1, LoopPerson1.class);
        assertEquals(new Byte("1").byteValue(), result.getB());
        assertEquals(new Byte("2").byteValue(), result.getBrother().getB());
        assertEquals(new Byte("1").byteValue(), result.getBrother().getBrother().getB());
    }

    //循环引用2
    //{"b":1,"brother":{"b":2,"brother":{"b":3,"brother":{"$ref":"$"}}}}
    @Test
    public void testLoopJavaBean2() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        lp1.setB(new Byte("1"));
        LoopPerson1 lp2 = new LoopPerson1();
        lp2.setB(new Byte("2"));
        LoopPerson1 lp3 = new LoopPerson1();
        lp3.setB(new Byte("3"));

        lp1.setBrother(lp2);
        lp2.setBrother(lp3);
        lp3.setBrother(lp1);

        LoopPerson1 result = executeBackAndForth(lp1, LoopPerson1.class);
        assertEquals(new Byte("1").byteValue(), result.getB());
        assertEquals(new Byte("2").byteValue(), result.getBrother().getB());
        assertEquals(new Byte("3").byteValue(), result.getBrother().getBrother().getB());

        assertEquals(new Byte("1").byteValue(), result.getBrother().getBrother().getBrother().getB());
    }


    //循环引用3
    //{"b":1,"brother":{"$ref":"@"}}
    @Test
    public void testLoopJavaBean3() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        lp1.setB(new Byte("1"));

        lp1.setBrother(lp1);

        LoopPerson1 lp = executeBackAndForth(lp1, LoopPerson1.class);
        assertEquals(new Byte("1").byteValue(), lp.getB());
        assertEquals(new Byte("1").byteValue(), lp.getBrother().getB());
    }


    //循环引用4
    //ref在数组中
    //{"b":1,"brother":{"b":3,"brother":{"b":2}},"loopPerson2s":[{"$ref":"$.brother.brother"}]}
    @Test
    public void testLoopJavaBean4() throws Exception {
        LoopPerson2 lp1 = new LoopPerson2();
        lp1.setB(new Byte("1"));

        LoopPerson2 lp2 = new LoopPerson2();
        lp2.setB(new Byte("2"));

        LoopPerson2[] loopPerson2s = {lp2};
        lp1.setLoopPerson2s(loopPerson2s);

        LoopPerson2 lp3 = new LoopPerson2();
        lp3.setB(new Byte("3"));
        lp3.setBrother(lp2);

        lp1.setBrother(lp3);

        LoopPerson2 lp = executeBackAndForth(lp1, LoopPerson2.class);
        assertEquals(new Byte("1").byteValue(), lp.getB());
        assertEquals(new Byte("3").byteValue(), lp.getBrother().getB());
        assertEquals(lp.getLoopPerson2s()[0].getB(), lp.getBrother().getBrother().getB());
    }

    //循环引用4
    //ref在List中
    //{"b":1,"brother":{"b":3,"brother":{"b":2}},"list":[{"$ref":"$.brother.brother"}]}
    @Test
    public void testLoopJavaBean5() throws Exception {
        LoopPerson3 lp1 = new LoopPerson3();
        lp1.setB(new Byte("1"));

        LoopPerson3 lp2 = new LoopPerson3();
        lp2.setB(new Byte("2"));

        List<LoopPerson3> list = new ArrayList<LoopPerson3>();
        list.add(lp2);
        lp1.setList(list);

        LoopPerson3 lp3 = new LoopPerson3();
        lp3.setB(new Byte("3"));
        lp3.setBrother(lp2);

        lp1.setBrother(lp3);

        LoopPerson3 lp = executeBackAndForth(lp1, LoopPerson3.class);
        assertEquals(new Byte("1").byteValue(), lp.getB());
        assertEquals(new Byte("3").byteValue(), lp.getBrother().getB());
        assertEquals(lp.getList().get(0).getB(), lp.getBrother().getBrother().getB());
    }

    //循环引用4
    //ref在Set中
    //{"b":1,"brother":{"b":3,"brother":{"b":2}},"set":[{"$ref":"$.brother.brother"}]}
    @Test
    public void testLoopJavaBean6() throws Exception {
        LoopPerson4 lp1 = new LoopPerson4();
        lp1.setB(new Byte("1"));

        LoopPerson4 lp2 = new LoopPerson4();
        lp2.setB(new Byte("2"));

        Set<LoopPerson4> treeSet = new TreeSet<LoopPerson4>();
        treeSet.add(lp2);
        lp1.setSet(treeSet);

        LoopPerson4 lp3 = new LoopPerson4();
        lp3.setB(new Byte("3"));
        lp3.setBrother(lp2);

        lp1.setBrother(lp3);

        LoopPerson4 lp = executeBackAndForth(lp1, LoopPerson4.class);
        assertEquals(new Byte("1").byteValue(), lp.getB());
        assertEquals(new Byte("3").byteValue(), lp.getBrother().getB());
        assertEquals(lp.getSet().iterator().next().getB(), lp.getBrother().getBrother().getB());
    }

    //ref在map中作为value
    @Test
    public void testLoopJavaBean8() throws Exception {
        LoopPerson5 lp = new LoopPerson5();
        HashMap<String, LoopPerson5> map = new HashMap<String, LoopPerson5>();
        map.put("mySelf", lp);
        lp.setMap(map);

        LoopPerson5 result = executeBackAndForth(lp, LoopPerson5.class);
        assertNotNull(result);
        assertEquals(result, result.getMap().get("mySelf"));
    }

    //ref在map中做key
    @Test
    public void testLoopJavaBean9() throws Exception {
        LoopPerson6 lp = new LoopPerson6();
        HashMap<LoopPerson6, String> map = new HashMap<LoopPerson6, String>();
        map.put(lp, "mySelf");
        lp.setMap(map);
        LoopPerson6 result = executeBackAndForth(lp, LoopPerson6.class);
        assertNotNull(result);
        assertEquals("mySelf", result.getMap().get(result));
    }

    //引用作为map的value
    @Test
    public void testLoopJavaBean10() throws Exception {
        LoopPerson7 lp1 = new LoopPerson7();
        LoopPerson7 lp2 = new LoopPerson7();
        LoopPerson7 lp3 = new LoopPerson7();

        lp1.setBro(lp2);
        HashMap<String, LoopPerson7> map = new HashMap<String, LoopPerson7>();
        lp3.setBro(lp2);
        map.put("abcd", lp3);
        lp1.setMap(map);
        lp2.setBro(lp3);

        LoopPerson7 result = executeBackAndForth(lp1, LoopPerson7.class);
        assertEquals(result.getBro(), result.getMap().get("abcd").getBro());
    }

    //11~13为序列化List相关的所有循环引用的功能测试
    @Test
    public void testLoopJavaBean11() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));
        ArrayList<LoopPerson1> list = new ArrayList<LoopPerson1>();
        list.add(lp1);
        list.add(lp2);

        ArrayList<LoopPerson1> result = executeBackAndForth(list, ArrayList.class);
        assertEquals(result.get(0), result.get(1).getBrother());
    }


    //这里注意一下，如果用接口类型反序列化时，必须强转，而不能把接口类当参数传入进行反序列化
    //并且必须手动设置传入类名
    @Test
    public void testLoopJavaBean12() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));
        List<LoopPerson1> list = new ArrayList<LoopPerson1>();
        list.add(lp1);
        list.add(lp2);

        Parameters parameters = new Parameters();
        parameters.setNeedWriteClassName(true);
        List<LoopPerson1> result = (List)executeBackAndForth(list, parameters);
        assertEquals(result.get(0), result.get(1).getBrother());
    }

    @Test
    public void testLoopJavaBean13() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));
        List list = new ArrayList();
        list.add(lp1);
        list.add(lp2);

        ArrayList result = executeBackAndForth(list, ArrayList.class);
        assertEquals((LoopPerson1) result.get(0), ((LoopPerson1) result.get(1)).getBrother());
    }

    //14~16为序列化set
    @Test
    public void testLoopJavaBean14() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));
        HashSet<LoopPerson1> list = new HashSet<LoopPerson1>();
        list.add(lp1);
        list.add(lp2);

        HashSet<LoopPerson1> result = executeBackAndForth(list, HashSet.class);
        Iterator<LoopPerson1> it = result.iterator();
        assertEquals(it.next(), it.next().getBrother());
    }

    //这里注意一下，如果用接口类型反序列化时，必须强转，而不能把接口类当参数传入进行反序列化
    //而且必须手动设置传入类名
    @Test
    public void testLoopJavaBean15() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));
        Set<LoopPerson1> list = new HashSet<LoopPerson1>();
        list.add(lp1);
        list.add(lp2);

        Parameters parameters = new Parameters();
        parameters.setNeedWriteClassName(true);

        Set<LoopPerson1> result = (Set)executeBackAndForth(list, parameters);
        Iterator<LoopPerson1> it = result.iterator();
        assertEquals(it.next(), it.next().getBrother());
    }

    @Test
    public void testLoopJavaBean16() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));
        Set list = new HashSet();
        list.add(lp1);
        list.add(lp2);

        HashSet result = executeBackAndForth(list, HashSet.class);
        Iterator<LoopPerson1> it = result.iterator();
        assertEquals(it.next(), it.next().getBrother());
    }

    //17~22为序列化map
    @Test
    public void testLoopJavaBean17() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));

        HashMap<String, LoopPerson1> map = new HashMap<String, LoopPerson1>();
        map.put("1", lp1);
        map.put("2", lp2);

        HashMap<String, LoopPerson1> result = executeBackAndForth(map, HashMap.class);
        assertEquals(result.get("1"), result.get("2").getBrother());
    }

    //这里注意一下，如果用接口类型反序列化时，必须强转，而不能把接口类当参数传入进行反序列化
    //而且必须手动设置传入类名
    @Test
    public void testLoopJavaBean18() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));

        Map<String, LoopPerson1> map = new HashMap<String, LoopPerson1>();
        map.put("1", lp1);
        map.put("2", lp2);

        Parameters parameters = new Parameters();
        parameters.setNeedWriteClassName(true);

        Map<String, LoopPerson1> result = (Map)executeBackAndForth(map, parameters);
        assertEquals(result.get("1"), result.get("2").getBrother());
    }

    //这里注意一下，如果用接口类型反序列化时，必须强转，而不能把接口类当参数传入进行反序列化
    //而且必须手动设置传入类名
    @Test
    public void testLoopJavaBean19() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));

        Map map = new HashMap();
        map.put("1", lp1);
        map.put("2", lp2);

        Parameters parameters = new Parameters();
        parameters.setNeedWriteClassName(true);

        Map result = (Map)executeBackAndForth(map, parameters);
        assertEquals((LoopPerson1) result.get("1"), ((LoopPerson1) result.get("2")).getBrother());
    }

    @Test
    public void testLoopJavaBean20() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));
        HashMap<LoopPerson1, String> map = new HashMap<LoopPerson1, String>();
        map.put(lp1, "1");
        map.put(lp2, "2");

        HashMap<LoopPerson1, String> result = executeBackAndForth(map, HashMap.class);
        LoopPerson1 expected = null;
        LoopPerson1 actual = null;
        for (Map.Entry<LoopPerson1, String> entry : result.entrySet()) {
            if (entry.getValue().equals("1")) {
                expected = entry.getKey();
            }
            if (entry.getValue().equals("2")) {
                actual = entry.getKey().getBrother();
            }
        }
        assertEquals(expected, actual);
    }

    @Test
    public void testLoopJavaBean21() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));
        Map<LoopPerson1, String> map = new HashMap<LoopPerson1, String>();
        map.put(lp1, "1");
        map.put(lp2, "2");

        Map<LoopPerson1, String> result = executeBackAndForth(map, HashMap.class);
        LoopPerson1 expected = null;
        LoopPerson1 actual = null;
        for (Map.Entry<LoopPerson1, String> entry : result.entrySet()) {
            if (entry.getValue().equals("1")) {
                expected = entry.getKey();
            }
            if (entry.getValue().equals("2")) {
                actual = entry.getKey().getBrother();
            }
        }
        assertEquals(expected, actual);
    }

    //这里注意一下，如果用接口类型反序列化时，必须强转，而不能把接口类当参数传入进行反序列化
    //而且必须手动设置传入类名
    @Test
    public void testLoopJavaBean22() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        LoopPerson1 lp2 = new LoopPerson1();
        lp1.setB(Byte.valueOf("1"));
        lp1.setBrother(lp2);
        lp2.setBrother(lp1);
        lp2.setB(Byte.valueOf("2"));
        Map map = new HashMap();
        map.put(lp1, "1");
        map.put(lp2, "2");

        Parameters parameters = new Parameters();
        parameters.setNeedWriteClassName(true);

        Map result = (Map)executeBackAndForth(map, parameters);
        LoopPerson1 expected = null;
        LoopPerson1 actual = null;
        for (Object entry : result.entrySet()) {
            if (((Map.Entry) entry).getValue().equals("1")) {
                expected = (LoopPerson1) ((Map.Entry) entry).getKey();
            }
            if (((Map.Entry) entry).getValue().equals("2")) {
                actual = ((LoopPerson1) ((Map.Entry) entry).getKey()).getBrother();
            }
        }
        assertEquals(expected, actual);
    }

    //属性为
    //List<LoopPerson8> list = new LinkedList<LoopPerson8>();
    @Test
    public void testLoopJavaBean23() throws Exception {
        LoopPerson8 lp = new LoopPerson8();
        lp.setInteger(1);

        LoopPerson8 lp2 = new LoopPerson8();
        lp2.setInteger(2);
        lp2.setLp(lp);

        lp.setLp(lp2);
        List<LoopPerson8> list = new LinkedList<LoopPerson8>();
        list.add(lp2);
        lp.setList(list);

        LoopPerson8 result = executeBackAndForth(lp, LoopPerson8.class);
        assertNotNull(result);
        assertEquals("1", result.getInteger().toString());
        assertEquals("2", result.getLp().getInteger().toString());
        assertEquals(result, result.getLp().getLp());
        assertEquals(result.getList().getClass(), LinkedList.class);
        assertEquals(result.getLp(), result.getList().get(0));
    }


    //属性为
    //Set<LoopPerson9> set = new HashSet<LoopPerson9>();
    @Test
    public void testLoopJavaBean24() throws Exception {
//        LoopPerson9 lp = new LoopPerson9();
//        lp.setInteger(1);
//
//        LoopPerson9 lp2 = new LoopPerson9();
//        lp2.setInteger(2);
//
//        LoopPerson9 lp3 = new LoopPerson9();
//        lp3.setInteger(3);
//
//        lp2.setLp(lp3);
//        lp3.setLp(lp);
//        lp.setLp(lp2);
//
//        Set<LoopPerson9> set = new HashSet<LoopPerson9>();
//        set.add(lp2);
//        set.add(lp3);
//
//        lp.setSet(set);
//
//        LoopPerson9 result = executeBackAndForth(lp, LoopPerson9.class);
//        assertNotNull(result);
//        assertEquals("1", result.getInteger().toString());
//        assertEquals("2", result.getLp().getInteger().toString());
//        assertEquals(result, result.getLp().getLp().getLp());
//        assertEquals(result.getSet().getClass(), HashSet.class);
//
//        Iterator<LoopPerson9> it = result.getSet().iterator();
//        assertEquals(2, result.getSet().size());
//        LoopPerson9 res2 = null;
//        LoopPerson9 res3 = null;
//        while (it.hasNext()) {
//            LoopPerson9 lpInSet = it.next();
//            if (lpInSet.getInteger() == 2) {
//                res2 = lpInSet;
//            } else if (lpInSet.getInteger() == 3) {
//                res3 = lpInSet;
//            }
//        }
//        assertEquals(result.getLp(), res2);
//        assertEquals(result.getLp().getLp(), res3);
    }

    //属性为
    //Map<String, LoopPerson10> map = new HashMap<String, LoopPerson10>();
    @Test
    public void testLoopJavaBean25() throws Exception {
//        LoopPerson10 lp = new LoopPerson10();
//        lp.setInteger(1);
//
//        LoopPerson10 lp2 = new LoopPerson10();
//        lp2.setInteger(2);
//
//        LoopPerson10 lp3 = new LoopPerson10();
//        lp3.setInteger(3);
//
//        lp2.setLp(lp3);
//        lp3.setLp(lp);
//        lp.setLp(lp2);
//
//        Map<String, LoopPerson10> map1 = new HashMap<String, LoopPerson10>();
//        map1.put("2", lp2);
//        map1.put("3", lp3);
//        lp.setMap1(map1);
//
//        Map<LoopPerson10, String> map2 = new ConcurrentHashMap<LoopPerson10, String>();
//        map2.put(lp2, "2");
//        map2.put(lp3, "3");
//        lp.setMap2(map2);
//
//        LoopPerson10 result = executeBackAndForth(lp, LoopPerson10.class);
//        assertNotNull(result);
//        assertEquals("1", result.getInteger().toString());
//        assertEquals("2", result.getLp().getInteger().toString());
//        assertEquals(result, result.getLp().getLp().getLp());
//        assertEquals(result.getMap1().getClass(), HashMap.class);
//
//        assertEquals(lp2.getInteger(), result.getMap1().get("2").getInteger());
//        assertEquals(lp3.getInteger(), result.getMap1().get("3").getInteger());
//
//        assertEquals(result.getMap2().getClass(), ConcurrentHashMap.class);
//
//        for (Map.Entry<LoopPerson10, String> entry : result.getMap2().entrySet()) {
//            if (entry.getValue().equals("2")) {
//                assertEquals(lp2.getInteger(), entry.getKey().getInteger());
//            }
//            if (entry.getValue().equals("3")) {
//                assertEquals(lp3.getInteger(), entry.getKey().getInteger());
//            }
//        }
    }

    //内部类用接口申请集合属性
    @Test
    public void testLoopJavaBean26() throws Exception {
//        LoopPerson11 lp = new LoopPerson11();
//
//        LoopPerson11 result = executeBackAndForth(lp, LoopPerson11.class);
//        assertEquals(result, result.findInnerList().get(0));
//        assertEquals(result, result.findInnerSet().iterator().next());
//        assertEquals(result, result.findInnerMap().get("1"));
    }


    @Test
    public void testLoopJavaBean30() throws Exception {
//        LoopPerson1 lp1 = new LoopPerson1();
//        lp1.setB(new Byte("1"));
//
//        LoopPerson1 lp2 = new LoopPerson1();
//        lp2.setB(new Byte("2"));
//
//        LoopPerson1 lp3 = new LoopPerson1();
//        lp3.setB(new Byte("3"));
//
//        LoopPerson1 lp4 = new LoopPerson1();
//        lp4.setB(new Byte("4"));
//
//        lp1.setBrother(lp2);
//        lp2.setBrother(lp3);
//        lp3.setBrother(lp4);
//        lp4.setBrother(lp2);
//
//        LoopPerson1 result = executeBackAndForth(lp1, LoopPerson1.class);
//        assertEquals(result.getBrother(), result.getBrother().getBrother().getBrother().getBrother());
    }


    @Test
    public void testLoopJavaBean31() throws Exception {
//        Group admin = new Group("admin");
//
//        User jobs = new User("jobs");
//        User sager = new User("sager");
//        User sdh5724 = new User("sdh5724");
//
//        //双向引用
//        admin.getMembers().add(jobs);
//        jobs.getGroups().add(admin);
//
//        admin.getMembers().add(sager);
//        sager.getGroups().add(admin);
//
//        admin.getMembers().add(sdh5724);
//        sdh5724.getGroups().add(admin);
//
//        sager.setReportTo(sdh5724);
//        jobs.setReportTo(sdh5724);
//
//        Group group = executeBackAndForth(admin, Group.class);
//        assertEquals(group.getMembers().get(0).getName(), "jobs");
//        assertEquals(group.getMembers().get(0).getGroups().get(0).getName(), "admin");
//
//        assertEquals(group.getMembers().get(1).getName(), "sager");
//        assertEquals(group.getMembers().get(1).getGroups().get(0).getName(), "admin");
//
//        assertEquals(group.getMembers().get(2).getName(), "sdh5724");
//        assertEquals(group.getMembers().get(2).getGroups().get(0).getName(), "admin");
//
//        assertEquals(group.getMembers().get(1).getReportTo().getName(), "sdh5724");
//        assertEquals(group.getMembers().get(0).getReportTo().getName(), "sdh5724");
    }



    //针对用Object声明属性，用所有其他类型实现属性
    //关于Object属性声明的终极测试！
    @Test
    public void testJavaBean8() throws Exception{
        Person10 p = new Person10();
        p.setObj1(true);
        p.setObj2(new Boolean(false));
        p.setObj3(new Byte("1").byteValue());
        p.setObj4(new Byte("1"));
        p.setObj5(Character.MIN_VALUE);
        p.setObj6(new Character('A'));
        p.setObj7(Double.MIN_VALUE);
        p.setObj8(new Double("100"));
        p.setObj9(Float.MIN_VALUE);
        p.setObj10(new Float("1.21"));
        p.setObj11(Integer.MIN_VALUE);
        p.setObj12(new Integer("121212"));
        p.setObj13(BigDecimal.ONE);
        p.setObj14(BigInteger.TEN);
        p.setObj15(Long.MAX_VALUE);
        p.setObj16(new Long("123123123"));
        p.setObj17(Short.MIN_VALUE);
        p.setObj18(new Short("123"));
        p.setObj19("123123");
        p.setObj20(Course.Chinese);
        p.setObj21(Inet4Address.getByName("127.0.0.1"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date date = formatter.parse("2012-12-21 23:59:59", pos);
        p.setObj22(date);
        long millis = (System.currentTimeMillis() / 1000) * 1000;
        Timestamp timestamp = new Timestamp(millis);
        p.setObj23(timestamp);
        p.setObj24(new AtomicBoolean(true));
        p.setObj25(new AtomicInteger(1212312));
        p.setObj26(new AtomicLong(13123123123L));
        Person1 ppp = new Person1();
        ppp.setId(1L);
        p.setObj27(ppp);
        InnerBean ib = new InnerBean();
        ib.setI(1);
        p.setObj28(ib);
        List<String> list1 = new LinkedList<String>();
        list1.add("123");
        list1.add("223");
        p.setObj29(list1);
        List list2 = new LinkedList();
        list2.add("123");
        list2.add(223);
        p.setObj30(list2);
        List list3 = new LinkedList();
        list3.add("123");
        list3.add(223);
        Person1 list3p = new Person1();
        list3p.setId(1L);
        list3.add(list3p);
        p.setObj31(list3);

        Set<String> set1 = new TreeSet<String>();
        set1.add("123");
        p.setObj32(set1);
        Set set2 = new TreeSet();
        set2.add(223);
        p.setObj33(set2);
        Set set3 = new TreeSet();
        Person1 set3p = new Person1();
        set3p.setId(1L);
        set3.add(set3p);
        p.setObj34(set3);

        Map<String, Integer> map1 = new LinkedHashMap<String, Integer>();
        map1.put("123", 123);
        p.setObj35(map1);
        Map map2 = new LinkedHashMap();
        map2.put(123, "123");
        p.setObj36(map2);
        Map map3 = new LinkedHashMap();
        Person1 map3p = new Person1();
        map3p.setId(1L);
        map3.put(map3p, 123);
        p.setObj37(map3);
        Map map4 = new LinkedHashMap();
        Person1 map4p = new Person1();
        map4p.setId(1L);
        map4.put(123, map4p);
        p.setObj38(map4);

        p.setObj39(p);
        p.setObj40(list3p);
        Map mapRef = new HashMap();
        mapRef.put(1, map4p);
        p.setObj41(mapRef);

        p.setObj42(map3);

        Person10 result = executeBackAndForth(p, Person10.class);
        assertEquals(p.getObj1().toString(), result.getObj1().toString());
        assertEquals(p.getObj2().toString(), result.getObj2().toString());
        assertEquals(p.getObj3().toString(), result.getObj3().toString());
        assertEquals(p.getObj4().toString(), result.getObj4().toString());
        assertEquals(p.getObj5().toString(), result.getObj5().toString());
        assertEquals(p.getObj6().toString(), result.getObj6().toString());
        assertEquals(p.getObj7().toString(), result.getObj7().toString());
        assertEquals(p.getObj8().toString(), result.getObj8().toString());
        assertEquals(p.getObj9().toString(), result.getObj9().toString());
        assertEquals(p.getObj10().toString(), result.getObj10().toString());
        assertEquals(p.getObj11().toString(), result.getObj11().toString());
        assertEquals(p.getObj12().toString(), result.getObj12().toString());
        assertEquals(p.getObj13().toString(), result.getObj13().toString());
        assertEquals(p.getObj14().toString(), result.getObj14().toString());
        assertEquals(p.getObj15().toString(), result.getObj15().toString());
        assertEquals(p.getObj16().toString(), result.getObj16().toString());
        assertEquals(p.getObj17().toString(), result.getObj17().toString());
        assertEquals(p.getObj18().toString(), result.getObj18().toString());
        assertEquals(p.getObj19().toString(), result.getObj19().toString());
        assertEquals(p.getObj20().toString(), result.getObj20().toString());
        assertEquals(p.getObj21().toString(), result.getObj21().toString());

        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals("2012-12-21 23:59:59", formatter1.format(result.getObj22()));

        assertEquals(p.getObj23().toString(), result.getObj23().toString());
        assertEquals(p.getObj24().toString(), result.getObj24().toString());
        assertEquals(p.getObj25().toString(), result.getObj25().toString());
        assertEquals(p.getObj26().toString(), result.getObj26().toString());
        assertEquals(((Person1)p.getObj27()).getId(), ((Person1)result.getObj27()).getId());
        assertEquals(((InnerBean) p.getObj28()).getI(), ((InnerBean) result.getObj28()).getI());

        assertEquals(((LinkedList<String>)(p.getObj29())).get(0), ((LinkedList<String>)(result.getObj29())).get(0));
        assertEquals(((LinkedList<String>) (p.getObj29())).get(1), ((LinkedList<String>) (result.getObj29())).get(1));
        assertEquals(((LinkedList)(p.getObj30())).get(0), ((LinkedList)(result.getObj30())).get(0));
        assertEquals(((LinkedList)(p.getObj30())).get(1), ((LinkedList)(result.getObj30())).get(1));
        assertEquals(((LinkedList)(p.getObj31())).get(0), ((LinkedList)(result.getObj31())).get(0));
        assertEquals(((LinkedList)(p.getObj31())).get(1), ((LinkedList)(result.getObj31())).get(1));
        assertEquals(((Person1)((LinkedList)(p.getObj31())).get(2)).getId(), ((Person1)((LinkedList)(result.getObj31())).get(2)).getId());

        assertEquals(((TreeSet)p.getObj32()).iterator().next(), ((TreeSet)result.getObj32()).iterator().next());
        assertEquals(((TreeSet)p.getObj33()).iterator().next(), ((TreeSet)result.getObj33()).iterator().next());
        assertEquals(((Person1)((TreeSet)p.getObj34()).iterator().next()).getId(),((Person1)((TreeSet)result.getObj34()).iterator().next()).getId());

        assertEquals(((LinkedHashMap)p.getObj35()).get("123"), ((LinkedHashMap)result.getObj35()).get("123"));
        assertEquals(((LinkedHashMap)p.getObj36()).get(123), ((LinkedHashMap)result.getObj36()).get(123));
        assertEquals(((Person1)(((Map.Entry)(((LinkedHashMap)p.getObj37()).entrySet().iterator().next())).getKey())).getId(), ((Person1)(((Map.Entry)(((LinkedHashMap)result.getObj37()).entrySet().iterator().next())).getKey())).getId());
        assertEquals(((Person1)(((Map.Entry)(((LinkedHashMap)p.getObj38()).entrySet().iterator().next())).getValue())).getId(), ((Person1)(((Map.Entry)(((LinkedHashMap)result.getObj38()).entrySet().iterator().next())).getValue())).getId());

        assertEquals(result, result.getObj39());
        assertEquals(((Person1)((LinkedList)(result.getObj31())).get(2)), result.getObj40());
        assertEquals(((Map.Entry)(((LinkedHashMap)result.getObj38()).entrySet().iterator().next())).getValue(), ((Map.Entry)(((HashMap)result.getObj41()).entrySet().iterator().next())).getValue());

        assertEquals(result.getObj37(), result.getObj42());
    }

    //针对javabean-asm中有HASHMAP，无循环引用 value
    @Test
    public void testJavaBean9() throws Exception{
        LoopPerson5 lp1 = new LoopPerson5();
        lp1.setInteger(1);

        LoopPerson5 lp2 = new LoopPerson5();
        lp2.setInteger(2);

        HashMap<String, LoopPerson5> map = new HashMap<String, LoopPerson5>();
        map.put("2", lp2);
        lp1.setMap(map);

        LoopPerson5 result = executeBackAndForth(lp1, LoopPerson5.class);
        assertEquals(lp2.getInteger(), result.getMap().get("2").getInteger());
    }

    //针对javabean-asm中有HASHMAP，无循环引用 key
    @Test
    public void testJavaBean10() throws Exception{
//        LoopPerson6 lp1 = new LoopPerson6();
//        lp1.setInteger(1);
//
//        LoopPerson6 lp2 = new LoopPerson6();
//        lp2.setInteger(2);
//
//        HashMap<LoopPerson6, String> map = new HashMap<LoopPerson6, String>();
//        map.put(lp2, "2");
//        lp1.setMap(map);
//
//        LoopPerson6 result = executeBackAndForth(lp1, LoopPerson6.class);
//        assertEquals(lp2.getInteger(), result.getMap().entrySet().iterator().next().getKey().getInteger());
    }


}
