package com.jd.dd.glowworm.serializer;

import java.io.IOException;
import java.lang.reflect.Type;

public interface ObjectSerializer {

    void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException;
}
