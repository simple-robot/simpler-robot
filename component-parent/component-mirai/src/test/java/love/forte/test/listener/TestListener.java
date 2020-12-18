package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.*;
import love.forte.simbot.api.message.events.*;
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


}
