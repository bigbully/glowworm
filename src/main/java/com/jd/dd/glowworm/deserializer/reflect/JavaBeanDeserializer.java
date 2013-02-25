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
package com.jd.dd.glowworm.deserializer.reflect;

import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;
import com.jd.dd.glowworm.util.DeserializeBeanInfo;
import com.jd.dd.glowworm.util.FieldInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.*;

public class JavaBeanDeserializer implements ObjectDeserializer {

    private final Map<String, FieldDeserializer> feildDeserializerMap = new IdentityHashMap<String, FieldDeserializer>();

    private final List<FieldDeserializer> fieldDeserializers = new ArrayList<FieldDeserializer>();

    private final Class<?> clazz;
    private final Type type;

    private DeserializeBeanInfo beanInfo;

    public JavaBeanDeserializer(DeserializeBeanInfo beanInfo) {
        this.beanInfo = beanInfo;
        this.clazz = beanInfo.getClazz();
        this.type = beanInfo.getType();
    }

    public JavaBeanDeserializer(PBDeserializer config, Class<?> clazz) {
        this(config, clazz, clazz);
    }

    public JavaBeanDeserializer(PBDeserializer config, Class<?> clazz, Type type) {
        this.clazz = clazz;
        this.type = type;

        beanInfo = DeserializeBeanInfo.computeSetters(clazz, type);

        for (FieldInfo fieldInfo : beanInfo.getFieldList()) {
            addFieldDeserializer(config, clazz, fieldInfo);
        }

        Collections.sort(beanInfo.getFieldList());
        Collections.sort((ArrayList) fieldDeserializers);
    }

    public Map<String, FieldDeserializer> getFieldDeserializerMap() {
        return feildDeserializerMap;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Type getType() {
        return type;
    }

    private void addFieldDeserializer(PBDeserializer mapping, Class<?> clazz, FieldInfo fieldInfo) {
        FieldDeserializer fieldDeserializer = createFieldDeserializer(mapping, clazz, fieldInfo);

        feildDeserializerMap.put(fieldInfo.getName().intern(), fieldDeserializer);
        fieldDeserializers.add(fieldDeserializer);
    }

    public FieldDeserializer createFieldDeserializer(PBDeserializer mapping, Class<?> clazz, FieldInfo fieldInfo) {
        return mapping.createFieldDeserializer(mapping, clazz, fieldInfo);
    }

    public Object createInstance(PBDeserializer deserializer, Type type) {
        if (beanInfo.getDefaultConstructor() == null) {
            return null;
        }

        Object object;
        try {
            Constructor<?> constructor = beanInfo.getDefaultConstructor();
            if (constructor.getParameterTypes().length == 0) {
                object = constructor.newInstance();
            } else {
                object = constructor.newInstance(deserializer.getObject());
            }
        } catch (Exception e) {
            throw new PBException("create instance error, class " + clazz.getName(), e);
        }

        return object;
    }


    public <T> T deserialize(PBDeserializer parser, Type type, boolean needConfirmExist, Object... extraParams) {
        Object object = null;

        Map<String, Object> fieldValues = null;
        object = createInstance(parser, type);
        parser.addToObjectIndexMap(object, this);
        if (object == null) {
            fieldValues = new HashMap<String, Object>(this.fieldDeserializers.size());
        }

        try {
            int tmpListSz = fieldDeserializers.size();
            for (int i = 0; i < tmpListSz; i++) {
                parseField(parser, i, object, type, fieldValues);
            }

            if (object == null) {
                if (fieldValues == null) {
                    object = createInstance(parser, type);
                    return (T) object;
                }

                List<FieldInfo> fieldInfoList = beanInfo.getFieldList();
                int size = fieldInfoList.size();
                Object[] params = new Object[size];
                for (int i = 0; i < size; ++i) {
                    FieldInfo fieldInfo = fieldInfoList.get(i);
                    params[i] = fieldValues.get(fieldInfo.getName());
                }

                if (beanInfo.getCreatorConstructor() != null) {
                    try {
                        object = beanInfo.getCreatorConstructor().newInstance(params);
                    } catch (Exception e) {
                        throw new PBException("create instance error, "
                                + beanInfo.getCreatorConstructor().toGenericString(), e);
                    }
                } else if (beanInfo.getFactoryMethod() != null) {
                    try {
                        object = beanInfo.getFactoryMethod().invoke(null, params);
                    } catch (Exception e) {
                        throw new PBException("create factory method error, "
                                + beanInfo.getFactoryMethod().toString(), e);
                    }
                }
            }

            return (T) object;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
        }
    }

    public boolean parseField(PBDeserializer parser, int feildIndexParm, Object object, Type objectType,
                              Map<String, Object> fieldValues) {
        FieldDeserializer fieldDeserializer = fieldDeserializers.get(feildIndexParm);
        fieldDeserializer.parseField(parser, object, objectType, fieldValues);

        return true;
    }
}
