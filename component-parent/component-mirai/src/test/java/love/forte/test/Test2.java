package love.forte.test;

import love.forte.common.utils.annotation.AnnotationUtil;
import love.forte.simbot.annotation.Filters;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.test.listener.PrivateLis;

import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class Test2 {
    public static void main(String[] args) throws NoSuchMethodException {
        final Method method = PrivateLis.class.getDeclaredMethod("nudgeLis", PrivateMsg.class, MsgSender.class);

        final Filters filters = AnnotationUtil.getAnnotation(method, Filters.class);

        System.out.println(filters);

    }

}
