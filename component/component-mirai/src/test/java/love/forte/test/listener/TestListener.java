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
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.Filters;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.annotation.Listens;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.constant.PriorityConstant;
import love.forte.simbot.filter.FilterTargets;
import love.forte.simbot.listener.ListenerContext;

import java.util.Objects;

/**
 * @author ForteScarlet
 */
@Beans
// @OnPrivate
public class TestListener {



    @Listens(
            value = @Listen(PrivateMsg.class),
            priority = 0
    )
    @Filters(customFilter = "MyFilter")
    public void listen1(PrivateMsg msg, MsgSender sender, ListenerContext context) {
        System.out.println("l1");
        sender.SENDER.sendPrivateMsg(msg, "我收到了");
        sender.SENDER.sendPrivateMsg(msg, msg.getMsgContent());
        if (Objects.equals(msg.getText(), "2")) {
            System.out.println("set value.");
            context.instant("value", "value");
        }
    }

    @Listens(
            value = @Listen(PrivateMsg.class),
            priority = PriorityConstant.COMPONENT_EIGHTH
    )
    @Filter(value = "value", target = FilterTargets.CONTEXT_INSTANT_NULLABLE + "value")
    public void listen2(PrivateMsg msg, MsgSender sender) {
        System.out.println("l2");
        sender.SENDER.sendPrivateMsg(msg, "我收到了.");
        sender.SENDER.sendPrivateMsg(msg, msg.getMsgContent());
    }


    // @Filter(value = "r")
    // @OnPrivate
    // public void lis3(BotManager botManager) {
    //     botManager.registerBot(new BotRegisterInfo("3521361891", "LiChengYang9983."));
    // }

}
