package com.quick.user

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.module.user.UserService
import com.quick.module.user.dto.request.NativeLoginRequestDto
import com.quick.module.user.dto.response.LoginResponseDto
import com.quick.module.user.dto.vo.UserVo
import com.quick.module.user.repository.UserJooqRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestComponent
import java.util.*

@TestComponent
class AuthTestUtil {
    @Autowired
    private lateinit var userJooqRepository: UserJooqRepository

    @Autowired
    private lateinit var userService: UserService

    @Value($$"${security.api-key}")
    private lateinit var apiKey: String

    @Value($$"${jwt.access.secret}")
    private lateinit var accessSecretKey: String

    @Value($$"${jwt.refresh.secret}")
    private lateinit var refreshSecretKey: String

    fun getTestToken(reqDto: NativeLoginRequestDto): LoginResponseDto =
        userService.nativeLogin(reqDto)

    fun getTestApiKey(): String = apiKey

    fun generateExpiredAccessToken(reqDto: NativeLoginRequestDto): String {
        val data = makeExpiredLoginData(reqDto)

        return Jwts.builder()
            .subject(data.first.userId.toString())
            .claim("role", data.first.role.toString())
            .issuedAt(data.second)
            .expiration(data.third)
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretKey)), Jwts.SIG.HS256)
            .compact()
    }

    fun generateExpiredRefreshToken(reqDto: NativeLoginRequestDto): String {
        val data = makeExpiredLoginData(reqDto)

        return Jwts.builder()
            .subject(data.first.userId.toString())
            .issuedAt(data.second)
            .expiration(data.third)
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretKey)), Jwts.SIG.HS256)
            .compact()
    }

    private fun makeExpiredLoginData(reqDto: NativeLoginRequestDto): Triple<UserVo, Date, Date> {
        val loginData = userJooqRepository.findLoginInfoByEmail(reqDto.email)
            ?: throw CustomException(ResponseCode.LOGIN_ERROR)
        val now = Date()
        val expiry = Date(now.time - 1)
        return Triple(loginData, now, expiry)
    }
}