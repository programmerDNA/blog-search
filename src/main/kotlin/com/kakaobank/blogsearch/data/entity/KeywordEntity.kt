package com.kakaobank.blogsearch.data.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import javax.persistence.*


@Entity
@Table(name = "bs_keyword")
@DynamicUpdate
class KeywordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="keyId", nullable = false)
    var keyId: Int = 0

    @Column(name="keyword", nullable = false)
    var keyword: String? = null

    @Column(name="regDtm")
    @field:CreationTimestamp
    var regDtm: Timestamp? = null

    @Column(name="modDtm")
    @field:UpdateTimestamp
    var modDtm: Timestamp? = null

    @Column(name="searchCnt", nullable = false)
    var searchCnt: Int = 1

    /* 생성자 */
    constructor(keyword: String?) {
        this.keyword = keyword
    }

    /* 함수 */
    fun searched(): Int = this.searchCnt++ // 조회 횟수 증가

}

@Repository
interface KeywordRepo: JpaRepository<KeywordEntity, Int> {
    fun findTop10ByOrderBySearchCntDesc(): List<KeywordEntity>?
    fun findByKeyword(keyword: String): KeywordEntity?
}