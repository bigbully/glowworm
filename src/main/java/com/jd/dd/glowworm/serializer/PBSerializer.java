package com.jd.dd.glowworm.serializer;

import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.serializer.asm.ASMSerializerFactory;
import com.jd.dd.glowworm.serializer.multi.ArraySerializer;
import com.jd.dd.glowworm.serializer.multi.ListSerializer;
import com.jd.dd.glowworm.serializer.multi.MapSerializer;
import com.jd.dd.glowworm.serializer.multi.SetSerializer;
import com.jd.dd.glowworm.serializer.normal.*;
import com.jd.dd.glowworm.serializer.primary.*;
import com.jd.dd.glowworm.serializer.reflect.JavaBeanSerializer;
import com.jd.dd.glowworm.util.*;
import com.jd.dd.glowworm.util.IdentityHashMap;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PBSerializer {

    private static IdentityHashMap theSerializerHMap = new IdentityHashMap();//序列化器的缓存

    private OutputStreamBuffer outputStreamBuffer;//其中引用4个流，2个CodedOutputStream分别用来存引用和内容,2个OutputBitStream用来存null和type
    private CodedOutputStream theCodedOutputStream;//写入不同类型对象的逻辑
    private CodedOutputStream refStream;//引用的buffer
    private ExistOutputStream existStream;//是否非空的buffer
    private TypeOutputStream typeStream;//保存type的buffer
    private CodedOutputStream headStream;//保存整个头信息
    private Parameters parameters;//一些参数信息
    private final static ThreadLocal<SoftReference<OutputStreamBuffer>> bufLocal = new ThreadLocal<SoftReference<OutputStreamBuffer>>();//buf的缓存
    private List<SerializeContext> objectIndexList;//用来保存所有和javabeean, Array, List, Set, Map相关的对象-index键值对
    private HashMap<Integer, Integer> refMap;//key = 当前元素的index（不重复）, value=引用的index；
    private int currentIndex;

    static {
        initTheSerializerHMap();
    }

    //序列化一个Object,所有序列化的入口
    public void write(Object object) {
        Class<?> clazz = object.getClass();
        ObjectSerializer writer = getObjectWriter(clazz);

        if (needConsiderRef(writer)) {
            addObjectIndexList(object);
        }
        try {
            writer.write(this, object, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initTheSerializerHMap() {
        theSerializerHMap.put(Boolean.class, BooleanSerializer.instance);
        theSerializerHMap.put(Byte.class, ByteSerializer.instance);
        theSerializerHMap.put(Short.class, ShortSerializer.instance);
        theSerializerHMap.put(Character.class, CharacterSerializer.instance);
        theSerializerHMap.put(Integer.class, IntegerSerializer.instance);
        theSerializerHMap.put(Long.class, LongSerializer.instance);
        theSerializerHMap.put(Float.class, FloatSerializer.instance);
        theSerializerHMap.put(Double.class, DoubleSerializer.instance);

        theSerializerHMap.put(BigDecimal.class, BigDecimalSerializer.instance);
        theSerializerHMap.put(BigInteger.class, BigIntegerSerializer.instance);
        theSerializerHMap.put(Date.class, DateSerializer.instance);
        theSerializerHMap.put(Timestamp.class, TimestampSerializer.instance);
        theSerializerHMap.put(Inet4Address.class, InetAddressSerializer.instance);
        theSerializerHMap.put(Inet6Address.class, InetAddressSerializer.instance);
        theSerializerHMap.put(String.class, StringSerializer.instance);

        theSerializerHMap.put(Class.class, ClassSerializer.instance);

        theSerializerHMap.put(byte[].class, ByteArraySerializer.instance);
        theSerializerHMap.put(short[].class, ShortArraySerializer.instance);
        theSerializerHMap.put(int[].class, IntArraySerializer.instance);
        theSerializerHMap.put(long[].class, LongArraySerializer.instance);
        theSerializerHMap.put(float[].class, FloatArraySerializer.instance);
        theSerializerHMap.put(double[].class, DoubleArraySerializer.instance);
        theSerializerHMap.put(boolean[].class, BooleanArraySerializer.instance);
        theSerializerHMap.put(char[].class, CharArraySerializer.instance);

        theSerializerHMap.put(AtomicInteger.class, AtomicIntegerSerializer.instance);
        theSerializerHMap.put(AtomicBoolean.class, AtomicBooleanSerializer.instance);
        theSerializerHMap.put(AtomicLong.class, AtomicLongSerializer.instance);

    }

    public PBSerializer() {
        initBuffer();
    }

    //初始化CodedOutputStream
    private void initBuffer() {
        SoftReference<OutputStreamBuffer> ref = bufLocal.get();

        if (ref != null) {
            outputStreamBuffer = ref.get();
            theCodedOutputStream = outputStreamBuffer.getTheCodedOutputStream();
            refStream = outputStreamBuffer.getRefStream();
            typeStream = outputStreamBuffer.getTypeStream();
            existStream = outputStreamBuffer.getExistStream();
            headStream = outputStreamBuffer.getHeadStream();
            bufLocal.set(null);
        }

        if (theCodedOutputStream == null) {
            theCodedOutputStream = new CodedOutputStream(new BufferOutputStream(1024));
        }
        if (refStream == null) {
            refStream = new CodedOutputStream(new BufferOutputStream(30));
        }
        if (typeStream == null) {
            typeStream = new TypeOutputStream(50);
        }
        if (existStream == null) {
            existStream = new ExistOutputStream(100);
        }
        if (headStream == null) {
            headStream = new CodedOutputStream(new BufferOutputStream(10));
        }
    }

    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        ObjectSerializer writer = (ObjectSerializer) theSerializerHMap.get(clazz);

        if (writer == null) {
            if (clazz.isArray()) {
                return ArraySerializer.instance;
            } else if (Map.class.isAssignableFrom(clazz)) {
                return MapSerializer.instance;
            } else if (List.class.isAssignableFrom(clazz)) {
                return ListSerializer.instance;
            } else if (Set.class.isAssignableFrom(clazz)){
                return SetSerializer.instance;
            } else if (clazz.isEnum() || (clazz.getSuperclass() != null && clazz.getSuperclass().isEnum())) {
                return EnumSerializer.instance;
            } else {
                try {
                    theSerializerHMap.put(clazz, createJavaBeanSerializer(clazz));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            writer = (ObjectSerializer) theSerializerHMap.get(clazz);
        }
        return writer;
    }

    private Object createJavaBeanSerializer(Class<?> clazz) {
        if (!Modifier.isPublic(clazz.getModifiers())) {
            return new JavaBeanSerializer(clazz);
        }

        boolean asm = true;

        if (asm && ASMClassLoader.isExternalClass(clazz) || clazz == Serializable.class || clazz == Object.class) {
            asm = false;
        }

        if (asm) {
            try {
                return ASMSerializerFactory.getInstance().createJavaBeanSerializer(clazz);
            } catch (Throwable e) {
                throw new PBException("create asm serializer error, class " + clazz, e);
            }
        }

        return new JavaBeanSerializer(clazz);
    }

    //组装byte数组
    public byte[] createByteArray() {
        byte[] src = getHead();
        byte[] des = getCodedOutputStream().getBytes();
        if (des != null) {
            byte[] result = new byte[src.length + des.length];
            System.arraycopy(src, 0, result, 0, src.length);
            System.arraycopy(des, 0, result, src.length, des.length);
            return result;
        } else {
            return src;
        }
    }

    public byte[] getHead() {
        writeEachStreamBytes(getRefByte());//写入refBytes
        writeEachStreamBytes(existStream.getBytes());//写入existBytes
        writeEachStreamBytes(typeStream.getHeadBytes());//写入typeHeadBytes
        writeEachStreamBytes(typeStream.getBytes());//写入typeBytes
        return headStream.getBytes();
    }

    private void writeEachStreamBytes(byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            headStream.writeNaturalInt(bytes.length);
            headStream.writeRawBytes(bytes);
        } else {
            headStream.writeRawByte(0);
        }
    }

    //关闭时缓存buffer
    public void close() {
        theCodedOutputStream.reset();
        refStream.reset();
        existStream.reset();
        typeStream.reset();
        typeStream.headReset();
        headStream.reset();

        if (outputStreamBuffer == null) {
            outputStreamBuffer = new OutputStreamBuffer(theCodedOutputStream, refStream, existStream, typeStream, headStream);
        } else {
            outputStreamBuffer.setAll(theCodedOutputStream, refStream, existStream, typeStream, headStream);
        }

        bufLocal.set(new SoftReference<OutputStreamBuffer>(outputStreamBuffer));

        theCodedOutputStream = null;
        refStream = null;
        existStream = null;
        typeStream = null;

        outputStreamBuffer = null;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }



    //组装引用byte数组
    private byte[] getRefByte() {
        if (refMap == null) {
            return null;
        } else {
            refStream.writeNaturalInt(refMap.size());
            for (Map.Entry<Integer, Integer> entry : refMap.entrySet()) {
                refStream.writeNaturalInt(entry.getKey());
                refStream.writeNaturalInt(entry.getValue());
            }
        }
        return refStream.getBytes();
    }

    public CodedOutputStream getCodedOutputStream() {
        return theCodedOutputStream;
    }

    //写入type
    public void writeType(int typeCode) {
        typeStream.write(typeCode);
    }

    //业务方法如下
    public void writeNaturalInt(int i) {
        theCodedOutputStream.writeNaturalInt(i);
    }

    public void writeInt(int i) {
        theCodedOutputStream.writeInt(i);
    }

    public void writeLong(long l) {
        theCodedOutputStream.writeLong(l);
    }

    public void writeDouble(double d) {
        theCodedOutputStream.writeDouble(d);
    }

    public void writeByte(byte b) {
        theCodedOutputStream.writeRawByte(b);
    }

    public void writeFloat(float f) {
        theCodedOutputStream.writeFloat(f);
    }

    public void writeShort(short s) {
        theCodedOutputStream.writeInt(s);
    }

    public void writeBool(boolean b) {
        theCodedOutputStream.writeRawByte(b ? 1 : 0);
    }

    //写入不知名的Object对象，作为UserJavaBean的属性
    public void writeFieldObject(Object object, boolean needWriteType) {
        try {
            if (object == null) {
                writeNull();
            } else {
                Class<?> clazz = object.getClass();
                ObjectSerializer writer = getObjectWriter(clazz);

                if (needConsiderRef(writer) && this.isReference(object)) {
                    writeNull();
                } else {
                    writeNotNull();
                    if (isAsmJavaBean(writer) && needWriteType) {//如果是asm,把类名写上
                        writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                        writeString(clazz.getName());
                    }
                    writer.write(this, object, needWriteType);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //------------------------------所有基本类型的array处理-------------------//
    public void writeBooleanArray(boolean[] array) {
        byte[] tmpBytes = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            tmpBytes[i] = (byte) (array[i] ? 1 : 0);
        }
        theCodedOutputStream.writeNaturalInt(array.length);
        theCodedOutputStream.writeRawBytes(tmpBytes);
    }

    public void writeByteArray(byte[] array) {
        theCodedOutputStream.writeNaturalInt(array.length);
        theCodedOutputStream.writeRawBytes(array);
    }

    public void writeString(String s) {
        theCodedOutputStream.writeString(s);
    }

    public void writeStringWithCharset(String s){
        if (parameters != null){
            theCodedOutputStream.writeString(s, parameters.getCharset());
        }else {
            theCodedOutputStream.writeString(s);
        }
    }

    public void writeDoubleArray(double[] array) {
        theCodedOutputStream.writeNaturalInt(array.length);
        for (int i = 0; i < array.length; ++i) {
            theCodedOutputStream.writeDouble(array[i]);
        }
    }

    public void writeFloatArray(float[] array) {
        theCodedOutputStream.writeNaturalInt(array.length);
        for (int i = 0; i < array.length; ++i) {
            theCodedOutputStream.writeFloat(array[i]);
        }
    }

    public void writeIntArray(int[] array) {
        theCodedOutputStream.writeNaturalInt(array.length);
        for (int i = 0; i < array.length; ++i) {
            theCodedOutputStream.writeInt(array[i]);
        }
    }

    public void writeLongArray(long[] array) {
        theCodedOutputStream.writeNaturalInt(array.length);
        for (int i = 0; i < array.length; ++i) {
            theCodedOutputStream.writeLong(array[i]);
        }
    }

    public void writeShortArray(short[] array) {
        theCodedOutputStream.writeNaturalInt(array.length);
        for (int i = 0; i < array.length; ++i) {
            theCodedOutputStream.writeInt(array[i]);
        }
    }

    ///---------------是否存在的判断------------------//
    public void writeNull() {
        currentIndex++;
        existStream.write(false);
    }

    public void writeNotNull() {
        existStream.write(true);
    }

    //--------------以下是引用的处理--------------------//
    public boolean isReference(Object item) {
        boolean flag;
        currentIndex++;
        SerializeContext serializeContext = getSerializeContextObj(item);
        //任何做过判断的对象，放入object-index的MAP中，index从0开始递增
        if (objectIndexList == null){
            objectIndexList = new ArrayList<SerializeContext>();
        }
        objectIndexList.add(new SerializeContext(item, currentIndex));
        if (serializeContext != null) {//判断是否是引用
            if (refMap == null) {
                refMap = new HashMap<Integer, Integer>();
            }
            //如果是引用，添加在应用list中,待后续处理
            refMap.put(currentIndex, serializeContext.getIndex());
            currentIndex--;//考虑确定每个引用之后writeNull的时候会index++，由于之前已经加过一次了，所以这里还原，等待writeNull的时候再增加
            flag = true;
        } else {//不是引用
            flag = false;
        }
        return flag;
    }

    private SerializeContext getSerializeContextObj(Object object) {
        if (objectIndexList == null) return null;
        for (SerializeContext serializeContext : objectIndexList) {
            if (serializeContext.getObj() == object) {
                return serializeContext;
            }
        }
        return null;
    }

    //增加一个对象到引用List中
    private void addObjectIndexList(Object object) {
        if (objectIndexList == null){
            objectIndexList = new ArrayList<SerializeContext>();
        }
        objectIndexList.add(new SerializeContext(object,currentIndex));
    }

    //是否考虑引用
    public boolean needConsiderRef(ObjectSerializer writer) {
        if (writer == null){//如果不是在序列化器里进行序列化的，则直接传null。现在list,set是这么做的
            return true;
        }
        Class clazz = writer.getClass();
        if (JavaBeanSerializer.class.isAssignableFrom(clazz)) {
            return true;
        } else if (isAsmJavaBean(writer)) {
            return true;
        } else if (ListSerializer.class.isAssignableFrom(clazz)) {
            return true;
        } else if (MapSerializer.class.isAssignableFrom(clazz)) {
            return true;
        } else if (ArraySerializer.class.isAssignableFrom(clazz)) {
            return true;
        } else if (SetSerializer.class.isAssignableFrom(clazz)) {
            return true;
        } else {
            return false;
        }
    }

    //---------------------工具方法-------------------//
    public boolean isAsmJavaBean(ObjectSerializer writer) {
        return writer.getClass().getName().startsWith(ASMSerializerFactory.GenClassName_prefix);
    }



}
