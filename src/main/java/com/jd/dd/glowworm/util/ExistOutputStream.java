package com.jd.dd.glowworm.util;

public class ExistOutputStream {

    byte[] buffer;
    int offset;
    int limit;
    int pos;
    int bitPos;

    public ExistOutputStream(int size) {
        this(new byte[size]);
    }

    public ExistOutputStream(byte[] buffer) {
        this.buffer = buffer;
        this.limit = buffer.length;
    }

    public void write(boolean bool) {
        if (bitPos != 0 && bitPos % 8 == 0){
            pos++;
            checkCapacity(pos);
        }
        byte b1 = bitPos % 8 == 0 ? 0:buffer[pos];
        if (bool){
            bitPos++;
        }else {
            b1 = (byte)(b1 | (1 << (7 - (bitPos++ % 8))));
        }
        buffer[pos] = b1;
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
        this.bitPos = this.offset;
    }

    public byte[] getBytes(){
        if (this.bitPos == 0){
            return null;
        }else {
            byte[] retBytes = new byte[pos + 1];
            System.arraycopy(buffer, 0, retBytes, 0, pos + 1);
            return retBytes;
        }
    }

}
