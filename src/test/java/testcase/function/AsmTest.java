package testcase.function;

import org.junit.Test;
import testcase.TestBase;
import userJavabean.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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


}
