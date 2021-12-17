/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty


/**
 * 通过指定的Key寻找所有的 `META-INF/simbot.properties` 文件，并读取他们的值。
 *
 * `simbot.factories` 是properties格式的，值可能是多个的，他们会通过 `,` 进行分割。
 *
 * properties的 key/value 由 `=` 分割，允许存在多个重复key，所有重复的key最终都会被置于 [SimbotPropertyResources.values] 中。
 *
 */
public interface SimbotPropertyResources {
    public val key: String
    public val values: List<String>

    @Suppress("MemberVisibilityCanBePrivate")
    public companion object {
        public const val FACTORY_RESOURCE_NAME: String = "simbot.factories"
        public const val VALUES_SPLIT: String = ","
        public const val KEY_VALUE_SPLIT: String = "="


        // 读取所有的 `simbot.factories` 文件并作为Properties记录。
        private val allFactories: List<Pair<String, String>> by WeakVal(false) {
            val loader = SimbotPropertyResources::class.java.classLoader
            val resources = loader.getResources("META-INF/$FACTORY_RESOURCE_NAME") // META-INF/simbot.properties
            resources.asSequence().flatMap { resource ->
                resource.readText().lines().flatMap { line ->
                    val splits = line.split(KEY_VALUE_SPLIT, limit = 2)
                    if (splits.size == 2) {
                        splits[1].splitToSequence(VALUES_SPLIT)
                            .map { sv -> splits[0] to sv }
                    } else emptySequence()
                }
            }.toList()
        }

        @JvmStatic
        public fun findKey(key: String): SimbotPropertyResources {
            val list: List<String> = allFactories.asSequence().filter { (k, _) -> k == key }
                .map { (_, v) -> v }.toList()

            return SimbotPropertyResourcesImpl(key, list)
        }

    }
}


private data class SimbotPropertyResourcesImpl(override val key: String, override val values: List<String>) :
    SimbotPropertyResources


private class WeakVal<T>(init: Boolean, private val getFunc: () -> T) {

    private var weak = WeakReference<T>(if (init) getFunc() else null)

    public operator fun getValue(instance: Any, property: KProperty<*>): T {
        return weak.get() ?: synchronized(this) {
            weak.get() ?: getFunc().also { weak = WeakReference(it) }
        }
    }


}