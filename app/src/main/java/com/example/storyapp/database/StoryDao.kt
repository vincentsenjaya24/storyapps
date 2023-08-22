package com.example.storyapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.ListStoryPaging

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(stories: List<ListStoryPaging>)

    @Query("SELECT * FROM stories")
    fun getAllStories(): PagingSource<Int, ListStoryPaging>

    @Query("SELECT * FROM stories")
    fun getAllListStories(): List<ListStoryPaging> //get all stories but in list

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}