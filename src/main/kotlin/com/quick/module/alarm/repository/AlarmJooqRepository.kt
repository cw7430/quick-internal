package com.quick.module.alarm.repository

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.common.type.YN
import com.quick.jooq.tables.references.ALARM
import com.quick.jooq.tables.references.CHAT_MESSAGE
import com.quick.jooq.tables.references.CHAT_MESSAGE_ALARM
import com.quick.jooq.tables.references.USERS
import com.quick.module.alarm.dto.response.AlarmResponseDto
import com.quick.module.alarm.type.AlarmType
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class AlarmJooqRepository(
    private val dslContext: DSLContext
) {
    fun getAlarmListByUserId(
        userId: Long,
        cursorUpdatedAlarmAt: Instant?,
        cursorAlarmId: Long?,
        size: Int = 10
    ): List<AlarmResponseDto.ListData> {
        val ou = USERS.`as`("ou")
        val cma = CHAT_MESSAGE_ALARM.`as`("cma")
        val cma2 = CHAT_MESSAGE_ALARM.`as`("cma2")
        val cms = CHAT_MESSAGE.`as`("cms")

        val alarmSubquery = DSL.select(
            ALARM.ID,
            ALARM.OTHER_USER_ID,
            ALARM.CHAT_ROOM_ID,
            ALARM.TYPE,
            ALARM.READ,
            ALARM.UPDATED_ALARM_AT
        ).from(ALARM)
            .where(ALARM.MY_USER_ID.eq(userId))
            .and(ALARM.ACTIVE_FLAG.eq(1))
            .and(
                if (cursorUpdatedAlarmAt != null && cursorAlarmId != null) {
                    ALARM.UPDATED_ALARM_AT.lt(cursorUpdatedAlarmAt).or(
                        ALARM.UPDATED_ALARM_AT.eq(cursorUpdatedAlarmAt)
                            .and(ALARM.ID.lt(cursorAlarmId))
                    )
                } else {
                    DSL.noCondition()
                }
            )
            .orderBy(ALARM.UPDATED_ALARM_AT.desc(), ALARM.ID.desc())
            .limit(size)
            .asTable("a")

        val aId = alarmSubquery.field(ALARM.ID)!!
        val aOtherUserId = alarmSubquery.field(ALARM.OTHER_USER_ID)!!
        val aChatRoomId = alarmSubquery.field(ALARM.CHAT_ROOM_ID)!!
        val aType = alarmSubquery.field(ALARM.TYPE)!!
        val aRead = alarmSubquery.field(ALARM.READ)!!
        val aUpdatedAlarmAt = alarmSubquery.field(ALARM.UPDATED_ALARM_AT)!!

        val maxChatMessageId = DSL.field(
            DSL.select(DSL.max(cma2.CHAT_MESSAGE_ID))
                .from(cma2)
                .where(cma2.ALARM_ID.eq(aId))
                .and(aType.eq("MESSAGE"))
        )

        return dslContext.select(
            aId,
            aOtherUserId,
            ou.NICK_NAME,
            aChatRoomId,
            aType,
            aRead,
            aUpdatedAlarmAt,
            cma.ID,
            cma.CHAT_MESSAGE_ID,
            cms.MESSAGE
        ).from(alarmSubquery)
            .join(ou)
            .on(aOtherUserId.eq(ou.ID))
            .leftJoin(cma)
            .on(cma.CHAT_MESSAGE_ID.eq(maxChatMessageId))
            .leftJoin(cms)
            .on(cms.ID.eq(cma.CHAT_MESSAGE_ID))
            .and(cms.ACTIVE_FLAG.eq(1))
            .orderBy(aUpdatedAlarmAt.desc(), aId.desc())
            .fetch { record ->
                val chatMessageAlarmId = record[cma.ID]
                val chatMessageId = record[cma.CHAT_MESSAGE_ID]
                val message = record[cms.MESSAGE]

                AlarmResponseDto.ListData(
                    alarmId = record[aId]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    userId = record[aOtherUserId]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    nickName = record[ou.NICK_NAME]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    chatRoomId = record[aChatRoomId]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    alarmType = AlarmType.from(record[aType])
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    read = YN.from(record[aRead])
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    updatedAlarmAt = record[aUpdatedAlarmAt]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    messageAlarm = if (
                        chatMessageAlarmId != null &&
                        chatMessageId != null &&
                        message != null
                    ) {
                        AlarmResponseDto.ChatMessageAlarm(
                            chatMessageAlarmId = chatMessageAlarmId,
                            chatMessageId = chatMessageId,
                            message = message
                        )
                    } else {
                        null
                    }
                )
            }
    }

    fun createAlarmAndGet(myUserId: Long, otherUserId: Long, chatRoomId: Long, type: AlarmType) =
        dslContext.insertInto(ALARM)
            .set(ALARM.MY_USER_ID, myUserId)
            .set(ALARM.OTHER_USER_ID, otherUserId)
            .set(ALARM.CHAT_ROOM_ID, chatRoomId)
            .set(ALARM.TYPE, type.value)
            .returning()
            .fetchOne()


    fun createChatMessageAlarm(alarmId: Long, chatMessageId: Long) {
        dslContext.insertInto(CHAT_MESSAGE_ALARM)
            .set(CHAT_MESSAGE_ALARM.ALARM_ID, alarmId)
            .set(CHAT_MESSAGE_ALARM.CHAT_MESSAGE_ID, chatMessageId)
    }
}