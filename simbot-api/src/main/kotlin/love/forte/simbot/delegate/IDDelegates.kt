/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.delegate

import love.forte.simbot.*
import love.forte.simbot.utils.randomIdStr
import kotlin.random.Random
import kotlin.reflect.KProperty


/**
 * 将一个 [Long] 委托为 [LongID].
 *
 * e.g.
 * ```kotlin
 * val id by longID { 123L }
 * ```
 *
 * ## 作用域与应用场景
 *
 * [LongIDDelegate.getValue] 委托主要应用于对外的公开属性场景，
 * 通过委托来减少某个对象在初始化时产生过多的简单包装器。
 *
 * 你需要避免一些错误的做法：
 *
 * 1. 避免在一个类的私有作用域使用此委托，例如使用一个私有属性作为委托目标如下：
 *
 * ```kotlin
 * // Bad
 * class Foo {
 *    // 私有属性没有代理的必要
 *    private val id by longID { 123L }
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * // Good
 * class Foo {
 *     private val idValue = 123L
 *     // 委托对外公开的属性而非内部应用的属性
 *     private val id by longID { idValue }
 * }
 * ```
 *
 * 2. 避免在局部作用域使用此委托，例如一个方法中如下：
 *
 * ```kotlin
 * // Bad
 * fun foo() {
 *    val id by longID { 123L }
 *
 *    // 下面会产生3个 ID 对象
 *    useID(now)
 *    useID(now)
 *    useID(now)
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * fun foo() {
 *    // 直接使用 ID 本身的API即可。
 *    val id = 123L.ID
 *    useID(id)
 *    useID(id)
 *    useID(id)
 * }
 * ```
 *
 * @since 3.1.0
 *
 * @see ID
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun LongIDDelegate.getValue(o: Any?, property: KProperty<*>?): LongID = value.ID

/**
 * 用于在 [LongIDDelegate.getValue] 中作为委托接收器，通过 [longID] 构造。
 *
 * @since 3.1.0
 *
 * @see LongIDDelegate.getValue
 */
@JvmInline
public value class LongIDDelegate @PublishedApi internal constructor(@PublishedApi internal val value: Long)

/**
 * 构造一个 [LongIDDelegate] 对象，用于进行 [LongID] 的属性委托。
 *
 * @since 3.1.0
 *
 * @see LongIDDelegate.getValue
 */
public inline fun longID(block: () -> Long): LongIDDelegate = LongIDDelegate(block())

/**
 * 将一个 [Int] 委托为 [IntID].
 *
 * e.g.
 * ```kotlin
 * val id by intID { 123 }
 * ```
 *
 * ## 作用域与应用场景
 *
 * [IntIDDelegate.getValue] 委托主要应用于对外的公开属性场景，
 * 通过委托来减少某个对象在初始化时产生过多的简单包装器。
 *
 * 你需要避免一些错误的做法：
 *
 * 1. 避免在一个类的私有作用域使用此委托，例如使用一个私有属性作为委托目标如下：
 *
 * ```kotlin
 * // Bad
 * class Foo {
 *    // 私有属性没有代理的必要
 *    private val id by intID { 123 }
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * // Good
 * class Foo {
 *     private val idValue = 123
 *     // 委托对外公开的属性而非内部应用的属性
 *     private val id by intID { idValue }
 * }
 * ```
 *
 * 2. 避免在局部作用域使用此委托，例如一个方法中如下：
 *
 * ```kotlin
 * // Bad
 * fun foo() {
 *    val id by intID { 123 }
 *
 *    // 下面会产生3个 ID 对象
 *    useID(now)
 *    useID(now)
 *    useID(now)
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * fun foo() {
 *    // 直接使用 ID 本身的API即可。
 *    val id = 123.ID
 *    useID(id)
 *    useID(id)
 *    useID(id)
 * }
 * ```
 *
 * @since 3.1.0
 *
 * @see ID
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun IntIDDelegate.getValue(o: Any?, property: KProperty<*>?): IntID = value.ID


/**
 * 用于在 [IntIDDelegate.getValue] 中作为委托接收器，通过 [intID] 构造。
 *
 * @since 3.1.0
 *
 * @see IntIDDelegate.getValue
 */
@JvmInline
public value class IntIDDelegate @PublishedApi internal constructor(@PublishedApi internal val value: Int)

