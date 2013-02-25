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

import java.lang.reflect.Type;


public class JavaObjectDeserializer implements ObjectDeserializer {

    public static final JavaObjectDeserializer instance = new JavaObjectDeserializer();

    //这个反序列化器进行所有用Object声明的属性的反序列化
    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        int typeCode = deserializer.scanType();
        return (T) deserializer.parsePureObject(typeCode);
    }
}
