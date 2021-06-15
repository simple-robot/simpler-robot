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

package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.ListenGroup;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.containers.DetailAccountInfo;
import love.forte.simbot.api.message.containers.GroupAccountInfo;
import love.forte.simbot.api.message.containers.GroupBotInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.listener.ListenerFunction;
import love.forte.simbot.listener.ListenerGroup;

/**
 *
 * @author ForteScarlet
 */
@Beans
public class TestListener2 {

    @ListenGroup({"group3", "group4"})
    @OnGroup
    public void l3(GroupMsg msg){
        System.out.println(msg);
        GroupBotInfo botInfo = msg.getBotInfo();
        System.out.println("botInfo = " + botInfo);
        System.out.println("botInfo.getPermission() = " + botInfo.getPermission());
        System.out.println("botInfo.getAccountTitle() = " + botInfo.getAccountTitle());

        GroupAccountInfo accountInfo = msg.getAccountInfo();
        System.out.println("accountInfo = " + accountInfo);
        if (accountInfo instanceof DetailAccountInfo) {
            System.out.println("((DetailAccountInfo) accountInfo).getAge() = " + ((DetailAccountInfo) accountInfo).getAge());
            System.out.println("((DetailAccountInfo) accountInfo).getEmail() = " + ((DetailAccountInfo) accountInfo).getEmail());
            System.out.println("((DetailAccountInfo) accountInfo).getSignature() = " + ((DetailAccountInfo) accountInfo).getSignature());
            System.out.println("((DetailAccountInfo) accountInfo).getGender() = " + ((DetailAccountInfo) accountInfo).getGender());
            System.out.println("((DetailAccountInfo) accountInfo).getPhone() = " + ((DetailAccountInfo) accountInfo).getPhone());
        }

        System.out.println();

    }

    @ListenGroup({"group3", "group4"})
    @OnPrivate
    public void l4(ListenerFunction function){

        System.out.println(function.getGroups());

        System.out.println();

        ListenerGroup group = function.getGroups().get(0);
        for (ListenerFunction listener : group.getListeners()) {
            System.out.println("listener " + listener.getId() + " In Group " + group.getName());
        }

    }

}
