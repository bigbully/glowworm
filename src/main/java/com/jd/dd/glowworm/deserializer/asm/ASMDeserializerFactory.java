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
package com.jd.dd.glowworm.deserializer.asm;

import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.asm.*;
import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;
import com.jd.dd.glowworm.deserializer.multi.ArrayDeserializer;
import com.jd.dd.glowworm.deserializer.multi.ListDeserializer;
import com.jd.dd.glowworm.deserializer.multi.MapDeserializer;
import com.jd.dd.glowworm.deserializer.multi.SetDeserializer;
import com.jd.dd.glowworm.deserializer.reflect.FieldDeserializer;
import com.jd.dd.glowworm.deserializer.reflect.JavaBeanDeserializer;
import com.jd.dd.glowworm.util.*;

import java.lang.reflect.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.jd.dd.glowworm.util.ASMUtils.getDesc;
import static com.jd.dd.glowworm.util.ASMUtils.getType;

public class ASMDeserializerFactory implements Opcodes {

    public static String DeserializerClassName_prefix = "Glowworm_ASM_";

    private static final ASMDeserializerFactory instance = new ASMDeserializerFactory();

    private ASMClassLoader classLoader = new ASMClassLoader();

    public final static ASMDeserializerFactory getInstance() {
        return instance;
    }

    public ObjectDeserializer createJavaBeanDeserializer(PBDeserializer config, Class<?> clazz, Type type) throws Exception {
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("not support type :" + clazz.getName());
        }

        String className = getGenClassName(clazz);

        ClassWriter cw = new ClassWriter();
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, getType(ASMJavaBeanDeserializer.class), null);

        DeserializeBeanInfo beanInfo = DeserializeBeanInfo.computeSetters(clazz, type);

        _init(cw, new Context(className, config, beanInfo, 3));
        _createInstance(cw, new Context(className, config, beanInfo, 3));
        _deserialize(cw, new Context(className, config, beanInfo, 6));

        byte[] code = cw.toByteArray();

        org.apache.commons.io.IOUtils.write(code, new java.io.FileOutputStream(
                "/home/bigbully/" + className + ".class"));

        Class<?> exampleClass = classLoader.defineClassPublic(className, code, 0, code.length);

        Constructor<?> constructor = exampleClass.getConstructor(PBDeserializer.class, Class.class);
        Object instance = constructor.newInstance(config, clazz);

        return (ObjectDeserializer) instance;
    }

    //这个方法注意一下啊。如果某个属性最终需要调用PbDesiariazer.scanFieldObject()方法，那么就提前在整个类的反序列化器中把每个属性的序列化器存下来，以后就不用再从Map中查找了~
    //但是几种特殊类型因为没有自己对应的唯一的序列化器，所以就pass了~
    private void _init(ClassWriter cw, Context context) {
        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);
            Class<?> fieldClass = fieldInfo.getFieldClass();

            if (fieldClass.isPrimitive()) {
                continue;
            }
            if (List.class.isAssignableFrom(fieldClass)) {
                continue;
            }
            if (Set.class.isAssignableFrom(fieldClass)) {
                continue;
            }
            if (Map.class.isAssignableFrom(fieldClass)) {
                continue;
            }
            if (fieldClass.isArray() && !fieldClass.getComponentType().isPrimitive()) {
                continue;
            }
            FieldVisitor fw = cw.visitField(ACC_PUBLIC, fieldInfo.getName() + "_asm_deser__",
                    getDesc(ObjectDeserializer.class));
            fw.visitEnd();

        }

        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + getDesc(PBDeserializer.class)
                + getDesc(Class.class) + ")V", null, null);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitVarInsn(ALOAD, 1);
        mw.visitVarInsn(ALOAD, 2);
        mw.visitMethodInsn(INVOKESPECIAL, getType(ASMJavaBeanDeserializer.class), "<init>",
                "(" + getDesc(PBDeserializer.class) + getDesc(Class.class) + ")V");

        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, getType(ASMJavaBeanDeserializer.class), "serializer",
                getDesc(ASMJavaBeanDeserializer.InnerJavaBeanDeserializer.class));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JavaBeanDeserializer.class), "getFieldDeserializerMap",
                "()" + getDesc(Map.class));
        mw.visitInsn(POP);

        mw.visitInsn(RETURN);
        mw.visitMaxs(4, 4);
        mw.visitEnd();
    }

    private void _createInstance(ClassWriter cw, Context context) {
        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "createInstance", "(" + getDesc(PBDeserializer.class)
                + getDesc(Type.class) + ")Ljava/lang/Object;",
                null, null);

        mw.visitTypeInsn(NEW, getType(context.getClazz()));
        mw.visitInsn(DUP);
        mw.visitMethodInsn(INVOKESPECIAL, getType(context.getClazz()), "<init>", "()V");

        mw.visitInsn(ARETURN);
        mw.visitMaxs(3, 3);
        mw.visitEnd();
    }

    private void _deserialize(ClassWriter cw, Context context) {
        if (context.getFieldInfoList().size() == 0) {
            return;
        }

        //现在的架构暂时没发现遇到char或list就必须走以下这段逻辑的地方，或许以后会用到吧，到时候再说！
//        for (FieldInfo fieldInfo : context.getFieldInfoList()) {
//            Class<?> fieldClass = fieldInfo.getFieldClass();
//            Type fieldType = fieldInfo.getFieldType();
//
//            if (fieldClass == char.class) {
//                return;
//            }
//
//            if (Collection.class.isAssignableFrom(fieldClass)) {
//                if (fieldType instanceof ParameterizedType) {
//                    Type itemType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
//                    if (itemType instanceof Class) {
//                        continue;
//                    } else {
//                        return;
//                    }
//                } else {
//                    return;
//                }
//            }
//        }

        Collections.sort(context.getFieldInfoList());

        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "deserialize", "(" + ASMUtils.getDesc(PBDeserializer.class) + "Ljava/lang/reflect/Type;Z[Ljava/lang/Object;)Ljava/lang/Object;", "<T:Ljava/lang/Object;>(Lcom/jd/dd/glowworm/deserializer/PBDeserializer;Ljava/lang/reflect/Type;Z[Ljava/lang/Object;)TT;", null);

        //Label super_ = new Label();
        Label return_ = new Label();
        Label end_ = new Label();

        Constructor<?> defaultConstructor = context.getBeanInfo().getDefaultConstructor();


        //暂时忽略反序列化拿接口进行接收的情况
        // create instance
