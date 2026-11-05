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

import love.forte.simbot.qguild.api.group.*;
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyCreate;
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyGroupAction;
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyModify;
import love.forte.simbot.qguild.model.group.GroupJoinApprovalStrategyOp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GroupJoinApprovalStrategyJavaTests {
    @Test
    public void buildersAndFactoriesAreUsableFromJava() {
        GroupJoinApprovalStrategyCreate input = GroupJoinApprovalStrategyCreate.builder()
            .addGroupId("123456789")
            .isEnable(false)
            .remark("策略备注")
            .build();
        Assertions.assertEquals(List.of("123456789"), input.getGroupIds());
        Assertions.assertEquals(Boolean.FALSE, input.isEnable());
        Assertions.assertEquals(input, CreateGroupJoinApprovalStrategyApi.create(input).getBody());
        GroupJoinApprovalStrategyGroupAction action = GroupJoinApprovalStrategyGroupAction.builder()
            .op(GroupJoinApprovalStrategyOp.DEL)
            .addGroupOpenids(List.of("group"))
            .build();
        GroupJoinApprovalStrategyModify modify = GroupJoinApprovalStrategyModify.builder()
            .groupAction(action)
            .isEnable(false)
            .build();
        Assertions.assertEquals(modify, ModifyGroupJoinApprovalStrategyApi.create("strategy", modify).getBody());
        Assertions.assertEquals("/v2/groups/join_approval_strategy", GetGroupJoinApprovalStrategyListApi.create().getUrl().getEncodedPath());
        Assertions.assertNotNull(DeleteGroupJoinApprovalStrategyApi.create("strategy"));
        Assertions.assertNotNull(ExecuteGroupJoinApprovalStrategyApi.create("strategy"));
        Assertions.assertNotNull(ModifyGroupJoinApprovalStrategyWhitelistApi.create("strategy", GroupJoinApprovalStrategyOp.ADD, List.of("1234567")));
    }
}
