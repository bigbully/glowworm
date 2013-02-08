package com.jd.dd.glowworm.deserializer;

import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.asm.ASMException;
import com.jd.dd.glowworm.deserializer.asm.ASMDeserializerFactory;
import com.jd.dd.glowworm.deserializer.asm.ASMJavaBeanDeserializer;
import com.jd.dd.glowworm.deserializer.multi.ArrayDeserializer;
import com.jd.dd.glowworm.deserializer.multi.ListDeserializer;
import com.jd.dd.glowworm.deserializer.multi.MapDeserializer;
import com.jd.dd.glowworm.deserializer.multi.SetDeserializer;
import com.jd.dd.glowworm.deserializer.normal.*;
import com.jd.dd.glowworm.deserializer.primary.*;
import com.jd.dd.glowworm.deserializer.reflect.DefaultFieldDeserializer;
import com.jd.dd.glowworm.deserializer.reflect.FieldDeserializer;
import com.jd.dd.glowworm.deserializer.reflect.JavaBeanDeserializer;
import com.jd.dd.glowworm.serializer.multi.ListSerializer;
import com.jd.dd.glowworm.util.*;
import com.jd.dd.glowworm.util.IdentityHashMap;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PBDeserializer {

    private InputStreamBuffer inputStreamBuffer;
    private CodedInputStream theCodedInputStream;
    private ExistInputStream existStream;
    private TypeInputStream typeStream;

    private static IdentityHashMap<Type, ObjectDeserializer> derializers = new IdentityHashMap<Type, ObjectDeserializer>();
    private Parameters parameters;//用来保存反序列化的参数信息
    private Map<Integer, Object> objectIndexMap;//用来保存所有和javabeean, Array, List, Set, Map相关的index-对象键值对
    private HashMap<Integer, Integer> refMap;
    private final static ThreadLocal<SoftReference<InputStreamBuffer>> bufLocal = new ThreadLocal<SoftReference<InputStreamBuffer>>();

    static {
        initTheDeserializerHMap();
    }

    private static void initTheDeserializerHMap() {
        derializers.put(boolean.class, BooleanDeserializer.instance);
        derializers.put(Boolean.class, BooleanDeserializer.instance);
        derializers.put(int.class, IntegerDeserializer.instance);
        derializers.put(Integer.class, IntegerDeserializer.instance);
        derializers.put(double.class, DoubleDeserializer.instance);
        derializers.put(Double.class, DoubleDeserializer.instance);
        derializers.put(long.class, LongDeserializer.instance);
        derializers.put(Long.class, LongDeserializer.instance);
        derializers.put(byte.class, ByteDeserializer.instance);
        derializers.put(Byte.class, ByteDeserializer.instance);
        derializers.put(char.class, CharacterDeserializer.instance);
        derializers.put(Character.class, CharacterDeserializer.instance);
        derializers.put(short.class, ShortDeserializer.instance);
        derializers.put(Short.class, ShortDeserializer.instance);
        derializers.put(float.class, FloatDeserializer.instance);
        derializers.put(Float.class, FloatDeserializer.instance);

        derializers.put(boolean[].class, BooleanArrayDeserializer.instance);
        derializers.put(byte[].class, ByteArrayDeserializer.instance);
        derializers.put(char[].class, CharArrayDeserializer.instance);
        derializers.put(double[].class, DoubleArrayDeserializer.instance);
        derializers.put(float[].class, FloatArrayDeserializer.instance);
        derializers.put(int[].class, IntArrayDeserializer.instance);
        derializers.put(long[].class, LongArrayDeserializer.instance);
        derializers.put(short[].class, ShortArrayDeserializer.instance);

        derializers.put(String.class, StringDeserializer.instance);

        //todo
//        derializers.put(Object.class, JavaObjectDeserializer.instance);

        derializers.put(BigDecimal.class, BigDecimalDeserializer.instance);
        derializers.put(BigInteger.class, BigIntegerDeserializer.instance);
        derializers.put(Date.class, DateDeserializer.instance);
        derializers.put(Timestamp.class, TimestampDeserializer.instance);
        derializers.put(Inet4Address.class, InetAddressDeserializer.instance);
        derializers.put(Inet6Address.class, InetAddressDeserializer.instance);

        derializers.put(AtomicInteger.class, AtomicIntegerDeserializer.instance);
        derializers.put(AtomicBoolean.class, AtomicBooleanDeserializer.instance);
        derializers.put(AtomicLong.class, AtomicLongDeserializer.instance);
    }

    public void analysizeHead(byte[] bytes) throws IOException {
        SoftReference<InputStreamBuffer> ref = bufLocal.get();

        if (ref != null) {
            inputStreamBuffer = ref.get();
            theCodedInputStream = inputStreamBuffer.getTheCodedInputStream();
            existStream = inputStreamBuffer.getExistStream();
            typeStream = inputStreamBuffer.getTypeStream();
            bufLocal.set(null);
        }

        if (theCodedInputStream == null) {
            theCodedInputStream = new CodedInputStream(new BufferInputStream(bytes));
        } else {
            theCodedInputStream.reset(bytes);
        }

        //首先读出exist的长度和位置
        int refSize = theCodedInputStream.readInt32();
        if (refSize != 0) {
            analysizeRef();
        }
        int existBytesSize = theCodedInputStream.readInt32();
        if (existStream == null) {
            existStream = new ExistInputStream(bytes, theCodedInputStream.getPos(), existBytesSize);
        } else {
            existStream.reset(bytes, theCodedInputStream.getPos(), existBytesSize);
        }

        //跳过exist内容的长度，继续读typeHead的长度和位置
        theCodedInputStream.skipRawBytes(existBytesSize);
        int typeHeadBytesSize = theCodedInputStream.readInt32();
        if (typeStream == null) {
            typeStream = new TypeInputStream(bytes, theCodedInputStream.getPos(), typeHeadBytesSize);
        } else {
            typeStream.headReset(bytes, theCodedInputStream.getPos(), typeHeadBytesSize);
        }
        theCodedInputStream.skipRawBytes(typeHeadBytesSize);
        int typeBytesSize = theCodedInputStream.readInt32();
        typeStream.reset(theCodedInputStream.getPos(), typeBytesSize);
        theCodedInputStream.skipRawBytes(typeBytesSize);
    }

    private void analysizeRef() {

    }

    public boolean isUserJavaBean(Class parserClazz) {
        return ASMJavaBeanDeserializer.class.isAssignableFrom(parserClazz) ||
                JavaBeanDeserializer.class.isAssignableFrom(parserClazz);
    }

    public boolean isAsmJavaBean(Class parserClazz) {
        return parserClazz.getName().startsWith(ASMDeserializerFactory.DeserializerClassName_prefix);
    }

    public boolean isPureObject(ObjectDeserializer parser){
        return parser instanceof JavaObjectDeserializer;
    }

    public int scanType() {
        return typeStream.readInt();
    }

    public void close() {
        existStream.reset();
        theCodedInputStream.reset();
        typeStream.reset();
        if (inputStreamBuffer == null) {
            inputStreamBuffer = new InputStreamBuffer(theCodedInputStream, existStream, typeStream);
        } else {
            inputStreamBuffer.setAll(theCodedInputStream, existStream, typeStream);
        }

        bufLocal.set(new SoftReference<InputStreamBuffer>(inputStreamBuffer));
        this.theCodedInputStream = null;
        this.existStream = null;
        this.typeStream = null;
        this.inputStreamBuffer = null;
    }

    public ObjectDeserializer getDeserializer(Type type) {
        ObjectDeserializer derializer = this.derializers.get(type);
        if (derializer != null) {
            return derializer;
        }

        if (type instanceof Class<?>) {
            return getDeserializer((Class<?>) type, type);
        }

        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class<?>) {
                return getDeserializer((Class<?>) rawType, type);
            } else {
                return getDeserializer(rawType);
            }
        }

        return null;
    }

    public ObjectDeserializer getDeserializer(Class<?> clazz, Type type) {
        ObjectDeserializer deserializer = derializers.get(type);
        if (deserializer != null) {
            return deserializer;
        }

        if (type == null) {
            type = clazz;
        }

        deserializer = derializers.get(type);
        if (deserializer != null) {
            return deserializer;
        }

        if (type instanceof WildcardType || type instanceof TypeVariable) {
            deserializer = derializers.get(clazz);
        }

        if (deserializer != null) {
            return deserializer;
        }

        deserializer = derializers.get(type);
        if (deserializer != null) {
            return deserializer;
        }

        if (clazz.isArray()) {
            return ArrayDeserializer.instance;
        } else if (Map.class.isAssignableFrom(clazz)){
            return MapDeserializer.instance;
        } else if (List.class.isAssignableFrom(clazz)){
            return ListDeserializer.instance;
        } else if (Set.class.isAssignableFrom(clazz)){
            return SetDeserializer.instance;
        } else {
            deserializer = createJavaBeanDeserializer(clazz, type);
        }

        putDeserializer(type, deserializer);

        return deserializer;
    }

    public ObjectDeserializer createJavaBeanDeserializer(Class<?> clazz, Type type) {
        /*if (clazz == Class.class) {
            return this.defaultSerializer;
        }*/

        boolean asmEnable = true;
        if (asmEnable && !Modifier.isPublic(clazz.getModifiers())) {
            asmEnable = false;
        }

        if (clazz.getTypeParameters().length != 0) {
            asmEnable = false;
        }

        if (ASMClassLoader.isExternalClass(clazz)) {
            asmEnable = false;
        }

        if (asmEnable) {
            DeserializeBeanInfo beanInfo = DeserializeBeanInfo.computeSetters(clazz, type);
            for (FieldInfo fieldInfo : beanInfo.getFieldList()) {
                if (fieldInfo.isGetOnly()) {
                    asmEnable = false;
                    break;
                }

                Class<?> fieldClass = fieldInfo.getFieldClass();
                if (!Modifier.isPublic(fieldClass.getModifiers())) {
                    asmEnable = false;
                    break;
                }

                if (fieldClass.isMemberClass() && !Modifier.isStatic(fieldClass.getModifiers())) {
                    asmEnable = false;
                }
            }
        }

        if (asmEnable) {
            if (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {
                asmEnable = false;
            }
        }

        if (!asmEnable) {
            return new JavaBeanDeserializer(this, clazz, type);
        }

        try {
            return ASMDeserializerFactory.getInstance().createJavaBeanDeserializer(this, clazz, type);
        } catch (ASMException asmError) {
            return new JavaBeanDeserializer(this, clazz, type);
        } catch (Exception e) {
            throw new PBException("create asm deserializer error, " + clazz.getName(), e);
        }
    }

    private void putDeserializer(Type type, ObjectDeserializer deserializer) {
        derializers.put(type, deserializer);
    }

    public boolean isObjectExist() {
        return existStream.readRawByte() == 0 ? true : false;
    }

    public boolean scanBool() throws IOException {
        return theCodedInputStream.readBool();
    }

    public int scanInt() throws IOException{
        return theCodedInputStream.readInt();
    }

    public float scanFloat() throws IOException{
        return theCodedInputStream.readFloat();
    }

    public short scanShort() throws IOException {
        return (short)theCodedInputStream.readInt();
    }

    public long scanLong() throws IOException{
        return theCodedInputStream.readLong();
    }

    public byte scanByte() throws IOException{
        return theCodedInputStream.readRawByte();
    }

    public double scanDouble() throws IOException{
        return theCodedInputStream.readDouble();
    }

    public Enum scanEnum() throws Exception {
        return Enum.valueOf((Class<Enum>) TypeUtils.loadClass(scanString()), scanString());
    }

    public int scanNaturalInt() throws Exception {
        return theCodedInputStream.readInt32();
    }

    public byte[] scanByteArray() throws Exception {
        return theCodedInputStream.readRawBytes(scanNaturalInt()).toByteArray();
    }

    public String scanString() throws Exception {
        return theCodedInputStream.readString();
    }

    public Object getReference() {
        //todo
        return null;
    }

    public void addToObjectIndexMap(Object object, ObjectDeserializer deserializer) {
        //todo
    }

    public Object scanFieldObject(ObjectDeserializer parser, Type type, boolean needConfirmExist){
        Object ret = null;
        try {
            if (isAsmJavaBean(parser.getClass()) || isPureObject(parser)) {
                if (isObjectExist()){
                    ret = parser.deserialize(this, type, needConfirmExist);
                }else {
                    ret = getReference();
                }
            } else {
                ret = parser.deserialize(this, type, needConfirmExist);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public Object parsePureObject(Integer itemType) {
        try {
            itemType = itemType == null ? scanType() : itemType;
            switch (itemType) {
//                case com.jd.dd.glowworm.asm.Type.OBJECT:
//                    return parseObject(null);
                case com.jd.dd.glowworm.asm.Type.BYTE:
                    return scanByte();
                case com.jd.dd.glowworm.asm.Type.SHORT:
                    return scanShort();
                case com.jd.dd.glowworm.asm.Type.CHAR:
                    return scanString();
                case com.jd.dd.glowworm.asm.Type.INT:
                    return scanInt();
                case com.jd.dd.glowworm.asm.Type.DOUBLE:
                    return scanDouble();
                case com.jd.dd.glowworm.asm.Type.FLOAT:
                    return scanFloat();
                case com.jd.dd.glowworm.asm.Type.LONG:
                    return scanLong();
                case com.jd.dd.glowworm.asm.Type.STRING:
                    return scanString();
                case com.jd.dd.glowworm.asm.Type.BOOLEAN:
                    return scanBool();
                case com.jd.dd.glowworm.asm.Type.BIGDECIMAL:
                    return scanBigDecimal();
                case com.jd.dd.glowworm.asm.Type.BIGINTEGER:
                    return scanBigInteger();
                case com.jd.dd.glowworm.asm.Type.ENUM:
                    return scanEnum();
                case com.jd.dd.glowworm.asm.Type.ARRAY_BYTE:
                    return scanByteArray();
                case com.jd.dd.glowworm.asm.Type.ARRAY_CHAR:
                    return scanString().toCharArray();
                case com.jd.dd.glowworm.asm.Type.ARRAY_INT:
                    return scanIntArray();
                case com.jd.dd.glowworm.asm.Type.ARRAY_DOUBLE:
                    return scanDoubleArray();
                case com.jd.dd.glowworm.asm.Type.ARRAY_FLOAT:
                    return scanFloatArray();
                case com.jd.dd.glowworm.asm.Type.ARRAY_SHORT:
                    return scanIntArray();
                case com.jd.dd.glowworm.asm.Type.ARRAY_BOOLEAN:
                    return scanBooleanArray();
                case com.jd.dd.glowworm.asm.Type.ARRAY_LONG:
                    return scanLongArray();
                case com.jd.dd.glowworm.asm.Type.ARRAY:
                    return scanObjArray();
                case com.jd.dd.glowworm.asm.Type.INETADDRESS:
                    return scanInetAddress();
                case com.jd.dd.glowworm.asm.Type.DATE:
                    return scanDate();
                case com.jd.dd.glowworm.asm.Type.TIMESTAMP:
                    return scanTimeStamp();
                case com.jd.dd.glowworm.asm.Type.ATOMIC_BOOL:
                    return scanAtomicBool();
                case com.jd.dd.glowworm.asm.Type.ATOMIC_INT:
                    return scanAtomicInt();
                case com.jd.dd.glowworm.asm.Type.ATOMIC_LONG:
                    return scanAtomicLong();
//                case com.jd.dd.glowworm.asm.Type.LIST_ARRAYLIST:
//                    ArrayList retList = new ArrayList();
//                    addToObjectIndexMap(retList, null);
//                    parseArray(Object.class, retList, null, null);
//                    return retList;
//                case com.jd.dd.glowworm.asm.Type.LIST_LINKEDLIST:
//                    LinkedList retLinkedList = new LinkedList();
//                    addToObjectIndexMap(retLinkedList, null);
//                    parseArray(Object.class, retLinkedList, null, null);
//                    return retLinkedList;
//                case com.jd.dd.glowworm.asm.Type.COLLECTION_HASHSET:
//                    HashSet retHashSet = new HashSet();
//                    addToObjectIndexMap(retHashSet, null);
//                    parseArray(Object.class, retHashSet, null, null);
//                    return retHashSet;
//                case com.jd.dd.glowworm.asm.Type.COLLECTION_TREESET:
//                    TreeSet retTreeSet = new TreeSet();
//                    addToObjectIndexMap(retTreeSet, null);
//                    parseArray(Object.class, retTreeSet, null, null);
//                    return retTreeSet;
//                case com.jd.dd.glowworm.asm.Type.MAP_HASH:
//                    HashMap retMap = new HashMap();
//                    addToObjectIndexMap(retMap, null);
//                    int tmpMapSz = scanInt();
//                    parseObject(retMap, null, tmpMapSz);
//                    return retMap;
//                case com.jd.dd.glowworm.asm.Type.MAP_LinkedHash:
//                    LinkedHashMap retLinkedHashMap = new LinkedHashMap();
//                    addToObjectIndexMap(retLinkedHashMap, null);
//                    tmpMapSz = scanInt();
//                    parseObject(retLinkedHashMap, null, tmpMapSz);
//                    return retLinkedHashMap;
//                case com.jd.dd.glowworm.asm.Type.MAP_ConcurrentHashMap:
//                    ConcurrentHashMap retConcurrentHashMap = new ConcurrentHashMap();
//                    addToObjectIndexMap(retConcurrentHashMap, null);
//                    tmpMapSz = scanInt();
//                    parseObject(retConcurrentHashMap, null, tmpMapSz);
//                    return retConcurrentHashMap;
                default:
                    throw new PBException("没找到对应的解析类型 " + itemType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AtomicBoolean scanAtomicBool() throws IOException {
        return new AtomicBoolean(theCodedInputStream.readBool());
    }

    public AtomicInteger scanAtomicInt() throws IOException {
        return new AtomicInteger(theCodedInputStream.readInt());
    }

    public AtomicLong scanAtomicLong() throws IOException {
        return new AtomicLong(theCodedInputStream.readLong());
    }


    public Timestamp scanTimeStamp() throws IOException {
        return new Timestamp(theCodedInputStream.readLong());
    }

    public Date scanDate() throws IOException {
        return new Date(theCodedInputStream.readLong());
    }

    public InetAddress scanInetAddress() throws IOException {
        return InetAddress.getByName(theCodedInputStream.readString());
    }

    public BigDecimal scanBigDecimal() throws IOException {
        return new BigDecimal(theCodedInputStream.readString());
    }

    public Object scanBigInteger() throws IOException {
        return new BigInteger(theCodedInputStream.readString());
    }

    public Object[] scanObjArray() {
        return ArrayDeserializer.instance.deserialize(this, Object[].class, false);
    }

    public long[] scanLongArray() throws Exception {
        int size = scanNaturalInt();
        long[] longs = new long[size];
        for (int i = 0; i < size; i++) {
            longs[i] = scanLong();
        }
        return longs;
    }

    public float[] scanFloatArray() throws Exception {
        int size = scanNaturalInt();
        float[] floats = new float[size];
        for (int i = 0; i < size; i++) {
            floats[i] = scanFloat();
        }
        return floats;
    }

    public int[] scanIntArray() throws Exception {
        int size = scanNaturalInt();
        int[] ints = new int[size];
        for (int i = 0; i < size; i++) {
            ints[i] = scanInt();
        }
        return ints;
    }

    public double[] scanDoubleArray() throws Exception {
        int size = scanNaturalInt();
        double[] doubles = new double[size];
        for (int i = 0; i < size; i++) {
            doubles[i] = scanDouble();
        }
        return doubles;
    }

    public boolean[] scanBooleanArray() throws Exception {
        byte[] tmpBytes = scanByteArray();
        boolean[] booleans = new boolean[tmpBytes.length];
        for (int i = 0; i < tmpBytes.length; i++) {
            booleans[i] = tmpBytes[i] == 1?true:false;
        }
        return booleans;
    }

    public Object getObject() {
        //todo
        return null;
    }

    public FieldDeserializer createFieldDeserializer(PBDeserializer mapping, Class<?> clazz, FieldInfo fieldInfo) {
        boolean asmEnable = true;

        if (!Modifier.isPublic(clazz.getModifiers())) {
            asmEnable = false;
        }

        if (fieldInfo.getFieldClass() == Class.class) {
            asmEnable = false;
        }

        if (ASMClassLoader.isExternalClass(clazz)) {
            asmEnable = false;
        }

        if (!asmEnable) {
            return createFieldDeserializerWithoutASM(mapping, clazz, fieldInfo);
        }

        try {
            return ASMDeserializerFactory.getInstance().createFieldDeserializer(mapping, clazz, fieldInfo);
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return createFieldDeserializerWithoutASM(mapping, clazz, fieldInfo);
    }

    public FieldDeserializer createFieldDeserializerWithoutASM(PBDeserializer mapping, Class<?> clazz, FieldInfo fieldInfo) {
        Class<?> fieldClass = fieldInfo.getFieldClass();

//        if (fieldClass == boolean.class || fieldClass == Boolean.class) {
//            return new BooleanFieldDeserializer(mapping, clazz, fieldInfo);
//        }
//
//        if (fieldClass == int.class || fieldClass == Integer.class) {
//            return new IntegerFieldDeserializer(mapping, clazz, fieldInfo);
//        }
//
//        if (fieldClass == long.class || fieldClass == Long.class) {
//            return new LongFieldDeserializer(mapping, clazz, fieldInfo);
//        }
//
//        if (fieldClass == String.class) {
//            return new StringFieldDeserializer(mapping, clazz, fieldInfo);
//        }
//
//        if (fieldClass == List.class || fieldClass == ArrayList.class) {
//            Type fieldType = fieldInfo.getFieldType();
//            if (fieldType instanceof ParameterizedType) {
//                Type itemType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
//                if (itemType == String.class) {
//                    return new ArrayListStringFieldDeserializer(mapping, clazz, fieldInfo);
//                }
//            }
//
//            return new ArrayListTypeFieldDeserializer(mapping, clazz, fieldInfo);
//        }

        return new DefaultFieldDeserializer(mapping, clazz, fieldInfo);
    }

    public ObjectDeserializer getDeserializer(FieldInfo fieldInfo) {
        return getDeserializer(fieldInfo.getFieldClass(), fieldInfo.getFieldType());
    }



}
