package com.quick.module.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "RefreshRequest")
data class RefreshRequestDto (
    @get:Schema(description = "장기 갱신", example = "false")
    val isAuto: Boolean = false
)