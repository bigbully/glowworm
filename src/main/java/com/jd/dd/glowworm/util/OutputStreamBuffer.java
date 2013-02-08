package com.jd.dd.glowworm.util;

public class OutputStreamBuffer {

    private CodedOutputStream theCodedOutputStream;
    private CodedOutputStream refStream;
    private ExistOutputStream existStream;
    private TypeOutputStream typeStream;
    private CodedOutputStream headStream;

    public OutputStreamBuffer(CodedOutputStream theCodedOutputStream, CodedOutputStream refStream, ExistOutputStream existStream, TypeOutputStream typeStream, CodedOutputStream headStream) {
        this.theCodedOutputStream = theCodedOutputStream;
        this.refStream = refStream;
        this.existStream = existStream;
        this.typeStream = typeStream;
        this.headStream = headStream;
    }

    public CodedOutputStream getTheCodedOutputStream() {
        return theCodedOutputStream;
    }

    public CodedOutputStream getRefStream() {
        return refStream;
    }

    public ExistOutputStream getExistStream() {
        return existStream;
    }

    public TypeOutputStream getTypeStream() {
        return typeStream;
    }

    public CodedOutputStream getHeadStream() {
        return headStream;
    }

    public void setAll(CodedOutputStream theCodedOutputStream, CodedOutputStream refStream, ExistOutputStream existStream, TypeOutputStream typeStream, CodedOutputStream headStream) {
        this.theCodedOutputStream = theCodedOutputStream;
        this.refStream = refStream;
        this.existStream = existStream;
        this.typeStream = typeStream;
        this.headStream = headStream;
    }
}
