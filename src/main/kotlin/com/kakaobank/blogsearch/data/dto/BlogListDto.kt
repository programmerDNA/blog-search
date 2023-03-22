package com.kakaobank.blogsearch.data.dto

import com.kakaobank.blogsearch.data.model.BlogModel

class BlogListDto {
    constructor()
    constructor(blogs: List<BlogModel>?, total: Int?) {
        this.blogs = blogs
        this.total = total
    }
    var blogs: List<BlogModel>? = null
    var total: Int? = null
}