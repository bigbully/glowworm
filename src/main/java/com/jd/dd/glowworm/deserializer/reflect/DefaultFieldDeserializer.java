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
                        value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, Object.class, Object.class, rawClass.isInterface());
                    } else if (Collection.class.isAssignableFrom(rawClass)) {
                        value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, Object.class, rawClass.isInterface());
                    } else {//默认类型
                        value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false);
                    }
                } else {
                    ParameterizedType rawClass = (ParameterizedType) type;
                    Class rawType = (Class) rawClass.getRawType();
                    if (Map.class.isAssignableFrom(rawType)) {
                        Type keyType = rawClass.getActualTypeArguments()[0];
                        Type valueType = rawClass.getActualTypeArguments()[1];
                        Class keyClazz = keyType instanceof Class?(Class) keyType:Object.class;
                        Class valueClazz = valueType instanceof Class?(Class)valueType:Object.class;
                        value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, keyClazz, valueClazz, ((Class) rawClass.getRawType()).isInterface());
                    } else if (Collection.class.isAssignableFrom(rawType)) {
                        Type componentType = rawClass.getActualTypeArguments()[0];
                        Class componentClazz = componentType instanceof Class?(Class)componentType:Object.class;
                        value = fieldValueDeserilizer.deserialize(deserializer, getFieldType(), false, componentClazz, rawType.isInterface());
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
