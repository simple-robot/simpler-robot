---
sidebar_position: 10
title: 唯一标识 ID
---

import Tabs from '@theme/Tabs'; import TabItem from '@theme/TabItem';


ID，用于代表一个目标的唯一标识。ID一个大家都熟悉的内容，它常常表现为一串数字，或者一串字符。

为了统一在各个环境下可能存在的ID类型差异，在 `simbot3` 中提供了一套针对ID的类型。

## ID

ID 是一个通用的唯一标识定义，其定义如下：

```kotlin
sealed class ID : Comparable<ID> {
  // 得到字符串类型字面值
	fun toString(): String
  
  // 比较
  fun compareTo(other ID): Int
}
```

一个 `ID` 有两层含义：这个唯一标识的类型，以及它具体的值。
不同类型的 `ID` 的值类型可能不同，但是你都可以直接通过 `toString` 来得到它们的字符串字面值。

## 类型
`ID` 总共有3个大类型，其中 `NumericalID` (尤其 `IntID` 和 `LongID` ) 和 `CharSequenceID` 理论上可以满足绝大多数需求。

### NumericalID
此类型代表一个 **数字形式** 的ID。其定义如下：

```kotlin
sealed class NumericalID<N : Number> : ID() {
  // 得到字面值
  fun getValue(): N
  
  
  // 一些类似于 Number中的方法
  
  fun toByte(): Byte
  fun toShort(): Short
  fun toInt(): Int
  fun toLong(): Long
  fun toDouble(): Double
  fun toFloat(): Float
  fun toChar(): Char
}
```

`NumericalID`提供了6个基本的类型实现：
- `IntID`
- `LongID`
- `DoubleID`
- `FloatID`
- `BigDecimalID`
- `BigIntegerID`

简单的使用示例：



<!-- 展示类型 -->
<Tabs groupId="code">
<TabItem value="Kotlin" default>

<!-- id类型 -->
<Tabs groupId="id-type">

<TabItem value="Int">

```kotlin
val intId = 123.ID
```

</TabItem>
<TabItem value="Long">

```kotlin
val longId = 123456L.ID
```

</TabItem>
<TabItem value="Double">

```kotlin
val doubleId = 123.456.ID
```

</TabItem>
<TabItem value="Float">

```kotlin
val floatId = 123.456F.ID
```

</TabItem>
<TabItem value="BigDecimal">

```kotlin
val bdId = BigDecimal("123,456").ID
```

</TabItem>
<TabItem value="BigInteger">

```kotlin
val biId = BigInteger.valueOf(123456).ID
```

</TabItem>

</Tabs>

</TabItem>
<TabItem value="Java">

<!-- id类型 -->
<Tabs groupId="id-type">

<TabItem value="Int">

```java
final IntID intId = Identifies.ID(123);
```

</TabItem>
<TabItem value="Long">

```java
final LongID longId = Identifies.ID(123456L);
```

</TabItem>
<TabItem value="Double">

```java
final DoubleID doubleId = Identifies.ID(123.456);
```

</TabItem>
<TabItem value="Float">

```java
final FloatID floatId = Identifies.ID(123.456F);
```

</TabItem>
<TabItem value="BigDecimal">

```java
final BigDecimalID bdId = Identifies.ID(new BigDecimal("123.456").setScale(1, RoundingMode.HALF_UP));
```

</TabItem>
<TabItem value="BigInteger">

```java
final BigIntegerID biId = Identifies.ID(BigInteger.valueOf(500L));
```

</TabItem>

</Tabs>

</TabItem>
</Tabs>



### CharSequenceID 
除了数字ID，最常见的就是字符序列ID了，比如一串UUID。`CharSequenceID` 是一个独立实现，表示一个最基础的字符串ID。

<Tabs groupId="code">
<TabItem value="Kotlin" label="Kotlin" default>

```kotlin
val id: CharSequenceID = "HelloWorld".ID

val anyId: CharSequenceID = 123.ID.toCharSequenceID() // 所有ID理论上都可以作为字符序列ID
```

</TabItem>
<TabItem value="Java" label="Java">

```java
final CharSequenceID helloId = Identifies.ID("HelloWorld");

final CharSequenceID strId = Identifies.toCharSequenceID(Identifies.ID(123));
```

</TabItem>
</Tabs>
