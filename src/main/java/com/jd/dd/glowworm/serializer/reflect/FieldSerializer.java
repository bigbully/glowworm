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

import com.jd.dd.glowworm.serializer.PBSerializer;
import com.jd.dd.glowworm.util.FieldInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class FieldSerializer implements Comparable<FieldSerializer> {

    protected final FieldInfo fieldInfo;
    private boolean writeNull = false;

    public FieldSerializer(FieldInfo fieldInfo) {
        super();
        this.fieldInfo = fieldInfo;
        fieldInfo.setAccessible(true);
    }

    public boolean isWriteNull() {
        return writeNull;
    }

    public Field getField() {
        return fieldInfo.getField();
    }

    public String getName() {
        return fieldInfo.getName();
    }

    public Method getMethod() {
        return fieldInfo.getMethod();
    }

    public int compareTo(FieldSerializer o) {
        return this.getName().compareTo(o.getName());
    }

    public Object getPropertyValue(Object object) throws Exception {
        return fieldInfo.get(object);
    }

    public abstract void writeProperty(PBSerializer serializer, Object propertyValue, int fieldInfoIndexParm) throws Exception;

}
