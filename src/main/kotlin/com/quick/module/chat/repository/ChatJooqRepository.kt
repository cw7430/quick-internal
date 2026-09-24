package com.quick.module.chat.repository

import com.quick.common.type.YN
import com.quick.jooq.tables.references.CHAT_MEMBER
import com.quick.jooq.tables.references.CHAT_MESSAGE
import com.quick.jooq.tables.references.CHAT_ROOM
import com.quick.jooq.tables.references.USERS
import com.quick.module.chat.dto.response.ChatMessageResponseDto
import com.quick.module.chat.dto.response.ChatRoomResponseDto
import com.quick.module.user.type.Gender
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ChatJooqRepository(
    private val dslContext: DSLContext
) {
    fun findChatRoomListByUserId(
        reqUserId: Long,
        cursorUpdatedAt: Instant?,
        cursorChatRoomId: Long?,
        size: Int = 10
    ): List<ChatRoomResponseDto.ListData> {
        val cmbMe = CHAT_MEMBER.`as`("cmb_me")
        val cmbOther = CHAT_MEMBER.`as`("cmb_other")
        val cmbMsg = CHAT_MEMBER.`as`("cmb_msg")
        val cmsLast = CHAT_MESSAGE.`as`("cms_last")
        val cmsUnread = CHAT_MESSAGE.`as`("cms_unread")
        val cr = CHAT_ROOM.`as`("cr")
        val u = USERS.`as`("u")

        val lastMessage = DSL
            .select(cmsLast.MESSAGE)
            .from(cmsLast)
            .join(cmbMsg).on(cmbMsg.ID.eq(cmsLast.CHAT_MEMBER_ID))
            .where(cmbMsg.CHAT_ROOM_ID.eq(cr.ID))
            .and(cmsLast.VALID.eq(YN.Y.value))
            .orderBy(
                cmsLast.UPDATED_AT.desc(),
                cmsLast.ID.desc()
            )
            .limit(1)
            .asField<String>("lastMessage")

        val totalUnread = DSL
            .selectCount()
            .from(cmsUnread)
            .where(
                cmsUnread.CHAT_MEMBER_ID.eq(cmbOther.ID)
                    .and(cmsUnread.UNREAD.gt(0))
            )
            .and(cmsUnread.VALID.eq(YN.Y.value))
            .asField<Long>("totalUnread")

        return dslContext
            .select(
                cr.ID.`as`("chatRoomId"),
                cr.UPDATED_AT,
                cmbOther.ID.`as`("chatMemberId"),
                cmbOther.USER_ID,
                u.NICK_NAME,
                u.GENDER,
                cmbOther.ACCEPTED,
                lastMessage,
                totalUnread
            )
            .from(cr)
            .join(cmbMe).on(
                cmbMe.CHAT_ROOM_ID.eq(cr.ID)
                    .and(cmbMe.USER_ID.eq(reqUserId))
            )
            .join(cmbOther).on(
                cmbOther.CHAT_ROOM_ID.eq(cr.ID)
                    .and(cmbOther.USER_ID.ne(reqUserId))
            )
            .join(u).on(cmbOther.USER_ID.eq(u.ID))
            .where(
                if (cursorUpdatedAt != null && cursorChatRoomId != null) {
                    cr.UPDATED_AT.lt(cursorUpdatedAt)
                        .or(
                            cr.UPDATED_AT.eq(cursorUpdatedAt)
                                .and(cr.ID.lt(cursorChatRoomId))
                        )
                } else {
                    DSL.noCondition()
                }
            )
            .and(cr.VALID.eq(YN.Y.value))
            .orderBy(
                cr.UPDATED_AT.desc(),
                cr.ID.desc()
            )
            .limit(size)
            .fetchInto(ChatRoomResponseDto.ListData::class.java)
    }

    fun findChatRoomByChatRoomIdAndUserId(chatRoomId: Long, reqUserId: Long): ChatRoomResponseDto.DetailData? {
        val cmMain = CHAT_MEMBER.`as`("cm_main")
        val cmSub = CHAT_MEMBER.`as`("cm_sub")
        val cr = CHAT_ROOM.`as`("cr")
        val u = USERS.`as`("u")

        return dslContext.select(
            cr.ID,
            cr.UPDATED_AT,
            multiset(
                dslContext.select(
                    cmSub.ID,
                    cmSub.USER_ID,
                    u.NICK_NAME,
                    u.GENDER,
                    cmSub.ACCEPTED
                )
                    .from(cmSub)
                    .join(u).on(cmSub.USER_ID.eq(u.ID))
                    .where(cmSub.CHAT_ROOM_ID.eq(cr.ID))
            ).convertFrom { result ->
                result.mapNotNull { record ->
                    val id = record[cmSub.ID] ?: return@mapNotNull null
                    val userId = record[cmSub.USER_ID] ?: return@mapNotNull null
                    val nickName = record[u.NICK_NAME] ?: return@mapNotNull null
                    val gender = Gender.from(record[u.GENDER]) ?: return@mapNotNull null
                    val accepted = YN.from(record[cmSub.ACCEPTED]) ?: return@mapNotNull null
                    val me = if (userId == reqUserId) YN.Y else YN.N

                    ChatRoomResponseDto.ChatMember(
                        chatMemberId = id,
                        userId = userId,
                        nickName = nickName,
                        gender = gender,
                        accepted = accepted,
                        me
                    )
                }
            }
        )
            .from(cr)
            .join(cmMain).on(cmMain.CHAT_ROOM_ID.eq(cr.ID))
            .where(cr.ID.eq(chatRoomId))
            .and(cr.VALID.eq(YN.Y.value))
            .and(cmMain.USER_ID.eq(reqUserId))
            .fetchOne { record ->
                val id = record[cr.ID] ?: return@fetchOne null
                val updatedAt = record[cr.UPDATED_AT] ?: return@fetchOne null
                val memberList = record.value3()

                ChatRoomResponseDto.DetailData(
                    chatRoomId = id,
                    updatedAt = updatedAt,
                    memberList = memberList
                )
            }
    }

    fun findChatMessageListByChatRoomId(
        chatRoomId: Long,
        cursorCreatedAt: Instant?,
        cursorChatMessageId: Long?,
        size: Int = 10
    ): List<ChatMessageResponseDto> {
        val cms = CHAT_MESSAGE.`as`("cms")

        return dslContext.select(
            cms.ID.`as`("chatMessageId"), cms.CHAT_MEMBER_ID,
            cms.MESSAGE,
            cms.VALID,
            cms.UNREAD,
            cms.CREATED_AT,
            cms.UPDATED_AT
        ).from(cms)
            .where(cms.CHAT_ROOM_ID.eq(chatRoomId))
            .and(
                if (cursorCreatedAt != null && cursorChatMessageId != null) {
                    cms.CREATED_AT.lt(cursorCreatedAt)
                        .or(
                            cms.CREATED_AT.eq(cursorCreatedAt)
                                .and(cms.ID.lt(cursorChatMessageId))
                        )
                } else {
                    DSL.noCondition()
                }
            )
            .and(cms.ACTIVE_FLAG.eq(1))
            .orderBy(cms.CREATED_AT.desc(), cms.ID.desc())
            .limit(size)
            .fetchInto(ChatMessageResponseDto::class.java)
    }

    fun existActiveChatRoomByUserId(
        reqUserId: Long,
        resUserId: Long
    ): Boolean {
        val cmSub = CHAT_MEMBER.`as`("cm_sub")
        val cr = CHAT_ROOM.`as`("cr")
        val cmMain = CHAT_MEMBER.`as`("cm_main")

        return dslContext.fetchExists(
            DSL.selectOne()
                .from(cmMain)
                .join(cr).on(cmMain.CHAT_ROOM_ID.eq(cr.ID))
                .where(cmMain.USER_ID.`in`(reqUserId, resUserId))
                .and(cr.VALID.eq(YN.Y.value))
                .groupBy(cmMain.CHAT_ROOM_ID)
                .having(DSL.countDistinct(cmMain.USER_ID).eq(2))
                .and(
                    DSL.field(
                        DSL.selectCount()
                            .from(cmSub)
                            .where(cmSub.CHAT_ROOM_ID.eq(cmMain.CHAT_ROOM_ID))
                    ).eq(2)
                )
        )
    }

    fun existChatMemberByChatRoomIdAndUserId(chatRoomId: Long, userId: Long) =
        dslContext.fetchExists(
            DSL.selectOne()
                .from(CHAT_MEMBER)
                .where(CHAT_MEMBER.CHAT_ROOM_ID.eq(chatRoomId))
                .and(CHAT_MEMBER.USER_ID.eq(userId))
        )

    fun existChatMemberByChatMemberIdAndUserId(chatMemberId: Long, userId: Long) =
        dslContext.fetchExists(
            DSL.selectOne()
                .from(CHAT_MEMBER)
                .where(CHAT_MEMBER.ID.eq(chatMemberId))
                .and(CHAT_MEMBER.USER_ID.eq(userId))
        )

    fun existChatMessageByChatMessageIdAndUserId(chatMessageId: Long, userId: Long) =
        dslContext.fetchExists(
            DSL.selectOne()
                .from(CHAT_MESSAGE)
                .join(CHAT_MEMBER).on(CHAT_MESSAGE.CHAT_MEMBER_ID.eq(CHAT_MEMBER.ID))
                .where(CHAT_MESSAGE.ID.eq(chatMessageId))
                .and(CHAT_MEMBER.USER_ID.eq(userId))
        )

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

    fun createChatMessageByChatMemberId(chatMemberId: Long, message: String): Int {
        val cmSender = CHAT_MEMBER.`as`("cm_sender")
        val cmOther = CHAT_MEMBER.`as`("cm_other")

        val chatRoomId = DSL.select(cmSender.CHAT_ROOM_ID)
            .from(cmSender)
            .where(cmSender.ID.eq(chatMemberId))

        val unread = DSL
            .selectCount()
            .from(cmOther)
            .where(
                cmOther.CHAT_ROOM_ID.eq(
                    DSL.select(cmSender.CHAT_ROOM_ID)
                        .from(cmSender)
                        .where(cmSender.ID.eq(chatMemberId))
                )
            )
            .and(cmOther.ID.ne(chatMemberId))
            .asField<Long>("unread")

        return dslContext.insertInto(CHAT_MESSAGE)
            .set(CHAT_MESSAGE.CHAT_ROOM_ID, chatRoomId)
            .set(CHAT_MESSAGE.CHAT_MEMBER_ID, chatMemberId)
            .set(CHAT_MESSAGE.MESSAGE, message)
            .set(CHAT_MESSAGE.UNREAD, unread)
            .execute()
    }

    fun updateChatMemberAcceptedByChatMemberId(chatMemberId: Long) =
        dslContext.update(CHAT_MEMBER)
            .set(CHAT_MEMBER.ACCEPTED, YN.Y.value)
            .where(CHAT_MEMBER.ID.eq(chatMemberId))
            .execute()

    fun updateChatRoomUpdatedAtByChatMemberId(chatMemberId: Long) =
        dslContext.update(CHAT_ROOM)
            .set(CHAT_ROOM.UPDATED_AT, Instant.now())
            .where(
                CHAT_ROOM.ID.eq(
                    DSL.select(CHAT_MEMBER.CHAT_ROOM_ID)
                        .from(CHAT_MEMBER)
                        .where(CHAT_MEMBER.ID.eq(chatMemberId))
                        .limit(1)
                )
            ).execute()

    fun updateChatRoomUpdatedAtByChatMessageId(chatMessageId: Long) =
        dslContext.update(CHAT_ROOM)
            .set(CHAT_ROOM.UPDATED_AT, Instant.now())
            .where(
                CHAT_ROOM.ID.eq(
                    DSL.select(CHAT_MESSAGE.CHAT_ROOM_ID)
                        .from(CHAT_MESSAGE)
                        .where(CHAT_MESSAGE.ID.eq(chatMessageId))
                        .limit(1)
                )
            ).execute()

    fun updateChatRoomInvalidByChatRoomId(chatRoomId: Long) =
        dslContext.update(CHAT_ROOM)
            .set(CHAT_ROOM.VALID, YN.N.value)
            .set(CHAT_ROOM.DELETED_AT, Instant.now())
            .where(CHAT_ROOM.ID.eq(chatRoomId))
            .execute()

    fun updateChatMessageByChatMessageId(chatMessageId: Long, message: String) =
        dslContext.update(CHAT_MESSAGE)
            .set(CHAT_MESSAGE.MESSAGE, message)
            .where(CHAT_MESSAGE.ID.eq(chatMessageId))
            .execute()

    fun updateChatMessageReadByChatMessageIdList(chatMessageIdList: List<Long>) =
        dslContext.update(CHAT_MESSAGE)
            .set(CHAT_MESSAGE.UNREAD, CHAT_MESSAGE.UNREAD.minus(1))
            .where(CHAT_MESSAGE.ID.`in`(chatMessageIdList))
            .execute()

    fun updateChatMessageInvalidByChatMessageId(chatMessageId: Long) =
        dslContext.update(CHAT_MESSAGE)
            .set(CHAT_MESSAGE.VALID, YN.N.value)
            .set(CHAT_MESSAGE.DELETED_AT, Instant.now())
            .where(CHAT_MESSAGE.ID.eq(chatMessageId))
            .execute()
}