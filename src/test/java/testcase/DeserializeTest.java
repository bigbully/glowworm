package testcase;

import org.junit.Test;

public class DeserializeTest {








    //无泛型或泛型为Object的集合，如何循环引用
    @Test
    public void testJavaBeanLoopWithCollection() throws Exception{
//        Person11 p1 = new Person11();
//        p1.setInteger(1);
//        Person11 p2 = new Person11();
//        p2.setInteger(2);
//
//        Map map1 = new LinkedHashMap();
//        map1.put(1, p1);
//        map1.put(p2, 2);
//
//        Map<Object, Object> map2 = new LinkedHashMap<Object, Object>();
//        map2.put(1, p1);
//        map2.put(p2, 2);
//
//        p1.setMap1(map1);
//        p1.setMap2(map2);
//        p1.setP(p2);
//
//        Person11 result = executeBackAndForth(p1, Person11.class);
//        assertEquals(p1.getInteger(), result.getInteger());
//        assertEquals(p2.getInteger(), result.getP().getInteger());
//        assertEquals(result, result.getMap1().get(1));
//        assertEquals(2, result.getMap1().get(result.getP()));
//        assertEquals(result, result.getMap2().get(1));
//        assertEquals(2, result.getMap2().get(result.getP()));
    }


    @Test
    public void testAtomicBean1() throws Exception {
//        AtomicPerson1 ap1 = new AtomicPerson1();
//        AtomicInteger ai = new AtomicInteger(Integer.MIN_VALUE);
//        ap1.setAi(ai);
//        AtomicBoolean ab = new AtomicBoolean(true);
//        ap1.setAb(ab);
//        AtomicLong al = new AtomicLong(Long.MIN_VALUE);
//        ap1.setAl(al);
//        AtomicPerson1 result = executeBackAndForth(ap1, AtomicPerson1.class);
//        assertEquals(Integer.MIN_VALUE, result.getAi().intValue());
//        assertEquals(true, result.getAb().get());
//        assertEquals(Long.MIN_VALUE, result.getAl().longValue());
    }

    @Test
    public void testInnerJavaBean() throws Exception {
//        Person7 p7 = new Person7();
//        Person7 person7 = executeBackAndForth(p7, Person7.class);
//        assertNotNull(person7);
//        assertEquals(Person7.class, person7.getClass());
//        assertEquals(1, person7.findPerson9Id());
//        assertEquals("123232", person7.getYName());
//        assertEquals("123", person7.findPerson9Name());
    }

    @Test
    public void testInnerJavaBeanWithRef() throws Exception {
//        InnerBean ib1 = new InnerBean();
//        ib1.setI(1);
//
//        InnerBean ib2 = new InnerBean();
//        ib2.setI(2);
//        ib2.setIb(ib1);
//
//        ib1.setIb(ib2);
//
//        InnerBean ib = executeBackAndForth(ib1, InnerBean.class);
//
//        assertEquals(1, ib.getIb().getIb().getI());
    }


    //对象套对象,无双向关联
    @Test
    public void testComplicatedJavaBean1() throws Exception {
//        School school = new School();
//        school.setId(1L);
//        school.setName("蓝翔技校");
//
////        List<Student> list = new ArrayList<Student>();
//
//        Student s1 = new Student();
//        s1.setId(1L);
//        s1.setName("小明");
//        s1.setSalary(10001.23D);
//        s1.setSex(true);
////        s1.setSchool(school);
//
////        list.add(s1);
////        school.setStudents(list);
//
//        Student student1 = executeBackAndForth(s1, Student.class);
//        assertNotNull(student1);
//        assertEquals(Student.class, student1.getClass());
//        assertEquals(String.valueOf(1L), student1.getId().toString());
//        assertEquals("小明", student1.getName());
//        assertEquals(String.valueOf(10001.23D), student1.getSalary().toString());
//        assertEquals(true, student1.getSex());
////        assertNotNull(student1.getSchool());
////        assertEquals(String.valueOf(1L), student1.getSchool().getId().toString());
////        assertEquals("蓝翔技校", student1.getSchool().getName());

    }

    //对象套对象和对象数组
    @Test
    public void testComplicatedJavaBean2() throws Exception {
//        Person5 p = new Person5();
//        Person6 p1 = new Person6();
//        p1.setName("123");
//        Object[] person6s = {p1};
//        p.setPerson6s(person6s);
//        Person5 person5 = executeBackAndForth(p, Person5.class);
//        assertNotNull(person5);
//        assertNotNull((person5.getPerson6s())[0]);
//        assertEquals("123", ((Person6) person5.getPerson6s()[0]).getName());
    }

    @Test
    public void testComplicatedJavaBean3() throws Exception {
//        Person8 p = new Person8();
//        List<Course> list = new ArrayList<Course>();
//        list.add(Course.Chinese);
//        list.add(Course.English);
//        p.setCourses(list);
//        Person8 person8 = executeBackAndForth(p, Person8.class);
//        assertNotNull(person8.getCourses());
    }


    //对象套对象和对象数组
    @Test
    public void testComplicatedJavaBean4() throws Exception {
//        School school = new School();
//        school.setId(1L);
//        school.setName("蓝翔技校");
//
//        Student s1 = new Student();
//        s1.setId(1L);
//        s1.setName("小明");
//        s1.setSalary(10001.23D);
//        s1.setSex(true);
//        s1.setSchool(school);
//
//        Student s2 = new Student();
//        s2.setId(2L);
//        s2.setName("小红");
//        s2.setSchool(school);
//
//        Student[] classmates = {s2};
//        s1.setClassmates(classmates);
//
//        Set<Course> courses = new HashSet<Course>();
//        courses.add(Course.Chinese);
//        courses.add(Course.English);
//
//        s1.setCourses(courses);
//
//        Student xiaoming = executeBackAndForth(s1, Student.class);
//        assertNotNull(xiaoming);
//        assertEquals(Student.class, xiaoming.getClass());
//        assertEquals(String.valueOf(1L), xiaoming.getId().toString());
//        assertEquals("小明", xiaoming.getName());
//        assertNotNull(xiaoming.getClassmates()[0].getSchool());
//        assertEquals(String.valueOf(1L), xiaoming.getClassmates()[0].getSchool().getId().toString());
//        assertEquals("蓝翔技校", xiaoming.getClassmates()[0].getSchool().getName());
//
//        assertNotNull(xiaoming.getClassmates()[0]);
//        assertEquals(Student.class, xiaoming.getClassmates()[0].getClass());
//        assertEquals(String.valueOf(2L), xiaoming.getClassmates()[0].getId().toString());
//        assertEquals("小红", xiaoming.getClassmates()[0].getName());
//        assertEquals(String.valueOf(10001.23D), xiaoming.getSalary().toString());
//        assertEquals(true, xiaoming.getSex());
//        assertNotNull(xiaoming.getSchool());
//        assertEquals(String.valueOf(1L), xiaoming.getSchool().getId().toString());
//        assertEquals("蓝翔技校", xiaoming.getSchool().getName());
//
//        assertFalse(xiaoming.getCourses().isEmpty());
//        for (Course course : xiaoming.getCourses()) {
//            assertEquals(Course.class, course.getClass());
//        }
    }





