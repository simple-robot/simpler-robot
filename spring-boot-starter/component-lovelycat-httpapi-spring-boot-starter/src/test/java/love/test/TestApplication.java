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

package love.test;

import love.forte.simbot.api.sender.BotSender;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ForteScarlet
 */
@EnableSimbot
@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        BotManager bean = SpringApplication.run(TestApplication.class, args).getBean(BotManager.class);
        BotSender sender = bean.getDefaultBot().getSender();
        // System.out.println(sender.getBotInfo());
        // System.out.println("-------------------------------");
        // for (FriendInfo friendInfo : sender.GETTER.getFriendList()) {
        //     System.out.println(friendInfo);
        // }
        // System.out.println("-------------------------------");
        //
        // System.out.println(sender.GETTER.getAuthInfo());
        // for (SimpleGroupInfo simpleGroupInfo : sender.GETTER.getGroupList()) {
        //     System.out.println("group: " + simpleGroupInfo);
        //     GroupMemberList groupMemberList = sender.GETTER.getGroupMemberList(simpleGroupInfo);
        //     for (GroupMemberInfo groupMemberInfo : groupMemberList) {
        //         System.out.println("\tmember: " + groupMemberInfo);
        //     }
        // }

    }
}
