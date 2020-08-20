package ru.skillbranch.skillarticles.data.repositories

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.data.local.DbManager
import ru.skillbranch.skillarticles.data.local.DbManager.db
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.local.dao.*
import ru.skillbranch.skillarticles.data.local.entities.ArticleItem
import ru.skillbranch.skillarticles.data.local.entities.CategoryData
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.data.remote.res.ArticleRes
import java.lang.Thread.sleep

interface IArticlesRepository {
    fun loadArticlesFromNetwork(start: Int = 0, size: Int): List<ArticleRes>
    fun insertArticlesToDb(articles: List<ArticleRes>)
    fun toggleBookmark(articleId: String)
    fun findTags(): LiveData<List<String»
    fun findCategoriesData(): LiveData<List<CategoryData>>
    fun rawQueryArticles(filter: ArticleFilter): DataSource.Factory<Int, ArticleItem>
    fun incrementTagUseCount(tag: String)
}

object ArticlesRepository: IArticlesRepository {

    private val network = NetworkDataHolder
    private var articlesDao = db.articlesDao()
    private var articleCountsDao = db.articleCountsDao()
    private var categoriesDao = db.categoriesDao()
    private var tagsDao = db.tagsDao()
    private var articlePersonalDao = db.articlePersonalInfosDao()

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setupTestDao(
        articlesDao: ArticlesDao,
        articleCountsDao: ArticleCountsDao,
        categoriesDao: CategoriesDao,
        tagsDao: TagsDao,
        articlePersonalDao: ArticlePersonalInfosDao
    ) {
        this.articlesDao = articlesDao
        this.articleCountsDao = articleCountsDao
        this.categoriesDao = categoriesDao
        this.tagsDao = tagsDao
        this.articlePersonalDao = articlePersonalDao
    }

    override fun loadArticlesFromNetwork(start: Int, size: Int): List<ArticleRes> {
        network.findArticlesItem(start, size)
    }

    override fun insertArticlesToDb(articles: List<ArticleRes>) {
        TODO("Not yet implemented")
    }

    override fun toggleBookmark(articleId: String) {
        TODO("Not yet implemented")
    }

    override fun findTags(): LiveData<List<String>> {
        TODO("Not yet implemented")
    }

    override fun findCategoriesData(): LiveData<List<CategoryData>> {
        TODO("Not yet implemented")
    }

    override fun rawQueryArticles(filter: ArticleFilter): DataSource.Factory<Int, ArticleItem> {
        TODO("Not yet implemented")
    }

    override fun incrementTagUseCount(tag: String) {
        TODO("Not yet implemented")
    }

//    fun allArticles(): ArticlesDataFactory =
//        ArticlesDataFactory(ArticleStrategy.AllArticles(::findArticlesByRange))
//
//    fun searchArticles(searchQuery: String) =
//        ArticlesDataFactory(ArticleStrategy.SearchArticle(::searchArticlesByTitle, searchQuery))
//
//    fun allBookmarked(): ArticlesDataFactory =
//        ArticlesDataFactory(ArticleStrategy.BookmarkArticles(::findBookmarkArticles))
//
//    fun searchBookmarkedArticles(searchQuery: String): ArticlesDataFactory =
//        ArticlesDataFactory(ArticleStrategy.SearchBookmark(::searchBookmarkArticles, searchQuery))
//
//    private fun findArticlesByRange(start: Int, size: Int) = local.localArticleItems
//        .drop(start)
//        .take(size)
//
//    private fun findBookmarkArticles(start: Int, size: Int) = local.localArticleItems
//        .asSequence()
//        .filter { it.isBookmark }
//        .drop(start)
//        .take(size)
//        .toList()
//
//    private fun searchBookmarkArticles(start: Int, size: Int,  query: String) = local.localArticleItems
//        .asSequence()
//        .filter { it.isBookmark  && it.title.contains(query, true)  }
//        .drop(start)
//        .take(size)
//        .toList()
//
//    private fun searchArticlesByTitle(start: Int, size: Int, queryTitle: String) =
//        local.localArticleItems
//            .asSequence()
//            .filter { it.title.contains(queryTitle, true) }
//            .drop(start)
//            .take(size)
//            .toList()
//
//    fun loadArticlesFromNetwork(start: Int, size: Int): List<ArticleItemData> =
//        network.networkArticleItems
//            .drop(start)
//            .take(size)
//            .apply { sleep(500) }
//
//    fun insertArticlesToDb(articles: List<ArticleItemData>) {
//        local.localArticleItems.addAll(articles)
//            .apply { sleep(100) }
//    }
//
//    fun updateBookmark(id: String, checked: Boolean) {
//        val index = local.localArticleItems.indexOfFirst { it.id == id }
//        if (index == -1) return
//        local.localArticleItems[index] = local.localArticleItems[index].copy(isBookmark = checked)
//    }


}

class ArticlesDataFactory(val strategy: ArticleStrategy) :
    DataSource.Factory<Int, ArticleItemData>() {
    override fun create(): DataSource<Int, ArticleItemData> = ArticleDataSource(strategy)
}


class ArticleDataSource(private val strategy: ArticleStrategy) :
    PositionalDataSource<ArticleItemData>() {
    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<ArticleItemData>
    ) {
        val result = strategy.getItems(params.requestedStartPosition, params.requestedLoadSize)
        callback.onResult(result, params.requestedStartPosition)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<ArticleItemData>) {
        val result = strategy.getItems(params.startPosition, params.loadSize)
        callback.onResult(result)
    }
}

sealed class ArticleStrategy() {
    abstract fun getItems(start: Int, size: Int): List<ArticleItemData>

    class AllArticles(
        private val itemProvider: (Int, Int) -> List<ArticleItemData>
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItemData> =
            itemProvider(start, size)
    }

    class SearchArticle(
        private val itemProvider: (Int, Int, String) -> List<ArticleItemData>,
        private val query: String
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItemData> =
            itemProvider(start, size, query)
    }

    class SearchBookmark(
        private val itemProvider: (Int, Int, String) -> List<ArticleItemData>,
        private val query: String
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItemData> =
            itemProvider(start, size, query)
    }

    class BookmarkArticles(
        private val itemProvider: (Int, Int) -> List<ArticleItemData>
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItemData> =
            itemProvider(start, size)
    }
}