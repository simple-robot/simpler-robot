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
import love.forte.simbot.annotation.ListenBreak;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.annotation.SpareListen;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.events.PrivateMsg;

/**
 * @author ForteScarlet
 */
@Beans
@OnPrivate
public class TestListener {



    @ListenBreak
    public void lis1(PrivateMsg msg) {
        System.out.println("lis1.");
        AccountInfo accountInfo = msg.getAccountInfo();
        System.out.println(accountInfo.getAccountNickname());
    }


    public boolean lis2(PrivateMsg msg) {
        System.out.println("lis2.");
        AccountInfo accountInfo = msg.getAccountInfo();
        System.out.println(accountInfo.getAccountNickname() + ": " + msg.getText());
        return msg.getText().equals("123");
    }

    @SpareListen
    public void lis3(PrivateMsg msg) {
        System.out.println("spare!");
        AccountInfo accountInfo = msg.getAccountInfo();
        System.out.println(accountInfo.getAccountNickname());
    }

}
