package love.forte.common.api.listener


/**
 * 定义一个**监听函数**。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
interface Listener : Comparable<Listener> {

    /**
     * 代表了当前listener的唯一ID。
     */
    val id: String

    /**
     * 此监听函数的名称。
     */
    val name: String


    /**
     * 执行监听函数，并得到一个执行结果
     */
    fun invoke(): ListenResult<*>


}