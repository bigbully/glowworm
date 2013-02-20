package com.jd.dd.glowworm.asm;

public class Type {

    //0~7为所有常用类型
    public static final int OBJECT = 0;
    public static final int BOOLEAN = 1;
    public static final int INT = 2;
    public static final int LONG = 3;
    public static final int DOUBLE = 4;
    public static final int STRING = 5;
    public static final int LIST_ARRAYLIST = 6;
    public static final int MAP_HASH = 7;

    //非常用类型按顺序往下排
    public static final int CHAR = 8;
    public static final int BYTE = 9;
    public static final int SHORT = 10;
    public static final int FLOAT = 11;

    public static final int BIGDECIMAL = 12;
    public static final int BIGINTEGER = 13;
    public static final int ENUM = 14;
    public static final int DATE = 15;
    public static final int TIMESTAMP = 16;
    public static final int INETADDRESS = 17;
    public static final int ATOMIC_BOOL = 18;
    public static final int ATOMIC_INT = 19;
    public static final int ATOMIC_LONG = 20;

    public static final int ARRAY = 21;
    public static final int MAP_LinkedHash = 22;
    public static final int MAP_ConcurrentHashMap = 23;
    public static final int LIST_LINKEDLIST = 24;
    public static final int COLLECTION_HASHSET = 25;
    public static final int COLLECTION_TREESET = 26;

    public static final int ARRAY_BOOLEAN = 27;
    public static final int ARRAY_BYTE = 28;
    public static final int ARRAY_CHAR = 29;
    public static final int ARRAY_DOUBLE = 30;
    public static final int ARRAY_FLOAT = 31;
    public static final int ARRAY_INT = 32;
    public static final int ARRAY_LONG = 33;
    public static final int ARRAY_SHORT = 34;

    public static final int CLASS = 35;
    public static final int LIST_ARRAYS_ARRAYLIST = 36;
    public static final int TIME = 37;
    public static final int EXCEPTION = 38;

    public static final int Unknown = 63;


    /**
     * The sort of this Java type.
     */
    private final int sort;

    /**
     * A buffer containing the internal name of this Java type. This field is only used for reference types.
     */
    private final char[] buf;

    /**
     * The offset of the internal name of this Java type in {@link #buf buf} or, for primitive types, the size,
     * descriptor and getOpcode offsets for this type (byte 0 contains the size, byte 1 the descriptor, byte 2 the
     * offset for IALOAD or IASTORE, byte 3 the offset for all other instructions).
     */
    private final int off;

    /**
     * The length of the internal name of this Java type.
     */
    private final int len;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs a reference type.
     *
     * @param sort the sort of the reference type to be constructed.
     * @param buf  a buffer containing the descriptor of the previous type.
     * @param off  the offset of this descriptor in the previous buffer.
     * @param len  the length of this descriptor.
     */
    private Type(final int sort, final char[] buf, final int off, final int len) {
        this.sort = sort;
        this.buf = buf;
        this.off = off;
        this.len = len;
    }

    /**
     * Returns the Java type corresponding to the given type descriptor.
     *
     * @param typeDescriptor a type descriptor.
     * @return the Java type corresponding to the given type descriptor.
     */
    public static Type getType(final String typeDescriptor) {
        return getType(typeDescriptor.toCharArray(), 0);
    }

    /**
     * Computes the size of the arguments and of the return value of a method.
     *
     * @param desc the descriptor of a method.
     * @return the size of the arguments of the method (plus one for the implicit this argument), argSize, and the size
     *         of its return value, retSize, packed into a single int i = <tt>(argSize << 2) | retSize</tt> (argSize is
     *         therefore equal to <tt>i >> 2</tt>, and retSize to <tt>i & 0x03</tt>).
     */
    public static int getArgumentsAndReturnSizes(final String desc) {
        int n = 1;
        int c = 1;
        while (true) {
            char car = desc.charAt(c++);
            if (car == ')') {
                car = desc.charAt(c);
                return n << 2 | (car == 'V' ? 0 : (car == 'D' || car == 'J' ? 2 : 1));
            } else if (car == 'L') {
                while (desc.charAt(c++) != ';') {
                }
                n += 1;
            } else if (car == '[') {
                while ((car = desc.charAt(c)) == '[') {
                    ++c;
                }
                if (car == 'D' || car == 'J') {
                    n -= 1;
                }
            } else if (car == 'D' || car == 'J') {
                n += 2;
            } else {
                n += 1;
            }
        }
    }

    /**
     * Returns the Java type corresponding to the given type descriptor.
     *
     * @param buf a buffer containing a type descriptor.
     * @param off the offset of this descriptor in the previous buffer.
     * @return the Java type corresponding to the given type descriptor.
     */
    private static Type getType(final char[] buf, final int off) {
        int len;
        switch (buf[off]) {
            case '[':
                len = 1;
                while (buf[off + len] == '[') {
                    ++len;
                }
                if (buf[off + len] == 'L') {
                    ++len;
                    while (buf[off + len] != ';') {
                        ++len;
                    }
                }
                return new Type(ARRAY, buf, off, len + 1);
            // case 'L':
            default:
                len = 1;
                while (buf[off + len] != ';') {
                    ++len;
                }
                return new Type(OBJECT, buf, off + 1, len - 1);
        }
    }

    // ------------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------------

    public int getSort() {
        return sort;
    }

    /**
     * Returns the internal name of the class corresponding to this object or array type. The internal name of a class
     * is its fully qualified name (as returned by Class.getName(), where '.' are replaced by '/'. This method should
     * only be used for an object or array type.
     *
     * @return the internal name of the class corresponding to this object type.
     */
    public String getInternalName() {
        return new String(buf, off, len);
    }

    // ------------------------------------------------------------------------
    // Conversion to type descriptors
    // ------------------------------------------------------------------------

    /**
     * Returns the descriptor corresponding to this Java type.
     *
     * @return the descriptor corresponding to this Java type.
     */
    public String getDescriptor() {
        StringBuffer buf = new StringBuffer();

        if (this.buf == null) {
            // descriptor is in byte 3 of 'off' for primitive types (buf == null)
            buf.append((char) ((off & 0xFF000000) >>> 24));
        } else if (sort == ARRAY) {
            buf.append(this.buf, off, len);
        } else { // sort == OBJECT
            buf.append('L');
            buf.append(this.buf, off, len);
            buf.append(';');
        }

        return buf.toString();
    }
}