/**
 * 构造一个 [IntIDDelegate] 对象，用于进行 [IntID] 的属性委托。
 *
 * @since 3.1.0
 *
 * @see IntIDDelegate.getValue
 */
public inline fun intID(block: () -> Int): IntIDDelegate = IntIDDelegate(block())

/**
 * 将一个 [UInt] 委托为 [UIntID].
 *
 * e.g.
 * ```kotlin
 * val id by uIntID { 123u }
 * ```
 * ## 作用域与应用场景
 *
 * [UIntIDDelegate.getValue] 委托主要应用于对外的公开属性场景，
 * 通过委托来减少某个对象在初始化时产生过多的简单包装器。
 *
 * 你需要避免一些错误的做法：
 *
 * 1. 避免在一个类的私有作用域使用此委托，例如使用一个私有属性作为委托目标如下：
 *
 * ```kotlin
 * // Bad
 * class Foo {
 *    // 私有属性没有代理的必要
 *    private val id by uIntID { 123u }
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * // Good
 * class Foo {
 *     private val idValue = 123u
 *     // 委托对外公开的属性而非内部应用的属性
 *     private val id by uIntID { idValue }
 * }
 * ```
 *
 * 2. 避免在局部作用域使用此委托，例如一个方法中如下：
 *
 * ```kotlin
 * // Bad
 * fun foo() {
 *    val id by uIntID { 123u }
 *
 *    // 下面会产生3个 ID 对象
 *    useID(now)
 *    useID(now)
 *    useID(now)
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * fun foo() {
 *    // 直接使用 ID 本身的API即可。
 *    val id = 123u.ID
 *    useID(id)
 *    useID(id)
 *    useID(id)
 * }
 * ```
 *
 * @since 3.1.0
 *
 * @see ID
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun UIntIDDelegate.getValue(o: Any?, property: KProperty<*>?): UIntID = value.ID


/**
 * 用于在 [UIntIDDelegate.getValue] 中作为委托接收器，通过 [uIntID] 构造。
 *
 * @since 3.1.0
 *
 * @see UIntIDDelegate.getValue
 */
@JvmInline
public value class UIntIDDelegate @PublishedApi internal constructor(@PublishedApi internal val value: UInt)

/**
 * 构造一个 [UIntIDDelegate] 对象，用于进行 [UIntID] 的属性委托。
 *
 * @since 3.1.0
 *
 * @see UIntIDDelegate.getValue
 */
public inline fun uIntID(block: () -> UInt): UIntIDDelegate = UIntIDDelegate(block())

/**
 * 将一个 [ULong] 委托为 [ULongID].
 *
 * e.g.
 * ```kotlin
 * val id by uLongID { 123u }
 * ```
 *
 * ## 作用域与应用场景
 *
 * [ULongIDDelegate.getValue] 委托主要应用于对外的公开属性场景，
 * 通过委托来减少某个对象在初始化时产生过多的简单包装器。
 *
 * 你需要避免一些错误的做法：
 *
 * 1. 避免在一个类的私有作用域使用此委托，例如使用一个私有属性作为委托目标如下：
 *
 * ```kotlin
 * // Bad
 * class Foo {
 *    // 私有属性没有代理的必要
 *    private val id by uLongID { 123u }
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * // Good
 * class Foo {
 *     private val idValue: ULong = 123u
 *     // 委托对外公开的属性而非内部应用的属性
 *     private val id by uLongID { idValue }
 * }
 * ```
 *
 * 2. 避免在局部作用域使用此委托，例如一个方法中如下：
 *
 * ```kotlin
 * // Bad
 * fun foo() {
 *    val id by uLongID { 123u }
 *
 *    // 下面会产生3个 ID 对象
 *    useID(now)
 *    useID(now)
 *    useID(now)
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * fun foo() {
 *    // 直接使用 ID 本身的API即可。
 *    val idValue: ULong = 123u
 *    val id = idValue.ID
 *    useID(id)
 *    useID(id)
 *    useID(id)
 * }
 * ```
 *
 * @since 3.1.0
 *
 * @see ID
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun ULongIDDelegate.getValue(o: Any?, property: KProperty<*>?): ULongID = value.ID

/**
 * 用于在 [ULongIDDelegate.getValue] 中作为委托接收器，通过 [uLongID] 构造。
 *
 * @since 3.1.0
 *
 * @see ULongIDDelegate.getValue
 */
