/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     DetailContainers.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.api.message.containers

import java.time.LocalDate
import java.time.OffsetDateTime


/**
 * 各种细节性质的容器，例如用户详情中的年龄、邮箱等。
 *
 * 需要注意的是，这些属性全部都是不稳定的。
 *
 * profile
 *
 */
public interface DetailContainers : Container


/**
 * 等级容器.
 * 如果不支持获取，等级则会为 `-1`.
 */
public interface LevelContainer : DetailContainers {
    val level: Long
}


/**
 * 年龄容器。
 */
public interface AgeContainer : DetailContainers {
    /** 用户的年龄。获取不到的时候得到 `-1` */
    val age: Int

    /** 用户的生日。获取不到的时候得到 `-1` */
    val birthday: Long
        get() =
            if (age == -1) -1
            else LocalDate.now()
                .plusYears(age.toLong())
                .atStartOfDay()
                .toInstant(OffsetDateTime.now().plusYears(age.toLong()).offset)
                .toEpochMilli()
}

/**
 * 联系方式容器，可以允许得到用户的手机号、邮箱。
 */
public interface ContactDetailsContainer : DetailContainers {
    val email: String?
    val phone: String?
}

/**
 * 性别枚举
 */
public enum class Gender {
    MALE, FEMALE, UNKNOWN
}

/**
 * 性别容器，可以得到一个 [性别][Gender].
 *
 * 如果获取不到则为null，如果获取到了但是是未知或者为其他非男/女性别时得到 [Gender.UNKNOWN]
 *
 */
public interface GenderContainer : DetailContainers {
    val gender: Gender?
}


/**
 * 个性签名 / 自我介绍容器
 */
public interface SignatureContainer : DetailContainers {
    val signature: String?
}


/**
 * 一个账号的详细信息。
 */
public interface AccountDetailInfo : LevelContainer, GenderContainer, AgeContainer, ContactDetailsContainer, SignatureContainer
