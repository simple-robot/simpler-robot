package love.forte.simbot.definition

/**
 * 对 [AccountInfo] 的容器。
 * @author ForteScarlet
 */
public interface AccountInfoContainer : Container {

    /**
     * 能够得到一个 [AccountInfo] 实例。
     */
    public val accountInfo: AccountInfo
}