@JvmInline
public value class ULongIDDelegate @PublishedApi internal constructor(@PublishedApi internal val value: ULong)

/**
 * 构造一个 [LongIDDelegate] 对象，用于进行 [LongID] 的属性委托。
 *
 * @since 3.1.0
 *
 * @see LongIDDelegate.getValue
 */
public inline fun uLongID(block: () -> ULong): ULongIDDelegate = ULongIDDelegate(block())

/**
 * 将一个 [CharSequence] (通常为 [String]) 委托为 [CharSequenceID].
 *
 * e.g.
 * ```kotlin
 * val id by stringID { "123" }
 * ```
 * 
 * 特别的，你可以在 [stringID] 或 [charSequenceID] 的作用域中使用一些快捷能力，
 * 例如通过 [random][CharSequenceIDDelegate.random] 快速表示委托为一个随机字符串。
 *
 * ```kotlin
 * val id by stringID { random }
 * ```
 *
 * ## 作用域与应用场景
 *
 * [CharSequenceIDDelegate.getValue] 委托主要应用于对外的公开属性场景，
 * 通过委托来减少某个对象在初始化时产生过多的简单包装器。
 *
 * 你需要避免一些错误的做法：
 *
 * 1. 避免在一个类的私有作用域使用此委托，例如使用一个私有属性作为委托目标如下：
 *
 * ```kotlin
 * // Bad
 * class Foo {
 *    // 私有属性没有代理的必要
 *    private val id by stringID { "123" }
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * // Good
 * class Foo {
 *     private val idValue = "123"
 *     // 委托对外公开的属性而非内部应用的属性
 *     private val id by stringID { idValue }
 * }
 * ```
 *
 * 2. 避免在局部作用域使用此委托，例如一个方法中如下：
 *
 * ```kotlin
 * // Bad
 * fun foo() {
 *    val id by stringID { "123" }
 *
 *    // 下面会产生3个 ID 对象
 *    useID(now)
 *    useID(now)
 *    useID(now)
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * fun foo() {
 *    // 直接使用 ID 本身的API即可。
 *    val id = "123".ID
 *    useID(id)
 *    useID(id)
 *    useID(id)
 * }
 * ```
 *
 * @since 3.1.0
 *
 * @see ID
 * @see stringID
 * @see charSequenceID
 * 
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun CharSequenceIDDelegate.getValue(o: Any?, property: KProperty<*>?): CharSequenceID = value.ID

/**
 * 用于在 [CharSequenceIDDelegate.getValue] 中作为委托接收器，通过 [stringID] 构造。
 *
 * @since 3.1.0
 *
 * @see CharSequenceIDDelegate.getValue
 */
@JvmInline
public value class CharSequenceIDDelegate @PublishedApi internal constructor(@PublishedApi internal val value: CharSequence) {
    public companion object {
        /**
         * 在 [stringID] 或 [charSequenceID] 作用域中可以快速使用一个随机ID。
         * ```kotlin
         * val strID by stringID { random }
         * ```
         *
         * @see randomIdStr
         */
        public inline val random: String get() = randomIdStr()

        /**
         * 在 [stringID] 或 [charSequenceID] 作用域中可以快速使用一个随机ID，
         * 并指定一个具体的 [Random] 实例。
         *
         * ```kotlin
         * val strID by stringID { random(Random(123)) }
         * ```
         *
         * @see randomIdStr
         */
        @Suppress("NOTHING_TO_INLINE")
        public inline fun random(r: Random): String = randomIdStr(r)
    }
}

/**
 * 构造一个 [CharSequenceIDDelegate] 对象，用于进行 [CharSequenceID] 的属性委托。
 *
 * @since 3.1.0
 *
 * @see CharSequenceIDDelegate.getValue
 */
public inline fun stringID(block: CharSequenceIDDelegate.Companion.() -> String): CharSequenceIDDelegate =
    CharSequenceIDDelegate(block(CharSequenceIDDelegate))

/**
 * 构造一个 [CharSequenceIDDelegate] 对象，用于进行 [CharSequenceID] 的属性委托。
 *
 * @since 3.1.0
 *
 * @see CharSequenceIDDelegate.getValue
 */
public inline fun charSequenceID(block: CharSequenceIDDelegate.Companion.() -> CharSequence): CharSequenceIDDelegate =
    CharSequenceIDDelegate(block(CharSequenceIDDelegate))