    //private ConcurrentHashMap theConHMap------javabean
    @Test
    public void testSupplyMap2() throws Exception {
//        Supply2 s2 = new Supply2();
//        ConcurrentHashMap map1 = new ConcurrentHashMap();
//        map1.put("c1_2", "c1_value");
//        map1.put("c2_2", "c2_value");
//        map1.put(1, s2);
//
//        s2.setTheConHMap(map1);
//        Supply2 result = executeBackAndForth(s2, Supply2.class);
//
//        for (Object obj : s2.getTheConHMap().entrySet()) {
//            if (((Map.Entry) obj).getValue() instanceof Supply2) {
//                Supply2 sResult = (Supply2) ((Map.Entry) obj).getValue();
//                assertNotNull(sResult.getTheConHMap());
//                assertEquals(3, sResult.getTheConHMap().entrySet().size());
//            } else {
//                assertEquals(((Map.Entry) obj).getValue(), result.getTheConHMap().get(((Map.Entry) obj).getKey()));
//            }
//        }
    }

    //private List list;
    //list 中包含 HashMap ------ ASM
    @Test
    public void testSupplyMapInList1() throws Exception {
//        Supply3 s3 = new Supply3();
//        ArrayList list = new ArrayList();
//        HashMap map = new HashMap();
//        map.put("1231", "MMMM--DD");
//        map.put("asdfksl", "123u1293");
//        list.add(map);
//        s3.setList(list);
//        Supply3 result = executeBackAndForth(s3, Supply3.class);
//        assertNotNull(result);
//        assertNotNull(result.getList());
//        assertEquals(1, result.getList().size());
//        assertEquals("MMMM--DD", ((Map) result.getList().get(0)).get("1231"));
//        assertEquals("123u1293", ((Map) result.getList().get(0)).get("asdfksl"));
    }


