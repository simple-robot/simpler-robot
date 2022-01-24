/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

@file:JvmName("ComponentAttributeUtil")
package love.forte.simbot

import kotlinx.serialization.Serializable
import org.jetbrains.annotations.Range
import java.util.*

public object ComponentAttributes {

    /**
     * 此组件的开发者信息.
     */
    @JvmField
    public val authors: Attribute<Authors> = attribute("component.authors")

    /**
     * 此组件的展示性名称。
     */
    @JvmField
    public val display: Attribute<Display> = attribute("component.display")
}


//region Authors
/**
 * 作者列表。
 */
@Serializable
public class Authors internal constructor(private val delegate: List<Author>) : List<Author> by delegate {
    internal constructor(author: Author): this(listOf(author))
    public companion object {

        @Api4J
        @JvmStatic
        public fun fromCollections(coll: Collection<Author>): Authors {
            return Authors(Collections.unmodifiableList(coll.toList()))
        }

    }
}
@JvmSynthetic
public fun Collection<Author>.toAuthors(): Authors = Authors(Collections.unmodifiableList(toList()))

/**
 * 一位作者信息。
 */
@Serializable
public data class Author(
    /**
     * 作者在这其中的唯一标识，将会仅仅依靠[id]作为重复判断。
     */
    val id: ID,
    /** 这位开发者的名称 */
    val name: String,
    /** 这位作者的联系邮箱 */
    val email: String? = null,
    /** 这位作者的个人主页 */
    val url: String? = null,
    /** 这位作者所扮演的角色列表 */
    val roles: List<String> = emptyList(),
    /** 这位作者所在时区 */
    val timezone: @Range(from = -11L, to = 12L) Int
)

/**
 * 尝试获取组件中的作者信息。
 */
public val Component.authors: Authors? get() = get(ComponentAttributes.authors)

//endregion


//region Display
/**
 * （针对于[Component]）的展示用名称。与 [Component.id] 不同，[Display] 用作友好型的信息展示。
 */
public interface Display {

    /**
     * 根据地区信息得到当前组件的本地化展示信息
     */
    public fun getValue(locale: Locale = Locale.getDefault()): String

}

/**
 * 尝试获取组件中的展示信息。
 */
public fun Component.getDisplay(locale: Locale = Locale.getDefault()): String? = get(ComponentAttributes.display)?.getValue(locale)

/**
 * 尝试获取组件中的展示信息。
 */
public val Component.display: String? get() = getDisplay()
//endregion



