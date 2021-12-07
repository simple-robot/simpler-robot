/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.Filters;

/**
 * @author ForteScarlet
 */
public class FilterTest4J {

    @Filter("Foo1")
    @Filter(value = "Foo2", and = @Filters(value = {
            @Filter("Foo3"),
            @Filter("Foo4")
    }))
    public void listen() {
    }

}
