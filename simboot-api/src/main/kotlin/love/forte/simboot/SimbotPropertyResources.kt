/*
 * Copyright (c) 2021-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simboot

import love.forte.simboot.utils.WeakVal


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


