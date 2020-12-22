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

package love.forte.simbot.serialization.json;

/**
 *
 * 简易 json 序列化器，可以实现json字符串与一个实体类的相互转化。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface JsonSerializer<T> {

    /**
     * 将实例转化为json字符串。
     *
     * @param entity 实例
     * @return json字符串。
     */
    String toJson(T entity);

    /**
     * 将json解析为一个类实例。
     *
     * @param json json字符串
     * @return 类实例。
     */
    T fromJson(String json);

}
