package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.annotation.ListenBreak;
import love.forte.simbot.annotation.Listens;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.constant.PriorityConstant;
import love.forte.simbot.core.listener.ListenResultImpl;
import love.forte.simbot.listener.ListenResult;

@Beans
public class TestListener {

    // @OnPrivate
    @Listens(value = @Listen(PrivateMsg.class),
            priority = PriorityConstant.FIRST)
    @ListenBreak
    public ListenResult<?> msg1(PrivateMsg msg){
        System.out.println("msg1");
        System.out.println(msg);
        ListenResult<?> successResult = ListenResultImpl.success(null, true);
        return successResult;
    }

    @Listens(value = @Listen(PrivateMsg.class))
    public void msg2(PrivateMsg msg){
        System.out.println("msg2");
        System.out.println(msg);
    }



}
