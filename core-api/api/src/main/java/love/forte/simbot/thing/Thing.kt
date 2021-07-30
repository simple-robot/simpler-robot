/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("Somethings")

package love.forte.simbot.thing


/**
 * 一个 **东西**。
 * @author ForteScarlet
 */
interface Thing<T> {

    /**
     * 被描述的对象。
     */
    val value: T
}


/**
 * 一个有结构的 [东西][Thing]. 除了自己，可能还会有很多.
 *
 * 结构应当是稳固的，因此 [children] 不应是可变动的。
 *
 */
interface StructuralThing<T> : Thing<T> {
    /**
     * 被描述的对象。
     */
    override val value: T

    /**
     * 结构化的东西中，会有很多小东西。
     */
    val children: List<StructuralThing<T>>
}


/**
 * 一个有 [名字][name] 的 [东西][Thing]。
 */
interface NamedThing<T> : Thing<T> {
    /**
     * 这个东西的名字。
     */
    val name: String

    /**
     * 被描述的对象。
     */
    override val value: T
}

/**
 * 有名字的结构化事物。
 */
interface StructuralThingWithName<T> : StructuralThing<T>, NamedThing<T> {
    override val value: T
    override val name: String
    override val children: List<StructuralThingWithName<T>>
}


/**
 * 根据层级name寻找指定结果。
 *
 * [index]代表的是 [valuePath] 开始的索引位。
 */
public fun <T> StructuralThingWithName<T>.resolveValue(valuePath: Array<String>, index: Int = 0): T? {
    if (valuePath.isEmpty()) return null
    if (index > valuePath.lastIndex) return null
    if (name != valuePath[index]) {
        return null
    }
    if (index == valuePath.lastIndex && name == valuePath[index]) {
        return value
    }

    for (child in this.children) {
        val resolved = child.resolveValue(valuePath, index = (index + 1))
        if (resolved != null) {
            return resolved
        }
    }

    return null
}

/**
 * 将 [StructuralThingWithName] 解析为 [Map].
 * 如果 [resolveRoot] 为 false，则不解析当前节点。
 */
public fun <T> StructuralThingWithName<T>.resolveToMap(
    delimiter: String,
    resolveRoot: Boolean = true,
    filter: (T) -> Boolean = { it != null },
): Map<String, T> {
    val map = mutableMapOf<String, T>()

    fun deep(parent: String? = null, t: StructuralThingWithName<T>) {
        val name = parent?.let { p -> p + delimiter + t.name } ?: t.name
        if (filter(t.value)) {
            map[name] = t.value
        }
        for (child in t.children) {
            deep(name, child)
        }

    }

    if (resolveRoot) {
        if (filter(value)) {
            map[name] = value
            return map
        }
        for (child in children) {
            deep(name, child)
        }

    } else {
        if (children.isNotEmpty()) {
            for (child in children) {
                deep(null, child)
            }
        }
    }


    return map
}


/**
 * 根据name过滤条件查找一个值。
 */
public fun <T> StructuralThingWithName<T>.findValue(filter: (name: String) -> Boolean): T? {
    if (filter(name)) {
        return value
    }
    if (children.isEmpty()) {
        return null
    }
    children.forEach { c ->
        val found = c.findValue(filter)
        if (found != null) {
            return found
        }
    }
    return null
}


// Thing 基础实现


/**
 * 对 [Thing] 的基础实现。
 */
public fun <T> thing(value: T): Thing<T> = SimpleThing(value)
public data class SimpleThing<T>(override val value: T) : Thing<T>

/**
 * 得到一个没有内容的 [Thing].
 */
public fun nothing(): Thing<Nothing?> = EmptyThing

private object EmptyThing : Thing<Nothing?> {
    override val value: Nothing? get() = null
    override fun toString(): String = "EmptyThing(value=null)"
}

/**
 * 对 [有名字的东西][NamedThing] 的基础实现。
 */
public fun <T> thing(name: String, value: T): NamedThing<T> = SimpleNamedThing(name, value)
public data class SimpleNamedThing<T>(override val name: String, override val value: T) : NamedThing<T>

/**
 * 对 [结构化事物][StructuralThing] 的基础实现。
 */
public fun <T> thing(value: T, children: List<StructuralThing<T>>): StructuralThing<T> =
    SimpleStructuralThing(value, children)

public data class SimpleStructuralThing<T>(
    override val value: T,
    override val children: List<StructuralThing<T>>,
) : StructuralThing<T>

/**
 * 对 [有名字的结构化事物][StructuralThingWithName] 的基础实现。
 */
public fun <T> thing(name: String, value: T, children: List<StructuralThingWithName<T>>): StructuralThingWithName<T> =
    SimpleStructuralThingWithName(name, value, children)

public data class SimpleStructuralThingWithName<T>(
    override val name: String, override val value: T,
    override val children: List<StructuralThingWithName<T>>,
) : StructuralThingWithName<T>


public fun <T> StructuralThing<T>.forEach(block: StructuralThingForEach<T>) {
    block(null, this)
    this.children.forEach {
        it.forEach(this, block)
    }
}

private fun <T> StructuralThing<T>.forEach(
    parentThing: Thing<T>,
    block: StructuralThingForEach<T>,
) {
    block(parentThing, this)
    this.children.forEach {
        it.forEach(this, block)
    }
}

@JvmName("forEachNamed")
public fun <T> StructuralThingWithName<T>.forEach(block: StructuralThingWithNameForEach<T>) {
    block(null, this)
    this.children.forEach {
        it.forEach(this, block)
    }
}

@JvmName("forEachNamed")
private fun <T> StructuralThingWithName<T>.forEach(
    parentThing: NamedThing<T>,
    block: StructuralThingWithNameForEach<T>,
) {
    block(parentThing, this)
    this.children.forEach {
        it.forEach(this, block)
    }
}


public fun interface StructuralThingForEach<T> {
    operator fun invoke(parentThing: Thing<T>?, thing: Thing<T>)
}

public fun interface StructuralThingWithNameForEach<T> {
    operator fun invoke(parentThing: NamedThing<T>?, thing: NamedThing<T>)
}
