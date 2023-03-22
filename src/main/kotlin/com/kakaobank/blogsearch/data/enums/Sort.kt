package com.kakaobank.blogsearch.data.enums

enum class Sort {
    ACCR,
    RECENT
}

enum class KakaoSort(val sort: Sort) {
    accuracy(Sort.ACCR),
    recency(Sort.RECENT);
    companion object {
        fun findBySort(sort: Sort) = KakaoSort.values().first { it.sort == sort }
    }
}

enum class NaverSort(val sort: Sort) {
    sim(Sort.ACCR),
    date(Sort.RECENT);
    companion object {
        fun findBySort(sort: Sort) = NaverSort.values().first { it.sort == sort }
    }
}