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
package com.jd.dd.glowworm.deserializer.normal;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;
import com.jd.dd.glowworm.deserializer.multi.ArrayDeserializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

public class ExceptionDeserializer implements ObjectDeserializer {

    private Class clazz;

    public ExceptionDeserializer(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        Object obj;
        if (needConfirmExist) {//作为对象的属性
            if (deserializer.isObjectExist()) {
                obj = getThrowable(deserializer);
            } else {
                obj = deserializer.getReference();
            }
        } else {
            obj = getThrowable(deserializer);
        }
        return (T) obj;
    }

    public Object getThrowable(PBDeserializer deserializer) {
        Throwable throwable = null;
        Throwable cause = deserialize(deserializer, null, true);
        //默认写入两个属性localizedMessage，message。这两个属性现在来看一致，所以只读一个属性
        String message = null;
        try {
            message = StringDeserializer.instance.deserialize(deserializer, String.class, true);
            StringDeserializer.instance.deserialize(deserializer, String.class, true);//废弃这个属性
        } catch (Exception e) {
            e.printStackTrace();
        }
        StackTraceElement[] stackTrace = ArrayDeserializer.instance.deserialize(deserializer, StackTraceElement[].class, true);
        try {
            throwable = createException(message, cause, this.clazz);
            throwable.setStackTrace(stackTrace);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return throwable;
    }

    private Throwable createException(String message, Throwable cause, Class<?> exClass) throws Exception {
        Constructor<?> defaultConstructor = null;
        Constructor<?> messageConstructor = null;
        Constructor<?> causeConstructor = null;
        for (Constructor<?> constructor : exClass.getConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                defaultConstructor = constructor;
                continue;
            }

            if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0] == String.class) {
                messageConstructor = constructor;
                continue;
            }

            if (constructor.getParameterTypes().length == 2 && constructor.getParameterTypes()[0] == String.class
                    && constructor.getParameterTypes()[1] == Throwable.class) {
                causeConstructor = constructor;
                continue;
            }
        }

        if (causeConstructor != null) {
            return (Throwable) causeConstructor.newInstance(message, cause);
        }

        if (messageConstructor != null) {
            return (Throwable) messageConstructor.newInstance(message);
        }

        if (defaultConstructor != null) {
            return (Throwable) defaultConstructor.newInstance();
        }

        return null;
    }
}
