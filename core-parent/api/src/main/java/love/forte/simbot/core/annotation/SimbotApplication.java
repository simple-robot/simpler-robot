/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     SimbotApplication.java
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
 * simbot启动类标记，用于在执行 run 的时候填入参数。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.TYPE}) //接口、类、枚举、注解、方法
@Documented
public @interface SimbotApplication {

    /**
     * 配置文件路径。
     */
    SimbotResource[] value() default {
        // 默认配置文件名称为 simbot.properties
        @SimbotResource(value = "simbot.properties", orIgnore = true),
        @SimbotResource(value = "simbot.yml", orIgnore = true)
    };

}



