# AnnotationTools

`AnnotationTools` 是核心中针对 `AnnotationTool` 所提供的默认实现， 如果你没有自行实现 `AnnotationTool` 的需求，
或者仅仅只是希望能自定义一下缓存用的Map和类型转化，那么你就可以考虑使用 `AnnotationTools` 来获取注解工具。

## SimpleAnnotationTool

`AnnotationTools` 中提供的绝大多数实现，目前是由 `SimpleAnnotationTool` 提供。 对于`SimpleAnnotationTool`的特性有如下内容：

#### 内置缓存由Map实现。

它的内置缓存由Map实现，并存在两个缓存Map：一个是值缓存，一个是空缓存。当使用 `clearCache` 的时候，会同时清理二者。

#### 不是线程安全的。

`SimpleAnnotationTool` 的实现不是线程安全的，因此需要注意异步调用问题。

#### 支持可重复注解。

对于一个**可重复**注解，首先假设可重复注解分为**容器** 和 **元素**。

在 `SimpleAnnotationTool` 中，使用 `getAnnotation` 获取一个**容器**注解的时候，会将目标中所有的 **元素** 注解获取（包括其他映射才能得到的注解）并注入到 `value` 属性中。

使用 `getAnnotations` 获取一个**元素**注解的时候，会获取所有的此类型与相关映射转化而来的注解。
使用 `getAnnotations` 时，获取目标不一定必须是**可重复**（标注了 `@Repeatable` ）的，但是假如是可重复的，那么会优先检查容器注解的缓存。
但是需要注意，`getAnnotations` 本身不会**直接**记录缓存。因此，如果你要获取的是一个可重复注解，优先建议通过 `getAnnotation` 获取容器类型注解。

#### 代理
`SimpleAnnotationTool` 的基本实现原理是基于JDK的动态代理，因此通过 `SimpleAnnotationTool` 而得到的 **所有** 注解就是由其进行代理包装的。
不过由于存在缓存，因此不会频繁的进行代理构建。（`createAnnotationInstance` 除外，此方法每次都会获取新的代理对象）。

#### 可见性
虽然我直接告诉了你，工具内默认的实现类是 `SimpleAnnotationTool` ，但是实际上此类并不对外开放，因此在未来有可能会存在变化。
所以你不需要太过于关心这个类，按照接口描述使用即可。

## 使用

### 默认实现

```java
AnnotationTool tool=AnnotationTools.getAnnotationTool();
```

在默认实现中, 内置的两个缓存为 `LinkedHashMap`，转化器为 `NonConverters`.

对于**NonConverters**, 它实际上并不存在"类型转化"，他只会直接将相同的类型进行赋值，而假若遇到了不同的类型， 将会抛出 `ConvertionException` 异常。

### 自定义配置

```java
final AnnotationToolConfiguration config=new AnnotationToolConfiguration();
// 可以实现Converters，来自定义转化器
config.setConverters(Converters.nonConverters());

// 设置一个缓存器，需要是可变Map
config.setCacheMap(new HashMap<>());

// 设置一个空值缓存器，需要是可变Map
config.setNullCacheMap(new LinkedHashMap<>());

// 根据配置得到实例。
final AnnotationTool tool=AnnotationTools.getAnnotationTool(config);

```
