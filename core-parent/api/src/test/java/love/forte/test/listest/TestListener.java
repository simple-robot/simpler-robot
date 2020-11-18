package love.forte.test.listest;

import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.annotation.Listens;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.constant.PriorityConstant;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Beans
public class TestListener {

    @Listen(PrivateMsg.class)
    public void privateListen1(PrivateMsg msg){
        // ... do something
    }

    @Listen(PrivateMsg.class)
    @Listen(GroupMsg.class)
    public void testListen2(MsgGet msg){
        // ... do something
    }


    @Listens(value = {
            @Listen(PrivateMsg.class),
            @Listen(GroupMsg.class)
    }, priority = PriorityConstant.FIRST, name = "privateListen3")
    public void testListen3(MsgGet msg){
        // ... do something
    }

    @OnPrivate
    @OnGroup
    public void testListen4(MsgGet msg){
        // ... do something
    }


}
