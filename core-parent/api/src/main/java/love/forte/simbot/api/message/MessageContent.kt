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

@file:JvmName("MessageContents")
@file:JvmMultifileClass
package love.forte.simbot.api.message

import love.forte.catcode.Neko

/*
 *
 * 定义消息正文接口及部分实现类
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */


/*
 * 旧注释存档：
 *
 * 这时候就有人会问了：“为什么不提供一个方法来获取一个 [MessageContent] 中的所有类型的消息链呢？”
 * 问得好。因为simbot几乎没有绝对定义的消息类型。at、image、text这些还好理解，因为它们很常见。
 * 但是除了这种十分常见的消息类型以外，我不能保证所有的组件中出现的消息类型都在我的预期之内。
 * 因此我只会陆续提供部分较为特殊的消息类型，例如 image 类型。
 * 而其他类型的消息，或许会陆续进行支持，也或许会仅仅提供一个 `type` 参数进行区分。
 *
 * 至于其他一切可能出现的类型，[msg] 与 [CatCodeUtil.split] 可以满足绝大部分的可能性与需求。
 *
 * 如果你不关心特殊消息的具体内容，而只需要消息中的纯文本部分的内容，那么你可以考虑使用 [MsgGet.text]。
 *
 * 如果你只需要一些目前可以提供的特殊消息内容，例如 **图片** 内容，那么你可以使用 [cats]。
 *
 * 如果你想要自行解析任何 **可能** 出现的消息内容，例如可能会出现的 **face(表情)**、**nudge(头像戳一戳)**、**share(分享)** 等，
 * 那么你需要考虑使用 [MessageGet.msg] 或者 [MessageContent.msg] 来根据其中的CAT码自行进行解析。
 *
 * 但是需要注意的是，有些情况下，[MessageGet.msg] 相比较于 [MsgGet.text] 的效率会更加低下，而有些时候则恰恰相反，
 * 这取决于组件的实现细节，还需要仔细参阅对应组件的文档说明来进行抉择。
 */


/**
 * **消息内容**。
 *
 * 从 [MessageGet.msgContent] 获取到的消息内容有可能是一个 **复合消息**，即一个 msgContent 中存在多个不同类型的消息，
 * 也有可能只是一个 **独立消息**，即其本身就是全部的消息内容。
 *
 * 它被使用在[MessageGet] 接口的 [MessageGet.msgContent] 上，表示当前消息的正文内容。
 *
 * 一个 [MessageContent] 实例至少应该保证能够得到当前消息的 [消息字符串文本][msg]。
 *
 * 对于例如 [msg]、[cats]等内容的获取，有些组件可能会需要使用懒加载来提高效率。
 * 在实现懒加载的时候不需要考虑线程安全，对于线程安全的问题应当由使用者自行考虑。
 *
 * [MessageContent] 的具体实现中可能存在任何形式的数据格式，
 * 例如用于http请求的参数、接收到的某种json字符串或者某个组件的消息链实例，而使用者一般不需要考虑其具体实现内容。
 *
 * [MessageContent.cats] 中的 `text` 文本消息也会被作为 `cat` 码进行表现，其type为 `text`。
 *
 * [MessageContent.cats] 、[MessageContent.msg] 与 [MsgGet.text] 本质上都是对消息内容的一种展现形式，
 * [MessageContent.cats] 主要用于应对一串消息中出现的所有可能的消息类型，来使得使用者可以按需解析。
 * [MessageContent.msg] 是上述的 `cats` 的一种字符串表现，一般可用于对消息链序列化并保存。
 * [MsgGet.text] 是所有事件类型都可以获取的一种属性，其表示这个事件中可能存在的 "文本消息" 内容，而不会包含任何 **特殊** 消息，
 * 其主要用于对消息的过滤、关键字的匹配或者应对一些不需要解析特殊消息码的场景。
 *
 *
 * 不同的组件对于 [MessageContent] 和 [MsgGet.text] 的实现都会是不同的，
 * 一般情况下我会将他们的大致解析原理注明在文档或者注释中，使用者需要根据具体需求和实现来判断使用哪种方式会更加高效。
 *
 *
 *
 *
 */
public interface MessageContent {
    /**
     * 消息字符串文本。
     *
     * 一般来讲，[msg] 就相当于将 [cats] 中的内容全部toString并拼接在了一起，但是text文本不再表现为cat码。
     */
    val msg: String



    /**
     * 获取此消息中的所有可能包含的cat码。
     *
     * 此处所获得的cat码指的是 **所有** 消息链中元素的cat码，
     * 也就是说一段普通的 **文本消息** 也会被作为cat码进行处理，其类型为 `message`, 参数只有一个 `text`，代表其正文信息。
     *
     * 对于组件实现，一般需要耗时获取的属性可通过 `lazy cat` 来进行实现。
     *
     */
    val cats: List<Neko>

    /**
     * 获取指定过滤类型的 [cats] 列表。
     */
    @JvmDefault
    fun getCats(vararg types: String) = cats.filter { it.type in types }

}







