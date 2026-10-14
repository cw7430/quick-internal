package com.quick.module.alarm.dto.vo

import com.quick.common.type.YN
import com.quick.module.alarm.type.AlarmType
import java.time.Instant

interface AlarmVo {
    data class Alarm(
        val alarmId: Long,
        val userId: Long,
        val nickName: String,
        val chatRoomId: Long,
        val alarmType: AlarmType,
        val read: YN,
        val updatedAlarmAt: Instant,
       // val alarmIdList:List<Long>
    ):AlarmVo

    data class ChatMessageAlarm(
        val chatMessageAlarmId: Long,
        val alarmId: Long,
        val chatMessageId: Long,
        val message: String
    ):AlarmVo
}