package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;

@Beans
public class TestListener {


    @OnPrivate
    public void listenQuote(PrivateMsg msg){
        long n1 = System.nanoTime();
        System.out.println(msg.getText());
        long n2 = System.nanoTime();
        System.out.println(msg.getMsg());
        long n3 = System.nanoTime();
        System.out.println("text time:\t" + /*TimeUnit.NANOSECONDS.toMillis*/(n2 - n1));
        System.out.println("msg time:\t" + /*TimeUnit.NANOSECONDS.toMillis*/(n3 - n2));
    }


}
