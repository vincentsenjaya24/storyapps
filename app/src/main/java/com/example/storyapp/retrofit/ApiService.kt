package com.example.storyapp.retrofit

import com.example.storyapp.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("register")
    fun createUser(@Body requestRegister: RequestRegister): Call<ResponseMsg>

    @POST("login")
    fun fetchUser(@Body requestLogin: RequestLogin): Call<ResponseLogin>


    @Multipart
    @POST("stories")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Header("Authorization") token: String
    ): Call<ResponseMsg>

    @GET("stories")
    suspend fun getPagingStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int,
        @Header("Authorization") token: String
    ): StoryResponseItem

    @GET("stories")
    fun getPagingStories(
        @Query("location") location: Int,
        @Header("Authorization") token: String
    ): Call<ResponseStory>

}