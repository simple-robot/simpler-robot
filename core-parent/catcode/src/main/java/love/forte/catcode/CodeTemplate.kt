/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CodeTemplate.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */


@file:Suppress("unused")

package love.forte.catcode

import love.forte.catcode.codes.Nyanko

/**
 * 定义特殊码的一些模板方法，例如at等。
 * 返回值类型全部相同
 * 主要基于猫猫码规范定义。
 */
interface CodeTemplate<T> {
    /**
     * at别人
     * @see at
     */
    @JvmDefault fun at(code: Long): T = at(code.toString())

    /**
     * at别人
     */
    fun at(code: String): T

    /**
     * at所有人
     */
    fun atAll(): T

    /**
     * face
     */
    fun face(id: String): T
    @JvmDefault fun face(id: Long): T = face(id.toString())

    /**
     * big face
     */
    fun bface(id: String): T
    @JvmDefault fun bface(id: Long): T = bface(id.toString())

    /**
     * small face
     */
    fun sface(id: String): T
    @JvmDefault fun sface(id: Long): T = sface(id.toString())

    /**
     * image
     * @param file id
     * @param destruct 闪图
     */
    fun image(file: String, destruct: Boolean): T
    @JvmDefault fun image(id: String): T = image(id, false)


    /**
     * 语言
     * [CAT:record,[file]={1},[magic]={2}] - 发送语音
     * {1}为音频文件名称，音频存放在酷Q目录的data\record\下
     * {2}为是否为变声，若该参数为true则显示变声标记。该参数可被忽略。
     * 举例：[CAT:record,file=1.silk，magic=true]（发送data\record\1.silk，并标记为变声）
     */
    fun record(file: String, magic: Boolean): T
    @JvmDefault fun record(id: String): T = record(id, false)



    /**
     * rps 猜拳
     * [CAT:rps,[type]={1}] - 发送猜拳魔法表情
     * {1}为猜拳结果的类型，暂不支持发送时自定义。该参数可被忽略。
     * 1 - 猜拳结果为石头
     * 2 - 猜拳结果为剪刀
     * 3 - 猜拳结果为布
     */
    fun rps(type: String): T
    fun rps(): T
    @JvmDefault fun rps(type: Int) = rps(type.toString())


    /**
     * 骰子
     * [CAT:dice,type={1}] - 发送掷骰子魔法表情
     * {1}对应掷出的点数，暂不支持发送时自定义。该参数可被忽略。
     */
    fun dice(): T
    fun dice(type: String): T
    @JvmDefault fun dice(type: Int) = dice(type.toString())

    /**
     * 戳一戳（原窗口抖动，仅支持好友消息使用）
     */
    fun shake(): T

    /**
     * 音乐
     * [CAT:music,type={1},id={2},style={3}]
     * {1} 音乐平台类型，目前支持qq、163
     * {2} 对应音乐平台的数字音乐id
     * {3} 音乐卡片的风格。仅 Pro 支持该参数，该参数可被忽略。
     * 注意：音乐只能作为单独的一条消息发送
     * 例子
     * [CAT:music,type=qq,id=422594]（发送一首QQ音乐的“Time after time”歌曲到群内）
     * [CAT:music,type=163,id=28406557]（发送一首网易云音乐的“桜咲く”歌曲到群内）
     */
    fun music(type: String, id: String, style: String?): T
    @JvmDefault fun music(type: String, id: String): T = music(type, id, null)

    /**
     * [CAT:music,type=custom,url={1},audio={2},title={3},content={4},image={5}] - 发送音乐自定义分享
     * 注意：音乐自定义分享只能作为单独的一条消息发送
     * @param url   {1}为分享链接，即点击分享后进入的音乐页面（如歌曲介绍页）。
     * @param audio {2}为音频链接（如mp3链接）。
     * @param title  {3}为音乐的标题，建议12字以内。
     * @param content  {4}为音乐的简介，建议30字以内。该参数可被忽略。
     * @param image  {5}为音乐的封面图片链接。若参数为空或被忽略，则显示默认图片。
     *
     */
    fun customMusic(url: String, audio: String, title: String, content: String?, image: String?): T
    @JvmDefault fun customMusic(url: String, audio: String, title: String): T = customMusic(url, audio, title, null, null)


