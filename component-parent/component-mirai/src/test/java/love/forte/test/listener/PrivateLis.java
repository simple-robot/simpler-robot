package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Beans
public class PrivateLis {

    @Depend
    private MessageContentBuilderFactory builderFactory;

    @OnPrivate
    @Filter(value = "mua", matchType = MatchType.STARTS_WITH)
    public void pTest(PrivateMsg privateMsg, MsgSender sender){

        System.out.println(privateMsg);
        System.out.println("获取msg。");
        System.out.println(privateMsg.getMsg());
        System.out.println("获取text。");
        System.out.println(privateMsg.getText());


        // System.out.println(builderFactory);
        // System.out.println(builderFactory.getMessageContentBuilder());
        //
        // sender.SENDER.sendPrivateMsg(privateMsg, privateMsg.getMsgContent());
        //
        // final CatCodeUtil catCodeUtil = CatCodeUtil.INSTANCE;
        //
        // String nudge = catCodeUtil.toCat("nudge");
        //
        // sender.SENDER.sendPrivateMsg(privateMsg, nudge);
    }

}