    //最复杂的javabean -- javabean包含inner
    @Test
    public void testTheMostComplicatedJavaBean1() throws Exception {
//        TpMagic tpMagic = new TpMagic();
//        tpMagic.setS1("test hahahahahaha");
//        tpMagic.setD1(1001.02);
//        tpMagic.setL1(new Long(1000000));
//        tpMagic.setTheb2(true);
//        tpMagic.setTheBn1(new Boolean(true));
//        int[] tmpIA = new int[5];
//        tmpIA[0] = 100;
//        tmpIA[4] = 104;
//
//        tpMagic.setTheDifficulty(Difficulty.NORMAL);
//
//        tpMagic.setTheBigDecimal(new BigDecimal("100.021"));
//
//        int[] tmpIB = new int[5];
//        tmpIB[0] = 1000;
//        tmpIB[4] = 1004;
//        tpMagic.setTheI_Array(tmpIB);
//
//        Object[] tmpObjs = new Object[5];
//        tmpObjs[0] = 10;
//        tmpObjs[1] = new String("abc");
//        ArrayList tmpList_objectItem = new ArrayList();
//        tmpList_objectItem.add(123);
//        tmpList_objectItem.add("abc");
//        ArrayList tmpList2 = new ArrayList();
//        tmpList2.add(1234);
//        tmpList2.add("str1");
//        tmpList_objectItem.add(tmpList2);
//        tmpObjs[2] = tmpList_objectItem;
//        tmpObjs[3] = Difficulty.HARD;
//        tmpObjs[4] = new BigDecimal("22.022");
//        tpMagic.setTheObjs(tmpObjs);
//
//        ArrayList tmpList = new ArrayList();
//        tmpList.add(123);
//        tmpList.add("abc");
//
//        HashMap tmpListItemHMap = new HashMap();
//        tmpListItemHMap.put(1, "ok le man1");
//        tmpList.add(tmpListItemHMap);
//
//        LinkedHashMap tmpLinkedHashMap = new LinkedHashMap();
//        tmpLinkedHashMap.put(1, "1 value");
//        tmpLinkedHashMap.put(2, "2 value");
//        tmpList.add(tmpLinkedHashMap);
//
//        ConcurrentHashMap tmpCHMap = new ConcurrentHashMap();
//        tmpCHMap.put("c1", "c1_value");
//        tmpCHMap.put("c2", "c2_value");
//        tmpList.add(tmpCHMap);
//
//        byte[] tmpbytes2 = new byte[100];
//        tmpbytes2[0] = 10;
//        tmpbytes2[10] = 100;
//        tmpbytes2[20] = 120;
//        tmpList.add(tmpbytes2);
//
//        long[] tmpLongs2 = new long[10];
//        tmpLongs2[0] = 100;
//        tmpLongs2[1] = 1000;
//        tmpLongs2[2] = 10000;
//        tmpList.add(tmpLongs2);
//
//        char tmpChar2 = 13;
//        Character tmpCharacter2 = new Character((char) 14);
//        tmpList.add(tmpChar2);
//        tmpList.add(tmpCharacter2);
//
//        char[] tmpChars2 = new char[5];
//        tmpChars2[0] = 10;
//        tmpChars2[1] = 20;
//        tmpList.add(tmpChars2);
//
//        double[] tmpDous2 = new double[6];
//        tmpDous2[0] = 100;
//        tmpDous2[1] = 200;
//        tmpDous2[2] = 300;
//        tmpList.add(tmpDous2);
//        tpMagic.setTheList(tmpList);
//
//        LinkedHashMap tmpLinkedHashMap2 = new LinkedHashMap();
//        tmpLinkedHashMap2.put(1000, "1000 value");
//        tmpLinkedHashMap2.put(2000, "2000 value");
//        tpMagic.setTheLinkHMap(tmpLinkedHashMap2);
//
//        ConcurrentHashMap tmpCHMap2 = new ConcurrentHashMap();
//        tmpCHMap2.put("c1_2", "c1_value");
//        tmpCHMap2.put("c2_2", "c2_value");
//        tpMagic.setTheConHMap(tmpCHMap2);
//
//        ConcurrentHashMap tmpCHMap3 = new ConcurrentHashMap();
//        tmpCHMap3.put("c1_3", "c3_value");
//        tmpCHMap3.put("c2_3", "c3_value");
//
//        HashMap tmpHMap2 = new HashMap();
//        tmpHMap2.put(1, "ok le man1");
//
//        LinkedHashMap tmpLinkedHMap2 = new LinkedHashMap();
//        tmpLinkedHMap2.put(22, "1 value");
//        tmpLinkedHMap2.put(23, "2 value");
//        tmpLinkedHMap2.put(24, (byte) 20);
//
//        byte tmpbyte = 100;
//        Byte tmpB = new Byte((byte) 200);
//        tpMagic.setThebyte_b(tmpbyte);
//        tpMagic.setTheByte_Byte(tmpB);
//
//        byte[] tmpbytes = new byte[100];
//        tmpbytes[0] = 0;
//        tmpbytes[10] = 10;
//        tmpbytes[20] = 20;
//        tpMagic.setThebytes(tmpbytes);
//
//        long[] tmpLongs = new long[10];
//        tmpLongs[0] = 10;
//        tmpLongs[1] = 100;
//        tmpLongs[2] = 1000;
//        tpMagic.setTheLongs(tmpLongs);
//
//        long tmpl1 = 1000;
//        tpMagic.setL_1_2(tmpl1);
//
//        char tmpChar = 200;
//        tpMagic.setTheChar(tmpChar);
//
//        Character tmpCharacter = new Character((char) 300);
//        tpMagic.setTheChar_2(tmpCharacter);
//
//        char[] tmpChars = new char[10];
//        tmpChars[1] = 20;
//        tmpChars[2] = 21;
//        tmpChars[8] = 22;
//        tpMagic.setTheChars(tmpChars);
//
//        double[] tmpDous = new double[6];
//        tmpDous[0] = 100;
//        tmpDous[1] = 200;
//        tmpDous[2] = 300;
//        tpMagic.setTheDous(tmpDous);
//
//        ArrayList<Double> tmpDList = new ArrayList();
//        tmpDList.add(100.20);
//        tmpDList.add(null);
//        tmpDList.add(200.60);
//        tpMagic.setTheDList(tmpDList);
//
//        ArrayList<Integer> tmpIList = new ArrayList();
//        tmpIList.add(100);
//        tmpIList.add(200);
//        tpMagic.setTheIList(tmpIList);
//
//        ArrayList<String> tmpSList = new ArrayList();
//        tmpSList.add("abc1");
//        tmpSList.add("abc2");
//        tpMagic.setTheSList(tmpSList);
//
//        ArrayList tmpRuleItemList = new ArrayList();
//        RuleItem tmpListRuleItem = new RuleItem();
//        tmpListRuleItem.setDescription("description_test");
//        tmpListRuleItem.setItem_id("item_id_test");
//        tmpListRuleItem.setRule_id("rule_id_test");
//        tmpListRuleItem.setWarning_value("warning_value_test");
//        tmpRuleItemList.add(tmpListRuleItem);
//
//        tpMagic.setTheRuleItemList(tmpRuleItemList);
//
//        LinkedList tmpRuleItemLinkedList = new LinkedList();
//        RuleItem tmpListRuleItem2 = new RuleItem();
//        tmpListRuleItem2.setDescription("description_test");
//        tmpListRuleItem2.setItem_id("item_id_test");
//        tmpListRuleItem2.setRule_id("rule_id_test");
//        tmpListRuleItem2.setWarning_value("warning_value_test_haha");
//        tmpRuleItemLinkedList.add(tmpListRuleItem2);
//
//        LinkedList tmpRuleItemLinkedList1 = new LinkedList();
//        tmpRuleItemLinkedList1.add("s1");
//        tmpRuleItemLinkedList1.add(100);
//        tmpRuleItemLinkedList1.add(null);
//
//        RuleItem tmpListRuleItem3 = new RuleItem();
//        tmpListRuleItem3.setDescription("description_test");
//        tmpListRuleItem3.setItem_id("item_id_test");
//        tmpListRuleItem3.setRule_id("rule_id_test_3_ok le man");
//        tmpListRuleItem3.setWarning_value("warning_value_test_haha");
//        tmpRuleItemLinkedList1.add(tmpListRuleItem3);
//
//        tmpRuleItemLinkedList.add(tmpRuleItemLinkedList1);
//
//        tpMagic.setTheRuleItemLinkedList(tmpRuleItemLinkedList);
//
//        HashSet tmpHashSet = new HashSet();
//
//        RuleItem tmpListRuleItem4 = new RuleItem();
//        tmpListRuleItem4.setDescription("description_test");
//        tmpListRuleItem4.setItem_id("item_id_test");
//        tmpListRuleItem4.setRule_id("rule_id_test_4_ok le man");
//        tmpHashSet.add(tmpListRuleItem4);
//
//        tmpHashSet.add(null);
//        tmpHashSet.add("str1");
//
//        RuleItem tmpListRuleItem5 = new RuleItem();
//        tmpListRuleItem5.setDescription("description_test");
//        tmpListRuleItem5.setItem_id("item_id_test");
//        tmpListRuleItem5.setRule_id("rule_id_test_5_ok le man");
//        tmpHashSet.add(tmpListRuleItem5);
//
//        tpMagic.setTheHSet(tmpHashSet);
//
//        TreeSet tmpTreeSet = new TreeSet();
//        tmpTreeSet.add("tree 0");
//        tmpTreeSet.add("tree 1");
//        tmpTreeSet.add("tree 2");
//        tpMagic.setTheTreeSet(tmpTreeSet);
//
//        HashSet<String> tmpStringHashSet = new HashSet();
//        tmpStringHashSet.add("ok");
//        tmpStringHashSet.add("a13");
//        tpMagic.setTheHStringSet(tmpStringHashSet);
//
//        short[] tmpShort = new short[6];
//        tmpShort[0] = 100;
//        tmpShort[1] = 200;
//        tmpShort[2] = 10000;
//        tpMagic.setTheShort(tmpShort);
//
//        float[] tmpFloats = new float[6];
//        tmpFloats[0] = new Float("100.01");
//        tmpFloats[1] = 200;
//        tmpFloats[2] = 10000;
//        tpMagic.setTheFloats(tmpFloats);
//
//        boolean[] tmpBooleans = new boolean[5];
//        tmpBooleans[0] = true;
//        tmpBooleans[1] = false;
//        tmpBooleans[2] = true;
//        tpMagic.setTheBooleans(tmpBooleans);
//
//        short tmpTheShort = new Short("-32768").shortValue();
//        tpMagic.setTheShortOne(tmpTheShort);
//
//        float tmpFloat = new Float("1.4E-45").floatValue();
//        tpMagic.setTheFloatOne(tmpFloat);
//
//        tpMagic.setTheBigLong(10101010011L);
////        byte[] bytes = executeSerialization(tpMagic);
////        TpMagic obj = (TpMagic)executeDeserialization(bytes);
//
//        TpMagic result = executeBackAndForth(tpMagic, TpMagic.class);
//
//        //come on!40个属性!
//        assertEquals(tpMagic.getD1(), result.getD1());//0
//        assertEquals(tpMagic.getD2(), result.getD2());//1
//        assertEquals(tpMagic.getI1(), result.getI1());//2
//        assertEquals(tpMagic.getL1(), result.getL1());//3
//        assertEquals(tpMagic.getL_1_2(), result.getL_1_2());//4
//        assertEquals(tpMagic.getS1(), result.getS1());//5
//        assertEquals(tpMagic.getTheBigDecimal(), result.getTheBigDecimal());//6
//        assertEquals(tpMagic.getTheBigLong(), result.getTheBigLong());//7
//        assertEquals(tpMagic.getTheBn1(), result.getTheBn1());//8
//        assertEquals(tpMagic.getTheBn2(), result.getTheBn2());//9
//        //10
//        for (int i = 0; i < tpMagic.getTheBooleans().length; i++) {
//            assertEquals(tpMagic.getTheBooleans()[i], result.getTheBooleans()[i]);
//        }
//        assertEquals(String.valueOf(tpMagic.getTheByte_Byte()), String.valueOf(result.getTheByte_Byte()));//11
//        assertEquals(tpMagic.getTheChar(), result.getTheChar());//12
//        assertEquals(tpMagic.getTheChar_2(), result.getTheChar_2());//13
//        //14
//        for (int i = 0; i < tpMagic.getTheChars().length; i++) {
//            assertEquals(tpMagic.getTheChars()[i], result.getTheChars()[i]);
//        }
//        //15
//        for (Object obj : tpMagic.getTheConHMap().entrySet()) {
//            assertEquals(((Map.Entry) obj).getValue(), result.getTheConHMap().get(((Map.Entry) obj).getKey()));
//        }
//        //16
//        for (int i = 0; i < tpMagic.getTheDList().size(); i++) {
//            assertEquals(tpMagic.getTheDList().get(i), result.getTheDList().get(i));
//        }
//        assertEquals(tpMagic.getTheDifficulty(), result.getTheDifficulty());//17
//        //18
//        for (int i = 0; i < tpMagic.getTheDous().length; i++) {
//            assertEquals(String.valueOf(tpMagic.getTheDous()[i]), String.valueOf(result.getTheDous()[i]));
//        }
//        assertEquals(String.valueOf(tpMagic.getTheFloatOne()), String.valueOf(result.getTheFloatOne()));//19
//        //20
//        for (int i = 0; i < tpMagic.getTheFloats().length; i++) {
//            assertEquals(String.valueOf(tpMagic.getTheFloats()[i]), String.valueOf(result.getTheFloats()[i]));
//        }
//        assertNull(result.getTheHMap());//21
//        //22
//        assertEquals(4, result.getTheHSet().size());
//        Boolean theHsetFlag1 = false;
//        Boolean theHsetFlag2 = false;
//        Boolean theHsetFlag3 = false;
//        Boolean theHsetFlag4 = false;
//        for (Object obj : result.getTheHSet()) {
//            if (obj instanceof RuleItem) {
//                if (((RuleItem) obj).getRule_id().equals("rule_id_test_4_ok le man")) {
//                    theHsetFlag1 = true;
//                } else if (((RuleItem) obj).getRule_id().equals("rule_id_test_5_ok le man")) {
//                    theHsetFlag2 = true;
//                }
//            } else if (obj == null) {
//                theHsetFlag3 = true;
//            } else if (obj.getClass() == String.class) {
//                if (obj.toString().equals("str1")) {
//                    theHsetFlag4 = true;
//                }
//            }
//        }
//        assertTrue(theHsetFlag1);
//        assertTrue(theHsetFlag2);
//        assertTrue(theHsetFlag3);
//        assertTrue(theHsetFlag4);
//        //23
//        Boolean theHStringSetFlag1 = false;
//        Boolean theHStringSetFlag2 = false;
//        for (Object obj : result.getTheHStringSet()) {
//            if (obj.toString().equals("a13")) {
//                theHStringSetFlag1 = true;
//            } else if (obj.toString().equals("ok")) {
//                theHStringSetFlag2 = true;
//            }
//        }
//        assertTrue(theHStringSetFlag1);
//        assertTrue(theHStringSetFlag2);
//        //24
//        for (int i = 0; i < tpMagic.getTheIList().size(); i++) {
//            assertEquals(tpMagic.getTheIList().get(i), result.getTheIList().get(i));
//        }
//        //25
//        for (int i = 0; i < tpMagic.getTheI_Array().length; i++) {
//            assertEquals(String.valueOf(tpMagic.getTheI_Array()[i]), String.valueOf(result.getTheI_Array()[i]));
//        }
//        //26
//        for (Object obj : tpMagic.getTheLinkHMap().entrySet()) {
//            assertEquals(((Map.Entry) obj).getValue(), result.getTheLinkHMap().get(((Map.Entry) obj).getKey()));
//        }
//        //27
//        for (int i = 0; i < tpMagic.getTheList().size(); i++) {
//            if (tpMagic.getTheList().get(i) instanceof byte[]) {
//                byte[] array = (byte[]) tpMagic.getTheList().get(i);
//                byte[] arrayActual = (byte[]) result.getTheList().get(i);
//                for (int j = 0; j < array.length; j++) {
//                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
//                }
//            } else if (tpMagic.getTheList().get(i) instanceof long[]) {
//                long[] array = (long[]) tpMagic.getTheList().get(i);
//                long[] arrayActual = (long[]) result.getTheList().get(i);
//                for (int j = 0; j < array.length; j++) {
//                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
//                }
//            } else if (tpMagic.getTheList().get(i) instanceof char[]) {
//                char[] array = (char[]) tpMagic.getTheList().get(i);
//                char[] arrayActual = (char[]) result.getTheList().get(i);
//                for (int j = 0; j < array.length; j++) {
//                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
//                }
//            } else if (tpMagic.getTheList().get(i) instanceof double[]) {
//                double[] array = (double[]) tpMagic.getTheList().get(i);
//                double[] arrayActual = (double[]) result.getTheList().get(i);
//                for (int j = 0; j < array.length; j++) {
//                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
//                }
//            } else if (tpMagic.getTheList().get(i) instanceof Map) {
//                Map map = (Map) tpMagic.getTheList().get(i);
//                Map mapActual = (Map) result.getTheList().get(i);
//                for (Object obj : map.entrySet()) {
//                    assertEquals(((Map.Entry) obj).getValue(), mapActual.get(((Map.Entry) obj).getKey()));
//                }
//            } else {
//                assertEquals(String.valueOf(tpMagic.getTheList().get(i)), String.valueOf(result.getTheList().get(i)));
//            }
//        }
//        //28
//        for (int i = 0; i < tpMagic.getTheLongs().length; i++) {
//            assertEquals(String.valueOf(tpMagic.getTheLongs()[i]), String.valueOf(result.getTheLongs()[i]));
//        }
//        //29 innerclass
//        List innerList = result.findtheListInner();
//        assertEquals(100, innerList.get(0));
//        assertEquals(200, innerList.get(1));
//        assertEquals(null, innerList.get(2));
//        assertEquals(600, innerList.get(3));
//        HashMap innerMap = result.findHMapInner();
//        assertEquals("ok le man1", innerMap.get(1));
//        assertEquals("rule_id_test", ((RuleItem) innerMap.get(2)).getRule_id());
//
//        //30
//        assertEquals(String.valueOf(10), String.valueOf(result.getTheObjs()[0]));
//        assertEquals("abc", result.getTheObjs()[1]);
//        ArrayList tmpList_objectItem_result = (ArrayList) result.getTheObjs()[2];
//        assertEquals(123, tmpList_objectItem_result.get(0));
//        assertEquals("abc", tmpList_objectItem_result.get(1));
//        ArrayList tmpList2_result = (ArrayList) tmpList_objectItem_result.get(2);
//        assertEquals(1234, tmpList2_result.get(0));
//        assertEquals("str1", tmpList2_result.get(1));
//        assertEquals(Difficulty.HARD, result.getTheObjs()[3]);
//        assertEquals("22.022", result.getTheObjs()[4].toString());
//
//        //31
//        assertEquals(LinkedList.class, result.getTheRuleItemLinkedList().getClass());
//
//        assertEquals("rule_id_test", ((RuleItem) result.getTheRuleItemLinkedList().get(0)).getRule_id());
//
//        assertEquals("s1", ((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(0));
//        assertEquals(100, ((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(1));
//        assertNull(((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(2));
//        assertEquals("rule_id_test_3_ok le man", ((RuleItem) (((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(3))).getRule_id());
//
//        //32
//        assertEquals("rule_id_test", ((RuleItem) result.getTheRuleItemList().get(0)).getRule_id());
//
//        //33
//        assertEquals("abc1", ((ArrayList<String>) result.getTheSList()).get(0));
//        assertEquals("abc2", ((ArrayList<String>) result.getTheSList()).get(1));
//
//        //34
//        assertEquals(100, ((short[]) result.getTheShort())[0]);
//        assertEquals(200, ((short[]) result.getTheShort())[1]);
//        assertEquals(10000, ((short[]) result.getTheShort())[2]);
//
//        assertEquals(tpMagic.getTheShortOne(), result.getTheShortOne());//35
//
//        //36
//        assertEquals(3, result.getTheTreeSet().size());
//        Boolean theTreeSetFlag1 = false;
//        Boolean theTreeSetFlag2 = false;
//        Boolean theTreeSetFlag3 = false;
//        for (Object obj : result.getTheTreeSet()) {
//            if (obj.toString().equals("tree 0")) {
//                theTreeSetFlag1 = true;
//            } else if (obj.toString().equals("tree 1")) {
//                theTreeSetFlag2 = true;
//            } else if (obj.toString().equals("tree 2")) {
//                theTreeSetFlag3 = true;
//            }
//        }
//        assertTrue(theTreeSetFlag1);
//        assertTrue(theTreeSetFlag2);
//        assertTrue(theTreeSetFlag3);
//
//        assertTrue(result.isTheb2());//37
//
//        assertEquals(String.valueOf(100), String.valueOf(result.getThebyte_b()));//38
//
//        //39
//        assertEquals(byte[].class, result.getThebytes().getClass());
//        assertEquals(String.valueOf(0), String.valueOf(result.getThebytes()[0]));
//        assertEquals(String.valueOf(0), String.valueOf(result.getThebytes()[1]));
//        assertEquals(String.valueOf(10), String.valueOf(result.getThebytes()[10]));
//        assertEquals(String.valueOf(20), String.valueOf(result.getThebytes()[20]));

    }

