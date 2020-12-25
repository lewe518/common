package com.lewely.common;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 反射工具类
 *
 * @author yiliua
 * Create at: 2020/10/28
 */
public class ReflectionUtils {
    /**
     * 获取Class所有的成员变量，包括父类
     *
     * @param clazz clazz
     * @param <T>   T
     * @return List
     */
    public static <T> List<Field> getAllFields(Class<T> clazz) {
        List<Field> fieldList = new ArrayList<>();

        fieldList.addAll(getOwnFields(clazz));
        fieldList.addAll(getSuperFields(clazz));

        return fieldList;
    }

    /**
     * 获取Class所有的非Static、Final成员变量，包括父类
     *
     * @param clazz clazz
     * @param <T>   T
     * @return List
     */
    public static <T> List<Field> getAllFieldsWithoutStaticFinal(Class<T> clazz) {
        List<Field> fieldList = new ArrayList<>();

        fieldList.addAll(getOwnFields(clazz));
        fieldList.addAll(getSuperFields(clazz));

        return fieldList.stream()
                .filter(field -> (field.getModifiers() & ModifierType.STATIC.getValue()) == 0 &&
                        (field.getModifiers() & ModifierType.FINAL.getValue()) == 0)
                .collect(Collectors.toList());
    }

    /**
     * 获取Class自己的成员变量，不含父类
     *
     * @param clazz clazz
     * @param <T>   T
     * @return List
     */
    public static <T> List<Field> getOwnFields(Class<T> clazz) {
        Field[] currentFields = clazz.getDeclaredFields();
        return new ArrayList<>(Arrays.asList(currentFields));
    }

    public static <T> List<Field> getSuperFields(Class<T> clazz) {
        List<Field> superFields = new ArrayList<>();

        Class<? super T> superclass = clazz.getSuperclass();
        if (superclass != null) {
            superFields.addAll(Arrays.asList(superclass.getDeclaredFields()));
        }

        return superFields;
    }

    public static <T> boolean isCollection(Class<T> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (List.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (Set.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }

    public static enum ModifierType {
        PUBLIC(1),
        PRIVATE(2),
        PROTECTED(4),
        STATIC(8),
        FINAL(16),
        SYNCHRONIZED(32),
        VOLATILE(64),
        TRANSIENT(128),
        NATIVE(256),
        INTERFACE(512),
        ABSTRACT(1024),
        STRICT(2048);

        private final int value;

        ModifierType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
