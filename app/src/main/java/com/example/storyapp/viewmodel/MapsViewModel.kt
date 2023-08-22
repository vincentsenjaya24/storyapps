package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.ListStory
import com.example.storyapp.repository.StoryRepository

class MapsViewModel(private val provideRepository: StoryRepository) : ViewModel() {
    var stories: LiveData<List<ListStory>> = provideRepository.stories
    val message: LiveData<String> = provideRepository.message
    val isLoading: LiveData<Boolean> = provideRepository.isLoading

    fun getStories(token: String) {
        provideRepository.getStories(token)
    }

}