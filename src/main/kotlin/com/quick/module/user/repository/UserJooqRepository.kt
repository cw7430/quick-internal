package com.quick.module.user.repository

import com.quick.jooq.tables.references.NATIVE_USERS
import com.quick.jooq.tables.references.REFRESH_TOKEN
import com.quick.jooq.tables.references.USERS
import com.quick.module.user.dto.vo.UserVo
import com.quick.module.user.type.AuthType
import com.quick.module.user.type.Gender
import com.quick.module.user.type.Role
import org.jooq.DSLContext
import org.jooq.impl.DSL
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

    fun findPasswordByUserId(userId: Long): String? =
        dslContext
            .select(NATIVE_USERS.PASSWORD_HASH)
            .from(NATIVE_USERS)
            .where(NATIVE_USERS.ID.eq(userId))
            .fetchOneInto(String::class.java)

    fun existRefreshTokenByUserIdAndToken(userId: Long, refreshToken: String): Boolean =
        dslContext
            .fetchExists(
                DSL.selectOne().from(REFRESH_TOKEN)
                    .where(REFRESH_TOKEN.USER_ID.eq(userId))
                    .and(REFRESH_TOKEN.TOKEN.eq(refreshToken))
            )

    fun existNativeUsersByUserId(userId: Long): Boolean =
        dslContext.fetchExists(
            DSL.selectOne().from(NATIVE_USERS)
                .where(NATIVE_USERS.ID.eq(userId))
        )

    fun existNativeUsersByEmail(email: String): Boolean =
        dslContext.fetchExists(
            DSL.selectOne().from(NATIVE_USERS)
                .where(NATIVE_USERS.EMAIL.eq(email))
        )

    fun createRefreshToken(userId: Long, refreshToken: String, expiresAt: Instant) =
        dslContext.insertInto(REFRESH_TOKEN)
            .set(REFRESH_TOKEN.USER_ID, userId)
            .set(REFRESH_TOKEN.TOKEN, refreshToken)
            .set(REFRESH_TOKEN.EXPIRES_AT, expiresAt)
            .execute()

    fun createUsersAndGetUsers(
        authType: AuthType,
        nickName: String,
        gender: Gender
    ) = dslContext.insertInto(USERS)
        .set(USERS.AUTH_TYPE, authType.value)
        .set(USERS.NICK_NAME, nickName)
        .set(USERS.GENDER, gender.value)
        .returning()
        .fetchOne()

    fun createNativeUsers(id: Long, email: String, passwordHash: String) =
        dslContext.insertInto(NATIVE_USERS)
            .set(NATIVE_USERS.ID, id)
            .set(NATIVE_USERS.EMAIL, email)
            .set(NATIVE_USERS.PASSWORD_HASH, passwordHash)
            .execute()

    fun updatePassword(userId: Long, passwordHash: String) =
        dslContext.update(NATIVE_USERS)
            .set(NATIVE_USERS.PASSWORD_HASH, passwordHash)
            .where(NATIVE_USERS.ID.eq(userId))
            .execute()

    fun updateNickName(userId: Long, nickName: String) =
        dslContext.update(USERS)
            .set(USERS.NICK_NAME, nickName)
            .where(USERS.ID.eq(userId))
            .execute()

    fun deleteRefreshTokenByRefreshToken(refreshToken: String) =
        dslContext.deleteFrom(REFRESH_TOKEN)
            .where(REFRESH_TOKEN.TOKEN.eq(refreshToken))
            .execute()
}