/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

import love.forte.simbot.component.ComponentFactoryProvider;
import love.forte.simbot.component.qguild.QQGuildComponentFactoryConfigurerProvider;
import love.forte.simbot.component.qguild.QQGuildComponentFactoryProvider;
import love.forte.simbot.component.qguild.bot.QQGuildBotManagerFactoryConfigurerProvider;
import love.forte.simbot.component.qguild.bot.QQGuildBotManagerProvider;
import love.forte.simbot.plugin.PluginFactoryProvider;

module simbot.component.qqguild.core {
    requires kotlin.stdlib;
    requires transitive simbot.api;
    requires transitive simbot.component.qqguild.stdlib;
    requires transitive simbot.component.qqguild.api;

    requires kotlinx.datetime;
    requires io.ktor.client.core;
    requires io.ktor.utils;

    // exports

    exports love.forte.simbot.component.qguild;
    exports love.forte.simbot.component.qguild.bot;
    exports love.forte.simbot.component.qguild.bot.config;
    exports love.forte.simbot.component.qguild.channel;
    exports love.forte.simbot.component.qguild.event;
    exports love.forte.simbot.component.qguild.forum;
    exports love.forte.simbot.component.qguild.guild;
    exports love.forte.simbot.component.qguild.friend;
    exports love.forte.simbot.component.qguild.group;
    exports love.forte.simbot.component.qguild.message;
    exports love.forte.simbot.component.qguild.role;
    exports love.forte.simbot.component.qguild.utils;

    // SPI
    provides ComponentFactoryProvider with QQGuildComponentFactoryProvider;
    uses QQGuildComponentFactoryConfigurerProvider;
    provides PluginFactoryProvider with QQGuildBotManagerProvider;
    uses QQGuildBotManagerFactoryConfigurerProvider;
}
