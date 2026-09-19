package com.quick.chat

import com.quick.BaseIntegrationTest
import com.quick.module.chat.repository.ChatJooqRepository
import org.springframework.beans.factory.annotation.Autowired

class ChatControllerTest : BaseIntegrationTest() {
    @Autowired
    protected lateinit var chatJooqRepository: ChatJooqRepository

    companion object {
        protected const val CHAT_URL = "$BASE_URL/chat"
    }
}