package testcase.function;

import org.junit.Test;
import testcase.TestBase;
import userJavabean.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReflectTest extends TestBase {

    //只有一个属性boolean的javabean
    @Test
    public void testUserJavaBean1() throws Exception {
        User7 u7 = new User7();
        u7.putInnerBoolean(true);
        User7 result = executeBackAndForth(u7, User7.class);
        assertEquals(true, result.findInnerBoolean());
    }

    //只有一个属性Boolean的javabean
    @Test
    public void testUserJavaBean2() throws Exception {
        User8 u = new User8();
        u.putInnerBoolean(true);
        User8 result = executeBackAndForth(u, User8.class);
        assertEquals(true, result.findInnerBoolean());
    }

    //测试User9的inner中的属性是User1的情况，user1走asm
    @Test
    public void testUserJavaBean3() throws Exception {
        User1 u1 = new User1();
        u1.setB(true);

        User9 u9 = new User9();
        u9.putInnerBean(u1);
        User9 result = executeBackAndForth(u9, User9.class);
        assertEquals(true, result.findInnerUser1().getB());
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
        User10 result = executeBackAndForth(u10, User10.class);
        assertEquals(true, result.findInnerArray()[0].getB());
        assertNull(result.findInnerArray()[1]);
        assertEquals(false, result.findInnerArray()[2].getB());
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
        User11 result = executeBackAndForth(u_11, User11.class);
        assertEquals(true, ((User1) (result.findInnerArray()[0])).getB());
        assertEquals(false, ((User1) (result.findInnerArray()[1])).getB());
        assertNull(result.findInnerArray()[2]);
        assertEquals(true, ((User2) (result.findInnerArray()[3])).getB());
    }

    //内部类里放各种类型的map，走反射
    @Test
    public void testUserJavaBean6() throws Exception {
        User12 u = new User12();
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

        u.putHashMap(hashMap);
        u.putMap(map);
        u.putGenericMap(map1);

        User12 result = executeBackAndForth(u, User12.class);
        assertEquals(true, ((User1) result.findMap().get(1)).getB());
        assertEquals(false, ((User1) result.findMap().get(2)).getB());
        assertEquals("123", result.findMap().get(3));
        assertEquals(true, ((User1) result.findHashMap().get(1)).getB());
        assertEquals(false, ((User1) result.findHashMap().get(2)).getB());
        assertEquals("123", result.findHashMap().get(3));
        assertEquals(true, (result.findGenericMap().get(1)).getB());
        assertEquals(false, (result.findGenericMap().get(2)).getB());
    }


    //内部类里放各种类型的list，走反射
    @Test
    public void testUserJavaBean7() throws Exception {
        User14 u = new User14();
        User1 u1 = new User1();
        u1.setB(true);
        User1 u2 = new User1();
        u2.setB(false);
        List<User1> list = new ArrayList<User1>();
        list.add(u1);
        list.add(null);
        list.add(u2);

        LinkedList linkedList = new LinkedList();
        linkedList.add(u1);
        linkedList.add(null);
        linkedList.add(u2);

        u.putLinkedList(linkedList);
        u.putList(list);
        User14 result = executeBackAndForth(u, User14.class);
        assertEquals(true, result.findList().get(0).getB());
        assertNull(result.findList().get(1));
        assertEquals(false, result.findList().get(2).getB());
        assertEquals(true, ((User1) result.findLinkedList().get(0)).getB());
        assertNull(result.findList().get(1));
        assertEquals(false, ((User1) result.findLinkedList().get(2)).getB());
    }

    //测试Arrays.ArrayList 无泛型 + 有泛型
    @Test
    public void testArrayListInArrays() throws Exception {
        User14 user14 = new User14();
        Person1 p = new Person1();
        p.setId(1L);
        List list = Arrays.asList("1", 1, p);
        user14.putList2(list);

        Person1 p2 = new Person1();
        p2.setId(2L);
        Person1 p3 = new Person1();
        p3.setId(3L);
        List<Person1> list2 = Arrays.asList(p2, p3);
        user14.putList3(list2);

        User14 result = executeBackAndForth(user14, User14.class);
        assertEquals(3, result.findList2().size());
        assertEquals("1", result.findList2().get(0));
        assertEquals(1, result.findList2().get(1));
        assertEquals("1", ((Person1) result.findList2().get(2)).getId().toString());
        assertEquals("2", (result.findList3().get(0)).getId().toString());
        assertEquals("3", (result.findList3().get(1)).getId().toString());
    }

    //测试序列化Exception
    @Test
    public void testException() throws Exception {
        User17 u = new User17();
        u.putException(new RuntimeException("123123"));
        User17 result = executeBackAndForth(u, User17.class);
        assertEquals("123123", result.findException().getMessage());
    }
}
