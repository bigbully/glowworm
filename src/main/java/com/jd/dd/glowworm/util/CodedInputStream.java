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


import java.io.EOFException;
import java.io.IOException;

public final class CodedInputStream {
    private int offset;
    private int limit;
    private int pos;
    private BufferInputStream bis;

    public CodedInputStream(BufferInputStream inParm) {
        this.limit = 2147483647;
        this.bis = inParm;
    }

    public CodedInputStream(Buffer data) {
        this(new BufferInputStream(data));
        this.limit = data.length;
    }

    public CodedInputStream(byte[] data) {
        this(new BufferInputStream(data));
        this.limit = data.length;
    }

    public double readDouble() throws IOException {
        Buffer tmpBuffer = readRawBytes(8);
        return getDoubleFromBytes(tmpBuffer.data, tmpBuffer.offset);
    }

    public static double getDoubleFromBytes(byte[] byteValues) {
        long tmpLongValue = getLongFromBytes(byteValues);
        return Double.longBitsToDouble(tmpLongValue);
    }

    public static long getLongFromBytes(byte[] valueBytesParm, int offsetParm) {
        long returnLong = -1;
        try {
            byte[] tmpHighBytes = new byte[4];
            System.arraycopy(valueBytesParm, offsetParm, tmpHighBytes, 0, 4);
            byte[] tmpLowBytes = new byte[4];
            System.arraycopy(valueBytesParm, offsetParm + 4, tmpLowBytes, 0, 4);
            returnLong = ((long) (getIntFromBytes(tmpHighBytes)) << 32)
                    + (getIntFromBytes(tmpLowBytes) & 0xFFFFFFFFL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnLong;
    }

    public static double getDoubleFromBytes(byte[] byteValues, int offsetParm) {
        long tmpLongValue = getLongFromBytes(byteValues, offsetParm);
        return Double.longBitsToDouble(tmpLongValue);
    }

    public static long getLongFromBytes(byte[] valueBytesParm) {
        long returnLong = -1;
        try {
            byte[] tmpHighBytes = new byte[4];
            System.arraycopy(valueBytesParm, 0, tmpHighBytes, 0, 4);
            byte[] tmpLowBytes = new byte[4];
            System.arraycopy(valueBytesParm, 4, tmpLowBytes, 0, 4);
            returnLong = ((long) (getIntFromBytes(tmpHighBytes)) << 32)
                    + (getIntFromBytes(tmpLowBytes) & 0xFFFFFFFFL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnLong;
    }

    public static int getIntFromBytes(byte[] valueBytesParm) {
        int returnInt = -1;
        try {
            int ch1 = (valueBytesParm[0] & 0xFF);
            int ch2 = (valueBytesParm[1] & 0xFF);
            int ch3 = (valueBytesParm[2] & 0xFF);
            int ch4 = (valueBytesParm[3] & 0xFF);
            returnInt = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnInt;
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readRawLittleEndian32());
    }

    public long readUInt64() throws IOException {
        return readRawVarint64();
    }

    public long readInt64() throws IOException {
        return readRawVarint64();
    }

    public int readInt32() throws IOException {
        return readRawVarint32();
    }

    public int readInt() throws IOException {
        return decodeZigZag32(readRawVarint32());
    }

    public long readLong() throws IOException {
        return decodeZigZag64(readRawVarint64());
    }

    public long readFixed64() throws IOException {
        return readRawLittleEndian64();
    }

    public int readFixed32() throws IOException {
        return readRawLittleEndian32();
    }

    public boolean readBool() throws IOException {
        return (readRawVarint32() != 0);
    }

    public String readString() throws IOException {
        int size = readRawVarint32();
        Buffer data = readRawBytes(size);
        return new String(data.data, data.offset, data.length, "UTF-8");
    }

    public Buffer readBytes() throws IOException {
        int size = readRawVarint32();
        return readRawBytes(size);
    }

    public int readUInt32() throws IOException {
        return readRawVarint32();
    }

    public int readEnum()
            throws IOException {
        return readRawVarint32();
    }

    public int readSFixed32() throws IOException {
        return readRawLittleEndian32();
    }

    public long readSFixed64() throws IOException {
        return readRawLittleEndian64();
    }

    public int readSInt32() throws IOException {
        return decodeZigZag32(readRawVarint32());
    }

    public long readSInt64() throws IOException {
        return decodeZigZag64(readRawVarint64());
    }

    public int readRawVarint32()
            throws IOException {
        byte tmp = readRawByte();
        if (tmp >= 0)
            return tmp;

        int result = tmp & 0x7F;
        if ((tmp = readRawByte()) >= 0) {
            result |= tmp << 7;
        } else {
            result |= (tmp & 0x7F) << 7;
            if ((tmp = readRawByte()) >= 0) {
                result |= tmp << 14;
            } else {
                result |= (tmp & 0x7F) << 14;
                if ((tmp = readRawByte()) >= 0) {
                    result |= tmp << 21;
                } else {
                    result |= (tmp & 0x7F) << 21;
                    result |= (tmp = readRawByte()) << 28;
                    if (tmp < 0) {
                        for (int i = 0; i < 5; ++i)
                            if (readRawByte() >= 0)
                                return result;

                        throw InvalidProtocolBufferException.malformedVarint();
                    }
                }
            }
        }
        return result;
    }

    public long readRawVarint64() throws IOException {
        int shift = 0;
        long result = 0L;
        while (shift < 64) {
            byte b = readRawByte();
            // 修复大数值long数据丢失的bug
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0)
                return result;
            shift += 7;
        }
        throw InvalidProtocolBufferException.malformedVarint();
    }

    public int readRawLittleEndian32() throws IOException {
        byte b1 = readRawByte();
        byte b2 = readRawByte();
        byte b3 = readRawByte();
        byte b4 = readRawByte();
        return (b1 & 0xFF | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24);
    }

    public long readRawLittleEndian64() throws IOException {
        byte b1 = readRawByte();
        byte b2 = readRawByte();
        byte b3 = readRawByte();
        byte b4 = readRawByte();
        byte b5 = readRawByte();
        byte b6 = readRawByte();
        byte b7 = readRawByte();
        byte b8 = readRawByte();
        return (b1 & 0xFF | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24 | (b5 & 0xFF) << 32 | (b6 & 0xFF) << 40 | (b7 & 0xFF) << 48 | (b8 & 0xFF) << 56);
    }

    public static int decodeZigZag32(int n) {
        return (n >>> 1 ^ -(n & 0x1));
    }

    public static long decodeZigZag64(long n) {
        return (n >>> 1 ^ -(n & 1L));
    }

    public byte readRawByte()
            throws IOException {
        if (this.pos >= this.limit)
            throw new EOFException();

        int rc = this.bis.read();
        if (rc < 0)
            throw new EOFException();

        this.pos += 1;
        return (byte) (rc & 0xFF);
    }

    public Buffer readRawBytes(int size)
            throws IOException {
        if (size == 0)
            return new Buffer(new byte[0]);

        if (this.pos + size > this.limit) {
            throw new EOFException();
        }

        if (this.bis != null) {
            Buffer rc = this.bis.readBuffer(size);
            if ((rc == null) || (rc.getLength() < size))
                throw new EOFException();

            this.pos += rc.getLength();
            return rc;
        }

        byte[] rc = new byte[size];

        int pos = 0;
        while (pos < size) {
            int c = this.bis.read(rc, pos, size - pos);
            if (c < 0)
                throw new EOFException();

            this.pos += c;
            pos += c;
        }

        return new Buffer(rc);
    }

    public void skipRawBytes(int size)
            throws IOException {
        if (size == 0)
            return;

        if (this.pos + size > this.limit) {
            throw new EOFException();
        } else {
            int n = (int) this.bis.skip(size);
            pos += n;
        }
    }

    public int pushLimit(int limit) {
        int rc = this.limit;
        this.limit = (this.pos + limit);
        return rc;
    }

    public void popLimit(int limit) {
        this.limit = limit;
    }

    public String readString(String charset) throws IOException {
        int size = readRawVarint32();
        Buffer data = readRawBytes(size);
        return new String(data.data, data.offset, data.length, charset);
    }

    public int getPos() {
        return pos;
    }

    public void reset(byte[] bytes) {
        limit = bytes.length;
        pos = offset;
        bis.setNewData(bytes);
    }

    public void reset() {
        limit = offset;
        pos = offset;
        bis.reset();
    }

//
//    //用于做测试用
//    public void printAllBytes(int start, int end){
//        for(int i=0;i< this.bis.buffer.length;i++){
//            if (i >= start && i <= end){
//                System.out.println(i+ " | " + this.bis.buffer[i]);
//            }
//        }
//    }
}
