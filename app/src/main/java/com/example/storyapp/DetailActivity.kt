package com.example.storyapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.helper.DateConverter
import com.example.storyapp.helper.LocationConverter

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<ListStoryPaging>(EXTRA_STORY)
        story?.let { setStory(it) }
        setActionBar(story?.name.toString())
    }

    private fun setStory(story: ListStoryPaging) {
        with(binding) {
            tvName.text = story.name
            tvDate.text = getString(
                R.string.date,
                DateConverter.mouth(story.createdAt),
                DateConverter.day(story.createdAt),
                DateConverter.year(story.createdAt)
            )
            etDes.text = story.createdAt

            tvLocation.text = LocationConverter.getStringAddress(
                LocationConverter.toLatlng(story.lat, story.lon),
                this@DetailActivity
            )

            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imgPhoto)
        }
    }

    private fun setActionBar(story: String) {
        supportActionBar?.apply {
            title = getString(R.string.detail_title, story)
            setDefaultDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}
