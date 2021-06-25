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

package love.test.guild.listen;


import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.Sender;
import org.springframework.stereotype.Component;

/**
 * @author ForteScarlet
 */
@Component
public class TestListen {

    @OnGroup
    public void group(GroupMsg groupMsg, Sender sender) {
        System.out.println(groupMsg);
        sender.sendGroupMsg(groupMsg, groupMsg.getMsgContent());
    }

    @OnPrivate
    public void pri(PrivateMsg privateMsg, Sender sender) {
        System.out.println(privateMsg);
        sender.sendPrivateMsg(privateMsg, privateMsg.getMsgContent());
    }


}
