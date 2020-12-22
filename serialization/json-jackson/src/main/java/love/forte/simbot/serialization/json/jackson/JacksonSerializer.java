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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import love.forte.simbot.serialization.json.JsonDeserializationException;
import love.forte.simbot.serialization.json.JsonSerializationException;
import love.forte.simbot.serialization.json.JsonSerializer;

import java.lang.reflect.Type;

/**
 *
 * jackson 的 {@link JsonSerializer} 实现。
 *
 * @author ForteScarlet
 */
public class JacksonSerializer<T> implements JsonSerializer<T> {

    private final ObjectMapper objectMapper;

    private final Type type;

    private final JavaType javaType;

    public JacksonSerializer(ObjectMapper objectMapper, Type type) {
        this.objectMapper = objectMapper;
        this.type = type;
        this.javaType = TypeFactory.defaultInstance().constructType(type);
    }

    /**
     * 将实例转化为json字符串。
     *
     * @param entity 实例
     * @return json字符串。
     */
    @Override
    public String toJson(T entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException(entity.toString(), e);
        }
    }

    /**
     * 将json解析为一个类实例。
     *
     * @param json json字符串
     * @return 类实例。
     */
    @Override
    public T fromJson(String json) {
        try {
            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializationException(json, e);
        }
    }
}
