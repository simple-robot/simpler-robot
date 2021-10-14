package love.forte.test.listeners;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.Sender;

/**
 * @author ForteScarlet
 */
@Beans
public class PriListen {


    @OnPrivate
    public void run(PrivateMsg msg, Sender sender){
        System.out.println(msg);
        System.out.println(msg.getText());
        System.out.println(msg.getMsg());
        sender.sendPrivateMsg(msg, "HI");
    }

}
