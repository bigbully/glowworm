package com.jd.dd.glowworm.deserializer;

import java.lang.reflect.Type;

public interface ObjectDeserializer {

    <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams);
}
