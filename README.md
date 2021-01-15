# 使用文档

### ObjectFiller
> 对象填充器，会对class所有属性赋值
```java
ObjectFiller filler = new ObjectFiller();
filler.setObjDefaultValue(DateTime.class, DateTime.now());// 自定义类型默认值
MyClass obj = filler.fill(MyClass.class);
```

### ObjectComparator
> 对象比较
```java
ObjectComparator comparator = new ObjectComparator();
// 自定义类型比较器
comparator.setBasicComparator(DateTime.class, (ComparePredicate<DateTime, DateTime>) Objects::equals);
comparator.compare(instance1, instance2, MyClass.class);
```

### ObjectNullableValidator
> 对象校验器
```java
ObjectNullableValidator validator = new ObjectNullableValidator();
validator.setBasicValidator(String.class, s -> s != null && !"".equals(s));
Assert.assertFalse(validator.validate(new User(1, "")));
```