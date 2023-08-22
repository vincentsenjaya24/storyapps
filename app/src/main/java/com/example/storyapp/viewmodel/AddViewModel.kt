package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    val message: LiveData<String> = storyRepository.message
    val isLoading: LiveData<Boolean> = storyRepository.isLoading

    fun upload(photo: MultipartBody.Part, description: RequestBody, token: String, lat: Double?, lng: Double?) {
        storyRepository.upload(photo, description, token, lat, lng)
    }
}