    //最复杂的javaBean2---asm
    @Test
    public void testTheMostComplicatedJavaBean2() throws Exception {
//        TpMagic2 tpMagic = new TpMagic2();
//        tpMagic.setS1("test hahahahahaha");
//        tpMagic.setD1(1001.02);
//        tpMagic.setL1(new Long(1000000));
//        tpMagic.setTheb2(true);
//        tpMagic.setTheBn1(new Boolean(true));
//        int[] tmpIA = new int[5];
//        tmpIA[0] = 100;
//        tmpIA[4] = 104;
//
//        tpMagic.setTheDifficulty(Difficulty.NORMAL);
//
//        tpMagic.setTheBigDecimal(new BigDecimal("100.021"));
//
//        int[] tmpIB = new int[5];
//        tmpIB[0] = 1000;
//        tmpIB[4] = 1004;
//        tpMagic.setTheI_Array(tmpIB);
//
//        Object[] tmpObjs = new Object[5];
//        tmpObjs[0] = 10;
//        tmpObjs[1] = new String("abc");
//        ArrayList tmpList_objectItem = new ArrayList();
//        tmpList_objectItem.add(123);
//        tmpList_objectItem.add("abc");
//        ArrayList tmpList2 = new ArrayList();
//        tmpList2.add(1234);
//        tmpList2.add("str1");
//        tmpList_objectItem.add(tmpList2);
//        tmpObjs[2] = tmpList_objectItem;
//        tmpObjs[3] = Difficulty.HARD;
//        tmpObjs[4] = new BigDecimal("22.022");
//        tpMagic.setTheObjs(tmpObjs);
//
//        ArrayList tmpList = new ArrayList();
//        tmpList.add(123);
//        tmpList.add("abc");
//
//        HashMap tmpListItemHMap = new HashMap();
//        tmpListItemHMap.put(1, "ok le man1");
//        tmpList.add(tmpListItemHMap);
//
//        LinkedHashMap tmpLinkedHashMap = new LinkedHashMap();
//        tmpLinkedHashMap.put(1, "1 value");
//        tmpLinkedHashMap.put(2, "2 value");
//        tmpList.add(tmpLinkedHashMap);
//
//        ConcurrentHashMap tmpCHMap = new ConcurrentHashMap();
//        tmpCHMap.put("c1", "c1_value");
//        tmpCHMap.put("c2", "c2_value");
//        tmpList.add(tmpCHMap);
//
//        byte[] tmpbytes2 = new byte[100];
//        tmpbytes2[0] = 10;
//        tmpbytes2[10] = 100;
//        tmpbytes2[20] = 120;
//        tmpList.add(tmpbytes2);
//
//        long[] tmpLongs2 = new long[10];
//        tmpLongs2[0] = 100;
//        tmpLongs2[1] = 1000;
//        tmpLongs2[2] = 10000;
//        tmpList.add(tmpLongs2);
//
//        char tmpChar2 = 13;
//        Character tmpCharacter2 = new Character((char) 14);
//        tmpList.add(tmpChar2);
//        tmpList.add(tmpCharacter2);
//
//        char[] tmpChars2 = new char[5];
//        tmpChars2[0] = 10;
//        tmpChars2[1] = 20;
//        tmpList.add(tmpChars2);
//
//        double[] tmpDous2 = new double[6];
//        tmpDous2[0] = 100;
//        tmpDous2[1] = 200;
//        tmpDous2[2] = 300;
//        tmpList.add(tmpDous2);
//        tpMagic.setTheList(tmpList);
//
//        LinkedHashMap tmpLinkedHashMap2 = new LinkedHashMap();
//        tmpLinkedHashMap2.put(1000, "1000 value");
//        tmpLinkedHashMap2.put(2000, "2000 value");
//        tpMagic.setTheLinkHMap(tmpLinkedHashMap2);
//
//        ConcurrentHashMap tmpCHMap2 = new ConcurrentHashMap();
//        tmpCHMap2.put("c1_2", "c1_value");
//        tmpCHMap2.put("c2_2", "c2_value");
//        tpMagic.setTheConHMap(tmpCHMap2);
//
//        ConcurrentHashMap tmpCHMap3 = new ConcurrentHashMap();
//        tmpCHMap3.put("c1_3", "c3_value");
//        tmpCHMap3.put("c2_3", "c3_value");
//
//        HashMap tmpHMap2 = new HashMap();
//        tmpHMap2.put(1, "ok le man1");
//
//        LinkedHashMap tmpLinkedHMap2 = new LinkedHashMap();
//        tmpLinkedHMap2.put(22, "1 value");
//        tmpLinkedHMap2.put(23, "2 value");
//        tmpLinkedHMap2.put(24, (byte) 20);
//
//        byte tmpbyte = 100;
//        Byte tmpB = new Byte((byte) 200);
//        tpMagic.setThebyte_b(tmpbyte);
//        tpMagic.setTheByte_Byte(tmpB);
//
//        byte[] tmpbytes = new byte[100];
//        tmpbytes[0] = 0;
//        tmpbytes[10] = 10;
//        tmpbytes[20] = 20;
//        tpMagic.setThebytes(tmpbytes);
//
//        long[] tmpLongs = new long[10];
//        tmpLongs[0] = 10;
//        tmpLongs[1] = 100;
//        tmpLongs[2] = 1000;
//        tpMagic.setTheLongs(tmpLongs);
//
//        long tmpl1 = 1000;
//        tpMagic.setL_1_2(tmpl1);
//
//        char tmpChar = 200;
//        tpMagic.setTheChar(tmpChar);
//
//        Character tmpCharacter = new Character((char) 300);
//        tpMagic.setTheChar_2(tmpCharacter);
//
//        char[] tmpChars = new char[10];
//        tmpChars[1] = 20;
//        tmpChars[2] = 21;
//        tmpChars[8] = 22;
//        tpMagic.setTheChars(tmpChars);
//
//        double[] tmpDous = new double[6];
//        tmpDous[0] = 100;
//        tmpDous[1] = 200;
//        tmpDous[2] = 300;
//        tpMagic.setTheDous(tmpDous);
//
//        ArrayList<Double> tmpDList = new ArrayList();
//        tmpDList.add(100.20);
//        tmpDList.add(null);
//        tmpDList.add(200.60);
//        tpMagic.setTheDList(tmpDList);
//
//        ArrayList<Integer> tmpIList = new ArrayList();
//        tmpIList.add(100);
//        tmpIList.add(200);
//        tpMagic.setTheIList(tmpIList);
//
//        ArrayList<String> tmpSList = new ArrayList();
//        tmpSList.add("abc1");
//        tmpSList.add("abc2");
//        tpMagic.setTheSList(tmpSList);
//
//        ArrayList tmpRuleItemList = new ArrayList();
//        RuleItem tmpListRuleItem = new RuleItem();
//        tmpListRuleItem.setDescription("description_test");
//        tmpListRuleItem.setItem_id("item_id_test");
//        tmpListRuleItem.setRule_id("rule_id_test");
//        tmpListRuleItem.setWarning_value("warning_value_test");
//        tmpRuleItemList.add(tmpListRuleItem);
//
//        tpMagic.setTheRuleItemList(tmpRuleItemList);
//
//        LinkedList tmpRuleItemLinkedList = new LinkedList();
//        RuleItem tmpListRuleItem2 = new RuleItem();
//        tmpListRuleItem2.setDescription("description_test");
//        tmpListRuleItem2.setItem_id("item_id_test");
//        tmpListRuleItem2.setRule_id("rule_id_test");
//        tmpListRuleItem2.setWarning_value("warning_value_test_haha");
//        tmpRuleItemLinkedList.add(tmpListRuleItem2);
//
//        LinkedList tmpRuleItemLinkedList1 = new LinkedList();
//        tmpRuleItemLinkedList1.add("s1");
//        tmpRuleItemLinkedList1.add(100);
//        tmpRuleItemLinkedList1.add(null);
//
//        RuleItem tmpListRuleItem3 = new RuleItem();
//        tmpListRuleItem3.setDescription("description_test");
//        tmpListRuleItem3.setItem_id("item_id_test");
//        tmpListRuleItem3.setRule_id("rule_id_test_3_ok le man");
//        tmpListRuleItem3.setWarning_value("warning_value_test_haha");
//        tmpRuleItemLinkedList1.add(tmpListRuleItem3);
//
//        tmpRuleItemLinkedList.add(tmpRuleItemLinkedList1);
//
//        tpMagic.setTheRuleItemLinkedList(tmpRuleItemLinkedList);
//
//        HashSet tmpHashSet = new HashSet();
//
//        RuleItem tmpListRuleItem4 = new RuleItem();
//        tmpListRuleItem4.setDescription("description_test");
//        tmpListRuleItem4.setItem_id("item_id_test");
//        tmpListRuleItem4.setRule_id("rule_id_test_4_ok le man");
//        tmpHashSet.add(tmpListRuleItem4);
//
//        tmpHashSet.add(null);
//        tmpHashSet.add("str1");
//
//        RuleItem tmpListRuleItem5 = new RuleItem();
//        tmpListRuleItem5.setDescription("description_test");
//        tmpListRuleItem5.setItem_id("item_id_test");
//        tmpListRuleItem5.setRule_id("rule_id_test_5_ok le man");
//        tmpHashSet.add(tmpListRuleItem5);
//
//        tpMagic.setTheHSet(tmpHashSet);
//
//        TreeSet tmpTreeSet = new TreeSet();
//        tmpTreeSet.add("tree 0");
//        tmpTreeSet.add("tree 1");
//        tmpTreeSet.add("tree 2");
//        tpMagic.setTheTreeSet(tmpTreeSet);
//
//        HashSet<String> tmpStringHashSet = new HashSet();
//        tmpStringHashSet.add("ok");
//        tmpStringHashSet.add("a13");
//        tpMagic.setTheHStringSet(tmpStringHashSet);
//
//        short[] tmpShort = new short[6];
//        tmpShort[0] = 100;
//        tmpShort[1] = 200;
//        tmpShort[2] = 10000;
//        tpMagic.setTheShort(tmpShort);
//
//        float[] tmpFloats = new float[6];
//        tmpFloats[0] = new Float("100.01");
//        tmpFloats[1] = 200;
//        tmpFloats[2] = 10000;
//        tpMagic.setTheFloats(tmpFloats);
//
//        boolean[] tmpBooleans = new boolean[5];
//        tmpBooleans[0] = true;
//        tmpBooleans[1] = false;
//        tmpBooleans[2] = true;
//        tpMagic.setTheBooleans(tmpBooleans);
//
//        short tmpTheShort = new Short("-32768").shortValue();
//        tpMagic.setTheShortOne(tmpTheShort);
//
//        float tmpFloat = new Float("1.4E-45").floatValue();
//        tpMagic.setTheFloatOne(tmpFloat);
//
//        tpMagic.setTheBigLong(10101010011L);
////        byte[] bytes = executeSerialization(tpMagic);
////        TpMagic obj = (TpMagic)executeDeserialization(bytes);
//
//        TpMagic2 result = executeBackAndForth(tpMagic, TpMagic2.class);
//
//        //come on!40个属性!
//        assertEquals(tpMagic.getD1(), result.getD1());//0
//        assertEquals(tpMagic.getD2(), result.getD2());//1
//        assertEquals(tpMagic.getI1(), result.getI1());//2
//        assertEquals(tpMagic.getL1(), result.getL1());//3
//        assertEquals(tpMagic.getL_1_2(), result.getL_1_2());//4
//        assertEquals(tpMagic.getS1(), result.getS1());//5
//        assertEquals(tpMagic.getTheBigDecimal(), result.getTheBigDecimal());//6
//        assertEquals(tpMagic.getTheBigLong(), result.getTheBigLong());//7
//        assertEquals(tpMagic.getTheBn1(), result.getTheBn1());//8
//        assertEquals(tpMagic.getTheBn2(), result.getTheBn2());//9
//        //10
//        for (int i = 0; i < tpMagic.getTheBooleans().length; i++) {
//            assertEquals(tpMagic.getTheBooleans()[i], result.getTheBooleans()[i]);
//        }
//        assertEquals(String.valueOf(tpMagic.getTheByte_Byte()), String.valueOf(result.getTheByte_Byte()));//11
//        assertEquals(tpMagic.getTheChar(), result.getTheChar());//12
//        assertEquals(tpMagic.getTheChar_2(), result.getTheChar_2());//13
//        //14
//        for (int i = 0; i < tpMagic.getTheChars().length; i++) {
//            assertEquals(tpMagic.getTheChars()[i], result.getTheChars()[i]);
//        }
//        //15
//        for (Object obj : tpMagic.getTheConHMap().entrySet()) {
//            assertEquals(((Map.Entry) obj).getValue(), result.getTheConHMap().get(((Map.Entry) obj).getKey()));
//        }
//        //16
//        for (int i = 0; i < tpMagic.getTheDList().size(); i++) {
//            assertEquals(tpMagic.getTheDList().get(i), result.getTheDList().get(i));
//        }
//        assertEquals(tpMagic.getTheDifficulty(), result.getTheDifficulty());//17
//        //18
//        for (int i = 0; i < tpMagic.getTheDous().length; i++) {
//            assertEquals(String.valueOf(tpMagic.getTheDous()[i]), String.valueOf(result.getTheDous()[i]));
//        }
//        assertEquals(String.valueOf(tpMagic.getTheFloatOne()), String.valueOf(result.getTheFloatOne()));//19
//        //20
//        for (int i = 0; i < tpMagic.getTheFloats().length; i++) {
//            assertEquals(String.valueOf(tpMagic.getTheFloats()[i]), String.valueOf(result.getTheFloats()[i]));
//        }
//        assertNull(result.getTheHMap());//21
//        //22
//        assertEquals(4, result.getTheHSet().size());
//        Boolean theHsetFlag1 = false;
//        Boolean theHsetFlag2 = false;
//        Boolean theHsetFlag3 = false;
//        Boolean theHsetFlag4 = false;
//        for (Object obj : result.getTheHSet()) {
//            if (obj instanceof RuleItem) {
//                if (((RuleItem) obj).getRule_id().equals("rule_id_test_4_ok le man")) {
//                    theHsetFlag1 = true;
//                } else if (((RuleItem) obj).getRule_id().equals("rule_id_test_5_ok le man")) {
//                    theHsetFlag2 = true;
//                }
//            } else if (obj == null) {
//                theHsetFlag3 = true;
//            } else if (obj.getClass() == String.class) {
//                if (obj.toString().equals("str1")) {
//                    theHsetFlag4 = true;
//                }
//            }
//        }
//        assertTrue(theHsetFlag1);
//        assertTrue(theHsetFlag2);
//        assertTrue(theHsetFlag3);
//        assertTrue(theHsetFlag4);
//        //23
//        Boolean theHStringSetFlag1 = false;
//        Boolean theHStringSetFlag2 = false;
//        for (Object obj : result.getTheHStringSet()) {
//            if (obj.toString().equals("a13")) {
//                theHStringSetFlag1 = true;
//            } else if (obj.toString().equals("ok")) {
//                theHStringSetFlag2 = true;
//            }
//        }
//        assertTrue(theHStringSetFlag1);
//        assertTrue(theHStringSetFlag2);
//        //24
//        for (int i = 0; i < tpMagic.getTheIList().size(); i++) {
//            assertEquals(tpMagic.getTheIList().get(i), result.getTheIList().get(i));
//        }
//        //25
//        for (int i = 0; i < tpMagic.getTheI_Array().length; i++) {
//            assertEquals(String.valueOf(tpMagic.getTheI_Array()[i]), String.valueOf(result.getTheI_Array()[i]));
//        }
//        //26
//        for (Object obj : tpMagic.getTheLinkHMap().entrySet()) {
//            assertEquals(((Map.Entry) obj).getValue(), result.getTheLinkHMap().get(((Map.Entry) obj).getKey()));
//        }
//        //27
//        for (int i = 0; i < tpMagic.getTheList().size(); i++) {
//            if (tpMagic.getTheList().get(i) instanceof byte[]) {
//                byte[] array = (byte[]) tpMagic.getTheList().get(i);
//                byte[] arrayActual = (byte[]) result.getTheList().get(i);
//                for (int j = 0; j < array.length; j++) {
//                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
//                }
//            } else if (tpMagic.getTheList().get(i) instanceof long[]) {
//                long[] array = (long[]) tpMagic.getTheList().get(i);
//                long[] arrayActual = (long[]) result.getTheList().get(i);
//                for (int j = 0; j < array.length; j++) {
//                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
//                }
//            } else if (tpMagic.getTheList().get(i) instanceof char[]) {
//                char[] array = (char[]) tpMagic.getTheList().get(i);
//                char[] arrayActual = (char[]) result.getTheList().get(i);
//                for (int j = 0; j < array.length; j++) {
//                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
//                }
//            } else if (tpMagic.getTheList().get(i) instanceof double[]) {
//                double[] array = (double[]) tpMagic.getTheList().get(i);
//                double[] arrayActual = (double[]) result.getTheList().get(i);
//                for (int j = 0; j < array.length; j++) {
//                    assertEquals(String.valueOf(array[j]), String.valueOf(arrayActual[j]));
//                }
//            } else if (tpMagic.getTheList().get(i) instanceof Map) {
//                Map map = (Map) tpMagic.getTheList().get(i);
//                Map mapActual = (Map) result.getTheList().get(i);
//                for (Object obj : map.entrySet()) {
//                    assertEquals(((Map.Entry) obj).getValue(), mapActual.get(((Map.Entry) obj).getKey()));
//                }
//            } else {
//                assertEquals(String.valueOf(tpMagic.getTheList().get(i)), String.valueOf(result.getTheList().get(i)));
//            }
//        }
//        //28
//        for (int i = 0; i < tpMagic.getTheLongs().length; i++) {
//            assertEquals(String.valueOf(tpMagic.getTheLongs()[i]), String.valueOf(result.getTheLongs()[i]));
//        }
//
//        //30
//        assertEquals(String.valueOf(10), String.valueOf(result.getTheObjs()[0]));
//        assertEquals("abc", result.getTheObjs()[1]);
//        ArrayList tmpList_objectItem_result = (ArrayList) result.getTheObjs()[2];
//        assertEquals(123, tmpList_objectItem_result.get(0));
//        assertEquals("abc", tmpList_objectItem_result.get(1));
//        ArrayList tmpList2_result = (ArrayList) tmpList_objectItem_result.get(2);
//        assertEquals(1234, tmpList2_result.get(0));
//        assertEquals("str1", tmpList2_result.get(1));
//        assertEquals(Difficulty.HARD, result.getTheObjs()[3]);
//        assertEquals("22.022", result.getTheObjs()[4].toString());
//
//        //31
//        assertEquals(LinkedList.class, result.getTheRuleItemLinkedList().getClass());
//
//        assertEquals("rule_id_test", ((RuleItem) result.getTheRuleItemLinkedList().get(0)).getRule_id());
//
//        assertEquals("s1", ((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(0));
//        assertEquals(100, ((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(1));
//        assertNull(((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(2));
//        assertEquals("rule_id_test_3_ok le man", ((RuleItem) (((LinkedList) result.getTheRuleItemLinkedList().get(1)).get(3))).getRule_id());
//
//        //32
//        assertEquals("rule_id_test", ((RuleItem) result.getTheRuleItemList().get(0)).getRule_id());
//
//        //33
//        assertEquals("abc1", ((ArrayList<String>) result.getTheSList()).get(0));
//        assertEquals("abc2", ((ArrayList<String>) result.getTheSList()).get(1));
//
//        //34
//        assertEquals(100, ((short[]) result.getTheShort())[0]);
//        assertEquals(200, ((short[]) result.getTheShort())[1]);
//        assertEquals(10000, ((short[]) result.getTheShort())[2]);
//
//        assertEquals(tpMagic.getTheShortOne(), result.getTheShortOne());//35
//
//        //36
//        assertEquals(3, result.getTheTreeSet().size());
//        Boolean theTreeSetFlag1 = false;
//        Boolean theTreeSetFlag2 = false;
//        Boolean theTreeSetFlag3 = false;
//        for (Object obj : result.getTheTreeSet()) {
//            if (obj.toString().equals("tree 0")) {
//                theTreeSetFlag1 = true;
//            } else if (obj.toString().equals("tree 1")) {
//                theTreeSetFlag2 = true;
//            } else if (obj.toString().equals("tree 2")) {
//                theTreeSetFlag3 = true;
//            }
//        }
//        assertTrue(theTreeSetFlag1);
//        assertTrue(theTreeSetFlag2);
//        assertTrue(theTreeSetFlag3);
//
//        assertTrue(result.isTheb2());//37
//
//        assertEquals(String.valueOf(100), String.valueOf(result.getThebyte_b()));//38
//
//        //39
//        assertEquals(byte[].class, result.getThebytes().getClass());
//        assertEquals(String.valueOf(0), String.valueOf(result.getThebytes()[0]));
//        assertEquals(String.valueOf(0), String.valueOf(result.getThebytes()[1]));
//        assertEquals(String.valueOf(10), String.valueOf(result.getThebytes()[10]));
//        assertEquals(String.valueOf(20), String.valueOf(result.getThebytes()[20]));
    }

