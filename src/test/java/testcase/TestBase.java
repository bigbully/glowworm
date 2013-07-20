package testcase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jd.dd.glowworm.PB;
import com.jd.dd.glowworm.util.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestBase {

    public static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    private boolean isForFunctionality = true;//功能测试true|性能测试false
    private boolean needCompareWithFastJSON = false;//是否需要和fastJSON对对比测试

    private int preHeatTimes = 10000;//预热次数
    private int runTimes = 10000;//总次数
    private boolean isFirstExecutation = false;//是否是第一次执行

    //执行序列化
    protected synchronized byte[] executeSerialization(Object testData) throws InterruptedException {
        byte[] result = null;
        if (isForFunctionality) {
            result = PB.toPBBytes(testData);
            StringBuilder sb = new StringBuilder();
            int headNum = result[0];
            int classNameNum = 1 + result[0];
            for (int i = 0; i < result.length; i++) {
                sb.append(result[i]).append(",");
            }
            System.out.println("---------");
            System.out.println(sb.toString().substring(0, sb.toString().length() - 1));
            return result;
        } else {
            if (!isFirstExecutation) {
                for (int preheatTimes = 10000; preheatTimes >= 0; preheatTimes--) {
                    PB.toPBBytes(testData);
                }
                isFirstExecutation = true;
            }
            Long start = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                result = PB.toPBBytes(testData);
            }
            Long end = System.currentTimeMillis();
            logger.info("测试序列化{}  {} 执行时间 {}ms", new Object[]{testData.getClass().getSimpleName(), testData.toString(), (end - start)});
            return result;
        }
    }


    //执行反序列化
    protected synchronized Object executeDeserialization(byte[] bytes) {
        Object result = null;
        if (isForFunctionality) {
            result = PB.parsePBBytes(bytes);
            return result;
        } else {
            if (!isFirstExecutation) {
                for (int i = preHeatTimes; i >= 0; i--) {
                    PB.parsePBBytes(bytes);
                }
                isFirstExecutation = true;
            }
            Long start = System.currentTimeMillis();
            for (int i = 0; i < runTimes; i++) {
                result = PB.parsePBBytes(bytes);
            }
            Long end = System.currentTimeMillis();
            logger.info("测试反序列化{}  {} 执行时间 {}ms", new Object[]{result.getClass().getSimpleName(), result.toString(), (end - start)});
            return result;
        }
    }

    //
//    protected synchronized Object executeBackAndForthWithFastJson(Object obj) {
//        return JSON.parse(JSON.toJSONBytes(obj, SerializerFeature.WriteClassName));
//    }
//
    //执行序列化+反序列化，与fastJSON的对比测试可以开启
    //并记录耗时
    protected synchronized Object executeBackAndForth(Object obj) throws InterruptedException {
        Object result = null;
        if (isForFunctionality) {
            byte[] bytes = PB.toPBBytes(obj);
            result = PB.parsePBBytes(bytes);
            return result;
        } else {
            if (needCompareWithFastJSON) {
                return compareWithFastJSON(obj);
            } else {
                if (!isFirstExecutation) {
                    for (int i = preHeatTimes; i > 0; i--) {
                        PB.parsePBBytes(PB.toPBBytes(obj));
                    }
                    isFirstExecutation = true;
                }
                Long start = System.currentTimeMillis();
                for (int i = 0; i < runTimes; i++) {
                    result = PB.parsePBBytes(PB.toPBBytes(obj));
                }
                Long end = System.currentTimeMillis();
                logger.info("测试序列化+反序列化{}  {} 执行时间 {}ms", new Object[]{result.getClass().getSimpleName(), result.toString(), (end - start)});
                return result;
            }
        }
    }
