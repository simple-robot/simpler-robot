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
        System.out.println("m1");
        System.out.println("on: " + msg);
        String text = msg.getText().trim();

        System.out.println(text);
        System.out.println(text.startsWith("h"));

        return text.startsWith("h");
    }

    @Listens(
            value = @Listen(PrivateMsg.class),
            priority = PriorityConstant.FIFTH
    )
    public void m2(MsgGet msg) {
        System.out.println("m2");
        System.out.println("on: " + msg);
    }

}
