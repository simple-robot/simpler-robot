package love.forte.test.lis

import cn.hutool.core.convert.ConverterRegistry
import love.forte.common.utils.convert.HutoolConverterManagerImpl


object TestPrivateMsg


fun main() {

    val c = "love.forte.test.lis.TestPrivateMsg"

    val converterRegistry = ConverterRegistry.getInstance()

    val convert = converterRegistry.convert<Class<*>>(Class::class.java, c)

    println(convert)

    // converterRegistry.putCustom(Class::class.java) { v, dv ->
    //
    // }

    val manager = HutoolConverterManagerImpl(converterRegistry)


    val cla: Class<*> = manager.convert(Class::class.java, c, Any::class.java)

    println(cla)


}