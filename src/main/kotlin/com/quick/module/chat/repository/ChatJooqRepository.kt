package com.quick.module.chat.repository

import com.quick.common.type.YN
import com.quick.jooq.tables.references.CHAT_MEMBER
import com.quick.jooq.tables.references.CHAT_ROOM
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ChatJooqRepository(
    private val dslContext: DSLContext
) {
    fun existActiveChatRoomByUserId(
        reqUserId: Long,
        resUserId: Long
    ): Boolean {
        val cmSub = CHAT_MEMBER.`as`("cm_sub")
        val cr = CHAT_ROOM.`as`("cr")
        val cmMain = CHAT_MEMBER.`as`("cm_main")

        return dslContext.fetchExists(
            dslContext
                .select(cmMain.CHAT_ROOM_ID)
                .from(cmMain)
                .where(
                    cmMain.CHAT_ROOM_ID.`in`(
                        dslContext
                            .select(cmSub.CHAT_ROOM_ID)
                            .from(cmSub)
                            .innerJoin(cr).on(cmSub.CHAT_ROOM_ID.eq(cr.ID))
                            .where(cmSub.USER_ID.`in`(reqUserId, resUserId))
                            .and(cr.VALID.eq(YN.Y.value))
                            .groupBy(cmSub.CHAT_ROOM_ID)
                            .having(DSL.countDistinct(cmSub.USER_ID).eq(2))
                    )
                )
                .groupBy(cmMain.CHAT_ROOM_ID)
                .having(DSL.count().eq(2))
        )
    }

    fun createChatRoomAndGet() =
        dslContext.insertInto(CHAT_ROOM)
            .defaultValues()
            .returning()
            .fetchOne()

    fun createChatMember(chatRoomId: Long, userId: Long, accepted: YN) =
        dslContext.insertInto(CHAT_MEMBER)
            .set(CHAT_MEMBER.CHAT_ROOM_ID, chatRoomId)
            .set(CHAT_MEMBER.USER_ID, userId)
            .set(CHAT_MEMBER.ACCEPTED, accepted.value)
            .execute()

    fun updateChatMemberAccepted(chatMemberId: Long) =
        dslContext.update(CHAT_MEMBER)
            .set(CHAT_MEMBER.ACCEPTED, YN.Y.value)
            .where(CHAT_MEMBER.ID.eq(chatMemberId))
            .execute()

    fun updateChatRoomUpdatedAt(chatRoomId: Long) =
        dslContext.update(CHAT_ROOM)
            .set(CHAT_ROOM.UPDATED_AT, Instant.now())
            .where(CHAT_ROOM.ID.eq(chatRoomId))
            .execute()
}