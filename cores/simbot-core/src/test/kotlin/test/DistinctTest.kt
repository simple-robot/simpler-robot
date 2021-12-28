/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package test

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor


fun main() {
    val r = Regex("（.+）")

    val systemClipboard = Toolkit.getDefaultToolkit().systemClipboard
    val transferable = systemClipboard.getContents(null)
    if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        val contents = transferable.getTransferData(DataFlavor.stringFlavor) as String
        val lines = contents.lines().mapNotNull { line ->
            line.trim().let {
                var s = it
                if (s.startsWith('"')) s = s.substring(1)
                if (s.endsWith('"')) s = s.substringBeforeLast('"')
                if (r.containsMatchIn(s)) s = s.replaceFirst(r, "")
                s
            }.trim().takeIf { it.isNotBlank() }
        }.distinct().sorted()

        lines.forEach(::println)
        println("Size: ${lines.size}")

    }


}