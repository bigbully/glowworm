package com.jd.dd.glowworm.util;


import java.util.Iterator;
import java.util.List;

public class Buffer
        implements Comparable<Buffer> {
    public final byte[] data;
    public final int offset;
    public final int length;

    public Buffer(Buffer other) {
        this(other.data, other.offset, other.length);
    }

    public Buffer(byte[] data) {
        this(data, 0, data.length);
    }

    public Buffer(byte[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public final Buffer slice(int low, int high) {
        int sz;
        if (high < 0)
            sz = this.length + high;
        else {
            sz = high - low;
        }

        if (sz < 0) {
            sz = 0;
        }

        return new Buffer(this.data, this.offset + low, sz);
    }

    public final byte[] getData() {
        return this.data;
    }

    public final int getLength() {
        return this.length;
    }

    public final int getOffset() {
        return this.offset;
    }

    public Buffer compact() {
        if (this.length != this.data.length)
            return new Buffer(toByteArray());

        return this;
    }

    public final byte[] toByteArray() {
        byte[] data = this.data;
        int length = this.length;
        if (length != data.length) {
            byte[] t = new byte[length];
            System.arraycopy(data, this.offset, t, 0, length);
            data = t;
        }
        return data;
    }

    public byte byteAt(int i) {
        return this.data[(this.offset + i)];
    }

    public int hashCode() {
        byte[] target = new byte[4];
        for (int i = 0; i < this.length; ++i) {
            int tmp18_17 = (i % 4);
            byte[] tmp18_14 = target;
            tmp18_14[tmp18_17] = (byte) (tmp18_14[tmp18_17] ^ this.data[(this.offset + i)]);
        }
        return (target[0] << 24 | target[1] << 16 | target[2] << 8 | target[3]);
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if ((obj == null) || (obj.getClass() != Buffer.class))
            return false;

        return equals((Buffer) obj);
    }

    public final boolean equals(Buffer obj) {
        if (this.length != obj.length)
            return false;

        for (int i = 0; i < this.length; ++i)
            if (obj.data[(obj.offset + i)] != this.data[(this.offset + i)])
                return false;


        return true;
    }

    public final BufferInputStream newInput() {
        return new BufferInputStream(this);
    }

    public final BufferOutputStream newOutput() {
        return new BufferOutputStream(this);
    }

    public final boolean isEmpty() {
        return (this.length == 0);
    }

    public final boolean contains(byte value) {
        return (indexOf(value, 0) >= 0);
    }

    public final int indexOf(byte value, int pos) {
        for (int i = pos; i < this.length; ++i)
            if (this.data[(this.offset + i)] == value)
                return i;


        return -1;
    }

    public static final Buffer join(List<Buffer> items, Buffer seperator) {
        if (items.isEmpty())
            return new Buffer(seperator.data, 0, 0);

        int size = 0;
        for (Iterator i$ = items.iterator(); i$.hasNext(); ) {
            Buffer item = (Buffer) i$.next();
            size += item.length;
        }
        size += seperator.length * (items.size() - 1);

        int pos = 0;
        byte[] data = new byte[size];
        for (Iterator i$ = items.iterator(); i$.hasNext(); ) {
            Buffer item = (Buffer) i$.next();
            if (pos != 0) {
                System.arraycopy(seperator.data, seperator.offset, data, pos, seperator.length);
                pos += seperator.length;
            }
            System.arraycopy(item.data, item.offset, data, pos, item.length);
            pos += item.length;
        }

        return new Buffer(data, 0, size);
    }

    public int compareTo(Buffer o) {
        byte b1;
        byte b2;
        int minLength = Math.min(this.length, o.length);
        if (this.offset == o.offset) {
            int pos = this.offset;
            int limit = minLength + this.offset;
            while (pos < limit) {
                b1 = this.data[pos];
                b2 = o.data[pos];
                if (b1 != b2)
                    return (b1 - b2);

                ++pos;
            }
        } else {
            int offset1 = this.offset;
            int offset2 = o.offset;
            while (minLength-- != 0) {
                b1 = this.data[(offset1++)];
                b2 = o.data[(offset2++)];
                if (b1 != b2)
                    return (b1 - b2);
            }
        }

        return (this.length - o.length);
    }
}
