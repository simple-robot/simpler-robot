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
import kotlinx.serialization.modules.subclass
import love.forte.simbot.kaiheila.SerializerModuleRegistrar


/**
 *
 * [引用消息Quote](https://developer.kaiheila.cn/doc/objects#%E5%BC%95%E7%94%A8%E6%B6%88%E6%81%AFQuote)
 *
 * @author ForteScarlet
 */
public interface Quote {

    /**
     * Id 引用消息id
     */
    val id: String

    /**
     * Type 引用消息类型
     */
    val type: Int

    /**
     * Content 	引用消息内容
     */
    val content: String

    /**
     * Create at 引用消息创建时间（毫秒）
     */
    val createAt: Long

    /**
     * Author 作者的用户信息
     */
    val author: User

    companion object : SerializerModuleRegistrar {
        override fun SerializersModuleBuilder.serializerModule() {
            polymorphic(Quote::class) {
                subclass(QuoteImpl::class)
                default { QuoteImpl.serializer() }
            }
        }
    }
}


@Serializable
@SerialName(QuoteImpl.SERIAL_NAME)
public data class QuoteImpl(
    override val id: String,
    override val type: Int,
    override val content: String,
    @SerialName("create_at")
    override val createAt: Long,
    override val author: User
) : Quote {
    internal companion object {
        const val SERIAL_NAME = "QUOTE_I"
    }
}