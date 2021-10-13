@file:Suppress("unused")
@file:JvmName("NekoUtil")

package love.forte.simbot.kaiheila.utils

import catcode.CatCodeUtil
import catcode.CatEncoder
import catcode.Neko
import catcode.cTo


@Suppress("FunctionName")
public fun TextNeko(text: String): Neko = CatCodeUtil.toNeko("text", "text" cTo CatEncoder.encodeParams(text))

