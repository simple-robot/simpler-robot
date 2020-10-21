package love.forte.test.listest;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.filter.FilterData;
import love.forte.simbot.filter.ListenerFilter;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Beans("MyFilter")
public class MyFilter implements ListenerFilter {

    @Override
    public boolean test(@NotNull FilterData data) {
        // do something...
        return true;
    }
}
