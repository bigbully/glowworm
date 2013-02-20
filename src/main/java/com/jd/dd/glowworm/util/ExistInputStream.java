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
