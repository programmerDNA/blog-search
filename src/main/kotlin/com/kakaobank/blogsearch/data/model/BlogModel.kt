package com.kakaobank.blogsearch.data.model

import java.time.LocalDateTime


class BlogModel {
    constructor(title: String?, url: String?, contents: String?, postDate: LocalDateTime?) {
        this.title = title
        this.url = url
        this.contents = contents
        this.postDate = postDate
    }
    var title: String? = null
    var url: String? = null
    var contents: String? = null
    var postDate: LocalDateTime? = null
}