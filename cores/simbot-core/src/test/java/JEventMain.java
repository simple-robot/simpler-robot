/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import love.forte.simbot.core.event.CoreListenerUtil;
import love.forte.simbot.event.EventListener;
import love.forte.simbot.event.MessageEvent;

/**
 * @author ForteScarlet
 */
public class JEventMain {
    public static void main(String[] args) {
        final EventListener eventListener = CoreListenerUtil.newCoreListener(MessageEvent.class, (c, e) -> {
            return 1;
        });


    }
}
