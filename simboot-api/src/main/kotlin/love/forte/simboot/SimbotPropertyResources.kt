/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
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


