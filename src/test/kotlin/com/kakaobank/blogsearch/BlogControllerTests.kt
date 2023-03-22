package com.kakaobank.blogsearch

import com.kakaobank.blogsearch.config.exception.BSResponseCode
import com.kakaobank.blogsearch.data.dto.BlogListDto
import com.kakaobank.blogsearch.data.entity.KeywordRepo
import com.kakaobank.blogsearch.service.BlogService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.Rollback
import javax.transaction.Transactional


@Transactional
@Rollback
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BlogControllerTests {

    @LocalServerPort
    private val port = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var blogService: BlogService

    @Autowired
    private lateinit var keywordRepo: KeywordRepo

    @AfterEach
    fun clean() {
        keywordRepo.deleteAll()
    }

    @Test
    @DisplayName("Search > Success")
    @Throws(Exception::class)
    fun getSearch() {
        // given
        val url = "http://localhost:$port/v1/blog/search?keyword=집짓기&sort=ACCR&size=1&page=1"
        val blogList = restTemplate.getForObject(url, BlogListDto::class.java)

        // then
        assertAll(
            { assertThat(blogList).isNotNull },
            { assertThat(blogList.blogs).isNotNull },
            { assertThat(blogList.total).isGreaterThan(0) },
        )
    }

    @Test
    @DisplayName("Search > Fail > INVALID_PARAM(keyword)")
    @Throws(Exception::class)
    fun getSearchFail_keyword() {
        // given
        val url = "http://localhost:$port/v1/blog/search"
        val response = restTemplate.exchange(url, HttpMethod.GET, null, HashMap::class.java)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.get("errorCode")).isEqualTo(BSResponseCode.INVALID_PARAM.name)
    }

    @Test
    @DisplayName("Search > fail : INVALID_PARAM_VALUE(sort)")
    fun searchFail_sort() {
        // given
        val url = "http://localhost:$port/v1/blog/search?keyword=집짓기&sort=ABCD"
        val response = restTemplate.exchange(url, HttpMethod.GET, null, HashMap::class.java)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.get("errorCode")).isEqualTo(BSResponseCode.INVALID_PARAM_VALUE.name)
    }

    @Test
    @DisplayName("Search > fail : INVALID_PARAM_VALUE(page)")
    fun searchFail_page() {
        // given
        val urlMin = "http://localhost:$port/v1/blog/search?keyword=집짓기&page=0"
        val responseMin = restTemplate.exchange(urlMin, HttpMethod.GET, null, HashMap::class.java)
        val urlMax = "http://localhost:$port/v1/blog/search?keyword=집짓기&page=1001"
        val responseMax = restTemplate.exchange(urlMax, HttpMethod.GET, null, HashMap::class.java)

        // then
        assertThat(responseMin.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(responseMax.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(responseMin.body?.get("errorCode")).isEqualTo(BSResponseCode.INVALID_PARAM_VALUE.name)
        assertThat(responseMax.body?.get("errorCode")).isEqualTo(BSResponseCode.INVALID_PARAM_VALUE.name)
    }

    @Test
    @DisplayName("Search > fail : INVALID_PARAM_VALUE(size)")
    fun searchFail_size() {
        // given
        val urlMin = "http://localhost:$port/v1/blog/search?keyword=집짓기&size=0"
        val responseMin = restTemplate.exchange(urlMin, HttpMethod.GET, null, HashMap::class.java)
        val urlMax = "http://localhost:$port/v1/blog/search?keyword=집짓기&size=101"
        val responseMax = restTemplate.exchange(urlMax, HttpMethod.GET, null, HashMap::class.java)

        // then
        assertThat(responseMin.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(responseMax.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(responseMin.body?.get("errorCode")).isEqualTo(BSResponseCode.INVALID_PARAM_VALUE.name)
        assertThat(responseMax.body?.get("errorCode")).isEqualTo(BSResponseCode.INVALID_PARAM_VALUE.name)
    }

    @Test
    @DisplayName("Keyword > Success")
    @Throws(Exception::class)
    fun getTopKeyword() {
        // given
        val url = "http://localhost:$port/v1/blog/keyword"
        val keywords = restTemplate.getForObject(url, HashMap::class.java)

        // then
        assertThat(keywords).isNotNull
    }

    @Test
    @DisplayName("Keyword > Create")
    @Throws(Exception::class)
    fun createKeyword() {
        // given
        val targetKeyword = "Daum"
        val url = "http://localhost:$port/v1/blog/keyword"
        val prevKeywords = restTemplate.getForObject(url, HashMap::class.java)

        // when
        blogService.setKeyword(targetKeyword)

        // then
        val tobeKeyword = keywordRepo.findByKeyword(targetKeyword)
        assertAll(
            { assertThat(tobeKeyword).isNotNull },
            { assertFalse(prevKeywords!!.contains(targetKeyword)) },
            { assertEquals(1, tobeKeyword!!.searchCnt) }
        )
    }

}