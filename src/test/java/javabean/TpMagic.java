package javabean;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class TpMagic {
    private String s1;
    private Integer i1;
    private Double d1;
    private String d2;
    private Boolean theBn1;
    private Boolean theBn2;
    private boolean theb2;
    private Long l1;
    private long l_1_2;
    private int[] theI_Array;
    private List theList;
    private Object[] theObjs;
    private HashMap theHMap;
    private LinkedHashMap theLinkHMap;
    private ConcurrentHashMap theConHMap;
    private byte thebyte_b;
    private Byte theByte_Byte;
    private long[] theLongs;
    private char theChar;
    private Character theChar_2;
    private char[] theChars;
    private double[] theDous;
    private ArrayList<Double> theDList;
    private ArrayList<Integer> theIList;
    private ArrayList<String> theSList;
    private ArrayList<RuleItem> theRuleItemList;
    private LinkedList theRuleItemLinkedList;
    private byte[] thebytes;
    private HashSet theHSet;
    private HashSet<String> theHStringSet;
    private TreeSet theTreeSet;
    private short[] theShort;
    private float[] theFloats;
    private boolean[] theBooleans;
    private short theShortOne;
    private float theFloatOne;
    private My_InnerClass theMy_InnerClass;
    private Difficulty theDifficulty;
    private BigDecimal theBigDecimal;
    private Long theBigLong;

    public TpMagic() {
        this.theMy_InnerClass = new My_InnerClass();
        ArrayList tmpHashItemList = new ArrayList();
        tmpHashItemList.add(100);
        tmpHashItemList.add(200);
        tmpHashItemList.add(null);
        tmpHashItemList.add(600);
        this.theMy_InnerClass.setTheList(tmpHashItemList);

        HashMap tmpHMap = new HashMap();
        tmpHMap.put(1, "ok le man1");

        RuleItem tmpRuleItem = new RuleItem();
        tmpRuleItem.setDescription("description_test");
        tmpRuleItem.setItem_id("item_id_test");
        tmpRuleItem.setRule_id("rule_id_test");
        tmpRuleItem.setWarning_value("warning_value_test");
        tmpHMap.put(2, tmpRuleItem);

        this.theMy_InnerClass.setTheHMap(tmpHMap);

    }

    public List findtheListInner() {
        return this.theMy_InnerClass.getTheList();
    }

    public HashMap findHMapInner() {
        return this.theMy_InnerClass.getTheHMap();
    }

    public String getS1() {
        return s1;
    }

    public void setS1(String s1) {
        this.s1 = s1;
    }


    public Integer getI1() {
        return i1;
    }

    public void setI1(Integer i1) {
        this.i1 = i1;
    }

    public Double getD1() {
        return d1;
    }

    public void setD1(Double d1) {
        this.d1 = d1;
    }

    public String getD2() {
        return d2;
    }

    public void setD2(String d2) {
        this.d2 = d2;
    }

    public Long getL1() {
        return l1;
    }

    public void setL1(Long l1) {
        this.l1 = l1;
    }

    public Boolean getTheBn1() {
        return theBn1;
    }

    public void setTheBn1(Boolean theBn1) {
        this.theBn1 = theBn1;
    }

    public boolean isTheb2() {
        return theb2;
    }

    public void setTheb2(boolean theb2) {
        this.theb2 = theb2;
    }

    public Boolean getTheBn2() {
        return theBn2;
    }

    public void setTheBn2(Boolean theBn2) {
        this.theBn2 = theBn2;
    }

    public int[] getTheI_Array() {
        return theI_Array;
    }

    public void setTheI_Array(int[] theI_Array) {
        this.theI_Array = theI_Array;
    }

    public Object[] getTheObjs() {
        return theObjs;
    }

    public void setTheObjs(Object[] theObjs) {
        this.theObjs = theObjs;
    }

    public HashMap getTheHMap() {
        return theHMap;
    }

    public void setTheHMap(HashMap theHMap) {
        this.theHMap = theHMap;
    }

    public LinkedHashMap getTheLinkHMap() {
        return theLinkHMap;
    }

    public void setTheLinkHMap(LinkedHashMap theLinkHMap) {
        this.theLinkHMap = theLinkHMap;
    }

    public ConcurrentHashMap getTheConHMap() {
        return theConHMap;
    }

    public void setTheConHMap(ConcurrentHashMap theConHMap) {
        this.theConHMap = theConHMap;
    }

    public byte getThebyte_b() {
        return thebyte_b;
    }

    public void setThebyte_b(byte thebyte_b) {
        this.thebyte_b = thebyte_b;
    }

    public Byte getTheByte_Byte() {
        return theByte_Byte;
    }

    public void setTheByte_Byte(Byte theByte_Byte) {
        //System.out.println("haha "+theByte_Byte);
        this.theByte_Byte = theByte_Byte;
    }

    public long getL_1_2() {
        return l_1_2;
    }

    public void setL_1_2(long l_1_2) {
        this.l_1_2 = l_1_2;
    }

    public long[] getTheLongs() {
        return theLongs;
    }

    public void setTheLongs(long[] theLongs) {
        this.theLongs = theLongs;
    }

    public char getTheChar() {
        return theChar;
    }

    public void setTheChar(char theChar) {
        this.theChar = theChar;
    }

    public Character getTheChar_2() {
        return theChar_2;
    }

    public void setTheChar_2(Character theChar_2) {
        this.theChar_2 = theChar_2;
    }

    public char[] getTheChars() {
        return theChars;
    }

    public void setTheChars(char[] theChars) {
        this.theChars = theChars;
    }


    public List getTheList() {
        return theList;
    }

    public void setTheList(List theList) {
        this.theList = theList;
    }

    public double[] getTheDous() {
        return theDous;
    }

    public void setTheDous(double[] theDous) {
        this.theDous = theDous;
    }

    public ArrayList<Double> getTheDList() {
        return theDList;
    }

    public void setTheDList(ArrayList<Double> theDList) {
        this.theDList = theDList;
    }

    public ArrayList<Integer> getTheIList() {
        return theIList;
    }

    public void setTheIList(ArrayList<Integer> theIList) {
        this.theIList = theIList;
    }

    public ArrayList<RuleItem> getTheRuleItemList() {
        return theRuleItemList;
    }

    public void setTheRuleItemList(ArrayList<RuleItem> theRuleItemList) {
        this.theRuleItemList = theRuleItemList;
    }

    public byte[] getThebytes() {
        return thebytes;
    }

    public void setThebytes(byte[] thebytes) {
        this.thebytes = thebytes;
    }

    public LinkedList getTheRuleItemLinkedList() {
        return theRuleItemLinkedList;
    }

    public void setTheRuleItemLinkedList(LinkedList theRuleItemLinkedList) {
        this.theRuleItemLinkedList = theRuleItemLinkedList;
    }

    public ArrayList<String> getTheSList() {
        return theSList;
    }

    public void setTheSList(ArrayList<String> theSList) {
        this.theSList = theSList;
    }

    public Long getTheBigLong() {
        return theBigLong;
    }

    public void setTheBigLong(Long theBigLong) {
        this.theBigLong = theBigLong;
    }

    public Difficulty getTheDifficulty() {
        return theDifficulty;
    }

    public void setTheDifficulty(Difficulty theDifficulty) {
        this.theDifficulty = theDifficulty;
    }


    private class My_InnerClass {
        private String s1;
        private Integer i1;
        private Double d1;
        private String d2;
        private Boolean theBn1;
        private Boolean theBn2;
        private boolean theb2;
        private Long l1;
        private long l_1_2;
        private int[] theI_Array;
        private List theList;
        private Object[] theObjs;
        private HashMap theHMap;
        private LinkedHashMap theLinkHMap;
        private ConcurrentHashMap theConHMap;
        private byte thebyte_b;
        private Byte theByte_Byte;
        private long[] theLongs;
        private char theChar;
        private Character theChar_2;
        private char[] theChars;
        private double[] theDous;
        private ArrayList<Double> theDList;
        private ArrayList<Integer> theIList;
        private ArrayList<String> theSList;
        private ArrayList<RuleItem> theRuleItemList;
        private LinkedList theRuleItemLinkedList;
        private byte[] thebytes;
        private HashSet theHSet;
        private HashSet<String> theHStringSet;
        private TreeSet theTreeSet;
        private short[] theShort;
        private float[] theFloats;
        private boolean[] theBooleans;
        private short theShortOne;
        private float theFloatOne;

        protected My_InnerClass() {

        }

        public String getS1() {
            return s1;
        }

        public void setS1(String s1) {
            this.s1 = s1;
        }

        public Integer getI1() {
            return i1;
        }

        public void setI1(Integer i1) {
            this.i1 = i1;
        }

        public Double getD1() {
            return d1;
        }

        public void setD1(Double d1) {
            this.d1 = d1;
        }

        public String getD2() {
            return d2;
        }

        public void setD2(String d2) {
            this.d2 = d2;
        }

        public Boolean getTheBn1() {
            return theBn1;
        }

        public void setTheBn1(Boolean theBn1) {
            this.theBn1 = theBn1;
        }

        public Boolean getTheBn2() {
            return theBn2;
        }

        public void setTheBn2(Boolean theBn2) {
            this.theBn2 = theBn2;
        }

        public boolean isTheb2() {
            return theb2;
        }

        public void setTheb2(boolean theb2) {
            this.theb2 = theb2;
        }

        public Long getL1() {
            return l1;
        }

        public void setL1(Long l1) {
            this.l1 = l1;
        }

        public long getL_1_2() {
            return l_1_2;
        }

        public void setL_1_2(long l_1_2) {
            this.l_1_2 = l_1_2;
        }

        public int[] getTheI_Array() {
            return theI_Array;
        }

        public void setTheI_Array(int[] theI_Array) {
            this.theI_Array = theI_Array;
        }

        public List getTheList() {
            return theList;
        }

        public void setTheList(List theList) {
            this.theList = theList;
        }

        public Object[] getTheObjs() {
            return theObjs;
        }

        public void setTheObjs(Object[] theObjs) {
            this.theObjs = theObjs;
        }

        public HashMap getTheHMap() {
            return theHMap;
        }

        public void setTheHMap(HashMap theHMap) {
            this.theHMap = theHMap;
        }

        public LinkedHashMap getTheLinkHMap() {
            return theLinkHMap;
        }

        public void setTheLinkHMap(LinkedHashMap theLinkHMap) {
            this.theLinkHMap = theLinkHMap;
        }

        public ConcurrentHashMap getTheConHMap() {
            return theConHMap;
        }

        public void setTheConHMap(ConcurrentHashMap theConHMap) {
            this.theConHMap = theConHMap;
        }

        public byte getThebyte_b() {
            return thebyte_b;
        }

        public void setThebyte_b(byte thebyte_b) {
            this.thebyte_b = thebyte_b;
        }

        public Byte getTheByte_Byte() {
            return theByte_Byte;
        }

        public void setTheByte_Byte(Byte theByte_Byte) {
            this.theByte_Byte = theByte_Byte;
        }

        public long[] getTheLongs() {
            return theLongs;
        }

        public void setTheLongs(long[] theLongs) {
            this.theLongs = theLongs;
        }

        public char getTheChar() {
            return theChar;
        }

        public void setTheChar(char theChar) {
            this.theChar = theChar;
        }

        public Character getTheChar_2() {
            return theChar_2;
        }

        public void setTheChar_2(Character theChar_2) {
            this.theChar_2 = theChar_2;
        }

        public char[] getTheChars() {
            return theChars;
        }

        public void setTheChars(char[] theChars) {
            this.theChars = theChars;
        }

        public double[] getTheDous() {
            return theDous;
        }

        public void setTheDous(double[] theDous) {
            this.theDous = theDous;
        }

        public ArrayList<Double> getTheDList() {
            return theDList;
        }

        public void setTheDList(ArrayList<Double> theDList) {
            this.theDList = theDList;
        }

        public ArrayList<Integer> getTheIList() {
            return theIList;
        }

        public void setTheIList(ArrayList<Integer> theIList) {
            this.theIList = theIList;
        }

        public ArrayList<String> getTheSList() {
            return theSList;
        }

        public void setTheSList(ArrayList<String> theSList) {
            this.theSList = theSList;
        }

        public ArrayList<RuleItem> getTheRuleItemList() {
            return theRuleItemList;
        }

        public void setTheRuleItemList(ArrayList<RuleItem> theRuleItemList) {
            this.theRuleItemList = theRuleItemList;
        }

        public LinkedList getTheRuleItemLinkedList() {
            return theRuleItemLinkedList;
        }

        public void setTheRuleItemLinkedList(LinkedList theRuleItemLinkedList) {
            this.theRuleItemLinkedList = theRuleItemLinkedList;
        }

        public byte[] getThebytes() {
            return thebytes;
        }

        public void setThebytes(byte[] thebytes) {
            this.thebytes = thebytes;
        }

        public HashSet getTheHSet() {
            return theHSet;
        }

        public void setTheHSet(HashSet theHSet) {
            this.theHSet = theHSet;
        }

        public HashSet<String> getTheHStringSet() {
            return theHStringSet;
        }

        public void setTheHStringSet(HashSet<String> theHStringSet) {
            this.theHStringSet = theHStringSet;
        }

        public TreeSet getTheTreeSet() {
            return theTreeSet;
        }

        public void setTheTreeSet(TreeSet theTreeSet) {
            this.theTreeSet = theTreeSet;
        }

        public short[] getTheShort() {
            return theShort;
        }

        public void setTheShort(short[] theShort) {
            this.theShort = theShort;
        }

        public float[] getTheFloats() {
            return theFloats;
        }

        public void setTheFloats(float[] theFloats) {
            this.theFloats = theFloats;
        }

        public boolean[] getTheBooleans() {
            return theBooleans;
        }

        public void setTheBooleans(boolean[] theBooleans) {
            this.theBooleans = theBooleans;
        }

        public short getTheShortOne() {
            return theShortOne;
        }

        public void setTheShortOne(short theShortOne) {
            this.theShortOne = theShortOne;
        }

        public float getTheFloatOne() {
            return theFloatOne;
        }

        public void setTheFloatOne(float theFloatOne) {
            this.theFloatOne = theFloatOne;
        }
    }

    public HashSet getTheHSet() {
        return theHSet;
    }

    public void setTheHSet(HashSet theHSet) {
        this.theHSet = theHSet;
    }

    public TreeSet getTheTreeSet() {
        return theTreeSet;
    }

    public void setTheTreeSet(TreeSet theTreeSet) {
        this.theTreeSet = theTreeSet;
    }

    public HashSet<String> getTheHStringSet() {
        return theHStringSet;
    }

    public void setTheHStringSet(HashSet<String> theHStringSet) {
        this.theHStringSet = theHStringSet;
    }

    public short[] getTheShort() {
        return theShort;
    }

    public void setTheShort(short[] theShort) {
        this.theShort = theShort;
    }

    public float[] getTheFloats() {
        return theFloats;
    }

    public void setTheFloats(float[] theFloats) {
        this.theFloats = theFloats;
    }

    public boolean[] getTheBooleans() {
        return theBooleans;
    }

    public void setTheBooleans(boolean[] theBooleans) {
        this.theBooleans = theBooleans;
    }

    public short getTheShortOne() {
        return theShortOne;
    }

    public void setTheShortOne(short theShortOne) {
        this.theShortOne = theShortOne;
    }

    public float getTheFloatOne() {
        return theFloatOne;
    }

    public void setTheFloatOne(float theFloatOne) {
        this.theFloatOne = theFloatOne;
    }

    public My_InnerClass getTheMy_InnerClass() {
        return theMy_InnerClass;
    }

    public void setTheMy_InnerClass(My_InnerClass theMy_InnerClass) {
        this.theMy_InnerClass = theMy_InnerClass;
    }

//    public Difficulty getTheDifficulty() {
//        return theDifficulty;
//    }
//
//    public void setTheDifficulty(Difficulty theDifficulty) {
//        this.theDifficulty = theDifficulty;
//    }

    public BigDecimal getTheBigDecimal() {
        return theBigDecimal;
    }

    public void setTheBigDecimal(BigDecimal theBigDecimal) {
        this.theBigDecimal = theBigDecimal;
    }
}
