package com.kakaobank.blogsearch.service

import com.kakaobank.blogsearch.data.dto.BlogListDto
import com.kakaobank.blogsearch.data.entity.KeywordEntity
import com.kakaobank.blogsearch.data.entity.KeywordRepo
import com.kakaobank.blogsearch.data.enums.KakaoSort
import com.kakaobank.blogsearch.data.enums.NaverSort
import com.kakaobank.blogsearch.data.enums.Sort
import com.kakaobank.blogsearch.data.model.BlogModel
import com.kakaobank.blogsearch.util.ApiUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Service
class BlogService {

    @Autowired
    private lateinit var apiUtil: ApiUtil

    @Autowired
    private lateinit var keywordRepo: KeywordRepo

    fun getSearchKakao(keyword: String, sort: Sort?, page: Int?, size: Int?) : BlogListDto? {
        // Param 설정
        var paramUrl = "query=$keyword"
        sort?.run {paramUrl += "&sort=${KakaoSort.findBySort(sort).name}"}
        page?.run {paramUrl += "&page=$page"}
        size?.run {paramUrl += "&size=$size"}

        // API 호출
        val response = apiUtil.callKakaoAPI(HttpMethod.GET, "/v2/search/blog?${paramUrl}", null)
        if(response == null || !response.contains("documents") || !response.contains("meta")) {
            throw Exception("API has been failed.")
        }

        // Data 정제
        val total = (response["meta"] as HashMap<String, *>)["total_count"]?.toString()?.toInt()
        val datetimeForm = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val blogs = (response["documents"] as List<HashMap<String, String>>).map { blog ->
            BlogModel(
                title = blog["title"],
                url = blog["url"],
                contents = blog["contents"],
                postDate = LocalDateTime.parse(blog["datetime"], datetimeForm)
            )
        }.toList()

        return BlogListDto(blogs = blogs, total = total)
    }

    fun getSearchNaver(keyword: String, sort: Sort?, page: Int?, size: Int?) : BlogListDto? {
        // Param 설정
        var paramUrl = "query=$keyword"
        sort?.run {paramUrl += "&sort=${NaverSort.findBySort(sort).name}"}
        page?.run {paramUrl += "&start=$page"}
        size?.run {paramUrl += "&display=$size"}

        // API 호출
        val response = apiUtil.callNaverAPI(HttpMethod.GET, "/v1/search/blog.json?${paramUrl}", null)
        if(response == null || !response.contains("items") || !response.contains("total")) {
            throw Exception("API has been failed.")
        }

        // Data 정제
        val total = response["total"]?.toString()?.toInt()
        val datetimeForm = DateTimeFormatter.BASIC_ISO_DATE
        val blogs = (response["items"] as List<HashMap<String, String>>).map { blog ->
            BlogModel(
                title = blog["title"],
                url = blog["link"],
                contents = blog["description"],
                postDate = LocalDate.parse(blog["postdate"], datetimeForm).atStartOfDay()
            )
        }.toList()

        return BlogListDto(blogs = blogs, total = total)
    }

    fun getTopKeyword(): Map<*, *>? {
        return when(val result = keywordRepo.findTop10ByOrderBySearchCntDesc()) {
            null -> null
            else -> result.associateBy({it.keyword}, {it.searchCnt})
        }
    }

    fun setKeyword(keyword: String) {
        when(val keywordEnt = keywordRepo.findByKeyword(keyword)) {
            null -> {
                keywordRepo.save(KeywordEntity(keyword))
            }
            else -> {
                keywordEnt.searched()
                keywordRepo.save(keywordEnt)
            }
        }
    }

}