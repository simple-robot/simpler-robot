package love.forte.test.listest;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.core.annotation.*;
import love.forte.simbot.core.api.message.MsgGet;
import love.forte.simbot.core.api.message.events.GroupMsg;
import love.forte.simbot.core.api.message.events.PrivateMsg;
import love.forte.simbot.core.constant.PriorityConstant;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Beans
public class TestFilter {


    @Listen(PrivateMsg.class)
    @Filter("")
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
