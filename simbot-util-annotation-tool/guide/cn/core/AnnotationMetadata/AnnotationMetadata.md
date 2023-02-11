# AnnotationMetadata

顾名思义，此接口定义了一个注解类型的一些基础属性。

## 概述
#### 实例缓存
`AnnotationMetadata` 通过其静态方法 `resolve` 进行获取。
目前的默认状态下，`AnnotationMetadata` 内置针对于注解类型的Class的**弱缓存**，且线程安全，因此相同的 Class 得到的metadata理论上应当是相同的。

由于是弱缓存，因此假若对应的Class被卸载，但是你却依然保留着 `metadata` 的实例，
那么当你调用需要获取Class的方法的时候 （例如`getAnnotationType`）便会抛出`IllegalStateException(ClassNotFoundException)`异常。

#### 反射异常
由于对注解的各种操作很有可能涉及到反射，因此此接口中大部分方法都需要捕获 `ReflectiveOperationException` 异常。


## 使用
### 获取
```java
AnnotationMetadata metadata = AnnotationMetada.resolve(XxxAnnotation.class);
```


## 方法描述
_具体的方法描述可以查看方法上我写的工地英语。_

#### Class\<A> getAnnotationType
得到这个注解的类型. 

#### RetentionPolicy getRetention
得到这个注解上标注的 @Retention 中的元素（或者默认的RetentionPolicy.CLASS)

#### Set\<ElementType> getTargets
得到这个注解上标注的 @Target 中的元素

#### boolean containsTarget
判断是否存在某个对应的 target.

#### boolean isDocumented
判断是否标注了 `@Documented`

#### boolean isInherited
判断是否标注了 `@Inherited`

#### boolean isDeprecated
判断是否标注了 `@Depercated`

#### boolean isRepeatable
判断是否标注了 `@Repeatable`, 
或者存在一个 `value` 属性， 这个属性的类型是一个注解数组，
并且这个注解的类型中存在 `@Repeatable`, 
同时这个 `@Repeatable` 的值等于当前注解类型。

#### boolean isRepeatableContainer
判断是否是一个可重复注解中的容器注解。也就是上述 `isRepeatable` 判断条件中的第二个成立条件的情况。

#### Class\<? extends Annotation> getRepeatableAnnotationType
如果这是一个可重复注解：
    如果是容器注解，得到可重复的元素类型。
    如果是可重复元素注解，得到容器类型。
否则得到null。

#### Set\<String> getPropertyNames
得到这个注解的所有属性(参数)的名称。

#### boolean containsProperty
判断是否存在对应的属性名。

#### Map\<String, Class\<?>> getPropertyTypes
得到这个注解的所有属性名与属性类型的键值对儿。

#### Class\<?> getPropertyType
得到对应属性的类型。如果没有此属性，得到null。

#### Map\<String, Object> getPropertyDefaultValues
得到所有有默认值的属性，以及他们的默认值。

#### Object getPropertyDefaultValue
得到对应属性的默认值。如果没有则返回null。

#### Map<String, Object> getProperties(A)
提供一个对应类型的注解实例，得到这个注解的所有属性和对应的值。

#### Map<String, String> getPropertyNamingMaps
提供一个其他的注解类型, 得到这个注解类型与当前注解类型中的属性的映射关系。
key是其他的注解类型的属性，value是当前注解的属性。
如果无法确定映射则为null。

有关映射的来源，如果当前注解配置了对应的 `AnnotationMapper.Property`, 则使用此对应的映射关系，
否则如果名称相同，也会视为映射。

#### String getPropertyNamingMap
得到一个指定注解类型的某个属性，对当前注解的映射属性名。可能为null。

#### Object getAnnotationValue
提供一个属性名和对应的注解实例，得到这个注解的属性值。获取不到则会得到null。

#### static resolve
根据类型得到一个 `AnnotationMetadata` 实例。