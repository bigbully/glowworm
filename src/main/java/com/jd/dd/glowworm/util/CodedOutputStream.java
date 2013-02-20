package com.jd.dd.glowworm.util;

public final class CodedOutputStream {

    private BufferOutputStream bos;

    public CodedOutputStream(BufferOutputStream os) {
        bos = (BufferOutputStream) os;
    }

    public byte[] getBytes() {
        return bos.getBytes();
    }

    public void reset() {
        bos.reset();
    }

    //写入非负整数
    public void writeNaturalInt(int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                writeRawByte(value);
                return;
            } else {
                writeRawByte((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    public void writeInt(int i) {
        writeRawVarint32(encodeZigZag32(i));
    }

    //写入int类型的byte
    public void writeRawByte(int value) {
        writeRawByte((byte) value);
    }

    //写入纯byte
    public void writeRawByte(byte value) {
        bos.write(value);
    }

    //写入纯byte数组
    public void writeRawBytes(byte[] value) {
        writeRawBytes(value, 0, value.length);
    }

    //写入纯byte数组
    public void writeRawBytes(byte[] value, int offset, int length) {
        bos.write(value, offset, length);
    }

    public void writeString(String s) {
        try {
            byte[] bytes = s.getBytes("UTF-8");
            writeRawVarint32(bytes.length);
            writeRawBytes(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void writeString(String value, String Charset) {
        try {
            byte[] bytes = value.getBytes(Charset);
            writeRawVarint32(bytes.length);
            writeRawBytes(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void writeDouble(double d) {
        writeRawBytes(getBytesFromLong(Double.doubleToLongBits(d)), 0, 8);
    }

    public void writeLong(long l) {
        writeRawVarint64(encodeZigZag64(l));
    }

    public void writeFloat(float f) {
        writeRawLittleEndian32(Float.floatToRawIntBits(f));
    }

    //varint 正整数
    private void writeRawVarint32(int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                writeRawByte(value);
                return;
            } else {
                writeRawByte((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    private void writeRawVarint64(long l) {
        while (true) {
            if ((l & ~0x7FL) == 0) {
                writeRawByte((int) l);
                return;
            } else {
                writeRawByte(((int) l & 0x7F) | 0x80);
                l >>>= 7;
            }
        }
    }

    private long encodeZigZag64(long l) {
        return (l << 1) ^ (l >> 63);
    }

    private int encodeZigZag32(int i) {
        return (i << 1) ^ (i >> 31);
    }

    public static byte[] getBytesFromLong(long longValueParm) {
        byte[] returnInt = new byte[8];
        returnInt[0] = (byte) ((longValueParm >> 56) & 0xFF);
        returnInt[1] = (byte) ((longValueParm >>> 48) & 0xFF);
        returnInt[2] = (byte) ((longValueParm >>> 40) & 0xFF);
        returnInt[3] = (byte) ((longValueParm >>> 32) & 0xFF);
        returnInt[4] = (byte) ((longValueParm >>> 24) & 0xFF);
        returnInt[5] = (byte) ((longValueParm >>> 16) & 0xFF);
        returnInt[6] = (byte) ((longValueParm >>> 8) & 0xFF);
        returnInt[7] = (byte) ((longValueParm >>> 0) & 0xFF);
        return returnInt;
    }


    private void writeRawLittleEndian32(int value) {
        writeRawByte((value) & 0xFF);
        writeRawByte((value >> 8) & 0xFF);
        writeRawByte((value >> 16) & 0xFF);
        writeRawByte((value >> 24) & 0xFF);
    }
}