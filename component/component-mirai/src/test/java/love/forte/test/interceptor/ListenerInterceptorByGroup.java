/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.forte.test.interceptor;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.core.intercept.FixedRangeGroupedListenerInterceptor;
import love.forte.simbot.intercept.InterceptionType;
import love.forte.simbot.listener.ListenerInterceptContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Beans
public class ListenerInterceptorByGroup extends FixedRangeGroupedListenerInterceptor {

    @NotNull
    @Override
    protected InterceptionType doIntercept(@NotNull ListenerInterceptContext context, @Nullable String group) {
        System.out.println("拦截的组：" + group);
        System.out.println("listener: " + context.getListenerFunction().getId());
        // 放行
        return InterceptionType.PASS;
    }

    /**
     * 拦截分组 "Group1" 的监听函数。 只会因初始化而被调用一次。
     *
     */
    @NotNull
    @Override
    protected String[] getGroupRange() {
        return new String[]{"Group1"};
    }
}