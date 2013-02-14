package com.jd.dd.glowworm.serializer.asm;

import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.asm.*;
import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;
import com.jd.dd.glowworm.serializer.multi.ArraySerializer;
import com.jd.dd.glowworm.serializer.multi.ListSerializer;
import com.jd.dd.glowworm.serializer.multi.MapSerializer;
import com.jd.dd.glowworm.serializer.multi.SetSerializer;
import com.jd.dd.glowworm.util.ASMClassLoader;
import com.jd.dd.glowworm.util.ASMUtils;
import com.jd.dd.glowworm.util.FieldInfo;
import com.jd.dd.glowworm.util.TypeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ASMSerializerFactory implements Opcodes {

    public static String GenClassName_prefix = "Glowworm_Serializer_";
    private ASMClassLoader classLoader = new ASMClassLoader();

    private static final ASMSerializerFactory instance = new ASMSerializerFactory();

    public final static ASMSerializerFactory getInstance() {
        return instance;
    }

    public ObjectSerializer createJavaBeanSerializer(Class<?> clazz) throws Exception {
        return createJavaBeanSerializer(clazz, null);
    }

    private ObjectSerializer createJavaBeanSerializer(Class<?> clazz, Map<String, String> aliasMap) throws Exception {
        if (clazz.isPrimitive()) {
            throw new PBException("unsupportd class " + clazz.getName());
        }

        List<FieldInfo> getters = TypeUtils.computeGetters(clazz, aliasMap, true);

        String className = getGenClassName(clazz);

        ClassWriter cw = new ClassWriter();

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, "java/lang/Object",
                new String[]{"com/jd/dd/glowworm/serializer/ObjectSerializer"});

//        for (FieldInfo fieldInfo : getters) {
//            FieldVisitor fw = cw.visitField(ACC_PUBLIC, fieldInfo.getName() + "_asm_fieldType",
//                    "Ljava/lang/reflect/Type;");
//            fw.visitEnd();
//        }

        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");

        mw.visitInsn(RETURN);
        mw.visitMaxs(4, 4);
        mw.visitEnd();

        {
            Context context = new Context(className);

            mw = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "write", "(Lcom/jd/dd/glowworm/serializer/PBSerializer;Ljava/lang/Object;Z[Ljava/lang/Object;)V", null, new String[]{"java/io/IOException"});

            mw.visitVarInsn(ALOAD, context.obj()); // obj
            mw.visitTypeInsn(CHECKCAST, ASMUtils.getType(clazz)); // serializer
            mw.visitVarInsn(ASTORE, context.var("entity")); //当前asm序列化的对象

            generateWriteMethod(clazz, mw, getters, context);

            mw.visitInsn(RETURN);
            mw.visitMaxs(8, context.getVariantCount() + 2);
            mw.visitEnd();
        }

        byte[] code = cw.toByteArray();

