package testcase.function;

import com.alibaba.fastjson.JSONObject;
import com.jd.bdp.seomonitor.model.page.SeoWord;
import com.jd.dd.glowworm.PB;
import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.util.Parameters;
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

public class AsmTest extends TestBase {

    //只有一个属性boolean的userJavaBean
    @Test
    public void testUserJavaBean1() throws Exception {
        User1 u = new User1();
        u.setB(true);
        User1 result = executeBackAndForth(u, User1.class);
        assertEquals(true, result.getB());
    }

    //只有一个属性Boolean的userJavaBean
    @Test
    public void testUserJavaBean2() throws Exception {
        User2 u = new User2();
        u.setB(true);
        User2 result = executeBackAndForth(u, User2.class);
        assertEquals(true, result.getB());
    }

    //只有一个属性Boolean的userJavaBean 但是属性为空
    @Test
    public void testUserJavaBean3() throws Exception {
        User2 u = new User2();
        User2 result = executeBackAndForth(u, User2.class);
        assertNull(result.getB());
    }


    @Test
    public void testUserJavaBean4() throws Exception {
        User1 u1 = new User1();
        u1.setB(true);

        User3 u3 = new User3();
        u3.setUser1(u1);

        User3 result = executeBackAndForth(u3, User3.class);
        assertEquals(true, result.getUser1().getB());

    }

