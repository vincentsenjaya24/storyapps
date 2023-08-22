package com.example.storyapp

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class RequestRegister(
    var name: String,
    var email: String,
    var password: String
)

data class RequestLogin(
    var email: String,
    var password: String
)

data class ResponseMsg(
    var error: Boolean,
    var message: String
)

data class ResponseLogin(
    var error: Boolean,
    var message: String,
    var loginResult: LoginResult
)

data class LoginResult(
    var userId: String,
    var name: String,
    var token: String
)

data class ResponseStory(
    var error: String,
    var message: String,
    var listStory: List<ListStory>
)

@Parcelize
data class ListStory(
    var id: String,
    var name: String? = null,
    var description: String? = null,
    var photoUrl: String? = null,
    var createdAt: String? = null,
    var lat: Double? = null,
    var lon: Double? = null
) : Parcelable

@Parcelize
@Entity(tableName = "stories")
data class ListStoryPaging(
    @PrimaryKey
    var id: String,
    var name: String? = null,
    var description: String? = null,
    var photoUrl: String? = null,
    var createdAt: String? = null,
    var lat: Double? = null,
    var lon: Double? = null
) : Parcelable

data class StoryResponseItem(

    @field:SerializedName("error")
    var error: Boolean,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("listStory")
    var listStory: List<ListStoryPaging>? = null
)