    //测试Object数组中的循环引用
    @Test
    public void testCollection4() throws Exception{
//        Object[] array = new Object[2];
//        LoopPerson1 p1 = new LoopPerson1();
//        p1.setB(new Byte("1"));
//        LoopPerson1 p2 = new LoopPerson1();
//        p2.setB(new Byte("2"));
//        p1.setBrother(p2);
//        p2.setBrother(p1);
//
//        array[0] = p1;
//        array[1] = p2;
//
//        Object[] result = executeBackAndForth(array, Object[].class);
//        assertEquals("1", String.valueOf(((LoopPerson1)result[0]).getB()));
//        assertEquals("2", String.valueOf(((LoopPerson1)result[1]).getB()));
//        assertEquals(result[0], ((LoopPerson1)result[1]).getBrother());
    }

    //序列化一个Object数组，测试不传多余类名
    @Test
    public void testCollection5() throws Exception{
//        Object[] array = new Object[3];
//        LoopPerson1 p1 = new LoopPerson1();
//        p1.setB(new Byte("1"));
//        LoopPerson1 p2 = new LoopPerson1();
//        p2.setB(new Byte("2"));
//        p1.setBrother(p2);
//        p2.setBrother(p1);
//
//        Person1 p3 = new Person1();
//        p3.setId(3L);
//
//        array[0] = p1;
//        array[1] = p2;
//        array[2] = p3;
//
//        Object[] result = executeBackAndForth(array, Object[].class);
//        assertEquals("1", String.valueOf(((LoopPerson1)result[0]).getB()));
//        assertEquals("2", String.valueOf(((LoopPerson1)result[1]).getB()));
//        assertEquals(result[0], ((LoopPerson1)result[1]).getBrother());
//        assertEquals(String.valueOf(3), ((Person1)result[2]).getId().toString());
    }



