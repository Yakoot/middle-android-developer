package ru.skillbranch.skillarticles.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.skillbranch.skillarticles.BuildConfig
import ru.skillbranch.skillarticles.data.local.AppDb
import ru.skillbranch.skillarticles.data.local.dao.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Provides
    fun provideAppDb(@ApplicationContext context: Context): AppDb = Room.databaseBuilder(
        context,
        AppDb::class.java,
        AppDb.DATABASE_NAME
    )
        .run { if (BuildConfig.DEBUG) fallbackToDestructiveMigration() else this }
        .build()

    @Provides
    @Singleton
    fun provideArticlesDao(db: AppDb): ArticlesDao = db.articlesDao()
    @Provides
    @Singleton
    fun provideArticleCountsDao(db: AppDb): ArticleCountsDao = db.articleCountsDao()
    @Provides
    @Singleton
    fun provideCategoriesDao(db: AppDb): CategoriesDao = db.categoriesDao()
    @Provides
    @Singleton
    fun provideArticlePersonalInfosDao(db: AppDb): ArticlePersonalInfosDao = db.articlePersonalInfosDao()
    @Provides
    @Singleton
    fun provideTagsDao(db: AppDb): TagsDao = db.tagsDao()
    @Provides
    @Singleton
    fun provideArticleContentsDao(db: AppDb): ArticleContentsDao  = db.articleContentsDao()
}