package love.forte.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import love.forte.simbot.api.utils.concurrentCollection
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class CollectionUtil {


    @Test
    fun collectionTest() {
        TODO("Suspend!")
        println(Dispatchers.Default)
        val scope = CoroutineScope(Dispatchers.Default)
        val list = concurrentCollection<Int>()
        val job = scope.launch {
           repeat(100) {
               println(it)
               launch {
                   list.add(it)
               }
               println(it)
           }

        }

        job.start()
        job.invokeOnCompletion {
            println(list.size)
        }

        while (job.isActive){
        }


    }

}