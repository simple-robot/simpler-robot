package love.forte.common.api.listener



/**
 * 监听函数的执行结果
 */
public interface ListenResult<T> {
    /**
     * 是否执行成功
     */
    fun isSuccess(): Boolean

    /**
     * 是否截断接下来的监听函数
     */
    fun isBreak(): Boolean

    /**
     * 得到一个执行结果
     */
    val result: T?

    /**
     * 如果出现了异常，得到这个异常。
     */
    val err: Throwable?
}