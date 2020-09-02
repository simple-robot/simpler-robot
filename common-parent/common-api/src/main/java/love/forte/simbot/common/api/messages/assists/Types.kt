/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Types.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package love.forte.simbot.common.api.messages.assists

/*
 *
 * 此处定义一些非嵌套在接口中的类型. 所谓类型，一般都是指枚举
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/1
 * @since
 */

/**
 * 定义一个成员的**权限**。一般指群员在群里的权限。
 *
 * @property level 权限等级。等级越大，则代表其权限等级越高。
 */
public enum class Permissions(public val level: Int) {
    /**
     * 群主
     */
    OWNER(0),

    /**
     * 管理员
     */
    ADMINISTRATOR(-10),

    /**
     * 普通成员
     */
    MEMBER(-20);


    /** 判断是否为[拥有者][OWNER] */
    public fun isOwner() = this == OWNER

    /** 判断是否为 [管理员][ADMINISTRATOR] */
    public fun isAdmin() = this == ADMINISTRATOR

    /** 判断是否为 [拥有者][OWNER]或者 [管理员][ADMINISTRATOR] */
    public fun isOwnerOrAdmin() = isOwner() || isAdmin()

    /** 判断是否为普通群员 */
    public fun isMember() = this == MEMBER
}



/**
 * 行动动机类, 一般不同的消息事件中可能会有其各自独特的 **类型** 枚举,
 * 例如
 * [群消息类型][love.forte.simbot.common.api.messages.events.GroupMsg.Type] ,
 * [增加类型][love.forte.simbot.common.api.messages.events.IncreaseEventGet.Type]
 * 等。
 *
 * 这些类型中，有一部分便可以表达为一个 [行动动机][ActionMotivations],
 * 简单来说就是可以将他们归类为 [主动][PROACTIVE] 或 [被动][PASSIVE]
 *
 *
 */
public enum class ActionMotivations {
    /** 主动的 */
    PROACTIVE,
    /** 被动的 */
    PASSIVE
}


