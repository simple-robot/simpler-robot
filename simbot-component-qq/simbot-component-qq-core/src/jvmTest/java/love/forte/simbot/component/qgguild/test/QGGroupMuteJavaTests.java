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
import love.forte.simbot.common.id.StringID;
import love.forte.simbot.component.qguild.group.QGGroup;
import love.forte.simbot.component.qguild.group.QGGroupMember;
import love.forte.simbot.component.qguild.group.QGGroupMemberMuteChange;
import love.forte.simbot.component.qguild.group.QGGroupRelation;
import love.forte.simbot.qguild.model.group.GroupMemberMuteState;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证 Java 可调用毫秒工厂及生成的阻塞入口。
 */
public class QGGroupMuteJavaTests {
    /**
     * 毫秒工厂暴露静态方法，输入单位和成员 ID 均得到保留。
     */
    @Test
    public void millisecondFactoriesAreCallable() {
        ID memberId = StringID.valueOf("member");
        long milliseconds = 1791676800123L;
        assertEquals(milliseconds, QGGroupMemberMuteChange.addAtMilliseconds(memberId, milliseconds).getMuteExpireAt().toEpochMilliseconds());
        assertEquals(milliseconds, QGGroupMemberMuteChange.updateAtMilliseconds(memberId, milliseconds).getMuteExpireAt().toEpochMilliseconds());
        assertEquals(memberId, QGGroupMemberMuteChange.remove(memberId).getMemberId());
    }

    /**
     * 实际调用阻塞入口，供带模拟客户端的运行测试验证请求及委托。
     */
    public static GroupMemberMuteState exercise(
            QGGroupMember member, QGGroup group, QGGroupRelation relation, long milliseconds) {
        member.addMuteAtMillisecondsBlocking(milliseconds);
        member.updateMuteAtMillisecondsBlocking(milliseconds);
        member.unmuteBlocking();
        GroupMemberMuteState state = member.muteStateBlocking();
        group.addMemberMuteAtMillisecondsBlocking(member.getId(), milliseconds);
        group.updateMemberMuteAtMillisecondsBlocking(member.getId(), milliseconds);
        group.unmuteMemberBlocking(member.getId());
        group.muteSettingBlocking();
        relation.addMemberMuteAtMillisecondsBlocking(group.getId(), member.getId(), milliseconds);
        relation.updateMemberMuteAtMillisecondsBlocking(group.getId(), member.getId(), milliseconds);
        relation.unmuteMemberBlocking(group.getId(), member.getId());
        relation.muteSettingBlocking(group.getId());
        group.setMemberMutesBlocking(Collections.singletonList(QGGroupMemberMuteChange.remove(member.getId())));
        relation.setMemberMutesBlocking(group.getId(), Collections.singletonList(QGGroupMemberMuteChange.remove(member.getId())));
        return state;
    }
}
