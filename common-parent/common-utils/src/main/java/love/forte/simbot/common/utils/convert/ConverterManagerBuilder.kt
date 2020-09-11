package love.forte.simbot.common.utils.convert


/**
 *
 * [ConverterManager]的构建器
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
interface ConverterManagerBuilder2 {

    /**
     * 注册一个转化器。
     * @param cover 如果对应的转化已经存在，是否覆盖。
     */
    fun <T> register(target: Class<T>, cover: Boolean, converter: Converter<T>): Boolean


    /**
     * 构建要一个 [ConverterManager]
     */
    fun build(): ConverterManager
}


