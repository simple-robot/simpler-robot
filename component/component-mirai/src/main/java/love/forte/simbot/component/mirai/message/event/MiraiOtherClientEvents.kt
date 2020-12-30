/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.component.mirai.message.event

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.Container
import love.forte.simbot.component.mirai.message.MiraiBotAccountInfo
import net.mamoe.mirai.contact.ClientKind
import net.mamoe.mirai.contact.OtherClient
import net.mamoe.mirai.event.events.OtherClientOfflineEvent
import net.mamoe.mirai.event.events.OtherClientOnlineEvent


/**
 * mirai设备信息容器。
 */
public interface MiraiOtherClientContainer : Container {
    /**
     * 设备信息。
     * @see OtherClient
     */
    val client: OtherClient
}



/**
 *
 * mirai的其他客户端上线事件。
 *
 * @author ForteScarlet
 */
public interface MiraiOtherClientOnline : MiraiOtherClientContainer {

    /**
     * 上线的设备。
     * @see OtherClient
     */
    override val client: OtherClient

    /**
     * 设备类型。
     * @see ClientKind
     */
    val kind: ClientKind?
}

/**
 * mirai的其他客户端下线事件。
 * @author ForteScarlet
 */
public interface MiraiOtherClientOffline : MiraiOtherClientContainer {
    /**
     * 下线的设备。
     * @see OtherClient
     */
    override val client: OtherClient
}




public class MiraiOtherClientOnlineImpl(event: OtherClientOnlineEvent) :
    AbstractMiraiMsgGet<OtherClientOnlineEvent>(event), MiraiOtherClientOnline {

    /**
     * 那个上线的设备的信息。类型是mirai提供的 [OtherClient]
     */
    override val client: OtherClient
        get() = event.client

    /**
     * 设备类型。
     * @see ClientKind
     */
    override val kind: ClientKind?
        get() = event.kind


    /** 当前监听事件消息的ID。 */
    override val id: String
        get() = "MOCO-${event.hashCode()}"

    /**
     * 账号的信息。即当前bot信息。
     */
    override val accountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)

    /**
     * 文本为null，即无法进行文本过滤。
     * @see isEmptyMsg
     *
     */
    override val text: String?
        get() = null

}



public class MiraiOtherClientOfflineImpl(event: OtherClientOfflineEvent) :
    AbstractMiraiMsgGet<OtherClientOfflineEvent>(event), MiraiOtherClientOffline {

    /**
     * 那个下线的设备的信息。类型是mirai提供的 [OtherClient]
     */
    override val client: OtherClient
        get() = event.client


    /** 当前监听事件消息的ID。 */
    override val id: String
        get() = "MOCO-${event.hashCode()}"

    /**
     * 账号的信息。即当前bot信息。
     */
    override val accountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)

    /**
     * 文本为null，即无法进行文本过滤。
     * @see isEmptyMsg
     *
     */
    override val text: String?
        get() = null

}














