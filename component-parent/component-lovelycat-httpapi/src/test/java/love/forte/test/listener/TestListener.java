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
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.annotation.Listens;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.component.lovelycat.message.event.LovelyCatReceivedTransfer;

/**
 * @author ForteScarlet
 */
@Beans
public class TestListener {


    @Listen(LovelyCatReceivedTransfer.class)
    public void group(LovelyCatReceivedTransfer receivedTransfer){
        System.out.println("==================");
        System.out.println(receivedTransfer.getMoney());
        System.out.println(receivedTransfer.getTransferInfo());
        System.out.println(receivedTransfer.getTransferInfo().getMoney());
        System.out.println(receivedTransfer.getTransferInfo().getMoney().doubleValue());
        receivedTransfer.accept();
        System.out.println("==================");
    }

}
