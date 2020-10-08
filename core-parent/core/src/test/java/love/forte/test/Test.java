package love.forte.test;

import love.forte.common.ioc.annotation.Beans;
import love.forte.common.utils.annotation.AnnotationUtil;
import love.forte.simbot.core.annotation.Listen;
import love.forte.simbot.core.api.message.MsgGet;
import love.forte.simbot.core.configuration.CoreConverterManagerConfiguration;

import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */

public class Test {

    public static void main(String[] args) throws NoSuchMethodException {

        final Method m = CoreConverterManagerConfiguration.class.getMethod("coreConverterManagerBuilder");


        final Beans b = AnnotationUtil.getAnnotation(m, Beans.class);

        System.out.println(b);
        System.out.println(b.priority());

    }
}
