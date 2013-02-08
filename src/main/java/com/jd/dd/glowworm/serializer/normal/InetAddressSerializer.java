
package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;

public class InetAddressSerializer implements ObjectSerializer {

    public static InetAddressSerializer instance = new InetAddressSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.INETADDRESS);
        }
        serializer.writeString(((InetAddress) object).getHostAddress());
    }
}
