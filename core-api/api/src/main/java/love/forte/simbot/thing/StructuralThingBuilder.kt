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

package love.forte.simbot.thing

private typealias DSL = StructuralThingBuilderDSL
private typealias DSL_WN = StructuralThingWithNameBuilderDSL

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
public annotation class StructuralThingBuilderDSL

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
public annotation class StructuralThingWithNameBuilderDSL

//// structural thing
@DSL
public fun <T> buildStructuralThing(
    valueSupplier: (() -> T)? = null,
    block: StructuralThingBuilder<T>.() -> Unit,
): StructuralThing<T> {
    return StructuralThingBuilder(valueSupplier).apply(block).build()
}

@DSL
public fun <T> buildStructuralThing(
    value: T,
    block: StructuralThingBuilder<T>.() -> Unit,
): StructuralThing<T> {
    return StructuralThingBuilder { value }.apply(block).build()
}

/**
 * [StructuralThing]'s builder.
 *
 */
@DSL
public class StructuralThingBuilder<T>(
    private var valueSupplier: (() -> T)? = null,
) {

    private val children = mutableListOf<StructuralThing<T>>()

    val value: T get() = valueSupplier?.invoke() ?: throw IllegalStateException("Value was not init yet.")

    @DSL
    fun value(v: T) {
        valueSupplier = { v }
    }

    @DSL
    fun value(block: () -> T) {
        valueSupplier = block
    }

    @DSL
    fun child(child: StructuralThing<T>) {
        children.add(child)
    }

    @DSL
    fun child(
        valueSupplier: (() -> T)? = null,
        block: StructuralThingBuilder<T>.() -> Unit,
    ) {
        children.add(buildStructuralThing(valueSupplier, block))
    }

    @DSL
    fun child(
        value: T,
        block: (StructuralThingBuilder<T>.() -> Unit)? = null,
    ) {
        block?.let { b -> children.add(buildStructuralThing(value, b)) } ?: children.add(thing(value, emptyList()))

    }


    fun build(): StructuralThing<T> = thing(
        value = requireNotNull(valueSupplier) { "Required 'value' was not init." }(),
        children = children.ifEmpty { emptyList() }
    )
}

//// structural thing with name
@DSL_WN
public fun <T> buildStructuralThing(
    name: String? = null,
    valueSupplier: (() -> T)? = null,
    block: StructuralThingWithNameBuilder<T>.() -> Unit,
): StructuralThingWithName<T> {
    return StructuralThingWithNameBuilder(name, valueSupplier).apply(block).build()
}

@DSL_WN
public fun <T> buildStructuralThing(
    name: String? = null,
    value: T,
    block: StructuralThingWithNameBuilder<T>.() -> Unit,
): StructuralThingWithName<T> {
    return StructuralThingWithNameBuilder(name = name, valueSupplier = { value }).apply(block).build()
}

/**
 * [StructuralThing]'s builder.
 *
 */
@DSL_WN
public class StructuralThingWithNameBuilder<T>(
    @DSL_WN
    var name: String? = null,
    private var valueSupplier: (() -> T)? = null,
) {

    constructor(name: String?, value: T) : this(name, { value })
    constructor(value: T) : this(valueSupplier = { value })

    private val children = mutableListOf<StructuralThingWithName<T>>()

    val value: T get() = valueSupplier?.invoke() ?: throw IllegalStateException("Value was not init yet.")

    @DSL_WN
    fun value(v: T) {
        valueSupplier = { v }
    }

    @DSL_WN
    fun value(block: () -> T) {
        valueSupplier = block
    }

    @DSL_WN
    fun child(child: StructuralThingWithName<T>) {
        children.add(child)
    }

    @DSL_WN
    fun child(
        name: String? = null,
        valueSupplier: (() -> T)? = null,
        block: StructuralThingWithNameBuilder<T>.() -> Unit,
    ) {
        children.add(buildStructuralThing(name, valueSupplier, block))
    }

    @DSL_WN
    fun child(
        name: String? = null,
        value: T,
        block: (StructuralThingWithNameBuilder<T>.() -> Unit)? = null,
    ) {
        block?.let { b -> children.add(buildStructuralThing(name, value, b)) } ?: children.add(thing(
            name = requireNotNull(name) { "Required 'name' was null." },
            value = value,
            emptyList()))

    }


    fun build(): StructuralThingWithName<T> = thing(
        name = requireNotNull(name) { "Required 'name' was null." },
        value = requireNotNull(valueSupplier) { "Required 'value' was not init." }(),
        children = children.ifEmpty { emptyList() }
    )


}
