package javabean;

import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;


public class Glowworm_ASM_LoopPerson1 {


    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) throws Exception {
        String a;
        if (deserializer.isObjectExist()) {
            a = deserializer.scanString();
        }else {
            a = null;
        }

        return null;
    }
}
