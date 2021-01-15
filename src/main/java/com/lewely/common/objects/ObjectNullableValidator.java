package com.lewely.common.objects;

import com.lewely.common.function.ValidatePredicate;
import com.lewely.common.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 空对象校验器
 *
 * @author yiliua
 * Create at: 2021/1/15
 */
public class ObjectNullableValidator {
    private Map<Class<?>, ValidatePredicate<?>> basicValidator = new HashMap<>();

    public <T> void setBasicValidator(Class<T> clazz, ValidatePredicate<T> predicate) {
        basicValidator.put(clazz, predicate);
    }

    public <T> boolean validate(T instance) {
        if (instance == null) {
            return false;
        }

        try {
            List<Field> fields = ReflectionUtils.getAllFields(instance.getClass());
            for (Field field : fields) {
                field.setAccessible(true);
                Object val = field.get(instance);
                if (val == null) {
                    return false;
                }

                ValidatePredicate<Object> predicate = (ValidatePredicate<Object>)basicValidator.get(field.getType());
                if (predicate != null && !predicate.test(val)) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
