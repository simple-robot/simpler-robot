module simbot.component.onebot11v.event {
    requires kotlin.stdlib;
    requires simbot.api;
    requires simbot.component.onebot.common;
    requires static simbot.common.annotations;
    requires transitive simbot.component.onebot11v.common;
    requires transitive simbot.component.onebot11v.message;
    requires transitive kotlinx.coroutines.core;

    exports love.forte.simbot.component.onebot.v11.event;
    exports love.forte.simbot.component.onebot.v11.event.request;
    exports love.forte.simbot.component.onebot.v11.event.notice;
    exports love.forte.simbot.component.onebot.v11.event.meta;
    exports love.forte.simbot.component.onebot.v11.event.message;
}
