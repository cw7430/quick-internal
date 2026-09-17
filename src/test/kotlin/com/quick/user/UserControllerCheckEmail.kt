package com.quick.user

import com.quick.common.api.type.ResponseCode
import com.quick.module.user.dto.request.CreateNativeUserRequestDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerCheckEmail : UserControllerTest() {

    @Test
    @DisplayName("Email 중복 체크 - 성공")
    fun checkEmailSuccess() {
        post(URL)
            .key().body(DATA).send()
            .andExpect(status().isNoContent())
    }

    @Test
    @DisplayName("Email 중복 체크 - 잘 못된 입력 값")
    fun checkEmailFailWithValidationError() {
        post(URL)
            .key().body(INVALIDATED_DATA).send()
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ResponseCode.VALIDATION_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("Email 중복 체크 - Api Key 오류")
    fun checkEmailFailWithKeyError() {
        post(URL)
            .body(DATA).send().andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("Email 중복 체크 - 중복 된 값")
    fun checkEmailFailWithDuplicateResource() {
        post(URL)
            .key().body(DUPLICATED_DATA).send()
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(ResponseCode.DUPLICATE_RESOURCE.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$USER_URL/email"
        private val DATA = CreateNativeUserRequestDto.CheckEmail("example@gmail.com")
        private val INVALIDATED_DATA = CreateNativeUserRequestDto.CheckEmail("1234")
        private val DUPLICATED_DATA = CreateNativeUserRequestDto.CheckEmail("email@email.com")
    }
}