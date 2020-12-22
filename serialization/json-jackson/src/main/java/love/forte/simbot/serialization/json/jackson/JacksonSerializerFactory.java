/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.serialization.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import love.forte.simbot.serialization.json.JsonSerializer;
import love.forte.simbot.serialization.json.JsonSerializerFactory;

import java.lang.reflect.Type;

/**
 * Jackson 的 {@link JsonSerializerFactory} 实现。
 * @author ForteScarlet
 */
public class JacksonSerializerFactory implements JsonSerializerFactory {

    private final ObjectMapper objectMapper;

    public JacksonSerializerFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 获取一个对应类型的jackson解析器。
     *
     * @param type 类型。
     * @return 对应的解析器。
     */
    @Override
    public <T> JsonSerializer<T> getJsonSerializer(Type type) {
        return new JacksonSerializer<>(objectMapper, type);
    }

    /**
     * 获取一个对应类型的jackson解析器。
     *
     * @param type 类型。
     * @return 对应的解析器。
     */
    @Override
    public <T> JsonSerializer<T> getJsonSerializer(Class<T> type) {
        return new JacksonSerializer<>(objectMapper, type);
    }
}
