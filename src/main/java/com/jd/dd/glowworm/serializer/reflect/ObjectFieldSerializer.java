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
                        fieldSerializer.write(serializer, propertyValue, false, Object.class, Object.class, rawClass.isInterface());
                    } else if (Collection.class.isAssignableFrom(rawClass)) {
                        fieldSerializer.write(serializer, propertyValue, false, Object.class, rawClass.isInterface());
                    } else if (rawClass.isArray()) {
                        fieldSerializer.write(serializer, propertyValue, false, ((Class) type).getComponentType());
                    } else {//默认类型
                        fieldSerializer.write(serializer, propertyValue, false);
                    }
                } else {
                    ParameterizedType rawClass = (ParameterizedType) type;
                    Class rawType = (Class) rawClass.getRawType();
                    if (Map.class.isAssignableFrom(rawType)) {
                        Type keyType = rawClass.getActualTypeArguments()[0];
                        Type valueType = rawClass.getActualTypeArguments()[1];
                        Class keyClazz = keyType instanceof Class?(Class)keyType:Object.class;
                        Class valueClazz = valueType instanceof  Class?(Class)valueType:Object.class;
                        fieldSerializer.write(serializer, propertyValue, false, keyClazz, valueClazz, rawType.isInterface());
                    } else if (Collection.class.isAssignableFrom(rawType)) {
                        Type componentType = rawClass.getActualTypeArguments()[0];
                        Class componentClazz = componentType instanceof Class?(Class)componentType:Object.class;
                        fieldSerializer.write(serializer, propertyValue, false, componentClazz, rawType.isInterface());
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
