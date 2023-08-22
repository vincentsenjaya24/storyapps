package com.example.storyapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.storyapp.databinding.ActivitySetLocationBinding
import com.example.storyapp.helper.LocationConverter
import com.example.storyapp.utils.wrapEspressoIdlingResource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task


class SetLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivitySetLocationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setButtonCurrentLocationEnable(false)
        setButtonPickPlaceEnable(false)
        setActions()


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun setActions(){
        binding.btCurrentLocation.setOnClickListener {
            showAlertDialog(currentLagLng)
        }

        binding.btPlacePicker.setOnClickListener {
            showAlertDialog(pickedPlace)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        getMyLocation()
        getDeviceLocation()
        mMap.setOnMapClickListener {
            wrapEspressoIdlingResource{
                pickedPlace = it
                val markerOptions = MarkerOptions()
                markerOptions.position(it)

                markerOptions.title(LocationConverter.getStringAddress(it, this))
                mMap.clear()
                val location = CameraUpdateFactory.newLatLngZoom(
                    it, 15f
                )
                mMap.animateCamera(location)
                mMap.addMarker(markerOptions)
                setButtonPickPlaceEnable(true)
            }
        }

    }

    private fun showAlertDialog(latlng: LatLng?) {
        val address = LocationConverter.getStringAddress(latlng, this)
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle(getString(R.string.use_this_location))
            .setMessage(address)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                sendResultLocation(latlng)
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                alert.cancel()
            }
            .show()
    }

    private fun sendResultLocation(latlng: LatLng?) {
        val intent = Intent()
        if (latlng != null) {
            intent.putExtra(EXTRA_LAT, latlng.latitude)
            intent.putExtra(EXTRA_LNG, latlng.longitude)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun defaultLocation() = LatLng(-34.0, 151.0)


    private fun isPremissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getDeviceLocation()
            }
        }

    private fun getMyLocation() {
        wrapEspressoIdlingResource{
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun setButtonCurrentLocationEnable(isEnable: Boolean) {
        binding.btCurrentLocation.isEnabled = isEnable
    }

    private fun setButtonPickPlaceEnable(isEnable: Boolean) {
        binding.btPlacePicker.isEnabled = isEnable
    }

    private fun getDeviceLocation() {
        wrapEspressoIdlingResource{
            try {
                if (isPremissionGranted()) {
                    val locationResult: Task<Location> =
                        LocationServices.getFusedLocationProviderClient(this).lastLocation
                    locationResult.addOnSuccessListener {
                        val itt = null //<-- untuk testing current location null
                        if (it != null) { // <-- ubah itt
                            currentLagLng = LatLng(
                                it.latitude,
                                it.longitude
                            )
                            setButtonCurrentLocationEnable(true)
                            mMap.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        it.latitude,
                                        it.longitude
                                    )
                                ).title(getString(R.string.my_location))
                            )
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        it.latitude,
                                        it.longitude
                                    ), DEFAULT_ZOOM
                                )
                            )
                        } else {

                            setButtonCurrentLocationEnable(false)
                            Toast.makeText(
                                this,
                                getString(R.string.no_current_location),
                                Toast.LENGTH_SHORT
                            ).show()
                            mMap.moveCamera(
                                CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation(), DEFAULT_ZOOM)
                            )
                            mMap.isMyLocationEnabled = false
                        }
                    }
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } catch (e: SecurityException) {
                Log.e(getString(R.string.error_message), e.message, e)
            }
        }

    }

    companion object {
        var IRcounter= 1
        var currentLagLng: LatLng? = null
        var pickedPlace: LatLng? = null
        const val DEFAULT_ZOOM = 15.0f
        const val EXTRA_LAT = "LAT"
        const val EXTRA_LNG = "LNG"
    }
}