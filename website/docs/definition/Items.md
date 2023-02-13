---
sidebar_position: 20
title: 元素序列
---

import Label from '@site/src/components/Label'
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';


元素序列 `Items` 是由 simbot 所提供的类序列类型定义。

`Items` 旨在尽可能的消除非阻塞和阻塞的序列API之间的差异，同时简化组件开发的繁杂度。
目前在 simbot 中的绝大多数数据类型为序列类型的相关API（例如：获取XX列表）都使用了 `Items` 作为序列类型。

## 定义
`Items` 的基本定义如下：

> 下述定义内容有所简化。

```kotlin title='love/forte/simbot/utils/item/Items.kt'
public interface Items<out T> {
    
    /**
     * 数据限流。取得的数据条数的最大上限。当 [count] > 0 时有效。
     */
    public fun limit(count: Int): Items<T>
    
    
    /**
     * 数据偏移。从 [offset] 数量之后的数据后开始获取。当 [offset] > 0 时有效。
     */
    public fun offset(count: Int): Items<T>
    
    
    /**
     * 批次大小。如果支持批次获取的话，则每批次获取 [size] 的元素数量。通常 [size] > 0 时有效。
     */
    public fun batch(size: Int): Items<T>
    
    
    /**
     * 收集当前数据序列中的元素. [collectTo] 可能会产生挂起，会直到当前序列中的所有可能产生的元素收集完毕后结束挂起。
     */
    public suspend fun collect(collector: suspend (T) -> Unit)
    
    /**
     * 将当前元素序列转化为 [Flow].
     */
    public fun asFlow(): Flow<T>
    
    /**
     * 将当前元素序列转化为 [Sequence].
     */
    public fun asSequence(): Sequence<T>
    
    /**
     * 将当前元素序列转化为 [Stream].
     */
    public fun asStream(): Stream<out T>
    
    // 省略部分其他兼容性API
}
```

## 预处理函数
在 `Items` 中，存在一些预处理函数。

| 预处理函数         | 描述                                                    |
|---------------|-------------------------------------------------------|
| `limit(Int)`  | 数据限流。取得的数据条数的最大上限。当参数 > 0 时有效。                        |
| `offset(Int)` | 数据偏移。从参数指定的数量之后的数据后开始获取。可以理解为"跳过"指定数量的数据。当参数 > 0 时有效。 |
| `batch(Int)`  | 批次大小。在支持批次的情况下，配置每次实际获取的数据量。通常情况下，参数 > 0 时有效。         |

当你在使用上述的预处理函数的时候，`Items` 通常只是在内部记录对应的数据值，而不会发生任何**真正的变化**。
也同样因此，同一个预处理函数应当尽可能**至多**配置**一次**。

如下示例中：

```kotlin
items.limit(10).limit(20)
```

items内部最终记录的 `limit` 值为 `20`，即后者覆盖前者。

:::info 不太一样

这实际上与常见的序列API（例如 `Sequence` 或者 `Stream` 中类似概念的函数表现不太一样，因此需要多加注意。

:::


## 收集函数
除了上述提到的 [预处理函数](#预处理函数) 以外，在 `Items` 中还存在另外一种函数：**终结函数**。只有当执行了 **终结函数** 的时候，
才会真正的发生数据序列的产生，包括伴随着一起发生的任何其他行为，例如网络请求或挂起。

而 **收集函数** 为终结函数的一种。


:::info 有些类似

对于 **中间函数** 和 **终结函数** 这两个类型来讲，从概念上与普通的序列类型相似。

:::

`Items` 中的收集函数有：

<Tabs groupId="code">
<TabItem value="Kotlin">

| API                                                 | 描述             |
|-----------------------------------------------------|----------------|
| `collect(suspend T -> Unit)` <Label>suspend</Label> | 挂起并收集当前序列中的元素。 |


除了 `collect { ... }`, `Items` 还有一些可供使用的扩展收集函数：

| 扩展API                                                       | 描述                    |
|-------------------------------------------------------------|-----------------------|
| `toCollection(MutableCollection<T>)` <Label>suspend</Label> | 挂起并收集当前序列中的元素到指定的序列中。 |
| `toList()` <Label>suspend</Label>                           | 挂起并收集当前序列中的元素到列表中。    |


</TabItem>
<TabItem value="Java">

| API                        | 描述                           |
|----------------------------|------------------------------|
| `collect(T -> void)`       | 阻塞的收集当前序列中的元素。               |
| `collectTo(Collection<T>)` | 阻塞的收集当前序列中的所有元素到指定的集合容器中。    |
| `collectToList()`          | 阻塞的收集当前序列中的所有元素到一个 `List` 中。 |


</TabItem>
</Tabs>




## 转化函数
与 [收集函数](#收集函数) **类似**，可以将其视为 `Items` 的终结函数，但是又有所不同：因为 **转化函数** 的转化结果为序列相关的类型，
而这些类型尚未执行过终结函数，因此它并非完全的 _终结_，也不会真正的发生数据序列的产生行为。

:::tip 复用?

理论上来讲，`Items` 的转化函数是可以复用的（即调用多次），每次都会得到一个全新的序列实例。
但是并不建议这么做，也没有什么实际意义。而且不保证未来会保持对此行为的宽松约束。

因此，如果没有必要，对于所有转化函数来讲请保证 **至多** 只调用 **一次**。

:::

`Items` 中的转化函数的使用参考：

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
class Bar
class Tar(val foo: Bar)

fun foo(items: Items<Bar>) {
    val tarItems: Items<Tar> = items.transform { Tar(it) }
}
```

:::tip

事实上，转化函数主要用于面向那些对外提供 `Items` 结果的API，而并非使用者。如果作为使用者想要进行类型转化，
你大可以先将其通过 `asSequence` 或 `asFlow` 将其转化为序列类型。

```kotlin
fun foo(items: Items<Bar>) {
    val flow: Flow<Tar> = items.asFlow().map { Tar(it) }
}
```

```kotlin
fun foo(items: Items<Bar>) {
    val sequence: Sequence<Tar> = items.asSequence().map { Tar(it) }
}
```

:::

</TabItem>
<TabItem value="Java">

```java
class Bar{ /* ... */ }
class Tar{
    Tar(Bar bar){ /* ... */ }
}

public void foo(Items<Bar> items) {
    final Items<Tar> tarItems = Items.transform(items, Tar::new);
}
```


:::tip

事实上，转化函数主要用于面向那些对外提供 `Items` 结果的API，而并非使用者。
这一点从上述示例中那种较为繁杂的使用方式也能看出来，这并不是为了使用者准备的api。

如果作为使用者想要进行类型转化，
你大可以先将其通过 `asStream` 将其转化为序列类型。

```java
public void foo(Items<Bar> items) {
    final Stream<Tar> stream = items.asStream().map(Tar::new);
}
```


:::

</TabItem>
</Tabs>









