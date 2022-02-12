package com.example.bookwhale.screen.article

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.screen.main.home.HomeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PostArticleViewModel(
    private val detailRepository: DetailRepository
): BaseViewModel() {

}