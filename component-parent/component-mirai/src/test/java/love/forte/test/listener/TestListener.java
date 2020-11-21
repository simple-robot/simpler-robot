package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.filter.MatchType;

@Beans
public class TestListener {

    //
    // @OnPrivate
    // @Filter(value = "hi", matchType = MatchType.STARTS_WITH)
    // @Filter(value = "hello", matchType = MatchType.STARTS_WITH)
    // public void l1(PrivateMsg msg){
    //     System.out.println("hi or hello.");
    // }
    // @OnPrivate
    // @Filter(value = "mua", matchType = MatchType.STARTS_WITH)
    // @Filter(value = "qaq", matchType = MatchType.STARTS_WITH)
    // public void l2(PrivateMsg msg){
    //     System.out.println("mua or qaq.");
    // }

    @OnGroup
    @Filter(value = "hi", matchType = MatchType.STARTS_WITH)
    @Filter(value = "hello", matchType = MatchType.STARTS_WITH)
    public void l1(){
        System.out.println("hi or hello.");
    }
    @OnGroup
    @Filter(value = "mua", matchType = MatchType.STARTS_WITH)
    @Filter(value = "qaq", matchType = MatchType.STARTS_WITH)
    public void l2(){
        System.out.println("mua or qaq.");
    }


}
