/*
 *     Copyright (c) 2024-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

import love.forte.simbot.component.ComponentFactoryProvider;
import love.forte.simbot.component.kook.KookComponentFactoryProvider;
import love.forte.simbot.component.kook.bot.KookBotManagerFactoryConfigurerProvider;
import love.forte.simbot.component.kook.bot.KookBotManagerFactoryProvider;
import love.forte.simbot.plugin.PluginFactoryProvider;

module simbot.component.kook.core {
    requires kotlin.stdlib;
    requires transitive simbot.component.kook.stdlib;
    requires static simbot.api;
    requires static simbot.common.annotations;
    requires io.ktor.client.websockets;

    exports love.forte.simbot.component.kook;
    exports love.forte.simbot.component.kook.bot;
    exports love.forte.simbot.component.kook.event;
    exports love.forte.simbot.component.kook.message;
    exports love.forte.simbot.component.kook.role;
    exports love.forte.simbot.component.kook.blacklist;
    exports love.forte.simbot.component.kook.util;

    // provider
    provides ComponentFactoryProvider with KookComponentFactoryProvider;
    provides PluginFactoryProvider with KookBotManagerFactoryProvider;

    uses KookBotManagerFactoryConfigurerProvider;
}
