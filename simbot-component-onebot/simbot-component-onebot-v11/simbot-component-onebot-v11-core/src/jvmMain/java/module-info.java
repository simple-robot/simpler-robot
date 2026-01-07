/*
 *     Copyright (c) 2026. ForteScarlet.
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
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotManagerFactoryConfigurerProvider;
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotManagerFactoryProvider;
import love.forte.simbot.component.onebot.v11.core.component.OneBot11ComponentFactoryConfigurerProvider;
import love.forte.simbot.component.onebot.v11.core.component.OneBot11ComponentFactoryProvider;
import love.forte.simbot.plugin.PluginFactoryProvider;

module simbot.component.onebot11v.core {
    requires kotlin.stdlib;
    requires simbot.logger;
    requires simbot.common.suspendrunner;
    requires simbot.component.onebot.common;
    requires simbot.api;
    requires simbot.common.core;
    requires static simbot.common.annotations;
    requires transitive simbot.component.onebot11v.common;
    requires transitive simbot.component.onebot11v.message;
    requires transitive simbot.component.onebot11v.event;

    requires transitive kotlinx.coroutines.core;
    requires transitive kotlinx.serialization.core;
    requires transitive kotlinx.serialization.json;

    requires io.ktor.client.content.negotiation;
    requires io.ktor.client.core;
    requires io.ktor.client.websockets;
    requires io.ktor.http;
    requires io.ktor.io;

    exports love.forte.simbot.component.onebot.v11.core;
    exports love.forte.simbot.component.onebot.v11.core.actor;
    exports love.forte.simbot.component.onebot.v11.core.api;
    exports love.forte.simbot.component.onebot.v11.core.api.nonstandard;
    exports love.forte.simbot.component.onebot.v11.core.bot;
    exports love.forte.simbot.component.onebot.v11.core.component;
    exports love.forte.simbot.component.onebot.v11.core.event;
    exports love.forte.simbot.component.onebot.v11.core.event.message;
    exports love.forte.simbot.component.onebot.v11.core.event.meta;
    exports love.forte.simbot.component.onebot.v11.core.event.notice;
    exports love.forte.simbot.component.onebot.v11.core.event.request;
    exports love.forte.simbot.component.onebot.v11.core.event.stage;
    exports love.forte.simbot.component.onebot.v11.core.utils;
    exports love.forte.simbot.component.onebot.v11.core.message;

    provides ComponentFactoryProvider with OneBot11ComponentFactoryProvider;
    uses OneBot11ComponentFactoryConfigurerProvider;

    provides PluginFactoryProvider with OneBotBotManagerFactoryProvider;
    uses OneBotBotManagerFactoryConfigurerProvider;
}
