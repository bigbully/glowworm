/*
 * Copyright 360buy
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.jd.dd.glowworm.deserializer.multi;

import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;
import com.jd.dd.glowworm.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapDeserializer extends MultiDeserialier implements ObjectDeserializer {

    public final static MapDeserializer instance = new MapDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        if (needConfirmExist) {//作为属性
            try {
                if (deserializer.isObjectExist()) {
                    return (T) getMap(deserializer, type, extraParams);
                } else {
                    return (T) deserializer.getReference();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {//直接序列化
            return (T) getMap(deserializer, type, extraParams);
        }
        return null;
    }

    private Object getMap(PBDeserializer deserializer, Type type, Object[] extraParams) {
        Object map;
        if (isInterface(extraParams)) {
            map = getActualTypeObjectWhileInterface(deserializer);
        } else {
            map = createMap(type, deserializer);
        }
        int size = 0;
        try {
            size = deserializer.scanNaturalInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        deserializer.addToObjectIndexMap(map, this);
        Class keyClazz;
        Class valueClazz;
        if (extraParams == null || extraParams.length == 0) {
            keyClazz = Object.class;
            valueClazz = Object.class;
        } else {
            keyClazz = (Class) extraParams[0];
            valueClazz = (Class) extraParams[1];
        }
        if (keyClazz == Object.class && valueClazz == Object.class) {//key,value都选择性写入类名(Object)
            getAllObjectElement(deserializer, map, size);
        } else if (keyClazz != Object.class && valueClazz == Object.class) {//key不写类名(Generic)，value写(Object)
            getKeyGValueOElement(deserializer, map, keyClazz, size);
        } else if (keyClazz == Object.class && valueClazz != Object.class) {//key写(Object), value不写(Generic)
            getKeyOValueGElement(deserializer, map, valueClazz, size);
        } else if (keyClazz != Object.class && valueClazz != Object.class) {//都不写(Generic)
            getElementWithGerenic(deserializer, map, keyClazz, valueClazz, size);
        }
        return map;
    }

    private void getElementWithGerenic(PBDeserializer deserializer, Object map, Class keyClazz, Class valueClazz, int size) {
        ObjectDeserializer keyParser = deserializer.getDeserializer(keyClazz);
        ObjectDeserializer valueParser = deserializer.getDeserializer(valueClazz);
        try {
            for (int i = 0; i < size; i++) {
                Object key;
                if (deserializer.isObjectExist()) {
                    key = keyParser.deserialize(deserializer, keyClazz, false);
                } else {
                    key = deserializer.getReference();
                }

                Object value;
                if (deserializer.isObjectExist()) {
                    value = valueParser.deserialize(deserializer, valueClazz, false);
                } else {
                    value = deserializer.getReference();
                }
                ((Map) map).put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getKeyOValueGElement(PBDeserializer deserializer, Object map, Class valueClazz, int size) {
        Class preKeyClazz = null;
        ObjectDeserializer preKeyParser = null;
        ObjectDeserializer valueParser = deserializer.getDeserializer(valueClazz);
        try {
            for (int i = 0; i < size; i++) {
                Object key;
                if (deserializer.isObjectExist()) {
                    int itemType = deserializer.scanType();
                    if (itemType == com.jd.dd.glowworm.asm.Type.OBJECT) {
                        String itemClassName = deserializer.scanString();
                        //如果与前一个反序列化器相同的话，类名为空字符串,直接使用上一个反序列化器，如果能读到类名，则直接用新的反序列化器
                        if (!itemClassName.equals("")) {
                            preKeyClazz = TypeUtils.loadClass(itemClassName);
                            preKeyParser = deserializer.getDeserializer(preKeyClazz);
                        }
                        key = preKeyParser.deserialize(deserializer, preKeyClazz, false);
                    } else {
                        key = deserializer.parsePureObject(itemType);
                    }
                } else {
                    key = deserializer.getReference();
                }

                Object value;
                if (deserializer.isObjectExist()) {
                    value = valueParser.deserialize(deserializer, valueClazz, false);
                } else {
                    value = deserializer.getReference();
                }
                ((Map) map).put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getKeyGValueOElement(PBDeserializer deserializer, Object map, Class keyClazz, int size) {
        Class preValueClazz = null;
        ObjectDeserializer preValueParser = null;
        ObjectDeserializer keyParser = deserializer.getDeserializer(keyClazz);
        try {
            for (int i = 0; i < size; i++) {
                Object key;
                if (deserializer.isObjectExist()) {
                    key = keyParser.deserialize(deserializer, keyClazz, false);
                } else {
                    key = deserializer.getReference();
                }

                Object value;
                if (deserializer.isObjectExist()) {
                    int itemType = deserializer.scanType();
                    if (itemType == com.jd.dd.glowworm.asm.Type.OBJECT) {
                        String itemClassName = deserializer.scanString();
                        //如果与前一个反序列化器相同的话，类名为空字符串,直接使用上一个反序列化器，如果能读到类名，则直接用新的反序列化器
                        if (!itemClassName.equals("")) {
                            preValueClazz = TypeUtils.loadClass(itemClassName);
                            preValueParser = deserializer.getDeserializer(preValueClazz);
                        }
                        value = preValueParser.deserialize(deserializer, preValueClazz, false);
                    } else {
                        value = deserializer.parsePureObject(itemType);
                    }
                } else {
                    value = deserializer.getReference();
                }
                ((Map) map).put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getActualTypeObjectWhileInterface(PBDeserializer deserializer) {
        Object map;
        int type = deserializer.scanType();
        if (type == com.jd.dd.glowworm.asm.Type.MAP_HASH) {
            map = new HashMap();
        } else if (type == com.jd.dd.glowworm.asm.Type.MAP_LinkedHash) {
            map = new LinkedHashMap();
        } else if (type == com.jd.dd.glowworm.asm.Type.MAP_ConcurrentHashMap) {
            map = new ConcurrentHashMap();
        } else {
            map = null;
        }
        return map;
    }

    protected boolean isInterface(Object[] extraParams) {
        return extraParams != null && extraParams.length == 3 && (Boolean) extraParams[2];
    }

    private void getAllObjectElement(PBDeserializer deserializer, Object map, int size) {
        Class preKeyClazz = null;
        Class preValueClazz = null;
        ObjectDeserializer preKeyParser = null;
        ObjectDeserializer preValueParser = null;
        try {
            for (int i = 0; i < size; i++) {
                Object key;
                if (deserializer.isObjectExist()) {
                    int itemType = deserializer.scanType();
                    if (itemType == com.jd.dd.glowworm.asm.Type.OBJECT) {
                        String itemClassName = deserializer.scanString();
                        //如果与前一个反序列化器相同的话，类名为空字符串,直接使用上一个反序列化器，如果能读到类名，则直接用新的反序列化器
                        if (!itemClassName.equals("")) {
                            preKeyClazz = TypeUtils.loadClass(itemClassName);
                            preKeyParser = deserializer.getDeserializer(preKeyClazz);
                        }
                        key = preKeyParser.deserialize(deserializer, preKeyClazz, false);
                    } else {
                        key = deserializer.parsePureObject(itemType);
                    }
                } else {
                    key = deserializer.getReference();
                }

                Object value;
                if (deserializer.isObjectExist()) {
                    int itemType = deserializer.scanType();
                    if (itemType == com.jd.dd.glowworm.asm.Type.OBJECT) {
                        String itemClassName = deserializer.scanString();
                        //如果与前一个反序列化器相同的话，类名为空字符串,直接使用上一个反序列化器，如果能读到类名，则直接用新的反序列化器
                        if (!itemClassName.equals("")) {
                            preValueClazz = TypeUtils.loadClass(itemClassName);
                            preValueParser = deserializer.getDeserializer(preValueClazz);
                        }
                        value = preValueParser.deserialize(deserializer, preValueClazz, false);
                    } else {
                        value = deserializer.parsePureObject(itemType);
                    }
                } else {
                    value = deserializer.getReference();
                }
                ((Map) map).put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Map<Object, Object> createMap(Type type, PBDeserializer deserializer) {
        if (type == ConcurrentMap.class || type == ConcurrentHashMap.class) {
            return new ConcurrentHashMap();
        }
        if (type == HashMap.class) {
            return new HashMap();
        }
        if (type == LinkedHashMap.class) {
            return new LinkedHashMap();
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return createMap(parameterizedType.getRawType(), deserializer);
        }
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            if (clazz.isInterface()) {
                return (Map) getActualMapObjectWhileInterface(deserializer);
            }
            try {
                return (Map<Object, Object>) clazz.newInstance();
            } catch (Exception e) {
                throw new PBException("unsupport type " + type, e);
            }
        }
        throw new PBException("unsupport type " + type);
    }

    @Override
    public void setEachElement(Object multi, int i, Object item) {
        //这个方法废弃掉了
    }

}
