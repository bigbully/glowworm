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
import com.jd.dd.glowworm.util.FieldInfo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class DefaultFieldDeserializer extends FieldDeserializer {

    private ObjectDeserializer fieldValueDeserilizer;

    public DefaultFieldDeserializer(PBDeserializer mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);
    }

    @Override
    public void parseField(PBDeserializer deserializer, Object object, Type objectType, Map<String, Object> fieldValues) {
        if (fieldValueDeserilizer == null) {
            fieldValueDeserilizer = deserializer.getDeserializer(fieldInfo);
        }

        try {
            Object value;
            if (deserializer.isObjectExist()) {//判断每一个innerJavabean中的属性是否为空，所以在deserialize方法中boolean设为false
                Type type = fieldInfo.getFieldType();
                if (type instanceof Class) {
                    Class rawClass = (Class) type;
                    if (Map.class.isAssignableFrom(rawClass)) {
                        if (rawClass.isInterface()) {
                            value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, Object.class, Object.class, true);
                        } else {
                            value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, Object.class, Object.class, false);
                        }
                    } else if (Collection.class.isAssignableFrom(rawClass)) {
                        if (rawClass.isInterface()) {
                            value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, Object.class, true);
                        } else {
                            value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, Object.class, false);
                        }
                    } else {//默认类型
                        value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false);
                    }
                } else {
                    ParameterizedType rawClass = (ParameterizedType) type;
                    Class rawType = (Class) rawClass.getRawType();
                    if (Map.class.isAssignableFrom(rawType)) {
                        Class keyClazz = (Class) rawClass.getActualTypeArguments()[0];
                        Class valueClazz = (Class) (rawClass).getActualTypeArguments()[1];
                        if (((Class) rawClass.getRawType()).isInterface()) {
                            value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, keyClazz, valueClazz, true);
                        } else {
                            value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, keyClazz, valueClazz, false);
                        }
                    } else if (Collection.class.isAssignableFrom(rawType)) {
                        Class componentClazz = (Class) rawClass.getActualTypeArguments()[0];
                        if (rawType.isInterface()) {
                            value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, componentClazz, true);
                        } else {
                            value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, componentClazz, false);
                        }
                    } else {
                        throw new PBException("暂时不支持的带泛型的集合");
                    }
                }
                if (object == null) {
                    fieldValues.put(fieldInfo.getName(), value);
                } else {
                    setValue(object, value);
                }
            } else {
                value = deserializer.getReference();
                if (object == null) {
                    fieldValues.put(fieldInfo.getName(), value);
                } else {
                    setValue(object, value);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
