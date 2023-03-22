package com.kakaobank.blogsearch.controller

import com.kakaobank.blogsearch.config.exception.BSException
import com.kakaobank.blogsearch.config.exception.BSResponseCode
import com.kakaobank.blogsearch.data.enums.Sort
import com.kakaobank.blogsearch.service.BlogService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/blog", produces = ["application/json"])
class BlogController {

    @Autowired
    private lateinit var blogService: BlogService

    @GetMapping("/search")
    fun getBlogSearch(
        @RequestParam("keyword") keyword: String,
        @RequestParam("sort") sort: Sort? = Sort.ACCR,
        @RequestParam("page") page: Int? = 1,
        @RequestParam("size") size: Int? = 10
    ): ResponseEntity<Any> {
        // 키워드 > 기록/수정
        blogService.setKeyword(keyword)

        // Validation
        page?.run {
            if(page < 1 || page > 1000) throw BSException(BSResponseCode.INVALID_PARAM_VALUE, "page")
        }
        size?.run {
            if(size < 1 || size > 100) throw BSException(BSResponseCode.INVALID_PARAM_VALUE, "size")
        }

        // Kakao API
        try {
            return ResponseEntity.ok(blogService.getSearchKakao(keyword, sort, page, size))
        } catch (e: Exception) { }

        // Naver API
        try {
            return ResponseEntity.ok(blogService.getSearchNaver(keyword, sort, page, size))
        } catch (e: Exception) { }

        throw BSException(BSResponseCode.BAD_REQUEST)
    }

    @GetMapping("/keyword")
    fun getTopKeyword(): ResponseEntity<Any> = ResponseEntity.ok(mapOf("keywords" to blogService.getTopKeyword()))

}