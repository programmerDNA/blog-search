package com.kakaobank.blogsearch.config.exception

import org.springframework.http.HttpStatus

data class BSException(
    var bSResponseCode: BSResponseCode = BSResponseCode.BAD_REQUEST,
    var errorMessage: String = "",
    var errorDetail: Any? = null,
    var httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
) : RuntimeException() {
    constructor(bsResponseCode: BSResponseCode) : this() {
        this.bSResponseCode = bsResponseCode
    }
    constructor(bsResponseCode: BSResponseCode, errorData: Any?) : this() {
        this.bSResponseCode = bsResponseCode
        this.errorDetail = errorData
    }
}