import love.forte.simbot.Identifies;
import love.forte.simbot.message.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ForteScarlet
 */
public class MsgShowTest1 {
    public static void main(String[] args) {

        List<Message.Element<?>> messageList = new ArrayList<>(3);
        messageList.add(Text.of("simbot"));
        messageList.add(new At(Identifies.ID(123)));
        messageList.add(AtAll.INSTANCE);

        // 通过列表得到消息链
        final Messages messagesOfList = Messages.listToMessages(messageList);

        // 注意! Messages 不允许直接的修改操作
        // messagesOfList.add(AtAll.INSTANCE);

        // 需要通过 plus 得到新的消息链
        final Messages newMessagesOfList = messagesOfList.plus(AtAll.INSTANCE);

        // 通过 Messages.getMessages 得到消息链
        final Messages messages = Messages.getMessages(Text.of("forte"), new At(Identifies.ID(114514)), AtAll.INSTANCE);


    }
}
