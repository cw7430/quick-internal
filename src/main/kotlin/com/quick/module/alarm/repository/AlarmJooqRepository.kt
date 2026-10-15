package com.quick.module.alarm.repository

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.common.type.YN
import com.quick.jooq.tables.references.ALARM
import com.quick.jooq.tables.references.CHAT_MESSAGE
import com.quick.jooq.tables.references.CHAT_MESSAGE_ALARM
import com.quick.jooq.tables.references.USERS
import com.quick.module.alarm.dto.vo.AlarmVo
import com.quick.module.alarm.type.AlarmType
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class AlarmJooqRepository(
    private val dslContext: DSLContext
) {
    fun findAlarmListByUserId(
        userId: Long,
        cursorUpdatedAlarmAt: Instant?,
        cursorAlarmId: Long?,
        size: Int = 10
    ): List<AlarmVo.Alarm> {
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
                AlarmVo.Alarm(
                    alarmId = record[a.ID] ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    userId = record[a.OTHER_USER_ID]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    nickName = record[ou.NICK_NAME]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    chatRoomId = record[a.CHAT_ROOM_ID]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    alarmType = AlarmType.from(record[a.TYPE])
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    read = YN.from(record[a.READ])
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    updatedAlarmAt = record[a.UPDATED_ALARM_AT]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                )
            }
    }

    fun findChatAlarmListByAlarmIdList(alarmIdList: List<Long>): List<AlarmVo.ChatMessageAlarm> {
        val a = ALARM.`as`("a")
        val cma = CHAT_MESSAGE_ALARM.`as`("cma")
        val cma2 = CHAT_MESSAGE_ALARM.`as`("cma2")
        val cms = CHAT_MESSAGE.`as`("cms")

        return dslContext.select(
            cma.ID,
            cma.ALARM_ID,
            cma.CHAT_MESSAGE_ID,
            cms.MESSAGE
        ).from(a)
            .join(cma).on(
                cma.ALARM_ID.eq(a.ID).and(
                    cma.CHAT_MESSAGE_ID.eq(
                        DSL.select(cma2.CHAT_MESSAGE_ID)
                            .from(cma2)
                            .where(cma2.ALARM_ID.eq(a.ID))
                            .orderBy(cma2.CHAT_MESSAGE_ID.desc())
                            .limit(1)
                    )
                )
            )
            .join(cms).on(
                cma.CHAT_MESSAGE_ID.eq(cms.ID).and(
                    cms.ACTIVE_FLAG.eq(1)
                )
            )
            .where(a.ID.`in`(alarmIdList))
            .and(a.TYPE.eq(AlarmType.MESSAGE.value))
            .fetch { record ->
                AlarmVo.ChatMessageAlarm(
                    chatMessageAlarmId = record[cma.ID]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    alarmId = record[cma.ALARM_ID]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    chatMessageId = record[cms.ID]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                    message = record[cms.MESSAGE]
                        ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR),
                )
            }
    }

    fun findAlarmIdByMyUserIdAndChatRoomId(
        myUserId: Long,
        chatRoomId: Long
    ): Long? =
        dslContext.select(ALARM.ID)
            .from(ALARM)
            .where(ALARM.MY_USER_ID.eq(myUserId))
            .and(ALARM.CHAT_ROOM_ID.eq(chatRoomId))
            .and(ALARM.TYPE.eq(AlarmType.MESSAGE.value))
            .fetchOne(ALARM.ID)

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
            .execute()
    }

    fun updateAlarmUpdatedAlarmAtByAlarmId(alarmId: Long) {
        dslContext.update(ALARM)
            .set(ALARM.UPDATED_ALARM_AT, Instant.now())
            .where(ALARM.ID.eq(alarmId))
            .execute()
    }

    fun updateAlarmReadByAlarmIdList(alarmIdList: List<Long>) {
        dslContext.update(ALARM)
            .set(ALARM.READ, YN.Y.value)
            .where(ALARM.ID.`in`(alarmIdList))
            .execute()
    }

    fun updateAlarmInvalidateByAlarmId(alarmId: Long) {
        dslContext.update(ALARM)
            .set(ALARM.VALID, YN.N.value)
            .where(ALARM.ID.eq(alarmId))
            .execute()
    }
}