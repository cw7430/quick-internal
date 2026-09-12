package com.quick.common.api.exception

import com.quick.common.api.type.ResponseCode

class CustomException : RuntimeException {
    val responseCode: ResponseCode
    val fieldName: String?
    val customMessage: String?

    constructor(responseCode: ResponseCode) : super(responseCode.message) {
        this.responseCode = responseCode
        this.fieldName = null
        this.customMessage = null
    }

    constructor(responseCode: ResponseCode, customMessage: String) : super(customMessage) {
        this.responseCode = responseCode
        this.fieldName = null
        this.customMessage = customMessage
    }

    constructor(responseCode: ResponseCode, fieldName: String, customMessage: String) : super(customMessage) {
        this.responseCode = responseCode
        this.fieldName = fieldName
        this.customMessage = customMessage
    }
}