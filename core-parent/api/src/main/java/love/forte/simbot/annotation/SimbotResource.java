/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     SimbotResource.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.annotation;

import java.lang.annotation.Documented;

/**
 *
 * 用于 {@link SimbotApplication#value()} 中，标记所需的配置文件列表。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Documented
public @interface SimbotResource {

    /**
     * 配置文件路径。
     */
    String value();

    /**
     * 资源文件的类型，用于进行对应的解析，例如”properties“ 或 ”yml“。
     * 如果为空则默认使用 {@link #value()} 值的文件扩展名作为类型。
     * 如果无法截取到扩展名，则可能会使用所有的解析方法进行尝试，也有可能会直接抛出异常。
     */
    String type() default "";

    /**
     * 如果资源获取不到，则忽略。
     * 如果为false，则会抛出异常。
     */
    boolean orIgnore() default false;

    /**
     * 如果此参数不为空，
     * 则当 <b>启动参数</b> 中存在command中的内容的时候此resource才会生效。
     */
    String[] command() default {};

}
