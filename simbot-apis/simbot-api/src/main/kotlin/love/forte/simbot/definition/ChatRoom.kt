/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (即 simple robot的v3版本，因此亦可称为 simple-robot v3 、simbot v3 等) 的一部分。
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

package love.forte.simbot.definition

import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.action.SendSupport
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.emptyItems


/**
 * 一个 **聊天室**。 聊天室是 [组织][Organization] 的子类型，代表其是一个存在多人且允许相互交流“发送消息”的组织。
 *
 * 聊天室实现 [SendSupport] ，允许发送消息。
 *
 * @author ForteScarlet
 */
public interface ChatRoom : Organization, SendSupport {
    
    /**
     * 聊天室子集。
     *
     * 默认为空，通常情况下能够作为聊天室的组织不存在子集。
     */
    override val children: Items<Organization> get() = emptyItems()
    
    
    /**
     * 根据ID寻找当前聊天室下匹配的子聊天室。
     *
     * 默认得到null，通常情况下能够作为聊天室的组织不存在子集。
     */
    @JvmSynthetic
    override suspend fun child(id: ID): Organization? = null
    
    
    
    /**
     * 根据ID寻找当前聊天室下匹配的子聊天室。
     *
     * 默认得到null，通常情况下能够作为聊天室的组织不存在子集。
     */
    @Api4J
    override fun getChild(id: ID): Organization? = null
    
}