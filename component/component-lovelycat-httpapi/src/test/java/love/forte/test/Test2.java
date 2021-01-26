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

package love.forte.test;

import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.component.lovelycat.message.event.BaseLovelyCatEvents;


/**
 * @author ForteScarlet
 */
public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        GroupInfo groupInfo = BaseLovelyCatEvents.lovelyCatGroupInfo("18367333210@chatroom", "xxx群聊");
        System.out.println(groupInfo.getGroupCodeNumber());

    }
}
