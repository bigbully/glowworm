package com.jd.dd.glowworm.util;

import com.jd.dd.glowworm.PBException;

public class TypeInputStream {

    private byte[] buffer;
    private int pos;
    private int limit;
    private int bitPos;

    private int headPos;
    private int headLimit;
    private int headBitPos;

    private int offset;

    public TypeInputStream(){}

    public TypeInputStream(byte[] bytes, int headPos, int headLimit) {
        this.buffer = bytes;
        this.headPos = headPos;
        this.headLimit = headPos + headLimit;
    }

    public void headReset(byte[] bytes, int headPos, int headLength){
        this.buffer = bytes;
        this.headPos = headPos;
        this.headLimit = headPos + headLength;
    }

    public void reset(int pos, int length){
        this.pos = pos;
        this.limit = pos + length;
    }

    public void reset(){
        pos = offset;
        bitPos = offset;
        limit = offset;
        headPos = offset;
        headBitPos = offset;
        headLimit = offset;
    }

    public int readInt(){
        if (readRawByte() == 0){//常用类型，读3位
            if (bitPos % 8 < 6 || bitPos == 0){//要加的这3个bit在这个8个bit之内
                byte b;
                if (bitPos % 8 == 5){
                    b = (byte)((buffer[pos++] >> (5 - bitPos % 8)) & 7);
                    bitPos += 3;
                    return b;
                }else {
                    b = (byte)((buffer[pos] >> (5 - bitPos % 8)) & 7);
                    bitPos += 3;
                    return b;
                }
            }else {//需要进位取
                if (pos < limit){
                    if (bitPos % 8 == 6){
                        bitPos += 3;
                        return (byte)(((buffer[pos++] & 3) << 1) | ((buffer[pos] >> 7) & 1));
                    }else if (bitPos % 8 == 7){
                        bitPos += 3;
                        return (byte)((buffer[pos++] & 1) << 2 | (buffer[pos] >> 6) & 3);
                    }else {
                        throw new PBException("out of index");
                    }
                }else {
                    throw new PBException("out of index");
                }
            }
        }else{//非常用类型读6位
            if (bitPos % 8 < 3 || bitPos == 0){//要加的这6个bit在这个8个bit之内
                byte b;
                if (bitPos % 8 == 2){
                    b = (byte)((buffer[pos++] >> (2 - bitPos % 8)) & 63);
                    bitPos += 6;
                    return b;
                }else {
                    b = (byte)((buffer[pos] >> (2 - bitPos % 8)) & 63);
                    bitPos += 6;
                    return b;
                }
            }else {//需要进位取
                if (pos < limit){
                    if (bitPos % 8 == 3){
                        bitPos += 6;
                        return (byte)(((buffer[pos++] & 31) << 1) | (buffer[pos] >> 7 & 1));
                    }else if (bitPos % 8 == 4){
                        bitPos += 6;
                        return (byte)(((buffer[pos++] & 15) << 2) | ((buffer[pos] >> 6) & 3));
                    }else if (bitPos % 8 == 5){
                        bitPos += 6;
                        return (byte)(((buffer[pos++] & 7) << 3) | ((buffer[pos] >> 5) & 7));
                    }else if (bitPos % 8 == 6){
                        bitPos += 6;
                        return (byte)(((buffer[pos++] & 3) << 4) | ((buffer[pos] >> 4) & 15));
                    }else if (bitPos % 8 == 7){
                        bitPos += 6;
                        return (byte)(((buffer[pos++] & 1) << 5) | ((buffer[pos] >> 3) & 31));
                    }else {
                        throw new PBException("out of index");
                    }
                }else {
                    throw new PBException("out of index");
                }
            }
        }
    }

    private int readRawByte(){
        if (this.headPos < this.headLimit){
            if (headBitPos % 8 == 0 && headBitPos != 0){
                headPos++;
            }
            return ((byte)(buffer[headPos] >> (7 - (headBitPos++ % 8))) & 1);
        }else {
            throw new PBException("out of index");
        }
    }


}
