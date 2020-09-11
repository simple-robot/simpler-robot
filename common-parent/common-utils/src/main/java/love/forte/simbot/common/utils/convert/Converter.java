package love.forte.simbot.common.utils.convert;

/**
 *
 * 类型转化器接口
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface Converter<T> {
    /**
     * 将一个 任意类型转化为 {@code T}(Target)类型>
     * @param o 被转化的类型
     * @return 转化后的结果
     */
    T convert(Object o);
}
