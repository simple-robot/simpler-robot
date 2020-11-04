/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     FastJsonSerializerFactory.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.serialization.json.fastjson;import love.forte.simbot.serialization.json.JsonSerializer;
import love.forte.simbot.serialization.json.JsonSerializerFactory;

import java.lang.reflect.Type;

/**
 *
 * fast json 解析器工厂。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public final class FastJsonSerializerFactory implements JsonSerializerFactory {
    @Override
    public <T> JsonSerializer<T> getJsonSerializer(Type type) {
        return new FastJsonSerializer<>(type);
    }

    @Override
    public <T> JsonSerializer<T> getJsonSerializer(Class<T> type) {
        return new FastJsonSerializer<>(type);
    }
}
