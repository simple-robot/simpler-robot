/*
 *     Copyright (c) 2024. ForteScarlet.
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

module simbot.api {
    requires kotlin.stdlib;
    requires simbot.logger;
    requires static org.jetbrains.annotations;
    requires static simbot.common.annotations;
    requires simbot.common.streamable;
    requires simbot.common.suspendrunner;
    requires simbot.common.core;
    requires simbot.common.collection;
    requires kotlinx.coroutines.core;
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;
    requires kotlinx.io.core;
    requires static kotlinx.coroutines.reactive;
    requires static kotlinx.coroutines.reactor;
    requires static kotlinx.coroutines.rx2;
    requires static kotlinx.coroutines.rx3;
    requires static reactor.core;
    requires static io.reactivex.rxjava2;
    requires static io.reactivex.rxjava3;
    requires static org.reactivestreams;

    // libs.suspend.reversal.annotations?
    exports love.forte.simbot;
    exports love.forte.simbot.ability;
    exports love.forte.simbot.application;
    exports love.forte.simbot.bot;
    exports love.forte.simbot.bot.configuration;
    exports love.forte.simbot.component;
    exports love.forte.simbot.definition;
    exports love.forte.simbot.event;
    exports love.forte.simbot.message;
    exports love.forte.simbot.plugin;
    exports love.forte.simbot.resource;

    uses love.forte.simbot.component.ComponentFactoryProvider;
    uses love.forte.simbot.component.ComponentFactoryConfigurerProvider;
    uses love.forte.simbot.plugin.PluginFactoryProvider;
    uses love.forte.simbot.plugin.PluginFactoryConfigurerProvider;

}
