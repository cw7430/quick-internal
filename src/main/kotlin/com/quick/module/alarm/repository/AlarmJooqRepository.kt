package com.quick.module.alarm.repository

import com.quick.jooq.tables.references.ALARM
import com.quick.jooq.tables.references.CHAT_MESSAGE_ALARM
import com.quick.module.alarm.type.AlarmType
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class AlarmJooqRepository(
    private val dslContext: DSLContext
) {
    fun getAlarmListByUserId(
        userId: Long,
        createdAt: Instant?,
        alarmId: Long?,
        size: Int = 10
    ) {

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
            .set(CHAT_MESSAGE_ALARM.ID, alarmId)
            .set(CHAT_MESSAGE_ALARM.CHAT_MESSAGE_ID, chatMessageId)
    }
}