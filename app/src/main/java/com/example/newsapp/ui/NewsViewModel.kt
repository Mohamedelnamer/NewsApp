package com.example.newsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.models.NewsResponse
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import com.example.newsapp.data.models.Article
import com.example.newsapp.data.repository.ApiRepository
import com.example.newsapp.data.repository.DatabaseRepository

class NewsViewModel(val databaseRepository: DatabaseRepository,val apiRepository: ApiRepository ) : ViewModel() {

    val breakingNewsLiveData:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage=1

    val searchNewsLiveData:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage=1

    init {
        getBreakingNews("eg")
    }


    fun getBreakingNews(countryCode:String)=viewModelScope.launch {
        breakingNewsLiveData.postValue(Resource.Loading())
        val response=apiRepository.getBreakingNews(countryCode,breakingNewsPage)
        breakingNewsLiveData.postValue(handleBreakingNewsResponse(response))
    }

    fun searchNews(searchQuery: String)=viewModelScope.launch {
        searchNewsLiveData.postValue(Resource.Loading())
        val response=apiRepository.searchNews(searchQuery,searchNewsPage)
        searchNewsLiveData.postValue(handleSearchNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse > {
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse > {
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article : Article)=viewModelScope.launch{
        databaseRepository.insert(article)
    }
    fun getSavedNews()=databaseRepository.getSavedNews()

    fun deleteArticle(article:Article)=viewModelScope.launch{
        databaseRepository.deleteArticle(article)
    }
    
}