package com.quick.common.config.logging

import com.github.vertical_blank.sqlformatter.SqlFormatter
import com.github.vertical_blank.sqlformatter.languages.Dialect
import com.p6spy.engine.spy.appender.MessageFormattingStrategy

class P6SpySqlFormat : MessageFormattingStrategy {
    override fun formatMessage(
        connectionId: Int,
        now: String?,
        elapsed: Long,
        category: String?,
        prepared: String?,
        sql: String?,
        url: String?
    ): String {
        if (sql.isNullOrBlank()) {
            return ""
        }

        val formattedSql = SqlFormatter.of(Dialect.MySql).format(sql)

        return """
            |--------------------------------------------------
            |Execution Time : $elapsed ms
            |--------------------------------------------------
            |$formattedSql
            |--------------------------------------------------
        """.trimMargin()
    }
}