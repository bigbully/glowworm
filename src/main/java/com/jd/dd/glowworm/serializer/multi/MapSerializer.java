package com.jd.dd.glowworm.serializer.multi;

import com.jd.dd.glowworm.asm.Type;
import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapSerializer extends MultiSerializer implements ObjectSerializer {

    public final static MapSerializer instance = new MapSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        int size = ((Map) object).size();
        serializer.writeNaturalInt(size);
        if (!needWriteType) {
            if (isInterface(extraParams)) {
                writeActualType(serializer, object);
            }
            Class keyClazz = (Class) extraParams[0];
            Class valueClazz = (Class) extraParams[1];
            if (keyClazz == Object.class && valueClazz == Object.class) {//key,value都选择性写入类名(Object)
                writeObjectElement(serializer, object, size);
            } else if (keyClazz != Object.class && valueClazz == Object.class) {//key不写类名(Generic)，value写(Object)
                writeKeyGValueOElement(serializer, object, keyClazz, size);
            } else if (keyClazz == Object.class && valueClazz != Object.class) {//key写(Object), value不写(Generic)
                writeKeyOValueGElement(serializer, object, valueClazz, size);
            } else if (keyClazz != Object.class && valueClazz != Object.class) {//都不写(Generic)
                writeElementWithGerenic(serializer, object, keyClazz, valueClazz, size);
            }
        } else {//如果在序列化集合，则不传extraParams
            writeActualType(serializer, object);
            writeObjectElement(serializer, object, size);
        }
    }

    private void writeElementWithGerenic(PBSerializer serializer, Object multi, Class keyClazz, Class valueClazz, int size) {
        try {
            ObjectSerializer keyWriter = serializer.getObjectWriter(keyClazz);
            ObjectSerializer valueWriter = serializer.getObjectWriter(valueClazz);
            Iterator<Map.Entry> iterator = ((Map) multi).entrySet().iterator();
            for (int i = 0; i < size; i++) {
                Map.Entry item = (Map.Entry) getEachElement(iterator, i);

                //key
                Object key = item.getKey();
                if (key == null) {
                    serializer.writeNull();
                } else {
                    if (serializer.needConsiderRef(keyWriter) && serializer.isReference(key)) {
                        serializer.writeNull();
                    } else {
                        serializer.writeNotNull();
                        keyWriter.write(serializer, key, false);
                    }
                }

                //value
                Object value = item.getValue();
                if (value == null) {
                    serializer.writeNull();
                } else {
                    if (serializer.needConsiderRef(valueWriter) && serializer.isReference(value)) {
                        serializer.writeNull();
                    } else {
                        serializer.writeNotNull();
                        valueWriter.write(serializer, value, false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeKeyOValueGElement(PBSerializer serializer, Object multi, Class valueClazz, int size) {
        try {
            ObjectSerializer preKeyWriter = null;
            Class preKeyClazz = null;
            ObjectSerializer valueWriter = serializer.getObjectWriter(valueClazz);
            Iterator<Map.Entry> iterator = ((Map) multi).entrySet().iterator();
            for (int i = 0; i < size; i++) {
                Map.Entry item = (Map.Entry) getEachElement(iterator, i);

                //key
                Object key = item.getKey();
                if (key == null) {
                    serializer.writeNull();
                } else {
                    Class clazz = key.getClass();
                    if (clazz == preKeyClazz) {
                        if (serializer.needConsiderRef(preKeyWriter) && serializer.isReference(key)) {
                            serializer.writeNull();
                        } else {
                            serializer.writeNotNull();
                            if (serializer.isAsmJavaBean(preKeyWriter)) {
                                serializer.writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                                serializer.writeString("");//写入空类名
                            }
                            preKeyWriter.write(serializer, key, true);
                        }
                    } else {
                        preKeyClazz = clazz;
                        preKeyWriter = serializer.getObjectWriter(clazz);
                        if (serializer.needConsiderRef(preKeyWriter) && serializer.isReference(key)) {
                            serializer.writeNull();
                        } else {
                            serializer.writeNotNull();
                            if (serializer.isAsmJavaBean(preKeyWriter)) {
                                serializer.writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                                serializer.writeString(clazz.getName());//写入类名
                            }
                            preKeyWriter.write(serializer, key, true);
                        }
                    }
                }

                //value
                Object value = item.getValue();
                if (value == null) {
                    serializer.writeNull();
                } else {
                    if (serializer.needConsiderRef(valueWriter) && serializer.isReference(value)) {
                        serializer.writeNull();
                    } else {
                        serializer.writeNotNull();
                        valueWriter.write(serializer, value, false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeKeyGValueOElement(PBSerializer serializer, Object multi, Class keyClazz, int size) {
        try {
            ObjectSerializer preValueWriter = null;
            Class preValueClazz = null;
            ObjectSerializer keyWriter = serializer.getObjectWriter(keyClazz);
            Iterator<Map.Entry> iterator = ((Map) multi).entrySet().iterator();
            for (int i = 0; i < size; i++) {
                Map.Entry item = (Map.Entry) getEachElement(iterator, i);

                //key
                Object key = item.getKey();
                if (key == null) {
                    serializer.writeNull();
                } else {
                    if (serializer.needConsiderRef(keyWriter) && serializer.isReference(key)) {
                        serializer.writeNull();
                    } else {
                        serializer.writeNotNull();
                        keyWriter.write(serializer, key, false);
                    }
                }

                //value
                Object value = item.getValue();
                if (value == null) {
                    serializer.writeNull();
                } else {
                    Class clazz = value.getClass();
                    if (clazz == preValueClazz) {
                        if (serializer.needConsiderRef(preValueWriter) && serializer.isReference(value)) {
                            serializer.writeNull();
                        } else {
                            serializer.writeNotNull();
                            if (serializer.isAsmJavaBean(preValueWriter)) {
                                serializer.writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                                serializer.writeString("");//写入空类名
                            }
                            preValueWriter.write(serializer, value, true);
                        }
                    } else {
                        preValueClazz = clazz;
                        preValueWriter = serializer.getObjectWriter(clazz);
                        if (serializer.needConsiderRef(preValueWriter) && serializer.isReference(value)) {
                            serializer.writeNull();
                        } else {
                            serializer.writeNotNull();
                            if (serializer.isAsmJavaBean(preValueWriter)) {
                                serializer.writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                                serializer.writeString(clazz.getName());//写入类名
                            }
                            preValueWriter.write(serializer, value, true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeActualType(PBSerializer serializer, Object object) {
        Class clazz = object.getClass();
        if (ConcurrentHashMap.class.isAssignableFrom(clazz)) {
            serializer.writeType(Type.MAP_ConcurrentHashMap);
        } else if (LinkedHashMap.class.isAssignableFrom(clazz)) {
            serializer.writeType(Type.MAP_LinkedHash);
        } else if (HashMap.class.isAssignableFrom(clazz)) {
            serializer.writeType(Type.MAP_HASH);
        } else {
            serializer.writeType(Type.Unknown);
        }
    }

    protected boolean isInterface(Object[] extraParams) {
        return extraParams.length == 3 && (Boolean) extraParams[2];
    }

    //无泛型或泛型为Object
    protected void writeObjectElement(PBSerializer serializer, Object multi, int size) {
        try {
            ObjectSerializer preKeyWriter = null;
            ObjectSerializer preValueWriter = null;
            Class preKeyClazz = null;
            Class preValueClazz = null;
            Iterator<Map.Entry> iterator = ((Map) multi).entrySet().iterator();
            for (int i = 0; i < size; i++) {
                Map.Entry item = (Map.Entry) getEachElement(iterator, i);

                //key
                Object key = item.getKey();
                if (key == null) {
                    serializer.writeNull();
                } else {
                    Class clazz = key.getClass();
                    if (clazz == preKeyClazz) {
                        if (serializer.needConsiderRef(preKeyWriter) && serializer.isReference(key)) {
                            serializer.writeNull();
                        } else {
                            serializer.writeNotNull();
                            if (serializer.isAsmJavaBean(preKeyWriter)) {
                                serializer.writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                                serializer.writeString("");//写入空类名
                            }
                            preKeyWriter.write(serializer, key, true);
                        }
                    } else {
                        preKeyClazz = clazz;
                        preKeyWriter = serializer.getObjectWriter(clazz);
                        if (serializer.needConsiderRef(preKeyWriter) && serializer.isReference(key)) {
                            serializer.writeNull();
                        } else {
                            serializer.writeNotNull();
                            if (serializer.isAsmJavaBean(preKeyWriter)) {
                                serializer.writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                                serializer.writeString(clazz.getName());//写入类名
                            }
                            preKeyWriter.write(serializer, key, true);
                        }
                    }
                }

                //value
                Object value = item.getValue();
                if (value == null) {
                    serializer.writeNull();
                } else {
                    Class clazz = value.getClass();
                    if (clazz == preValueClazz) {
                        if (serializer.needConsiderRef(preValueWriter) && serializer.isReference(value)) {
                            serializer.writeNull();
                        } else {
                            serializer.writeNotNull();
                            if (serializer.isAsmJavaBean(preValueWriter)) {
                                serializer.writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                                serializer.writeString("");//写入空类名
                            }
                            preValueWriter.write(serializer, value, true);
                        }
                    } else {
                        preValueClazz = clazz;
                        preValueWriter = serializer.getObjectWriter(clazz);
                        if (serializer.needConsiderRef(preValueWriter) && serializer.isReference(value)) {
                            serializer.writeNull();
                        } else {
                            serializer.writeNotNull();
                            if (serializer.isAsmJavaBean(preValueWriter)) {
                                serializer.writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                                serializer.writeString(clazz.getName());//写入类名
                            }
                            preValueWriter.write(serializer, value, true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getEachElement(Object iterator, int i) {
        return ((Iterator) iterator).next();
    }
}
