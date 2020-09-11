package love.forte.common.api.listener


/**
 *
 * 判断当前监听事件中，bot是否被at了。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public fun interface AtDetection {
    fun test(): Boolean
}