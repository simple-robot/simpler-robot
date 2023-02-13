---
title: 事件概述
---

事件是simbot的核心之一。

## Event
从simbot3版本开始，事件的根接口不再保留遗留命名为 `MsgGet`，而是直接命名为常见的 `Event`。

事件是在simbot中最基础且最常见的类型之一，它代表了一次事件调度中所承载的事件信息。

一个 `Event` 的基础定义如下（略有简化）：

:::info 注意

当下述描述与实际代码产生出入时，以**实际代码**表现为准，
并及时通过 [**issue**](http://github.com/simple-robot-library/simbot3-website/issues/) 进行反馈。

:::

```kotlin
public interface Event : BotContainer, IDContainer, ComponentContainer {

    /** 事件的唯一标识。 */
    override val id: ID

    /** 与这个事件有关系的 [Bot]. */
    override val bot: Bot

    /**
     * 一个事件所属的组件。
     * 通常与 [bot] 的组件所属一致。
     */
    override val component: Component

    /**
     * 此时间发生的时间戳。
     *
     * 如果相关组件支持，则为对应时间，如果不支持则一般为构建时的瞬时时间戳。
     */
    public val timestamp: Timestamp


    /**
     * 得到当前事件所对应的类型key。
     */
    public val key: Key<out Event>

    /** 所有事件的根类型。 */
    public companion object Root : Key<Event> {
    
        /** Event根节点的唯一ID常量。 */
        public const val ID_VALUE: String = "api.root"

        /** Event根节点的唯一ID。 */
        override val id: CharSequenceID = ID_VALUE.ID

        /** Event是所有事件的根，不可能是其他事件的子项。 */
        override val parents: Set<Key<*>> get() = emptySet()
        
        // ...
    }
    
    // 其他定义, 下文将会提到
    
    public interface Key { /* ... */ }
    
}
```

其中，一个 `Event` 应该有它属于的组件。通过 `bot.component` 来决定一个事件的所属组件。


## Event.Key

对于一个事件和对其事件类型的判断，不再直接使用其类型 ( `[K]Class` ) 进行直接判断，而是使用约定中的 `Event.Key` 作为事件类型的判断依据。

`Event.Key` 是 `Event` 的内部接口，其定义如下：

```kotlin
public interface Event /* : ... */ {
    
    // ...

    public interface Key<E : Event> {

        /**
         * 此事件的ID，需要是唯一的。假若在事件注册时出现了ID相同但不是同一个Key的情况将会导致异常。
         */
        public val id: CharSequenceID

        /**
         * 此事件所继承的所有父事件。
         * 此属性应当是不可变的，不应在运行期内发生变更。
         */
        public val parents: Set<Key<*>>

        /**
         * 将一个提供的类型转化为当前的目标事件。
         * 如果得到null，则说明无法被转化。
         */
        public fun safeCast(value: Any): E?


        public companion object {
            // ... 
        }
    }
    
    // ...

}
```

`Event.Key` 目前被约定为必须**尽可能地**通过伴生对象实现，且其泛型类型标记为当前被监听的事件。
其中，为了达成事件继承关系，提供 `parents` 标记当前事件所继承的父类事件。


## Event的实现

通常情况下，`Event` 类型由组件进行具体的实现。核心库提供了部分常见的 `Event` 类型并作为"标准事件类型"。组件在实现的时候，
应当优先考虑是否满足标准事件类型的定义，如果满足则应优先使用标准事件类型作为实现目标。只有在不满足的情况下组件实现才应考虑提供额外的非标准事件实现。

## 标准事件定义
对于标准事件相关内容，可以参考 [标准事件](standard-event)。







