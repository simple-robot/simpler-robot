/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.spring.configuration.listener

import love.forte.simbot.common.attribute.Attribute
import love.forte.simbot.common.attribute.AttributeMap
import love.forte.simbot.common.attribute.AttributeMapContainer
import love.forte.simbot.common.attribute.attribute
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.quantcat.common.binder.FunctionalBindableEventListener
import love.forte.simbot.quantcat.common.binder.ParameterBinder
import kotlin.reflect.KClass
import kotlin.reflect.KFunction


public abstract class KFunctionEventListener(instance: Any?, caller: KFunction<*>) : FunctionalBindableEventListener(instance, caller) {
    public abstract val attributes: AttributeMap

    @Suppress("MemberVisibilityCanBePrivate")
    public companion object {
        /**
         * [RawFunctionAttribute] 的属性名
         */
        public const val RAW_FUNCTION_ATTRIBUTE_NAME: String = "$\$RAW_FUNCTION$"

        /**
         * 记录 [KFunctionEventListener] 内含原始函数的属性
         */
        @JvmField
        public val RawFunctionAttribute: Attribute<KFunction<*>> = attribute(RAW_FUNCTION_ATTRIBUTE_NAME)

        /**
         * [RawBindersAttribute] 的属性名
         */
        public const val RAW_BINDERS_ATTRIBUTE_NAME: String = "$\$RAW_BINDERS$"

        /**
         * 记录 [KFunctionEventListener] 内含的最终绑定器集的属性。
         */
        @JvmField
        public val RawBindersAttribute: Attribute<Iterable<ParameterBinder>> = attribute(RAW_BINDERS_ATTRIBUTE_NAME)

        /**
         * [RawListenTargetAttribute] 的属性名
         */
        public const val RAW_LISTEN_TARGET_ATTRIBUTE_NAME: String = "$\$RAW_LISTEN_TARGET$"

        /**
         * 记录 [KFunctionEventListener] 内含的最终的监听目标事件的类型的属性。
         */
        @JvmField
        public val RawListenTargetAttribute: Attribute<KClass<out Event>> =
            attribute(RAW_LISTEN_TARGET_ATTRIBUTE_NAME)
    }
}


/**
 *
 * @author ForteScarlet
 */
internal class KFunctionEventListenerImpl(
    instance: Any?,
    caller: KFunction<*>,
    override val binders: Array<ParameterBinder>,
    override val attributes: AttributeMap,
    private val matcher: suspend (EventListenerContext) -> Boolean
) : KFunctionEventListener(instance, caller), AttributeMapContainer {
    override suspend fun match(context: EventListenerContext): Boolean = matcher(context)

    override val attributeMap: AttributeMap
        get() = attributes

    override fun toString(): String =
        "KFunctionEventListener(caller=$caller, attributes=$attributes)"
}