//
//
    //执行序列化+反序列化，与fastJSON的对比测试可以开启
    //并记录反序列化的耗时
    protected synchronized Object executeBackAndForth(Object obj, Parameters parameters) throws InterruptedException {
        Object result = null;
        if (isForFunctionality) {
            byte[] bytes = PB.toPBBytes(obj, parameters);
            result = PB.parsePBBytes(bytes, parameters);
            return result;
        } else {
            if (needCompareWithFastJSON) {
                return compareWithFastJSON(obj, parameters);
            } else {
                if (!isFirstExecutation) {
                    for (int i = preHeatTimes; i > 0; i--) {
                        PB.parsePBBytes(PB.toPBBytes(obj, parameters), parameters);
                    }
                    isFirstExecutation = true;
                }
                Long start = System.currentTimeMillis();
                for (int i = 0; i < runTimes; i++) {
                    result = PB.parsePBBytes(PB.toPBBytes(obj, parameters), parameters);
                }
                Long end = System.currentTimeMillis();
                logger.info("测试序列化+反序列化{}  {} 执行时间 {}ms", new Object[]{result.getClass().getSimpleName(), result.toString(), (end - start)});
                return result;
            }
        }
    }

    //执行序列化+反序列化，与fastJSON的对比测试可以开启
    //并记录耗时
    protected synchronized <T> T executeBackAndForth(Object obj, Class<T> clazz) throws InterruptedException {
        Object result = null;
        if (isForFunctionality) {
            result = PB.parsePBBytes(PB.toPBBytes(obj));
            return (T) result;
        } else {
            if (needCompareWithFastJSON) {
                return compareWithFastJSON(obj, clazz);
            } else {
                if (!isFirstExecutation) {
                    for (int i = preHeatTimes; i > 0; i--) {
                        PB.parsePBBytes(PB.toPBBytes(obj), clazz);
                    }
                    isFirstExecutation = true;
                }
                Long start = System.currentTimeMillis();
                for (int i = 0; i < runTimes; i++) {
                    result = PB.parsePBBytes(PB.toPBBytes(obj), clazz);
                }
                Long end = System.currentTimeMillis();
                logger.info("测试序列化+反序列化{}  {} 执行时间 {}ms", new Object[]{result.getClass().getSimpleName(), result.toString(), (end - start)});
                return (T) result;
            }
        }
    }


    //执行序列化+反序列化，与fastJSON的对比测试可以开启
    //并记录反序列化的耗时
    protected synchronized <T> T executeBackAndForth(Object obj, Class<T> fieldClass, Parameters parameters) throws InterruptedException {
        Object result = null;
        if (isForFunctionality) {
            result = PB.parsePBBytes(PB.toPBBytes(obj, parameters), fieldClass, parameters);
            return (T) result;
        } else {
            if (needCompareWithFastJSON) {
                return compareWithFastJSON(obj, fieldClass, parameters);
            } else {
                if (!isFirstExecutation) {
                    for (int i = preHeatTimes; i > 0; i--) {
                        PB.parsePBBytes(PB.toPBBytes(obj, parameters), fieldClass, parameters);
                    }
                    isFirstExecutation = true;
                }
                Long start = System.currentTimeMillis();
                for (int i = 0; i < runTimes; i++) {
                    result = PB.parsePBBytes(PB.toPBBytes(obj, parameters), fieldClass, parameters);
                }
                Long end = System.currentTimeMillis();
                logger.info("测试序列化+反序列化{}  {} 执行时间 {}ms", new Object[]{result.getClass().getSimpleName(), result.toString(), (end - start)});
                return (T) result;
            }
        }
    }


        private <T> T compareWithFastJSON(T obj) {
        byte[] fastJSONBytes = JSON.toJSONBytes(obj, SerializerFeature.WriteClassName);
        for (int i = 0; i < preHeatTimes; i++) {
            JSON.parse(JSON.toJSONBytes(obj, SerializerFeature.WriteClassName));
        }
        T fastJsonResult = null;
        Long startFj = System.currentTimeMillis();
        for (int i = 0; i < runTimes; i++) {
            fastJsonResult = (T) JSON.parse(JSON.toJSONBytes(obj, SerializerFeature.WriteClassName));
        }
        Long endFj = System.currentTimeMillis();

        byte[] glowWormBytes = PB.toPBBytes(obj);
        for (int i = 0; i < preHeatTimes; i++) {
            PB.parsePBBytes(PB.toPBBytes(obj));
        }
        T glowWormResult = null;
        Long startGw = System.currentTimeMillis();
        for (int i = 0; i < runTimes; i++) {
            glowWormResult = (T) PB.parsePBBytes(PB.toPBBytes(obj));
        }
        Long endGw = System.currentTimeMillis();
        logger.info("对比测试：{}  {}", new Object[]{obj.getClass().getSimpleName(), obj.toString()});
        logger.info("byte数组大小对比---fastJSON：{}   glowWorm: {}", new Object[]{fastJSONBytes.length, glowWormBytes.length});
        logger.info("速度对比----------fastJSON: {}ms glowWorm: {}ms", new Object[]{(endFj - startFj), (endGw - startGw)});
        logger.info("--------------------------------------------------------------");
        return glowWormResult;
    }
