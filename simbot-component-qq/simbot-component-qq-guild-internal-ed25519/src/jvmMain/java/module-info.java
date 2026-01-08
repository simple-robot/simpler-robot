module simbot.component.qqguild.internal.ed25519s {
    requires kotlin.stdlib;
    requires static org.bouncycastle.provider;
    requires simbot.logger;
    requires net.i2p.crypto.eddsa;

    exports love.forte.simbot.qguild.ed25519 to
            simbot.component.qqguild.stdlib,
            simbot.component.qqguild.core;
    exports love.forte.simbot.qguild.ed25519.annotations;
}
