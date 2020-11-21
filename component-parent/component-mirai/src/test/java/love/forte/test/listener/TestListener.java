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

    @OnPrivate
    @Filter(value = "hi", matchType = MatchType.STARTS_WITH)
    @Filter(value = "hello", matchType = MatchType.STARTS_WITH)
    public void l1(PrivateMsg m, MsgSender sender){
        sender.SENDER.sendPrivateMsg(m, "hi or hello.");
    }
    @OnPrivate
    @Filter(value = "lll", matchType = MatchType.STARTS_WITH)
    @Filter(value = "qaq", matchType = MatchType.STARTS_WITH)
    public void l2(PrivateMsg m, MsgSender sender){
        sender.SENDER.sendPrivateMsg(m, "lll or qaq.");
    }


}
