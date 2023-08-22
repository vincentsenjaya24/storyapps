package com.example.storyapp

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.*
import com.example.storyapp.adapter.ListStoryLocationAdapter
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.helper.LocationConverter
import com.example.storyapp.viewmodel.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val mapsViewModel: MapsViewModel by viewModels {
        RepoViewModelFactory(this)
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var storiesWithLocation = listOf<ListStory>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val pref = MyPreference.getInstance(dataStore)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMaps.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvMaps.addItemDecoration(itemDecoration)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        dataStoreViewModel.getToken().observe(this) {
            mapsViewModel.getStories(it)
        }

        mapsViewModel.stories.observe(this) {
            setDataStories(it)
        }

        mapsViewModel.message.observe(this) {
            showToast(it)
        }

        mapsViewModel.isLoading.observe(this) {
            showLoading(it)
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setMapStyle()
        setFabAction()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(msg: String) {
        if (msg != "Stories fetched successfully") {
            binding.tvErrorText.visibility = View.VISIBLE
            Toast.makeText(
                this,
                "${getString(R.string.error_load)} $msg",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Log.d("TOKEN",msg)
            binding.tvErrorText.visibility = View.GONE
        }
    }

    private fun setMarker(stories: List<ListStory>) {
        if (stories.isNotEmpty()) {
            for (story in stories) {
                val posisition = LocationConverter.toLatlng(story.lat, story.lon)
                val address = LocationConverter.getStringAddress(posisition, this)
                if (posisition != null) {//pengecekan data lokasi
                    storiesWithLocation = storiesWithLocation + story
                    mMap.addMarker(
                        MarkerOptions().position(posisition).title(story.name).snippet(address)
                    )

                }
            }
        }
        if (storiesWithLocation.isNotEmpty()) {
            //stories with location berisi story yang sudah pasti terdapat data lat dan lon
            val position =
                LocationConverter.toLatlng(storiesWithLocation[0].lat, storiesWithLocation[0].lon)!!
            mMap.animateCamera(//menganimasikan kamera ke story pertama yang memiliki lokasi
                CameraUpdateFactory.newLatLngZoom(
                    position, INITIAL_ZOOM
                )
            )
        }
    }

    private fun setFabAction() {
        binding.fabNormal.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            setSelectedFab(binding.fabNormal)
        }
        binding.fabSatellite.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            setSelectedFab(binding.fabSatellite)
        }
        binding.fabTerrain.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            setSelectedFab(binding.fabTerrain)

        }
        binding.fabHybrid.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            setSelectedFab(binding.fabHybrid)
        }
    }

    private fun setFabColorSelected(fab: FloatingActionButton, isSelected: Boolean) {
        var buttonDrawable: Drawable = fab.background
        buttonDrawable = DrawableCompat.wrap(buttonDrawable)
        if (isSelected) {
            DrawableCompat.setTint(buttonDrawable, resources.getColor(R.color.red, theme))
        } else {
            DrawableCompat.setTint(buttonDrawable, resources.getColor(R.color.tosca, theme))
        }
        fab.background = buttonDrawable

    }


    private fun setDataStories(stories: List<ListStory>) {
        val listUserAdapter = ListStoryLocationAdapter(stories)
        binding.rvMaps.adapter = listUserAdapter
        setMarker(stories)

        listUserAdapter.setOnItemClickCallback(object : ListStoryLocationAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStory) {
                val posisition = LocationConverter.toLatlng(data.lat, data.lon)
                if (posisition != null) {
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            posisition, DEFAULT_ZOOM
                        )
                    )
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        getString(R.string.no_location_data),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })


    }


    @SuppressLint("RestrictedApi")
    private fun setActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun setSelectedFab(fab: FloatingActionButton) {
        when (fab) {
            binding.fabNormal -> {
                setFabColorSelected(binding.fabNormal, true)
                setFabColorSelected(binding.fabSatellite, false)
                setFabColorSelected(binding.fabTerrain, false)
                setFabColorSelected(binding.fabHybrid, false)
            }
            binding.fabSatellite -> {
                setFabColorSelected(binding.fabSatellite, true)
                setFabColorSelected(binding.fabNormal, false)
                setFabColorSelected(binding.fabTerrain, false)
                setFabColorSelected(binding.fabHybrid, false)
            }
            binding.fabTerrain -> {
                setFabColorSelected(binding.fabTerrain, true)
                setFabColorSelected(binding.fabNormal, false)
                setFabColorSelected(binding.fabSatellite, false)
                setFabColorSelected(binding.fabHybrid, false)
            }
            binding.fabHybrid -> {
                setFabColorSelected(binding.fabHybrid, true)
                setFabColorSelected(binding.fabNormal, false)
                setFabColorSelected(binding.fabSatellite, false)
                setFabColorSelected(binding.fabTerrain, false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val TAG = "MAP"
        const val DEFAULT_ZOOM = 15f
        const val INITIAL_ZOOM = 6f

    }
}