    /**
     * [CAT:share,url={1},title={2},content={3},image={4}] - 发送链接分享
     * {1}为分享链接。
     * {2}为分享的标题，建议12字以内。
     * {3}为分享的简介，建议30字以内。该参数可被忽略。
     * {4}为分享的图片链接。若参数为空或被忽略，则显示默认图片。
     * 注意：链接分享只能作为单独的一条消息发送
     */
    fun share(url: String, title: String, content: String?, image: String?): T
    @JvmDefault fun share(url: String, title: String): T = share(url, title, null, null)


    /**
     * 地点
     * [CAT:location,lat={1},lon={2},title={3},content={4}]
     * {1} 纬度
     * {2} 经度
     * {3} 分享地点的名称
     * {4} 分享地点的具体地址
     */
    fun location(lat: String, lon: String, title: String, content: String): T


}


//**************************************
//*         String template
//**************************************


/**
 * 基于 [NekoTemplate] 的模板实现, 以`string`作为猫猫码载体。
 * 默认内置于[CatCodeUtil.nekoTemplate]
 */
@Suppress("OverridingDeprecatedMember")
object StringTemplate: CodeTemplate<String> {
    @JvmStatic
    val instance get() = this
    private val utils: CatCodeUtil = CatCodeUtil
    private const val AT_ALL: String = "[CAT:at,qq=all]"
    /**
     * at别人
     */
    override fun at(code: String): String = "[CAT:at,qq=$code]" 

    /**
     * at所有人
     */
    override fun atAll(): String = AT_ALL

    /**
     * face
     */
    override fun face(id: String): String = "[CAT:face,id=$id]" 

    /**
     * big face
     */
    override fun bface(id: String): String = "[CAT:bface,id=$id]" 

    /**
     * small face
     */
    override fun sface(id: String): String = "[CAT:sface,id=$id]" 

    /**
     * image
     * @param file file/url/id
     * @param destruct true=闪图
     */
    override fun image(file: String, destruct: Boolean): String = "[CAT:image,file=$file,destruct=$destruct]"



    /**
     * 语言
     * [CAT:record,file={1},magic={2}] - 发送语音
     * {1}为音频文件名称，音频存放在酷Q目录的data\record\下
     * {2}为是否为变声，若该参数为true则显示变声标记。该参数可被忽略。
     * 举例：[CAT:record,file=1.silk，magic=true]（发送data\record\1.silk，并标记为变声）
     */
    override fun record(file: String, magic: Boolean): String =
        "[CAT:record,file=$file,magic=$magic]"


    /**
     * const val for rps
     */
    private const val RPS = "[CAT:rps]"

    /**
     * rps 猜拳
     * [CAT:rps,type={1}] - 发送猜拳魔法表情
     * {1}为猜拳结果的类型，暂不支持发送时自定义。该参数可被忽略。
     * 1 - 猜拳结果为石头
     * 2 - 猜拳结果为剪刀
     * 3 - 猜拳结果为布
     */
    override fun rps(): String = RPS



    /**
     * rps 猜拳
     * [CAT:rps,type={1}] - 发送猜拳魔法表情
     * {1}为猜拳结果的类型，暂不支持发送时自定义。该参数可被忽略。
     * 1 - 猜拳结果为石头
     * 2 - 猜拳结果为剪刀
     * 3 - 猜拳结果为布
     */
    override fun rps(type: String): String = "[CAT:rps,type=$type]"

    /**
     * const val for dice
     */
    private const val DICE = "[CAT:dice]"

    /**
     * 骰子
     * [CAT:dice,type={1}] - 发送掷骰子魔法表情
     * {1}对应掷出的点数，暂不支持发送时自定义。该参数可被忽略。
     */
    override fun dice(): String = DICE


    /**
     * 骰子
     * [CAT:dice,type={1}] - 发送掷骰子魔法表情
     * {1}对应掷出的点数，暂不支持发送时自定义。该参数可被忽略。
     */
    override fun dice(type: String): String = "[CAT:dice,type=$type]" 


    /**
     * const val for shake
     */
    private const val SHAKE = "[CAT:shake]"

    /**
     * 戳一戳（原窗口抖动，仅支持好友消息使用）
     */
    override fun shake(): String = SHAKE

