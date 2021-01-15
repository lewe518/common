# 使用文档

### ObjectFiller
> 对象填充器，会对class所有属性赋值
```java
ObjectFiller filler = new ObjectFiller();
filler.setObjDefaultValue(DateTime.class, DateTime.now());// 自定义类型默认值
MyClass obj = filler.fill(MyClass.class);
```