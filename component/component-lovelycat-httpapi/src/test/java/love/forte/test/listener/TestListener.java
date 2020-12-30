/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.test.listener;

import love.forte.catcode.Neko;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.component.lovelycat.message.event.LovelyCatReceivedTransfer;
import love.forte.simbot.component.lovelycat.message.event.LovelyCatScanCashMoney;

/**
 * @author ForteScarlet
 */
@Beans
public class TestListener {


    private final MessageContentBuilderFactory messageContentBuilderFactory;

    public TestListener(MessageContentBuilderFactory messageContentBuilderFactory) {
        this.messageContentBuilderFactory = messageContentBuilderFactory;
    }


    @OnGroup
    @Filter(atBot = true)
    public void groupMsg(GroupMsg msg, MsgSender sender) {
        System.out.println("text: " + msg.getText());
        System.out.println("msg : " + msg.getMsg());
        System.out.println("ori : " + msg.getOriginalData());

        for (Neko cat : msg.getMsgContent().getCats()) {
            System.out.println(cat);
        }

        MessageContentBuilder builder = messageContentBuilderFactory.getMessageContentBuilder();

        MessageContent msgContent = builder.at(msg).text("at你爹？").build();

        // 复读
        sender.SENDER.sendGroupMsg(msg, msgContent);
    }

    @OnPrivate
    public void privateMsg(PrivateMsg msg, MsgSender sender) {
        System.out.println("text: " + msg.getText());
        System.out.println("msg : " + msg.getMsg());
        System.out.println("ori : " + msg.getOriginalData());
        // 复读
        sender.SENDER.sendPrivateMsg(msg, msg.getMsgContent());
    }


    @Listen(LovelyCatReceivedTransfer.class)
    public void transfer(LovelyCatReceivedTransfer receivedTransfer, MsgSender sender){
        if (!receivedTransfer.isAccepted()) {
            receivedTransfer.accept();
            sender.SENDER.sendPrivateMsg(receivedTransfer, "谢谢你的" + receivedTransfer.getMoney() + "块钱~");
        }

    }


    @Listen(LovelyCatScanCashMoney.class)
    public void scanPay(LovelyCatScanCashMoney scanCashMoney){
        System.out.println("==================");
        System.out.println(scanCashMoney.getMoney());
        System.out.println(scanCashMoney.getAccountInfo());
        System.out.println(scanCashMoney.getPaySourceInfo());
        System.out.println(scanCashMoney.getText());
        System.out.println(scanCashMoney.getPayInfo().getMilliTimestamp());
        System.out.println("==================");
    }

}
