package com.quick.common.config.security.type

enum class Role(
    val code: String,
    val authority: String
) {
    USER("USER", "ROLE_USER"),
    ADMIN("ADMIN", "ROLE_ADMIN"),
    GUEST("GUEST", "ROLE_GUEST");

    companion object {
        fun from(code: String): Role {
            if (code.isBlank()) {
                return GUEST
            }

            return try {
                Role.valueOf(code.uppercase())
            } catch (e: IllegalArgumentException) {
                e.stackTrace
                GUEST
            }
        }
    }
}