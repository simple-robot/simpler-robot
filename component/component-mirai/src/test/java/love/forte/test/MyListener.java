package love.forte.test;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.Sender;

/**
 * @author ForteScarlet
 */
@Beans
public class MyListener {

    @OnPrivate
    public void reply(PrivateMsg msg, Sender sender) {
        sender.sendPrivateMsgAsync(msg, msg.getMsgContent());
    }


}
