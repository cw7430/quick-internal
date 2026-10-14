package com.quick.module.alarm

import com.quick.common.config.security.JwtUtil
import com.quick.module.alarm.repository.AlarmJooqRepository
import org.springframework.stereotype.Service
import com.quick.module.alarm.dto.request.AlarmRequestDto
import com.quick.module.alarm.dto.response.AlarmResponseDto
import com.quick.module.alarm.type.AlarmType

@Service
class AlarmService(
    private val jwtUtil: JwtUtil,
    private val alarmJooqRepository: AlarmJooqRepository
) {
    fun getAlarmList(reqDto: AlarmRequestDto.GetList): List<AlarmResponseDto.ListData> {
        val userId = jwtUtil.getCurrentUserId()

        val alarmList = alarmJooqRepository.findAlarmListByUserId(
            userId,
            reqDto.updatedAlarmAt,
            cursorAlarmId = reqDto.alarmId,
            size = reqDto.size
        )

        if (alarmList.isEmpty()) {
            return emptyList()
        }

        val alarmIdList = alarmList
            .filter { it.alarmType == AlarmType.MESSAGE }
            .map { it.alarmId }

        val alarmMessageMap = if (alarmIdList.isNotEmpty()) {
            alarmJooqRepository.findChatAlarmListByAlarmIdList(alarmIdList)
                .associateBy { it.alarmId }
        } else {
            emptyMap()
        }

        return alarmList.map { alarm ->
            val messageAlarm = alarmMessageMap[alarm.alarmId]

            AlarmResponseDto.ListData(
                alarmId = alarm.alarmId,
                userId = alarm.userId,
                nickName = alarm.nickName,
                chatRoomId = alarm.chatRoomId,
                alarmType = alarm.alarmType,
                read = alarm.read,
                updatedAlarmAt = alarm.updatedAlarmAt,
                messageAlarm = messageAlarm?.let {
                    AlarmResponseDto.ChatMessageAlarm(
                        chatMessageAlarmId = it.chatMessageAlarmId,
                        chatMessageId = it.chatMessageId,
                        message = it.message
                    )
                }
            )
        }
    }
}