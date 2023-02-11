/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simbot.definition

import love.forte.simbot.ID
import love.forte.simbot.literal


/**
 * 一个 _类别_ 。
 * 常表现为"类别"、"分类"、"分组"等含义，默认应用于 [UserInfo] 和 [OrganizationInfo] 中。
 *
 * [Category] 是一个不够稳定的存在：simbot无法保证何时何地何种组件中会存在“分组”，也无法保证组件中能够支持获取组件。
 * 因此 [Category] 的定义中仅提供两个最基础且存在可能性最高的两个属性：[id] 和 [name]。
 *
 * 对于 [id] 和 [name] 来讲，他们两个其中至少也需要存在一个，而当信息无法同时满足二者时，它们会进行互补：如果仅存在 [id] 而不存在 [name],
 * 则 [name] 的值即为 [id] 的字面值；相反，如果组件只能提供 [name] 而无法提供 [id]，则 [id] 等同于将 [name] 作为字面量的 [love.forte.simbot.CharSequenceID]。
 *
 * 因此，[id] 与 [name] 之间是 _互补不为空_ 的。
 *
 * 提供了一个针对定义属性的简单实现类型 [SimpleCategory] 来应对简单场景。
 * 而对于支持更加复杂的 [Category] 的情况，需要由具体组件提供更细致的定制实现。
 *
 * @see SimpleCategory
 *
 * @author ForteScarlet
 */
public interface Category : IDContainer {
    
    /**
     * 此分类的唯一标识。
     *
     * 如果不存在可用标识，则以 [name] 代为补充。
     *
     */
    override val id: ID
    
    /**
     * 此分类的名称。
     *
     * 如果不存在名称，则以 [id] 的[字面量][ID.literal] 代为补充。
     *
     */
    public val name: String
    
    
    public companion object {
        
        /**
         * 根据参数构建一个 [SimpleCategory] 实例。
         *
         * @param id 此分类的唯一标识。
         * @param name 此分类的名称。默认为 [id] 的[字面量][ID.literal]。
         */
        @JvmStatic
        @JvmName("of")
        @JvmOverloads
        public operator fun invoke(id: ID, name: String = id.literal): SimpleCategory {
            return SimpleCategory(id, name)
        }
        
        /**
         * 根据参数构建一个 [SimpleCategory] 实例。
         *
         * @param nameAlsoId 此分类的名称和唯一标识。
         */
        @JvmStatic
        @JvmName("of")
        public operator fun invoke(nameAlsoId: String): SimpleCategory {
            return SimpleCategory(nameAlsoId.ID, nameAlsoId)
        }
        
    }
}

/**
 * 提供 [Category] 的解构扩展。第1个参数，代表 [Category.id]。
 * ```kotlin
 * val (id, name) = category
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun Category.component1(): ID = id

/**
 * 提供 [Category] 的解构扩展。第2个参数，代表 [Category.name]。
 * ```kotlin
 * val (id, name) = category
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun Category.component2(): String = name


/**
 * [Category] 的最低限度基础实现。
 *
 * 可以通过工厂方法 [Category.invoke] 创建。
 *
 */
public data class SimpleCategory internal constructor(override val id: ID, override val name: String) : Category
