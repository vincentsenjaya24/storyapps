package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.RequestLogin
import com.example.storyapp.ResponseLogin
import com.example.storyapp.repository.StoryRepository


class LoginViewModel(private val provideRepository: StoryRepository) : ViewModel() {

    val message: LiveData<String> = provideRepository.message
    val isLoading: LiveData<Boolean> = provideRepository.isLoading
    var userlogin: LiveData<ResponseLogin> = provideRepository.userlogin

    fun getResponseLogin(requestLogin: RequestLogin) {
        provideRepository.getResponseLogin(requestLogin)
    }

}