package com.jd.dd.glowworm.util;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeserializeBeanInfo {

    private final Class<?> clazz;
    private final Type type;
    private Constructor<?> defaultConstructor;
    private Constructor<?> creatorConstructor;
    private Method factoryMethod;

    private final List<FieldInfo> fieldList = new ArrayList<FieldInfo>();

    public DeserializeBeanInfo(Class<?> clazz) {
        super();
        this.clazz = clazz;
        this.type = clazz;
    }

    public Constructor<?> getDefaultConstructor() {
        return defaultConstructor;
    }

    public void setDefaultConstructor(Constructor<?> defaultConstructor) {
        this.defaultConstructor = defaultConstructor;
    }

    public Constructor<?> getCreatorConstructor() {
        return creatorConstructor;
    }

    public void setCreatorConstructor(Constructor<?> createConstructor) {
        this.creatorConstructor = createConstructor;
    }

    public Method getFactoryMethod() {
        return factoryMethod;
    }

    public void setFactoryMethod(Method factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Type getType() {
        return type;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public FieldInfo getField(String propertyName) {
        for (FieldInfo item : this.fieldList) {
            if (item.getName().equals(propertyName)) {
                return item;
            }
        }

        return null;
    }

    public boolean add(FieldInfo field) {
        for (FieldInfo item : this.fieldList) {
            if (item.getName().equals(field.getName())) {
                return false;
            }
        }
        fieldList.add(field);

        return true;
    }

    public static DeserializeBeanInfo computeSetters(Class<?> clazz) {
        return computeSetters(clazz, clazz);
    }

    public static DeserializeBeanInfo computeSetters(Class<?> clazz, Type type) {
        DeserializeBeanInfo beanInfo = new DeserializeBeanInfo(clazz);

        Constructor<?> defaultConstructor = getDefaultConstructor(clazz);
        if (defaultConstructor != null) {
            defaultConstructor.setAccessible(true);
            beanInfo.setDefaultConstructor(defaultConstructor);
        }

        for (Method method : clazz.getMethods()) {
            String methodName = method.getName();
            if (methodName.length() < 4) {
                continue;
            }

            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            // support builder set
            if (!(method.getReturnType().equals(Void.TYPE) || method.getReturnType().equals(clazz))) {
                continue;
            }

            if (method.getParameterTypes().length != 1) {
                continue;
            }

            if (methodName.startsWith("set") && Character.isUpperCase(methodName.charAt(3))) {
                String propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                Field field = ASMUtils.getField(clazz, propertyName);

                if (field != null) {
                    //如果有瞬时变量，去除
                    if (field.getAnnotation(Transient.class) != null) {
                        continue;
                    }
                    //如果有瞬时get方法，去除
                    try {
                        Method getMethod = clazz.getDeclaredMethod("g" + methodName.substring(1, methodName.length()));
                        if (getMethod.getAnnotation(Transient.class) != null) {
                            continue;
                        }
                    } catch (NoSuchMethodException e) {
                        //一个set方法没有对应的get方法就算了
                    }
                } else {
                    continue;
                }
                beanInfo.add(new FieldInfo(propertyName, method, null, clazz, type));
                method.setAccessible(true);
            }
        }

        for (Field field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            if (!Modifier.isPublic(field.getModifiers())) {
                continue;
            }

            boolean contains = false;
            for (FieldInfo item : beanInfo.getFieldList()) {
                if (item.getName().equals(field.getName())) {
                    contains = true;
                    continue;
                }
            }

            if (contains) {
                continue;
            }

            //如果有瞬时变量，去除
            if (field.getAnnotation(Transient.class) != null) {
                continue;
            }

            beanInfo.add(new FieldInfo(field.getName(), null, field));
        }

        for (Method method : clazz.getMethods()) {
            String methodName = method.getName();
            if (methodName.length() < 4) {
                continue;
            }

            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            if (methodName.startsWith("get") && Character.isUpperCase(methodName.charAt(3))) {
                if (method.getParameterTypes().length != 0) {
                    continue;
                }

                if (!Collection.class.isAssignableFrom(method.getReturnType())) {
                    continue;
                }

                //判断get方法是否有Transient
                if (method.getAnnotation(Transient.class) != null) {
                    continue;
                }

                String propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);

                FieldInfo fieldInfo = beanInfo.getField(propertyName);
                if (fieldInfo == null) {
                    continue;
                }

                beanInfo.add(new FieldInfo(propertyName, method, null, clazz, type));
                method.setAccessible(true);
            }
        }

        return beanInfo;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (Exception e) {
            return null;
        }
    }

    public static Constructor<?> getDefaultConstructor(Class<?> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return null;
        }

        Constructor<?> defaultConstructor = null;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                defaultConstructor = constructor;
                break;
            }
        }

        if (defaultConstructor == null) {
            if (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {
                for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                    if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0].equals(clazz.getDeclaringClass())) {
                        defaultConstructor = constructor;
                        break;
                    }
                }
            }
        }

        return defaultConstructor;
    }

    public static Constructor<?> getCreatorConstructor(Class<?> clazz) {
        Constructor<?> creatorConstructor = null;
        return creatorConstructor;
    }

    public static Method getFactoryMethod(Class<?> clazz) {
        Method factoryMethod = null;

        for (Method method : clazz.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            if (!clazz.isAssignableFrom(method.getReturnType())) {
                continue;
            }
        }

        return factoryMethod;
    }

}
