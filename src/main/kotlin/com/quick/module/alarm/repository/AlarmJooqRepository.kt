package com.quick.module.alarm.repository

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.common.type.YN
import com.quick.jooq.tables.references.ALARM
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
        val a = ALARM.`as`("a")
        val ou = USERS.`as`("ou")
        return dslContext.select(
            a.ID,
            a.OTHER_USER_ID,
            ou.NICK_NAME,
            a.CHAT_ROOM_ID,
            a.TYPE,
            a.READ,
            a.UPDATED_ALARM_AT
        ).from(a)
            .join(ou).on(a.OTHER_USER_ID.eq(ou.ID))
            .where(a.MY_USER_ID.eq(userId))
            .and(a.ACTIVE_FLAG.eq(1))
            .and(
                if (cursorUpdatedAlarmAt != null && cursorAlarmId != null) {
                    a.UPDATED_ALARM_AT.lt(cursorUpdatedAlarmAt).or(
                        a.UPDATED_ALARM_AT.eq(cursorUpdatedAlarmAt)
                            .and(a.ID.lt(cursorAlarmId))
                    )
                } else {
                    DSL.noCondition()
                }
            )
            .orderBy(a.UPDATED_ALARM_AT.desc(), a.ID.desc())
            .limit(size)
            .fetch { record ->
                AlarmResponseDto.ListData(
                    alarmId = record[a.ID] ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    userId = record[a.OTHER_USER_ID] ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    nickName = record[ou.NICK_NAME] ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    chatRoomId = record[a.CHAT_ROOM_ID] ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    alarmType = AlarmType.from(record[a.TYPE])
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    read = YN.from(record[a.READ]) ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    updatedAlarmAt = record[a.UPDATED_ALARM_AT]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    messageAlarm = null,
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