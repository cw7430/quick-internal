package com.quick.module.user.repository

import com.example.jooq.tables.references.NATIVE_USERS
import com.example.jooq.tables.references.REFRESH_TOKEN
import com.example.jooq.tables.references.USERS
import com.quick.module.user.dto.vo.UserVo
import com.quick.module.user.type.Role
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class UserJooqRepository(
    private val dslContext: DSLContext
) {
    fun findLoginInfoByEmail(email: String): UserVo.NativeLogin? =
        dslContext
            .select(
                USERS.ID.`as`("userId"),
                NATIVE_USERS.PASSWORD_HASH,
                USERS.AUTH_TYPE,
                USERS.NICK_NAME,
                USERS.GENDER,
                USERS.ROLE,
                USERS.CREATED_AT,
                USERS.UPDATED_AT
            )
            .from(USERS)
            .join(NATIVE_USERS).on(USERS.ID.eq(NATIVE_USERS.ID))
            .where(NATIVE_USERS.EMAIL.eq(email))
            .and(USERS.ROLE.ne(Role.LEFT.value))
            .fetchOneInto(UserVo.NativeLogin::class.java)

    fun findRefreshInfoByUserId(userId: Long): UserVo.Public? =
        dslContext
            .select(
                USERS.ID.`as`("userId"),
                USERS.AUTH_TYPE,
                USERS.NICK_NAME,
                USERS.GENDER,
                USERS.ROLE,
                USERS.CREATED_AT,
                USERS.UPDATED_AT
            )
            .from(USERS)
            .where(USERS.ID.eq(userId))
            .and(USERS.ROLE.ne(Role.LEFT.value))
            .fetchOneInto(UserVo.Public::class.java)

    fun existRefreshTokenByUserIdAndToken(userId: Long, refreshToken: String): Boolean =
        dslContext
            .fetchExists(
                dslContext.selectFrom(REFRESH_TOKEN)
                    .where(REFRESH_TOKEN.USER_ID.eq(userId))
                    .and(REFRESH_TOKEN.TOKEN.eq(refreshToken))
            )

    fun createRefreshToken(userId: Long, refreshToken: String, expiresAt: Instant) =
        dslContext.insertInto(REFRESH_TOKEN)
            .set(REFRESH_TOKEN.USER_ID, userId)
            .set(REFRESH_TOKEN.TOKEN, refreshToken)
            .set(REFRESH_TOKEN.EXPIRES_AT, expiresAt)
            .execute()

    fun deleteRefreshTokenByRefreshToken(refreshToken: String) =
        dslContext.deleteFrom(REFRESH_TOKEN)
            .where(REFRESH_TOKEN.TOKEN.eq(refreshToken))
            .execute()
}