    /**
     * 音乐
     * [CAT:music,type={1},id={2},style={3}]
     * {1} 音乐平台类型，目前支持qq、163
     * {2} 对应音乐平台的数字音乐id
     * {3} 音乐卡片的风格。仅 Pro 支持该参数，该参数可被忽略。
     * 注意：音乐只能作为单独的一条消息发送
     * 例子
     * [CAT:music,type=qq,id=422594]（发送一首QQ音乐的“Time after time”歌曲到群内）
     * [CAT:music,type=163,id=28406557]（发送一首网易云音乐的“桜咲く”歌曲到群内）
     */
    override fun music(type: String, id: String, style: String?): String {
        return if (style != null) {
            "[CAT:music,type=$type,id=$id,style=$style]" 
        }else{
            "[CAT:music,type=$type,id=$id]" 
        }
    }

    /**
     * [CAT:music,type=custom,url={1},audio={2},title={3},content={4},image={5}] - 发送音乐自定义分享
     * 注意：音乐自定义分享只能作为单独的一条消息发送
     * @param url   {1}为分享链接，即点击分享后进入的音乐页面（如歌曲介绍页）。
     * @param audio {2}为音频链接（如mp3链接）。
     * @param title  {3}为音乐的标题，建议12字以内。
     * @param content  {4}为音乐的简介，建议30字以内。该参数可被忽略。
     * @param image  {5}为音乐的封面图片链接。若参数为空或被忽略，则显示默认图片。
     *
     */
    override fun customMusic(url: String, audio: String, title: String, content: String?, image: String?): String {
        return if(content != null && image != null){
            "[CAT:music,type=custom,url=$url,audio=$audio,title=$title,content=$content,image=$image]"
        }else{
            val list: MutableList<Pair<String, Any>> = mutableListOf("type" to "custom", "url" to url, "audio" to audio, "title" to title)
            content?.run {
                list.add("content" to this)
            }
            image?.run {
                list.add("image" to this)
            }
            utils.toCat("music", pair = list.toTypedArray())
        }
    }

    /**
     * [CAT:share,url={1},title={2},content={3},image={4}] - 发送链接分享
     * {1}为分享链接。
     * {2}为分享的标题，建议12字以内。
     * {3}为分享的简介，建议30字以内。该参数可被忽略。
     * {4}为分享的图片链接。若参数为空或被忽略，则显示默认图片。
     * 注意：链接分享只能作为单独的一条消息发送
     */
    override fun share(url: String, title: String, content: String?, image: String?): String {
        return if(content != null && image != null){
            "[CAT:share,url=$url,title=$title,content=$content,image=$image]"
        }else{
            val list: MutableList<Pair<String, Any>> = mutableListOf("url" to url, "title" to title)
            content?.run {
                list.add("content" to this)
            }
            image?.run {
                list.add("image" to this)
            }
            utils.toCat("share", pair = list.toTypedArray())
        }
    }

    /**
     * 地点
     * [CAT:location,lat={1},lon={2},title={3},content={4}]
     * {1} 纬度
     * {2} 经度
     * {3} 分享地点的名称
     * {4} 分享地点的具体地址
     */
    override fun location(lat: String, lon: String, title: String, content: String): String =
        "[CAT:location,lat=$lat,lon=$lon,title=$title,content=$content]"
}



//**************************************
//*         KQ template
//**************************************



/**
 * 基于 [NekoTemplate] 的模板实现, 以[Neko]作为猫猫码载体。
 * 默认内置于[CatCodeUtil.nekoTemplate]
 */
@Suppress("OverridingDeprecatedMember")
object NekoTemplate: CodeTemplate<Neko> {
    @JvmStatic
    val instance get() = this
    /**
     * at别人
     */
    override fun at(code: String): Neko = Nyanko.byCode(StringTemplate.at(code))

//    /** kq for all */
//    private val AT_ALL: KQCode = AtAll

    /**
     * at所有人
     */
    override fun atAll(): Neko = NekoAtAll

    /**
     * face
     */
    override fun face(id: String): Neko = Nyanko.byCode(StringTemplate.face(id))

    /**
     * big face
     */
    override fun bface(id: String): Neko = Nyanko.byCode(StringTemplate.bface(id))

    /**
     * small face
     */
    override fun sface(id: String): Neko = Nyanko.byCode(StringTemplate.sface(id))

    /**
     * image
     * @param file id
     * @param destruct true=闪图
     */
    override fun image(file: String, destruct: Boolean): Neko =
        Nyanko.byCode(StringTemplate.image(file, destruct))


