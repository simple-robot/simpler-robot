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

import love.forte.simbot.common.id.ID;
import love.forte.simbot.component.qguild.group.QGGroup;
import love.forte.simbot.component.qguild.group.QGGroupRelation;
import love.forte.simbot.component.qguild.group.QGGroupMemberWithInfo;
import java.util.Collections;

public final class QGGroupMemberManagementJavaCalls {
    public static QGGroupMemberWithInfo exercise(QGGroup group, QGGroupRelation relation, ID memberId) {
        group.getMember(memberId);
        group.memberInfoBlocking(memberId);
        QGGroupMemberWithInfo member = group.memberWithInfoBlocking(memberId);
        relation.memberInfoBlocking(group.getId(), memberId);
        relation.memberWithInfoBlocking(group.getId(), memberId);
        group.removeMembersBlocking(Collections.singletonList(memberId), true);
        relation.removeMembersBlocking(group.getId(), Collections.singletonList(memberId), false);
        group.addToBlacklistBlocking(Collections.singletonList(memberId));
        group.removeFromBlacklistBlocking(Collections.singletonList(memberId));
        relation.addToBlacklistBlocking(group.getId(), Collections.singletonList(memberId));
        relation.removeFromBlacklistBlocking(group.getId(), Collections.singletonList(memberId));
        group.getBlacklist();
        relation.blacklist(group.getId());
        return member;
    }
}
