package com.lewely.common;


import com.lewely.common.exception.ObjectFillException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * 对象填充器
 * <p>
 * Demo:
 * ObjectFiller objectFiller = new ObjectFiller();
 * objectFiller.setObjDefaultValue(org.joda.time.DateTime.class, DateTime.now());
 * ProductGroupContext obj = objectFiller.fill(ProductGroupContext.class);
 *
 * @author yiliua
 * Create at: 2020/10/26
 */
@SuppressWarnings("all")
public class ObjectFiller {

    /**
     * 是否填充父类中的字段
     */
    private boolean fillSuperClass = true;
    /**
     * 基础类型的默认值
     */
    private Map<Class<?>, Object> basicObjValue;

    public ObjectFiller() {
        basicObjValue = getBasicObjValue();
    }

    public ObjectFiller(Map<Class<?>, Object> basicObjValue) {
        this.basicObjValue = basicObjValue;
    }

    public ObjectFiller(boolean fillSuperClass, Map<Class<?>, Object> basicObjValue) {
        this.fillSuperClass = fillSuperClass;
        this.basicObjValue = basicObjValue;
    }

    public <T> T fill(Class<T> clazz) {
        try {
            return getDefaultValue(clazz, null);
        } catch (Exception e) {
            throw new ObjectFillException(e);
        }
    }

    public <T> void setObjDefaultValue(Class<T> clazz, Object val) {
        basicObjValue.put(clazz, val);
    }

    private <T> T getDefaultValue(Class<T> clazz, Type genericType) throws Exception {
        if (ReflectionUtils.isCollection(clazz)) {
            return getDefaultCollections(clazz, genericType);
        }

        if (clazz.equals(Optional.class)) {
            return (T) getDefaultOptional(clazz, genericType);
        }

        if (clazz.isInterface()) {
            return null;
        }

        if (clazz.isEnum()) {
            return (T) Enum.valueOf((Class) clazz, clazz.getDeclaredFields()[0].getName());
        }

        if (basicObjValue.containsKey(clazz)) {
            return (T) basicObjValue.get(clazz);
        }

        T instance = clazz.newInstance();
        List<Field> fields = getAllField(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> subClazz = field.getType();
            Object subValue = getDefaultValue(subClazz, field.getGenericType());
            field.set(instance, subValue);
        }

        return instance;
    }

    private <T> List<Field> getAllField(Class<T> clazz) {
        if (fillSuperClass) {
            return ReflectionUtils.getAllFieldsWithoutStaticFinal(clazz);
        }
        return ReflectionUtils.getOwnFields(clazz);
    }

    private <T> Optional<T> getDefaultOptional(Class<T> clazz, Type genericType) throws Exception {
        List<Object> genericValues = getGenericValues(genericType);
        if (genericValues.size() == 1) {
            return Optional.of((T) genericValues.get(0));
        }
        return Optional.empty();
    }

    private <T> T getDefaultCollections(Class<T> clazz, Type genericType) throws Exception {
        List<Object> genericValues = getGenericValues(genericType);

        if (genericValues.size() == 2) {
            if (Map.class.isAssignableFrom(clazz)) {
                HashMap hashMap = new HashMap();
                hashMap.put(genericValues.get(0), genericValues.get(1));
                return (T) hashMap;
            }
        }

        if (genericValues.size() == 1) {
            if (List.class.isAssignableFrom(clazz)) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(genericValues.get(0));
                return (T) arrayList;
            }
            if (Set.class.isAssignableFrom(clazz)) {
                HashSet hashSet = new HashSet();
                hashSet.add(genericValues.get(0));
                return (T) hashSet;
            }
        }
        return (T) null;
    }

    private List<Object> getGenericValues(Type genericType) throws Exception {
        List<Object> genericValues = new ArrayList<>();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                Class<?> genericClass;
                if (typeArgument instanceof ParameterizedType) {
                    genericClass = Class.forName(((ParameterizedType) typeArgument).getRawType().getTypeName());
                } else {
                    genericClass = Class.forName(typeArgument.getTypeName());
                }
                Object val = getDefaultValue(genericClass, typeArgument);
                genericValues.add(val);
            }
        }
        return genericValues;
    }

    public void setFillSuperClass(boolean fillSuperClass) {
        this.fillSuperClass = fillSuperClass;
    }

    public Map<Class<?>, Object> getBasicObjValue() {
        Map<Class<?>, Object> objValues = new HashMap<>();
        objValues.put(Integer.class, 1);
        objValues.put(int.class, 1);
        objValues.put(byte.class, (byte) 1);
        objValues.put(Byte.class, (byte) 1);
        objValues.put(long.class, 1L);
        objValues.put(Long.class, 1L);
        objValues.put(String.class, "default");
        objValues.put(Boolean.class, true);
        objValues.put(boolean.class, true);
        objValues.put(short.class, (short) 1);
        objValues.put(Short.class, (short) 1);
        objValues.put(Double.class, 1D);
        objValues.put(double.class, 1D);
        objValues.put(Float.class, 1F);
        objValues.put(float.class, 1F);
        objValues.put(char.class, 'c');
        objValues.put(Character.class, 'c');
        objValues.put(Calendar.class, Calendar.getInstance());
        objValues.put(Date.class, Calendar.getInstance().getTime());
        objValues.put(BigDecimal.class, BigDecimal.TEN);
        return objValues;
    }
}
