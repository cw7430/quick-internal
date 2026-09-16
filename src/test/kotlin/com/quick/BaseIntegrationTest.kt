package com.quick

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.quick.module.user.dto.request.NativeLoginRequestDto
import com.quick.user.AuthTestUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
abstract class BaseIntegrationTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var authTestUtil: AuthTestUtil
    protected val objectMapper: ObjectMapper = ObjectMapper()
        .findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    private fun request(method: HttpMethod, url: String): RequestBuilder =
        RequestBuilder(method, url)

    protected fun get(url: String) =
        request(HttpMethod.GET, url)

    protected fun post(url: String) =
        request(HttpMethod.POST, url)

    protected fun put(url: String) =
        request(HttpMethod.PUT, url)

    protected fun patch(url: String) =
        request(HttpMethod.PATCH, url)

    protected fun delete(url: String) =
        request(HttpMethod.DELETE, url)

    inner class RequestBuilder {
        private val requestBuilder: MockHttpServletRequestBuilder

        constructor(method: HttpMethod, url: String) {
            this.requestBuilder = MockMvcRequestBuilders.request(method, url)
                .contentType(MediaType.APPLICATION_JSON)
        }

        fun key(): RequestBuilder {
            this.requestBuilder.header("X-API-Key", authTestUtil.getTestApiKey())
            return this
        }

        fun auth(token: String): RequestBuilder {
            this.requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            return this
        }

        fun body(body: Any): RequestBuilder {
            this.requestBuilder.content(objectMapper.writeValueAsString(body))
            return this
        }

        fun send(): ResultActions =
            mockMvc.perform(this.requestBuilder).andDo(print())
    }

    companion object {
        protected val BASE_URL = "/api/v1"
        protected val MASTER_LOGIN_DATA = NativeLoginRequestDto(
            "admin",
            "0000",
            false
        )
        protected val INVALID_TOKEN = "123dj3w989kp2ekohoiysofhawioerq87retreheiogujigbydfggauid"
    }
}