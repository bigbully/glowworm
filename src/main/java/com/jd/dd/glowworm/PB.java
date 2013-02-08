package com.jd.dd.glowworm;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

public class PB {

    /**
     * 对象序列化的方法
     *
     * @param object 需要序列化的对象
     * @return byte[] 序列化之后的byte数组
     */
    public static byte[] toPBBytes(Object object) {
        if (object == null) {
            throw new PBException("不能序列化NULL！");
        }
        PBSerializer serializer = new PBSerializer();
        try {
            serializer.write(object);
            return serializer.createByteArray();
        } finally {
            serializer.close();
        }
    }

    /**
          * 对象反序列化的方法
          * @param bytes 反序列化传入的byte数组
          * @param clazz 需要反序列化的类,不能是接口。必须是实现类。
          * @param <T> 泛型参数，无需强转类型
          * @return clazz对应的对象
         */
    public static <T> T parsePBBytes(byte[] bytes, Class<T> clazz) {
        if (bytes == null){
            throw new PBException("不能反序列化空byte数组!");
        }
        PBDeserializer deserializer = new PBDeserializer();
        try {
            //分析数组头
            deserializer.analysizeHead(bytes);
            return (T)deserializeObj(deserializer, clazz);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }finally {
            deserializer.close();
        }
    }

    /**
     * 反序列化的方法
     * @param bytes 反序列化传入的byte数组
     * @return 返回object，需要强转。
     */
    public static Object parsePBBytes(byte[] bytes) {
        if (bytes == null){
            throw new PBException("不能反序列化空byte数组!");
        }
        PBDeserializer deserializer = new PBDeserializer();

        Class fieldClass = null;
        try {
            //分析数组头
            deserializer.analysizeHead(bytes);
            return deserializeObj(deserializer, fieldClass);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }finally {
            deserializer.close();
        }
    }

//    /**
//     * 对象序列化的方法(带压缩的)
//     * @param objectParm 需要序列化的对象
//     * @return byte[] 序列化之后的byte数组
//     */
//    public static byte[] toPBBytes_Compress(Object objectParm) {
//        byte[] tmpRowBytes = toPBBytes(objectParm);
//        byte[] tmpCompressBytes = null;
//
//        try {
//            tmpCompressBytes = Snappy.compress(tmpRowBytes);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return tmpCompressBytes;
//    }
//
//    /**
//     * 对象反序列化的方法（带压缩）
//     * @param pbCompressBytesParm 反序列化传入的byte数组
//     * @param fieldClass 需要反序列化的类
//     * @return fieldClass的对象
//     */
//    public static Object parsePBBytes_Compress(byte[] pbCompressBytesParm, Class<?> fieldClass) {
//        Object retObj = null;
//
//        try {
//            byte[] tmpRowBytes = Snappy.uncompress(pbCompressBytesParm);
//            retObj = parsePBBytes(tmpRowBytes, fieldClass);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return retObj;
//    }
//
//
//

//
//    /**
//     * 反序列化的方法带压缩
//     * @param pbCompressBytesParm 反序列化传入的byte数组
//     * @return object需要强转
//     */
//    public static Object parsePBBytes_Compress(byte[] pbCompressBytesParm) {
//        Object retObj = null;
//
//        try {
//            byte[] tmpRowBytes = Snappy.uncompress(pbCompressBytesParm);
//            retObj = parsePBBytes(tmpRowBytes);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return retObj;
//    }
//
    private static Object deserializeObj(PBDeserializer deserializer, Class<?> clazz) {
        ObjectDeserializer objectDeserializer = deserializer.getDeserializer(clazz);
        Class parserClazz = objectDeserializer.getClass();
        if (!deserializer.isUserJavaBean(parserClazz)) {
            try {
                deserializer.scanType();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Object retObj = objectDeserializer.deserialize(deserializer, clazz, false);
        return retObj;
    }

//
//
//    //-----------------------------------------带Parameters参数的--------------------
//
//    /**
//     * 反序列化的方法，带参数
//     * @param pbBytesParm 反序列化传入的byte
//     * @param fieldClass 反序列化传入的类型，不能是接口类型
//     * @param parameters 如果需要选择编码类型，需要写入类型，则创建Parameters对象
//     * @param <T>
//     * @return 返回值是fieldClass的实例
//     */
//    public static <T> T parsePBBytes(byte[] pbBytesParm, Class<T> fieldClass, Parameters parameters) {
//        if (pbBytesParm == null){
//            throw new PBException("不能反序列化空byte数组!");
//        }
//        PBDeserializer tmpPBDeserializer = new PBDeserializer(pbBytesParm);
//        tmpPBDeserializer.setParameters(parameters);
//        try {
//            //分析数组头
//            if (tmpPBDeserializer.hasHead(pbBytesParm)){
//                tmpPBDeserializer.analysizeHead();
//            }else {
//                tmpPBDeserializer.scanByte();
//            }
//            if (parameters.getNeedWriteClassName()){
//                String tmpClassName = tmpPBDeserializer.scanString();
//            }
//            return (T)deserializeObj(tmpPBDeserializer, fieldClass);
//        }catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }finally {
//            tmpPBDeserializer.close();
//        }
//    }
//
//    /**
//     * 反序列化的方法，带参数
//     * @param pbBytesParm 反序列化传入的byte数组
//     * @param parameters 如果需要选择编码类型，需要写入类型，则创建Parameters对象
//     * @param <T>
//     * @return object，需要强转
//     */
//    public static <T> T parsePBBytes(byte[] pbBytesParm, Parameters parameters) {
//        if (pbBytesParm == null){
//            throw new PBException("不能反序列化空byte数组!");
//        }
//        PBDeserializer tmpPBDeserializer = new PBDeserializer(pbBytesParm);
//        tmpPBDeserializer.setParameters(parameters);
//        Class fieldClass = null;
//        try {
//            //分析数组头
//            if (tmpPBDeserializer.hasHead(pbBytesParm)){
//                tmpPBDeserializer.analysizeHead();
//            }else {
//                tmpPBDeserializer.scanByte();
//            }
//            if (parameters.getNeedWriteClassName()){
//                String tmpClassName = tmpPBDeserializer.scanString();
//                fieldClass = TypeUtils.loadClass(tmpClassName);
//            }
//            return (T)deserializeObj(tmpPBDeserializer, fieldClass);
//        }catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }finally {
//            tmpPBDeserializer.close();
//        }
//
//    }
//
//    /**
//     * 序列化方法，带参数
//     * @param objectParm 需要序列化的对象
//     * @param parameters 如果需要选择编码类型，需要写入类型，则创建Parameters对象
//     * @return 序列化生成的byte数组
//     */
//    public static byte[] toPBBytes(Object objectParm, Parameters parameters) {
//        SerializeWriter tmpSerializeWriter = new SerializeWriter();
//        try {
//
//            PBSerializer tmpPBSerializer = new PBSerializer(tmpSerializeWriter);
//            tmpPBSerializer.setParameters(parameters);
//
//            if (parameters.getNeedWriteClassName()){
//                // 对象类型字符串
//                Class<?> clazz = objectParm.getClass();
//                tmpSerializeWriter.getCodedOutputStream().writeString(clazz.getName());
//            }
//
//            tmpPBSerializer.write(objectParm);
//            return tmpPBSerializer.createByteArray();
//        }finally {
//            tmpSerializeWriter.close();
//        }
//
//    }
}
