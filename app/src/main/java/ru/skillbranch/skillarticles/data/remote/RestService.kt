package ru.skillbranch.skillarticles.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.skillarticles.data.local.entities.ArticleContent
import ru.skillbranch.skillarticles.data.models.CommentItemData
import ru.skillbranch.skillarticles.data.remote.res.ArticleContentRes
import ru.skillbranch.skillarticles.data.remote.res.ArticleRes

interface RestService {
    @GET("articles")
    suspend fun articles(
        @Query("last") last: String? = null,
        @Query("limit") limit: Int = 10
    ): List<ArticleRes>

    @GET("articles/{article}/content")
    suspend fun loadArticleContent(@Path("article") articleId: String): ArticleContentRes {
        TODO("Not yet implemented")
    }

    @GET("articles/{article}/messages")
    fun loadComments(
        @Path("article") articleId: String,
        @Query("last") last: String? = null,
        @Query("limit") limit: Int = 5
    ): Call<List<CommentItemData>> {
        TODO("Not yet implemented")
    }
}