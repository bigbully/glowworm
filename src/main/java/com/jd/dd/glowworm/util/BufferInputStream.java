package com.jd.dd.glowworm.util;


import java.io.IOException;

public final class BufferInputStream {
    byte[] buffer;
    int limit;
    int pos;
    int mark;

    public BufferInputStream(byte[] data) {
        this(data, 0, data.length);
    }

    public BufferInputStream(Buffer sequence) {
        this(sequence.getData(), sequence.getOffset(), sequence.getLength());
    }

    public BufferInputStream(byte[] data, int offset, int size) {
        this.buffer = data;
        this.mark = offset;
        this.pos = offset;
        this.limit = (offset + size);
    }

    public BufferInputStream(byte[] data, int offset) {
        this.buffer = data;
        this.mark = offset;
        this.pos = offset;
        this.limit = (offset + data.length);
    }

    public int read() throws IOException {
        if (this.pos < this.limit)
            return (this.buffer[(this.pos++)] & 0xFF);

        return -1;
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) {
        if (this.pos < this.limit) {
            len = Math.min(len, this.limit - this.pos);
            System.arraycopy(this.buffer, this.pos, b, off, len);
            this.pos += len;
            return len;
        }
        return -1;
    }

    public Buffer readBuffer(int len) {
        Buffer rc = null;
        if (this.pos < this.limit) {
            len = Math.min(len, this.limit - this.pos);
            rc = new Buffer(this.buffer, this.pos, len);
            this.pos += len;
        }
        return rc;
    }

    public long skip(long len) throws IOException {
        if (this.pos < this.limit) {
            len = Math.min(len, this.limit - this.pos);
            if (len > 0L) {
                BufferInputStream tmp33_32 = this;
                tmp33_32.pos = (int) (tmp33_32.pos + len);
            }
            return len;
        }
        return -1L;
    }

    public int available() {
        return (this.limit - this.pos);
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int markpos) {
        this.mark = this.pos;
    }

    public void reset() {
        this.pos = this.mark;
    }

    public void setNewData(byte[] data) {
        this.buffer = data;
        this.mark = 0;
        this.pos = 0;
        this.limit = (0+ data.length);
    }
}
