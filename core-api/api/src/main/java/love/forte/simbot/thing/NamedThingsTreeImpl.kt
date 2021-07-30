/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     NamedThingsTreeBuilder.kt
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

package love.forte.simbot.thing

private typealias DSL = NamedThingsTreeBuilderDSL
private typealias NodeDSL = NamedThingsTreeBuilderNodeDSL


@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
public annotation class NamedThingsTreeBuilderDSL

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
public annotation class NamedThingsTreeBuilderNodeDSL

/**
 * [NamedThingsTree]的构建器。
 */
@DSL
public class NamedThingsTreeBuilder<T, NT>(
    @DSL
    var name: String? = null,
    private var valueGetter: (() -> T)? = null,
) {
    constructor(name: String?, value: T) : this(name, { value })

    @DSL
    fun value(valueGetter: () -> T) {
        this.valueGetter = valueGetter
    }

    private val nodes = mutableListOf<NamedThingsTree.Node<NT>>()

    @DSL
    fun node(node: NamedThingsTree.Node<NT>) {
        nodes.add(node)
    }

    @DSL
    fun node(block: NamedThingsTreeNodeBuilder<NT>.() -> Unit) {
        NamedThingsTreeNodeBuilder<NT>().apply(block).build().let(nodes::add)
    }


    fun build(): NamedThingsTree<T, NT> = SimpleNamedThingsTree(
        requireNotNull(name) { "Required 'name' was null." },
        requireNotNull(valueGetter) { "Required 'value' was not init." }(),
        nodes
    )
}

@DSL
public fun <T, NT> buildNamedThingsTree(
    name: String? = null,
    valueGetter: (() -> T)? = null,
    block: NamedThingsTreeBuilder<T, NT>.() -> Unit,
): NamedThingsTree<T, NT> {
    return NamedThingsTreeBuilder<T, NT>(name, valueGetter).apply(block).build()
}

@NodeDSL
public fun <T> buildNamedThingsTreeNode(
    name: String? = null,
    valueGetter: (() -> T)? = null,
    block: NamedThingsTreeNodeBuilder<T>.() -> Unit,
): NamedThingsTree.Node<T> {
    return NamedThingsTreeNodeBuilder<T>(name, valueGetter).apply(block).build()
}


@NodeDSL
public class NamedThingsTreeNodeBuilder<T>
@JvmOverloads constructor(
    @NodeDSL
    var name: String? = null,
    private var valueGetter: (() -> T)? = null,
) {
    constructor(name: String?, value: T) : this(name, { value })

    @NodeDSL
    fun value(valueGetter: () -> T) {
        this.valueGetter = valueGetter
    }

    private val nodes = mutableListOf<NamedThingsTree.Node<T>>()

    @NodeDSL
    operator fun plus(node: NamedThingsTree.Node<T>) = node(node)

    @NodeDSL
    fun node(node: NamedThingsTree.Node<T>) {
        nodes.add(node)
    }

    @NodeDSL
    fun node(block: NamedThingsTreeNodeBuilder<T>.() -> Unit) {
        NamedThingsTreeNodeBuilder<T>().apply(block).build().let(nodes::add)
    }


    fun build(): NamedThingsTree.Node<T> = SimpleNamedThingsTreeNode(
        requireNotNull(name) { "Required 'name' was null." },
        requireNotNull(valueGetter) { "Required 'value' was not init." }(),
        nodes
    )
}


/**
 * 对 [NamedThingsTree] 的基础实现.
 */
private class SimpleNamedThingsTree<T, NT>(
    override val name: String,
    override val value: T,
    override val nodes: List<NamedThingsTree.Node<NT>>,
) : NamedThingsTree<T, NT> {
    override fun toString(): String {
        return "Tree(name=$name, value=$value, nodes=$nodes)"
    }
}


/**
 * 对 [NamedThingsTree.Node] 的基础实现.
 */
private class SimpleNamedThingsTreeNode<T>(
    override val name: String,
    override val value: T,
    override val nodes: List<NamedThingsTree.Node<T>>,
) : NamedThingsTree.Node<T> {
    override fun toString(): String {
        return "Node(name=$name, value=$value, nodes=$nodes)"
    }
}



