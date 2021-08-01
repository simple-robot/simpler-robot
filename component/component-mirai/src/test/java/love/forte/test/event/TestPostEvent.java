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

package love.forte.test.event;

import cn.hutool.core.util.IdUtil;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.component.mirai.MiraiBotAccountInfo;
import love.forte.simbot.component.mirai.message.event.AbstractMiraiMsgGet;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessagePostSendEvent;
import net.mamoe.mirai.message.data.PlainText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

/**
 * @author ForteScarlet
 */
public class TestPostEvent<C extends Contact> extends AbstractMiraiMsgGet<MessagePostSendEvent<C>> {
    public TestPostEvent(@NotNull MessagePostSendEvent<C> event) {
        super(event);
    }

    @NotNull
    @Override
    public String getId() {
        return IdUtil.fastUUID();
    }

    private final AccountInfo accountInfo = new MiraiBotAccountInfo(getEvent().getBot());

    @NotNull
    @Override
    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    private final String text = getEvent().getMessage().stream()
            .filter(m -> m instanceof PlainText)
            .map(m -> ((PlainText) m).getContent())
            .collect(Collectors.joining(""));

    @Nullable
    @Override
    public String getText() {
        return text;
    }
}
