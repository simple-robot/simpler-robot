package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.containers.AccountContainer;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;

@Beans
public class TestListener {
    @OnPrivate
    public void pri1(AccountContainer msg){
        String str = msg.toString();
        System.out.println(str);
        System.out.println(str.length());
        System.out.println(str.toCharArray());
        char c1 = str.charAt(str.length() - 5);
        char c2 = str.charAt(str.length() - 6);
        char c3 = str.charAt(str.length() - 7);
        char c4 = str.charAt(str.length() - 8);
        System.out.println(c1 + "("+ (int) c1 +")");
        System.out.println(c2 + "("+ (int) c2 +")");
        System.out.println(c3 + "("+ (int) c3 +")");
        System.out.println(c4 + "("+ (int) c4 +")");
    }


}
