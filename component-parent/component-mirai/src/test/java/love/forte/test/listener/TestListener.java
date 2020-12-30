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

package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

@Beans
/**
 * @author ForteScarlet
 */
public class TestListener {
    /** 发送一句“我收到了”，并再复读收到的所有消息 */
    @OnPrivate
    public void listen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, "我收到了");
        sender.SENDER.sendPrivateMsg(msg, msg.getMsgContent());
    }

}
