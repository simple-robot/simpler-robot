/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Filter.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.annotation;

import java.lang.annotation.*;

/**
 *
 * 注解过滤器，通过一些简单的消息匹配规则以实现对监听消息进行过滤。
 *
 * <p> 这种可复数的注解暂时不支持注解继承。如要继承，请尝试直接继承其对应上级注解 {@link Filters} </p>
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Repeatable(Filters.class)
public @interface Filter {
}
