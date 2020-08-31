package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.ArticleContent
import ru.skillbranch.skillarticles.data.remote.res.ArticleContentRes

fun ArticleContentRes.toArticleContent(): ArticleContent {
    return ArticleContent(
        articleId, content, source, shareLink, updatedAt
    )
}