    //序列化一个list，不传多余类名
    @Test
    public void testCollectionWithLoop3() throws Exception{
//        List list = new ArrayList();
//        LoopPerson1 p1 = new LoopPerson1();
//        p1.setB(new Byte("1"));
//        LoopPerson1 p2 = new LoopPerson1();
//        p2.setB(new Byte("2"));
//
//        Person1 p3 = new Person1();
//        p3.setId(3L);
//
//        list.add(p1);
//        list.add(p2);
//        list.add(p3);
//
//        ArrayList result = executeBackAndForth(list, ArrayList.class);
//        assertEquals("1", String.valueOf(((LoopPerson1)result.get(0)).getB()));
//        assertEquals("2", String.valueOf(((LoopPerson1)result.get(1)).getB()));
//        assertEquals(String.valueOf(3), ((Person1)result.get(2)).getId().toString());
    }

    //序列化一个对象，对象里有个属性是list，无泛型，不传多余类名
    @Test
    public void testCollectionWithLoop4() throws Exception{
//        PersonCollection3 pc = new PersonCollection3();
//        ArrayList list = new ArrayList();
//        LoopPerson1 p1 = new LoopPerson1();
//        p1.setB(new Byte("1"));
//        LoopPerson1 p2 = new LoopPerson1();
//        p2.setB(new Byte("2"));
//
//        LoopPerson1 p2_1 = new LoopPerson1();
//        p2_1.setB(new Byte("44"));
//
//        Person1 p3 = new Person1();
//        p3.setId(3L);
//
//        list.add(p1);
//        list.add(p2);
//        list.add(123);
//        list.add(p2_1);
//        list.add(p3);
//        pc.setList(list);
//
//        PersonCollection3 result = executeBackAndForth(pc, PersonCollection3.class);
//        assertEquals("1", String.valueOf(((LoopPerson1)result.getList().get(0)).getB()));
//        assertEquals("2", String.valueOf(((LoopPerson1)result.getList().get(1)).getB()));
//        assertEquals("123", String.valueOf(result.getList().get(2)));
//        assertEquals("44", String.valueOf(((LoopPerson1)result.getList().get(3)).getB()));
//        assertEquals(String.valueOf(3), ((Person1)result.getList().get(4)).getId().toString());
    }

