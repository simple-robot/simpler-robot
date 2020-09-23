/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Filters.java
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
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.TYPE}) //接口、类、枚举、注解、方法
@Documented
public @interface Filters {

    /**
     * 过滤注解。可以有多个。
     */
    Filter[] value();

    /**
     * 当bot被at的时候才会触发
     */
    boolean atBot() default false;

    /**
     * 有人被at了才会触发。其中可能不包括bot自身。
     */
    boolean anyAt() default false;

    /**
     * 当下列账号中的人被at了才会触发
     */
    String[] at() default {};




}
