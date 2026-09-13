package com.quick.common.config.security

import com.quick.common.api.exception.CustomException
import com.quick.common.api.type.ResponseCode
import com.quick.common.config.security.vo.TokenResponseClaim
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value($$"${jwt.access.secret}") accessSecretKey: String,
    @Value($$"${jwt.access.expiration}") accessTokenExpireTime: Duration,
    @Value($$"${jwt.refresh.secret}") refreshSecretKey: String,
    @Value($$"${jwt.refresh.expiration}") refreshTokenExpireTime: Duration
) {
    private val accessSecretKey: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretKey))
    private val accessTokenExpireTime: Long = accessTokenExpireTime.toMillis()
    private val refreshSecretKey: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretKey))
    private val refreshTokenExpireTime: Long = refreshTokenExpireTime.toMillis()

    fun generateAccessToken(userId: String, role: String): TokenResponseClaim {
        val now = Date()
        val expiry = Date(now.time + accessTokenExpireTime)

        val token = Jwts.builder()
            .subject(userId)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(accessSecretKey, Jwts.SIG.HS256)
            .compact()

        return TokenResponseClaim(
            token,
            expiresAtMs = expiry.time
        )
    }


    fun generateRefreshToken(userId: String, isAuto: Boolean): TokenResponseClaim {
        val now = Date()
        val expiry = when (isAuto) {
            true -> Date(now.time + Duration.ofDays(365).toMillis())
            false -> Date(now.time + refreshTokenExpireTime)
        }

        val token = Jwts.builder()
            .subject(userId)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(refreshSecretKey, Jwts.SIG.HS256)
            .compact()

        return TokenResponseClaim(
            token,
            expiresAtMs = expiry.time
        )
    }

    fun getClaims(token:String, isRefresh:Boolean): Claims {
        val key = if (isRefresh) refreshSecretKey else accessSecretKey

        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
        } catch (e: ExpiredJwtException) {
            e.stackTrace
            throw CustomException(ResponseCode.EXPIRED_TOKEN)
        } catch (e: Exception) {
            e.stackTrace
            throw CustomException(ResponseCode.INVALID_TOKEN)
        }
    }
}