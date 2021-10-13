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

package love.forte.simbot.kaiheila.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import love.forte.simbot.component.kaiheila.SerializerModuleRegistrar


/**
 *
 * [附加的多媒体数据Attachments](https://developer.kaiheila.cn/doc/objects#%E9%99%84%E5%8A%A0%E7%9A%84%E5%A4%9A%E5%AA%92%E4%BD%93%E6%95%B0%E6%8D%AEAttachments)
 *
 * @author ForteScarlet
 */
public interface Attachments {
    /**
     * Type 多媒体类型
     */
    val type: String

    /**
     * Url 多媒体地址
     */
    val url: String

    /**
     * Name 多媒体名
     */
    val name: String

    /**
     * Size 大小 单位（B）
     * 假如无法获取，得到-1.
     */
    val size: Long

    companion object : SerializerModuleRegistrar {
        override fun SerializersModuleBuilder.serializerModule() {
            polymorphic(Attachments::class) {
                val implSer = SimpleAttachments.serializer()
                subclass(SimpleAttachments::class, implSer)
                default { implSer }
            }
        }
    }
}

@Serializable
@SerialName(SimpleAttachments.SERIAL_NAME)
public data class SimpleAttachments(
    override val type: String,
    override val url: String,
    override val name: String,
    override val size: Long = -1
) : Attachments {
    internal companion object {
        const val SERIAL_NAME = "ATTACHMENTS_I"
    }
}