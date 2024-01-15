/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.test.*


/**
 *
 * @author ForteScarlet
 */
class CoroutineContextMergeTests {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun contextMergeWithoutJobTest() {
        val name1 = CoroutineName("name1")
        val name2 = CoroutineName("name2")

        with(name1.mergeWith(name2)) {
            assertEquals(name1.name, get(CoroutineName)?.name)
        }

        with(name1.mergeWith(name2 + Dispatchers.Unconfined)) {
            assertEquals(name1.name, get(CoroutineName)?.name)
            assertEquals(Dispatchers.Unconfined, get(CoroutineDispatcher))
        }
    }

    @Test
    fun contextMergeWithMainJobTest() {
        val job = Job()
        with((CoroutineName("name1") + job).mergeWith(CoroutineName("name2"))) {
            val subJob = get(Job)
            assertNotNull(subJob)
            assertNotEquals(job, subJob)

            assertEquals(1, job.children.toList().size)
            assertFalse(subJob.isCompleted)
            job.cancel()
            assertTrue(subJob.isCompleted)
        }
    }


    @Test
    fun contextMergeWithParentJobTest() {
        val job = Job()
        with(CoroutineName("name1").mergeWith(CoroutineName("name2") + job)) {
            val subJob = get(Job)
            assertNotNull(subJob)
            assertNotEquals(job, subJob)

            assertEquals(1, job.children.toList().size)
            assertFalse(subJob.isCompleted)
            job.cancel()
            assertTrue(subJob.isCompleted)
        }
    }

    @Test
    fun contextMergeWithAllJobTest1() {
        val job1 = Job()
        val job2 = Job()
        with(job1.mergeWith(job2)) {
            val subJob = get(Job)
            assertNotNull(subJob)
            assertNotEquals(job1, subJob)
            assertNotEquals(job2, subJob)

            assertEquals(1, job1.children.toList().size)
            assertEquals(0, job2.children.toList().size)

            assertFalse(subJob.isCompleted)
            job2.cancel()
            assertTrue(subJob.isCompleted)
        }
    }
}
