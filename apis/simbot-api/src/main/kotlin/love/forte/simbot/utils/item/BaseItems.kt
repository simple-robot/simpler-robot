/*
 *  Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
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
 */

package love.forte.simbot.utils.item

/**
 *
 * [Items] 的基础抽象类。
 *
 * 提供针对 [limit]、[offset]、[batch] 三个预处理属性的标准处理，并将配置属性值通过 [preprocessingProperties] 向子类提供。
 *
 * @author ForteScarlet
 */
public abstract class BaseItems<out T, out I : BaseItems<T, I>> : Items<T> {
    @Suppress("MemberVisibilityCanBePrivate")
    protected var limit: Int = -1
        private set
    
    @Suppress("MemberVisibilityCanBePrivate")
    protected var offset: Int = -1
        private set
    
    @Suppress("MemberVisibilityCanBePrivate")
    protected var batch: Int = -1
        private set
    
    /**
     * 得到作为泛型类型 [I] 的实例。
     */
    protected abstract val self: I
    
    /**
     * 数据限流。取得的数据条数的最大上限。当 [count] < 0 时无效。
     *
     */
    override fun limit(count: Int): I = self.also {
        if (count >= 0) {
            limit = count
        }
    }
    
    /**
     * 数据偏移。从 [count] 数量之后的数据后开始获取。当 [count] < 0 时无效。
     */
    override fun offset(count: Int): I = self.also {
        if (count >= 0) {
            offset = count
        }
    }
    
    /**
     * 批次数量。如果支持批次获取的话，则每批次获取 [size] 的数。通常 [size] > 0 时有效。
     */
    override fun batch(size: Int): I = self.also {
        if (size >= 0) {
            batch = size
        }
    }
    
    /**
     * 根据预处理参数构建得到 [Items.PreprocessingProperties] 实例。
     */
    protected open val preprocessingProperties: Items.PreprocessingProperties
        get() = Items.PreprocessingProperties(limit, offset, batch)
    
    
    
}