    //测试User4中的属性是User1数组的情况，都走asm
    @Test
    public void testUserJavaBean5() throws Exception {
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
    public void testUserJavaBean6() throws Exception {
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
        assertEquals(true, ((User1) result.getObjects()[0]).getB());
        assertEquals(false, ((User1) result.getObjects()[1]).getB());
        assertNull(result.getObjects()[2]);
        assertEquals(true, ((User2) result.getObjects()[3]).getB());
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
    public void testUserJavaBean7() throws Exception {
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
        assertEquals(true, ((User1) result.getMap().get(1)).getB());
        assertEquals(false, ((User1) result.getMap().get(2)).getB());
        assertEquals("123", result.getMap().get(3));
        assertEquals(true, ((User1) result.getHashMap().get(1)).getB());
        assertEquals(false, ((User1) result.getHashMap().get(2)).getB());
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
    public void testCollection2() throws Exception {
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
        assertEquals("1", ((Person1) result.getList().get(0)).getId().toString());
        assertEquals("12", ((Person1) result.getList().get(1)).getId().toString());
        assertEquals("2", ((Person2) result.getList().get(2)).getId().toString());
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
        List<LoopPerson1> result = (List) executeBackAndForth(list, parameters);
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

        Set<LoopPerson1> result = (Set) executeBackAndForth(list, parameters);
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

        Map<String, LoopPerson1> result = (Map) executeBackAndForth(map, parameters);
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

        Map result = (Map) executeBackAndForth(map, parameters);
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

        Map result = (Map) executeBackAndForth(map, parameters);
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
        LoopPerson9 lp = new LoopPerson9();
        lp.setInteger(1);

        LoopPerson9 lp2 = new LoopPerson9();
        lp2.setInteger(2);

        LoopPerson9 lp3 = new LoopPerson9();
        lp3.setInteger(3);

        lp2.setLp(lp3);
        lp3.setLp(lp);
        lp.setLp(lp2);

        Set<LoopPerson9> set = new HashSet<LoopPerson9>();
        set.add(lp2);
        set.add(lp3);

        lp.setSet(set);

        LoopPerson9 result = executeBackAndForth(lp, LoopPerson9.class);
        assertNotNull(result);
        assertEquals("1", result.getInteger().toString());
        assertEquals("2", result.getLp().getInteger().toString());
        assertEquals(result, result.getLp().getLp().getLp());
        assertEquals(result.getSet().getClass(), HashSet.class);

        Iterator<LoopPerson9> it = result.getSet().iterator();
        assertEquals(2, result.getSet().size());
        LoopPerson9 res2 = null;
        LoopPerson9 res3 = null;
        while (it.hasNext()) {
            LoopPerson9 lpInSet = it.next();
            if (lpInSet.getInteger() == 2) {
                res2 = lpInSet;
            } else if (lpInSet.getInteger() == 3) {
                res3 = lpInSet;
            }
        }
        assertEquals(result.getLp(), res2);
        assertEquals(result.getLp().getLp(), res3);
    }

    //属性为
    //Map<String, LoopPerson10> map = new HashMap<String, LoopPerson10>();
    @Test
    public void testLoopJavaBean25() throws Exception {
        LoopPerson10 lp = new LoopPerson10();
        lp.setInteger(1);

        LoopPerson10 lp2 = new LoopPerson10();
        lp2.setInteger(2);

        LoopPerson10 lp3 = new LoopPerson10();
        lp3.setInteger(3);

        lp2.setLp(lp3);
        lp3.setLp(lp);
        lp.setLp(lp2);

        Map<String, LoopPerson10> map1 = new HashMap<String, LoopPerson10>();
        map1.put("2", lp2);
        map1.put("3", lp3);
        lp.setMap1(map1);

        Map<LoopPerson10, String> map2 = new ConcurrentHashMap<LoopPerson10, String>();
        map2.put(lp2, "2");
        map2.put(lp3, "3");
        lp.setMap2(map2);

        LoopPerson10 result = executeBackAndForth(lp, LoopPerson10.class);
        assertNotNull(result);
        assertEquals("1", result.getInteger().toString());
        assertEquals("2", result.getLp().getInteger().toString());
        assertEquals(result, result.getLp().getLp().getLp());
        assertEquals(result.getMap1().getClass(), HashMap.class);

        assertEquals(lp2.getInteger(), result.getMap1().get("2").getInteger());
        assertEquals(lp3.getInteger(), result.getMap1().get("3").getInteger());

        assertEquals(result.getMap2().getClass(), ConcurrentHashMap.class);

        for (Map.Entry<LoopPerson10, String> entry : result.getMap2().entrySet()) {
            if (entry.getValue().equals("2")) {
                assertEquals(lp2.getInteger(), entry.getKey().getInteger());
            }
            if (entry.getValue().equals("3")) {
                assertEquals(lp3.getInteger(), entry.getKey().getInteger());
            }
        }
    }

    //内部类用接口申请集合属性
    @Test
    public void testLoopJavaBean26() throws Exception {
        LoopPerson11 lp = new LoopPerson11();

        LoopPerson11 result = executeBackAndForth(lp, LoopPerson11.class);
        assertEquals(result, result.findInnerList().get(0));
        assertEquals(result, result.findInnerSet().iterator().next());
        assertEquals(result, result.findInnerMap().get("1"));
    }


    //比较简单的对象套对象的循环引用
    @Test
    public void testLoopJavaBean30() throws Exception {
        LoopPerson1 lp1 = new LoopPerson1();
        lp1.setB(new Byte("1"));

        LoopPerson1 lp2 = new LoopPerson1();
        lp2.setB(new Byte("2"));

        LoopPerson1 lp3 = new LoopPerson1();
        lp3.setB(new Byte("3"));

        LoopPerson1 lp4 = new LoopPerson1();
        lp4.setB(new Byte("4"));

        lp1.setBrother(lp2);
        lp2.setBrother(lp3);
        lp3.setBrother(lp4);
        lp4.setBrother(lp2);

        LoopPerson1 result = executeBackAndForth(lp1, LoopPerson1.class);
        assertEquals(result.getBrother(), result.getBrother().getBrother().getBrother().getBrother());
    }


    //fastJson的循环引用案例
    @Test
    public void testLoopJavaBean31() throws Exception {
        Group admin = new Group("admin");

        User jobs = new User("jobs");
        User sager = new User("sager");
        User sdh5724 = new User("sdh5724");

        //双向引用
        admin.getMembers().add(jobs);
        jobs.getGroups().add(admin);

        admin.getMembers().add(sager);
        sager.getGroups().add(admin);

        admin.getMembers().add(sdh5724);
        sdh5724.getGroups().add(admin);

        sager.setReportTo(sdh5724);
        jobs.setReportTo(sdh5724);

        Group group = executeBackAndForth(admin, Group.class);
        assertEquals(group.getMembers().get(0).getName(), "jobs");
        assertEquals(group.getMembers().get(0).getGroups().get(0).getName(), "admin");

        assertEquals(group.getMembers().get(1).getName(), "sager");
        assertEquals(group.getMembers().get(1).getGroups().get(0).getName(), "admin");

        assertEquals(group.getMembers().get(2).getName(), "sdh5724");
        assertEquals(group.getMembers().get(2).getGroups().get(0).getName(), "admin");

        assertEquals(group.getMembers().get(1).getReportTo().getName(), "sdh5724");
        assertEquals(group.getMembers().get(0).getReportTo().getName(), "sdh5724");
    }


    //针对用Object声明属性，用所有其他类型实现属性
    //关于Object属性声明的终极测试！
    @Test
    public void testJavabeanWithAllObjectField() throws Exception {
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

        Person1 p1 = new Person1();
        p1.setId(1L);
        List list = Arrays.asList("1", 1, p1);
        p.setObj43(list);

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
        assertEquals(((Person1) p.getObj27()).getId(), ((Person1) result.getObj27()).getId());
        assertEquals(((InnerBean) p.getObj28()).getI(), ((InnerBean) result.getObj28()).getI());

        assertEquals(((LinkedList<String>) (p.getObj29())).get(0), ((LinkedList<String>) (result.getObj29())).get(0));
        assertEquals(((LinkedList<String>) (p.getObj29())).get(1), ((LinkedList<String>) (result.getObj29())).get(1));
        assertEquals(((LinkedList) (p.getObj30())).get(0), ((LinkedList) (result.getObj30())).get(0));
        assertEquals(((LinkedList) (p.getObj30())).get(1), ((LinkedList) (result.getObj30())).get(1));
        assertEquals(((LinkedList) (p.getObj31())).get(0), ((LinkedList) (result.getObj31())).get(0));
        assertEquals(((LinkedList) (p.getObj31())).get(1), ((LinkedList) (result.getObj31())).get(1));
        assertEquals(((Person1) ((LinkedList) (p.getObj31())).get(2)).getId(), ((Person1) ((LinkedList) (result.getObj31())).get(2)).getId());

        assertEquals(((TreeSet) p.getObj32()).iterator().next(), ((TreeSet) result.getObj32()).iterator().next());
        assertEquals(((TreeSet) p.getObj33()).iterator().next(), ((TreeSet) result.getObj33()).iterator().next());
        assertEquals(((Person1) ((TreeSet) p.getObj34()).iterator().next()).getId(), ((Person1) ((TreeSet) result.getObj34()).iterator().next()).getId());

        assertEquals(((LinkedHashMap) p.getObj35()).get("123"), ((LinkedHashMap) result.getObj35()).get("123"));
        assertEquals(((LinkedHashMap) p.getObj36()).get(123), ((LinkedHashMap) result.getObj36()).get(123));
        assertEquals(((Person1) (((Map.Entry) (((LinkedHashMap) p.getObj37()).entrySet().iterator().next())).getKey())).getId(), ((Person1) (((Map.Entry) (((LinkedHashMap) result.getObj37()).entrySet().iterator().next())).getKey())).getId());
        assertEquals(((Person1) (((Map.Entry) (((LinkedHashMap) p.getObj38()).entrySet().iterator().next())).getValue())).getId(), ((Person1) (((Map.Entry) (((LinkedHashMap) result.getObj38()).entrySet().iterator().next())).getValue())).getId());

        assertEquals(result, result.getObj39());
        assertEquals(((Person1) ((LinkedList) (result.getObj31())).get(2)), result.getObj40());
        assertEquals(((Map.Entry) (((LinkedHashMap) result.getObj38()).entrySet().iterator().next())).getValue(), ((Map.Entry) (((HashMap) result.getObj41()).entrySet().iterator().next())).getValue());

        assertEquals(result.getObj37(), result.getObj42());
        assertEquals(3, ((List) result.getObj43()).size());
        assertEquals("1", ((List) result.getObj43()).get(0));
        assertEquals(1, ((List) result.getObj43()).get(1));
        assertEquals("1", ((Person1) ((List) result.getObj43()).get(2)).getId().toString());
    }

    //针对javabean-asm中有HASHMAP，无循环引用 value
    @Test
    public void testJavaBean9() throws Exception {
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
    public void testJavaBean10() throws Exception {
        LoopPerson6 lp1 = new LoopPerson6();
        lp1.setInteger(1);

        LoopPerson6 lp2 = new LoopPerson6();
        lp2.setInteger(2);

        HashMap<LoopPerson6, String> map = new HashMap<LoopPerson6, String>();
        map.put(lp2, "2");
        lp1.setMap(map);

        LoopPerson6 result = executeBackAndForth(lp1, LoopPerson6.class);
        assertEquals(lp2.getInteger(), result.getMap().entrySet().iterator().next().getKey().getInteger());
    }


    //无泛型或泛型为Object的集合，如何循环引用
    @Test
    public void testJavaBeanLoopWithCollection() throws Exception {
        Person11 p1 = new Person11();
        p1.setInteger(1);
        Person11 p2 = new Person11();
        p2.setInteger(2);

        Map map1 = new LinkedHashMap();
        map1.put(1, p1);
        map1.put(p2, 2);

        Map<Object, Object> map2 = new LinkedHashMap<Object, Object>();
        map2.put(1, p1);
        map2.put(p2, 2);

        p1.setMap1(map1);
        p1.setMap2(map2);
        p1.setP(p2);

        Person11 result = executeBackAndForth(p1, Person11.class);
        assertEquals(p1.getInteger(), result.getInteger());
        assertEquals(p2.getInteger(), result.getP().getInteger());
        assertEquals(result, result.getMap1().get(1));
        assertEquals(2, result.getMap1().get(result.getP()));
        assertEquals(result, result.getMap2().get(1));
        assertEquals(2, result.getMap2().get(result.getP()));
    }

    //跟Atomic相关的javabean
    @Test
    public void testAtomicBean1() throws Exception {
        AtomicPerson1 ap1 = new AtomicPerson1();
        AtomicInteger ai = new AtomicInteger(Integer.MIN_VALUE);
        ap1.setAi(ai);
        AtomicBoolean ab = new AtomicBoolean(true);
        ap1.setAb(ab);
        AtomicLong al = new AtomicLong(Long.MIN_VALUE);
        ap1.setAl(al);
        AtomicPerson1 result = executeBackAndForth(ap1, AtomicPerson1.class);
        assertEquals(Integer.MIN_VALUE, result.getAi().intValue());
        assertEquals(true, result.getAb().get());
        assertEquals(Long.MIN_VALUE, result.getAl().longValue());
    }

    //比较基础的有内部类的情况
    @Test
    public void testInnerJavaBean() throws Exception {
        Person7 p7 = new Person7();
        Person7 person7 = executeBackAndForth(p7, Person7.class);
        assertNotNull(person7);
        assertEquals(Person7.class, person7.getClass());
        assertEquals(1, person7.findPerson9Id());
        assertEquals("123232", person7.getYName());
        assertEquals("123", person7.findPerson9Name());
    }

    //内部类中带引用
    @Test
    public void testInnerJavaBeanWithRef() throws Exception {
        InnerBean ib1 = new InnerBean();
        ib1.setI(1);

        InnerBean ib2 = new InnerBean();
        ib2.setI(2);
        ib2.setIb(ib1);

        ib1.setIb(ib2);

        InnerBean ib = executeBackAndForth(ib1, InnerBean.class);

        assertEquals(1, ib.getIb().getIb().getI());
    }


    //对象套对象，双向关联的引用
    @Test
    public void testComplicatedJavaBean1() throws Exception {
        School school = new School();
        school.setId(1L);
        school.setName("蓝翔技校");

        List<Student> list = new ArrayList<Student>();

        Student s1 = new Student();
        s1.setId(1L);
        s1.setName("小明");
        s1.setSalary(10001.23D);
        s1.setSex(true);
        s1.setSchool(school);

        list.add(s1);
        school.setStudents(list);

        Student student1 = executeBackAndForth(s1, Student.class);
        assertNotNull(student1);
        assertEquals(Student.class, student1.getClass());
        assertEquals(String.valueOf(1L), student1.getId().toString());
        assertEquals("小明", student1.getName());
        assertEquals(String.valueOf(10001.23D), student1.getSalary().toString());
        assertEquals(true, student1.getSex());
        assertNotNull(student1.getSchool());
        assertEquals(String.valueOf(1L), student1.getSchool().getId().toString());
        assertEquals("蓝翔技校", student1.getSchool().getName());

    }

    //对象套对象和对象数组
    @Test
    public void testComplicatedJavaBean2() throws Exception {
        Person5 p = new Person5();
        Person6 p1 = new Person6();
        p1.setName("123");
        Object[] person6s = {p1};
        p.setPerson6s(person6s);
        Person5 person5 = executeBackAndForth(p, Person5.class);
        assertNotNull(person5);
        assertNotNull((person5.getPerson6s())[0]);
        assertEquals("123", ((Person6) person5.getPerson6s()[0]).getName());
    }

    //跟枚举相关
    @Test
    public void testComplicatedJavaBean3() throws Exception {
        Person8 p = new Person8();
        List<Course> list = new ArrayList<Course>();
        list.add(Course.Chinese);
        list.add(Course.English);
        p.setCourses(list);
        Person8 person8 = executeBackAndForth(p, Person8.class);
        assertNotNull(person8.getCourses());
    }


    //对象套对象和对象数组,对象list，带引用
    @Test
    public void testComplicatedJavaBean4() throws Exception {
        School school = new School();
        school.setId(1L);
        school.setName("蓝翔技校");

        List<Student> list = new ArrayList<Student>();

        Student s1 = new Student();
        s1.setId(1L);
        s1.setName("小明");
        s1.setSalary(10001.23D);
        s1.setSex(true);
        s1.setSchool(school);

        list.add(s1);
        school.setStudents(list);

        Student s2 = new Student();
        s2.setId(2L);
        s2.setName("小红");
        s2.setSchool(school);

        Student[] classmates = {s2};
        s1.setClassmates(classmates);

        Set<Course> courses = new HashSet<Course>();
        courses.add(Course.Chinese);
        courses.add(Course.English);

        s1.setCourses(courses);

        Student xiaoming = executeBackAndForth(s1, Student.class);
        assertNotNull(xiaoming);
        assertEquals(Student.class, xiaoming.getClass());
        assertEquals(String.valueOf(1L), xiaoming.getId().toString());
        assertEquals("小明", xiaoming.getName());
        assertNotNull(xiaoming.getClassmates()[0].getSchool());
        assertEquals(String.valueOf(1L), xiaoming.getClassmates()[0].getSchool().getId().toString());
        assertEquals("蓝翔技校", xiaoming.getClassmates()[0].getSchool().getName());

        assertNotNull(xiaoming.getClassmates()[0]);
        assertEquals(Student.class, xiaoming.getClassmates()[0].getClass());
        assertEquals(String.valueOf(2L), xiaoming.getClassmates()[0].getId().toString());
        assertEquals("小红", xiaoming.getClassmates()[0].getName());
        assertEquals(String.valueOf(10001.23D), xiaoming.getSalary().toString());
        assertEquals(true, xiaoming.getSex());
        assertNotNull(xiaoming.getSchool());
        assertEquals(String.valueOf(1L), xiaoming.getSchool().getId().toString());
        assertEquals("蓝翔技校", xiaoming.getSchool().getName());

        assertEquals("小明", xiaoming.getSchool().getStudents().get(0).getName());

        assertFalse(xiaoming.getCourses().isEmpty());
        for (Course course : xiaoming.getCourses()) {
            assertEquals(Course.class, course.getClass());
        }
    }


    //private ConcurrentHashMap theConHMap------javabean
    @Test
    public void testSupplyMap2() throws Exception {
        Supply2 s2 = new Supply2();
        ConcurrentHashMap map1 = new ConcurrentHashMap();
        map1.put("c1_2", "c1_value");
        map1.put("c2_2", "c2_value");
        map1.put(1, s2);

        s2.setTheConHMap(map1);
        Supply2 result = executeBackAndForth(s2, Supply2.class);

        for (Object obj : s2.getTheConHMap().entrySet()) {
            if (((Map.Entry) obj).getValue() instanceof Supply2) {
                Supply2 sResult = (Supply2) ((Map.Entry) obj).getValue();
                assertNotNull(sResult.getTheConHMap());
                assertEquals(3, sResult.getTheConHMap().entrySet().size());
            } else {
                assertEquals(((Map.Entry) obj).getValue(), result.getTheConHMap().get(((Map.Entry) obj).getKey()));
            }
        }
    }

    //private List list;
    //list 中包含 HashMap ------ ASM
    @Test
    public void testSupplyMapInList1() throws Exception {
        Supply3 s3 = new Supply3();
        ArrayList list = new ArrayList();
        HashMap map = new HashMap();
        map.put("1231", "MMMM--DD");
        map.put("asdfksl", "123u1293");
        list.add(map);
        s3.setList(list);
        Supply3 result = executeBackAndForth(s3, Supply3.class);
        assertNotNull(result);
        assertNotNull(result.getList());
        assertEquals(1, result.getList().size());
        assertEquals("MMMM--DD", ((Map) result.getList().get(0)).get("1231"));
        assertEquals("123u1293", ((Map) result.getList().get(0)).get("asdfksl"));
    }

    //序列化一个对象，对象里有个属性是list，无泛型，不传多余类名
    @Test
    public void testCollectionWithLoop4() throws Exception {
        PersonCollection3 pc = new PersonCollection3();
        ArrayList list = new ArrayList();
        LoopPerson1 p1 = new LoopPerson1();
        p1.setB(new Byte("1"));
        LoopPerson1 p2 = new LoopPerson1();
        p2.setB(new Byte("2"));

        LoopPerson1 p2_1 = new LoopPerson1();
        p2_1.setB(new Byte("44"));

        Person1 p3 = new Person1();
        p3.setId(3L);

        list.add(p1);
        list.add(p2);
        list.add(123);
        list.add(p2_1);
        list.add(p3);
        pc.setList(list);

        PersonCollection3 result = executeBackAndForth(pc, PersonCollection3.class);
        assertEquals("1", String.valueOf(((LoopPerson1) result.getList().get(0)).getB()));
        assertEquals("2", String.valueOf(((LoopPerson1) result.getList().get(1)).getB()));
        assertEquals("123", String.valueOf(result.getList().get(2)));
        assertEquals("44", String.valueOf(((LoopPerson1) result.getList().get(3)).getB()));
        assertEquals(String.valueOf(3), ((Person1) result.getList().get(4)).getId().toString());
    }

    //序列化一个对象，对象里有个属性是list，有泛型但泛型是Object，不传多余类名
    @Test
    public void testCollectionWithLoop6() throws Exception {
        PersonCollection4 pc = new PersonCollection4();
        ArrayList<Object> list = new ArrayList<Object>();
        LoopPerson1 p1 = new LoopPerson1();
        p1.setB(new Byte("1"));
        LoopPerson1 p2 = new LoopPerson1();
        p2.setB(new Byte("2"));

        LoopPerson1 p2_1 = new LoopPerson1();
        p2_1.setB(new Byte("44"));

        Person1 p3 = new Person1();
        p3.setId(3L);

        list.add(p1);
        list.add(p2);
        list.add(123);
        list.add(p2_1);
        list.add(p3);
        pc.setList(list);

        PersonCollection4 result = executeBackAndForth(pc, PersonCollection4.class);
        assertEquals("1", String.valueOf(((LoopPerson1) result.getList().get(0)).getB()));
        assertEquals("2", String.valueOf(((LoopPerson1) result.getList().get(1)).getB()));
        assertEquals("123", String.valueOf(result.getList().get(2)));
        assertEquals("44", String.valueOf(((LoopPerson1) result.getList().get(3)).getB()));
        assertEquals(String.valueOf(3), ((Person1) result.getList().get(4)).getId().toString());
    }

    //序列化一个对象，对象里有个属性是list，有泛型，不传类名
    @Test
    public void testCollectionWithLoop7() throws Exception {
        UserGeneric ug = new UserGeneric();
        Person2 p1 = new Person2();
        p1.setId(1L);
        p1.setName("111");
        Person2 p2 = new Person2();
        p2.setId(2L);
        p2.setName("222");

        ArrayList<Person2> list = new ArrayList<Person2>();
        list.add(p1);
        list.add(p2);
        ug.setList(list);

        UserGeneric result = executeBackAndForth(ug, UserGeneric.class);
        assertEquals(2, result.getList().size());

        assertEquals("1", result.getList().get(0).getId().toString());
        assertEquals("2", result.getList().get(1).getId().toString());
    }


    //序列化一个对象，对象里有个属性是set，有泛型
    @Test
    public void testCollectionWithLoop8() throws Exception {
        UserGeneric ug = new UserGeneric();
        Person2 p1 = new Person2();
        p1.setId(1L);
        p1.setName("111");
        Person2 p2 = new Person2();
        p2.setId(2L);
        p2.setName("222");

        HashSet<Person2> set = new HashSet<Person2>();
        set.add(p1);
        set.add(p2);
        ug.setSet(set);

        UserGeneric result = executeBackAndForth(ug, UserGeneric.class);
        assertEquals(2, result.getSet().size());
    }

    //序列化一个对象，对象里有个属性是set，有泛型,是接口
    @Test
    public void testCollectionWithLoop9() throws Exception {
        UserGeneric ug = new UserGeneric();
        Person2 p1 = new Person2();
        p1.setId(1L);
        p1.setName("111");
        Person2 p2 = new Person2();
        p2.setId(2L);
        p2.setName("222");

        Set<Person2> set = new HashSet<Person2>();
        set.add(p1);
        set.add(p2);
        ug.setSet1(set);

        UserGeneric result = executeBackAndForth(ug, UserGeneric.class);
        assertEquals(2, result.getSet1().size());
    }

    //最复杂的javabean -- javabean包含inner
    @Test
    public void testTheMostComplicatedJavaBean1() throws Exception {
        TpMagic tpMagic = new TpMagic();
        tpMagic.setS1("test hahahahahaha");
        tpMagic.setD1(1001.02);
        tpMagic.setL1(new Long(1000000));
        tpMagic.setTheb2(true);
        tpMagic.setTheBn1(new Boolean(true));
        int[] tmpIA = new int[5];
        tmpIA[0] = 100;
        tmpIA[4] = 104;

        tpMagic.setTheDifficulty(Difficulty.NORMAL);

        tpMagic.setTheBigDecimal(new BigDecimal("100.021"));

        int[] tmpIB = new int[5];
        tmpIB[0] = 1000;
        tmpIB[4] = 1004;
        tpMagic.setTheI_Array(tmpIB);

        Object[] tmpObjs = new Object[5];
        tmpObjs[0] = 10;
        tmpObjs[1] = new String("abc");
        ArrayList tmpList_objectItem = new ArrayList();
        tmpList_objectItem.add(123);
        tmpList_objectItem.add("abc");
        ArrayList tmpList2 = new ArrayList();
        tmpList2.add(1234);
        tmpList2.add("str1");
        tmpList_objectItem.add(tmpList2);
        tmpObjs[2] = tmpList_objectItem;
        tmpObjs[3] = Difficulty.HARD;
        tmpObjs[4] = new BigDecimal("22.022");
        tpMagic.setTheObjs(tmpObjs);

        ArrayList tmpList = new ArrayList();
        tmpList.add(123);
        tmpList.add("abc");

        HashMap tmpListItemHMap = new HashMap();
        tmpListItemHMap.put(1, "ok le man1");
        tmpList.add(tmpListItemHMap);

        LinkedHashMap tmpLinkedHashMap = new LinkedHashMap();
        tmpLinkedHashMap.put(1, "1 value");
        tmpLinkedHashMap.put(2, "2 value");
        tmpList.add(tmpLinkedHashMap);

        ConcurrentHashMap tmpCHMap = new ConcurrentHashMap();
        tmpCHMap.put("c1", "c1_value");
        tmpCHMap.put("c2", "c2_value");
        tmpList.add(tmpCHMap);

        byte[] tmpbytes2 = new byte[100];
        tmpbytes2[0] = 10;
        tmpbytes2[10] = 100;
        tmpbytes2[20] = 120;
        tmpList.add(tmpbytes2);

        long[] tmpLongs2 = new long[10];
        tmpLongs2[0] = 100;
        tmpLongs2[1] = 1000;
        tmpLongs2[2] = 10000;
        tmpList.add(tmpLongs2);

        char tmpChar2 = 13;
        Character tmpCharacter2 = new Character((char) 14);
        tmpList.add(tmpChar2);
        tmpList.add(tmpCharacter2);

        char[] tmpChars2 = new char[5];
        tmpChars2[0] = 10;
        tmpChars2[1] = 20;
        tmpList.add(tmpChars2);

        double[] tmpDous2 = new double[6];
        tmpDous2[0] = 100;
        tmpDous2[1] = 200;
        tmpDous2[2] = 300;
        tmpList.add(tmpDous2);
        tpMagic.setTheList(tmpList);

        LinkedHashMap tmpLinkedHashMap2 = new LinkedHashMap();
        tmpLinkedHashMap2.put(1000, "1000 value");
        tmpLinkedHashMap2.put(2000, "2000 value");
        tpMagic.setTheLinkHMap(tmpLinkedHashMap2);

        ConcurrentHashMap tmpCHMap2 = new ConcurrentHashMap();
        tmpCHMap2.put("c1_2", "c1_value");
        tmpCHMap2.put("c2_2", "c2_value");
        tpMagic.setTheConHMap(tmpCHMap2);

        ConcurrentHashMap tmpCHMap3 = new ConcurrentHashMap();
        tmpCHMap3.put("c1_3", "c3_value");
        tmpCHMap3.put("c2_3", "c3_value");

        HashMap tmpHMap2 = new HashMap();
        tmpHMap2.put(1, "ok le man1");

        LinkedHashMap tmpLinkedHMap2 = new LinkedHashMap();
        tmpLinkedHMap2.put(22, "1 value");
        tmpLinkedHMap2.put(23, "2 value");
        tmpLinkedHMap2.put(24, (byte) 20);

        byte tmpbyte = 100;
        Byte tmpB = new Byte((byte) 200);
        tpMagic.setThebyte_b(tmpbyte);
        tpMagic.setTheByte_Byte(tmpB);

        byte[] tmpbytes = new byte[100];
        tmpbytes[0] = 0;
        tmpbytes[10] = 10;
        tmpbytes[20] = 20;
        tpMagic.setThebytes(tmpbytes);

        long[] tmpLongs = new long[10];
        tmpLongs[0] = 10;
        tmpLongs[1] = 100;
        tmpLongs[2] = 1000;
        tpMagic.setTheLongs(tmpLongs);

        long tmpl1 = 1000;
        tpMagic.setL_1_2(tmpl1);

        char tmpChar = 200;
        tpMagic.setTheChar(tmpChar);

        Character tmpCharacter = new Character((char) 300);
        tpMagic.setTheChar_2(tmpCharacter);

        char[] tmpChars = new char[10];
        tmpChars[1] = 20;
        tmpChars[2] = 21;
        tmpChars[8] = 22;
        tpMagic.setTheChars(tmpChars);

        double[] tmpDous = new double[6];
        tmpDous[0] = 100;
        tmpDous[1] = 200;
        tmpDous[2] = 300;
        tpMagic.setTheDous(tmpDous);

        ArrayList<Double> tmpDList = new ArrayList();
        tmpDList.add(100.20);
        tmpDList.add(null);
        tmpDList.add(200.60);
        tpMagic.setTheDList(tmpDList);

        ArrayList<Integer> tmpIList = new ArrayList();
        tmpIList.add(100);
        tmpIList.add(200);
        tpMagic.setTheIList(tmpIList);

        ArrayList<String> tmpSList = new ArrayList();
        tmpSList.add("abc1");
        tmpSList.add("abc2");
        tpMagic.setTheSList(tmpSList);

        ArrayList tmpRuleItemList = new ArrayList();
        RuleItem tmpListRuleItem = new RuleItem();
        tmpListRuleItem.setDescription("description_test");
        tmpListRuleItem.setItem_id("item_id_test");
        tmpListRuleItem.setRule_id("rule_id_test");
        tmpListRuleItem.setWarning_value("warning_value_test");
        tmpRuleItemList.add(tmpListRuleItem);

        tpMagic.setTheRuleItemList(tmpRuleItemList);

        LinkedList tmpRuleItemLinkedList = new LinkedList();
        RuleItem tmpListRuleItem2 = new RuleItem();
        tmpListRuleItem2.setDescription("description_test");
        tmpListRuleItem2.setItem_id("item_id_test");
        tmpListRuleItem2.setRule_id("rule_id_test");
        tmpListRuleItem2.setWarning_value("warning_value_test_haha");
        tmpRuleItemLinkedList.add(tmpListRuleItem2);

        LinkedList tmpRuleItemLinkedList1 = new LinkedList();
        tmpRuleItemLinkedList1.add("s1");
        tmpRuleItemLinkedList1.add(100);
        tmpRuleItemLinkedList1.add(null);

        RuleItem tmpListRuleItem3 = new RuleItem();
        tmpListRuleItem3.setDescription("description_test");
        tmpListRuleItem3.setItem_id("item_id_test");
        tmpListRuleItem3.setRule_id("rule_id_test_3_ok le man");
        tmpListRuleItem3.setWarning_value("warning_value_test_haha");
        tmpRuleItemLinkedList1.add(tmpListRuleItem3);

        tmpRuleItemLinkedList.add(tmpRuleItemLinkedList1);

        tpMagic.setTheRuleItemLinkedList(tmpRuleItemLinkedList);

        HashSet tmpHashSet = new HashSet();

        RuleItem tmpListRuleItem4 = new RuleItem();
        tmpListRuleItem4.setDescription("description_test");
        tmpListRuleItem4.setItem_id("item_id_test");
        tmpListRuleItem4.setRule_id("rule_id_test_4_ok le man");
        tmpHashSet.add(tmpListRuleItem4);

        tmpHashSet.add(null);
        tmpHashSet.add("str1");

        RuleItem tmpListRuleItem5 = new RuleItem();
        tmpListRuleItem5.setDescription("description_test");
        tmpListRuleItem5.setItem_id("item_id_test");
        tmpListRuleItem5.setRule_id("rule_id_test_5_ok le man");
        tmpHashSet.add(tmpListRuleItem5);

        tpMagic.setTheHSet(tmpHashSet);

        TreeSet tmpTreeSet = new TreeSet();
        tmpTreeSet.add("tree 0");
        tmpTreeSet.add("tree 1");
        tmpTreeSet.add("tree 2");
        tpMagic.setTheTreeSet(tmpTreeSet);

        HashSet<String> tmpStringHashSet = new HashSet();
        tmpStringHashSet.add("ok");
        tmpStringHashSet.add("a13");
        tpMagic.setTheHStringSet(tmpStringHashSet);

        short[] tmpShort = new short[6];
        tmpShort[0] = 100;
        tmpShort[1] = 200;
        tmpShort[2] = 10000;
        tpMagic.setTheShort(tmpShort);

        float[] tmpFloats = new float[6];
        tmpFloats[0] = new Float("100.01");
        tmpFloats[1] = 200;
        tmpFloats[2] = 10000;
        tpMagic.setTheFloats(tmpFloats);

        boolean[] tmpBooleans = new boolean[5];
        tmpBooleans[0] = true;
        tmpBooleans[1] = false;
        tmpBooleans[2] = true;
        tpMagic.setTheBooleans(tmpBooleans);

        short tmpTheShort = new Short("-32768").shortValue();
        tpMagic.setTheShortOne(tmpTheShort);

        float tmpFloat = new Float("1.4E-45").floatValue();
        tpMagic.setTheFloatOne(tmpFloat);

        tpMagic.setTheBigLong(10101010011L);
//        byte[] bytes = executeSerialization(tpMagic);
//        TpMagic obj = (TpMagic)executeDeserialization(bytes);

        TpMagic result = executeBackAndForth(tpMagic, TpMagic.class);

        //come on!40个属性!
        assertEquals(tpMagic.getD1(), result.getD1());//0
        assertEquals(tpMagic.getD2(), result.getD2());//1
        assertEquals(tpMagic.getI1(), result.getI1());//2
        assertEquals(tpMagic.getL1(), result.getL1());//3
        assertEquals(tpMagic.getL_1_2(), result.getL_1_2());//4
        assertEquals(tpMagic.getS1(), result.getS1());//5
        assertEquals(tpMagic.getTheBigDecimal(), result.getTheBigDecimal());//6
        assertEquals(tpMagic.getTheBigLong(), result.getTheBigLong());//7
        assertEquals(tpMagic.getTheBn1(), result.getTheBn1());//8
        assertEquals(tpMagic.getTheBn2(), result.getTheBn2());//9
        //10
        for (int i = 0; i < tpMagic.getTheBooleans().length; i++) {
            assertEquals(tpMagic.getTheBooleans()[i], result.getTheBooleans()[i]);
        }
        assertEquals(String.valueOf(tpMagic.getTheByte_Byte()), String.valueOf(result.getTheByte_Byte()));//11
        assertEquals(tpMagic.getTheChar(), result.getTheChar());//12
        assertEquals(tpMagic.getTheChar_2(), result.getTheChar_2());//13
        //14
        for (int i = 0; i < tpMagic.getTheChars().length; i++) {
            assertEquals(tpMagic.getTheChars()[i], result.getTheChars()[i]);
        }
        //15
        for (Object obj : tpMagic.getTheConHMap().entrySet()) {
            assertEquals(((Map.Entry) obj).getValue(), result.getTheConHMap().get(((Map.Entry) obj).getKey()));
        }
        //16
        for (int i = 0; i < tpMagic.getTheDList().size(); i++) {
            assertEquals(tpMagic.getTheDList().get(i), result.getTheDList().get(i));
        }
        assertEquals(tpMagic.getTheDifficulty(), result.getTheDifficulty());//17
        //18
        for (int i = 0; i < tpMagic.getTheDous().length; i++) {
            assertEquals(String.valueOf(tpMagic.getTheDous()[i]), String.valueOf(result.getTheDous()[i]));
        }
        assertEquals(String.valueOf(tpMagic.getTheFloatOne()), String.valueOf(result.getTheFloatOne()));//19
        //20
        for (int i = 0; i < tpMagic.getTheFloats().length; i++) {
            assertEquals(String.valueOf(tpMagic.getTheFloats()[i]), String.valueOf(result.getTheFloats()[i]));
        }
        assertNull(result.getTheHMap());//21
        //22
        assertEquals(4, result.getTheHSet().size());
        Boolean theHsetFlag1 = false;
        Boolean theHsetFlag2 = false;
        Boolean theHsetFlag3 = false;
        Boolean theHsetFlag4 = false;
        for (Object obj : result.getTheHSet()) {
            if (obj instanceof RuleItem) {
                if (((RuleItem) obj).getRule_id().equals("rule_id_test_4_ok le man")) {
                    theHsetFlag1 = true;
                } else if (((RuleItem) obj).getRule_id().equals("rule_id_test_5_ok le man")) {
                    theHsetFlag2 = true;
                }
            } else if (obj == null) {
                theHsetFlag3 = true;
            } else if (obj.getClass() == String.class) {
                if (obj.toString().equals("str1")) {
                    theHsetFlag4 = true;
                }
            }
        }
        assertTrue(theHsetFlag1);
        assertTrue(theHsetFlag2);
        assertTrue(theHsetFlag3);
        assertTrue(theHsetFlag4);
        //23
        Boolean theHStringSetFlag1 = false;
        Boolean theHStringSetFlag2 = false;
        for (Object obj : result.getTheHStringSet()) {
            if (obj.toString().equals("a13")) {
                theHStringSetFlag1 = true;
            } else if (obj.toString().equals("ok")) {
                theHStringSetFlag2 = true;
            }
        }
        assertTrue(theHStringSetFlag1);
        assertTrue(theHStringSetFlag2);
        //24
        for (int i = 0; i < tpMagic.getTheIList().size(); i++) {
            assertEquals(tpMagic.getTheIList().get(i), result.getTheIList().get(i));
        }
        //25
        for (int i = 0; i < tpMagic.getTheI_Array().length; i++) {
            assertEquals(String.valueOf(tpMagic.getTheI_Array()[i]), String.valueOf(result.getTheI_Array()[i]));
        }
        //26
        for (Object obj : tpMagic.getTheLinkHMap().entrySet()) {
            assertEquals(((Map.Entry) obj).getValue(), result.getTheLinkHMap().get(((Map.Entry) obj).getKey()));
        }
        //27
        for (int i = 0; i < tpMagic.getTheList().size(); i++) {
            if (tpMagic.getTheList().get(i) instanceof byte[]) {
                byte[] array = (byte[]) tpMagic.getTheList().get(i);
                byte[] arrayActual = (byte[]) result.getTheList().get(i);
                for (int j = 0; j < array.length; j++) {
                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
                }
            } else if (tpMagic.getTheList().get(i) instanceof long[]) {
                long[] array = (long[]) tpMagic.getTheList().get(i);
                long[] arrayActual = (long[]) result.getTheList().get(i);
                for (int j = 0; j < array.length; j++) {
                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
                }
            } else if (tpMagic.getTheList().get(i) instanceof char[]) {
                char[] array = (char[]) tpMagic.getTheList().get(i);
                char[] arrayActual = (char[]) result.getTheList().get(i);
                for (int j = 0; j < array.length; j++) {
                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
                }
            } else if (tpMagic.getTheList().get(i) instanceof double[]) {
                double[] array = (double[]) tpMagic.getTheList().get(i);
                double[] arrayActual = (double[]) result.getTheList().get(i);
                for (int j = 0; j < array.length; j++) {
                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
                }
            } else if (tpMagic.getTheList().get(i) instanceof Map) {
                Map map = (Map) tpMagic.getTheList().get(i);
                Map mapActual = (Map) result.getTheList().get(i);
                for (Object obj : map.entrySet()) {
                    assertEquals(((Map.Entry) obj).getValue(), mapActual.get(((Map.Entry) obj).getKey()));
                }
            } else {
                assertEquals(String.valueOf(tpMagic.getTheList().get(i)), String.valueOf(result.getTheList().get(i)));
            }
        }
        //28
        for (int i = 0; i < tpMagic.getTheLongs().length; i++) {
            assertEquals(String.valueOf(tpMagic.getTheLongs()[i]), String.valueOf(result.getTheLongs()[i]));
        }
        //29 innerclass
        List innerList = result.findtheListInner();
        assertEquals(100, innerList.get(0));
        assertEquals(200, innerList.get(1));
        assertEquals(null, innerList.get(2));
        assertEquals(600, innerList.get(3));
        HashMap innerMap = result.findHMapInner();
        assertEquals("ok le man1", innerMap.get(1));
        assertEquals("rule_id_test", ((RuleItem) innerMap.get(2)).getRule_id());

        //30
        assertEquals(String.valueOf(10), String.valueOf(result.getTheObjs()[0]));
        assertEquals("abc", result.getTheObjs()[1]);
        ArrayList tmpList_objectItem_result = (ArrayList) result.getTheObjs()[2];
        assertEquals(123, tmpList_objectItem_result.get(0));
        assertEquals("abc", tmpList_objectItem_result.get(1));
        ArrayList tmpList2_result = (ArrayList) tmpList_objectItem_result.get(2);
        assertEquals(1234, tmpList2_result.get(0));
        assertEquals("str1", tmpList2_result.get(1));
        assertEquals(Difficulty.HARD, result.getTheObjs()[3]);
        assertEquals("22.022", result.getTheObjs()[4].toString());

        //31
        assertEquals(LinkedList.class, result.getTheRuleItemLinkedList().getClass());

        assertEquals("rule_id_test", ((RuleItem) result.getTheRuleItemLinkedList().get(0)).getRule_id());

        assertEquals("s1", ((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(0));
        assertEquals(100, ((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(1));
        assertNull(((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(2));
        assertEquals("rule_id_test_3_ok le man", ((RuleItem) (((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(3))).getRule_id());

        //32
        assertEquals("rule_id_test", ((RuleItem) result.getTheRuleItemList().get(0)).getRule_id());

        //33
        assertEquals("abc1", ((ArrayList<String>) result.getTheSList()).get(0));
        assertEquals("abc2", ((ArrayList<String>) result.getTheSList()).get(1));

        //34
        assertEquals(100, ((short[]) result.getTheShort())[0]);
        assertEquals(200, ((short[]) result.getTheShort())[1]);
        assertEquals(10000, ((short[]) result.getTheShort())[2]);

        assertEquals(tpMagic.getTheShortOne(), result.getTheShortOne());//35

        //36
        assertEquals(3, result.getTheTreeSet().size());
        Boolean theTreeSetFlag1 = false;
        Boolean theTreeSetFlag2 = false;
        Boolean theTreeSetFlag3 = false;
        for (Object obj : result.getTheTreeSet()) {
            if (obj.toString().equals("tree 0")) {
                theTreeSetFlag1 = true;
            } else if (obj.toString().equals("tree 1")) {
                theTreeSetFlag2 = true;
            } else if (obj.toString().equals("tree 2")) {
                theTreeSetFlag3 = true;
            }
        }
        assertTrue(theTreeSetFlag1);
        assertTrue(theTreeSetFlag2);
        assertTrue(theTreeSetFlag3);

        assertTrue(result.isTheb2());//37

        assertEquals(String.valueOf(100), String.valueOf(result.getThebyte_b()));//38

        //39
        assertEquals(byte[].class, result.getThebytes().getClass());
        assertEquals(String.valueOf(0), String.valueOf(result.getThebytes()[0]));
        assertEquals(String.valueOf(0), String.valueOf(result.getThebytes()[1]));
        assertEquals(String.valueOf(10), String.valueOf(result.getThebytes()[10]));
        assertEquals(String.valueOf(20), String.valueOf(result.getThebytes()[20]));

    }

    //最复杂的javaBean2---asm
    @Test
    public void testTheMostComplicatedJavaBean2() throws Exception {
        TpMagic2 tpMagic = new TpMagic2();
        tpMagic.setS1("test hahahahahaha");
        tpMagic.setD1(1001.02);
        tpMagic.setL1(new Long(1000000));
        tpMagic.setTheb2(true);
        tpMagic.setTheBn1(new Boolean(true));
        int[] tmpIA = new int[5];
        tmpIA[0] = 100;
        tmpIA[4] = 104;

        tpMagic.setTheDifficulty(Difficulty.NORMAL);

        tpMagic.setTheBigDecimal(new BigDecimal("100.021"));

        int[] tmpIB = new int[5];
        tmpIB[0] = 1000;
        tmpIB[4] = 1004;
        tpMagic.setTheI_Array(tmpIB);

        Object[] tmpObjs = new Object[5];
        tmpObjs[0] = 10;
        tmpObjs[1] = new String("abc");
        ArrayList tmpList_objectItem = new ArrayList();
        tmpList_objectItem.add(123);
        tmpList_objectItem.add("abc");
        ArrayList tmpList2 = new ArrayList();
        tmpList2.add(1234);
        tmpList2.add("str1");
        tmpList_objectItem.add(tmpList2);
        tmpObjs[2] = tmpList_objectItem;
        tmpObjs[3] = Difficulty.HARD;
        tmpObjs[4] = new BigDecimal("22.022");
        tpMagic.setTheObjs(tmpObjs);

        ArrayList tmpList = new ArrayList();
        tmpList.add(123);
        tmpList.add("abc");

        HashMap tmpListItemHMap = new HashMap();
        tmpListItemHMap.put(1, "ok le man1");
        tmpList.add(tmpListItemHMap);

        LinkedHashMap tmpLinkedHashMap = new LinkedHashMap();
        tmpLinkedHashMap.put(1, "1 value");
        tmpLinkedHashMap.put(2, "2 value");
        tmpList.add(tmpLinkedHashMap);

        ConcurrentHashMap tmpCHMap = new ConcurrentHashMap();
        tmpCHMap.put("c1", "c1_value");
        tmpCHMap.put("c2", "c2_value");
        tmpList.add(tmpCHMap);

        byte[] tmpbytes2 = new byte[100];
        tmpbytes2[0] = 10;
        tmpbytes2[10] = 100;
        tmpbytes2[20] = 120;
        tmpList.add(tmpbytes2);

        long[] tmpLongs2 = new long[10];
        tmpLongs2[0] = 100;
        tmpLongs2[1] = 1000;
        tmpLongs2[2] = 10000;
        tmpList.add(tmpLongs2);

        char tmpChar2 = 13;
        Character tmpCharacter2 = new Character((char) 14);
        tmpList.add(tmpChar2);
        tmpList.add(tmpCharacter2);

        char[] tmpChars2 = new char[5];
        tmpChars2[0] = 10;
        tmpChars2[1] = 20;
        tmpList.add(tmpChars2);

        double[] tmpDous2 = new double[6];
        tmpDous2[0] = 100;
        tmpDous2[1] = 200;
        tmpDous2[2] = 300;
        tmpList.add(tmpDous2);
        tpMagic.setTheList(tmpList);

        LinkedHashMap tmpLinkedHashMap2 = new LinkedHashMap();
        tmpLinkedHashMap2.put(1000, "1000 value");
        tmpLinkedHashMap2.put(2000, "2000 value");
        tpMagic.setTheLinkHMap(tmpLinkedHashMap2);

        ConcurrentHashMap tmpCHMap2 = new ConcurrentHashMap();
        tmpCHMap2.put("c1_2", "c1_value");
        tmpCHMap2.put("c2_2", "c2_value");
        tpMagic.setTheConHMap(tmpCHMap2);

        ConcurrentHashMap tmpCHMap3 = new ConcurrentHashMap();
        tmpCHMap3.put("c1_3", "c3_value");
        tmpCHMap3.put("c2_3", "c3_value");

        HashMap tmpHMap2 = new HashMap();
        tmpHMap2.put(1, "ok le man1");

        LinkedHashMap tmpLinkedHMap2 = new LinkedHashMap();
        tmpLinkedHMap2.put(22, "1 value");
        tmpLinkedHMap2.put(23, "2 value");
        tmpLinkedHMap2.put(24, (byte) 20);

        byte tmpbyte = 100;
        Byte tmpB = new Byte((byte) 200);
        tpMagic.setThebyte_b(tmpbyte);
        tpMagic.setTheByte_Byte(tmpB);

        byte[] tmpbytes = new byte[100];
        tmpbytes[0] = 0;
        tmpbytes[10] = 10;
        tmpbytes[20] = 20;
        tpMagic.setThebytes(tmpbytes);

        long[] tmpLongs = new long[10];
        tmpLongs[0] = 10;
        tmpLongs[1] = 100;
        tmpLongs[2] = 1000;
        tpMagic.setTheLongs(tmpLongs);

        long tmpl1 = 1000;
        tpMagic.setL_1_2(tmpl1);

        char tmpChar = 200;
        tpMagic.setTheChar(tmpChar);

        Character tmpCharacter = new Character((char) 300);
        tpMagic.setTheChar_2(tmpCharacter);

        char[] tmpChars = new char[10];
        tmpChars[1] = 20;
        tmpChars[2] = 21;
        tmpChars[8] = 22;
        tpMagic.setTheChars(tmpChars);

        double[] tmpDous = new double[6];
        tmpDous[0] = 100;
        tmpDous[1] = 200;
        tmpDous[2] = 300;
        tpMagic.setTheDous(tmpDous);

        ArrayList<Double> tmpDList = new ArrayList();
        tmpDList.add(100.20);
        tmpDList.add(null);
        tmpDList.add(200.60);
        tpMagic.setTheDList(tmpDList);

        ArrayList<Integer> tmpIList = new ArrayList();
        tmpIList.add(100);
        tmpIList.add(200);
        tpMagic.setTheIList(tmpIList);

        ArrayList<String> tmpSList = new ArrayList();
        tmpSList.add("abc1");
        tmpSList.add("abc2");
        tpMagic.setTheSList(tmpSList);

        ArrayList tmpRuleItemList = new ArrayList();
        RuleItem tmpListRuleItem = new RuleItem();
        tmpListRuleItem.setDescription("description_test");
        tmpListRuleItem.setItem_id("item_id_test");
        tmpListRuleItem.setRule_id("rule_id_test");
        tmpListRuleItem.setWarning_value("warning_value_test");
        tmpRuleItemList.add(tmpListRuleItem);

        tpMagic.setTheRuleItemList(tmpRuleItemList);

        LinkedList tmpRuleItemLinkedList = new LinkedList();
        RuleItem tmpListRuleItem2 = new RuleItem();
        tmpListRuleItem2.setDescription("description_test");
        tmpListRuleItem2.setItem_id("item_id_test");
        tmpListRuleItem2.setRule_id("rule_id_test");
        tmpListRuleItem2.setWarning_value("warning_value_test_haha");
        tmpRuleItemLinkedList.add(tmpListRuleItem2);

        LinkedList tmpRuleItemLinkedList1 = new LinkedList();
        tmpRuleItemLinkedList1.add("s1");
        tmpRuleItemLinkedList1.add(100);
        tmpRuleItemLinkedList1.add(null);

        RuleItem tmpListRuleItem3 = new RuleItem();
        tmpListRuleItem3.setDescription("description_test");
        tmpListRuleItem3.setItem_id("item_id_test");
        tmpListRuleItem3.setRule_id("rule_id_test_3_ok le man");
        tmpListRuleItem3.setWarning_value("warning_value_test_haha");
        tmpRuleItemLinkedList1.add(tmpListRuleItem3);

        tmpRuleItemLinkedList.add(tmpRuleItemLinkedList1);

        tpMagic.setTheRuleItemLinkedList(tmpRuleItemLinkedList);

        HashSet tmpHashSet = new HashSet();

        RuleItem tmpListRuleItem4 = new RuleItem();
        tmpListRuleItem4.setDescription("description_test");
        tmpListRuleItem4.setItem_id("item_id_test");
        tmpListRuleItem4.setRule_id("rule_id_test_4_ok le man");
        tmpHashSet.add(tmpListRuleItem4);

        tmpHashSet.add(null);
        tmpHashSet.add("str1");

        RuleItem tmpListRuleItem5 = new RuleItem();
        tmpListRuleItem5.setDescription("description_test");
        tmpListRuleItem5.setItem_id("item_id_test");
        tmpListRuleItem5.setRule_id("rule_id_test_5_ok le man");
        tmpHashSet.add(tmpListRuleItem5);

        tpMagic.setTheHSet(tmpHashSet);

        TreeSet tmpTreeSet = new TreeSet();
        tmpTreeSet.add("tree 0");
        tmpTreeSet.add("tree 1");
        tmpTreeSet.add("tree 2");
        tpMagic.setTheTreeSet(tmpTreeSet);

        HashSet<String> tmpStringHashSet = new HashSet();
        tmpStringHashSet.add("ok");
        tmpStringHashSet.add("a13");
        tpMagic.setTheHStringSet(tmpStringHashSet);

        short[] tmpShort = new short[6];
        tmpShort[0] = 100;
        tmpShort[1] = 200;
        tmpShort[2] = 10000;
        tpMagic.setTheShort(tmpShort);

        float[] tmpFloats = new float[6];
        tmpFloats[0] = new Float("100.01");
        tmpFloats[1] = 200;
        tmpFloats[2] = 10000;
        tpMagic.setTheFloats(tmpFloats);

        boolean[] tmpBooleans = new boolean[5];
        tmpBooleans[0] = true;
        tmpBooleans[1] = false;
        tmpBooleans[2] = true;
        tpMagic.setTheBooleans(tmpBooleans);

        short tmpTheShort = new Short("-32768").shortValue();
        tpMagic.setTheShortOne(tmpTheShort);

        float tmpFloat = new Float("1.4E-45").floatValue();
        tpMagic.setTheFloatOne(tmpFloat);

        tpMagic.setTheBigLong(10101010011L);
//        byte[] bytes = executeSerialization(tpMagic);
//        TpMagic obj = (TpMagic)executeDeserialization(bytes);

        TpMagic2 result = executeBackAndForth(tpMagic, TpMagic2.class);

        //come on!40个属性!
        assertEquals(tpMagic.getD1(), result.getD1());//0
        assertEquals(tpMagic.getD2(), result.getD2());//1
        assertEquals(tpMagic.getI1(), result.getI1());//2
        assertEquals(tpMagic.getL1(), result.getL1());//3
        assertEquals(tpMagic.getL_1_2(), result.getL_1_2());//4
        assertEquals(tpMagic.getS1(), result.getS1());//5
        assertEquals(tpMagic.getTheBigDecimal(), result.getTheBigDecimal());//6
        assertEquals(tpMagic.getTheBigLong(), result.getTheBigLong());//7
        assertEquals(tpMagic.getTheBn1(), result.getTheBn1());//8
        assertEquals(tpMagic.getTheBn2(), result.getTheBn2());//9
        //10
        for (int i = 0; i < tpMagic.getTheBooleans().length; i++) {
            assertEquals(tpMagic.getTheBooleans()[i], result.getTheBooleans()[i]);
        }
        assertEquals(String.valueOf(tpMagic.getTheByte_Byte()), String.valueOf(result.getTheByte_Byte()));//11
        assertEquals(tpMagic.getTheChar(), result.getTheChar());//12
        assertEquals(tpMagic.getTheChar_2(), result.getTheChar_2());//13
        //14
        for (int i = 0; i < tpMagic.getTheChars().length; i++) {
            assertEquals(tpMagic.getTheChars()[i], result.getTheChars()[i]);
        }
        //15
        for (Object obj : tpMagic.getTheConHMap().entrySet()) {
            assertEquals(((Map.Entry) obj).getValue(), result.getTheConHMap().get(((Map.Entry) obj).getKey()));
        }
        //16
        for (int i = 0; i < tpMagic.getTheDList().size(); i++) {
            assertEquals(tpMagic.getTheDList().get(i), result.getTheDList().get(i));
        }
        assertEquals(tpMagic.getTheDifficulty(), result.getTheDifficulty());//17
        //18
        for (int i = 0; i < tpMagic.getTheDous().length; i++) {
            assertEquals(String.valueOf(tpMagic.getTheDous()[i]), String.valueOf(result.getTheDous()[i]));
        }
        assertEquals(String.valueOf(tpMagic.getTheFloatOne()), String.valueOf(result.getTheFloatOne()));//19
        //20
        for (int i = 0; i < tpMagic.getTheFloats().length; i++) {
            assertEquals(String.valueOf(tpMagic.getTheFloats()[i]), String.valueOf(result.getTheFloats()[i]));
        }
        assertNull(result.getTheHMap());//21
        //22
        assertEquals(4, result.getTheHSet().size());
        Boolean theHsetFlag1 = false;
        Boolean theHsetFlag2 = false;
        Boolean theHsetFlag3 = false;
        Boolean theHsetFlag4 = false;
        for (Object obj : result.getTheHSet()) {
            if (obj instanceof RuleItem) {
                if (((RuleItem) obj).getRule_id().equals("rule_id_test_4_ok le man")) {
                    theHsetFlag1 = true;
                } else if (((RuleItem) obj).getRule_id().equals("rule_id_test_5_ok le man")) {
                    theHsetFlag2 = true;
                }
            } else if (obj == null) {
                theHsetFlag3 = true;
            } else if (obj.getClass() == String.class) {
                if (obj.toString().equals("str1")) {
                    theHsetFlag4 = true;
                }
            }
        }
        assertTrue(theHsetFlag1);
        assertTrue(theHsetFlag2);
        assertTrue(theHsetFlag3);
        assertTrue(theHsetFlag4);
        //23
        Boolean theHStringSetFlag1 = false;
        Boolean theHStringSetFlag2 = false;
        for (Object obj : result.getTheHStringSet()) {
            if (obj.toString().equals("a13")) {
                theHStringSetFlag1 = true;
            } else if (obj.toString().equals("ok")) {
                theHStringSetFlag2 = true;
            }
        }
        assertTrue(theHStringSetFlag1);
        assertTrue(theHStringSetFlag2);
        //24
        for (int i = 0; i < tpMagic.getTheIList().size(); i++) {
            assertEquals(tpMagic.getTheIList().get(i), result.getTheIList().get(i));
        }
        //25
        for (int i = 0; i < tpMagic.getTheI_Array().length; i++) {
            assertEquals(String.valueOf(tpMagic.getTheI_Array()[i]), String.valueOf(result.getTheI_Array()[i]));
        }
        //26
        for (Object obj : tpMagic.getTheLinkHMap().entrySet()) {
            assertEquals(((Map.Entry) obj).getValue(), result.getTheLinkHMap().get(((Map.Entry) obj).getKey()));
        }
        //27
        for (int i = 0; i < tpMagic.getTheList().size(); i++) {
            if (tpMagic.getTheList().get(i) instanceof byte[]) {
                byte[] array = (byte[]) tpMagic.getTheList().get(i);
                byte[] arrayActual = (byte[]) result.getTheList().get(i);
                for (int j = 0; j < array.length; j++) {
                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
                }
            } else if (tpMagic.getTheList().get(i) instanceof long[]) {
                long[] array = (long[]) tpMagic.getTheList().get(i);
                long[] arrayActual = (long[]) result.getTheList().get(i);
                for (int j = 0; j < array.length; j++) {
                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
                }
            } else if (tpMagic.getTheList().get(i) instanceof char[]) {
                char[] array = (char[]) tpMagic.getTheList().get(i);
                char[] arrayActual = (char[]) result.getTheList().get(i);
                for (int j = 0; j < array.length; j++) {
                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
                }
            } else if (tpMagic.getTheList().get(i) instanceof double[]) {
                double[] array = (double[]) tpMagic.getTheList().get(i);
                double[] arrayActual = (double[]) result.getTheList().get(i);
                for (int j = 0; j < array.length; j++) {
                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
                }
            } else if (tpMagic.getTheList().get(i) instanceof Map) {
                Map map = (Map) tpMagic.getTheList().get(i);
                Map mapActual = (Map) result.getTheList().get(i);
                for (Object obj : map.entrySet()) {
                    assertEquals(((Map.Entry) obj).getValue(), mapActual.get(((Map.Entry) obj).getKey()));
                }
            } else {
                assertEquals(String.valueOf(tpMagic.getTheList().get(i)), String.valueOf(result.getTheList().get(i)));
            }
        }
        //28
        for (int i = 0; i < tpMagic.getTheLongs().length; i++) {
            assertEquals(String.valueOf(tpMagic.getTheLongs()[i]), String.valueOf(result.getTheLongs()[i]));
        }

        //30
        assertEquals(String.valueOf(10), String.valueOf(result.getTheObjs()[0]));
        assertEquals("abc", result.getTheObjs()[1]);
        ArrayList tmpList_objectItem_result = (ArrayList) result.getTheObjs()[2];
        assertEquals(123, tmpList_objectItem_result.get(0));
        assertEquals("abc", tmpList_objectItem_result.get(1));
        ArrayList tmpList2_result = (ArrayList) tmpList_objectItem_result.get(2);
        assertEquals(1234, tmpList2_result.get(0));
        assertEquals("str1", tmpList2_result.get(1));
        assertEquals(Difficulty.HARD, result.getTheObjs()[3]);
        assertEquals("22.022", result.getTheObjs()[4].toString());

        //31
        assertEquals(LinkedList.class, result.getTheRuleItemLinkedList().getClass());

        assertEquals("rule_id_test", ((RuleItem) result.getTheRuleItemLinkedList().get(0)).getRule_id());

        assertEquals("s1", ((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(0));
        assertEquals(100, ((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(1));
        assertNull(((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(2));
        assertEquals("rule_id_test_3_ok le man", ((RuleItem) (((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(3))).getRule_id());

        //32
        assertEquals("rule_id_test", ((RuleItem) result.getTheRuleItemList().get(0)).getRule_id());

        //33
        assertEquals("abc1", ((ArrayList<String>) result.getTheSList()).get(0));
        assertEquals("abc2", ((ArrayList<String>) result.getTheSList()).get(1));

        //34
        assertEquals(100, ((short[]) result.getTheShort())[0]);
        assertEquals(200, ((short[]) result.getTheShort())[1]);
        assertEquals(10000, ((short[]) result.getTheShort())[2]);

        assertEquals(tpMagic.getTheShortOne(), result.getTheShortOne());//35

        //36
        assertEquals(3, result.getTheTreeSet().size());
        Boolean theTreeSetFlag1 = false;
        Boolean theTreeSetFlag2 = false;
        Boolean theTreeSetFlag3 = false;
        for (Object obj : result.getTheTreeSet()) {
            if (obj.toString().equals("tree 0")) {
                theTreeSetFlag1 = true;
            } else if (obj.toString().equals("tree 1")) {
                theTreeSetFlag2 = true;
            } else if (obj.toString().equals("tree 2")) {
                theTreeSetFlag3 = true;
            }
        }
        assertTrue(theTreeSetFlag1);
        assertTrue(theTreeSetFlag2);
        assertTrue(theTreeSetFlag3);

        assertTrue(result.isTheb2());//37

        assertEquals(String.valueOf(100), String.valueOf(result.getThebyte_b()));//38

        //39
        assertEquals(byte[].class, result.getThebytes().getClass());
        assertEquals(String.valueOf(0), String.valueOf(result.getThebytes()[0]));
        assertEquals(String.valueOf(0), String.valueOf(result.getThebytes()[1]));
        assertEquals(String.valueOf(10), String.valueOf(result.getThebytes()[10]));
        assertEquals(String.valueOf(20), String.valueOf(result.getThebytes()[20]));
    }

    //asm中的arrays.arraylist 带泛型+不带泛型
    @Test
    public void testArrayListInArrays() throws Exception {
        User15 u = new User15();
        Person1 p = new Person1();
        p.setId(1L);
        List list = Arrays.asList("1", 1, p);
        u.setList(list);

        Person1 p2 = new Person1();
        p2.setId(2L);
        Person1 p3 = new Person1();
        p3.setId(3L);
        List<Person1> list2 = Arrays.asList(p2, p3);
        u.setList1(list2);

        User15 result = executeBackAndForth(u, User15.class);
        assertEquals(3, result.getList().size());
        assertEquals("1", result.getList().get(0));
        assertEquals(1, result.getList().get(1));
        assertEquals("1", ((Person1) result.getList().get(2)).getId().toString());

        assertEquals("2", (result.getList1().get(0)).getId().toString());
        assertEquals("3", (result.getList1().get(1)).getId().toString());

    }

    //测试序列化Exception
    @Test
    public void testException() throws Exception {
        User16 u = new User16();
        u.setException(new RuntimeException("123123"));
        User16 result = executeBackAndForth(u, User16.class);
        assertEquals("123123", result.getException().getMessage());
    }

    //List里面套Map
    @Test
    public void testMapInList() throws Exception{
        User18 u = new User18();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("1", "abc");
        map1.put("2", "cde");
        list.add(map1);
        u.setList(list);

        Set<Map<String, String>> set = new HashSet<Map<String, String>>();
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("1", "abc");
        map2.put("2", "cde");
        set.add(map2);
        u.setSet(set);

        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        Map<String, String> map3 = new HashMap<String, String>();
        map3.put("1", "abc");
        map3.put("2", "cde");
        map.put("www", map3);
        u.setMap(map);

        User18 result = executeBackAndForth(u,User18.class);
        assertEquals("abc", result.getList().get(0).get("1"));
        assertEquals("cde", result.getList().get(0).get("2"));

        assertEquals("abc", result.getSet().iterator().next().get("1"));
        assertEquals("cde", result.getSet().iterator().next().get("2"));

        assertEquals("abc", result.getMap().get("www").get("1"));
        assertEquals("cde", result.getMap().get("www").get("2"));
    }

    @Test
    public void testListSize0() throws Exception{
        School school = new School();
        school.setName("123");
        school.setStudents(new ArrayList<Student>());
        School result = executeBackAndForth(school, School.class);
        assertEquals(0, result.getStudents().size());
    }


    //测试是否传入类名的相关测试
    //使用默认方式，写入类名
    @Test
    public void testWriteClassName1() throws Exception{
        User1 user = new User1();
        user.setB(true);

        User1 result = (User1)PB.parsePBBytes(PB.toPBBytes(user));
        assertEquals(true, result.getB());
    }


    //使用默认方式，写入类名, 但是反序列化时传入Class参数（这种方式下，默认使用被写入的类名进行反序列化）
    @Test
    public void testWriteClassName2() throws Exception{
        User1 user = new User1();
        user.setB(true);
//
        User1 result = PB.parsePBBytes(PB.toPBBytes(user), User1.class);
        assertEquals(true, result.getB());
    }

    //序列化时明确表明，不写入类名, 但是反序列化时不传入Class参数（这种方式下，返回null）
    @Test
    public void testWriteClassName3() throws Exception{
        User1 user = new User1();
        user.setB(true);

        Parameters p = new Parameters();
        p.setNeedWriteClassName(false);


        User1 result = (User1)PB.parsePBBytes(PB.toPBBytes(user, p));
        assertNull(result);
    }


    //序列化时明确表明，不写入类名, 但是反序列化时传入Class参数（这种方式下，正常）
    @Test
    public void testWriteClassName4() throws Exception{
        User1 user = new User1();
        user.setB(true);

        Parameters p = new Parameters();
        p.setNeedWriteClassName(false);


        User1 result = PB.parsePBBytes(PB.toPBBytes(user, p), User1.class);
        assertEquals(true, result.getB());
    }

    //序列化时明确表明，写入类名, 但是反序列化时仍然传入Class参数（这种方式下，正常）
    @Test
    public void testWriteClassName5() throws Exception{
        User1 user = new User1();
        user.setB(true);

        Parameters p = new Parameters();
        p.setNeedWriteClassName(true);


        User1 result = PB.parsePBBytes(PB.toPBBytes(user, p), User1.class);
        assertEquals(true, result.getB());
    }

    //序列化时明确表明，写入类名, 但是反序列化时不传Class参数（这种方式下，正常）
    @Test
    public void testWriteClassName6() throws Exception{
        User1 user = new User1();
        user.setB(true);

        Parameters p = new Parameters();
        p.setNeedWriteClassName(true);


        User1 result = (User1)PB.parsePBBytes(PB.toPBBytes(user, p));
        assertEquals(true, result.getB());

    }


    @Test
    public void testTest(){
        MemoryRecord mr = new MemoryRecord();
        mr.setPin("05liusp");
        mr.setLevel(61);
        mr.setRegTime("2012-04-01");
        mr.setFirstWareId(1003480569);
        mr.setFirstWare("NIKE 耐克 LIUNAR");
        mr.setPs1Id(652);
        mr.setPs1Name("手机数码");
        mr.setPs1WareNum(8);
        mr.setPs2Id(1315);
        mr.setPs2Name("服饰鞋帽");
        mr.setPs2WareNum(1);
        mr.setPs3Id(0);
        mr.setPs3Name("NULL");
        mr.setPs3WareNum(0);
        mr.setPs4Id(0);
        mr.setPs4Name("哈哈你好");
        mr.setPs3WareNum(123213213);
        mr.setPs5Id(0);
        mr.setPs5Name("我还了");
        mr.setPs5WareNum(123214125);
        mr.setAllPsWareNum(123123);
        mr.setCommentNum(123);
        mr.setShoworderNum(123);
        mr.setHelp4Others(1232);
        mr.setOrderTotalNum(125125125);
        mr.setAllAmount(0.0D);
        mr.setAllWareType(123);
        mr.setConsumerRanking(2.34);
        mr.setDiscountAmount(18.4D);
        mr.setClosestFourCreated("2012-04-01 23:23:23");
        byte[] bytes = PB.toPBBytes(mr);
        MemoryRecord result = (MemoryRecord)PB.parsePBBytes(bytes);
        System.out.println(result);
    }

    @Test
    public void testForSeo(){
        List<SeoWord> list = new ArrayList<SeoWord>();
        SeoWord seoWord = new SeoWord();
        seoWord.setGrabDate(new Date());
        seoWord.setId(1L);
//        seoWord.setRanking("123123cxv");
        seoWord.setSearchNumber(12312L);
        seoWord.setUrl("sfwwwdbaidudcom");
        seoWord.setChange(1);
//        seoWord.setChangeDesc("123123123123");
//        seoWord.setGrabDate(new Date());
//        seoWord.setId(123123132L);
//        seoWord.setLimit(1);
//        seoWord.setOperateTime(new Date());
//        seoWord.setPerson("12");
//        seoWord.setStart(1123);
//        seoWord.setSearchTime("123123");
//        seoWord.setWordConfigId(123123L);
//        seoWord.setWordName("123123");
//        list.add(seoWord);

//        byte[] bytes = PB.toPBBytes(list);
//        List<SeoWord> result = (List)PB.parsePBBytes(bytes);
        byte[] bytes = PB.toPBBytes(seoWord);
        SeoWord result = (SeoWord)PB.parsePBBytes(bytes);
        System.out.println(result.toString());

    }

    @Test
    public void testForMr(){
        MemoryRecord mr = new MemoryRecord();
        mr.setPin("13724379985");
        mr.setLevel(56);
        mr.setRegTime("2012-04-01 13:39:00");
        mr.setFirstWareId(136363);
        mr.setFirstWare("金士顿（Kingston） 8G class4 TF (micro SD) 储存卡 （SDC4/8GBSP）");
        mr.setPs1Id(652);
        mr.setPs1Name("手机数码");
        mr.setPs1WareNum(3);
        mr.setPs2Id(1315);
        mr.setPs2Name("服饰鞋帽");
        mr.setPs2WareNum(2);
        mr.setPs3Id(0);
        mr.setPs3Name("NULL");
        mr.setPs3WareNum(0);
        mr.setPs4Id(0);
        mr.setPs4Name("NULL");
        mr.setPs3WareNum(123213213);
        mr.setPs5Id(0);
        mr.setPs5Name("NULL");
        mr.setPs5WareNum(0);
        mr.setAllPsWareNum(5);
        mr.setCommentNum(0);
        mr.setShoworderNum(0);
        mr.setHelp4Others(0);
        mr.setOrderTotalNum(4);
        mr.setAllAmount(1893.0D);
        mr.setAllWareType(5);
        mr.setConsumerRanking(0.7091D);
        mr.setDiscountAmount(508.0D);
        mr.setClosestFourCreated("0000-00-00 00:00:00");
        byte[] bytes = PB.toPBBytes(mr);
        MemoryRecord result = (MemoryRecord)PB.parsePBBytes(bytes);
        System.out.println(result);
    }

    @Test
    public void testForWhale(){
        Set<Broker> treeSet = new TreeSet<Broker>();
        Broker b = new Broker();
        b.setId(1);
        b.setIp("129.213.32.12");
        b.setMaster(true);
        b.setPort(123);

        treeSet.add(b);
        CommonResponse commonResponse = CommonResponse.successResponse(treeSet);

        byte[] bytes = PB.toPBBytes(commonResponse);
        CommonResponse result = (CommonResponse)PB.parsePBBytes(bytes);

        System.out.println(123);
    }

}
