/*
 *     Copyright (c) 2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.component.qgguild.test;

import love.forte.simbot.ability.DeleteOption;
import love.forte.simbot.common.id.StringID;
import love.forte.simbot.component.qguild.group.QGGroupJoinApprovalStrategyCreated;
import love.forte.simbot.component.qguild.group.QGGroupJoinApprovalStrategyHandle;
import love.forte.simbot.component.qguild.group.QGGroupJoinApprovalStrategyManager;
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyCreate;
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyModify;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public final class QGGroupJoinApprovalStrategyJavaCalls {
    public static QGGroupJoinApprovalStrategyCreated exercise(QGGroupJoinApprovalStrategyManager manager) throws Exception {
        QGGroupJoinApprovalStrategyCreated created = (QGGroupJoinApprovalStrategyCreated)
            QGGroupJoinApprovalStrategyManager.class.getMethod("createBlocking", GroupJoinApprovalStrategyCreate.class)
                .invoke(manager, GroupJoinApprovalStrategyCreate.builder().addGroupId("00123").build());
        Class<QGGroupJoinApprovalStrategyHandle> handleType = QGGroupJoinApprovalStrategyHandle.class;
        ((CompletableFuture<?>) handleType.getMethod("updateAsync", GroupJoinApprovalStrategyModify.class)
            .invoke(created, GroupJoinApprovalStrategyModify.builder().isEnable(false).build())).get();
        handleType.getMethod("addWhitelistUsersBlocking", Collection.class).invoke(created, Collections.singletonList(StringID.valueOf("00123")));
        handleType.getMethod("removeWhitelistUsersBlocking", Collection.class).invoke(created, Collections.singletonList(StringID.valueOf("00123")));
        handleType.getMethod("executeBlocking").invoke(created);
        handleType.getMethod("executeReserve").invoke(created);
        handleType.getMethod("deleteBlocking", DeleteOption[].class).invoke(created, (Object) new DeleteOption[0]);
        manager.list();
        return created;
    }
}
