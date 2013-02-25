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

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongSerializer implements ObjectSerializer {

    public final static AtomicLongSerializer instance = new AtomicLongSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        AtomicLong value = (AtomicLong) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.ATOMIC_LONG);
        }
        serializer.writeLong(value.get());
    }
}