//        org.apache.commons.io.IOUtils.write(code, new java.io.FileOutputStream(
//                "D:/" + className + ".class"));

        Class<?> exampleClass = classLoader.defineClassPublic(className, code, 0, code.length);
        Object instance = exampleClass.newInstance();

        return (ObjectSerializer) instance;
    }

    private void generateWriteMethod(Class<?> clazz, MethodVisitor mw, List<FieldInfo> getters, Context context)
            throws Exception {
        int size = getters.size();

        for (int i = 0; i < size; ++i) {
            FieldInfo property = getters.get(i);
            Class<?> propertyClass = property.getFieldClass();

            if (propertyClass == byte.class) {
                _byte(clazz, mw, property, context);
            } else if (propertyClass == short.class) {
                _short(clazz, mw, property, context);
            } else if (propertyClass == int.class) {
                _int(clazz, mw, property, context);
            } else if (propertyClass == long.class) {
                _long(clazz, mw, property, context);
            } else if (propertyClass == float.class) {
                _float(clazz, mw, property, context);
            } else if (propertyClass == double.class) {
                _double(clazz, mw, property, context);
            } else if (propertyClass == boolean.class) {
                _boolean(clazz, mw, property, context);
            } else if (propertyClass == char.class) {
                _char(clazz, mw, property, context);
            } else if (propertyClass == Date.class) {
                _date(clazz, mw, property, context);
            } else if (propertyClass == String.class) {
                _string(clazz, mw, property, context, i);
            } else if (propertyClass.isArray()) {
                if (property.getFieldClass().getComponentType().isPrimitive()){//如果是如boolean[]这样的基础类型数组直接走_Object
                    _object(mw, property, context);
                }else {
                    _array(clazz, mw, property, context, i);
                }
            } else if (List.class.isAssignableFrom(propertyClass)) {
                _list(clazz, mw, property, context, i);
            } else if (Set.class.isAssignableFrom(propertyClass)) {
                _set(clazz, mw, property, context, i);
            } else if (Map.class.isAssignableFrom(propertyClass)) {
                _map(clazz, mw, property, context, i);
            } else {
                _object(mw, property, context);
            }
        }
    }

    private void _date(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();
        _get(mw, context, property);

        mw.visitVarInsn(ASTORE, context.var("date"));

        Label _else = new Label();
        Label _end_if = new Label();

        // if (value == null) {
        mw.visitVarInsn(ALOAD, context.var("date"));
        mw.visitJumpInsn(IFNONNULL, _else);

        write_null(mw, property, context);

        mw.visitJumpInsn(GOTO, _end_if);

        mw.visitLabel(_else); // else { out.writeFieldValue(seperator, fieldName, fieldValue)
        write_not_null(mw, property, context);

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("date"));
        mw.visitMethodInsn(INVOKEVIRTUAL, "java/util/Date", "getTime", "()J");
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeLong", "(J)V");

        mw.visitLabel(_end_if);

        mw.visitLabel(_end);

    }

    private void _char(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(ISTORE, context.var("char"));

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");

        mw.visitVarInsn(ILOAD, context.var("char"));
        mw.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
        mw.visitVarInsn(ASTORE, context.var("char_obj"));
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("char_obj"));
        mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "toString", "()Ljava/lang/String;");
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeStringWithCharset", "(Ljava/lang/String;)V");

        mw.visitLabel(_end);
    }

    private void _byte(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(ISTORE, context.var("byte"));

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ILOAD, context.var("byte"));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeByte", "(B)V");

        mw.visitLabel(_end);
    }

    private void _float(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(FSTORE, context.var("float"));

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(FLOAD, context.var("float"));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeFloat", "(F)V");

        mw.visitLabel(_end);
    }

    private void _list(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context, int i) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(ASTORE, context.var("object"));

        //判断非空或引用
        Label if_ref = new Label();
        Label else_ref = new Label();
        Label if_null = new Label();
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitJumpInsn(IFNULL, if_null);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitInsn(ACONST_NULL);
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "needConsiderRef", "("+ASMUtils.getDesc(ObjectSerializer.class)+")Z");
        mw.visitJumpInsn(IFEQ, if_ref);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "isReference", "(Ljava/lang/Object;)Z");
        mw.visitJumpInsn(IFEQ, if_ref);
        mw.visitLabel(if_null);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNull", "()V");
        mw.visitJumpInsn(GOTO, else_ref);
        mw.visitLabel(if_ref);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");



        mw.visitFieldInsn(GETSTATIC, ASMUtils.getType(ListSerializer.class), "instance", ASMUtils.getDesc(ListSerializer.class));
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitInsn(ICONST_0);
        mw.visitInsn(ICONST_2);
        mw.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mw.visitInsn(DUP);

        Class componentClazz;
        if (property.getFieldType() instanceof Class) {
            componentClazz = Object.class;
        } else {
            componentClazz = (Class) ((ParameterizedType) property.getFieldType()).getActualTypeArguments()[0];
        }
        mw.visitInsn(ICONST_0);
        mw.visitLdcInsn(Type.getType(ASMUtils.getDesc(componentClazz)));
        mw.visitInsn(AASTORE);
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_1);
        if (property.getFieldClass().isInterface()) {
            mw.visitInsn(ICONST_1);
        } else {
            mw.visitInsn(ICONST_0);
        }
        mw.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        mw.visitInsn(AASTORE);

        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(ListSerializer.class), "write", "(" + ASMUtils.getDesc(PBSerializer.class) + "Ljava/lang/Object;Z[Ljava/lang/Object;)V");

        mw.visitLabel(else_ref);
        mw.visitLabel(_end);
    }

    private void _set(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context, int i) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(ASTORE, context.var("object"));

        //判断非空或引用
        Label if_ref = new Label();
        Label else_ref = new Label();
        Label if_null = new Label();
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitJumpInsn(IFNULL, if_null);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitInsn(ACONST_NULL);
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "needConsiderRef", "("+ASMUtils.getDesc(ObjectSerializer.class)+")Z");
        mw.visitJumpInsn(IFEQ, if_ref);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "isReference", "(Ljava/lang/Object;)Z");
        mw.visitJumpInsn(IFEQ, if_ref);
        mw.visitLabel(if_null);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNull", "()V");
        mw.visitJumpInsn(GOTO, else_ref);
        mw.visitLabel(if_ref);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");

        mw.visitFieldInsn(GETSTATIC, ASMUtils.getType(SetSerializer.class), "instance", ASMUtils.getDesc(SetSerializer.class));
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitInsn(ICONST_0);
        mw.visitInsn(ICONST_2);
        mw.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mw.visitInsn(DUP);

        Class componentClazz;
        if (property.getFieldType() instanceof Class) {
            componentClazz = Object.class;
        } else {
            componentClazz = (Class) ((ParameterizedType) property.getFieldType()).getActualTypeArguments()[0];
        }
        mw.visitInsn(ICONST_0);
        mw.visitLdcInsn(Type.getType(ASMUtils.getDesc(componentClazz)));
        mw.visitInsn(AASTORE);
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_1);
        if (property.getFieldClass().isInterface()) {
            mw.visitInsn(ICONST_1);
        } else {
            mw.visitInsn(ICONST_0);
        }
        mw.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        mw.visitInsn(AASTORE);

        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(SetSerializer.class), "write", "(" + ASMUtils.getDesc(PBSerializer.class) + "Ljava/lang/Object;Z[Ljava/lang/Object;)V");

        mw.visitLabel(else_ref);
        mw.visitLabel(_end);
    }

    private void _double(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(DSTORE, context.var("double", 2));

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(DLOAD, context.var("double", 2));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeDouble", "(D)V");

        mw.visitLabel(_end);
    }

    private void _long(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(LSTORE, context.var("long", 2));

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(LLOAD, context.var("long", 2));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeLong", "(J)V");

        mw.visitLabel(_end);
    }

    private void _string(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context, int i) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(ASTORE, context.var("string"));

        Label _else = new Label();
        Label _end_if = new Label();

        // if (value == null) {
        mw.visitVarInsn(ALOAD, context.var("string"));
        mw.visitJumpInsn(IFNONNULL, _else);

        write_null(mw, property, context);

        mw.visitJumpInsn(GOTO, _end_if);

        mw.visitLabel(_else); // else { out.writeFieldValue(seperator, fieldName, fieldValue)
        write_not_null(mw, property, context);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("string"));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeStringWithCharset",
                "(Ljava/lang/String;)V");

        mw.visitLabel(_end_if);

        mw.visitLabel(_end);
    }

    private void _int(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(ISTORE, context.var("int"));

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ILOAD, context.var("int"));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeInt", "(I)V");

        mw.visitLabel(_end);
    }

    private void _short(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(ISTORE, context.var("short"));

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ILOAD, context.var("short"));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeShort", "(S)V");

        mw.visitLabel(_end);
    }


    private void _array(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context, int i) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(ASTORE, context.var("object"));


        //判断非空或引用
        Label if_ref = new Label();
        Label else_ref = new Label();
        Label if_null = new Label();
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitJumpInsn(IFNULL, if_null);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitInsn(ACONST_NULL);
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "needConsiderRef", "("+ASMUtils.getDesc(ObjectSerializer.class)+")Z");
        mw.visitJumpInsn(IFEQ, if_ref);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "isReference", "(Ljava/lang/Object;)Z");
        mw.visitJumpInsn(IFEQ, if_ref);
        mw.visitLabel(if_null);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNull", "()V");
        mw.visitJumpInsn(GOTO, else_ref);
        mw.visitLabel(if_ref);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");


        mw.visitFieldInsn(GETSTATIC, ASMUtils.getType(ArraySerializer.class), "instance", ASMUtils.getDesc(ArraySerializer.class));
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitInsn(ICONST_0);
        mw.visitInsn(ICONST_1);
        mw.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_0);
        mw.visitLdcInsn(Type.getType(ASMUtils.getDesc(property.getFieldClass().getComponentType())));
        mw.visitInsn(AASTORE);
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(ArraySerializer.class), "write", "(" + ASMUtils.getDesc(PBSerializer.class) + "Ljava/lang/Object;Z[Ljava/lang/Object;)V");

        mw.visitLabel(else_ref);
        mw.visitLabel(_end);

    }



    private void _map(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context, int i) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(ASTORE, context.var("object"));

        //判断非空或引用
        Label if_ref = new Label();
        Label else_ref = new Label();
        Label if_null = new Label();
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitJumpInsn(IFNULL, if_null);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitInsn(ACONST_NULL);
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "needConsiderRef", "("+ASMUtils.getDesc(ObjectSerializer.class)+")Z");
        mw.visitJumpInsn(IFEQ, if_ref);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "isReference", "(Ljava/lang/Object;)Z");
        mw.visitJumpInsn(IFEQ, if_ref);
        mw.visitLabel(if_null);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNull", "()V");
        mw.visitJumpInsn(GOTO, else_ref);
        mw.visitLabel(if_ref);
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");

        mw.visitFieldInsn(GETSTATIC, ASMUtils.getType(MapSerializer.class), "instance", ASMUtils.getDesc(MapSerializer.class));
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("object"));
        mw.visitInsn(ICONST_0);
        mw.visitInsn(ICONST_3);
        mw.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mw.visitInsn(DUP);

        Class keyClazz;
        Class valueClazz;
        if (property.getFieldType() instanceof Class) {
            keyClazz = Object.class;
            valueClazz = Object.class;
        } else {
            keyClazz = (Class) ((ParameterizedType) property.getFieldType()).getActualTypeArguments()[0];
            valueClazz = (Class) ((ParameterizedType) property.getFieldType()).getActualTypeArguments()[1];
        }
        mw.visitInsn(ICONST_0);
        mw.visitLdcInsn(Type.getType(ASMUtils.getDesc(keyClazz)));
        mw.visitInsn(AASTORE);
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_1);
        mw.visitLdcInsn(Type.getType(ASMUtils.getDesc(valueClazz)));
        mw.visitInsn(AASTORE);
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_2);
        if (property.getFieldClass().isInterface()) {
            mw.visitInsn(ICONST_1);
        } else {
            mw.visitInsn(ICONST_0);
        }
        mw.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        mw.visitInsn(AASTORE);

        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(MapSerializer.class), "write", "(" + ASMUtils.getDesc(PBSerializer.class) + "Ljava/lang/Object;Z[Ljava/lang/Object;)V");

        mw.visitLabel(else_ref);
        mw.visitLabel(_end);

    }

    private void write_null(MethodVisitor mw, FieldInfo fieldInfo, Context context) {
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNull",
                "()V");
    }

    private void write_not_null(MethodVisitor mw, FieldInfo fieldInfo, Context context) {
        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull",
                "()V");
    }





    private void _object(MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(ASTORE, context.var("object"));

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ALOAD, context.var("object"));
        if (property.getFieldClass() == Object.class) {//如果是拿Object声明的，needWriteType=true
            mw.visitInsn(ICONST_1);
        } else {
            mw.visitInsn(ICONST_0);
        }
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeFieldObject", "(Ljava/lang/Object;Z)V");

        mw.visitLabel(_end);
    }

    private void _boolean(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _get(mw, context, property);
        mw.visitVarInsn(ISTORE, context.var("boolean"));

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeNotNull", "()V");

        mw.visitVarInsn(ALOAD, context.serializer());
        mw.visitVarInsn(ILOAD, context.var("boolean"));
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBSerializer.class), "writeBool", "(Z)V");

        mw.visitLabel(_end);
    }

    //这就是为什么要asm的原因，这个方法会生成get属性值得方法，而如果没有asm的话，获取属性值只能通过类反射，效率低下
    private void _get(MethodVisitor mw, Context context, FieldInfo property) {
        Method method = property.getMethod();
        if (method != null) {
            mw.visitVarInsn(ALOAD, context.var("entity"));
            mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(method.getDeclaringClass()), method.getName(), ASMUtils.getDesc(method));
        } else {
            mw.visitVarInsn(ALOAD, context.var("entity"));
            mw.visitFieldInsn(GETFIELD, ASMUtils.getType(property.getDeclaringClass()), property.getName(),
                    ASMUtils.getDesc(property.getFieldClass()));
        }
    }

    //这两个变量用于生成唯一的asm序列化器名字
    private final AtomicLong seed = new AtomicLong();

    public String getGenClassName(Class<?> clazz) {
        return GenClassName_prefix + seed.incrementAndGet();
    }

    //用来记录临时变量stack的工具类
    static class Context {

        private final String className;

        public Context(String className) {
            this.className = className;
        }

        private int variantIndex = 8;

        private Map<String, Integer> variants = new HashMap<String, Integer>();

        public int serializer() {
            return 1;
        }

        public String getClassName() {
            return className;
        }

        public int obj() {
            return 2;
        }

        public int paramFieldName() {
            return 3;
        }

        public int paramFieldType() {
            return 4;
        }

        public int fieldName() {
            return 5;
        }

        public int original() {
            return 6;
        }

        public int processValue() {
            return 7;
        }

        public int getVariantCount() {
            return variantIndex;
        }

        public int var(String name) {
            Integer i = variants.get(name);
            if (i == null) {
                variants.put(name, variantIndex++);
            }
            i = variants.get(name);
            return i.intValue();
        }

        public int var(String name, int increment) {
            Integer i = variants.get(name);
            if (i == null) {
                variants.put(name, variantIndex);
                variantIndex += increment;
            }
            i = variants.get(name);
            return i.intValue();
        }
    }
}
