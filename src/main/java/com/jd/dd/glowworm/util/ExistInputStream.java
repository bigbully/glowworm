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
package com.jd.dd.glowworm.util;

import com.jd.dd.glowworm.PBException;

public class ExistInputStream {

    private byte[] buffer;
    private int pos;
    private int limit;
    private int bitPos;
    private int offset;

    public ExistInputStream(byte[] bytes, int pos, int length) {
        this.buffer = bytes;
        this.pos = pos;
        this.limit = pos + length;
    }

    public void reset(byte[] bytes, int pos, int length) {
        this.buffer = bytes;
        this.pos = pos;
        this.limit = pos + length;
    }

    public int readRawByte() {
        if (this.pos < this.limit) {
            if (bitPos % 8 == 0 && bitPos != 0) {
                pos++;
            }
            return ((byte) (buffer[pos] >> (7 - (bitPos++ % 8))) & 1);
        } else {
            throw new PBException("out of index");
        }
    }

    public void reset() {
        pos = offset;
        limit = offset;
        bitPos = offset;
    }

}
