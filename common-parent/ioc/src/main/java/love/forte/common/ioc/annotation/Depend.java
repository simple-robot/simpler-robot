/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Depend.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.ioc.annotation;

import java.lang.annotation.*;

/**
 * 标注在方法参数或字段上，代表自动注入。
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.FIELD}) //接口、类、枚举、注解、方法
@Documented
public @interface Depend {

    /** 使用名称对依赖进行注入，如果为空字符串则使用类型进行注入 */
    String value() default "";

    /** 如果没有名称，且此参数类型不是Object类型，则通过此类型获取。 */
    Class<?> type() default Object.class;

    /**
     * 如果获取不到，使用null代替。默认会抛出异常。
     */
    boolean orNull() default false;

    /**
     * 是否尝试通过setter注入。
     */
    boolean bySetter() default true;

    /**
     * 如果 {@link #bySetter()} 为 true，则此处定义setter的名称。默认为set+字段名。
     * 注意，setter的方法参数应该与 {@link #type()} 类型一致。
     */
    String setterName() default "";




}
