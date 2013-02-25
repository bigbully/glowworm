/*
* Copyright 360buy
*
*    Licensed under the Apache License, Version 2.0 (the "License");
*    you may not use this file except in compliance with the License.
*    You may obtain a copy of the License at
*
*        http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS,
*    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*    See the License for the specific language governing permissions and
*    limitations under the License.
*/
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