    /**
     * 语言
     * [CAT:record,file={1},magic={2}] - 发送语音
     * {1}为音频文件名称，音频存放在酷Q目录的data\record\下
     * {2}为是否为变声，若该参数为true则显示变声标记。该参数可被忽略。
     * 举例：[CAT:record,file=1.silk，magic=true]（发送data\record\1.silk，并标记为变声）
     */
    override fun record(file: String, magic: Boolean): Neko =
        Nyanko.byCode(StringTemplate.record(file, magic))

//    /** rps */
//    private val RPS: KQCode = Rps

    /**
     * rps 猜拳
     * [CAT:rps,type={1}] - 发送猜拳魔法表情
     * {1}为猜拳结果的类型，暂不支持发送时自定义。该参数可被忽略。
     * 1 - 猜拳结果为石头
     * 2 - 猜拳结果为剪刀
     * 3 - 猜拳结果为布
     */
    override fun rps(): Neko = NekoRps


    /**
     * rps 猜拳
     * [CAT:rps,type={1}] - 发送猜拳魔法表情
     * {1}为猜拳结果的类型，暂不支持发送时自定义。该参数可被忽略。
     * 1 - 猜拳结果为石头
     * 2 - 猜拳结果为剪刀
     * 3 - 猜拳结果为布
     */
    override fun rps(type: String): Neko =
        Nyanko.byCode(StringTemplate.rps(type))

//    /** dice */
//    private val DICE: KQCode = Dice

    /**
     * 骰子
     * [CAT:dice,type={1}] - 发送掷骰子魔法表情
     * {1}对应掷出的点数，暂不支持发送时自定义。该参数可被忽略。
     */
    override fun dice(): Neko = NekoDice


    /**
     * 骰子
     * [CAT:dice,type={1}] - 发送掷骰子魔法表情
     * {1}对应掷出的点数，暂不支持发送时自定义。该参数可被忽略。
     *
     * @see dice
     *
     */
    override fun dice(type: String): Neko =
        Nyanko.byCode(StringTemplate.dice(type))


//    private val SHAKE: KQCode = Shake

    /**
     * 戳一戳（原窗口抖动，仅支持好友消息使用）
     */
    override fun shake(): Neko = NekoShake


    /**
     * 音乐
     * [CAT:music,type={1},id={2},style={3}]
     * {1} 音乐平台类型，目前支持qq、163
     * {2} 对应音乐平台的数字音乐id
     * {3} 音乐卡片的风格。仅 Pro 支持该参数，该参数可被忽略。
     * 注意：音乐只能作为单独的一条消息发送
     * 例子
     * [CAT:music,type=qq,id=422594]（发送一首QQ音乐的“Time after time”歌曲到群内）
     * [CAT:music,type=163,id=28406557]（发送一首网易云音乐的“桜咲く”歌曲到群内）
     */
    override fun music(type: String, id: String, style: String?): Neko =
        Nyanko.byCode(StringTemplate.music(type, id, style))

    /**
     * [CAT:music,type=custom,url={1},audio={2},title={3},content={4},image={5}] - 发送音乐自定义分享
     * 注意：音乐自定义分享只能作为单独的一条消息发送
     * @param url   {1}为分享链接，即点击分享后进入的音乐页面（如歌曲介绍页）。
     * @param audio {2}为音频链接（如mp3链接）。
     * @param title  {3}为音乐的标题，建议12字以内。
     * @param content  {4}为音乐的简介，建议30字以内。该参数可被忽略。
     * @param image  {5}为音乐的封面图片链接。若参数为空或被忽略，则显示默认图片。
     *
     */
    override fun customMusic(url: String, audio: String, title: String, content: String?, image: String?): Neko =
        Nyanko.byCode(StringTemplate.customMusic(url, audio, title, content, image))

    /**
     * [CAT:share,url={1},title={2},content={3},image={4}] - 发送链接分享
     * {1}为分享链接。
     * {2}为分享的标题，建议12字以内。
     * {3}为分享的简介，建议30字以内。该参数可被忽略。
     * {4}为分享的图片链接。若参数为空或被忽略，则显示默认图片。
     * 注意：链接分享只能作为单独的一条消息发送
     */
    override fun share(url: String, title: String, content: String?, image: String?): Neko =
        Nyanko.byCode(StringTemplate.share(url, title, content, image))

    /**
     * 地点
     * [CAT:location,lat={1},lon={2},title={3},content={4}]
     * {1} 纬度
     * {2} 经度
     * {3} 分享地点的名称
     * {4} 分享地点的具体地址
     */
    override fun location(lat: String, lon: String, title: String, content: String): Neko =
        Nyanko.byCode(StringTemplate.location(lat, lon, title, content))



}

