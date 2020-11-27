/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.component.lovelycat.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.component.lovelycat.message.event.*
import love.forte.simbot.core.configuration.ComponentBeans


/**
 *
 * @author ForteScarlet
 */
@ConfigBeans
public class LovelyCatParserConfiguration {


    @ComponentBeans("lovelyCatParser")
    fun lovelyCatParser(): LovelyCatParser {
        val defParser = DefaultLovelyCatParser()

        defParser.registerParser(LOGIN_EVENT, LovelyCatLoginEventParser)
        defParser.registerParser(GROUP_MSG_EVENT, LovelyCatGroupMsgEventParser)
        defParser.registerParser(PRIVATE_MSG_EVENT, LovelyCatPrivateMsgEventParser)
        defParser.registerParser(RECEIVED_TRANSFER_EVENT, LovelyCatReceivedTransferEventParser)
        defParser.registerParser(SCAN_CASH_MONEY_EVENT, LovelyCatScanCashMoneyEventParser)
        defParser.registerParser(FRIEND_VERIFY_EVENT, LovelyCatFriendVerifyEventParser)
        defParser.registerParser(CONTACTS_CHANGES_EVENT, LovelyCatContactsChangeEventParser)
        defParser.registerParser(GROUP_MEMBER_ADD_EVENT, LovelyCatGroupMemberAddEventParser)


        return defParser
    }

}