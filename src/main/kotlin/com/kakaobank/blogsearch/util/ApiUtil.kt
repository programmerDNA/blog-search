package com.kakaobank.blogsearch.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


@Component
class ApiUtil {

    @Value("\${kakao.api.url}")
    private val kakaoApiUrl: String = ""
    @Value("\${kakao.api.key}")
    private val kakaoApiKey: String = ""
    @Value("\${naver.api.url}")
    private val naverApiUrl: String = ""
    @Value("\${naver.api.client.id}")
    private val naverApiClientId: String = ""
    @Value("\${naver.api.client.key}")
    private val naverApiClientKey: String = ""

    // 외부 API 호출
    fun callAPI(method: HttpMethod, url: String, data: HashMap<*, *>?, headers: HttpHeaders? = HttpHeaders()) : HashMap<*, *>? {
        return try {
            // Header set
            headers?.contentType = MediaType.APPLICATION_JSON
            // Message
            val requestMessage: HttpEntity<*> = HttpEntity(data, headers)
            // Request => HashMap Format
            when(method) {
                HttpMethod.GET -> RestTemplate().exchange(url, HttpMethod.GET, requestMessage, HashMap::class.java).body
                else -> throw Exception("This httpMethod is not supported yet.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Kakao API 호출
    fun callKakaoAPI(method: HttpMethod, url: String, data: HashMap<*, *>?): HashMap<*, *>? {
        // Kakao API > Header
        val headers = HttpHeaders()
        headers["Authorization"] = "KakaoAK $kakaoApiKey"
        // callAPI
        return callAPI(method, "$kakaoApiUrl$url", data, headers)
    }

    // Naver API 호출
    fun callNaverAPI(method: HttpMethod, url: String, data: HashMap<*, *>?): HashMap<*, *>? {
        // Naver API > Header
        val headers = HttpHeaders()
        headers["X-Naver-Client-Id"] = naverApiClientId
        headers["X-Naver-Client-Secret"] = naverApiClientKey
        // callAPI
        return callAPI(method, "$naverApiUrl$url", data, headers)
    }

}