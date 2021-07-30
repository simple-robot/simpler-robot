/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     Thing.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

@file:JvmName("Somethings")

package love.forte.simbot.thing


/**
 * 一个 **东西**。
 * @author ForteScarlet
 */
interface Thing<out T> {

    /**
     * 被描述的对象。
     */
    val value: T
}

/**
 * 一个有 [名字][name] 的 [东西][Thing]。
 */
interface NamedThing<out T> : Thing<T> {
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
 * 一颗树。这棵树同样是一个 [东西][Thing],
 * 同时他包含很多小树，也就是树的 [节点][ThingsTree.Node]。
 *
 * 树的[类型][T]不一定要与节点的[类型][NT]一致，但是所有节点的类型都应一致。
 */
interface ThingsTree<out T, out NT> : Thing<T> {
    /**
     * 这棵树也可能存在一个描述对象.
     */
    override val value: T


    /**
     * 这棵树的所有子节点。当然，它可能是空的。
     */
    val nodes: List<Node<NT>>


    /**
     * 这棵树的节点，节点也可以是一棵 [树][ThingsTree].
     * 节点下，所有枝丫的类型都应一致。
     */
    interface Node<out T> : ThingsTree<T, T>
}


/**
 * 一棵有名字的树。这棵树是一个 [有名字的东西][NamedThing],
 * 同时他包含很多小树，也就是树的 [节点][NamedThingsTree.Node]。
 *
 * 树的[类型][T]不一定要与节点的[类型][NT]一致，但是所有节点的类型都应一致。
 */
interface NamedThingsTree<out T, out NT> : NamedThing<T> {

    /**
     * 这棵树的名称。
     */
    override val name: String

    /**
     * 这棵树也可能存在一个描述对象.
     */
    override val value: T

    /**
     * 这棵树的所有子节点。当然，它可能是空的。
     */
    val nodes: List<Node<NT>>


    /**
     * 这棵树的节点，节点也可以是一棵 [树][NamedThingsTree].
     * 节点下，所有枝丫的类型都应一致。
     */
    interface Node<out T> : NamedThingsTree<T, T>
}


public fun <T> NamedThingsTree<T, T>.asNode(): NamedThingsTree.Node<T> =
    if (this is NamedThingsTree.Node<T>) this else NamedThingsTreeNodeDelegate(this)

private class NamedThingsTreeNodeDelegate<T>(private val delegate: NamedThingsTree<T, T>) : NamedThingsTree.Node<T> {
    override val name: String
        get() = delegate.name
    override val value: T
        get() = delegate.value
    override val nodes: List<NamedThingsTree.Node<T>>
        get() = delegate.nodes
}


/**
 * 根据名称路径尝试寻找这棵树下对应名称的内容。
 * 从当前tree节点开始查询。由于`0`索引是从当前tree节点开始的，因此需要保证当前tree节点类型与子节点类型一致。
 */
public tailrec fun <NT> NamedThingsTree<NT, NT>.resolveValue(
    namePath: Array<String>,
    startIndex: Int = 0,
): NT? {
    if (namePath.isEmpty()) {
        return null
    }
    if (startIndex > namePath.lastIndex) {
        return null
    }

    if (namePath[startIndex] == this.name) {
        return this.value
    }

    return resolveValue(namePath, startIndex + 1)
}


/**
 * 根据名称路径尝试寻找这棵树下对应名称的内容。
 * 从子节点开始查询，当前节点不计入内。
 */
public fun <T, NT> NamedThingsTree<T, NT>.resolveSubValue(
    startIndex: Int = 0,
    namePath: Array<String>,
): NT? {
    if (namePath.isEmpty()) {
        return null
    }
    if (startIndex > namePath.lastIndex) {
        return null
    }

    for (node in this.nodes) {
        val resolve = node.resolveValue(namePath, startIndex)
        if (resolve != null) return resolve
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
}

/**
 * 对 [有名字的东西][NamedThing] 的基础实现。
 */
public fun <T> thing(name: String, value: T): NamedThing<T> = SimpleNamedThing(name, value)
public data class SimpleNamedThing<T>(override val name: String, override val value: T) : NamedThing<T>


public fun <T> NamedThingsTree<T, T>.forEach(block: (node: NamedThing<T>) -> Unit) {
    block(this)
    this.nodes.forEach {
        it.forEach(block)
    }
}

public fun <T> NamedThing<T>?.toGeneralString(): String =
    if (this == null) "null" else "NamedThing(name=$name, value=$value)"

public fun <T> Thing<T>?.toGeneralString(): String = if (this == null) "null" else "Thing(value=$value)"