package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

@Beans
public class TestListener {


    @OnGroup
    @Filter(anyAt = true)
    public void onGro(GroupMsg msg) {
        System.out.println(msg);
        System.out.println(msg.getText());
        System.out.println(msg.getMsg());
    }

    @OnPrivate
    public void onPri(PrivateMsg msg, MsgSender sender) {
        System.out.println(msg);
        System.out.println(msg.getText());
        System.out.println(msg.getMsg());
        sender.SENDER.sendPrivateMsg(msg, msg.getMsgContent());
    }


}
