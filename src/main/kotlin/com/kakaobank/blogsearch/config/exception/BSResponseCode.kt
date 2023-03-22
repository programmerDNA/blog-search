package com.kakaobank.blogsearch.config.exception

import org.springframework.http.HttpStatus

enum class BSResponseCode(
    val status: HttpStatus,
    val message: String
) {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request."),
    INVALID_PARAM(HttpStatus.BAD_REQUEST, "Parameter required:"),
    INVALID_PARAM_VALUE(HttpStatus.BAD_REQUEST, "Parameter value should be valid:"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Api is not matched:"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error has been occurred. Please report to administrator.")
}
