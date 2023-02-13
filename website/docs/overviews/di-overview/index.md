---
title: 依赖注入
draft: true
---

simbot3 依旧保留的依赖注入的功能，但是相对于 simbot2 而言**有所简化**。
在 simbot3 中，依赖注入所使用的支持库为 [forte-DI](https://github.com/forte-projects/forte-DI)。

`forte-DI` 保留了 `simbot2` 时期的 `@Beans` 等注解的使用习惯，但不同的是其主要以 [JSR 330](https://www.jcp.org/en/jsr/detail?id=330) 
标准为主（使用 `@Inject`、`@Named` 为主），且不再提供 **配置文件注入** 功能。

:::note

simbot3中，只有 [boot模块](../module-overview/boot) 使用了依赖注入功能，基础的 [核心模块](../module-overview/core) 并无依赖注入能力。

:::



import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';



<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin title="直接注入"
@Beans class Foo

// auto-inject foo
@Beans class Bar(val foo: Foo)
```

```kotlin title="工厂注入"
@BeansFactory
class FooConfig {
    @Beans
    fun foo() = Foo()

}

@Beans class Bar(val foo: Foo)
```

</TabItem>
<TabItem value="Java" label="Java">

```java title="直接注入"
@Beans class Foo {}

@Beans class Bar {
    private final Foo foo;
    
    public Bar(Foo foo) {
        this.foo = foo;
    }   
}
```
```java title="工厂注入"
@BeansFactory
public class FooFactory {
    @Beans
    public Foo foo() {
    	return new Foo();
    }
}

// Foo.java
public class Foo{}


// Bar.java
@Beans 
public class Bar {
    private final Foo foo;
    
    @Depend
    private Foo foo2;
    
    public Bar(Foo foo) {
        this.foo = foo;
    }   
}
```

</TabItem>
</Tabs>


