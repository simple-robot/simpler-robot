package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;

@Beans
public class TestListener {

    @OnGroup
    @Filter(groups = "703454734")
    public void group(GroupMsg msg){
        System.out.println(msg.getBotInfo().getBotCode() + " on group msg: " + msg);
    }

}
