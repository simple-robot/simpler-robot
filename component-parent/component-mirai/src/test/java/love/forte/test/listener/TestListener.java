package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.*;
import love.forte.simbot.api.message.events.*;
import love.forte.simbot.api.sender.MsgSender;

@Beans
public class TestListener {


    @OnPrivateMsgRecall
    public void onPri(PrivateMsgRecall msg) {
        System.out.println(msg);
        System.out.println(msg.getText());
        System.out.println(msg.getMsg());
    }

    @OnGroupMsgRecall
    public void onGro(GroupMsgRecall msg) {
        System.out.println(msg);
        System.out.println(msg.getText());
        System.out.println(msg.getMsg());
    }


}
