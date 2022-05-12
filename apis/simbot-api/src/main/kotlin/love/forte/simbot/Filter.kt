/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot


/**
 *
 * 过滤器, 对一个指定的目标进行过滤匹配, 并得到最终的匹配结果。
 *
 * 此过滤器也同样是一个可挂起的。
 *
 * @see BlockingFilter
 *
 * @author ForteScarlet
 */
public interface Filter<T> {

    /**
     * 通过匹配目标进行检测，得到匹配结果。
     *
     */
    @JvmSynthetic
    public suspend fun test(t: T): Boolean

}


/**
 * 使用非挂起函数的 [testBlocking] 来实现 [Filter] 的 [test].
 */
@Api4J
public interface BlockingFilter<T> : Filter<T> {
    
    /**
     * 通过匹配目标进行检测，得到匹配结果。
     *
     */
    @JvmSynthetic
    override suspend fun test(t: T): Boolean {
        return testBlocking()
    }
    
    /**
     * 通过匹配目标进行阻塞的检测，得到匹配结果。
     *
     */
    @Api4J
    public fun testBlocking(): Boolean
}