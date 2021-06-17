/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     FilterParameters.java
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.filter;

/**
 * 过滤器动态参数获取器
 * @author ForteScarlet
 */
public interface FilterParameters {

    /**
     * 根据指定参数名称获取对应的提取参数。
     * @param key Key
     * @return Value or null.
     */
    String get(String key);

}
