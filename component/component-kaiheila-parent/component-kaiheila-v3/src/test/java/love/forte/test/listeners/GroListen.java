package love.forte.test.listeners;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.Sender;

/**
 * @author ForteScarlet
 */
@Beans
public class GroListen {


    @OnGroup
    public void run(GroupMsg msg, Sender sender){
        System.out.println(msg);
        System.out.println(msg.getText());
        System.out.println(msg.getMsg());
        sender.sendGroupMsg(msg, "喵？");
    }

}
