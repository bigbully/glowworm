package com.jd.dd.glowworm.util;

import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 *
 */
public class TypeUtils {
    private static Map<String, Class<?>> mappings = new HashMap<String, Class<?>>();

    static {
        mappings.put("byte", byte.class);
        mappings.put("short", short.class);
        mappings.put("int", int.class);
        mappings.put("long", long.class);
        mappings.put("float", float.class);
        mappings.put("double", double.class);
        mappings.put("boolean", boolean.class);
        mappings.put("char", char.class);

        mappings.put("[byte", byte[].class);
        mappings.put("[short", short[].class);
        mappings.put("[int", int[].class);
        mappings.put("[long", long[].class);
        mappings.put("[float", float[].class);
        mappings.put("[double", double[].class);
        mappings.put("[boolean", boolean[].class);
        mappings.put("[char", char[].class);
    }

    public static Class<?> loadClass(String className) {
        if (className == null || className.length() == 0) {
            return null;
        }

        Class<?> clazz = mappings.get(className);

        if (clazz != null) {
            return clazz;
        }

        /*if (className.charAt(0) == '[') {
            Class<?> componentType = loadClass(className.substring(1));
            return Array.newInstance(componentType, 0).getClass();
        }*/

        try {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            mappings.put(className, clazz);
            return clazz;
        } catch (Throwable e) {
            // skip
            //e.printStackTrace();
        }

        try {
            clazz = Class.forName(className);
            //mappings.put(className, clazz);
            return clazz;
        } catch (Throwable e) {
            // skip
            //e.printStackTrace();
        }

        return clazz;
    }

    public static List<FieldInfo> computeGetters(Class<?> clazz, Map<String, String> aliasMap) {
        return computeGetters(clazz, aliasMap, true);
    }

