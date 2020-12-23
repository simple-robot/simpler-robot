package love.forte.test.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.FilterValue;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;

@Beans
public class TestListener {

    private MessageContent imgs;

    private final MessageContentBuilderFactory builderFactory;

    public TestListener(MessageContentBuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
        MessageContentBuilder b = builderFactory.getMessageContentBuilder();
        imgs = b.text("img:").image("http://forte.love:15520/img/r")
                .image("http://forte.love:15520/img/r")
                .image("http://forte.love:15520/img/r")
                .image("http://forte.love:15520/img/r")
                .image("http://forte.love:15520/img/r")
                .build()
        ;
    }


    @OnPrivate
    @Filter(value = "img{{num,\\d+}}", matchType = MatchType.REGEX_MATCHES)
    public void onPri(@FilterValue("num") int num, PrivateMsg msg, MsgSender sender) {
        System.out.println("num: " + num);
        System.out.println("sending...");
        if (num == 5){
            sender.SENDER.sendGroupMsg(703454734, imgs);
            sender.SENDER.sendGroupMsg(1043409458, imgs);
            sender.SENDER.sendGroupMsg(881263027, imgs);
        } else {
            MessageContentBuilder b = builderFactory.getMessageContentBuilder();
            for (int i = 0; i < num; i++) {
                b.image("http://forte.love:15520/img/r");
            }
            MessageContent c = b.build();
            sender.SENDER.sendGroupMsg(703454734, c);
            sender.SENDER.sendGroupMsg(1043409458, c);
            sender.SENDER.sendGroupMsg(881263027, c);
        }

        System.out.println("sent.");
    }


}
