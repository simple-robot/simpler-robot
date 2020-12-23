/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-ding
 * File     specialMessageCannotAt.kt
 * Date  2020/8/8 上午12:03
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.ding.messages


/*
    此文件定义那些不可以与at类型相结合的消息类型
    link
    整体跳转ActionCard
    独立跳转ActionCard
    FeedCard
 */


/**
 * link类型的消息
 * 似乎不可以与at类型共存
 * ```
{
"msgtype": "link",
"link": {
"text": "这个即将发布的新版本，创始人xx称它为红树林。而在此之前，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是红树林",
"title": "时代的火车向前开",
"picUrl": "",
"messageUrl": "https://www.dingtalk.com/s?__biz=MzA4NjMwMTA2Ng==&mid=2650316842&idx=1&sn=60da3ea2b29f1dcc43a7c8e4a7c97a16&scene=2&srcid=09189AnRJEdIiWVaKltFzNTw&from=timeline&isappinstalled=0&key=&ascene=2&uin=&devicetype=android-23&version=26031933&nettype=WIFI"
}
}
 * ```
 *
 *
 */
data class DingLink
@JvmOverloads
constructor(
        val title: String,
        val text: String,
        val messageUrl: String,
        val picUrl: String? = null
): BaseNormalDingSpecialMessage<DingLink>("link") {
    /**
     * 可获取的参数：
     * - title  消息标题
     * - text   消息内容。如果太长只会部分展示
     * - messageUrl 点击消息跳转的URL
     * - picUrl 图片URL
     */
    override fun get(key: String): Any? {
        return when(key) {
            "title" -> title
            "text" -> text
            "messageUrl" -> messageUrl
            "picUrl" -> picUrl
            else -> null
        }
    }

    /**
     * 与同类型消息进行合并
     */
    override fun doPlus(other: DingLink): DingLink {
        // 合并标题与正文，链接则优先使用此类链接
        val mergeTitle = this.title + other.title
        val mergeText = this.text + "\n" + other.text
        val mergeMessageUrl = this.messageUrl
        val mergePicUrl = this.picUrl ?: other.picUrl
        return DingLink(mergeTitle, mergeText, mergeMessageUrl, mergePicUrl)
    }

    /**
     * 排序
     * 根据title排序
     */
    override fun compareTo(other: DingSpecialMessage): Int {
        return if(other is DingLink){
            other.title.compareTo(other.title, true)
        }else this compareWith other
    }

}


/**
 * 跳转类型 action card，似乎不能支持at类型
 * 分为两种，一个是整体跳转，一个是独立跳转，区别就在于有没有跳转选项链接数组
 * title、text、btnOrientation属性是共有的
 *  - title 首屏会话透出的展示内容
 *  - text markdown格式的消息
 *  - btnOrientation 0-按钮竖直排列，1-按钮横向排列
 *
 * 两个子类的[DingActionCard.plus]方法，本质上最终都会返回[DingAutonomyActionCard]
 * 且应当尽量避免出现多个[DingActionCard]而触发合并。
 *
 * @see DingWholeActionCard
 * @see DingAutonomyActionCard
 */
sealed class DingActionCard : BaseNormalDingSpecialMessage<DingActionCard>("actionCard") {
    abstract val title: String
    abstract val text: String
    abstract val btnOrientation: String
    override fun get(key: String): Any? {
        return when (key) {
            "title" -> title
            "text" -> text
            "btnOrientation" -> btnOrientation
            else -> getValue(key)
        }
    }

    /**
     * 子类的其他类型获取方法
     */
    protected abstract fun getValue(key: String): Any?


    /**
     * 根据title排序
     */
    override fun compareTo(other: DingSpecialMessage): Int {
        return if (other is DingActionCard) {
            this.title.compareTo(other.title)
        } else this compareWith other
    }

}

/**
 * 整体跳转ActionCard
 *
 */
