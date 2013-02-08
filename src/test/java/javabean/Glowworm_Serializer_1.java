package javabean;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;
import com.jd.dd.glowworm.serializer.multi.ArraySerializer;
import com.jd.dd.glowworm.serializer.multi.MapSerializer;
import userJavabean.User1;
import userJavabean.User3;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

public class Glowworm_Serializer_1 implements ObjectSerializer {

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        Date value = (Date) object;
        serializer.writeLong(value.getTime());
    }
}
