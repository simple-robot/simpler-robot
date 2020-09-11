/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-core
 * File     FilterParameterMatcher.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 */

package love.forte.common.api.listener;

import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * Filter动态参数匹配器
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface FilterParameterMatcher {

    /**
     * 获取原始字符串
     * @return 原始字符串
     */
    String getOriginal();

    /**
     * 获取用于匹配的正则
     * @return 匹配正则
     */
    Pattern getPattern();

    /**
     * 检测文本是否符合正则。find()匹配
     * @param text 文本
     * @return 是否符合
     */
    default boolean test(String text){
        return getPattern().asPredicate().test(text);
    }

    /**
     * 检测文本是否符合正则。matches()匹配
     * @param text 文本
     * @return 是否符合
     */
    default boolean matches(String text){
        return getPattern().matcher(text).matches();
    }

    /**
     * 从一段匹配的文本中提取出需要的参数。
     * 此文本需要符合正则表达式。
     * @param text 匹配的文本
     * @return 得到的参数
     */
    Map<String, String> getParams(String text);

}
