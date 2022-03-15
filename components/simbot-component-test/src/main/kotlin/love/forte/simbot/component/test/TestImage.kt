/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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
 */

package love.forte.simbot.component.test

import kotlinx.serialization.Serializable
import love.forte.simbot.*
import love.forte.simbot.message.*
import love.forte.simbot.resources.*
import java.io.*


/**
 * test组件中使用的图片对象。
 *
 * @author ForteScarlet
 */
@Serializable
public class TestImage @JvmOverloads constructor(
    @Serializable(ID.AsCharSequenceIDSerializer::class) override val id: ID,
    public val resource: Resource? = null
) :
    Image<TestImage> {

    /**
     * 得到这个图片的数据资源。
     */
    override suspend fun resource(): Resource = resource ?: EMPTY_RESOURCE

    override fun toString(): String = "TestImage(id=$id, resource=$resource)"

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is TestImage) return false
        return id == other.id && resource == other.resource
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (resource?.hashCode() ?: 0)
        return result
    }


    override val key: Message.Key<TestImage>
        get() = Key

    public companion object Key : Message.Key<TestImage> {
        private val EMPTY_RESOURCE = ByteArrayInputStream(byteArrayOf()).toResource("emptyResource")
        override fun safeCast(value: Any): TestImage? = doSafeCast(value)

    }
}