package love.forte.test.listest;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.Filters;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.filter.MatchType;
import love.forte.simbot.filter.MostMatchType;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Beans
public class TestFilter {


    @OnPrivate
    @Filter(value = "hi", matchType = MatchType.STARTS_WITH)
    public void testListen1(PrivateMsg msg){
        // ... do something
    }

    @OnPrivate
    @Filter(value = "hi1", matchType = MatchType.STARTS_WITH)
    @Filter(value = "hi2", matchType = MatchType.STARTS_WITH)
    public void testListen2(PrivateMsg msg){
        // ... do something
    }


    @OnPrivate
    @Filters(value = {
            @Filter(value = "hi1", matchType = MatchType.STARTS_WITH),
            @Filter(value = "hi2", matchType = MatchType.STARTS_WITH)
    },
            mostMatchType = MostMatchType.ANY,
            customFilter = {"f1", "f2"}
            // ... other params
    )
    public void testListen3(PrivateMsg msg){
        // ... do something
    }

    @OnPrivate
    @Filters(customFilter = "MyFilter")
    public void testListen4(PrivateMsg msg){
        // ... do something
    }

    @OnPrivate
    @Filters(customFilter = {"MyFilter1", "MyFilter2"})
    public void testListen5(PrivateMsg msg){
        // ... do something
    }


}
