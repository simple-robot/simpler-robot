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
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @author ForteScarlet
 */
@Beans
public class TestListener {

    @OnGroup
    public void groupMsg(GroupMsg msg, MsgSender sender) {
        System.out.println("On msg: " + msg.getText());
        sender.SENDER.sendGroupMsg(msg, msg.getMsgContent());
    }

    @OnPrivate
    public void privateMsg(PrivateMsg msg, MsgSender sender) {
        System.out.println("On msg: " + msg.getText());
        sender.SENDER.sendPrivateMsg(msg, msg.getMsgContent());
    }

}
