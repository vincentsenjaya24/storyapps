package com.example.storyapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.example.storyapp.*
import com.example.storyapp.database.StoryRemoteMediator
import com.example.storyapp.database.StoryDatabase
import com.example.storyapp.retrofit.ApiConfig
import com.example.storyapp.retrofit.ApiService
import com.example.storyapp.utils.wrapEspressoIdlingResource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {

    private var _stories = MutableLiveData<List<ListStory>>()
    var stories: LiveData<List<ListStory>> = _stories

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userLogin = MutableLiveData<ResponseLogin>()
    var userlogin: LiveData<ResponseLogin> = _userLogin


    fun getResponseRegister(requestRegister: RequestRegister) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val api = ApiConfig.getApiService().createUser(requestRegister)
            api.enqueue(object : Callback<ResponseMsg> {
                override fun onResponse(
                    call: Call<ResponseMsg>,
                    response: Response<ResponseMsg>
                ) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (response.isSuccessful) {
                        _message.value = responseBody?.message.toString()
                    } else {
                        _message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<ResponseMsg>, t: Throwable) {
                    _isLoading.value = false
                }
            })
        }
    }

    fun getResponseLogin(requestLogin: RequestLogin) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val api = ApiConfig.getApiService().fetchUser(requestLogin)
            api.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(
                    call: Call<ResponseLogin>,
                    response: Response<ResponseLogin>
                ) {
                    _isLoading.value = false
                    val responseBody = response.body()

                    if (response.isSuccessful) {
                        _userLogin.value = responseBody!!
                        _message.value = "Login as ${_userLogin.value!!.loginResult.name}"
                    } else {
                        _message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = t.message.toString()
                }

            })
        }
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<ListStoryPaging>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        )
        return pager.liveData
    }

    fun getStories(token: String) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val api = ApiConfig.getApiService().getPagingStories(0, "Bearer $token")

            api.enqueue(object : Callback<ResponseStory> {
                override fun onResponse(
                    call: Call<ResponseStory>,
                    response: Response<ResponseStory>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _stories.value = responseBody.listStory
                        }
                        _message.value = responseBody?.message.toString()

                    } else {
                        _message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<ResponseStory>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = t.message.toString()
                }

            })
        }
    }

    fun upload(
        photo: MultipartBody.Part,
        des: RequestBody,
        token: String,
        lat: Double?,
        lng: Double?
    ) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val service = ApiConfig.getApiService().uploadImage(
                photo, des, lat?.toFloat(), lng?.toFloat(),
                "Bearer $token"
            )
            service.enqueue(object : Callback<ResponseMsg> {
                override fun onResponse(
                    call: Call<ResponseMsg>,
                    response: Response<ResponseMsg>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            _message.value = responseBody.message
                        }
                    } else {
                        _message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<ResponseMsg>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = "Failed Retrofit Instance"
                }
            })
        }
    }
}
