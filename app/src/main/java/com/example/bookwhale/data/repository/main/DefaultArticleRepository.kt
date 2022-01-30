package com.example.bookwhale.data.repository.main

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.bookwhale.data.db.dao.ArticleDao
import com.example.bookwhale.data.entity.favorite.AddFavoriteEntity
import com.example.bookwhale.data.entity.favorite.FavoriteEntity
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.ErrorResponse
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.favorite.AddFavoriteDTO
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.util.ArticlePagingSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DefaultArticleRepository(
    private val serverApiService: ServerApiService,
    private val articleDao: ArticleDao,
    private val ioDispatcher: CoroutineDispatcher
): ArticleRepository {
    override suspend fun getAllArticles(
        search: String?,
        page: Int,
        size: Int,
    ): NetworkResult<List<ArticleEntity>> = withContext(ioDispatcher) {
        val response = serverApiService.getAllArticles(search, page, size)

        if(response.isSuccessful) {
            NetworkResult.success(
                response.body()!!.map {
                    ArticleEntity(
                        articleId = it.articleId,
                        articleImage = it.articleImage,
                        articleTitle = it.articleTitle,
                        articlePrice = it.articlePrice,
                        bookStatus = it.bookStatus,
                        sellingLocation = it.sellingLocation,
                        chatCount = it.chatCount,
                        favoriteCount = it.favoriteCount,
                        beforeTime = it.beforeTime
                    )
                }
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun getLocalArticles(): NetworkResult<List<ArticleEntity>> = withContext(ioDispatcher) {
        val response = articleDao.getArticles()
        NetworkResult.success( response )
    }

    override suspend fun insertLocalArticles(articles: ArticleEntity) = withContext(ioDispatcher) {
        articleDao.insertArticles(articles)
    }

    override suspend fun getFavoriteArticles(): NetworkResult<List<FavoriteEntity>> = withContext(ioDispatcher) {

        val response = serverApiService.getFavorites()

        if(response.isSuccessful) {
            NetworkResult.success(
                response.body()!!.let {
                    it.map { data ->
                        FavoriteEntity(
                            favoriteId = data.favoriteId,
                            articleEntity = ArticleEntity(
                                articleId = data.articlesResponse.articleId,
                                articleImage = data.articlesResponse.articleImage,
                                articleTitle = data.articlesResponse.articleTitle,
                                articlePrice = data.articlesResponse.articlePrice,
                                bookStatus = data.articlesResponse.bookStatus,
                                sellingLocation = data.articlesResponse.sellingLocation,
                                chatCount = data.articlesResponse.chatCount,
                                favoriteCount = data.articlesResponse.favoriteCount,
                                beforeTime = data.articlesResponse.beforeTime
                            )
                        )
                    }
                }
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }

    }

    override suspend fun addFavoriteArticle(addFavoriteDTO: AddFavoriteDTO): NetworkResult<Boolean> = withContext(ioDispatcher) {

        val response = serverApiService.addFavorites(addFavoriteDTO)

        if(response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun deleteFavoriteArticle(favoriteId: Int): NetworkResult<Boolean> = withContext(ioDispatcher) {
        val response = serverApiService.deleteFavorites(favoriteId)

        if(response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override fun getAllArticles2(
        search: String?
    ): Flow<PagingData<ArticleModel>> {
        val flow = Pager(
            PagingConfig(pageSize = 10)
        ) {
            ArticlePagingSource(serverApiService, search)
        }.flow

        return flow
    }


}
