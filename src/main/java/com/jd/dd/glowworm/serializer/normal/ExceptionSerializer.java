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
package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.asm.Type;
import com.jd.dd.glowworm.serializer.PBSerializer;
import com.jd.dd.glowworm.serializer.reflect.JavaBeanSerializer;

import java.io.IOException;

public class ExceptionSerializer extends JavaBeanSerializer {

    public ExceptionSerializer(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        if (needWriteType) {
            serializer.writeType(Type.EXCEPTION);
        }
        super.write(serializer, object, false, extraParams);
    }

}
