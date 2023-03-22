package com.kakaobank.blogsearch.config.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException


@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BSException::class)
    protected fun handleBaseException(e: BSException): ResponseEntity<Any> {
        val errorCode: BSResponseCode = e.bSResponseCode
        val errorMap = HashMap<String, String>()
        errorMap["errorCode"] = errorCode.name
        errorMap["errorMessage"] = "${errorCode.message} ${e.errorMessage} ${e.errorDetail?:""}".trimEnd()
        return ResponseEntity(errorMap, errorCode.status)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun missingServletRequestParameterException(e: MissingServletRequestParameterException): ResponseEntity<Any> {
        val errorCode = BSResponseCode.INVALID_PARAM
        val errorMap = HashMap<String, String>()
        errorMap["errorCode"] = errorCode.name
        errorMap["errorMessage"] = "${errorCode.message} ${e.parameterName}"
        return ResponseEntity(errorMap, errorCode.status)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun methodArgumentNotValidExceptionHandler(e: MethodArgumentTypeMismatchException): ResponseEntity<Any> {
        val errorCode = BSResponseCode.INVALID_PARAM_VALUE
        val errorMap = HashMap<String, String>()
        errorMap["errorCode"] = errorCode.name
        errorMap["errorMessage"] = "${errorCode.message} ${e.name}"
        return ResponseEntity(errorMap, errorCode.status)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun noHandlerFoundException(e: NoHandlerFoundException): ResponseEntity<Any> {
        val errorCode = BSResponseCode.NOT_FOUND
        val errorMap = HashMap<String, String>()
        errorMap["errorCode"] = errorCode.name
        errorMap["errorMessage"] = "${errorCode.message} '${e.httpMethod} ${e.requestURL}'"
        return ResponseEntity(errorMap, errorCode.status)
    }

    @ExceptionHandler(RuntimeException::class)
    fun runtimeExceptionHandler(e: RuntimeException): ResponseEntity<Any> {
        e.printStackTrace()
        val errorCode = BSResponseCode.INTERNAL_SERVER_ERROR
        val errorMap = HashMap<String, String>()
        errorMap["errorCode"] = errorCode.name
        errorMap["errorMessage"] = errorCode.message
        return ResponseEntity(errorMap, errorCode.status)
    }
}