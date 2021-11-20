package love.forte.simbot


/**
 *
 * 过滤器, 对一个指定的目标进行过滤匹配, 并得到最终的匹配结果。
 *
 * 此过滤器也同样是一个可挂起的。
 *
 * @author ForteScarlet
 */
public interface Filter<T> {

    /**
     * 通过匹配目标进行检测，得到匹配结果。
     *
     */
    public suspend fun test(t: T): Boolean

}