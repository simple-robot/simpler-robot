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

package love.forte.simbot.common

import kotlinx.coroutines.Job
import love.forte.simbot.common.coroutines.linkTo
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JobLinkTests {

    @Test
    fun childJobLinkToParentAndParentCancelTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob) }
        parentJob.cancel()
        assertFalse(childJob.isActive)
    }
    @Test
    fun childJobLinkToParentAndParentCancelButChildUnlinkedTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob).apply { dispose() } }
        parentJob.cancel()
        assertTrue(childJob.isActive)
    }

    @Test
    fun childJobLinkToParentAndChildCancelTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob) }
        childJob.cancel()
        assertTrue(parentJob.isActive)
    }

    @Test
    fun childJobLinkToParentAndParentCompleteTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob) }
        parentJob.complete()
        assertFalse(childJob.isActive)
    }
    @Test
    fun childJobLinkToParentAndParentCompleteButChildUnlinkedTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob).apply { dispose() } }
        parentJob.complete()
        assertTrue(childJob.isActive)
    }

    @Test
    fun childJobLinkToParentAndChildCompleteTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob) }
        childJob.complete()
        assertTrue(parentJob.isActive)
    }
}
