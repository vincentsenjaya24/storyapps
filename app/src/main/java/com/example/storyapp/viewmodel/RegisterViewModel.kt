package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.RequestRegister
import com.example.storyapp.repository.StoryRepository

class RegisterViewModel(private val provideRepository: StoryRepository) : ViewModel() {
    val message: LiveData<String> = provideRepository.message
    val isLoading: LiveData<Boolean> = provideRepository.isLoading

    fun getResponseRegister(requestRegister: RequestRegister) {
        provideRepository.getResponseRegister(requestRegister)
    }
}