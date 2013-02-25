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

public class TypeOutputStream {

    byte[] headBuffer;
    int headLimit;
    int headBitPos;
    int headPos;

    byte[] buffer;
    int limit;
    int bitPos;
    int pos;

    int offset;

    public TypeOutputStream(int size) {
        this(new byte[size], new byte[size / 3]);
    }

    public TypeOutputStream(byte[] buffer, byte[] headBuffer) {
        this.headBuffer = headBuffer;
        this.headLimit = headBuffer.length;
        this.buffer = buffer;
        this.limit = buffer.length;
    }

    public void write(int i) {
        if (i < 8) {//如果是常用类型
            write(true);//typeHead里写0
            if (bitPos % 8 < 6 || bitPos == 0) {//要加的这3个bit在这个8个bit之内
                byte b1 = bitPos % 8 == 0 ? 0 : buffer[pos];
                b1 = (byte) (b1 | (i << (5 - (bitPos % 8))));
                buffer[pos] = b1;
                bitPos += 3;
                if (bitPos % 8 == 0 && bitPos != 0) {
                    pos++;
                    checkCapacity(pos);
                }
            } else {//如果需要进位
                int thisbitNum = 3 - (bitPos + 3) % 8;
                //拿thisbitNum == 2举例，可以写在当前byte中有两位bit，所以下面算法是，首先把byte左移两位。
                //把i & 6 >> 1的意思是i这个三位bit，与110这三位bit向与，然后又移1位，获得两位bit
                //再与之前的计算结果向或
                if (thisbitNum == 2) {
                    buffer[pos] = (byte) (buffer[pos] | ((i & 6) >> 1));
                    pos++;
                    checkCapacity(pos);
                    buffer[pos] = (byte) ((i & 1) << 7);
                } else if (thisbitNum == 1) {
                    buffer[pos] = (byte) (buffer[pos] | ((i & 4) >> 2));
                    pos++;
                    checkCapacity(pos);
                    buffer[pos] = (byte) ((i & 3) << 6);
                }
                bitPos += 3;
            }
        } else {//非常用类型
            write(false);//typeHead里写1
            if (bitPos % 8 < 3 || bitPos == 0) {//要加的这3个bit在这个8个bit之内
                byte b1 = bitPos % 8 == 0 ? 0 : buffer[pos];
                b1 = (byte) (b1 | (i << (2 - (bitPos % 8))));
                buffer[pos] = b1;
                bitPos += 6;
                if (bitPos % 8 == 0 && bitPos != 0) {
                    pos++;
                    checkCapacity(pos);
                }
            } else {//如果需要进位
                int thisbitNum = 6 - (bitPos + 6) % 8;
                if (thisbitNum == 5) {
                    buffer[pos] = (byte) (buffer[pos] | ((i & 62) >> 1));
                    pos++;
                    checkCapacity(pos);
                    buffer[pos] = (byte) ((i & 1) << 7);
                } else if (thisbitNum == 4) {
                    buffer[pos] = (byte) (buffer[pos] | ((i & 60) >> 2));
                    pos++;
                    checkCapacity(pos);
                    buffer[pos] = (byte) ((i & 3) << 6);
                } else if (thisbitNum == 3) {
                    buffer[pos] = (byte) (buffer[pos] | ((i & 56) >> 3));
                    pos++;
                    checkCapacity(pos);
                    buffer[pos] = (byte) ((i & 7) << 5);
                } else if (thisbitNum == 2) {
                    buffer[pos] = (byte) (buffer[pos] | ((i & 48) >> 4));
                    pos++;
                    checkCapacity(pos);
                    buffer[pos] = (byte) ((i & 15) << 4);
                } else if (thisbitNum == 1) {
                    buffer[pos] = (byte) (buffer[pos] | ((i & 32) >> 5));
                    pos++;
                    checkCapacity(pos);
                    buffer[pos] = (byte) ((i & 31) << 3);
                }
                bitPos += 6;
            }
        }
    }

    private void write(boolean bool) {
        if (headBitPos != 0 && headBitPos % 8 == 0) {
            headPos++;
            checkCapacityHead(headPos);
        }
        byte b1 = headBitPos % 8 == 0 ? 0 : headBuffer[headPos];
        if (bool) {
            headBitPos++;
        } else {
            b1 = (byte) (b1 | (1 << (7 - (headBitPos++ % 8))));
        }
        headBuffer[headPos] = b1;
    }

    private void checkCapacityHead(int minimumCapacity) {
        if (minimumCapacity + 1 >= this.headLimit) {
            byte[] tmpNewBytes = new byte[minimumCapacity + 16];
            System.arraycopy(this.headBuffer, 0, tmpNewBytes, 0, this.headLimit);
            this.headBuffer = tmpNewBytes;
            this.headLimit = tmpNewBytes.length;
        }
    }

    private void checkCapacity(int minimumCapacity) {
        if (minimumCapacity + 1 >= this.limit) {
            byte[] tmpNewBytes = new byte[minimumCapacity + 50];
            System.arraycopy(this.buffer, 0, tmpNewBytes, 0, this.limit);
            this.buffer = tmpNewBytes;
            this.limit = tmpNewBytes.length;
        }
    }

    public void reset() {
        this.pos = this.offset;
        this.bitPos = this.offset;
    }

    public byte[] getBytes() {
        if (this.bitPos == 0) {
            return null;
        } else {
            byte[] retBytes = new byte[pos + 1];
            System.arraycopy(this.buffer, 0, retBytes, 0, pos + 1);
            return retBytes;
        }
    }

    public byte[] getHeadBytes() {
        if (this.headBitPos == 0) {
            return null;
        } else {
            byte[] retBytes = new byte[headPos + 1];
            System.arraycopy(this.headBuffer, 0, retBytes, 0, headPos + 1);
            return retBytes;
        }
    }

    public void headReset() {
        this.headPos = this.offset;
        this.headBitPos = this.offset;
    }


}
