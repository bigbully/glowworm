package userJavabean;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;
import com.jd.dd.glowworm.serializer.normal.StringSerializer;

import java.io.IOException;
import java.util.Map;

public class Glowworm_Serializer_1 implements ObjectSerializer {

    public ObjectSerializer abcSerializer;

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
//        StringSerializer.instance.write(serializer, object, needWriteType, Map<String, String>);
    }
}