//        if (context.getClazz().isInterface()) {
//            mw.visitVarInsn(ALOAD, 0);
//            mw.visitVarInsn(ALOAD, 1);
//            mw.visitMethodInsn(INVOKESPECIAL, getType(ASMJavaBeanDeserializer.class), "createInstance",
//                    "(" + getDesc(PBDeserializer.class) + ")Ljava/lang/Object;");
//            mw.visitTypeInsn(CHECKCAST, getType(context.getClazz())); // cast
//            mw.visitVarInsn(ASTORE, context.var("instance"));
//        } else {
        if (defaultConstructor != null) {
            if (Modifier.isPublic(defaultConstructor.getModifiers())) {
                mw.visitTypeInsn(NEW, getType(context.getClazz()));
                mw.visitInsn(DUP);
                mw.visitMethodInsn(INVOKESPECIAL, getType(context.getClazz()), "<init>", "()V");

                mw.visitVarInsn(ASTORE, context.var("instance"));
            } else {
                mw.visitVarInsn(ALOAD, 0);
                mw.visitVarInsn(ALOAD, 1);
                mw.visitMethodInsn(INVOKESPECIAL, getType(ASMJavaBeanDeserializer.class), "createInstance",
                        "(" + getDesc(PBDeserializer.class) + ")Ljava/lang/Object;");
                mw.visitTypeInsn(CHECKCAST, getType(context.getClazz())); // cast
                mw.visitVarInsn(ASTORE, context.var("instance"));
            }
        } else {
            mw.visitInsn(ACONST_NULL);
            mw.visitTypeInsn(CHECKCAST, getType(context.getClazz())); // cast
            mw.visitVarInsn(ASTORE, context.var("instance"));
        }