    //序列化一个对象，对象里有个属性是list，有泛型但泛型是Object，不传多余类名
    @Test
    public void testCollectionWithLoop6() throws Exception{
//        PersonCollection4 pc = new PersonCollection4();
//        ArrayList<Object> list = new ArrayList<Object>();
//        LoopPerson1 p1 = new LoopPerson1();
//        p1.setB(new Byte("1"));
//        LoopPerson1 p2 = new LoopPerson1();
//        p2.setB(new Byte("2"));
//
//        LoopPerson1 p2_1 = new LoopPerson1();
//        p2_1.setB(new Byte("44"));
//
//        Person1 p3 = new Person1();
//        p3.setId(3L);
//
//        list.add(p1);
//        list.add(p2);
//        list.add(123);
//        list.add(p2_1);
//        list.add(p3);
//        pc.setList(list);
//
//        PersonCollection4 result = executeBackAndForth(pc, PersonCollection4.class);
//        assertEquals("1", String.valueOf(((LoopPerson1)result.getList().get(0)).getB()));
//        assertEquals("2", String.valueOf(((LoopPerson1)result.getList().get(1)).getB()));
//        assertEquals("123", String.valueOf(result.getList().get(2)));
//        assertEquals("44", String.valueOf(((LoopPerson1)result.getList().get(3)).getB()));
//        assertEquals(String.valueOf(3), ((Person1)result.getList().get(4)).getId().toString());
    }

