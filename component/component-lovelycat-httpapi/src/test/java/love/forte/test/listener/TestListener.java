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
import love.forte.simbot.api.message.Reply;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @author ForteScarlet
 */
@Beans
public class TestListener {

    @OnGroup
    public Object groupMsg(GroupMsg msg, MsgSender sender) {
        // sender.SENDER.sendGroupMsg("");
        return Reply.reply(msg.getMsgContent(), true);
        // return msg.getMsgContent();
    }

    @OnPrivate
    public Object privateMsg(PrivateMsg msg, MsgSender sender) {
        return Reply.reply(msg.getMsgContent(), true);
    }

}