//
    private <T> T compareWithFastJSON(T obj, Parameters parameters) {
        byte[] fastJSONBytes = JSON.toJSONBytes(obj, SerializerFeature.WriteClassName);
        for (int i = 0; i < preHeatTimes; i++) {
            JSON.parse(JSON.toJSONBytes(obj, SerializerFeature.WriteClassName));
        }
        T fastJsonResult = null;
        Long startFj = System.currentTimeMillis();
        for (int i = 0; i < runTimes; i++) {
            fastJsonResult = (T) JSON.parse(JSON.toJSONBytes(obj, SerializerFeature.WriteClassName));
        }
        Long endFj = System.currentTimeMillis();

        byte[] glowWormBytes = PB.toPBBytes(obj, parameters);
        for (int i = 0; i < preHeatTimes; i++) {
            PB.parsePBBytes(PB.toPBBytes(obj, parameters), parameters);
        }
        T glowWormResult = null;
        Long startGw = System.currentTimeMillis();
        for (int i = 0; i < runTimes; i++) {
            glowWormResult = (T) PB.parsePBBytes(PB.toPBBytes(obj, parameters), parameters);
        }
        Long endGw = System.currentTimeMillis();
        logger.info("对比测试：{}  {}", new Object[]{obj.getClass().getSimpleName(), obj.toString()});
        logger.info("byte数组大小对比---fastJSON：{}   glowWorm: {}", new Object[]{fastJSONBytes.length, glowWormBytes.length});
        logger.info("速度对比----------fastJSON: {}ms glowWorm: {}ms", new Object[]{(endFj - startFj), (endGw - startGw)});
        logger.info("--------------------------------------------------------------");
        return glowWormResult;
    }


    private <T> T compareWithFastJSON(Object obj, Class<T> clazz) {
        byte[] fastJSONBytes = JSON.toJSONBytes(obj, SerializerFeature.WriteClassName);
        for (int i = 0; i < preHeatTimes; i++) {
            JSON.parse(JSON.toJSONBytes(obj, SerializerFeature.WriteClassName));
        }
        T fastJsonResult = null;
        Long startFj = System.currentTimeMillis();
        for (int i = 0; i < runTimes; i++) {
            fastJsonResult = (T) JSON.parse(JSON.toJSONBytes(obj, SerializerFeature.WriteClassName));
        }
        Long endFj = System.currentTimeMillis();

        byte[] glowWormBytes = PB.toPBBytes(obj);
        for (int i = 0; i < preHeatTimes; i++) {
            PB.parsePBBytes(PB.toPBBytes(obj), clazz);
        }
        T glowWormResult = null;
        Long startGw = System.currentTimeMillis();
        for (int i = 0; i < runTimes; i++) {
            glowWormResult = PB.parsePBBytes(PB.toPBBytes(obj), clazz);
        }
        Long endGw = System.currentTimeMillis();
        logger.info("对比测试：{}  {}", new Object[]{obj.getClass().getSimpleName(), obj.toString()});
        logger.info("byte数组大小对比---fastJSON：{}   glowWorm: {}", new Object[]{fastJSONBytes.length, glowWormBytes.length});
        logger.info("速度对比----------fastJSON: {}ms glowWorm: {}ms", new Object[]{(endFj - startFj), (endGw - startGw)});
        logger.info("--------------------------------------------------------------");
        return glowWormResult;
    }

    private <T> T compareWithFastJSON(Object obj, Class<T> clazz, Parameters parameters) {
        byte[] fastJSONBytes = JSON.toJSONBytes(obj, SerializerFeature.WriteClassName);
        for (int i = 0; i < preHeatTimes; i++) {
            JSON.parse(JSON.toJSONBytes(obj, SerializerFeature.WriteClassName));
        }
        T fastJsonResult = null;
        Long startFj = System.currentTimeMillis();
        for (int i = 0; i < runTimes; i++) {
            fastJsonResult = (T) JSON.parse(JSON.toJSONBytes(obj, SerializerFeature.WriteClassName));
        }
        Long endFj = System.currentTimeMillis();

        byte[] glowWormBytes = PB.toPBBytes(obj, parameters);
        for (int i = 0; i < preHeatTimes; i++) {
            PB.parsePBBytes(PB.toPBBytes(obj, parameters), clazz, parameters);
        }
        T glowWormResult = null;
        Long startGw = System.currentTimeMillis();
        for (int i = 0; i < runTimes; i++) {
            glowWormResult = PB.parsePBBytes(PB.toPBBytes(obj, parameters), clazz, parameters);
        }
        Long endGw = System.currentTimeMillis();
        logger.info("对比测试：{}  {}", new Object[]{obj.getClass().getSimpleName(), obj.toString()});
        logger.info("byte数组大小对比---fastJSON：{}   glowWorm: {}", new Object[]{fastJSONBytes.length, glowWormBytes.length});
        logger.info("速度对比----------fastJSON: {}ms glowWorm: {}ms", new Object[]{(endFj - startFj), (endGw - startGw)});
        logger.info("--------------------------------------------------------------");
        return glowWormResult;
    }

    protected static int encodeZigZag32(int n) {
        // Note: the right-shift must be arithmetic
        return (n << 1) ^ (n >> 31);
    }

    protected static long encodeZigZag64(long n) {
        // Note: the right-shift must be arithmetic
        return (n << 1) ^ (n >> 63);
    }

    protected Byte[] writeRawVarint32(int value) {
        List<Byte> list = new ArrayList<Byte>();
        while (true) {
            if ((value & ~0x7F) == 0) {
                list.add((byte) value);
                Byte[] bytes = new Byte[list.size()];
                return list.toArray(bytes);
            } else {
                list.add((byte) ((value & 0x7F) | 0x80));
                value >>>= 7;
            }
        }
    }

    protected Byte[] writeRawVarint64(long value) {
        List<Byte> list = new ArrayList<Byte>();
        while (true) {
            if ((value & ~0x7FL) == 0) {
                list.add((byte) (int) value);
                Byte[] bytes = new Byte[list.size()];
                return list.toArray(bytes);
            } else {
                list.add((byte) (((int) value & 0x7F) | 0x80));
                value >>>= 7;
            }
        }
    }

    protected void printByteArray(byte[] result) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.length; i++) {
            sb.append(result[i] + ", ");
        }
        System.out.println(sb.substring(0, sb.length() - 2).toString());
    }

    protected byte[] getBytesFromLong(long longValueParm) {
        byte[] returnInt = new byte[8];
        returnInt[0] = (byte) ((longValueParm >> 56) & 0xFF);
        returnInt[1] = (byte) ((longValueParm >>> 48) & 0xFF);
        returnInt[2] = (byte) ((longValueParm >>> 40) & 0xFF);
        returnInt[3] = (byte) ((longValueParm >>> 32) & 0xFF);
        returnInt[4] = (byte) ((longValueParm >>> 24) & 0xFF);
        returnInt[5] = (byte) ((longValueParm >>> 16) & 0xFF);
        returnInt[6] = (byte) ((longValueParm >>> 8) & 0xFF);
        returnInt[7] = (byte) ((longValueParm >>> 0) & 0xFF);
        return returnInt;
    }

    protected byte[] writeRawFloat(Float f) {
        int i = Float.floatToRawIntBits(f);
        return new byte[]{(byte) (i & 0xFF), (byte) ((i >> 8) & 0xFF), (byte) ((i >> 16) & 0xFF), (byte) ((i >> 24) & 0xFF)};
    }

    //考虑所有头信息不超过127的情况
    //list[0] refArray
    //list[1] existArray
    //list[2] typeHeadArray
    //list[3] typeArray
    //list[4] contentBytes
    protected List<byte[]> getHeadBytesArray(byte[] result) {
        List<byte[]> list = new ArrayList<byte[]>();
        int refLength = result[0];
        byte[] refArray = new byte[refLength];
        System.arraycopy(result, 0, refArray, 0, refLength);
        list.add(refArray);
        int existLength = result[1 + refLength];
        byte[] existArray = new byte[existLength];
        System.arraycopy(result, 1 + refLength + 1, existArray, 0, existLength);
        list.add(existArray);
        int typeHeadLength = result[1 + refLength + 1 + existLength];
        byte[] typeHeadArray = new byte[typeHeadLength];
        System.arraycopy(result, 1 + refLength + 1 + existLength + 1, typeHeadArray, 0, typeHeadLength);
        list.add(typeHeadArray);
        int typeLength = result[1 + refLength + 1 + existLength + 1 + typeHeadLength];
        byte[] typeArray = new byte[typeLength];
        System.arraycopy(result, 1 + refLength + 1 + existLength + 1 + typeHeadLength + 1, typeArray, 0, typeLength);
        list.add(typeArray);
        int contentLength = result.length - 4 - refLength - existLength - typeHeadLength - typeLength;
        byte[] contentBytes = new byte[contentLength];
        System.arraycopy(result, 1 + refLength + 1 + existLength + 1 + typeHeadLength + 1 + typeLength, contentBytes, 0, contentLength);
        list.add(contentBytes);
        //打印content信息
        System.out.println("content内容为：");
        printByteArray(contentBytes);
        return list;
    }

}
