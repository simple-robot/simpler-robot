package love.forte.simbot.common.utils.convert;

import java.lang.reflect.Type;

/**
 *
 * {@link ConverterManager} 的构建器。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface ConverterManagerBuilder {

    /**
     * 注册一个转化器
     * @param target 目标类型
     * @param converter 转化器
     * @return 当前builder
     */
    ConverterManagerBuilder register(Type target, Converter<?> converter);

    /**
     * 构建一个 {@link ConverterManager} 实例。
     * @return {@link ConverterManager} 实例
     */
    ConverterManager build();
}
