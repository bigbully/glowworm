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
package com.jd.dd.glowworm.serializer.reflect;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;
import com.jd.dd.glowworm.util.FieldInfo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class ObjectFieldSerializer extends FieldSerializer {

    private ObjectSerializer fieldSerializer;

    private Class<?> runtimeFieldClass;

    public ObjectFieldSerializer(FieldInfo fieldInfo) {
        super(fieldInfo);
    }

    @Override
    public void writeProperty(PBSerializer serializer, Object propertyValue, int fieldInfoIndexParm) throws Exception {
        if (fieldSerializer == null) {
            if (propertyValue == null) {
                runtimeFieldClass = this.getMethod().getReturnType();
            } else {
                runtimeFieldClass = propertyValue.getClass();
            }
            fieldSerializer = serializer.getObjectWriter(runtimeFieldClass);
        }

        if (propertyValue == null) {
            serializer.writeNull();
        } else {
//            Class<?> valueClass = propertyValue.getClass();
//            if (valueClass == runtimeFieldClass) {
            if (serializer.needConsiderRef(fieldSerializer) && serializer.isReference(propertyValue)) {
                serializer.writeNull();
            } else {
                serializer.writeNotNull();

                Type type = fieldInfo.getFieldType();
                if (type instanceof Class) {
                    Class rawClass = (Class) type;
                    if (Map.class.isAssignableFrom(rawClass)) {
                        if (rawClass.isInterface()) {
                            fieldSerializer.write(serializer, propertyValue, false, Object.class, Object.class, true);
                        } else {
                            fieldSerializer.write(serializer, propertyValue, false, Object.class, Object.class, false);
                        }
                    } else if (Collection.class.isAssignableFrom(rawClass)) {
                        if (rawClass.isInterface()) {
                            fieldSerializer.write(serializer, propertyValue, false, Object.class, true);
                        } else {
                            fieldSerializer.write(serializer, propertyValue, false, Object.class, false);
                        }
                    } else if (rawClass.isArray()) {
                        fieldSerializer.write(serializer, propertyValue, false, ((Class) type).getComponentType());
                    } else {//默认类型
                        fieldSerializer.write(serializer, propertyValue, false);
                    }
                } else {
                    ParameterizedType rawClass = (ParameterizedType) type;
                    Class rawType = (Class) rawClass.getRawType();
                    if (Map.class.isAssignableFrom(rawType)) {
                        Class keyClazz = (Class) rawClass.getActualTypeArguments()[0];
                        Class valueClazz = (Class) (rawClass).getActualTypeArguments()[1];
                        if (rawType.isInterface()) {
                            fieldSerializer.write(serializer, propertyValue, false, keyClazz, valueClazz, true);
                        } else {
                            fieldSerializer.write(serializer, propertyValue, false, keyClazz, valueClazz, false);
                        }
                    } else if (Collection.class.isAssignableFrom(rawType)) {
                        Class componentClazz = (Class) rawClass.getActualTypeArguments()[0];
                        if (rawType.isInterface()) {
                            fieldSerializer.write(serializer, propertyValue, false, componentClazz, true);
                        } else {
                            fieldSerializer.write(serializer, propertyValue, false, componentClazz, false);
                        }
                    }
                }
            }
//            }else {//如果是拿Object声明的
//                ObjectSerializer valueSerializer = serializer.getObjectWriter(valueClass);
//                if (serializer.needConsiderRef(valueSerializer) && serializer.isReference(propertyValue)){
//                    serializer.writeNull();
//                }else {
//                    serializer.writeNotNull();
//                    valueSerializer.write(serializer, propertyValue, true);
//                }
//            }
        }
    }

}
