/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     MyGroupedInterceptor.java
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.test;

import love.forte.simbot.core.intercept.FixedRangeGroupedListenerInterceptor;
import love.forte.simbot.intercept.InterceptionType;
import love.forte.simbot.listener.ListenerInterceptContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author ForteScarlet
 */
public class MyGroupedInterceptor extends FixedRangeGroupedListenerInterceptor {

    @NotNull
    @Override
    protected String[] getGroupRange() {
        return new String[]{"group1", "group2"};
    }

    @NotNull
    @Override
    protected InterceptionType doIntercept(@NotNull ListenerInterceptContext context, String group) {
        System.out.println("拦截到了 name = "+ context.getListenerFunction().getName() +"！属于分组" + group);
        return InterceptionType.PASS;
    }
}
