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

import love.forte.catcode.Neko;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.component.lovelycat.message.event.LovelyCatReceivedTransfer;
import love.forte.simbot.component.lovelycat.message.event.LovelyCatScanCashMoney;

/**
 * @author ForteScarlet
 */
@Beans
public class TestListener {

    @OnGroup
    public void groupMsg(GroupMsg msg, MsgSender sender) {
        System.out.println(sender.GETTER.getGroupInfo(msg));
        for (GroupMemberInfo groupMemberInfo : sender.GETTER.getGroupMemberList(msg)) {
            System.out.println(groupMemberInfo);
        }
    }

}
