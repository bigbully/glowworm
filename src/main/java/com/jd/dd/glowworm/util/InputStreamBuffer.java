package com.jd.dd.glowworm.util;

public class InputStreamBuffer {

    private CodedInputStream theCodedInputStream;
    private ExistInputStream existStream;
    private TypeInputStream typeStream;

    public InputStreamBuffer(CodedInputStream theCodedInputStream, ExistInputStream existStream, TypeInputStream typeStream) {
        this.theCodedInputStream = theCodedInputStream;
        this.existStream = existStream;
        this.typeStream = typeStream;
    }

    public CodedInputStream getTheCodedInputStream() {
        return theCodedInputStream;
    }

    public ExistInputStream getExistStream() {
        return existStream;
    }

    public TypeInputStream getTypeStream() {
        return typeStream;
    }

    public void setAll(CodedInputStream theCodedInputStream, ExistInputStream existStream, TypeInputStream typeStream){
        this.theCodedInputStream = theCodedInputStream;
        this.existStream = existStream;
        this.typeStream = typeStream;
    }

}
