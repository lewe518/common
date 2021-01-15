package com.lewely.common.objects;

import com.lewely.common.util.ReflectionUtils;
import com.lewely.common.exception.ObjectCompareException;
import com.lewely.common.function.ComparePredicate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * 对象比较器
 *
 * @author yiliua
 * Create at: 2020/10/27
 */
public class ObjectComparator {
    Map<Class<?>, ComparePredicate<?, ?>> basicComparator;

    public ObjectComparator() {
        basicComparator = getBasicComparator();
    }

    public void setBasicComparator(Class<?> clazz, ComparePredicate<?, ?> predicate) {
        basicComparator.put(clazz, predicate);
    }

    public <T> boolean compare(T t1, T t2, Class<T> clazz) {
        return compareInternal(t1, t2, clazz, "");
    }

    private <T> boolean compareInternal(Object t1, Object t2, Class<T> clazz, String fieldName) {
        if (t1 == null && t2 == null) {
            return true;
        }
        if (t1 == null || t2 == null) {
            throw getCompareException(t1, t2, fieldName);
        }

        if (basicComparator.containsKey(clazz)) {
            ComparePredicate<Object, Object> predicate = (ComparePredicate<Object, Object>) basicComparator.get(clazz);
            if (!predicate.test(t1, t2)) {
                throw getCompareException(t1, t2, "");
            }
            return true;
        }

        if (clazz.isInterface()) {
            return true;
        }
        if (clazz.isEnum()) {
            return Objects.equals(t1, t2);
        }

        try {
            List<Field> allFields = ReflectionUtils.getAllFieldsWithoutStaticFinal(clazz);
            if (allFields.size() > 0) {
                for (Field field : allFields) {
                    field.setAccessible(true);

                    Class<?> subClazz = field.getType();

                    Object obj1 = field.get(t1);
                    Object obj2 = field.get(t2);
                    if (!compareInternal(obj1, obj2, subClazz, fieldName + "." + field.getName())) {
                        throw getCompareException(obj1, obj2, field.getName());
                    }
                }
                return true;
            }
        } catch (Exception e) {
            throw new ObjectCompareException(e.getMessage());
        }

        return t1.equals(t2);
    }

    private Map<Class<?>, ComparePredicate<?, ?>> getBasicComparator() {
        Map<Class<?>, ComparePredicate<?, ?>> objValues = new HashMap<>();
        objValues.put(Integer.class, (ComparePredicate<Integer, Integer>) Objects::equals);
        objValues.put(int.class, (ComparePredicate<Integer, Integer>) Objects::equals);
        objValues.put(byte.class, (ComparePredicate<Byte, Byte>) Objects::equals);
        objValues.put(Byte.class, (ComparePredicate<Byte, Byte>) Objects::equals);
        objValues.put(long.class, (ComparePredicate<Long, Long>) Objects::equals);
        objValues.put(Long.class, (ComparePredicate<Long, Long>) Objects::equals);
        objValues.put(String.class, (ComparePredicate<String, String>) Objects::equals);
        objValues.put(Boolean.class, (ComparePredicate<Boolean, Boolean>) Objects::equals);
        objValues.put(boolean.class, (ComparePredicate<Boolean, Boolean>) Objects::equals);
        objValues.put(short.class, (ComparePredicate<Short, Short>) Objects::equals);
        objValues.put(Short.class, (ComparePredicate<Short, Short>) Objects::equals);
        objValues.put(Double.class, (ComparePredicate<Double, Double>) Objects::equals);
        objValues.put(double.class, (ComparePredicate<Double, Double>) Objects::equals);
        objValues.put(Float.class, (ComparePredicate<Float, Float>) Objects::equals);
        objValues.put(float.class, (ComparePredicate<Float, Float>) Objects::equals);
        objValues.put(char.class, (ComparePredicate<Character, Character>) Objects::equals);
        objValues.put(Character.class, (ComparePredicate<Character, Character>) Objects::equals);
        objValues.put(Calendar.class, (ComparePredicate<Calendar, Calendar>) Objects::equals);
        objValues.put(Date.class, (ComparePredicate<Date, Date>) Objects::equals);
        objValues.put(BigDecimal.class, (ComparePredicate<BigDecimal, BigDecimal>) (o1, o2) -> o1.compareTo(o2) == 0);
        objValues.put(List.class, this::compareList);
        objValues.put(ArrayList.class, this::compareList);
        objValues.put(Map.class, this::compareMap);
        objValues.put(HashMap.class, this::compareMap);
        objValues.put(Optional.class, this::compareOptional);
        return objValues;
    }

    private boolean compareOptional(Object obj1, Object obj2) {
        Optional opt1 = (Optional) obj1;
        Optional opt2 = (Optional) obj2;
        Object v1 = opt1.get();
        Object v2 = opt2.get();
        compareInternal(v1, v2, v1.getClass(), "");
        return true;
    }

    private boolean compareMap(Object obj1, Object obj2) {
        Map map1 = (Map) obj1;
        Map map2 = (Map) obj2;
        if (map1.size() != map2.size()) {
            return false;
        }

        for (Object key : map1.keySet()) {
            compareInternal(map1.get(key), map2.get(key), map1.get(key).getClass(), "");
        }
        return true;
    }

    private boolean compareList(Object obj1, Object obj2) {
        List list1 = (List) obj1;
        List list2 = (List) obj2;
        if (list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            compareInternal(list1.get(i), list2.get(i), list1.get(i).getClass(), "");
        }
        return true;
    }

    private <T> ObjectCompareException getCompareException(Object t1, Object t2, String fieldName) {
        return new ObjectCompareException(fieldName + ": " + t1 + ";" + t2);
    }
}
