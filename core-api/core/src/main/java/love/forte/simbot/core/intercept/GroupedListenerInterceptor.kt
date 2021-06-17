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

package love.forte.simbot.core.intercept

import love.forte.simbot.api.SimbotInternalApi
import love.forte.simbot.intercept.InterceptionType
import love.forte.simbot.listener.ListenerInterceptContext
import love.forte.simbot.listener.ListenerInterceptor
import java.util.concurrent.ConcurrentHashMap


/**
 *
 * 基于监听函数分组的 [监听拦截器][ListenerInterceptor].
 *
 * @author ForteScarlet
 */
public abstract class GroupedListenerInterceptor : ListenerInterceptor {

    /**
     * 是否对监听函数进行缓存。
     * 如果你的 [groupCheck] 和 [nonGroupAble] 中可能得到的结果不是固定的，那么可能你需要关闭缓存。
     * 否则你可以直接开启缓存来提高匹配效率。
     */
    protected abstract val listenerCacheable: Boolean

    /**
     * 是否同时拦截没有分组的监听函数。
     */
    protected abstract val nonGroupAble: Boolean

    /**
     * 对于那些未命中的分组内容，其默认的放行策略。
     *
     * 可进行选择性重写，默认情况下，没有命中的监听函数将会被直接放行。
     *
     */
    protected open val missInterceptionType: InterceptionType get() = InterceptionType.PASS

    /**
     * 对于那些没有分组的监听函数，其默认的放行策略。
     *
     * 只有 [nonGroupAble] == false 的时候才会生效。
     *
     * 可进行选择性重写，默认情况下，没有命中的监听函数将会被直接放行。
     *
     */
    protected open val nonGroupInterceptionType: InterceptionType get() = InterceptionType.PASS


    /**
     * 对某个监听分组进行验证。
     */
    protected abstract fun groupCheck(group: String): Boolean


    /**
     * 执行真正的拦截逻辑。
     *
     * [group] 则代表当前函数是根据具体的哪一个分组通过匹配的。一般是分组中最前面的一个。
     * 如果你允许无分组函数也通过，则无函数分组的 [group] 将会为null。
     *
     * 当然，你依旧可以通过 [context.listenerFunction][ListenerInterceptContext.listenerFunction].[groups][love.forte.simbot.listener.ListenerFunction.groups] 查看这个函数的具体分组。
     *
     */
    protected abstract fun doIntercept(context: ListenerInterceptContext, group: String?): InterceptionType


    private val listenerCache: MutableMap<String, String> = ConcurrentHashMap()


    @OptIn(SimbotInternalApi::class)
    final override fun intercept(context: ListenerInterceptContext): InterceptionType {
        val listener = context.listenerFunction
        val id = listener.id
        if (id in listenerCache) {
            return doIntercept(context, listenerCache[id])
        }

        val functionGroups = context.listenerFunction.groups
        val nonGroupAble = nonGroupAble
        if (functionGroups.isEmpty()) {
            return if (nonGroupAble)
                doIntercept(context, null)
            else nonGroupInterceptionType
        }

        // todo 尚待优化
        for (group in functionGroups) {
            if (groupCheck(group.name)) {
                if (listenerCacheable) {
                    listenerCache[id] = group.name
                }
                return doIntercept(context, group.name)
            }
        }

        return missInterceptionType
    }
}


/**
 * 固定范围的 [GroupedListenerInterceptor] 抽象类。
 * 提供一个固定的[值范围][groupRange]（不可以为空内容）来拦截固定范围内的函数。
 *
 * 默认情况下，[是否拦截无分组函数][nonGroupAble] 为 `false`,
 * [未命中函数][missInterceptionType]、[无分组函数][nonGroupInterceptionType] 的默认拦截方式均为 [放行][InterceptionType.PASS].
 *
 * ```java
 *  @Beans
 *  public class MyGroupedInterceptor extends FixedRangeGroupedListenerInterceptor {
 *
 *      @NotNull
 *      @Override
 *      protected String[] getGroupRange() {
 *          return new String[]{"group1", "group2"};
 *      }
 *
 *      @NotNull
 *      @Override
 *      protected InterceptionType doIntercept(@NotNull ListenerInterceptContext context, String group) {
 *          System.out.println("拦截到了 name = "+ context.getListenerFunction().getName() +"！属于分组" + group);
 *          return InterceptionType.PASS;
 *      }
 *  }
 * ```
 *
 */
public abstract class FixedRangeGroupedListenerInterceptor
@JvmOverloads constructor(
    override val nonGroupAble: Boolean = false,
    override val missInterceptionType: InterceptionType = InterceptionType.PASS,
    override val nonGroupInterceptionType: InterceptionType = InterceptionType.PASS,
) : GroupedListenerInterceptor() {

    /**
     * 得到需要过滤的范围。
     * 此函数只会被调用一次，且得到的结果不应为空。
     */
    protected abstract val groupRange: Array<String>

    override val listenerCacheable: Boolean
        get() = true
    private val groupSet by lazy {
        groupRange.takeIf { it.isNotEmpty() }?.toSet() ?: throw IllegalArgumentException("Groups cannot be empty.")
    }


    override fun groupCheck(group: String): Boolean = group in groupSet
}
