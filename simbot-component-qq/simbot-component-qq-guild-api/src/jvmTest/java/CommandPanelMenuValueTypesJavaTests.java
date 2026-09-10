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

import love.forte.simbot.qguild.model.menu.CustomMenuItemBuilder;
import love.forte.simbot.qguild.model.menu.CustomMenuItemType;
import love.forte.simbot.qguild.model.panel.CommandPanelTargetUpdateBuilder;
import love.forte.simbot.qguild.model.panel.CommandPanelTargetUpdateOp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author ForteScarlet
 */
public class CommandPanelMenuValueTypesJavaTests {

    @Test
    public void customMenuItemTypeIsUsableFromJava() {
        Assertions.assertEquals(
            CustomMenuItemType.of("custom"),
            CustomMenuItemType.of("custom")
        );
        Assertions.assertNotSame(
            CustomMenuItemType.getLink(),
            CustomMenuItemType.getLink()
        );
        Assertions.assertEquals(
            CustomMenuItemType.getLink(),
            new CustomMenuItemBuilder().type(CustomMenuItemType.getLink()).getTypeValue()
        );
    }

    @Test
    public void commandPanelTargetUpdateOpIsUsableFromJava() {
        Assertions.assertEquals(
            CommandPanelTargetUpdateOp.of("custom"),
            CommandPanelTargetUpdateOp.of("custom")
        );
        Assertions.assertNotSame(
            CommandPanelTargetUpdateOp.getAdd(),
            CommandPanelTargetUpdateOp.getAdd()
        );
        Assertions.assertEquals(
            CommandPanelTargetUpdateOp.getAdd(),
            new CommandPanelTargetUpdateBuilder().op(CommandPanelTargetUpdateOp.getAdd()).getOp()
        );
    }
}
