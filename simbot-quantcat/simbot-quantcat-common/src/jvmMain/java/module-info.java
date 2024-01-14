module simbot.quantcat.common {
    requires kotlin.stdlib;
    requires static simbot.api;
    requires static kotlin.reflect;
    requires static kotlinx.coroutines.core;
    requires static simbot.common.core;
    requires static simbot.common.annotations;
    requires org.slf4j;
    requires simbot.logger;

    exports love.forte.simbot.quantcat.common.annotations;
    exports love.forte.simbot.quantcat.common.filter;
    exports love.forte.simbot.quantcat.common.keyword;
    exports love.forte.simbot.quantcat.common.listener;
    exports love.forte.simbot.quantcat.common.interceptor;
    exports love.forte.simbot.quantcat.common.interceptor.impl;
    exports love.forte.simbot.quantcat.common.binder;
    exports love.forte.simbot.quantcat.common.binder.impl;
}