public data class DingWholeActionCard(
        override val title: String, override val text: String, override val btnOrientation: String,
        val singleTitle: String, val singleURL: String
) : DingActionCard() {
    @Suppress("unused")
    constructor(title: String, text: String, btnOrientation: String, btn: DingAutonomyActionCardButtons):
            this(title, text, btnOrientation, btn.title, btn.actionURL)


    override fun getValue(key: String): Any? {
        return when (key) {
            "singleTitle" -> singleTitle
            "singleURL" -> singleURL
            else -> null
        }
    }

    /**
     * 进行合并
     */
    override fun doPlus(other: DingActionCard): DingActionCard {
        val mergeTitle = this.title + other.title
        val mergeText = this.text + "\n" + other.text
        val mergeBtnOrientation = this.btnOrientation
        val btns = when (other) {
            is DingWholeActionCard -> {
                // 相同类型，合并为独立跳转
                arrayOf(
                        DingAutonomyActionCardButtons(this.singleTitle, this.singleURL),
                        DingAutonomyActionCardButtons(other.singleTitle, other.singleURL)
                )
            }
            is DingAutonomyActionCard -> {
                other.btns.plusElement(DingAutonomyActionCardButtons(this.singleTitle, this.singleURL))
            }
        }
        return DingAutonomyActionCard(mergeTitle, mergeText, mergeBtnOrientation, btns)
    }

}

/**
 * 独立跳转
 */
data class DingAutonomyActionCard(
        override val title: String, override val text: String, override val btnOrientation: String,
        val btns: Array<DingAutonomyActionCardButtons>
) : DingActionCard() {
    override fun getValue(key: String): Any? {
        return if (key == "btns") btns else null
    }

    /**
     * 合并
     */
    override fun doPlus(other: DingActionCard): DingActionCard {
        val mergeTitle = this.title + other.title
        val mergeText = this.text + "\n" + other.text
        val mergeBtnOrientation = this.btnOrientation
        val btns = when (other) {
            is DingWholeActionCard -> {
                this.btns.plusElement(DingAutonomyActionCardButtons(other.singleTitle, other.singleURL))
            }
            is DingAutonomyActionCard -> {
                this.btns + other.btns
            }
        }
        return DingAutonomyActionCard(mergeTitle, mergeText, mergeBtnOrientation, btns)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DingAutonomyActionCard

        if (title != other.title) return false
        if (text != other.text) return false
        if (btnOrientation != other.btnOrientation) return false
        if (!btns.contentEquals(other.btns)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + btnOrientation.hashCode()
        result = 31 * result + btns.contentHashCode()
        return result
    }
}


/**
 * [DingAutonomyActionCard]按钮列表
 */
data class DingAutonomyActionCardButtons(val title: String, val actionURL: String)


/**
 * 钉钉的FeedCard类型消息
 *
 * feed card只有一个links类型参数，内容为多个链接
 * 但是对于使用CQ码解析可能就会困难一些
 *
 */
data class DingFeedCard(val links: Array<DingFeedCardLink>): BaseNormalDingSpecialMessage<DingFeedCard>("feedCard") {
    /**
     * 只有`links`可以被获取到
     */
    override fun get(key: String): Any? {
        return if(key == "links") links else null
    }

    override fun compareTo(other: DingSpecialMessage): Int {
        return if (other is DingFeedCard) {
            this.links.size.compareTo(other.links.size)
        } else this compareWith other
    }

    /**
     * 合并同类型，即合并links数组并去重
     */
    override fun doPlus(other: DingFeedCard): DingFeedCard {
        val plusLinks = (this.links + other.links).distinct()
        return DingFeedCard(plusLinks.toTypedArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DingFeedCard

        if (!links.contentEquals(other.links)) return false

        return true
    }

    override fun hashCode(): Int {
        return links.contentHashCode()
    }
}


/**
 * [DingFeedCard]中包含的link链接
 */
data class DingFeedCardLink(val title: String, val messageURL: String, val picURL: String)