/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationVariantSpec
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

@ExperimentalAbiValidation
fun AbiValidationVariantSpec.configAbiValidation() {
    filters.excluded.byNames.add("**.internal.**")

    filters.excluded.annotatedWith.addAll(
        "love.forte.simbot.annotations.ExperimentalSimbotAPI",
        "love.forte.simbot.annotations.InternalSimbotAPI",
        "love.forte.simbot.resource.ExperimentalIOResourceAPI",
        "love.forte.simbot.extension.continuous.session.ExperimentalContinuousSessionAPI"
    )
}

@ExperimentalAbiValidation
fun AbiValidationVariantSpec.configOneBotAbiValidation() {
    filters.excluded.annotatedWith.addAll(
        "love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI",
        "love.forte.simbot.component.onebot.common.annotations.ExperimentalOneBotAPI",
        "love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor",
        "love.forte.simbot.component.onebot.common.annotations.SourceEventConstructor",

        // CustomEventResolver
        "love.forte.simbot.component.onebot.v11.core.event.ExperimentalCustomEventResolverApi",

        // CustomOneBotApi
        "love.forte.simbot.component.onebot.v11.core.api.ExperimentalCustomOneBotApi",

        // OneBotNonStandardApi
        "love.forte.simbot.component.onebot.v11.core.api.nonstandard.OneBotNonStandardApi"
    )
}

@ExperimentalAbiValidation
fun AbiValidationVariantSpec.configKookAbiValidation() {
    filters.excluded.annotatedWith.addAll(
        "love.forte.simbot.kook.ExperimentalKookApi",
        "love.forte.simbot.kook.InternalKookApi",
        "love.forte.simbot.kook.api.template.ExperimentalTemplateApi",
        "love.forte.simbot.component.kook.blacklist.ExperimentalBlacklistApi"
    )
}
