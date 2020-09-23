package love.forte.test;

import love.forte.common.utils.annotation.AnnotationUtil;
import love.forte.simbot.core.annotation.Listen;
import love.forte.simbot.core.api.message.MsgGet;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */

public class Test {

    public static void main(String[] args) {

        final Listen annotation = AnnotationUtil.getAnnotation(Test.class, Listen.class);

        System.out.println(annotation);

    }
}
