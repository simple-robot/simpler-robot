package love.forte.test

import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class OkioTest {

    @Test
    fun io() {
        val p = "C:\\Users\\Administrator\\Desktop\\新建文本文档.txt"

        val path = p.toPath()

        println(path.segments)


    }

}