    public static List<FieldInfo> computeGetters(Class<?> clazz, Map<String, String> aliasMap, boolean sorted) {
        Map<String, FieldInfo> fieldInfoMap = new LinkedHashMap<String, FieldInfo>();

        for (Method method : clazz.getMethods()) {
            String methodName = method.getName();

            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            if (method.getReturnType().equals(Void.TYPE)) {
                continue;
            }

            if (method.getParameterTypes().length != 0) {
                continue;
            }

            if (method.getReturnType() == ClassLoader.class) {
                continue;
            }

            if (method.getName().equals("getMetaClass")
                    && method.getReturnType().getName().equals("groovy.lang.MetaClass")) {
                continue;
            }

            if (methodName.startsWith("get")) {
                if (methodName.length() < 4) {
                    continue;
                }

                if (methodName.equals("getClass")) {
                    continue;
                }

                if (!Character.isUpperCase(methodName.charAt(3))) {
                    continue;
                }

                //如果有瞬时get方法，去除
                if (method.getAnnotation(Transient.class) != null) {
                    continue;
                }

                String propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);

                Field field = ASMUtils.getField(clazz, propertyName);

                if (aliasMap != null) {
                    propertyName = aliasMap.get(propertyName);
                    if (propertyName == null) {
                        continue;
                    }
                }
                //如果有瞬时变量，去除
                if (field != null && field.getAnnotation(Transient.class) != null) {
                    continue;
                }

                fieldInfoMap.put(propertyName, new FieldInfo(propertyName, method, field));
            }

            if (methodName.startsWith("is")) {
                if (methodName.length() < 3) {
                    continue;
                }

                if (!Character.isUpperCase(methodName.charAt(2))) {
                    continue;
                }
                //如果有瞬时is方法，去除
                if (method.getAnnotation(Transient.class) != null) {
                    continue;
                }

                String propertyName = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);

                Field field = ASMUtils.getField(clazz, propertyName);
                if (field != null) {
                    if (aliasMap != null) {
                        propertyName = aliasMap.get(propertyName);
                        if (propertyName == null) {
                            continue;
                        }
                    }

                    //如果有瞬时变量，去除
                    if (field.getAnnotation(Transient.class) != null) {
                        continue;
                    }

                    fieldInfoMap.put(propertyName, new FieldInfo(propertyName, method, field));
                }
            }
        }

        for (Field field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            if (!Modifier.isPublic(field.getModifiers())) {
                continue;
            }

            if (!fieldInfoMap.containsKey(field.getName())) {
                fieldInfoMap.put(field.getName(), new FieldInfo(field.getName(), null, field));
            }
        }

        List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();

        boolean containsAll = false;
        String[] orders = null;

        if (containsAll) {
            for (String item : orders) {
                FieldInfo fieldInfo = fieldInfoMap.get(item);
                fieldInfoList.add(fieldInfo);
            }
        } else {
            for (FieldInfo fieldInfo : fieldInfoMap.values()) {
                fieldInfoList.add(fieldInfo);
            }

            if (sorted) {
                Collections.sort(fieldInfoList);
            }
        }

        return fieldInfoList;
    }

    public static Class<?> getClass(Type type) {
        if (type.getClass() == Class.class) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        }

        return Object.class;
    }

    @SuppressWarnings("unchecked")
    public static final <T> T cast(Object obj, Type type, PBDeserializer mapping) {
        if (obj == null) {
            return null;
        }

        if (type instanceof Class) {
            return (T) cast(obj, (Class<T>) type, mapping);
        }

        if (type instanceof ParameterizedType) {
            return (T) cast(obj, (ParameterizedType) type, mapping);
        }

        if (obj instanceof String) {
            String strVal = (String) obj;
            if (strVal.length() == 0) {
                return null;
            }
        }

        if (type instanceof TypeVariable) {
            return (T) obj;
        }

        throw new PBException("can not cast to : " + type);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final <T> T cast(Object obj, Class<T> clazz, PBDeserializer mapping) {
        if (obj == null) {
            return null;
        }

        if (clazz == obj.getClass()) {
            return (T) obj;
        }

        if (obj instanceof Map) {
            /*if (clazz == Map.class) {
                return (T) obj;
            }

            return castToJavaBean((Map<String, Object>) obj, clazz, mapping);*/
            return (T) obj;
        }

        if (clazz.isArray()) {
            if (obj instanceof Collection) {

                Collection collection = (Collection) obj;
                int index = 0;
                Object array = Array.newInstance(clazz.getComponentType(), collection.size());
                for (Object item : collection) {
                    Object value = cast(item, clazz.getComponentType(), mapping);
                    Array.set(array, index, value);
                    index++;
                }

                return (T) array;
            }
        }

        if (clazz.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }

        if (clazz == boolean.class || clazz == Boolean.class) {
            return (T) castToBoolean(obj);
        }

        if (clazz == byte.class || clazz == Byte.class) {
            return (T) castToByte(obj);
        }

        // if (clazz == char.class || clazz == Character.class) {
        // return (T) castToCharacter(obj);
        // }

        if (clazz == short.class || clazz == Short.class) {
            return (T) castToShort(obj);
        }

        if (clazz == int.class || clazz == Integer.class) {
            return (T) castToInt(obj);
        }

        if (clazz == long.class || clazz == Long.class) {
            return (T) castToLong(obj);
        }

        if (clazz == float.class || clazz == Float.class) {
            return (T) castToFloat(obj);
        }

        if (clazz == double.class || clazz == Double.class) {
            return (T) castToDouble(obj);
        }

        if (clazz == String.class) {
            return (T) castToString(obj);
        }

        if (clazz == BigDecimal.class) {
            return (T) castToBigDecimal(obj);
        }

        if (clazz == BigInteger.class) {
            return (T) castToBigInteger(obj);
        }

        /*if (clazz == Date.class) {
            return (T) castToDate(obj);
        }

        if (clazz == java.sql.Date.class) {
            return (T) castToSqlDate(obj);
        }

        if (clazz == java.sql.Timestamp.class) {
            return (T) castToTimestamp(obj);
        }

        if (clazz.isEnum()) {
            return (T) castToEnum(obj, clazz, mapping);
        }

        if (Calendar.class.isAssignableFrom(clazz)) {
            Date date = castToDate(obj);
            Calendar calendar;
            if (clazz == Calendar.class) {
                calendar = Calendar.getInstance();
            } else {
                try {
                    calendar = (Calendar) clazz.newInstance();
                } catch (Exception e) {
                    throw new JSONException("can not cast to : " + clazz.getName(), e);
                }
            }
            calendar.setTime(date);
            return (T) calendar;
        }*/

        if (obj instanceof String) {
            String strVal = (String) obj;
            if (strVal.length() == 0) {
                return null;
            }
        }

        throw new PBException("can not cast to : " + clazz.getName());
    }

//    @SuppressWarnings({"unchecked"})
//    public static final <T> T castToJavaBean(Map<String, Object> map, Class<T> clazz, PBDeserializer mapping) {
//        try {
//            if (clazz == StackTraceElement.class) {
//                String declaringClass = (String) map.get("className");
//                String methodName = (String) map.get("methodName");
//                String fileName = (String) map.get("fileName");
//                int lineNumber;
//                {
//                    Number value = (Number) map.get("lineNumber");
//                    if (value == null) {
//                        lineNumber = 0;
//                    } else {
//                        lineNumber = value.intValue();
//                    }
//                }
//
//                return (T) new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
//            }
//
//            /*Object iClassObject = map.get("@type");
//            if (iClassObject instanceof String) {
//                String className = (String) iClassObject;
//                clazz = (Class<T>) loadClass(className);
//            }*/
//
//            if (clazz.isInterface()) {
//                PBMapbject object;
//
//                if (map instanceof PBMapbject) {
//                    object = (PBMapbject) map;
//                } else {
//                    object = new PBMapbject(map);
//                }
//
//                return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
//                        new Class<?>[]{clazz}, object);
//            }
//
//            Map<String, FieldDeserializer> setters = mapping.getFieldDeserializers(clazz);
//
//            T object = clazz.newInstance();
//
//            for (Map.Entry<String, FieldDeserializer> entry : setters.entrySet()) {
//                String key = entry.getKey();
//                Method method = entry.getValue().getMethod();
//
//                if (map.containsKey(key)) {
//                    Object value = map.get(key);
//                    value = cast(value, method.getGenericParameterTypes()[0], mapping);
//                    method.invoke(object, new Object[]{value});
//                }
//            }
//
//            return object;
//        } catch (Exception e) {
//            throw new PBException(e.getMessage(), e);
//        }
//    }

    public static final Boolean castToBoolean(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }

        if (value instanceof String) {
            String str = (String) value;
            if (str.length() == 0) {
                return null;
            }

            if ("true".equals(str)) {
                return Boolean.TRUE;
            }
            if ("false".equals(str)) {
                return Boolean.FALSE;
            }

            if ("1".equals(str)) {
                return Boolean.TRUE;
            }
        }

        throw new PBException("can not cast to int, value : " + value);
    }

    public static final Byte castToByte(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            return Byte.parseByte(strVal);
        }

        throw new PBException("can not cast to byte, value : " + value);
    }

    public static final Character castToChar(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Character) {
            return (Character) value;
        }

        if (value instanceof String) {
            String strVal = (String) value;

            if (strVal.length() == 0) {
                return null;
            }

            if (strVal.length() != 1) {
                throw new PBException("can not cast to byte, value : " + value);
            }

            return strVal.charAt(0);
        }

        throw new PBException("can not cast to byte, value : " + value);
    }

    public static final Short castToShort(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            return Short.parseShort(strVal);
        }

        throw new PBException("can not cast to short, value : " + value);
    }

    public static final BigDecimal castToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }

        String strVal = value.toString();
        if (strVal.length() == 0) {
            return null;
        }

        return new BigDecimal(strVal);
    }

    public static final BigInteger castToBigInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }

        if (value instanceof Float || value instanceof Double) {
            return BigInteger.valueOf(((Number) value).longValue());
        }

        String strVal = value.toString();
        if (strVal.length() == 0) {
            return null;
        }

        return new BigInteger(strVal);
    }

    public static final Float castToFloat(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0) {
                return null;
            }

            return Float.parseFloat(strVal);
        }

        throw new PBException("can not cast to float, value : " + value);
    }

    public static final Double castToDouble(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0) {
                return null;
            }
            return Double.parseDouble(strVal);
        }

        throw new PBException("can not cast to double, value : " + value);
    }

    public static final Integer castToInt(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }

            return Integer.parseInt(strVal);
        }

        throw new PBException("can not cast to int, value : " + value);
    }

    public static final byte[] castToBytes(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }

        if (value instanceof String) {
            return Base64.decodeFast((String) value);
        }
        throw new PBException("can not cast to int, value : " + value);
    }

    public static final Long castToLong(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }

            return Long.parseLong(strVal);
        }

        throw new PBException("can not cast to long, value : " + value);
    }

    public static final String castToString(Object value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }
}
