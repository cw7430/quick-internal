package com.quick.user

import com.quick.common.api.type.ResponseCode
import com.quick.module.user.dto.request.UpdateNativeUserRequestDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UpdatePasswordTest : UserControllerTest() {

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    fun success() {
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        patch(URL)
            .key().auth(accessToken).body(DATA)
            .send().andExpect(status().isNoContent())

    }

    @Test
    @DisplayName("비밀번호 변경 - 잘 못된 입력 값")
    fun failWithValidationError() {
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        patch(URL)
            .key().auth(accessToken).body(INVALIDATED_DATA)
            .send().andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ResponseCode.VALIDATION_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("비밀번호 변경 - 인증 오류")
    fun failWithUnauthorized() {
        patch(URL)
            .key().body(DATA)
            .send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("비밀번호 변경 - 비밀번호 오류")
    fun failWithPasswordError() {
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        patch(URL)
            .key().auth(accessToken).body(WRONG_PASSWORD_DATA)
            .send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.PASSWORD_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("비밀번호 변경 - 잘못 된 토큰")
    fun failWithInvalidToken() {
        patch(URL)
            .key().auth(INVALID_TOKEN).body(DATA)
            .send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("비밀번호 변경 - 만료 된 토큰")
    fun failWithExpiredToken() {
        val accessToken = authTestUtil.generateExpiredAccessToken(USER_LOGIN_DATA)
        patch(URL)
            .key().auth(accessToken).body(DATA)
            .send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRED_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("비밀번호 변경 - Api Key 오류")
    fun failWithKeyError() {
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        patch(URL)
            .auth(accessToken).body(DATA)
            .send().andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("비밀번호 변경 - 중복 된 값")
    fun failWithDuplicateResource() {
        val accessToken = authTestUtil.getTestToken(USER_LOGIN_DATA).accessToken
        patch(URL)
            .key().auth(accessToken).body(DUPLICATED_DATA)
            .send().andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(ResponseCode.DUPLICATE_RESOURCE.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$USER_URL/password"
        private val DATA = UpdateNativeUserRequestDto.Password(
            password = "strongpassword123!@",
            newPassword = "newexamplepw1234!@",
        )
        private val INVALIDATED_DATA = UpdateNativeUserRequestDto.Password(
            password = "strongpassword123!@",
            newPassword = "1234",
        )
        private val WRONG_PASSWORD_DATA = UpdateNativeUserRequestDto.Password(
            password = "1234",
            newPassword = "newexamplepw1234!@",
        )
        private val DUPLICATED_DATA = UpdateNativeUserRequestDto.Password(
            password = "strongpassword123!@",
            newPassword = "strongpassword123!@"
        )
    }
}