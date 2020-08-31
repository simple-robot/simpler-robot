/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     MessageMsgGets.kt
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

package love.forte.simbot.common.api.message

import love.forte.simbot.common.api.message.containers.GroupContainer

/*
 *
 * 此处定义与 接收消息 相关的接口
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/1
 * @since
 **/


/**
 * 私聊消息
 */
public interface PrivateMsg: MessageEventGet {
    /**
     * 获取私聊消息类型
     */
    val type: Type

    /**
     * flag标识，一般可用于撤回之类的。默认为ID的值。
     */
    @JvmDefault
    override val flag: String get() = id

    /**
     * 私聊消息类型
     */
    enum class Type {
        /** 好友消息 */
        FRIEND,
        /** 来自群的临时会话 */
        GROUP_TEMP,
        /** 其他 */
        OTHER

    }


}


/**
 * 群消息
 */
public interface GroupMsg: MessageEventGet, GroupContainer {
    /**
     * 获取消息类型
     */
    val type: Type

    /**
     * flag标识，一般可用于撤回之类的。默认为ID的值。
     */
    @JvmDefault
    override val flag: String get() = id


    /** 群消息类型 */
    enum class Type {
        /** 普通消息 */
        NORMAL,
        /** 匿名消息 */
        ANON,
        /** 系统消息 */
        SYS
    }
}

