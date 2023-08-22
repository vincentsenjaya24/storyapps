package com.example.storyapp.viewmodel

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.ListStoryPaging
import com.example.storyapp.repository.StoryRepository

class HomeViewModel(private val provideRepository: StoryRepository) : ViewModel() {
    @ExperimentalPagingApi
    @JvmName("setToken1")
    fun getStories(token: String): LiveData<PagingData<ListStoryPaging>> {
        return provideRepository.getPagingStories(token).cachedIn(viewModelScope)
    }

}

