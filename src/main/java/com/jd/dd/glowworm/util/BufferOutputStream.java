package com.jd.dd.glowworm.util;


import java.io.IOException;

public final class BufferOutputStream {
    byte[] buffer;
    int offset;
    int limit;
    int pos;

    public BufferOutputStream(int size) {
        this(new byte[size]);
    }

    public BufferOutputStream(byte[] buffer) {
        this.buffer = buffer;
        this.limit = buffer.length;
    }

    public BufferOutputStream(Buffer data) {
        this.buffer = data.data;
        this.pos = (this.offset = data.offset);
        this.limit = (data.offset + data.length);
    }

    public void write(int b) {
        int newPos = this.pos + 1;
        checkCapacity(newPos);
        this.buffer[this.pos] = (byte) b;
        this.pos = newPos;
    }

    public void write(byte[] b, int off, int len) {
        int newPos = this.pos + len;
        checkCapacity(newPos);
        System.arraycopy(b, off, this.buffer, this.pos, len);
        this.pos = newPos;
    }

    public Buffer getNextBuffer(int len) throws IOException {
        int newPos = this.pos + len;
        checkCapacity(newPos);
        return new Buffer(this.buffer, this.pos, len);
    }

    private void checkCapacity(int minimumCapacity) {
        if (minimumCapacity > this.limit) {
            byte[] tmpNewBytes = new byte[minimumCapacity + 1024];
            System.arraycopy(this.buffer, 0, tmpNewBytes, 0, this.limit);
            this.buffer = tmpNewBytes;
            this.limit = tmpNewBytes.length;
        }
    }

    public void reset() {
        this.pos = this.offset;
    }

    public Buffer toBuffer() {
        return new Buffer(this.buffer, this.offset, this.pos);
    }

    public byte[] toByteArray() {
        return toBuffer().toByteArray();
    }


    public byte[] getBytes() {
        if (this.pos == 0) {
            return null;
        } else {
            byte[] retBytes = new byte[this.pos];
            System.arraycopy(this.buffer, 0, retBytes, 0, this.pos);
            return retBytes;
        }
    }

    public int size() {
        return (this.offset - this.pos);
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
