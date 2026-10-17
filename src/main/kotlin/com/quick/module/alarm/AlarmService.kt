package com.quick.module.alarm

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.common.config.security.JwtUtil
import com.quick.module.alarm.dto.request.AlarmRequestDto
import com.quick.module.alarm.dto.response.AlarmResponseDto
import com.quick.module.alarm.repository.AlarmJooqRepository
import com.quick.module.alarm.type.AlarmType
import com.quick.module.chat.repository.ChatJooqRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class AlarmService(
    private val jwtUtil: JwtUtil,
    private val alarmJooqRepository: AlarmJooqRepository,
    private val chatJooqRepository: ChatJooqRepository
) {
    fun getAlarmList(reqDto: AlarmRequestDto.GetList): List<AlarmResponseDto.ListData> {
        val myUserId = jwtUtil.getCurrentUserId()

        val alarmList = alarmJooqRepository.findAlarmListByUserId(
            myUserId,
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
        log.info { "Get Alarm List successfully for UserId:${myUserId}" }

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

    @Transactional
    fun createAlarm(reqDto: AlarmRequestDto.Alarm) {
        val myUserId = jwtUtil.getCurrentUserId()
        val isChatRoomExist = chatJooqRepository.existChatRoomByChatRoomIdAndUserId(
            chatRoomId = reqDto.chatRoomId,
            myUserId = myUserId,
            otherUserId = reqDto.userId
        )
        if (!isChatRoomExist) {
            throw CustomException(
                ResponseCode.RESOURCE_NOT_FOUND
            )
        }

        if (reqDto.alarmType != AlarmType.MESSAGE) {
            val alarmId = alarmJooqRepository.createAlarmAndGet(
                myUserId,
                otherUserId = reqDto.userId,
                chatRoomId = reqDto.chatRoomId,
                type = reqDto.alarmType
            )?.id ?: throw CustomException(
                ResponseCode.INTERNAL_SERVER_ERROR
            )
            log.info { "Create Alarm successfully for UserId:$myUserId, AlarmId:$alarmId" }
            return
        }

        val chatMessageId = requireNotNull(reqDto.chatMessageId)

        val isChatMessageExist = chatJooqRepository.existChatMessageByChatMessageIdAndUserId(
            chatMessageId,
            userId = myUserId
        )

        if (!isChatMessageExist) {
            throw CustomException(
                ResponseCode.RESOURCE_NOT_FOUND
            )
        }

        val existingAlarmId =
            alarmJooqRepository.findAlarmIdByMyUserIdAndChatRoomId(
                myUserId,
                reqDto.chatRoomId
            )

        val alarmId = if (existingAlarmId != null) {
            alarmJooqRepository.updateAlarmUpdatedAlarmAtByAlarmId(
                existingAlarmId
            )
            existingAlarmId
        } else {
            alarmJooqRepository.createAlarmAndGet(
                myUserId,
                otherUserId = reqDto.userId,
                chatRoomId = reqDto.chatRoomId,
                type = reqDto.alarmType
            )?.id ?: throw CustomException(
                ResponseCode.INTERNAL_SERVER_ERROR
            )
        }

        alarmJooqRepository.createChatMessageAlarm(
            alarmId = alarmId,
            chatMessageId = chatMessageId
        )

        log.info { "Create Alarm successfully for UserId:$myUserId, AlarmId:$alarmId" }
    }

    @Transactional
    fun updateAlarmRead(reqDto: AlarmRequestDto.Read) {
        val myUserId = jwtUtil.getCurrentUserId()
        alarmJooqRepository.updateAlarmReadByAlarmIdList(reqDto.alarmIdList)
        log.info { "Update Alarm Read successfully for UserId:$myUserId" }
    }

    @Transactional
    fun invalidateAlarm(alarmId: Long) {
        val myUserId = jwtUtil.getCurrentUserId()
        alarmJooqRepository.updateAlarmInvalidateByAlarmId(alarmId)
        log.info { "Invalidate Alarm successfully for UserId:$myUserId, AlarmId:$alarmId" }
    }
}