//        }


        mw.visitVarInsn(ALOAD, 1);
        mw.visitVarInsn(ALOAD, context.var("instance"));
        mw.visitVarInsn(ALOAD, 0);
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(PBDeserializer.class), "addToObjectIndexMap", "(Ljava/lang/Object;" + getDesc(ObjectDeserializer.class) + ")V");

        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);
            Class<?> fieldClass = fieldInfo.getFieldClass();

            if (fieldClass == boolean.class) {
                _boolean(context, mw, fieldInfo);
            } else if (fieldClass == int.class) {
                _int(context, mw, fieldInfo);
            } else if (fieldClass == char.class) {
                _char(context, mw, fieldInfo);
            } else if (fieldClass == long.class) {
                _long(context, mw, fieldInfo);
            } else if (fieldClass == byte.class) {
                _byte(context, mw, fieldInfo);
            } else if (fieldClass == short.class) {
                _short(context, mw, fieldInfo);
            } else if (fieldClass == float.class) {
                _float(context, mw, fieldInfo);
            } else if (fieldClass == double.class) {
                _double(context, mw, fieldInfo);
            } else if (fieldClass == String.class) {
                _string(context, mw, fieldInfo);
            } else if (fieldClass == Date.class) {
                _date(context, mw, fieldInfo);
            } else if (fieldClass.isArray()) {
                if (fieldClass.getComponentType().isPrimitive()) {//如果是如boolean[]这样的基础类型数组直接走_Object
                    _deserialze_obj(context, mw, fieldInfo, fieldClass);
                    continue;
                } else {
                    _array(context, mw, fieldInfo, fieldClass);
                }
            } else if (Map.class.isAssignableFrom(fieldClass)) {
                _map(context, mw, fieldInfo, fieldClass);
            } else if (List.class.isAssignableFrom(fieldClass)) {
                _list(context, mw, fieldInfo, fieldClass);
            } else if (Set.class.isAssignableFrom(fieldClass)) {
                _set(context, mw, fieldInfo, fieldClass);
            } else {
                _deserialze_obj(context, mw, fieldInfo, fieldClass);
                continue;
            }
        }

        mw.visitLabel(end_);

        if (!context.getClazz().isInterface() && !Modifier.isAbstract(context.getClazz().getModifiers())) {
            if (defaultConstructor != null) {
                _batchSet(context, mw);
            } else {
                Constructor<?> creatorConstructor = context.getBeanInfo().getCreatorConstructor();
                if (creatorConstructor != null) {
                    mw.visitTypeInsn(NEW, getType(context.getClazz()));
                    mw.visitInsn(DUP);

                    _loadCreatorParameters(context, mw);

                    mw.visitMethodInsn(INVOKESPECIAL, getType(context.getClazz()), "<init>",
                            getDesc(creatorConstructor));
                    mw.visitVarInsn(ASTORE, context.var("instance"));
                } else {
                    Method factoryMethod = context.getBeanInfo().getFactoryMethod();
                    if (factoryMethod != null) {
                        _loadCreatorParameters(context, mw);
                        mw.visitMethodInsn(INVOKESTATIC, getType(factoryMethod.getDeclaringClass()),
                                factoryMethod.getName(), getDesc(factoryMethod));
                        mw.visitVarInsn(ASTORE, context.var("instance"));
                    } else {
                        throw new PBException("缺省无参的构造函数！");
                    }
                }
            }
        }

        mw.visitLabel(return_);

        mw.visitVarInsn(ALOAD, context.var("instance"));
        mw.visitInsn(ARETURN);

        /*mw.visitLabel(super_);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitVarInsn(ALOAD, 1);
        mw.visitVarInsn(ALOAD, 2);
        mw.visitVarInsn(ALOAD, 3);
        mw.visitMethodInsn(INVOKESPECIAL, getType(ASMJavaBeanDeserializer.class), "deserialze",
                           "(" + getDesc(PBDeserializer.class) + getDesc(Type.class)
                                   + "Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitInsn(ARETURN);*/

        int maxStack = context.getVariantCount();
        Constructor<?> creatorConstructor = context.getBeanInfo().getCreatorConstructor();
        if (creatorConstructor != null) {
            int constructorTypeStack = 2;
            for (Class<?> type : creatorConstructor.getParameterTypes()) {
                if (type == long.class || type == double.class) {
                    constructorTypeStack += 2;
                } else {
                    constructorTypeStack++;
                }
            }
            if (maxStack < constructorTypeStack) {
                maxStack = constructorTypeStack;
            }
        } else {
            Method factoryMethod = context.getBeanInfo().getFactoryMethod();
            if (factoryMethod != null) {
                int paramStacks = 2;
                for (Class<?> type : factoryMethod.getParameterTypes()) {
                    if (type == long.class || type == double.class) {
                        paramStacks += 2;
                    } else {
                        paramStacks++;
                    }
                }
                if (maxStack < paramStacks) {
                    maxStack = paramStacks;
                }
            }
        }

        //System.out.println(maxStack+"#"+context.getVariantCount());
        mw.visitMaxs(maxStack, context.getVariantCount());
        mw.visitEnd();
    }

    private void _date(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "isObjectExist", "()Z");
        Label if_not_null = new Label();
        mw.visitJumpInsn(IFEQ, if_not_null);
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "scanDate", "()Ljava/util/Date;");
        mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));
        Label if_null = new Label();
        mw.visitJumpInsn(GOTO, if_null);
        mw.visitLabel(if_not_null);
        mw.visitInsn(ACONST_NULL);
        mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));
        mw.visitLabel(if_null);
    }

    private void _string(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "isObjectExist", "()Z");
        Label _if_not_null = new Label();
        mw.visitJumpInsn(IFEQ, _if_not_null);
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "scanStringWithCharset", "()Ljava/lang/String;");
        mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));
        Label _else_null = new Label();
        mw.visitJumpInsn(GOTO, _else_null);
        mw.visitLabel(_if_not_null);
        mw.visitInsn(ACONST_NULL);
        mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));
        mw.visitLabel(_else_null);
    }

    private void _double(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "isObjectExist", "()Z");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(PBDeserializer.class), "scanDouble", "()D");
        mw.visitVarInsn(DSTORE, context.var(fieldInfo.getName() + "_asm", 2));
    }

    private void _float(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "isObjectExist", "()Z");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(PBDeserializer.class), "scanFloat", "()F");
        mw.visitVarInsn(FSTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _short(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "isObjectExist", "()Z");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(PBDeserializer.class), "scanShort", "()S");
        mw.visitVarInsn(ISTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _byte(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "isObjectExist", "()Z");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(PBDeserializer.class), "scanByte", "()B");
        mw.visitVarInsn(ISTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _long(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "isObjectExist", "()Z");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(PBDeserializer.class), "scanLong", "()J");
        mw.visitVarInsn(LSTORE, context.var(fieldInfo.getName() + "_asm", 2));
    }

    private void _char(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "isObjectExist", "()Z");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "scanStringWithCharset", "()Ljava/lang/String;");
        mw.visitVarInsn(ASTORE, context.var("tmpString"));
        mw.visitVarInsn(ALOAD, context.var("tmpString"));
        mw.visitMethodInsn(INVOKESTATIC, ASMUtils.getType(TypeUtils.class), "castToChar", "(Ljava/lang/Object;)Ljava/lang/Character;");
        mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");

        mw.visitVarInsn(ISTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _int(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "isObjectExist", "()Z");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(PBDeserializer.class), "scanInt", "()I");
        mw.visitVarInsn(ISTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _boolean(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(PBDeserializer.class), "isObjectExist", "()Z");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(PBDeserializer.class), "scanBool", "()Z");
        mw.visitVarInsn(ISTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _list(Context context, MethodVisitor mw, FieldInfo fieldInfo, Class<?> fieldClass) {
        mw.visitFieldInsn(GETSTATIC, ASMUtils.getType(ListDeserializer.class), "instance", ASMUtils.getDesc(ListDeserializer.class));
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitLdcInsn(com.jd.dd.glowworm.asm.Type.getType(ASMUtils.getDesc(fieldClass)));
        mw.visitInsn(ICONST_1);
        mw.visitInsn(ICONST_2);
        mw.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_0);
        Class componentClazz;
        if (fieldInfo.getFieldType() instanceof Class) {
            componentClazz = Object.class;
        } else {
            java.lang.reflect.Type componentType = ((ParameterizedType) fieldInfo.getFieldType()).getActualTypeArguments()[0];
            if (componentType instanceof Class){
                componentClazz = (Class)componentType;
            }else {
                componentClazz = Object.class;
            }
        }
        mw.visitLdcInsn(com.jd.dd.glowworm.asm.Type.getType(ASMUtils.getDesc(componentClazz)));
        mw.visitInsn(AASTORE);
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_1);
        if (fieldInfo.getFieldClass().isInterface()) {
            mw.visitInsn(ICONST_1);
        } else {
            mw.visitInsn(ICONST_0);
        }
        mw.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        mw.visitInsn(AASTORE);
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(ListDeserializer.class), "deserialize", "(" + ASMUtils.getDesc(PBDeserializer.class) + "Ljava/lang/reflect/Type;Z[Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitTypeInsn(CHECKCAST, ASMUtils.getType(fieldClass));
        mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _set(Context context, MethodVisitor mw, FieldInfo fieldInfo, Class<?> fieldClass) {
        mw.visitFieldInsn(GETSTATIC, ASMUtils.getType(SetDeserializer.class), "instance", ASMUtils.getDesc(SetDeserializer.class));
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitLdcInsn(com.jd.dd.glowworm.asm.Type.getType(ASMUtils.getDesc(fieldClass)));
        mw.visitInsn(ICONST_1);
        mw.visitInsn(ICONST_2);
        mw.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_0);
        Class componentClazz;
        if (fieldInfo.getFieldType() instanceof Class) {
            componentClazz = Object.class;
        } else {
            java.lang.reflect.Type componentType = ((ParameterizedType) fieldInfo.getFieldType()).getActualTypeArguments()[0];
            if (componentType instanceof Class){
                componentClazz = (Class)componentType;
            }else {
                componentClazz = Object.class;
            }
        }
        mw.visitLdcInsn(com.jd.dd.glowworm.asm.Type.getType(ASMUtils.getDesc(componentClazz)));
        mw.visitInsn(AASTORE);
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_1);
        if (fieldInfo.getFieldClass().isInterface()) {
            mw.visitInsn(ICONST_1);
        } else {
            mw.visitInsn(ICONST_0);
        }
        mw.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        mw.visitInsn(AASTORE);
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(SetDeserializer.class), "deserialize", "(" + ASMUtils.getDesc(PBDeserializer.class) + "Ljava/lang/reflect/Type;Z[Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitTypeInsn(CHECKCAST, ASMUtils.getType(fieldClass));
        mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _map(Context context, MethodVisitor mw, FieldInfo fieldInfo, Class<?> fieldClass) {
        mw.visitFieldInsn(GETSTATIC, ASMUtils.getType(MapDeserializer.class), "instance", ASMUtils.getDesc(MapDeserializer.class));
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitLdcInsn(com.jd.dd.glowworm.asm.Type.getType(ASMUtils.getDesc(fieldClass)));
        mw.visitInsn(ICONST_1);
        mw.visitInsn(ICONST_3);
        mw.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_0);
        Class keyClazz;
        Class valueClazz;
        if (fieldInfo.getFieldType() instanceof Class) {
            keyClazz = Object.class;
            valueClazz = Object.class;
        } else {
            java.lang.reflect.Type keyType = ((ParameterizedType) fieldInfo.getFieldType()).getActualTypeArguments()[0];
            java.lang.reflect.Type valueType = ((ParameterizedType) fieldInfo.getFieldType()).getActualTypeArguments()[1];
            if (keyType instanceof Class){
                keyClazz = (Class)keyType;
            }else {
                keyClazz = Object.class;
            }
            if (valueType instanceof Class){
                valueClazz = (Class)valueType;
            }else {
                valueClazz = Object.class;
            }
        }
        mw.visitLdcInsn(com.jd.dd.glowworm.asm.Type.getType(ASMUtils.getDesc(keyClazz)));
        mw.visitInsn(AASTORE);
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_1);
        mw.visitLdcInsn(com.jd.dd.glowworm.asm.Type.getType(ASMUtils.getDesc(valueClazz)));
        mw.visitInsn(AASTORE);
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_2);
        if (fieldInfo.getFieldClass().isInterface()) {
            mw.visitInsn(ICONST_1);
        } else {
            mw.visitInsn(ICONST_0);
        }
        mw.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        mw.visitInsn(AASTORE);
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(MapDeserializer.class), "deserialize", "(" + ASMUtils.getDesc(PBDeserializer.class) + "Ljava/lang/reflect/Type;Z[Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitTypeInsn(CHECKCAST, ASMUtils.getType(fieldClass));
        mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _array(Context context, MethodVisitor mw, FieldInfo fieldInfo, Class<?> fieldClass) {
        mw.visitFieldInsn(GETSTATIC, ASMUtils.getType(ArrayDeserializer.class), "instance", ASMUtils.getDesc(ArrayDeserializer.class));
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitLdcInsn(com.jd.dd.glowworm.asm.Type.getType(ASMUtils.getDesc(fieldClass)));
        mw.visitInsn(ICONST_1);
        mw.visitInsn(ICONST_1);
        mw.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mw.visitInsn(DUP);
        mw.visitInsn(ICONST_0);
        mw.visitLdcInsn(com.jd.dd.glowworm.asm.Type.getType(ASMUtils.getDesc(fieldClass.getComponentType())));
        mw.visitInsn(AASTORE);
        mw.visitMethodInsn(INVOKEVIRTUAL, ASMUtils.getType(ArrayDeserializer.class), "deserialize", "(" + ASMUtils.getDesc(PBDeserializer.class) + "Ljava/lang/reflect/Type;Z[Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitTypeInsn(CHECKCAST, ASMUtils.getDesc(fieldClass));
        mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _deserialze_obj(Context context, MethodVisitor mw, FieldInfo fieldInfo,
                                 Class<?> fieldClass) {
        Label notNull_ = new Label();
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__",
                getDesc(ObjectDeserializer.class));
        mw.visitJumpInsn(IFNONNULL, notNull_);

        mw.visitVarInsn(ALOAD, 0);
        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitLdcInsn(com.jd.dd.glowworm.asm.Type.getType(getDesc(fieldInfo.getFieldClass())));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(PBDeserializer.class), "getDeserializer",
                "(" + getDesc(Type.class) + ")" + getDesc(ObjectDeserializer.class));

        mw.visitFieldInsn(PUTFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__",
                getDesc(ObjectDeserializer.class));

        mw.visitLabel(notNull_);

        mw.visitVarInsn(ALOAD, context.deserializer());
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__",
                getDesc(ObjectDeserializer.class));
        if (fieldInfo.getFieldType() instanceof Class) {
            mw.visitLdcInsn(com.jd.dd.glowworm.asm.Type.getType(getDesc(fieldInfo.getFieldClass())));
        } else {
            mw.visitVarInsn(ALOAD, 0);
            mw.visitLdcInsn(fieldInfo.getName());
            mw.visitMethodInsn(INVOKEVIRTUAL, getType(ASMJavaBeanDeserializer.class), "getFieldType",
                    "(Ljava/lang/String;)Ljava/lang/reflect/Type;");
        }
        mw.visitInsn(ICONST_1);//因为这个是没有判别出类型的属性，所以需要在反序列化器中确认是否非空，所以needConfirmExist需要设定为true

        mw.visitMethodInsn(INVOKEVIRTUAL, getType(PBDeserializer.class), "scanFieldObject",
                "(" + ASMUtils.getDesc(ObjectDeserializer.class)
                        + getDesc(Type.class) + "Z)Ljava/lang/Object;");

        mw.visitTypeInsn(CHECKCAST, getType(fieldClass)); // cast
        mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _loadCreatorParameters(Context context, MethodVisitor mw) {
        List<FieldInfo> fieldInfoList = context.getBeanInfo().getFieldList();

        for (int i = 0, size = fieldInfoList.size(); i < size; ++i) {
            FieldInfo fieldInfo = fieldInfoList.get(i);
            Class<?> fieldClass = fieldInfo.getFieldClass();
            Type fieldType = fieldInfo.getFieldType();

            if (fieldClass == boolean.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == byte.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == short.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == int.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == long.class) {
                mw.visitVarInsn(LLOAD, context.var(fieldInfo.getName() + "_asm", 2));
            } else if (fieldClass == char.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == float.class) {
                mw.visitVarInsn(FLOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == double.class) {
                mw.visitVarInsn(DLOAD, context.var(fieldInfo.getName() + "_asm", 2));
            } else if (fieldClass == String.class) {
                mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass.isEnum()) {
                mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                Type itemType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
                if (itemType == String.class) {
                    mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
                    mw.visitTypeInsn(CHECKCAST, getType(fieldClass)); // cast
                } else {
                    mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
                }
            } else {
                mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
            }
        }
    }

    private void _batchSet(Context context, MethodVisitor mw) {
        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);
            Class<?> fieldClass = fieldInfo.getFieldClass();
            Type fieldType = fieldInfo.getFieldType();

            mw.visitVarInsn(ALOAD, context.var("instance"));
            if (fieldClass == boolean.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == byte.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == short.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == int.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == char.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == long.class) {
                mw.visitVarInsn(LLOAD, context.var(fieldInfo.getName() + "_asm", 2));
                mw.visitMethodInsn(INVOKEVIRTUAL, getType(context.getClazz()), fieldInfo.getMethod().getName(), "(J)V");
                continue;
            } else if (fieldClass == float.class) {
                mw.visitVarInsn(FLOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == double.class) {
                mw.visitVarInsn(DLOAD, context.var(fieldInfo.getName() + "_asm", 2));
            } else if (fieldClass == String.class) {
                mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass.isEnum()) {
                mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (List.class.isAssignableFrom(fieldClass)) {
                mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
            } else {
                mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
            }

            int INVAKE_TYPE;
            if (context.getClazz().isInterface()) {
                INVAKE_TYPE = INVOKEINTERFACE;
            } else {
                INVAKE_TYPE = INVOKEVIRTUAL;
            }
            if (fieldInfo.getMethod() != null) {
                mw.visitMethodInsn(INVAKE_TYPE, getType(fieldInfo.getDeclaringClass()),
                        fieldInfo.getMethod().getName(), getDesc(fieldInfo.getMethod()));

                if (!fieldInfo.getMethod().getReturnType().equals(Void.TYPE)) {
                    mw.visitInsn(POP);
                }
            } else {
                mw.visitFieldInsn(PUTFIELD, getType(fieldInfo.getDeclaringClass()), fieldInfo.getField().getName(),
                        getDesc(fieldInfo.getFieldClass()));
            }
        }
    }

    //用来生成唯一的反序列化器名字
    private final AtomicLong seed = new AtomicLong();

    public String getGenClassName(Class<?> clazz) {
        return DeserializerClassName_prefix + clazz.getSimpleName() + "_" + seed.incrementAndGet();
    }

    static class Context {

        private int variantIndex = 5;

        private Map<String, Integer> variants = new HashMap<String, Integer>();

        private Class<?> clazz;
        private final DeserializeBeanInfo beanInfo;
        private String className;
        private List<FieldInfo> fieldInfoList;

        public int deserializer() {
            return 1;
        }

        public Context(String className, PBDeserializer config, DeserializeBeanInfo beanInfo, int initVariantIndex) {
            this.className = className;
            this.clazz = beanInfo.getClazz();
            this.variantIndex = initVariantIndex;
            this.beanInfo = beanInfo;
            fieldInfoList = new ArrayList<FieldInfo>(beanInfo.getFieldList());
        }

        public String getClassName() {
            return className;
        }

        public List<FieldInfo> getFieldInfoList() {
            return fieldInfoList;
        }

        public DeserializeBeanInfo getBeanInfo() {
            return beanInfo;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public int getVariantCount() {
            return variantIndex;
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

        public int var(String name) {
            Integer i = variants.get(name);
            if (i == null) {
                variants.put(name, variantIndex++);
            }
            i = variants.get(name);
            return i.intValue();
        }
    }

    public FieldDeserializer createFieldDeserializer(PBDeserializer mapping, Class<?> clazz, FieldInfo fieldInfo)
            throws Exception {
        Class<?> fieldClass = fieldInfo.getFieldClass();

//        if (fieldClass == int.class || fieldClass == long.class || fieldClass == String.class) {
//            return createStringFieldDeserializer(mapping, clazz, fieldInfo);
//        }

        FieldDeserializer fieldDeserializer = mapping.createFieldDeserializerWithoutASM(mapping, clazz, fieldInfo);
        return fieldDeserializer;
    }

}
