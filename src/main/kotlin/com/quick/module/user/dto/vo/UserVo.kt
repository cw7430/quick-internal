package com.quick.module.user.dto.vo

import com.quick.module.user.type.AuthType
import com.quick.module.user.type.Gender
import com.quick.module.user.type.Role
import java.time.Instant

sealed interface UserVo {
    val userId: Long
    val authType: AuthType
    val nickName: String
    val gender: Gender
    val role: Role
    val createdAt: Instant
    val updatedAt: Instant

    data class Public(
        override val userId: Long,
        override val authType: AuthType,
        override val nickName: String,
        override val gender: Gender,
        override val role: Role,
        override val createdAt: Instant,
        override val updatedAt: Instant,
    ) : UserVo

    data class NativeLogin(
        override val userId: Long,
        val passwordHash: String,
        override val authType: AuthType,
        override val nickName: String,
        override val gender: Gender,
        override val role: Role,
        override val createdAt: Instant,
        override val updatedAt: Instant,
    ) : UserVo

}