package com.jd.dd.glowworm.serializer;

import java.io.IOException;

public interface ObjectSerializer {

    void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException;
}
