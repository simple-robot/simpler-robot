package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;

@Beans
public class TestListener {
    @OnGroup
    @Filter(bots = "2370606773", groups = "703454734")
    public void gro1(GroupMsg msg){
        System.err.println("2370606773: " + msg);
    }

    @OnGroup
    @Filter(bots = "3521361891", groups = "703454734")
    public void gro2(GroupMsg msg){
        System.err.println("3521361891: " + msg);
    }

}
