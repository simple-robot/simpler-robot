package love.forte.simbot.utils.item

/**
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