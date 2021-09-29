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
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnlySession;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.filter.MatchType;
import love.forte.simbot.listener.ContinuousSessionScopeContext;
import love.forte.simbot.listener.ListenerContext;
import love.forte.simbot.listener.SessionCallback;

/**
 * @author ForteScarlet
 */
// @Beans
// @OnGroup
public class JListener {

    private static final String key1 = "==tellMeYourNameAndPhone==PHONE==";
    private static final String key2 = "==tellMeYourNameAndPhone==NAME==";

    @Filter(value = "tellme", groups = "1043409458")
    public void tellme(GroupMsg m, ListenerContext context, Sender sender) {
        final ContinuousSessionScopeContext sessionContext = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert sessionContext != null;


        final String groupCode = m.getGroupInfo().getGroupCode();
        final String code = m.getAccountInfo().getAccountCode();

        String key = groupCode + ":" + code;

        sender.sendGroupMsg(m, "[CAT:quote,id=" + m.getId() + "] 请输入手机号");


        final SessionCallback<GroupMsg> callback = SessionCallback.builder(GroupMsg.class).onResume(msg -> {
            final String text = msg.getText();
            final long phone = Long.parseLong(text);
            sender.sendGroupMsg(msg, "手机号为: " + phone);
            sender.sendGroupMsg(msg, "[CAT:quote,id=" + m.getId() + "] 请输入姓名");

            // wait.
            sessionContext.waiting(key2, key, name -> {
                sender.sendGroupMsg(msg, "姓名为 " + name);
                sender.sendGroupMsg(msg, name + "的手机号为" + phone);
            });

        }).onError(e -> {
            System.out.println("onError: 出错啦: " + e);
        }).onCancel(e -> {
            System.out.println("onCancel 关闭啦: " + e);
        }).build();

        // Do waiting
        sessionContext.waiting(key1, key, callback);
    }

    @OnlySession(group = key1)
    @Filter(value = "\\d+", matchType = MatchType.REGEX_MATCHES, groups = "1043409458")
    public void phone(GroupMsg m, ListenerContext context) {
        System.out.println("phone >> " + m);
        final ContinuousSessionScopeContext session = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert session != null;

        final String groupCode = m.getGroupInfo().getGroupCode();
        final String code = m.getAccountInfo().getAccountCode();

        String key = groupCode + ":" + code;

        session.push(key1, key, m);
    }

    @OnlySession(group = key2)
    @Filter(groups = "1043409458")
    public void onName(GroupMsg m, ListenerContext context) {
        System.out.println("name >> " + m);

        final ContinuousSessionScopeContext session = (ContinuousSessionScopeContext) context.getContext(ListenerContext.Scope.CONTINUOUS_SESSION);
        assert session != null;
        final String groupCode = m.getGroupInfo().getGroupCode();
        final String code = m.getAccountInfo().getAccountCode();

        String key = groupCode + ":" + code;
        session.push(key2, key, m.getText());
    }

}
