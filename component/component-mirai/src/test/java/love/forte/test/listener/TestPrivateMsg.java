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

import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.BotInfo;
import love.forte.simbot.api.message.events.PrivateMsg;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ConstantConditions")
public class TestPrivateMsg implements PrivateMsg {

    @NotNull
    @Override
    public String toString() {
        return super.toString();
    }

    @NotNull
    @Override
    public AccountInfo getAccountInfo() {
        return null;
    }

    @NotNull
    @Override
    public BotInfo getBotInfo() {
        return null;
    }

    @NotNull
    @Override
    public String getOriginalData() {
        return null;
    }

    @NotNull
    @Override
    public String getId() {
        return null;
    }

    @Override
    public long getTime() {
        return 0;
    }

    @NotNull
    @Override
    public MessageContent getMsgContent() {
        return null;
    }

    @NotNull
    @Override
    public Type getPrivateMsgType() {
        return null;
    }

    @NotNull
    @Override
    public MessageFlag getFlag() {
        return null;
    }



}