    //序列化一个对象，对象里有个属性是list，有泛型，不传类名
    @Test
    public void testCollectionWithLoop7() throws Exception{
//        UserGeneric ug = new UserGeneric();
//        Person2 p1 = new Person2();
//        p1.setId(1L);
//        p1.setName("111");
//        Person2 p2 = new Person2();
//        p2.setId(2L);
//        p2.setName("222");
//
//        ArrayList<Person2> list = new ArrayList<Person2>();
//        list.add(p1);
//        list.add(p2);
//        ug.setList(list);
//
//        UserGeneric result = executeBackAndForth(ug, UserGeneric.class);
//        assertEquals(2, result.getList().size());
//
//        assertEquals("1", result.getList().get(0).getId().toString());
//        assertEquals("2", result.getList().get(1).getId().toString());
    }




    //序列化一个对象，对象里有个属性是set，有泛型
    @Test
    public void testCollectionWithLoop8() throws Exception{
//        UserGeneric ug = new UserGeneric();
//        Person2 p1 = new Person2();
//        p1.setId(1L);
//        p1.setName("111");
//        Person2 p2 = new Person2();
//        p2.setId(2L);
//        p2.setName("222");
//
//        HashSet<Person2> set = new HashSet<Person2>();
//        set.add(p1);
//        set.add(p2);
//        ug.setSet(set);
//
//        UserGeneric result = executeBackAndForth(ug, UserGeneric.class);
//        assertEquals(2, result.getSet().size());
    }

    //序列化一个对象，对象里有个属性是set，有泛型,是接口
    @Test
    public void testCollectionWithLoop9() throws Exception{
//        UserGeneric ug = new UserGeneric();
//        Person2 p1 = new Person2();
//        p1.setId(1L);
//        p1.setName("111");
//        Person2 p2 = new Person2();
//        p2.setId(2L);
//        p2.setName("222");
//
//        Set<Person2> set = new HashSet<Person2>();
//        set.add(p1);
//        set.add(p2);
//        ug.setSet1(set);
//
//        UserGeneric result = executeBackAndForth(ug, UserGeneric.class);
//        assertEquals(2, result.getSet1().size());
    }


    public static void main(String[] args) throws Exception{
        System.out.println(2 << 1);
    }

}
