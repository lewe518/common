package com.lewely.common.objects;


import org.junit.Assert;
import org.junit.Test;

public class ObjectNullableValidatorTest {

    @Test
    public void validate() {
        ObjectNullableValidator validator = new ObjectNullableValidator();
        validator.setBasicValidator(String.class, s -> s != null && !"".equals(s));

        Assert.assertFalse(validator.validate(null));
        Assert.assertFalse(validator.validate(new User()));
        Assert.assertFalse(validator.validate(new User(1, "")));
        Assert.assertTrue(validator.validate(new User(1, "tom")));
    }

    public static class User {
        private int age;
        private String name;

        public User() {
        }

        public User(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}