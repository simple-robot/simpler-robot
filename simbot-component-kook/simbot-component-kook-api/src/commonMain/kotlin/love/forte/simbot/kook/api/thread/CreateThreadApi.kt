/*
 *     Copyright (c) 2021-2026. ForteScarlet.
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

package love.forte.simbot.kook.api.thread

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmStatic

/**
 * [创建帖子](https://developer.kookapp.cn/doc/http/thread#%E5%88%9B%E5%BB%BA%E5%B8%96%E5%AD%90)
 *
 * @since 4.3.0
 * @author Forte
 */
public class CreateThreadApi private constructor(public override val body: Body) : KookPostApi<ThreadView>() {
    /**
     * 用于创建帖子的请求体。
     *
     * @since 4.3.0
     */
    @Serializable
    public class Body internal constructor(
        /** 频道 id */
        @SerialName("channel_id")
        public val channelId: String,

        /** 服务器 id */
        @SerialName("guild_id")
        public val guildId: String,

        /** 标题 */
        public val title: String,

        /** 卡片消息内容 */
        public val content: String,

        /** 帖子分区 id（若无默认为综合分区） */
        @SerialName("category_id")
        public val categoryId: String? = null,

        /** 封面url */
        public val cover: String? = null,
    ) {
        override fun toString(): String {
            return "CreateThreadApi.Body(" +
                    "channelId='$channelId', " +
                    "guildId='$guildId', " +
                    "title='$title', " +
                    "content='$content', " +
                    "categoryId=$categoryId, " +
                    "cover=$cover)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Body) return false

            if (channelId != other.channelId) return false
            if (guildId != other.guildId) return false
            if (title != other.title) return false
            if (content != other.content) return false
            if (categoryId != other.categoryId) return false
            if (cover != other.cover) return false

            return true
        }

        override fun hashCode(): Int {
            var result = channelId.hashCode()
            result = 31 * result + guildId.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + content.hashCode()
            result = 31 * result + (categoryId?.hashCode() ?: 0)
            result = 31 * result + (cover?.hashCode() ?: 0)
            return result
        }
    }

    /**
     * 用于构建 [CreateThreadApi.Body] 的构建器。
     *
     * @since 4.3.0
     */
    public class Builder internal constructor(
        private var channelId: String,
        private var guildId: String,
        private var title: String,
        private var content: String
    ) {
        private var categoryId: String? = null
        private var cover: String? = null

        /**
         * 设置帖子分区 id。
         */
        public fun categoryId(categoryId: String?): Builder = apply {
            this.categoryId = categoryId
        }

        /**
         * 设置封面 URL。
         */
        public fun cover(cover: String?): Builder = apply {
            this.cover = cover
        }

        /**
         * 构建 [CreateThreadApi.Body]。
         */
        public fun build(): Body = Body(
            channelId = channelId,
            guildId = guildId,
            title = title,
            content = content,
            categoryId = categoryId,
            cover = cover
        )
    }

    public companion object Factory {
        private val PATH = ApiPath.create("thread", "create")

        private val serializer = ThreadView.serializer()

        /**
         * 构造 [创建帖子][CreateThreadApi] 请求。
         *
         * @param body 请求体
         */
        @JvmStatic
        public fun create(body: Body): CreateThreadApi = CreateThreadApi(body)

        /**
         * 构造 [创建帖子][CreateThreadApi] 请求。
         *
         * @param channelId 频道 id
         * @param guildId 服务器 id
         * @param title 标题
         * @param content 卡片消息内容
         */
        @JvmStatic
        public fun create(
            channelId: String,
            guildId: String,
            title: String,
            content: String
        ): CreateThreadApi = CreateThreadApi(
            Body(
                channelId = channelId,
                guildId = guildId,
                title = title,
                content = content
            )
        )

        /**
         * 创建 [CreateThreadApi.Builder] 用于构建请求体。
         *
         * @param channelId 频道 id
         * @param guildId 服务器 id
         * @param title 标题
         * @param content 卡片消息内容
         */
        @JvmStatic
        public fun builder(
            channelId: String,
            guildId: String,
            title: String,
            content: String
        ): Builder = Builder(channelId, guildId, title, content)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<ThreadView>
        get() = serializer
}
