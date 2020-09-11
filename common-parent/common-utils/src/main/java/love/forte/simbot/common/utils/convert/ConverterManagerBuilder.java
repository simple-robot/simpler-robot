package love.forte.simbot.common.utils.convert;

import java.lang.reflect.Type;

/**
 *
 * {@link ConverterManager} 的构建器。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface ConverterManagerBuilder {

    ConverterManagerBuilder register(Type target, Converter<?> converter);

    ConverterManager build();
}
