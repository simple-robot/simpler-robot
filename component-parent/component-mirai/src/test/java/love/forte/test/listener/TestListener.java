package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.annotation.ListenBreak;
import love.forte.simbot.annotation.Listens;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.constant.PriorityConstant;

@Beans
public class TestListener {

    @OnPrivate
    @ListenBreak
    public boolean m1(MsgGet msg) {
        String text = msg.getText().trim();


        return text.startsWith("h");
    }

    @Listens(
            value = @Listen(PrivateMsg.class),
            priority = PriorityConstant.FIFTH
    )
    public void m2(MsgGet msg) {
    }

}
