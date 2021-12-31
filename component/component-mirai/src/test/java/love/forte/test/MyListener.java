package love.forte.test;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;

/**
 * @author ForteScarlet
 */
@Beans
public class MyListener {

    @OnGroup
    public void quoteTest(GroupMsg msg, Sender sender) {
        sender.sendGroupMsgAsync(msg, "[CAT:quote] hi!");
    }


}
